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

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.lyo.oslc.rm.common.IRmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.l10n.Messages;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStore;

public class ServiceResource extends RioResource {

	public ServiceResource(String uri) throws RioServerException {
		super(uri);
		RioStore store = RioStore.getStore();
		String urlBase = store.getUriBase();
		
		this.addRdfType(IConstants.OSLC_TYPE_SERVICEPROVIDER);
		this.addUriProperty(IConstants.OSLC_DETAILS, urlBase + "/about.jsp");
		this.setStringProperty(IConstants.DCTERMS_TITLE, Messages.getString("Catalog.ServiceDocumentTitle"));
		this.setStringProperty(IConstants.DCTERMS_DESCRIPTION, Messages.getString("Catalog.CatalogDocumentDescription") );
		
		RioResource publisher = this.createInlinedResource(IConstants.DCTERMS_PUBLISHER, IConstants.OSLC_TYPE_PUBLISHER);
		publisher.setStringProperty(IConstants.DCTERMS_TITLE, IRmConstants.RIO_RM_PUBLISHER_TITLE);
		publisher.setStringProperty(IConstants.DCTERMS_IDENTIFIER, IRmConstants.RIO_RM_PUBLISHER_IDENTIFIER);
		
		Map<String, String> predefinedMappings = RioStore.getPredefinedNamespaceMappings();
		Set<Entry<String, String>> entries = predefinedMappings.entrySet();
		for (Entry<String, String> prefixMapping : entries) {
			RioResource prefixDef = this.createInlinedResource(IConstants.OSLC_PREFIXDEFINITION, IConstants.OSLC_TYPE_PREFIXDEFINITION );
			prefixDef.setStringProperty(IConstants.OSLC_PREFIX_PROP, prefixMapping.getValue());
			prefixDef.setStringProperty(IConstants.OSLC_PREFIXBASE, prefixMapping.getKey());
		}
		RioResource prefixDef = this.createInlinedResource(IConstants.OSLC_PREFIXDEFINITION, IConstants.OSLC_TYPE_PREFIXDEFINITION );
		prefixDef.setStringProperty(IConstants.OSLC_PREFIX_PROP, IRmConstants.OSLC_RM_PREFIX);
		prefixDef.setStringProperty(IConstants.OSLC_PREFIXBASE, IRmConstants.OSLC_RM_NAMESPACE);
		
		// RM services
		
		RioResource service = this.createInlinedResource(IConstants.OSLC_SERVICE, IConstants.OSLC_TYPE_SERVICE);
		service.setStringProperty(IConstants.OSLC_DOMAIN, IRmConstants.OSLC_RM_NAMESPACE);
		
		// Requirement
		
		String resShapeUrl = urlBase + "/shapes?type=Requirement"; 

		RioResource creationFactory = service.createInlinedResource(IConstants.OSLC_CREATIONFACTORY, IConstants.OSLC_TYPE_CREATIONFACTORY);
		creationFactory.setStringProperty(IConstants.DCTERMS_TITLE, "Requiement Factory");
		creationFactory.setStringProperty(IConstants.OSLC_LABEL, "Requirement Factory");
		creationFactory.setUriProperty(IConstants.OSLC_CREATION, urlBase + '/' + IRmConstants.SERVICE_REQUIREMENT);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCETYPE, IRmConstants.OSLC_RM_TYPE_REQUIREMENT);
		creationFactory.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		RioResource queryCapability = service.createInlinedResource(IConstants.OSLC_QUERYCAPABILITY, IConstants.OSLC_TYPE_QUERYCAPABILITY);
		queryCapability.setStringProperty(IConstants.DCTERMS_TITLE, "Requirement Query");
		queryCapability.setStringProperty(IConstants.OSLC_LABEL, "Requirement Query");
		queryCapability.setUriProperty(IConstants.OSLC_QUERYBASE, urlBase + '/' + IRmConstants.SERVICE_REQUIREMENT);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCETYPE, IRmConstants.OSLC_RM_TYPE_REQUIREMENT);
		queryCapability.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		RioResource dialog = service.createInlinedResource(IConstants.OSLC_SELECTIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "Requirement Selector");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "Requirement Picker");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, IRmConstants.RIO_RM_SELECTION_REQUIREMENT_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, IRmConstants.RIO_RM_SELECTION_REQUIREMENT_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/selector/" + IRmConstants.SERVICE_REQUIREMENT);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, IRmConstants.OSLC_RM_TYPE_REQUIREMENT);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
		dialog = service.createInlinedResource(IConstants.OSLC_CREATIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "Requirement Creator");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "Requirement Creator");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, IRmConstants.RIO_RM_SELECTION_REQUIREMENT_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, IRmConstants.RIO_RM_SELECTION_REQUIREMENT_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/creator/" + IRmConstants.SERVICE_REQUIREMENT);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, IRmConstants.OSLC_RM_TYPE_REQUIREMENT);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
		// RequirementCollection
		
		resShapeUrl = urlBase + "/shapes?type=RequirementCollection"; 

		creationFactory = service.createInlinedResource(IConstants.OSLC_CREATIONFACTORY, IConstants.OSLC_TYPE_CREATIONFACTORY);
		creationFactory.setStringProperty(IConstants.DCTERMS_TITLE, "Requirement Collection Factory");
		creationFactory.setStringProperty(IConstants.OSLC_LABEL, "ReqCol Factory");
		creationFactory.setUriProperty(IConstants.OSLC_CREATION, urlBase + '/' + IRmConstants.SERVICE_REQCOL);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCETYPE, IRmConstants.OSLC_RM_TYPE_REQUIREMENTCOLLECTION);
		creationFactory.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		queryCapability = service.createInlinedResource(IConstants.OSLC_QUERYCAPABILITY, IConstants.OSLC_TYPE_QUERYCAPABILITY);
		queryCapability.setStringProperty(IConstants.DCTERMS_TITLE, "Requirement Collection Query");
		queryCapability.setStringProperty(IConstants.OSLC_LABEL, "ReqCol Query");
		queryCapability.setUriProperty(IConstants.OSLC_QUERYBASE, urlBase + '/' + IRmConstants.SERVICE_REQCOL);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCETYPE, IRmConstants.OSLC_RM_TYPE_REQUIREMENTCOLLECTION);
		queryCapability.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		dialog = service.createInlinedResource(IConstants.OSLC_SELECTIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "Requirement Collection Selector");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "ReqCol Picker");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, IRmConstants.RIO_RM_SELECTION_REQUIREMENT_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, IRmConstants.RIO_RM_SELECTION_REQUIREMENT_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/selector/" + IRmConstants.SERVICE_REQCOL);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, IRmConstants.OSLC_RM_TYPE_REQUIREMENTCOLLECTION);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
		dialog = service.createInlinedResource(IConstants.OSLC_CREATIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "Requirement Collection Creator");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "ReqCol Creator");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, IRmConstants.RIO_RM_SELECTION_REQCOL_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, IRmConstants.RIO_RM_SELECTION_REQCOL_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/creator/" + IRmConstants.SERVICE_REQCOL);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, IRmConstants.OSLC_RM_TYPE_REQUIREMENTCOLLECTION);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
	}

}
