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
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

/**
 * Class consisting of various methods to assist in the marshalling/unmarshalling
 * of TRS toolkit resources for the servlet based implementation.
 */
public class ResponseUtil {
	private static final Logger logger = Logger.getLogger(ResponseUtil.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Messages");
	
	/**
	 * Return a string representing the requested accept type. Right now we 
	 * support text/turle and application/rdf+xml with text/turtle
	 * being the default response. 
	 */
	public static String parseAcceptType(HttpServletRequest request) throws IOException { 
		logger.debug("Entering parseAcceptType method in ResponseUtil class");
		
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
				logger.debug("Exiting parseAcceptType method in ResponseUtil class. Accept type is turtle");
				return OslcMediaType.TEXT_TURTLE;
			} else if (type != null && (type.startsWith(OslcMediaType.APPLICATION_RDF_XML) || type.startsWith(OslcMediaType.APPLICATION_XML))) {
				logger.debug("Exiting parseAcceptType method in ResponseUtil class. Accept type is RDF/XML");
				return OslcMediaType.APPLICATION_RDF_XML;
			}
		}	
		
		logger.error(MessageFormat.format(bundle.getString("UNSUPPORTED_MEDIA_TYPE"), acceptTypes));
		throw new WebApplicationException(Status.UNSUPPORTED_MEDIA_TYPE);
	}
}
