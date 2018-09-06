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
import java.util.ResourceBundle;

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

import org.apache.log4j.Logger;
import org.apache.wink.common.annotations.Workspace;
import org.eclipse.lyo.core.trs.AbstractChangeLog;
import org.eclipse.lyo.core.trs.ChangeEvent;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.core.trs.TrackedResourceSet;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.rio.trs.cm.PersistenceResourceUtil;
import org.eclipse.lyo.rio.trs.util.TRSObject;
import org.eclipse.lyo.rio.trs.util.TRSUtil;

/**
 * RESTful service endpoints for returning the Tracked Resource Set and the Change Log at a given
 * page.
 * 
 * An HTTP GET on a Tracked Resource Set URI returns a representation structured as follows
 * (note: for exposition, the example snippets show the RDF information content using Turtle; 
 * the actual representation of these resources "on the wire" may vary): 
 
 <pre>
	# Resource: http://cm1.example.com/trackedResourceSet
	{@literal @prefix trs: <http://jazz.net/ns/trs#> .}
	
	{@code
	<http://cm1.example.com/trackedResourceSet>
	  a trs:TrackedResourceSet ;
	  trs:base <http://cm1.example.com/baseResources> ;
	  trs:changeLog [
	    a trs:ChangeLog ; 
	    trs:changes ( ... ) .
	  ] .
	}
</pre>
 *  
 */
@Path("/trs")
@OslcService(TRSConstants.TRS_TYPE_TRACKED_RESOURCE_SET)
@Workspace(workspaceTitle = "Tracked Resource Set", collectionTitle = "TRS")

public class TRSResource {
	@Context
	protected UriInfo uriInfo;
	@Context private HttpServletResponse httpServletResponse;
	@Context private HttpServletRequest httpServletRequest;
	
	private static final Logger logger = Logger.getLogger(TRSResource.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Messages");
	
	/**
	 * getTrackedResourceSet() returns the Tracked Resource Set with the most recent
	 * page of the Change Log or EmptyChangeLog if no change logs pages exist
	 * @throws IOException 
	 */
	@GET
	@Produces({ OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_RDF_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public TrackedResourceSet getTrackedResourceSet() throws URISyntaxException, IOException{
		logger.debug("Entering getTrackedResourceSet method in TRSResource class");
		
		init();
		
		URI requestURI = uriInfo.getRequestUri();
		TRSObject trsObject = TRSUtil.getTrsObject(PersistenceResourceUtil.instance, requestURI);
		
		AbstractChangeLog changeLog = trsObject.getCurrentChangeLog();
		
		TrackedResourceSet set = new TrackedResourceSet();

		set.setAbout(requestURI);

		// Now set the uri for obtaining the Base of the Tracked Resource Set
		URI requestBase = uriInfo.getBaseUri();
		URI base = requestBase.resolve("trs/"+TRSConstants.TRS_TERM_BASE+"/");
		
		set.setBase(base);
			
		set.setChangeLog(changeLog);
		
		logger.debug("Exiting getChangeLog method in TRSResource class");
		return set;
	}

	/**
	 * getChangeLog() returns the current changelog...
	 * @throws IOException 
	 */
	@GET
	@Path("changelog")
	@Produces({ OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_RDF_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AbstractChangeLog getChangeLog() throws URISyntaxException, IOException{
		logger.debug("Entering getChangeLog method in TRSResource class");
		
		init();		

		URI requestURI = uriInfo.getRequestUri();

		TRSObject trsObject = TRSUtil.getTrsObject(PersistenceResourceUtil.instance, requestURI);
		
		AbstractChangeLog changeLog = trsObject.getCurrentChangeLog();
		
		logger.debug("Exiting getChangeLog method in TRSResource class");		
		return changeLog;
	}
	
	/**
	 * getChangeLogPage() returns the Change Log at the given page number
	 * @throws IOException 
	 * 
	 */	
	@GET
	@Path("changelog/{page}")
	@Produces({ OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_RDF_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AbstractChangeLog getChangeLogPage(@PathParam("page") final Long page) throws URISyntaxException, IOException{
		logger.debug("Entering getChangeLogPage method in TRSResource class. Param1: " + page);
		
		init();
		
		URI requestURI = uriInfo.getRequestUri();
		TRSObject trsObject = TRSUtil.getTrsObject(PersistenceResourceUtil.instance, requestURI);
		
		AbstractChangeLog changeLog = trsObject.getChangeLogPage(page);
		
		if (changeLog == null)
			throw new WebApplicationException(Status.NOT_FOUND);
				
		logger.debug("Exiting getChangeLogPage method in TRSResource class");
		return changeLog;
	}
	
    
	@POST
	@Path("ModifyCutOff") 
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	public void modifyCutOffEventhtml (  @FormParam ("event")  String event )
	{
		logger.debug("Entering modifyCutOffEventhtml in TRSResource class. Param1: " + event);
		
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
			logger.error(MessageFormat.format(bundle.getString("MODIFY_CUTOFF_FAILURE"), event), e);
			throw new WebApplicationException(e);
		}
	
		logger.debug("Exiting modifyCutOffEventhtml method in TRSResource class");
	}
	
	private void init() {
		// Initialize the base before adding / deleting any resource.  
		TRSUtil.initialize(PersistenceResourceUtil.instance, uriInfo.getBaseUri());
	}	
}
