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
package org.eclipse.lyo.rio.cm.changerequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.cm.common.ICmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.store.RioValue;


public class ChangeRequestListService extends RioBaseService {
	private static final long serialVersionUID = -8436719131002636593L;

	@SuppressWarnings("nls")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RioStore store = this.getStore();
		String query = 
			"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
			"PREFIX dcterms:<http://purl.org/dc/terms/>\n" +
			"SELECT ?uri ?title\n" +
			"WHERE {\n" +
			"  ?uri \n" +
			"    rdf:type <http://open-services.net/ns/cm#ChangeRequest>;\n" +
			"    dcterms:title ?title.\n" +
			"}\n";
		
		try {
			List<Map<String, RioValue>> results = store.query(IConstants.SPARQL, query, ICmConstants.DEFAULT_MAX_RESULTS);
			request.setAttribute("results", results);
			
			RequestDispatcher rd = request.getRequestDispatcher("/cm/changerequest_listing.jsp");
			rd.forward(request, response);
			
		} catch (RioServerException e) {
			e.printStackTrace();
		}
		
	}

}
