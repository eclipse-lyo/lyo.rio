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
package org.eclipse.lyo.oslc.am.linktype;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioServerException;


/**
 * Servlet implementation class ResourceCompact
 */
public class LinkTypeCompactService extends RioBaseService {
	private static final long serialVersionUID = 5776266053922927743L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getParameter("uri"); //$NON-NLS-1$
		LinkType linkType;
		try {
			linkType = new LinkType(uri,null);
		} catch (RioServerException e1) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, "Unable to intialize AM LinkType");
		}
		try {
			linkType = (LinkType) getStore().getResource(uri);
		} catch (RioServerException e) {
			throw new RioServiceException(e);
		}
		if( linkType == null ) {
			throw new RioServiceException(IConstants.SC_NOT_FOUND, "Link Type not found");
		}
		request.setAttribute("linkType", linkType); //$NON-NLS-1$
		RequestDispatcher rd = request.getRequestDispatcher("/am/previewLinkType.jsp"); //$NON-NLS-1$
		rd.forward(request, response);
		
	}

}
