/*******************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation.
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
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.rio.oslc4j.cm.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
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
import javax.ws.rs.core.UriInfo;

import org.eclipse.lyo.rio.oslc4j.cm.ChangeRequest;
import org.eclipse.lyo.rio.oslc4j.cm.Constants;
import org.eclipse.lyo.rio.oslc4j.cm.util.Persistence;
import org.eclipse.lyo.rio.oslc4j.cm.Type;
import org.eclipse.lyo.rio.oslc4j.cm.util.ChangeRequestUtils;
import org.eclipse.lyo.rio.oslc4j.cm.util.ServiceProviderSingleton;
import org.eclipse.lyo.oslc4j.client.OslcRestClient;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.Preview;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;

@OslcService(Constants.CHANGE_MANAGEMENT_DOMAIN)
@Path("changeRequests")
public class ChangeRequestResource
{
	
	private static final String SERVICES_PATH = "/services";
	private static final Set<Class<?>> PROVIDERS = JenaProvidersRegistry.getProviders();
	
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
             uri = "changeRequests/selector",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
             usages = {OslcConstants.OSLC_USAGE_DEFAULT}
        ),
        @OslcDialog
        (
             title = "Change Request List Dialog",
             label = "Change Request List Dialog",
             uri = "",
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
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML,
    		   OslcMediaType.APPLICATION_JSON, OslcMediaType.TEXT_TURTLE})
    public ChangeRequest[] getChangeRequests(@QueryParam("oslc.where") final String where)
    {

        final List<ChangeRequest> results = new ArrayList<ChangeRequest>();

        final ChangeRequest[] changeRequests = Persistence.getChangeRequests();

        for (final ChangeRequest changeRequest : changeRequests)
        {
        	changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

            results.add(changeRequest);

        }

        return results.toArray(new ChangeRequest[results.size()]);
    }

    @GET
    @Path("{changeRequestId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML,
    	       OslcMediaType.APPLICATION_JSON, OslcMediaType.TEXT_TURTLE})
    public ChangeRequest getChangeRequest(@Context                      final HttpServletResponse httpServletResponse,
                                          @PathParam("changeRequestId") final String              changeRequestId)
    {
        final ChangeRequest changeRequest = Persistence.getChangeRequest(changeRequestId);

        if (changeRequest != null)
        {
            changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

            setETagHeader(getETagFromChangeRequest(changeRequest), httpServletResponse);

            return changeRequest;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("{changeRequestId}")
    @Produces({MediaType.TEXT_HTML})
    public Response getChangeRequest(@Context                 final HttpServletRequest  httpServletRequest,
    		                    @Context                 final HttpServletResponse httpServletResponse,
                                @PathParam("changeRequestId") final String              changeRequestId)
    {

    	httpServletRequest.setAttribute("changeRequest", getChangeRequest(httpServletResponse, changeRequestId));
    	
    	try {	
    		RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/changerequest_html.jsp"); 
	    	rd.forward(httpServletRequest, httpServletResponse);
				
		} catch (Exception e) {
			throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
		}
        
    	
    	throw new WebApplicationException(Status.NOT_FOUND);
    }
    
    @GET
    @Path("{changeRequestId}")
    @Produces({OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML, OslcMediaType.APPLICATION_X_OSLC_COMPACT_JSON})
    public Compact getCompact(@Context                      final HttpServletRequest httpServletRequest,
                              @PathParam("changeRequestId") final String             changeRequestId)
           throws URISyntaxException
    {
        final ChangeRequest changeRequest = Persistence.getChangeRequest(changeRequestId);

        if (changeRequest != null)
        {
            final Compact compact = new Compact();

            compact.setAbout(changeRequest.getAbout());
            compact.setShortTitle(changeRequest.getTitle());
            compact.setTitle(changeRequest.getTitle());

            final String[] dctermsTypes = changeRequest.getDctermsTypes();

            if ((dctermsTypes != null) &&
                (dctermsTypes.length == 1))
            {
                final String dctermsType = dctermsTypes[0];

                final String file;

                if (Type.Defect.toString().equals(dctermsType))
                {
                    file = "Defect.png";
                }
                else
                {
                    // TODO - support icons for the other ChangeRequest types
                    file = null;
                }

                if (file != null)
                {
                    final URI iconURI = new URI(httpServletRequest.getScheme(),
                                                null,
                                                httpServletRequest.getServerName(),
                                                httpServletRequest.getServerPort(),
                                                httpServletRequest.getContextPath() + "UI/images/resources/" + file,
                                                null,
                                                null);

                    compact.setIcon(iconURI);
                }
                
                //Create and set attributes for preview resource
                final Preview largePreview = new Preview();
                largePreview.setHintHeight("20em");
                largePreview.setHintWidth("45em");
                largePreview.setDocument(new URI(compact.getAbout().toString() + "/largePreview"));
                compact.setLargePreview(largePreview);
                
            }

            return compact;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

	@GET
	@Path("{changeRequestId}/largePreview")
	@Produces({ MediaType.TEXT_HTML })
	public void getLargePreview(@Context final HttpServletRequest  httpServletRequest,
            					@Context final HttpServletResponse httpServletResponse,
            					@PathParam("changeRequestId") final String changeRequestId) throws ServletException, IOException, URISyntaxException
	{	
		final ChangeRequest changeRequest = Persistence.getChangeRequest(changeRequestId);
		
		if (changeRequest != null)
	    {			 
			httpServletRequest.setAttribute("changeRequest", changeRequest);
			 
			RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/changerequest_preview_large.jsp");
			rd.forward(httpServletRequest, httpServletResponse);
			 
	    }
		
	}
    

    @OslcDialog
    (
    		title = "Change Request Creation Dialog",
    		label = "Change Request Creation Dialog",
    		uri = "changeRequests/creator",
    		hintWidth = "1000px",
    		hintHeight = "600px",
    		resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
    		usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @OslcCreationFactory
    (
         title = "Change Request Creation Factory",
         label = "Change Request Creation",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_CHANGE_REQUEST},
         resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @POST
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML,
    	       OslcMediaType.APPLICATION_JSON, OslcMediaType.TEXT_TURTLE})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML,
    	       OslcMediaType.APPLICATION_JSON, OslcMediaType.TEXT_TURTLE})
    public Response addChangeRequest(@Context final HttpServletRequest  httpServletRequest,
    		                         @Context final HttpServletResponse httpServletResponse,
                                              final ChangeRequest       changeRequest)
           throws URISyntaxException
    {
        final long identifier = Persistence.getNextIdentifier();

        final URI about = new URI(httpServletRequest.getScheme(),
                                  null,
                                  httpServletRequest.getServerName(),
                                  httpServletRequest.getServerPort(),
                                  httpServletRequest.getContextPath() + SERVICES_PATH + "/changeRequests/" + identifier,
                                  null,
                                  null);

        changeRequest.setAbout(about);
        changeRequest.setIdentifier(String.valueOf(identifier));
        changeRequest.setServiceProvider(ServiceProviderSingleton.getServiceProviderURI());

        final Date date = new Date();

        changeRequest.setCreated(date);
        changeRequest.setModified(date);

        Persistence.addChangeRequest(changeRequest);
        setETagHeader(getETagFromChangeRequest(changeRequest), httpServletResponse);

        return Response.created(about).entity(changeRequest).build();
    }

    @PUT
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML,
    	       OslcMediaType.APPLICATION_JSON, OslcMediaType.TEXT_TURTLE})
    @Path("{changeRequestId}")
    public Response updateChangeRequest(@Context                      final HttpServletResponse httpServletResponse,
                                        @HeaderParam("If-Match")      final String              eTagHeader,
                                        @PathParam("changeRequestId") final String              changeRequestId,
    		                                                          final ChangeRequest       changeRequest)
    {
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
	            setETagHeader(getETagFromChangeRequest(changeRequest),httpServletResponse);
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

        return Response.ok().build();
    }

    @DELETE
    @Path("{changeRequestId}")
    public Response deleteChangeRequest(@PathParam("changeRequestId") final String changeRequestId)
    {
        final ChangeRequest changeRequest = Persistence.deleteChangeRequest(changeRequestId);

        if (changeRequest != null)
        {
            return Response.noContent().build();
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    private static void setETagHeader(final String              eTagFromChangeRequest,
                                      final HttpServletResponse httpServletResponse)
    {
    	httpServletResponse.setHeader("ETag", eTagFromChangeRequest);
	}

    private static String getETagFromChangeRequest(final ChangeRequest changeRequest)
	{
		return Long.toString(changeRequest.getModified().getTime());
	}
    
	@Path("selector")
	@Produces({ MediaType.TEXT_HTML })
	public void getSelectionDialog(@Context final HttpServletRequest request,
									@Context final HttpServletResponse response,
									@Context final UriInfo uriInfo,
									@QueryParam("searchFor") final String searchFor)
	{
		// handle any data in the request
		request.setAttribute("selectionUri", uriInfo.getAbsolutePath().toString());
		if (searchFor == null) 
		{
			try {

				RequestDispatcher rd = request.getRequestDispatcher("/web/changeresource_selector.jsp");
				rd.forward(request, response);
			} catch (Exception e) {
				throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
			}
		} else 
		{
			List<ChangeRequest> matchingResources = new ArrayList<ChangeRequest>();

			for (ChangeRequest thisResource : Persistence.getChangeRequests()) 
			{
				if (thisResource.getClass().equals(ChangeRequest.class)) 
				{
					String title = thisResource.getTitle();
					if (title != null) 
					{
						if (title.toUpperCase().contains(searchFor.toUpperCase())) 
						{
							matchingResources.add(thisResource);
						}
					}
				}
			}
			try {
				request.setAttribute("results", matchingResources);
				RequestDispatcher rd = request.getRequestDispatcher("/web/changeresource_filtered_json.jsp");
				rd.forward(request, response);
			} catch (Exception e) {
				throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
			}

		}

	}

	@GET
	@Path("creator")
	@Produces({ MediaType.TEXT_HTML })
	public void getCreationDialog(@Context final HttpServletRequest request,
									@Context final HttpServletResponse response,
									@Context final UriInfo uriInfo,
									@QueryParam("changePlan") final String changePlan) 
	{
		request.setAttribute("creatorUri", uriInfo.getAbsolutePath().toString());

		if (changePlan == null) 
		{
			Map<String, String> changePlanIDs = new HashMap<String, String>();

			for (ChangeRequest thisResource : Persistence.getChangeRequests()) 
			{
				if (thisResource.getClass().equals(ChangeRequest.class)) 
				{
					changePlanIDs.put(thisResource.getIdentifier(),	thisResource.getTitle());
				}
			}
			try {
				request.setAttribute("changePlans", changePlanIDs);
				RequestDispatcher rd = request.getRequestDispatcher("/web/changerequest_creator.jsp");
				rd.forward(request, response);
			} catch (Exception e) {

				throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@POST
	@Path("creator")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createHtmlChangeRequest(@Context final HttpServletRequest httpServletRequest,
											@Context final HttpServletResponse httpServletResponse,
											@FormParam("title") String title,
											@FormParam("description") String description) 
			throws URISyntaxException 
	{
		
		Response response;
				
		if ((title != null) && !title.isEmpty()) 
		{
				ChangeRequest newRequest = new ChangeRequest();
				newRequest.setTitle(title);
				newRequest.setDescription(description);

				final URI creationFactory = ChangeRequestUtils.getCreation(PROVIDERS,
																		   ServiceProviderSingleton.getServiceProviderURI(),
																		   URI.create(Constants.CHANGE_MANAGEMENT_DOMAIN),
																		   URI.create(Constants.TYPE_CHANGE_REQUEST));
				OslcRestClient client = new OslcRestClient(PROVIDERS, creationFactory, "application/rdf+xml");
				
				ChangeRequest createdResource = client.addOslcResource(newRequest);
				try 
				{
					if (createdResource != null) 
					{

						httpServletResponse.setContentType("application/json");
						httpServletResponse.setStatus(Status.CREATED.getStatusCode());
						httpServletResponse.addHeader("Location", createdResource.getAbout().toString());

						response = Response.created(createdResource.getAbout()).entity(createdResource).build();

					} else {
						final org.eclipse.lyo.oslc4j.core.model.Error error = new org.eclipse.lyo.oslc4j.core.model.Error();
						error.setStatusCode(Status.INTERNAL_SERVER_ERROR.toString());
						error.setMessage("Error creating change request");
						response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
					}

				} catch (Exception e) 
				{
					throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
				}
		} else {
			final org.eclipse.lyo.oslc4j.core.model.Error error = new org.eclipse.lyo.oslc4j.core.model.Error();
			error.setStatusCode(Status.INTERNAL_SERVER_ERROR.toString());
			error.setMessage("title missing or invalid");
			response = Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		return response;
	}
}
