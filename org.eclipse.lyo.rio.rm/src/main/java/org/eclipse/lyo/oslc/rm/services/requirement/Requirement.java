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
package org.eclipse.lyo.oslc.rm.services.requirement;

import java.util.Collection;
import java.util.List;

import org.eclipse.lyo.oslc.rm.common.IRmConstants;
import org.eclipse.lyo.oslc.rm.common.OslcRmResource;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;

public class Requirement extends OslcRmResource {

	public Requirement(String uri) throws RioServerException {
		super(uri);
	}

	public Requirement(String uri, List<RioStatement> statements) {
		super(uri, statements);
	}
	
	public Requirement(RioResource rioResource) throws RioServerException {
		super(rioResource.getUri());
		this.addStatements(rioResource.getStatements());
	}
	
	public String getDescription() {
		return this.getFirstStringProperty(IConstants.DCTERMS_DESCRIPTION);
	}

	public void setDescription(String description) throws RioServerException {
		this.setStringProperty(IConstants.DCTERMS_DESCRIPTION, description);
	}

	public String getShortTitle() {
		return this.getFirstStringProperty(IConstants.OSLC_SHORTTITLE);
	}

	public void setShortTitle(String shortTitle) throws RioServerException {
		this.setStringProperty(IConstants.OSLC_SHORTTITLE, shortTitle);
	}
	
	private Collection<String> getReferencedUris(String propertyUri) {
		return this.getUriProperties(propertyUri);
	}
	
	public Collection<String> getIsVersionOf() {
		return this.getUriProperties(IConstants.DCTERMS_ISVERSIONOF);
	}
	
	public void addIsVersionOf(String reqUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_ISVERSIONOF, reqUri);
	}

	public Collection<String> getHasVersion() {
		return this.getUriProperties(IConstants.DCTERMS_HASVERSION);
	}
	
	public void addHasVersionOf(String reqUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_HASVERSION, reqUri);
	}

	public Collection<String> getElaboratedBy() {
		return getReferencedUris(IRmConstants.OSLC_RM_ELABORATEDBY);
	}
	
	public void addElaboratedBy(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_ELABORATEDBY, resUri);
	}

	public Collection<String> getSpecifiedBy() {
		return getReferencedUris(IRmConstants.OSLC_RM_SPECIFIEDBY);
	}
	
	public void addSpecifiedBy(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_SPECIFIEDBY, resUri);
	}

	public Collection<String> getAffectedBy() {
		return getReferencedUris(IRmConstants.OSLC_RM_AFFECTEDBY);
	}
	
	public void addAffectedBy(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_AFFECTEDBY, resUri);
	}

	public Collection<String> getTrackedBy() {
		return getReferencedUris(IRmConstants.OSLC_RM_TRACKEDBY);
	}
	
	public void addTrackedBy(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_TRACKEDBY, resUri);
	}

	public Collection<String> getImplementedBy() {
		return getReferencedUris(IRmConstants.OSLC_RM_IMPLEMENTEDBY);
	}
	
	public void addImplementedBy(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_IMPLEMENTEDBY, resUri);
	}

	public Collection<String> getValidatedBy() {
		return getReferencedUris(IRmConstants.OSLC_RM_VALIDATEDBY);
	}
	
	public void addValidatedBy(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_VALIDATEDBY, resUri);
	}

	public Collection<String> getNestedClassifier() {
		return getReferencedUris(IRmConstants.UML4SYSML_NESTEDCLASSIFIER);
	}
	
	public void addNestedClassifier(String reqUri) throws RioServerException {
		this.addUriProperty(IRmConstants.UML4SYSML_NESTEDCLASSIFIER, reqUri);
	}

	public Collection<String> getTrace() {
		return getReferencedUris(IRmConstants.UML4SYSML_TRACE);
	}
	
	public void addTrace(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.UML4SYSML_TRACE, resUri);
	}

	public Collection<String> getDeriveReqt() {
		return getReferencedUris(IRmConstants.SYSMLREQUIREMENTS_DERIVEREQT);
	}
	
	public void addDeriveReqt(String reqUri) throws RioServerException {
		this.addUriProperty(IRmConstants.SYSMLREQUIREMENTS_DERIVEREQT, reqUri);
	}

	public Collection<String> getSatisfy() {
		return getReferencedUris(IRmConstants.SYSMLREQUIREMENTS_SATISFY);
	}
	
	public void addSatisfy(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.SYSMLREQUIREMENTS_SATISFY, resUri);
	}

	public Collection<String> getVerify() {
		return getReferencedUris(IRmConstants.SYSMLREQUIREMENTS_VERIFY);
	}
	
	public void addVerify(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.SYSMLREQUIREMENTS_VERIFY, resUri);
	}

	public Collection<String> getRefine() {
		return getReferencedUris(IRmConstants.SYSMLREQUIREMENTS_REFINE);
	}
	
	public void addRefine(String resUri) throws RioServerException {
		this.addUriProperty(IRmConstants.SYSMLREQUIREMENTS_REFINE, resUri);
	}

}
