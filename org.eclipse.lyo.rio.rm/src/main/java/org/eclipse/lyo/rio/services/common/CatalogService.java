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
package org.eclipse.lyo.rio.services.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.oslc.rm.common.IRmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.JsonFormatter2;
import org.eclipse.lyo.rio.store.RdfXmlFormatter;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.XmlFormatter;
import org.eclipse.lyo.rio.store.JsonFormatter2.IMultiValueResolver;

/**
 * Servlet implementation class Catalog
 */
public class CatalogService extends RioBaseService {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			CatalogResource catRes = new CatalogResource(req.getRequestURL().toString());
			
			String accept = req.getHeader(IConstants.HDR_ACCEPT);
			if( accept != null ) {
				// try this in our order of preference
				if( accept.indexOf(IConstants.CT_XML)>=0 || accept.indexOf(IConstants.CT_RDF_XML)>=0 ) {
					XmlFormatter formatter = new XmlFormatter();
					formatter.addNamespacePrefix(IRmConstants.OSLC_RM_NAMESPACE, IRmConstants.OSLC_RM_PREFIX);
					String servicesResource = formatter.format(catRes, IConstants.OSLC_TYPE_SERVICEPROVIDERCATALOG);
					resp.setContentType(IConstants.CT_RDF_XML); 
					resp.getWriter().write(servicesResource); 
					resp.setStatus(IConstants.SC_OK);
				} else if( accept.indexOf(IConstants.CT_JSON)>=0  ) {
					JsonFormatter2 formatter = new JsonFormatter2(new IMultiValueResolver() {
						
						@Override
						public boolean isMultiValued(String property) {
							if( IConstants.OSLC_SERVICEPROVIDER.equals(property) || 
									IConstants.OSLC_DOMAIN.equals(property) ) {
								return true;
							}  
							return false;
						}
					});
					formatter.addNamespacePrefix(IRmConstants.OSLC_RM_NAMESPACE, IRmConstants.OSLC_RM_PREFIX);
					String servicesResource = formatter.format(catRes);
					resp.setContentType(IConstants.CT_JSON); 
					resp.getWriter().write(servicesResource); 
					resp.setStatus(IConstants.SC_OK);
				} else if( accept.indexOf(IConstants.CT_TEXT_PLAIN)>=0 || accept.indexOf(IConstants.CT_APP_N_TRIPLES)>=0 ) {
					String triples = catRes.dumpNTriples();
					resp.setContentType(IConstants.CT_APP_N_TRIPLES); 
					resp.getWriter().write(triples); 
					resp.setStatus(IConstants.SC_OK);
				}
				
			} else {
				throw new RioServiceException(IConstants.SC_NOT_ACCEPTABLE, "Accept header required" );
			}
			
		} catch( RioServerException e ) { 
			throw new RioServiceException(e);
		}
	}

}
