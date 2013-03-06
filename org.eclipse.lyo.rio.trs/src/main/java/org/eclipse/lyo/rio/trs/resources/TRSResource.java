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

import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Workspace;
import org.eclipse.lyo.core.trs.AbstractChangeLog;
import org.eclipse.lyo.core.trs.ChangeEvent;
import org.eclipse.lyo.core.trs.EmptyChangeLog;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.core.trs.TrackedResourceSet;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.rio.trs.util.TRSUtil;

/**
 * RESTful service endpoints for returning the Tracked Resource Set and the Change Log at a given
 * page.
 * 
 * An HTTP GET on a Tracked Resource Set URI returns a representation structured as follows
 * (note: for exposition, the example snippets show the RDF information content using Turtle; 
 * the actual representation of these resources "on the wire" may vary): 
 
	# Resource: http://cm1.example.com/trackedResourceSet
	@prefix trs: <http://jazz.net/ns/trs#> .
	
	<http://cm1.example.com/trackedResourceSet>
	  a trs:TrackedResourceSet ;
	  trs:base <http://cm1.example.com/baseResources> ;
	  trs:changeLog [
	    a trs:ChangeLog ; 
	    trs:changes ( ... ) .
	  ] .

 *  
 */
@Path("/trs")
@OslcService(TRSConstants.TRS_TYPE_TRACKED_RESOURCE_SET)
@Workspace(workspaceTitle = "Tracked Resource Set", collectionTitle = "TRS")

public class TRSResource {
	@Context
	protected UriInfo uriInfo;
	@Context private HttpServletRequest httpServletRequest;
	@Context private HttpServletResponse httpServletResponse;
	
	/**
	 * getTrackedResourceSet() returns the Tracked Resource Set with the most recent
	 * page of the Change Log or EmptyChangeLog if no change logs pages exist
	 */
	@GET
	@Produces({ "application/rdf+xml", MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public TrackedResourceSet getTrackedResourceSet() throws URISyntaxException{
		// from uri find out which Inner container to access...
		URI requestURI = uriInfo.getRequestUri();
		
		AbstractChangeLog changeLog = TRSUtil.getTrsChangelogMap(requestURI).isEmpty() ? new EmptyChangeLog() : TRSUtil.getCurrentChangelog(requestURI);
			
		TrackedResourceSet set = new TrackedResourceSet();

		set.setAbout(requestURI);

		// Now set the uri for obtaining the Base of the Tracked Resource Set
		URI requestBase = uriInfo.getBaseUri();
		URI base = requestBase.resolve("trs/"+TRSConstants.TRS_TERM_BASE+"/");
		
		set.setBase(base);
		set.setChangeLog(changeLog);
		
		return set;
	}

	/**
	 * getChangeLog() returns the current changelog...
	 */
	@GET
	@Path("changelog")
	@Produces({ "application/rdf+xml", MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AbstractChangeLog getChangeLog() throws URISyntaxException{
		// from uri find out which Inner container to access...
		URI requestURI = uriInfo.getRequestUri();
		
		AbstractChangeLog changeLog = TRSUtil.getCurrentChangelog(requestURI);
		return changeLog;
	}
	
	/**
	 * getChangeLogPage() returns the Change Log at the given page number
	 * 
	 */	
	@GET
	@Path("changelog/{page}")
	@Produces({ "application/rdf+xml", MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AbstractChangeLog getChangeLogPage(@PathParam("page") final Long page) throws URISyntaxException{
		// from uri find out which Inner container to access...
		URI requestURI = uriInfo.getRequestUri();
		
		if (!TRSUtil.getTrsChangelogMap(requestURI).isEmpty() && !TRSUtil.getTrsChangelogMap(requestURI).containsKey(page))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		AbstractChangeLog changeLog = TRSUtil.getTrsChangelogMap(requestURI).isEmpty() ? new EmptyChangeLog() : TRSUtil.getTrsChangelogMap(requestURI).get(page); 
			
		return changeLog;
	}
	
    
	@POST
	@Path("ModifyCutOff") 
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	public void modifyCutOffEventhtml (  @FormParam ("event")  String event )
	{
		
		try {
			ChangeEvent  ce = TRSUtil.getChangeEvent((event));
			if (ce != null) {
				TRSUtil.modifyCutoffEvent(ce);
			}
				
			// Send back to the form a small JSON response.
			httpServletResponse.setContentType("application/json");
			httpServletResponse.setStatus(Status.OK.getStatusCode());
			
			PrintWriter out = httpServletResponse.getWriter();
			out.print("{\"event\": \"" + event + "\"}");
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}
	
	}
}
