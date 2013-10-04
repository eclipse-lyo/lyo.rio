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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.eclipse.lyo.core.trs.AbstractChangeLog;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JContext;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JMarshaller;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.rio.trs.cm.PersistenceResourceUtil;
import org.eclipse.lyo.rio.trs.util.ResponseUtil;
import org.eclipse.lyo.rio.trs.util.TRSObject;
import org.eclipse.lyo.rio.trs.util.TRSUtil;

/**
 * RESTful service endpoints for returning  the Change Log at a given
 * page. This is a servlet based generic implementation.
 *  
 */
@SuppressWarnings("serial")
public class ChangeLogGeneric extends HttpServlet {
	private static final Logger logger = Logger.getLogger(ChangeLogGeneric.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Messages");

	public ChangeLogGeneric() {
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Entering doGet method in ChangeLogGeneric class"); 
		
		String responseType = ResponseUtil.parseAcceptType(request);
		response.setContentType(responseType);
		
		AbstractChangeLog changeLog = null;
		Object[]  oArray = new Object[1];		
		
		String path = null;
		URI requestBase;
		try {
			requestBase = new URI(request.getRequestURL().toString());
			TRSObject trsObject = TRSUtil.getTrsObject(PersistenceResourceUtil.instance, requestBase);
			
			path = request.getPathInfo();
			if ( path != null){
				// find the page number
				Long page = (long) 0;
				String [] arrofStr;
				arrofStr = path.split("/");
				if (arrofStr.length > 1){
					page = (long) Integer.parseInt(arrofStr[1]);
				}			
				
				changeLog = trsObject.getChangeLogPage(page);
				if (changeLog == null) {
					logger.error(bundle.getString("NO_CHANGE_LOG"));
					throw new WebApplicationException(Status.NOT_FOUND);
				}
			}
			else
			{
				changeLog = trsObject.getCurrentChangeLog();
			}
		
			OSLC4JContext context = OSLC4JContext.newInstance();
			OSLC4JMarshaller marshaller = context.createMarshaller();

			if (responseType.equals(OslcMediaType.TEXT_TURTLE)) {
				marshaller.setMediaType(OslcMediaType.TEXT_TURTLE_TYPE);
			} else if (responseType.equals(OslcMediaType.APPLICATION_RDF_XML)) {
				marshaller.setMediaType(MediaType.APPLICATION_XML_TYPE);	
			}

			ServletOutputStream outputStream = response.getOutputStream();
			oArray[0] = changeLog;
			marshaller.marshal(oArray, outputStream);
		} catch (URISyntaxException e) {
			logger.error(bundle.getString("UNABLE_TO_CONSTRUCT_URI"), e);
		}
		
		logger.debug("Exiting doGet method in ChangeLogGeneric class");
	}
}
