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

import java.util.Collection;
import java.util.List;

import org.eclipse.lyo.rio.cm.common.ICmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioValue.RioValueType;


public class ChangeRequest extends OslcResource {

	public ChangeRequest(String uri) throws RioServerException {
		super(uri);
		addRdfType(ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
	}

	public ChangeRequest(String uri, List<RioStatement> statements) {
		super(uri, statements);
	}
	
	public void setDescription(String description) throws RioServerException {
		this.setStringProperty(IConstants.DCTERMS_DESCRIPTION, description);
	}

	public String getDescription() {
		return this.getFirstStringProperty(IConstants.DCTERMS_DESCRIPTION);
	}
	
	public String getStatus() {
		return this.getFirstStringProperty(ICmConstants.OSLC_CM_STATUS);
	}

	public void setStatus(String status) throws RioServerException {
		this.setStringProperty(ICmConstants.OSLC_CM_STATUS, status);
		
		// update state 
		if( ICmConstants.OSLC_CM_STATUS_SUBMITTED.equals(status) ) {
			this.setClosed(false);
			this.setInProgress(false);
			this.setFixed(false);
		} else if( ICmConstants.OSLC_CM_STATUS_IN_PROGRESS.equals(status) ) {
			this.setClosed(false);
			this.setInProgress(true);
			this.setFixed(false);
		} else if( ICmConstants.OSLC_CM_STATUS_DONE.equals(status) ) {
			this.setClosed(true);
			this.setInProgress(false);
			this.setFixed(false);
		} else if( ICmConstants.OSLC_CM_STATUS_FIXED.equals(status) ) {
			this.setClosed(true);
			this.setInProgress(false);
			this.setFixed(true);
		} else {
			throw new RioServerException("Invalid status value: " + status);
		}
		
	}

	public boolean isClosed() throws RioServerException {
		return this.getFirstBooleanProperty(ICmConstants.OSLC_CM_CLOSED, false);
	}
	
	public void setClosed(boolean closed) throws RioServerException {
		addProperty(ICmConstants.OSLC_CM_CLOSED, RioValueType.BOOLEAN, new Boolean(closed), true);
	}
	
	public boolean isInProgress() throws RioServerException {
		return this.getFirstBooleanProperty(ICmConstants.OSLC_CM_INPROGRESS, true);
	}
	
	public void setInProgress(boolean inProgress) throws RioServerException {
		addProperty(ICmConstants.OSLC_CM_INPROGRESS, RioValueType.BOOLEAN, new Boolean(inProgress), true);
	}
	
	public boolean isFixed() throws RioServerException {
		return this.getFirstBooleanProperty(ICmConstants.OSLC_CM_FIXED, false);
	}
	
	public void setFixed(boolean fixed) throws RioServerException {
		addProperty(ICmConstants.OSLC_CM_FIXED, RioValueType.BOOLEAN, new Boolean(fixed), true);
	}
	
	public boolean isApproved() throws RioServerException {
		return this.getFirstBooleanProperty(ICmConstants.OSLC_CM_APPROVED, false);
	}
	
	public void setApproved(boolean approved) throws RioServerException {
		addProperty(ICmConstants.OSLC_CM_APPROVED, RioValueType.BOOLEAN, new Boolean(approved), true);
	}
	
	public boolean isReviewed() throws RioServerException {
		return this.getFirstBooleanProperty(ICmConstants.OSLC_CM_REVIEWED, false);
	}
	
	public void setReviewed(boolean reviewed) throws RioServerException {
		addProperty(ICmConstants.OSLC_CM_REVIEWED, RioValueType.BOOLEAN, new Boolean(reviewed), true);
	}
	
	public boolean isVerified() throws RioServerException {
		return this.getFirstBooleanProperty(ICmConstants.OSLC_CM_VERIFIED, false);
	}
	
	public void setVerified(boolean verified) throws RioServerException {
		addProperty(ICmConstants.OSLC_CM_VERIFIED, RioValueType.BOOLEAN, new Boolean(verified), true);
	}

	public Collection<String> getRelatedChangeRequests() {
		return getUriProperties(ICmConstants.OSLC_CM_RELATEDCHANGEREQUEST);
	}
	
	public void addRelatedChangeRequest(String crUri) throws RioServerException {
		this.addUriProperty(ICmConstants.OSLC_CM_RELATEDCHANGEREQUEST, crUri);
	}

	public void addAffectsPlanItem(String crUri) throws RioServerException {
		this.addUriProperty(ICmConstants.OSLC_CM_AFFECTSPLANITEM, crUri);
	}
	
	public Collection<String> getAffectsPlanItem() {
		return getUriProperties(ICmConstants.OSLC_CM_AFFECTSPLANITEM);
	}
	
	public Collection<String> getAffectedByDefect() {
		return getUriProperties(ICmConstants.OSLC_CM_AFFECTEDBYDEFECT);
	}
	
	public void addAffectedByDefect(String crUri) throws RioServerException {
		this.addUriProperty(ICmConstants.OSLC_CM_RELATEDCHANGEREQUEST, crUri);
	}

	
}
