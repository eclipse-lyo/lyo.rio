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
package org.eclipse.lyo.oslc.am.resource;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;


/**
 * Servlet implementation class ResourceCompact
 */
public class ResourceCompactService extends RioBaseService {
	private static final long serialVersionUID = 5776266053922927743L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getParameter("uri"); //$NON-NLS-1$
		String previewType = request.getParameter("type"); //$NON-NLS-1$
		Resource amResource;
		try {
			amResource = new Resource(uri);
		} catch (RioServerException e1) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, "Unable to intialize AM Resource");
		}
		try {
			amResource = (Resource) getStore().getResource(amResource);
		} catch (RioServerException e) {
			throw new RioServiceException(e);
		}
		if( amResource == null ) {
			throw new RioServiceException(IConstants.SC_NOT_FOUND, "Resource not found");
		}
		request.setAttribute("resource", amResource); //$NON-NLS-1$
		String preview = null;
		if( "large".equals(previewType) ) { //$NON-NLS-1$
			preview = "/am/resource_largePreview.jsp"; //$NON-NLS-1$
		} else {
			preview = "/am/resource_smallPreview.jsp"; //$NON-NLS-1$
		}
		RequestDispatcher rd = request.getRequestDispatcher(preview);
		rd.forward(request, response);
		
	}

}
