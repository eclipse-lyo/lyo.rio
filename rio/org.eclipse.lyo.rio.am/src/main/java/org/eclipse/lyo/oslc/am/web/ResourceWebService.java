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
package org.eclipse.lyo.oslc.am.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.oslc.am.common.IAmConstants;
import org.eclipse.lyo.oslc.am.resource.Resource;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioStore;

public class ResourceWebService extends RioBaseService {
	
	private static final long serialVersionUID = 6031384602038217153L;


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
		
		String saveType = request.getParameter("Save");
		
		try{
			RioStore store = getStore();
			Resource resource = null;
			if( uri == null ) {
				uri = store.nextAvailableUri(IAmConstants.SERVICE_RESOURCE);
				resource = new Resource(uri);
			} else {
				OslcResource soriResource = store.getOslcResource(uri);
				if( !eTag.equals(soriResource.getETag()) ){
					throw new RioServiceException(IConstants.SC_CONFLICT, "ETag mismatch");
				}
				resource = new Resource(uri, soriResource.getStatements());
			}
			
			if( saveType.equals("SaveRdf")) {
				String rdfxml = request.getParameter("rdfxml"); 
				ByteArrayInputStream content = new ByteArrayInputStream(rdfxml.getBytes());
				
				// cache the created and creator
				Date created = resource.getCreated();
				String creator = resource.getCreator();
				
				OslcResource updatedResource = new OslcResource(resource.getUri());
				List<RioStatement> statements = store.parse(resource.getUri(), content, IConstants.CT_RDF_XML);
				updatedResource.addStatements(statements);
				updatedResource.setCreated(created);
				updatedResource.setCreator(creator);
				String userId = request.getRemoteUser();
				String userUri = this.getUserUri(userId);
				store.update(updatedResource, userUri);

			} else {
				String title = request.getParameter("title"); //$NON-NLS-1$
				String description = request.getParameter("description"); //$NON-NLS-1$

				resource.setTitle(title);
				resource.setDescription(description);

				String userUri = getUserUri(request.getRemoteUser());
				store.update(resource, userUri);
				
			}
			
			response.sendRedirect(store.getUriBase());

			
		} catch( Exception e) {
			throw new RioServiceException(e);
		}
	}


}
