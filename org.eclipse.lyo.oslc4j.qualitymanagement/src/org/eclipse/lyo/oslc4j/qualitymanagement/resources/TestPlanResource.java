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
 *     Paul McMahan         - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.qualitymanagement.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.qualitymanagement.Constants;
import org.eclipse.lyo.oslc4j.qualitymanagement.Persistence;
import org.eclipse.lyo.oslc4j.qualitymanagement.QmResource;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestPlan;

@OslcService(Constants.QUALITY_MANAGEMENT_DOMAIN)
@Path("testPlans")
public class TestPlanResource extends BaseQmResource<TestPlan> {
	
	public TestPlanResource () {
		super(TestPlan.class);
	}
	
	protected TestPlanResource(Class<TestPlan> resourceType) {
		super(resourceType);
	}
	
    @OslcDialogs(
    {
        @OslcDialog
        (
             title = "Test Plan Selection Dialog",
             label = "Test Plan Selection Dialog",
             uri = "testPlans/selector",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_TEST_PLAN},
             usages = {OslcConstants.OSLC_USAGE_DEFAULT}
        ),
        @OslcDialog
        (
             title = "Test Plan List Dialog",
             label = "Test Plan List Dialog",
             uri = "",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_TEST_PLAN},
             usages = {Constants.USAGE_LIST}
        )
    })
    @OslcQueryCapability
    (
        title = "Test Plan Query Capability",
        label = "Test Plan Catalog Query",
        resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_TEST_PLAN,
        resourceTypes = {Constants.TYPE_TEST_PLAN},
        usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @GET
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public TestPlan[] getResources(@QueryParam("oslc.where") final String where)
    {
    	return super.getResources(where);
    }
    
    @GET
    @Path("{resourceId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public TestPlan getResource(@Context                      final HttpServletResponse httpServletResponse,
                                          @PathParam("resourceId") final String              resourceId)
    {
    	return super.getResource(httpServletResponse, resourceId);
    }
 
    @GET
    @Path("{resourceId}")
    @Produces({MediaType.TEXT_HTML})
    public Response getResource(@Context                 final HttpServletRequest  httpServletRequest,
    		                    @Context                 final HttpServletResponse httpServletResponse,
                                @PathParam("resourceId") final String              resourceId)
    {

    	httpServletRequest.setAttribute("testPlan",super.getResource(httpServletResponse, resourceId));
    	
    	try {	
    		RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/testplan_html.jsp"); 
	    	rd.forward(httpServletRequest, httpServletResponse);
				
		} catch (Exception e) {
			throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
		}
        
    	throw new WebApplicationException(Status.NOT_FOUND);
    }
    
    

    @OslcDialog
    (
    		title = "Test Case Creation Dialog",
    		label = "Test Case Creation Dialog",
    		uri = "testPlans/creator",
    		hintWidth = "1000px",
            hintHeight = "600px",
            resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
            usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @OslcCreationFactory
    (
         title = "Test Plan Creation Factory",
         label = "Test Plan Creation",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_TEST_PLAN},
         resourceTypes = {Constants.TYPE_TEST_PLAN},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @POST
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public Response addResource(@Context final HttpServletRequest  httpServletRequest,
    		                    @Context final HttpServletResponse httpServletResponse,
                                         final TestPlan       resource)
           throws URISyntaxException
    {
    	return super.addResource(httpServletRequest, httpServletResponse, resource);
    }
    
    @PUT
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Path("{resourceId}")
    public Response updateResource(@Context                      final HttpServletResponse httpServletResponse,
                                        @HeaderParam("If-Match")      final String              eTagHeader,
                                        @PathParam("resourceId") final String              resourceId,
    		                                                          final TestPlan       resource)
    {
    	return super.updateResource(httpServletResponse, eTagHeader, resourceId, resource);
    }
    
    @DELETE
    @Path("{resourceId}")
    public Response deleteResource(@PathParam("resourceId") final String identifier)
    {
    	return super.deleteResource(identifier);
    }
    
    @GET
    @Path("creator")
    @Produces({MediaType.TEXT_HTML, MediaType.WILDCARD})
    
    public void planRequestCreator(@Context                 final HttpServletRequest httpServletRequest,
    		                       @Context                 final HttpServletResponse httpServletResponse,
    		                       @Context                 final UriInfo uriInfo,
    		                       @QueryParam("testPlan")  final String testPlan)
    {
    	httpServletRequest.setAttribute("creatorUri",uriInfo.getAbsolutePath().toString());

    	if (testPlan == null)
    	{
	    	Map<String,String> testPlanIDs = new HashMap<String,String>();
	    		
	    	for (QmResource thisResource:Persistence.getQmResources())
	    	{
	    		if (thisResource.getClass().equals(TestPlan.class))
	    		{
	    			testPlanIDs.put(thisResource.getIdentifier(), thisResource.getTitle());
	    		}
	    	}
	    	try {
				httpServletRequest.setAttribute("testPlans", testPlanIDs);
				RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/qmplan_creator.jsp"); 
				rd.forward(httpServletRequest, httpServletResponse);
			} catch (Exception e) {
				System.out.println("err");
				throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
			}
    	
    	}
    }
    	
	@POST
	@Path("creator")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createHtmlTestRequest(@Context final HttpServletRequest httpServletRequest,
											@Context final HttpServletResponse httpServletResponse,
											@FormParam("title") String title,
											@FormParam("description") String description) 
			throws URISyntaxException 
	{
		
		Response response;
				
		if ((title != null) && !title.isEmpty()) 
		{
			TestPlan newTestPlan = new TestPlan();
			newTestPlan.setTitle(title);
			newTestPlan.setDescription(description);
			
			final URI testPlanURI = createResourceInternal(httpServletRequest, httpServletResponse, newTestPlan);
			
			try 
			{
				httpServletResponse.setContentType("application/json");
				httpServletResponse.setStatus(Status.CREATED.getStatusCode());
				httpServletResponse.addHeader("Location", newTestPlan.getAbout().toString());
				response = Response.created(testPlanURI).entity(newTestPlan).build();

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
