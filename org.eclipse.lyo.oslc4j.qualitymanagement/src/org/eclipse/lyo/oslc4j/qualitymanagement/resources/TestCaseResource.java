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
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.qualitymanagement.Constants;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestCase;

@OslcService(Constants.QUALITY_MANAGEMENT_DOMAIN)
@Path("testCases")
public class TestCaseResource extends BaseQmResource<TestCase> {

	public TestCaseResource () {
		super(TestCase.class);
	}
	
	protected TestCaseResource(Class<TestCase> resourceType) {
		super(resourceType);
	}
	
    @OslcDialogs(
    {
        @OslcDialog
        (
             title = "Test Case Selection Dialog",
             label = "Test Case Selection Dialog",
             uri = "testCases/selector",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_TEST_CASE},
             usages = {OslcConstants.OSLC_USAGE_DEFAULT}
        ),
        @OslcDialog
        (
             title = "Test Case List Dialog",
             label = "Test Case List Dialog",
             uri = "",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_TEST_CASE},
             usages = {Constants.USAGE_LIST}
        )
    })
    @OslcQueryCapability
    (
        title = "Test Case Query Capability",
        label = "Test Case Catalog Query",
        resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_TEST_CASE,
        resourceTypes = {Constants.TYPE_TEST_CASE},
        usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @GET
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public TestCase[] getResources(@QueryParam("oslc.where") final String where)
    {
    	return super.getResources(where);
    }
    
    @GET
    @Path("{resourceId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public TestCase getResource(@Context                      final HttpServletResponse httpServletResponse,
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

    	httpServletRequest.setAttribute("testCase",super.getResource(httpServletResponse, resourceId));
    	
    	try {	
    		RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/testcase_html.jsp"); 
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
    		uri = "testCases/creator",
    		hintWidth = "1000px",
            hintHeight = "600px",
            resourceTypes = {Constants.TYPE_CHANGE_REQUEST},
            usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @OslcCreationFactory
    (
         title = "Test Case Creation Factory",
         label = "Test Case Creation",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_TEST_CASE},
         resourceTypes = {Constants.TYPE_TEST_CASE},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @POST
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public Response addResource(@Context final HttpServletRequest  httpServletRequest,
    		                    @Context final HttpServletResponse httpServletResponse,
                                         final TestCase       resource)
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
    		                                                          final TestCase       resource)
    {
    	return super.updateResource(httpServletResponse, eTagHeader, resourceId, resource);
    }
    
    @DELETE
    @Path("{resourceId}")
    public Response deleteResource(@PathParam("resourceId") final String identifier)
    {
    	return super.deleteResource(identifier);
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
			TestCase newTestCase = new TestCase();
			newTestCase.setTitle(title);
			newTestCase.setDescription(description);
				
			final URI testCaseURI = createResourceInternal(httpServletRequest, httpServletResponse, newTestCase);
			
			try 
			{
				httpServletResponse.setContentType("application/json");
				httpServletResponse.setStatus(Status.CREATED.getStatusCode());
				httpServletResponse.addHeader("Location", newTestCase.getAbout().toString());
				response = Response.created(testCaseURI).entity(newTestCase).build();

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
