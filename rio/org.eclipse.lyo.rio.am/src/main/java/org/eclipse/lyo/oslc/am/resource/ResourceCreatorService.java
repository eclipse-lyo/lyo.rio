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

import org.eclipse.lyo.oslc.am.common.IAmConstants;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.util.StringUtils;


/**
 * Servlet implementation class ResourceFactory
 */
public class ResourceCreatorService extends RioBaseService {
	private static final long serialVersionUID = 7466374797163202313L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("/am/resource_creator.jsp"); //$NON-NLS-1$
		rd.forward(req, resp);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("title"); //$NON-NLS-1$
		String description = request.getParameter("description"); //$NON-NLS-1$
		try{
			RioStore store = getStore();
			String uri = store.nextAvailableUri(IAmConstants.SERVICE_RESOURCE);
			Resource resource = new Resource(uri);
			resource.addRdfType(IAmConstants.OSLC_AM_TYPE_RESOURCE);
			resource.setTitle(title);
			resource.setDescription(description);
			String userUri = getUserUri(request.getRemoteUser());
			store.update(resource, userUri);
			
			// now redirect to URL (if this is not already closed)
			response.setStatus(IConstants.SC_CREATED);
			response.setHeader(IConstants.HDR_LOCATION, uri);
			response.setHeader(IConstants.HDR_ETAG, resource.getETag());
			response.setHeader(IConstants.HDR_LAST_MODIFIED, StringUtils.rfc2822(resource.getModified()));
			
		} catch( Exception e) {
			throw new RioServiceException(e);
		}
	}


}
