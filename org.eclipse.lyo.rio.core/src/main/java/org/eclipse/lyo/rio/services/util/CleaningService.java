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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.store.RioStore;
import org.openrdf.model.Resource;


public class CleaningService extends RioBaseService {
	private static final long serialVersionUID = -3479713115856190402L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().print("<html><head><title>Confirm RIO Clean Repository</title><link rel=\"SHORTCUT ICON\" href=\"oslc.png\"></head>"); //$NON-NLS-1$
		response.getWriter().print("<body><form method=post>Confirm clean of all RDF data: <input type=submit value=\"yes\" name=\"yes\"><input type=submit value=\"no, never mind\"/></form></p>"); //$NON-NLS-1$
		response.getWriter().print("<p>To abort close this window, or use the browser to go back to previous page.</body></html>"); //$NON-NLS-1$
		response.setStatus(IConstants.SC_OK);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String confirm = request.getParameter("yes"); //$NON-NLS-1$
		RioStore store = getStore();
		if( confirm != null  ) { 
			store.clear((Resource)null);
		}
		response.sendRedirect( store.getUriBase() +  '/' + "index.jsp"); //$NON-NLS-1$
	}
}
