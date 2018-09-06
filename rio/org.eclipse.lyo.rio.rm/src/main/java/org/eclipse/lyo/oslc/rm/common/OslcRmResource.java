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
/**
 * 
 */
package org.eclipse.lyo.oslc.rm.common;

import java.util.Collection;
import java.util.List;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;

/**
 * @author jconallen
 *
 */
public class OslcRmResource extends OslcResource {

	public OslcRmResource(String uri) throws RioServerException {
		super(uri);
	}

	public OslcRmResource(String uri, List<RioStatement> statements) {
		super(uri, statements);
	}

	
	/**
	 * @return URI of base resource
	 */
	public Collection<String> getIsVersionOf() {
		return this.getUriProperties(IConstants.DCTERMS_ISVERSIONOF);
	}
	
	/**
	 * @param resUri
	 * @throws RioServerException
	 */
	public void addIsVersionOf(String resUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_ISVERSIONOF, resUri);
	}
		 
	/**
	 * @return URI(s) of version(s) of this (base) resource
	 */
	public Collection<String> getHasVersion() {
		return this.getUriProperties(IConstants.DCTERMS_HASVERSION);
	}
	
	/**
	 * @param resUri
	 * @throws RioServerException
	 */
	public void addHasVersionOf(String resUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_HASVERSION, resUri);
	}

	/**
	 * @return URI of resource version this version is immediately following
	 */
	public Collection<String> getReplaces() {
		return this.getUriProperties(IConstants.DCTERMS_REPLACES);
	}
	
	/**
	 * @param resUri
	 * @throws RioServerException
	 */
	public void addReplaces(String resUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_REPLACES, resUri);
	}

	/**
	 * @return URI of resource version this resource view represents
	 */
	public Collection<String> getSubject() {
		return this.getUriProperties(IConstants.DCTERMS_SUBJECT);
	}
	
	/**
	 * @param resUri
	 * @throws RioServerException
	 */
	public void addSubject(String resUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_SUBJECT, resUri);
	}

	/**
	 * @return URI(s) of resources making up this resource view
	 */
	public Collection<String> getHasPart() {
		return this.getUriProperties(IConstants.DCTERMS_HASPART);
	}
	
	/**
	 * @param resUri
	 * @throws RioServerException
	 */
	public void addHasPart(String resUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_HASPART, resUri);
	}

}
