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
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.resources;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Workspace;
import org.eclipse.lyo.core.trs.Base;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.rio.trs.util.TRSUtil;

/**
 * RESTful service endpoints for returning the Base of a Tracked Resource Set in pages.
 * 
 * The Base of a Tracked Resource Set is an RDF container where each member references
 * a Resource that was in the Resource Set at the time the Base was computed. HTTP GET on
 * a Base URI returns an RDF container with the following structure: 
 * 
	# Resource: http://cm1.example.com/baseResources
	@prefix trs: <http://jazz.net/ns/trs#> .
	@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
	
	<http://cm1.example.com/baseResources> 
	trs:cutoffEvent 
	    <urn:urn-3:cm1.example.com:2010-10-27T17:39:31.000Z:101> ;
	  rdfs:member <http://cm1.example.com/bugs/1> ;
	  rdfs:member <http://cm1.example.com/bugs/2> ;
	  rdfs:member <http://cm1.example.com/bugs/3> ;
	  ...
	  rdfs:member <http://cm1.example.com/bugs/199> ;
	  rdfs:member <http://cm1.example.com/bugs/200> .

 *
 */
@Path("/trs/"+TRSConstants.TRS_TERM_BASE)
@OslcService(TRSConstants.TRS_BASE)
@Workspace(workspaceTitle = "Tracked Resource Set", collectionTitle = "Base")
public class BaseResource {
	@Context
	protected UriInfo uriInfo;
	
	
	/**
	 * getBase() on the root URI performs a redirect to page 1 in this implementation to represent
	 * paged Base resources of the Tracked Resource Set
	 */
	@GET
	@Produces({ "application/rdf+xml", MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Base getBase() throws URISyntaxException{
	    
        URI requestURI = uriInfo.getRequestUri();
        boolean endsWithSlash = requestURI.getPath().endsWith("/");
        
        throw new WebApplicationException(Response.temporaryRedirect(requestURI.resolve((endsWithSlash ? "" : "base/") + "1")).build());
	}
	
	/**
	 * getBasePage() is called with a page parameter and returns the page of Base resources at the 
	 * specified page.
	 */	
	@GET
	@Path("{page}")
	@Produces({ "application/rdf+xml", MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Base getBasePage(@PathParam("page")Long page) throws URISyntaxException{
	    TRSUtil.updateTRSResourceURI(uriInfo.getBaseUri());
	    
	    // from uri find out which Inner container to access...
	    URI requestURI = uriInfo.getRequestUri();
	    
		if (TRSUtil.getTrsBase(requestURI).isEmpty() || !TRSUtil.getTrsBase(requestURI).containsKey(page))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		return TRSUtil.getTrsBase(requestURI).get(page);
	}
}
