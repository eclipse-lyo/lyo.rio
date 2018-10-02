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
 *     Michael Fiedler         - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation.resources;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.eclipse.lyo.oslc4j.automation.AutomationResult;
import org.eclipse.lyo.oslc4j.automation.AutomationConstants;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

@OslcService(AutomationConstants.AUTOMATION_DOMAIN)
@Path("autoResults")
public class AutomationResultResource extends BaseAutoResource<AutomationResult> {

	public AutomationResultResource () {
		super(AutomationResult.class);
	}
	
	protected AutomationResultResource(Class<AutomationResult> resourceType) {
		super(resourceType);
	}
	
    @OslcDialogs(
    {
        @OslcDialog
        (
             title = "Automation Result Selection Dialog",
             label = "Automation Result Selection Dialog",
             uri = "autoResults/selector",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = { AutomationConstants.TYPE_AUTOMATION_RESULT},
             usages = {OslcConstants.OSLC_USAGE_DEFAULT}
        ),
        @OslcDialog
        (
             title = "Automation Result List Dialog",
             label = "Automation Result List Dialog",
             uri = "",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {AutomationConstants.TYPE_AUTOMATION_RESULT},
             usages = {AutomationConstants.USAGE_LIST}
        )
    })
    @OslcQueryCapability
    (
        title = "Automation Result Query Capability",
        label = "Automation Result Catalog Query",
        resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + AutomationConstants.PATH_AUTOMATION_RESULT,
        resourceTypes = {AutomationConstants.TYPE_AUTOMATION_RESULT},
        usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @GET
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public AutomationResult[] getResources(@QueryParam("oslc.where") final String where)
    {
    	return super.getResources(where);
    }
    
    @GET
    @Path("{resourceId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public AutomationResult getResource(@Context                      final HttpServletResponse httpServletResponse,
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

    	httpServletRequest.setAttribute("autoResult",super.getResource(httpServletResponse, resourceId));
    	
    	try {	
    		RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/autoresult_html.jsp"); 
	    	rd.forward(httpServletRequest, httpServletResponse);
				
		} catch (Exception e) {
			throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
		}
        
    	
    	throw new WebApplicationException(Status.NOT_FOUND);
    }
    
//    @OslcDialog
//    (
//         title = "Automation Result Creation Dialog",
//         label = "Automation Result Creation Dialog",
//         uri = "creator",
//         hintWidth = "1000px",
//         hintHeight = "600px",
//         resourceTypes = {AutomationConstants.TYPE_AUTOMATION_RESULT},
//         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
//    )
    @OslcCreationFactory
    (
         title = "Automation Result Creation Factory",
         label = "Automation Result Creation",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + AutomationConstants.PATH_AUTOMATION_RESULT},
         resourceTypes = {AutomationConstants.TYPE_AUTOMATION_RESULT},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @POST
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public Response addResource(@Context final HttpServletRequest  httpServletRequest,
    		                    @Context final HttpServletResponse httpServletResponse,
                                         final AutomationResult       resource)
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
    		                                                          final AutomationResult       resource)
    {
    	if (resource.getDesiredState() != null)
    	{
    		resource.setStates(new URI[]{resource.getDesiredState()});
    	}
    	return super.updateResource(httpServletResponse, eTagHeader, resourceId, resource);
    }
    
    @DELETE
    @Path("{resourceId}")
    public Response deleteResource(@PathParam("resourceId") final String identifier)
    {
    	return super.deleteResource(identifier);
    }
    	

}
