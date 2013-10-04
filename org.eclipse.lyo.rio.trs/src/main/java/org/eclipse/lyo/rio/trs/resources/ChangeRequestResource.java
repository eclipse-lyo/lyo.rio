/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 * 
 *    Ernest Mah - Initial implementation
 *    David Terry - TRS 2.0 compliant implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.apache.wink.common.annotations.Workspace;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.ResponseInfoArray;
import org.eclipse.lyo.rio.trs.cm.ChangeRequest;
import org.eclipse.lyo.rio.trs.cm.Constants;
import org.eclipse.lyo.rio.trs.cm.Persistence;
import org.eclipse.lyo.rio.trs.cm.PersistenceResourceUtil;
import org.eclipse.lyo.rio.trs.util.TRSUtil;

@OslcService(Constants.CHANGE_MANAGEMENT_DOMAIN)
@Path("changeRequests")
@Workspace(workspaceTitle = "Change Management", collectionTitle = "Change Request")
public class ChangeRequestResource
{
	@Context ServletContext servletContext;
	private static String URN_PREFIX = "urn:urn-3:cm1.example.com:";
	
	private static final Logger logger = Logger.getLogger(ChangeRequestResource.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Messages");
	
	// Handle the HTML request from browser.
	@POST
	@Path("creator") 
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	public void createHtmlChangeRequest(  @FormParam ("title")  String title,
			  @FormParam ("description")  String description, 
			  @FormParam ("filedAgainst") String filedAgainst)
	{
		logger.debug("Entering createHtmlChangeRequest method in ChangeRequestResource class. Param1: " + title + " Param2: " + description + " Param3: " + filedAgainst);
		
		try {
			URI baseURI = uriInfo.getBaseUri();
			
			// Initialize the base before adding / deleting any resource.  
			TRSUtil.initialize(PersistenceResourceUtil.instance, baseURI);
			
			ChangeRequest cr = Persistence.createChangeRequest(description,
	                title, filedAgainst);
						
			ChangeRequest changeRequest = Persistence.persistChangeRequest(baseURI, cr);
	
			TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());
	
			// Send back to the form a small JSON response.
			httpServletResponse.setContentType("application/json");
			httpServletResponse.setStatus(Status.CREATED.getStatusCode());
		    httpServletResponse.addHeader("Location",  changeRequest.getAbout().toString());
			
			PrintWriter out = httpServletResponse.getWriter();
			out.print("{\"title\": \"" + title + "\"," +
					"\"ID\" : \"" + changeRequest.getIdentifier() + "\"}");
			out.close();
			
		} catch (Exception e) {
			logger.error(bundle.getObject("FAILED_TO_CREATE_CR"), e);
			throw new WebApplicationException(e);
		}
		logger.debug("Exiting createHtmlChangeRequest method in ChangeRequestResource class");
	}

