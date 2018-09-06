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
package org.eclipse.lyo.oslc.rm.services.requirementcollection;

import java.util.Collection;
import java.util.List;

import org.eclipse.lyo.oslc.rm.common.IRmConstants;
import org.eclipse.lyo.oslc.rm.common.OslcRmResource;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioValue;
import org.eclipse.lyo.rio.store.RioValue.RioValueType;


public class RequirementCollection extends OslcRmResource {
	
		public RequirementCollection(String uri) throws RioServerException {
		super(uri);
	}

	public RequirementCollection(String uri, List<RioStatement> statements) {
		super(uri, statements);
	}
	
	public RequirementCollection(RioResource rioResource) throws RioServerException {
		super(rioResource.getUri());
		this.addStatements(rioResource.getStatements());
	}
	
	public String getDescription() {
		RioStatement statement = this.getFirstStatement(uri, IConstants.DCTERMS_DESCRIPTION, null);
		if( statement != null ) {
			RioValue value = statement.getObject();
			return value.stringValue();
		} 
		return null; 
	}

	public void setDescription(String description) throws RioServerException {
		addProperty(IConstants.DCTERMS_DESCRIPTION, RioValueType.STRING, description, true);
	}

	public String getShortTitle() {
		RioStatement statement = this.getFirstStatement(uri, IConstants.OSLC_SHORTTITLE, null);
		if( statement != null ) {
			RioValue value = statement.getObject();
			return value.stringValue();
		} 
		return null; 
	}

	public void setShortTitle(String shortTitle) throws RioServerException {
		addProperty(IConstants.OSLC_SHORTTITLE, RioValueType.STRING, shortTitle, true);
	}
	
	public Collection<String> getIsVersionOf() {
		return getUriProperties(IConstants.DCTERMS_ISVERSIONOF);
	}
	
	public void addIsVersionOf(String crUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_ISVERSIONOF, crUri);
	}

	public Collection<String> getHasVersion() {
		return getUriProperties(IConstants.DCTERMS_HASVERSION);
	}
	
	public void addHasVersionOf(String crUri) throws RioServerException {
		this.addUriProperty(IConstants.DCTERMS_HASVERSION, crUri);
	}

	public Collection<String> getUses() {
		return getUriProperties(IRmConstants.OSLC_RM_USES);
	}
	
	public void addUses(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_USES, crUri);
	}

	public Collection<String> getElaboratedBy() {
		return getUriProperties(IRmConstants.OSLC_RM_ELABORATEDBY);
	}
	
	public void addElaboratedBy(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_ELABORATEDBY, crUri);
	}

	public Collection<String> getSpecifiedBy() {
		return getUriProperties(IRmConstants.OSLC_RM_SPECIFIEDBY);
	}
	
	public void addSpecifiedBy(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_SPECIFIEDBY, crUri);
	}

	public Collection<String> getAffectedBy() {
		return getUriProperties(IRmConstants.OSLC_RM_AFFECTEDBY);
	}
	
	public void addAffectedBy(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_AFFECTEDBY, crUri);
	}

	public Collection<String> getTrackedBy() {
		return getUriProperties(IRmConstants.OSLC_RM_TRACKEDBY);
	}
	
	public void addTrackedBy(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_TRACKEDBY, crUri);
	}

	public Collection<String> getImplementedBy() {
		return getUriProperties(IRmConstants.OSLC_RM_IMPLEMENTEDBY);
	}
	
	public void addImplementedBy(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_IMPLEMENTEDBY, crUri);
	}

	public Collection<String> getValidatedBy() {
		return getUriProperties(IRmConstants.OSLC_RM_VALIDATEDBY);
	}
	
	public void addValidatedBy(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.OSLC_RM_VALIDATEDBY, crUri);
	}

	public Collection<String> getNestedClassifier() {
		return getUriProperties(IRmConstants.UML4SYSML_NESTEDCLASSIFIER);
	}
	
	public void addNestedClassifier(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.UML4SYSML_NESTEDCLASSIFIER, crUri);
	}

	public Collection<String> getTrace() {
		return getUriProperties(IRmConstants.UML4SYSML_TRACE);
	}
	
	public void addTrace(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.UML4SYSML_TRACE, crUri);
	}

	public Collection<String> getDeriveReqt() {
		return getUriProperties(IRmConstants.SYSMLREQUIREMENTS_DERIVEREQT);
	}
	
	public void addDeriveReqt(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.SYSMLREQUIREMENTS_DERIVEREQT, crUri);
	}

	public Collection<String> getSatisfy() {
		return getUriProperties(IRmConstants.SYSMLREQUIREMENTS_SATISFY);
	}
	
	public void addSatisfy(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.SYSMLREQUIREMENTS_SATISFY, crUri);
	}

	public Collection<String> getVerify() {
		return getUriProperties(IRmConstants.SYSMLREQUIREMENTS_VERIFY);
	}
	
	public void addVerify(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.SYSMLREQUIREMENTS_VERIFY, crUri);
	}

	public Collection<String> getRefine() {
		return getUriProperties(IRmConstants.SYSMLREQUIREMENTS_REFINE);
	}
	
	public void addRefine(String crUri) throws RioServerException {
		this.addUriProperty(IRmConstants.SYSMLREQUIREMENTS_REFINE, crUri);
	}

}
