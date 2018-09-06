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


import org.eclipse.lyo.oslc.rm.common.IRmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.l10n.Messages;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStore;

public class CatalogResource extends RioResource {

	public CatalogResource(String uri) throws RioServerException {
		super(uri);
		RioStore store = RioStore.getStore();
		String urlBase = store.getUriBase();
		
		this.addRdfType(IConstants.OSLC_TYPE_SERVICEPROVIDERCATALOG);
		this.setStringProperty(IConstants.DCTERMS_TITLE, Messages.getString("Catalog.CatalogDocumentTitle"));
		this.setStringProperty(IConstants.DCTERMS_DESCRIPTION, Messages.getString("Catalog.CatalogDocumentDescription") );
		RioResource publisher = this.createInlinedResource(IConstants.DCTERMS_PUBLISHER, IConstants.OSLC_TYPE_PUBLISHER);
		publisher.setStringProperty(IConstants.DCTERMS_TITLE, IRmConstants.RIO_RM_PUBLISHER_TITLE);
		publisher.setStringProperty(IConstants.DCTERMS_IDENTIFIER, IRmConstants.RIO_RM_PUBLISHER_IDENTIFIER);
		String iconUrl = urlBase +  '/' + IRmConstants.RIO_RM_ICON;
		publisher.setUriProperty( IConstants.OSLC_ICON, iconUrl) ;
		this.setStringProperty(IConstants.OSLC_DOMAIN, IRmConstants.OSLC_RM_NAMESPACE);
		this.setUriProperty(IConstants.OSLC_SERVICEPROVIDER, urlBase + "/services");
		
	}

}
