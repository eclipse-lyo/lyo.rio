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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.cm.common.ICmConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioStore;

/**
 * Provides delegated UI creator services
 */
public class ChangeRequestCreatorService extends RioBaseService {
	private static final long serialVersionUID = 7466374797163202313L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("/cm/changerequest_creator.jsp"); //$NON-NLS-1$
		rd.forward(req, resp);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("title"); //$NON-NLS-1$
		String description = request.getParameter("description"); //$NON-NLS-1$
		String status = request.getParameter("status"); //$NON-NLS-1$
		String approved = request.getParameter("approved"); //$NON-NLS-1$
		String reviewed = request.getParameter("reviewed"); //$NON-NLS-1$
		String verified = request.getParameter("verified"); //$NON-NLS-1$
		try{
			RioStore store = getStore();
			String uri = store.nextAvailableUri(ICmConstants.SERVICE_CHANGEREQUEST);
			ChangeRequest resource = new ChangeRequest(uri);
			resource.addRdfType(ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
			resource.setTitle(title);
			resource.setDescription(description);
			resource.setStatus(status);
			resource.setApproved(approved != null);
			resource.setReviewed(reviewed != null);
			resource.setVerified(verified != null);
			String userUri = getUserUri(request.getRemoteUser());
			store.update(resource, userUri);
			
			request.setAttribute("resource", resource);  //$NON-NLS-1$
			
			RequestDispatcher rd = request.getRequestDispatcher("/cm/changerequest_created.jsp"); //$NON-NLS-1$
			rd.forward(request, response);
			
		} catch( Exception e) {
			throw new RioServiceException(e);
		}
	}


}
