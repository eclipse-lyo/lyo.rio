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
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.core.trs.Base;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JContext;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JMarshaller;
import org.eclipse.lyo.rio.trs.cm.PersistenceResourceUtil;
import org.eclipse.lyo.rio.trs.util.TRSObject;
import org.eclipse.lyo.rio.trs.util.TRSUtil;

/**
 * RESTful service endpoints for returning the Base of a Tracked Resource Set in pages. 
 * This is a servlet based generic implementation.
 * 
 * The Base of a Tracked Resource Set is an RDF container where each member references
 * a Resource that was in the Resource Set at the time the Base was computed. HTTP GET on
 * a Base URI returns an RDF container with the following structure: 
 * <pre>
	# Resource: http://cm1.example.com/baseResources
	{@literal @prefix trs: <http://jazz.net/ns/trs#> .}
	{@literal @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .}
	
	{@code
	<http://cm1.example.com/baseResources> 
	trs:cutoffEvent 
	    <urn:urn-3:cm1.example.com:2010-10-27T17:39:31.000Z:101> ;
	  rdfs:member <http://cm1.example.com/bugs/1> ;
	  rdfs:member <http://cm1.example.com/bugs/2> ;
	  rdfs:member <http://cm1.example.com/bugs/3> ;
	  ...
	  rdfs:member <http://cm1.example.com/bugs/199> ;
	  rdfs:member <http://cm1.example.com/bugs/200> .
    }
   </pre>
 *
 */

@SuppressWarnings("serial")
public class BaseGeneric extends HttpServlet {

	public BaseGeneric() {
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/rdf+xml");
		String path = null;
	
		path = request.getPathInfo();
		if ( path != null){
			boolean endsWithSlash = path.endsWith("/");
			// find the page number
			Long page = (long) 0;
			String [] arrofStr;
			arrofStr = path.split("/");
			if (arrofStr.length > 1){
				page = (long) Integer.parseInt(arrofStr[1]);
			}
			
			if (page == 0){
				// redirect it to first page.
				response.sendRedirect(request.getRequestURL().toString() + ((endsWithSlash ? "" : "/") + "1"));			
			}
			else
			{
				// handle the page.
				URI requestBase;
				Object[]  oArray;
				try {
					requestBase = new URI(request.getRequestURL().toString());
					TRSUtil.updateTRSResourceURI(PersistenceResourceUtil.instance, requestBase);
					
					TRSObject trsObject = TRSUtil.getTrsObject(PersistenceResourceUtil.instance, requestBase);
					Base base = trsObject.getBasePage(page);
					if (base == null)
						throw new WebApplicationException(Status.NOT_FOUND);
					
					List<Base> results = new ArrayList<Base>();
					results.add(base);
					// return results;
					OSLC4JContext context = OSLC4JContext.newInstance();
					OSLC4JMarshaller marshaller = context.createMarshaller();
					marshaller.setMediaType(MediaType.APPLICATION_XML_TYPE);
					ServletOutputStream outputStream = response.getOutputStream();
					oArray = results.toArray();
					marshaller.marshal(oArray, outputStream);	
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
	
			}
		}
		else
		{
			// redirects it to first page.
			response.sendRedirect(request.getRequestURL().toString() + "/1");
		}
	}
}
