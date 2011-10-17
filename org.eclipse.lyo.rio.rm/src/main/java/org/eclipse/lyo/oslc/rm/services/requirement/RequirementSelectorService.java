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
package org.eclipse.lyo.oslc.rm.services.requirement;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.oslc.rm.common.IRmConstants;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.store.RioValue;
import org.eclipse.lyo.rio.util.StringUtils;


/**
 * Servlet implementation class ResourceFactory
 */
public class RequirementSelectorService extends RioBaseService {
	private static final long serialVersionUID = 6114613293834136389L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String terms = request.getParameter("terms"); //$NON-NLS-1$
		if( terms != null ) {
			sendQueryResponse(terms, response);
		} else {
			// redirect to jsp
			RequestDispatcher rd = request.getRequestDispatcher("/rm/requirement_selector.jsp"); //$NON-NLS-1$
			rd.forward(request, response);
		}
	}
	
	private void sendQueryResponse(String terms, HttpServletResponse response) throws RioServiceException {
		// query for link types with this term in the title or description
		String query = buildQuery(terms);
		RioStore store = getStore();
		StringBuffer jsonResults = new StringBuffer(); 
		jsonResults.append("{\"results\": [ \n" ); //$NON-NLS-1$

		try {
			List<Map<String, RioValue>> results = store.query(IConstants.SPARQL, query, IRmConstants.DEFAULT_MAX_RESULTS);
			boolean addComma = false;
			for (Map<String, RioValue> result : results) {
				String about = result.get("about").stringValue(); //$NON-NLS-1$
				String resUri = result.get("about").stringValue(); //$NON-NLS-1$
				String id = extractId(resUri);

				String title = result.get("title").stringValue(); //$NON-NLS-1$
				title = StringUtils.stringEscape(title) + " (" + id + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				
				if( !addComma ) {
					addComma = true;
				} else {
					jsonResults.append(",\n"); //$NON-NLS-1$
				}
				jsonResults.append( "{\"resource\": \"" + about + "\", \"title\": \"" + title + "\"}" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
	        jsonResults.append( "\n] \n}" ); //$NON-NLS-1$
	        response.setContentType(IConstants.CT_JSON);
	        response.getWriter().write(jsonResults.toString()); 
	        response.setStatus(IConstants.SC_OK);
		} catch (Exception e) {
			throw new RioServiceException(e);
		}

	}
	
	private String extractId(String resourceUri) {
		int pos = resourceUri.lastIndexOf('/');
		return resourceUri.substring(pos+1);
	}	
	
	@SuppressWarnings("nls")
	private String buildQuery(String terms) {
//		PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//		PREFIX dcterms:<http://purl.org/dc/terms/>
//		SELECT ?uri ?graph ?title
//		WHERE {
//		  GRAPH ?graph {
//		    ?uri 
//		      rdf:type <http://open-services.net/ns/am#LinkType>;
//		      dcterms:title ?title .
//		    FILTER( regex(?title,"lat","i") ) .
//		  }
//		}
		
		int id = -1;
		try{
			id = Integer.parseInt(terms);
		} catch(NumberFormatException nfe) {
			id = -1;
		}
		
		terms = StringUtils.stringEscape(terms);
		StringBuilder query = new StringBuilder();
		query.append(RioStore.sparqlDefaultPrefixes());
		query.append("SELECT ?graph ?about ?title\n");
		query.append("WHERE {\n");
		query.append("GRAPH ?graph {\n");
		query.append("  ?about rdf:type <http://open-services.net/ns/rm#Requirement>;\n");
		if( id >0 ) {
			query.append("       dcterms:identifier \"" + id + "\";\n");
			query.append("       dcterms:title ?title.\n");
		} else {
			query.append("       dcterms:title ?title.\n");
			query.append("   FILTER( regex(?title,\"" + terms + "\",\"i\") ). \n" );
		}
		query.append("}");
		query.append("}");
		return query.toString();
	}

}
