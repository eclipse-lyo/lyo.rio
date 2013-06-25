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
 * * Contributors:
 * 
 *    David Terry - Initial implementation
 *******************************************************************************/
package org.eclipse.lyo.rio.trs.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.core.trs.AbstractChangeLog;
import org.eclipse.lyo.core.trs.ChangeEvent;
import org.eclipse.lyo.core.trs.ChangeLog;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

/**
 * Class consisting of various methods to assist in the marshalling/unmarshalling
 * of TRS toolkit resources for the servlet based implementation.
 */
public class ResponseUtil {
	
	/**
	 * This method examines the incoming response type and alters the relative
	 * about URIs stored on the events of the change log. For turtle output the
	 * relative URIs are left alone or restored if necessary.  For RDF/XML the
	 * requestBase is added to the relative URI to get the appropriate absolute
	 * URI (since RDF/XML does not allow relative about URIs).
	 * 
	 * @param responseType
	 * @param requestBase
	 * @param changeLog
	 * @throws URISyntaxException
	 */
	public static void fixRelativeUris(String responseType, String requestBase, AbstractChangeLog changeLog) throws URISyntaxException {
		// If this is a populated change log it has relative uris we need to fix
		// or the RDF/XML will be illegal
		if (changeLog instanceof ChangeLog) {
			List<ChangeEvent> events = ((ChangeLog) changeLog).getChange();

			for (ChangeEvent event : events) {
				if (!(responseType.equals(OslcMediaType.TEXT_TURTLE))) {
					event.setAbout(new URI(requestBase + "#" + Integer.toString(event.getOrder())));
				} else {
					event.setAbout(new URI("#" + Integer.toString(event.getOrder())));
				}
			}
		}
	}
		
	/**
	 * Return a string representing the requested accept type. Right now we 
	 * support text/turle and application/rdf+xml with text/turtle
	 * being the default response. 
	 */
	public static String parseAcceptType(HttpServletRequest request) throws IOException { 
		
		String acceptTypes = request.getHeader("Accept");
		
		if (acceptTypes == null) {
			// turtle is the default response type if nothing specific is 
			// requested.
			return OslcMediaType.TEXT_TURTLE;
		}
		
		String[] types = acceptTypes.split(",");
		// Accepts should be answered in priority order.  Respond with the first
		// type we encounter that is supported.
		for (String type : types) {
			if (type != null && (type.startsWith(OslcMediaType.TEXT_TURTLE) || type.startsWith("*/*"))) {
				return OslcMediaType.TEXT_TURTLE;
			} else if (type != null && (type.startsWith(OslcMediaType.APPLICATION_RDF_XML) || type.startsWith(OslcMediaType.APPLICATION_XML))) {
				return OslcMediaType.APPLICATION_RDF_XML;
			}
		}	
		
		throw new WebApplicationException(Status.UNSUPPORTED_MEDIA_TYPE);
	}
}
