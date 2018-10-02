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
 *    Mukherjee Biswarup - Initial implementation
 *    David Terry - TRS 2.0 compliant implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.eclipse.lyo.core.trs.AbstractChangeLog;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.core.trs.TrackedResourceSet;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JContext;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JMarshaller;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.rio.trs.cm.PersistenceResourceUtil;
import org.eclipse.lyo.rio.trs.util.ResponseUtil;
import org.eclipse.lyo.rio.trs.util.TRSObject;
import org.eclipse.lyo.rio.trs.util.TRSUtil;
/**
 * RESTful service endpoints for returning the Tracked Resource Set  at a given
 * page. This is a servlet based generic implementation.
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
// The servlet class to handle request for /restx/trs/*. Implementation TRS through Servlet.
@SuppressWarnings("serial")
public class TRSGeneric extends HttpServlet {
	private static final Logger logger = Logger.getLogger(TRSGeneric.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    public TRSGeneric() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Entering doGet method in TRSGeneric class");
		
		String responseType = ResponseUtil.parseAcceptType(request);
		response.setContentType(responseType);

		Object[]  oArray = new Object[1];
		TrackedResourceSet set = new TrackedResourceSet();

		URI requestURI;
		try {
			requestURI = new URI(request.getRequestURL().toString());
			set.setAbout(requestURI);
		} catch (URISyntaxException e) {
			logger.warn(bundle.getString("UNABLE_TO_CONSTRUCT_URI"), e);
		}
		
		// Now set the uri for obtaining the Base of the Tracked Resource Set
		URI requestBase;
		try {
			requestBase = new URI(request.getRequestURL().toString());
			TRSObject trsObject = TRSUtil.getTrsObject(PersistenceResourceUtil.instance, requestBase);
			AbstractChangeLog changeLog = trsObject.getCurrentChangeLog();
			URI base = requestBase.resolve("trs/"+TRSConstants.TRS_TERM_BASE+"/");
			set.setBase(base);
			set.setChangeLog(changeLog);
		
			OSLC4JContext context = OSLC4JContext.newInstance();
			OSLC4JMarshaller marshaller = context.createMarshaller();
			
			if (responseType.equals(OslcMediaType.TEXT_TURTLE)) {
				marshaller.setMediaType(OslcMediaType.TEXT_TURTLE_TYPE);
			} else if (responseType.equals(OslcMediaType.APPLICATION_RDF_XML)) {
				marshaller.setMediaType(MediaType.APPLICATION_XML_TYPE);	
			}

			ServletOutputStream outputStream = response.getOutputStream();
			oArray[0] = set;
			marshaller.marshal(oArray, outputStream);
		} catch (URISyntaxException e) {
			logger.warn(bundle.getString("UNABLE_TO_CONSTRUCT_URI"), e);
		}
		
		logger.debug("Exiting doGet method in TRSGeneric class");
	}
}
