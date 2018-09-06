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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lyo.oslc.am.common.IAmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioValue;
import org.eclipse.lyo.rio.store.RioValue.RioValueType;
import org.eclipse.lyo.rio.store.UnrecognizedValueTypeException;


public class LinkType extends OslcResource {
	
	public LinkType(String uri, String about) throws RioServerException {
		super(uri);
		this.createProperty(about, IConstants.RDF_TYPE, RioValueType.URI, IAmConstants.OSLC_AM_TYPE_LINKTYPE, true);
		this.setAbout(about);
	}

	public LinkType(String uri, String about, String label, String comments) throws RioServerException {
		super(uri);
		this.createProperty(about, IConstants.RDF_TYPE, RioValueType.URI, IAmConstants.OSLC_AM_TYPE_LINKTYPE, true);
		this.setAbout(about); // always set about first!
		this.setLabel(label);
		this.setComments(comments);
	}

	public String getAbout() {
		List<RioStatement> sts = this.getStatements(null, IConstants.RDFS_LABEL, null);
		if( !sts.isEmpty() ) {
			return sts.get(0).getSubject();
		}
		return null;
	}

	public void setAbout(String about) throws RioServerException {
		String label = getLabel();
		this.createProperty(about, IConstants.RDFS_LABEL, RioValueType.STRING, label, true);
	}

	public void setLabel(String label) throws RioServerException {
		try{
			RioValue val = new RioValue(RioValueType.STRING, label);
			this.createProperty(getAbout(), IConstants.RDFS_LABEL, val, true);
		} catch(UnrecognizedValueTypeException e ) {
			throw new RioServerException(e);
		}
	}
	
	public String getLabel() {
		List<RioStatement> sts = this.getStatements(getAbout(), IConstants.RDFS_LABEL, null);
		if( !sts.isEmpty() ) {
			RioStatement statement = sts.get(0);
			return statement.getObject().stringValue();
		}
		return "";
	}

	public void setComments(String comments) throws RioServerException {
		try{
			RioValue val = new RioValue(RioValueType.STRING, comments);
			this.createProperty(getAbout(), IConstants.RDFS_COMMENTS, val, true);
		} catch(UnrecognizedValueTypeException e ) {
			throw new RioServerException(e);
		}
	}
	
	public String getComments() {
		List<RioStatement> sts = this.getStatements(getAbout(), IConstants.RDFS_COMMENTS, null);
		if( !sts.isEmpty() ) {
			RioStatement statement = sts.get(0);
			return statement.getObject().stringValue();
		}
		return null;
	}

	public List<String> validate() {
		List<String> errors = new ArrayList<String>();
		if( getLabel() == null ) {
			errors.add("Label is required for link types");
		}
		if( getAbout() == null ) {
			errors.add("Link type about URI is required for link types");
		}
		
		return errors;
	}

}
