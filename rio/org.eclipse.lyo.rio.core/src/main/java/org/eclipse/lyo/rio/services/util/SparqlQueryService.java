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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.store.RioValue;

public class SparqlQueryService extends RioBaseService {
	private static final long serialVersionUID = -6011601779316735563L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// redirect to JSP
		RequestDispatcher rd = request.getRequestDispatcher("sparql.jsp"); //$NON-NLS-1$
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("queryExp"); //$NON-NLS-1$
		RioStore store = this.getStore();
		try {
			List<Map<String, RioValue>> results = store.query(IConstants.SPARQL, query, 100);
			request.setAttribute("queryExp", query); //$NON-NLS-1$
			request.setAttribute("results", results); //$NON-NLS-1$
		} catch (RioServerException e) {
			throw new RioServiceException(e);
		}
		RequestDispatcher rd = request.getRequestDispatcher("sparql.jsp"); //$NON-NLS-1$
		rd.forward(request, response);
		
		
	}

}
