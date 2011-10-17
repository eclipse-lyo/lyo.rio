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
package org.eclipse.lyo.rio.cm.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.cm.changerequest.ChangeRequest;
import org.eclipse.lyo.rio.cm.common.ICmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioStore;


/**
 * Servlet implementation class ResourceFactory
 */
public class ChangeRequestWebService extends RioBaseService {
	private static final long serialVersionUID = 7466374797163202313L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("/cm/changerequest_creator.jsp"); //$NON-NLS-1$
		rd.forward(req, resp);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getParameter("uri"); //$NON-NLS-1$
		String eTag = request.getParameter("eTag"); //$NON-NLS-1$
		
		String saveType = request.getParameter("Save"); //$NON-NLS-1$

		try{
			RioStore store = getStore();
			ChangeRequest cr = null;
			if( uri == null ) {
				uri = store.nextAvailableUri(ICmConstants.SERVICE_CHANGEREQUEST);
				cr = new ChangeRequest(uri);
			} else {
				cr = new ChangeRequest(uri);
				store.getResource(cr);
				if( !eTag.equals(cr.getETag()) ){
					throw new RioServiceException(IConstants.SC_CONFLICT, "ETag mismatch");
				}
			}
			
			if( !cr.isRdfType(ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST) ) cr.addRdfType(ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
			
			if( "SaveRdf".equals(saveType) ){//$NON-NLS-1$
				String rdfxml = request.getParameter("rdfxml"); //$NON-NLS-1$
				ByteArrayInputStream content = new ByteArrayInputStream(rdfxml.getBytes());
				
				// cache the created and creator
				Date created = cr.getCreated();
				String creator = cr.getCreator();
				
				ChangeRequest updatedResource = new ChangeRequest(cr.getUri());
				List<RioStatement> statements = store.parse(cr.getUri(), content, IConstants.CT_RDF_XML);
				updatedResource.addStatements(statements);
				updatedResource.setCreated(created);
				updatedResource.setCreator(creator);
				String userId = request.getRemoteUser();
				String userUri = this.getUserUri(userId);
				store.update(updatedResource, userUri);

			} else {
				String title = request.getParameter("title"); //$NON-NLS-1$
				String description = request.getParameter("description"); //$NON-NLS-1$
				String status = request.getParameter("status"); //$NON-NLS-1$
				String approved = request.getParameter("approved"); //$NON-NLS-1$
				String reviewed = request.getParameter("reviewed"); //$NON-NLS-1$
				String verified = request.getParameter("verified"); //$NON-NLS-1$
				cr.setTitle(title);
				cr.setDescription(description);
				cr.setStatus(status);
				cr.setApproved(approved != null);
				cr.setReviewed(reviewed != null);
				cr.setVerified(verified != null);
				String userUri = getUserUri(request.getRemoteUser());
				store.update(cr, userUri);
			}
			
			response.sendRedirect(store.getUriBase());
			
		} catch( Exception e) {
			throw new RioServiceException(e);
		}
	}


}
