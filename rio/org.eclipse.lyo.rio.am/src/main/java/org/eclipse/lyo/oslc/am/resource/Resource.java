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

import java.util.List;

import org.eclipse.lyo.oslc.am.common.IAmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;

public class Resource extends OslcResource {

	public Resource(String uri) throws RioServerException {
		super(uri);
	}

	public Resource(String uri, List<RioStatement> statements) {
		super(uri, statements);
	}
	
	public Resource(RioResource rioResource) throws RioServerException {
		super(rioResource.getUri());
		this.addStatements(rioResource.getStatements());
	}
	
	public String getSource() {
		return this.getFirstUriProperty(IConstants.DCTERMS_SOURCE);
	}
	
	public void setSource(String source) throws RioServerException {
		this.setStringProperty(IConstants.DCTERMS_SOURCE, source);
	}

	public String getSourceContentType() {
		return this.getFirstStringProperty(IAmConstants.RIO_AM_SOURCE_CONTENT_TYPE);
	}
	
	public void setSourceContentType(String contentType) throws RioServerException {
		this.setStringProperty(IAmConstants.RIO_AM_SOURCE_CONTENT_TYPE, contentType);
	}
	
	public void setDescription(String description) throws RioServerException {
		this.setStringProperty(IConstants.DCTERMS_DESCRIPTION, description);
	}

	public String getDescription() {
		return this.getFirstStringProperty(IConstants.DCTERMS_DESCRIPTION);
	}

}