	@PUT
	@Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	@Path("{changeRequestId}")
	public void updateChangeRequest(@HeaderParam("If-Match")      final String              eTagHeader,
	                                @PathParam("changeRequestId") final String              changeRequestId,
			                                                      final ChangeRequest       changeRequest)
	{
		logger.error("Entering updateChangeRequest method in ChangeRequestResource class. Param1: " + eTagHeader + " Param2: " + changeRequestId);
	    final ChangeRequest originalChangeRequest = Persistence.getChangeRequest(changeRequestId);
	
	    if (originalChangeRequest != null)
	    {
	    	final String originalETag = getETagFromChangeRequest(originalChangeRequest);
	
	    	//no eTag or eTag unchanged
	        if ((eTagHeader == null) ||
	            (originalETag.equals(eTagHeader)))
	        {
	            changeRequest.setModified(new Date());
	            changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());
	            Persistence.updateChangeRequest(changeRequestId, changeRequest);
	
	        	// TRS - Insert the modification event to the change log	            
	            TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_MODIFICATION, changeRequest.getAbout(), getCurrentTimeStampURN());
	
				 String eTag = getETagFromChangeRequest(changeRequest);
	    		httpServletResponse.setContentType("application/json");
	    		httpServletResponse.setStatus(Status.OK.getStatusCode());
	    	    httpServletResponse.addHeader("ETag",  eTag);
	    		
	    		PrintWriter out;
				try {
					out = httpServletResponse.getWriter();
				
		    		out.print("{\"title\": \"" + changeRequest.getTitle() + "\"," +
		    				"\"ID\" : \"" + changeRequest.getIdentifier() + "\"}");
		    		out.close();
					} catch (IOException e) {
					logger.debug("Change request was updated but failed to update UI with status information.", e);
				}	
	        }
	        else
	        {
	        	throw new WebApplicationException(Status.PRECONDITION_FAILED);
	        }
	    }
	    else
	    {
	    	throw new WebApplicationException(Status.NOT_FOUND);
	    }
	    logger.debug("Exiting updateChangeRequest method in ChangeRequestResource class");
	}


	@Context protected UriInfo uriInfo;
	@Context private HttpServletRequest httpServletRequest;
	@Context private HttpServletResponse httpServletResponse;
	
    public ChangeRequestResource()
    {
        super();
    }

    @OslcDialogs(
    {
        @OslcDialog
        (
             title = "Change Request Selection Dialog",
             label = "Change Request Selection Dialog",
             uri = "/changeRequests/creator",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
             usages = {OslcConstants.OSLC_USAGE_DEFAULT}
        ),
        @OslcDialog
        (
             title = "Change Request List Dialog",
             label = "Change Request List Dialog",
             uri = "UI/changeRequests/list.jsp",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
             usages = {Constants.USAGE_LIST}
        )
    })
    @OslcQueryCapability
    (
        title = "Change Request Query Capability",
        label = "Change Request Catalog Query",
        resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_CHANGE_REQUEST,
        resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
        usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @GET
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public ResponseInfoArray<ChangeRequest> getChangeRequests(@QueryParam("oslc.where") final String where, @QueryParam("pageNo")final int pageNo, @Context HttpServletRequest request)
    {
       logger.debug("Entering getChangeRequests method in ChangeRequestResource class. Param1: " + where + " Param2: " + pageNo);
    	
       int totalResults = Persistence.getTotalChangeRequests();
       final ChangeRequest[] changeRequests = Persistence.getChangeRequests(pageNo);
       URI nextPage = null;
       
       if(totalResults > pageNo*Persistence.PAGE_SIZE+Persistence.PAGE_SIZE ){
	       URI requestURI = uriInfo.getRequestUri();
	       nextPage = UriBuilder.fromUri(requestURI).replaceQueryParam("pageNo", pageNo+1).build(new Object[0]);
        }
       
       logger.debug("Exiting getChangeRequest method in ChangeRequestResource class");
        return new ResponseInfoArray<ChangeRequest>(changeRequests,
                                                    null,
                                                    totalResults,
                                                    nextPage);
    }
    
    @GET
  	@Produces({ MediaType.TEXT_HTML })
  	public void getHtmlCollection(@QueryParam("pageNo")final int pageNo) throws ServletException, IOException
  	{
    	logger.debug("Entering getHtmlCollection method in ChangeRequestResource class. Param1: " + pageNo);
      	try
      	{
  	        int totalResults = Persistence.getTotalChangeRequests();
  	        final ChangeRequest[] changeRequests = Persistence.getChangeRequests(pageNo);
  	        URI nextPage = null;
  	        
  	        if(totalResults > pageNo*Persistence.PAGE_SIZE+Persistence.PAGE_SIZE){
  	 	       URI requestURI = uriInfo.getRequestUri();
  	 	       nextPage = UriBuilder.fromUri(requestURI).replaceQueryParam("pageNo", pageNo+1).build(new Object[0]);
  	 	       httpServletRequest.setAttribute("nextPageUri", nextPage.toString());
  	 	       httpServletRequest.setAttribute("currentPageUri", requestURI.toString());
  	         }
  	        
  	        ResponseInfoArray<ChangeRequest> results =  new ResponseInfoArray<ChangeRequest>(changeRequests,
  	                                                     null,
  	                                                     totalResults,
  	                                                     nextPage);
  	        httpServletRequest.setAttribute("results", results);
  	        
  	        
  	        logger.debug("Exiting getHtmlCollection method in ChangeRequestResource class");
  	        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/changerequest_collection_html.jsp");
  	    	rd.forward(httpServletRequest, httpServletResponse);
      	}catch (Exception e) {
      		logger.error(MessageFormat.format(bundle.getString("NO_SUCH_RESOURCE"), pageNo), e);
      		throw new WebApplicationException(Status.NOT_FOUND);	
      	}
  	}
    
    @GET
    @Path("{changeRequestId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public Response getChangeRequest(@PathParam("changeRequestId") final String  changeRequestId)
    {
    	logger.debug("Entering getChangeRequest method in ChangeRequestResource class. Param1: " + changeRequestId);
    	
        final ChangeRequest changeRequest = Persistence.getChangeRequest(changeRequestId);

        if (changeRequest != null)
        {
            changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

            String eTag = getETagFromChangeRequest(changeRequest);
            Response response = Response.ok(changeRequest).header("ETag", eTag).build();

            logger.debug("Exiting getChangeRequeswt method in ChangeRequestResource class.");
            return response;
        }

        logger.error(MessageFormat.format(bundle.getString("NO_SUCH_RESOURCE"), changeRequestId));
        throw new WebApplicationException(Status.NOT_FOUND);
    }
    
    // to allow modifications of a CR Prop through HTML. 
    @GET
    @Path("{changeRequestId}")
    @Produces({ MediaType.TEXT_HTML })
    public void getChangeRequest_html(@PathParam("changeRequestId") final int  changeRequestId)
    {
       	try {	
       			httpServletRequest.setAttribute("modifyUri", uriInfo.getAbsolutePath().toString());
	            httpServletRequest.setAttribute("changeRequestId", changeRequestId ); 
	            RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/ChangeReq_modify.jsp");
	    		rd.forward(httpServletRequest, httpServletResponse);   	        
    		} 
       	catch (Exception e) {
       			logger.error(MessageFormat.format(bundle.getString("MODIFICATION_FAILURE"), changeRequestId), e);
    			throw new WebApplicationException(e);
    		}
    }

    @OslcCreationFactory
    (
         title = "Change Request Creation Factory",
         label = "Change Request Creation",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_CHANGE_REQUEST},
         resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @POST
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public Response addChangeRequest(ChangeRequest changeRequest)
           throws URISyntaxException
    {
    	logger.debug("Entering addChangeRequest method in ChangeRequestResource class");
    	
    	URI basePath = uriInfo.getBaseUri();

        
        changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

        final Date date = new Date();

        changeRequest.setCreated(date);
        changeRequest.setModified(date);

        changeRequest = Persistence.persistChangeRequest(basePath, changeRequest);
        String eTag = getETagFromChangeRequest(changeRequest);
        
        logger.debug("Exiting addChangeRequest method in ChangeRequestResource class");
        return Response.created(changeRequest.getAbout()).entity(changeRequest).header("ETag", eTag).build();
    }

    @DELETE
    @Path("{changeRequestId}")
    public Response deleteChangeRequest(@PathParam("changeRequestId") final String changeRequestId)
    {
    	logger.debug("Entering deleteChangeRequest method in ChangeRequestResource class. Param1: " + changeRequestId);
    	
		URI baseURI = uriInfo.getBaseUri();
		// Initialize the base before adding / deleting any resource.  
		TRSUtil.initialize(PersistenceResourceUtil.instance, baseURI);
		
        final ChangeRequest changeRequest = Persistence.deleteChangeRequest(changeRequestId);

        if (changeRequest != null)
        {
        	// TRS - Insert the deletion event to the change log
        	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_DELETION, changeRequest.getAbout(), getCurrentTimeStampURN());
        	
        	logger.debug("Exiting deleteChangeRequest method in ChangeRequestResource class");
            return Response.ok().build();
            
        }
        
        logger.error(MessageFormat.format(bundle.getString("DELETION_FAILURE"), changeRequestId));
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    private static String getETagFromChangeRequest(final ChangeRequest changeRequest)
	{
		return Long.toString(changeRequest.getModified().getTime());
	}
    
    @GET
    @Path("/populate")
    @Produces(MediaType.TEXT_PLAIN)
    public String populate()
           throws URISyntaxException
    {
    	logger.debug("Entering populate method in ChangeRequestResource class");
    	
    	URI baseURI = uriInfo.getBaseUri();
    	
    	// Initialize the base before adding / deleting any resource.  
    	TRSUtil.initialize(PersistenceResourceUtil.instance, baseURI);
    	
    	ChangeRequest changeRequest = null;
    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Unable to execute Apache Tomcat due to missing Java runtime environment (JRE).",
                                                 "Apache Tomcat requires JRE", "Server"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());

    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Operating system password required to be alphanumeric with at least eight characters.",
                                                 "Password complexity rules", "Server"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());    	

    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Misspelling in error message in XYZZY product.",
                                                 "Error message mispelling", "Client"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());    	
        
    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Unable to execute Apache Tomcat due to missing Java runtime environment (JRE).",
                "Integer et elementum est. Maecenas bibendum fermentum pharetra.", "Server"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());    	

    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sed pharetra lectus..",
		                "Password complexity rules", "Client"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());    	
		
    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Vestibulum volutpat, est vehicula tincidunt tincidunt, tortor ipsum consequat ante, sit amet ultricies elit dui ac massa. Nulla facilisi..",
		                "Fusce dapibus imperdiet porta.", "Server"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());    	
		
    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Nunc aliquet scelerisque leo, eu molestie enim fermentum nec. Nulla lectus dui, dictum non sodales quis, dictum sed ante. Proin quam nulla, euismod ac facilisis eu, rhoncus vel quam. Donec ac ligula ante. Nulla egestas quam sit amet neque placerat quis iaculis risus tincidunt. Mauris a sapien nulla.",
		        "Suspendisse lobortis nisi nisl, et imperdiet nisl.", "Client"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());    	
		
    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Ut felis risus, blandit eu tristique sed, vulputate non ligula. Donec interdum mi sed odio bibendum eget placerat mi tempus. Pellentesque id erat mauris.",
		        "Duis placerat scelerisque purus sed tincidunt.", "Server"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());
		
    	changeRequest = Persistence.persistChangeRequest(baseURI,  Persistence.createChangeRequest("Etiam lacinia cursus risus at iaculis. Nunc metus nisi, lobortis vitae consequat nec, hendrerit nec augue.",
		        "Donec sit amet felis purus.", "Client"));
    	TRSUtil.insertEventTypeToChangeLog(TRSConstants.TRS_TYPE_CREATION, changeRequest.getAbout(), getCurrentTimeStampURN());    	
        
    	logger.debug("Exiting populate method in ChangeRequestResource class");
        return "Success!";
    }
    
    /**
     * OSLC delegated creation dialog for a single change request
     * 
     * Forwards to changerequest_creator.jsp to build the html form
     * 
     *  productId param not req it's specific to bugzilla
     * @throws IOException
     * @throws ServletException
     */
    
    @GET
    @Path("creator") 
    @Consumes({MediaType.WILDCARD})
    public void changeRequestCreator() throws IOException, ServletException
    {
    	try {							
			httpServletRequest.setAttribute("creatorUri", uriInfo.getAbsolutePath().toString());
	        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/ChangeReq_creator.jsp");
    		rd.forward(httpServletRequest, httpServletResponse);
			
		} catch (Exception e) {
			logger.error(bundle.getString("FAILED_TO_CREATE_CR"));
			throw new WebApplicationException(e);
		}    	
    }

	/**
	 * This method generates a simple unique URN for a change event based on
	 * the current system time. The URN allows consumers of the the feed
	 * to know which changes events have already been viewed.
	 * 
	 * @return
	 */
 	private static URI getCurrentTimeStampURN() {
 		URI timestampURI = null;
 		try {
 			timestampURI = new URI(URN_PREFIX + getCurrentTimeStamp());
 		} catch (URISyntaxException e) {
 			logger.debug("Unable to construct URN for the new change event.");
 		}
 		return timestampURI;
 	}

 	/**
 	 * Generates a string in the format of yyyy-MM-ddHH:mm:ss.SS which can be
 	 * used as part of a unique URN for change events.
 	 * 
 	 * @return
 	 */
 	private static String getCurrentTimeStamp() {
 		Date currDate = new Date();
 		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-ddHHmmss.SS");
 		String currDateStr = dateFormatGmt.format(currDate);
 		return currDateStr;
 	}
}
