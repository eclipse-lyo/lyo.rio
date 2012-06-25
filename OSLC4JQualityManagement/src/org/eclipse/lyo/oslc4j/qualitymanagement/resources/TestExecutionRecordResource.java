/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
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

import java.net.URISyntaxException;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.qualitymanagement.Constants;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestExecutionRecord;

@OslcService(Constants.QUALITY_MANAGEMENT_DOMAIN)
@Path("testExecutionRecords")
public class TestExecutionRecordResource extends BaseQmResource<TestExecutionRecord> {

	public TestExecutionRecordResource () {
		super(TestExecutionRecord.class);
	}
	
	protected TestExecutionRecordResource(Class<TestExecutionRecord> resourceType) {
		super(resourceType);
	}
	
    @OslcDialogs(
    {
        @OslcDialog
        (
             title = "Test Execution Record Selection Dialog",
             label = "Test Execution Record Selection Dialog",
             uri = "",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_TEST_EXECUTION_RECORD},
             usages = {OslcConstants.OSLC_USAGE_DEFAULT}
        ),
        @OslcDialog
        (
             title = "Test Execution Record List Dialog",
             label = "Test Execution Record List Dialog",
             uri = "UI/testExecutionRecords/list.jsp",
             hintWidth = "1000px",
             hintHeight = "600px",
             resourceTypes = {Constants.TYPE_TEST_EXECUTION_RECORD},
             usages = {Constants.USAGE_LIST}
        )
    })
    @OslcQueryCapability
    (
        title = "Test Execution Record Query Capability",
        label = "Test Execution Record Catalog Query",
        resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_TEST_EXECUTION_RECORD,
        resourceTypes = {Constants.TYPE_TEST_EXECUTION_RECORD},
        usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @GET
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public TestExecutionRecord[] getResources(@QueryParam("oslc.where") final String where)
    {
    	return super.getResources(where);
    }
    
    @GET
    @Path("{resourceId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public TestExecutionRecord getResource(@Context                      final HttpServletResponse httpServletResponse,
                                          @PathParam("resourceId") final String              resourceId)
    {
    	return super.getResource(httpServletResponse, resourceId);
    }
    
    @OslcCreationFactory
    (
         title = "Test Execution Record Creation Factory",
         label = "Test Execution Record Creation",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + Constants.PATH_TEST_EXECUTION_RECORD},
         resourceTypes = {Constants.TYPE_TEST_EXECUTION_RECORD},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @POST
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public Response addResource(@Context final HttpServletRequest  httpServletRequest,
    		                    @Context final HttpServletResponse httpServletResponse,
                                         final TestExecutionRecord       resource)
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
    		                                                          final TestExecutionRecord       resource)
    {
    	return super.updateResource(httpServletResponse, eTagHeader, resourceId, resource);
    }
    
    @DELETE
    @Path("{resourceId}")
    public Response deleteResource(@PathParam("resourceId") final String identifier)
    {
    	return super.deleteResource(identifier);
    }
    	

}
