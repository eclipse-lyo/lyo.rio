/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation.
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
 *    Jim Conallen - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.rio.services.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.store.XmlFormatter;
import org.eclipse.lyo.rio.util.XmlUtils;

public class EditService extends RioBaseService {

	private static final long serialVersionUID = 5819282343367457102L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			RioStore store = RioStore.getStore();
			
			String delete = req.getParameter("delete");
			if( delete == null ) {
					String uriBase = store.getUriBase();
					RioResource resource = null;

					String uri = req.getParameter("uri");
					String page = "";
					String rdfXml = "";
					
					if( uri == null ) {
						rdfXml = emptyResource("");
					} else {
						resource = store.getResource(uri); 
						XmlFormatter formatter = new XmlFormatter();
//						RdfXmlFormatter formatter = new RdfXmlFormatter();
						String rawRdf = formatter.format(resource, null);
						rdfXml = XmlUtils.encode(rawRdf);
					}
					
					page = page(uri, uriBase, rdfXml);
					PrintWriter writer = resp.getWriter();
					writer.write(page);
					resp.setContentType(IConstants.CT_HTML); 
					resp.setStatus(IConstants.SC_OK);
			} else {
				// delete this resource
				String uri = req.getParameter("uri");
				RioResource resource = store.getResource(uri); 
				store.remove(resource);
				resp.sendRedirect(store.getUriBase());
			}
		
		} catch( Exception e ) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
	
	private String page(String uri, String uriBase, String rdfXml){
		String page = 
			"<html><head><title>RIO Resource Editor: " + uri + "</title></head>\n" + 
			"<body><h3>RDF/XML</h3>\n" +
			"<form action=" + uriBase + "/edit method=\"POST\">\n" + 
			"<input type=\"submit\" name=\"Save\" value=\"Save\" />\n" + 
			"<input type=\"hidden\" size=80 name=\"uri\" value=\"" + uri + "\"/></br>\n" + 
			"<pre><textarea name=\"rdfxml\" rows=30 cols=132 id=\"rdfxml\">" + rdfXml + "</textarea></pre>\n" +
			"<input type=\"submit\" name=\"Save\" value=\"Save\" />\n" +
			"</form></body></html>";
		return page;
	}

	private String emptyResource(String uri){
		String emptyResource =  
			"<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
			"<rdf:Description rdf:about=\"" + uri + "\">\n" +
			"</rdf:Description>\n" +
			"</rdf:RDF>";

		return emptyResource;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getParameter("uri"); //$NON-NLS-1$
		
		try{
			
			RioStore store = getStore();
			String rdfxml = request.getParameter("rdfxml"); 
			ByteArrayInputStream content = new ByteArrayInputStream(rdfxml.getBytes());
			RioResource updatedResource = new RioResource(uri);
			List<RioStatement> statements = store.parse(uri, content, IConstants.CT_RDF_XML);
			updatedResource.addStatements(statements);
			store.update(updatedResource, uri);
			response.sendRedirect(store.getUriBase());
			
		} catch( Exception e) {
			throw new RioServiceException(e);
		}
	}
}
