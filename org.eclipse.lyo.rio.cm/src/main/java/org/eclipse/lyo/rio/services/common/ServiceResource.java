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
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.lyo.rio.cm.common.ICmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStore;

public class ServiceResource extends RioResource {

	public ServiceResource(String uri) throws RioServerException {
		super(uri);
		RioStore store = RioStore.getStore();
		String urlBase = store.getUriBase();
		
		this.addRdfType(IConstants.OSLC_TYPE_SERVICEPROVIDER);
		this.setStringProperty(IConstants.DCTERMS_TITLE, "RIO CM Services Document");
		this.setStringProperty(IConstants.DCTERMS_DESCRIPTION, "RIO CM Services Document" );
		
		RioResource publisher = this.createInlinedResource(IConstants.DCTERMS_PUBLISHER, IConstants.OSLC_TYPE_PUBLISHER);
		publisher.setStringProperty(IConstants.DCTERMS_TITLE, ICmConstants.RIO_CM_PUBLISHER_TITLE);
		publisher.setStringProperty(IConstants.DCTERMS_IDENTIFIER, ICmConstants.RIO_CM_PUBLISHER_IDENTIFIER);
		
		Map<String, String> predefinedMappings = RioStore.getPredefinedNamespaceMappings();
		Set<Entry<String, String>> entries = predefinedMappings.entrySet();
		for (Entry<String, String> prefixMapping : entries) {
			RioResource prefixDef = this.createInlinedResource(IConstants.OSLC_PREFIXDEFINITION, IConstants.OSLC_TYPE_PREFIXDEFINITION );
			prefixDef.setStringProperty(IConstants.OSLC_PREFIX_PROP, prefixMapping.getValue());
			prefixDef.setStringProperty(IConstants.OSLC_PREFIXBASE, prefixMapping.getKey());
		}
		RioResource prefixDef = this.createInlinedResource(IConstants.OSLC_PREFIXDEFINITION, IConstants.OSLC_TYPE_PREFIXDEFINITION );
		prefixDef.setStringProperty(IConstants.OSLC_PREFIX_PROP, ICmConstants.OSLC_CM_PREFIX);
		prefixDef.setStringProperty(IConstants.OSLC_PREFIXBASE, ICmConstants.OSLC_CM_NAMESPACE);
		
		RioResource service = this.createInlinedResource(IConstants.OSLC_SERVICE, IConstants.OSLC_TYPE_SERVICE);
		service.setStringProperty(IConstants.OSLC_DOMAIN, ICmConstants.OSLC_CM_NAMESPACE);
		
		String resShapeUrl = urlBase + "/shapes?type=ChangeRequest"; 

		RioResource creationFactory = service.createInlinedResource(IConstants.OSLC_CREATIONFACTORY, IConstants.OSLC_TYPE_CREATIONFACTORY);
		creationFactory.setStringProperty(IConstants.DCTERMS_TITLE, "Change Request Factory");
		creationFactory.setStringProperty(IConstants.OSLC_LABEL, "CR Factory");
		creationFactory.setUriProperty(IConstants.OSLC_CREATION, urlBase + '/' + ICmConstants.SERVICE_CHANGEREQUEST);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCETYPE, ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
		creationFactory.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		RioResource queryCapability = service.createInlinedResource(IConstants.OSLC_QUERYCAPABILITY, IConstants.OSLC_TYPE_QUERYCAPABILITY);
		queryCapability.setStringProperty(IConstants.DCTERMS_TITLE, "Change Request Query");
		queryCapability.setStringProperty(IConstants.OSLC_LABEL, "CR Query");
		queryCapability.setUriProperty(IConstants.OSLC_QUERYBASE, urlBase + '/' + ICmConstants.SERVICE_CHANGEREQUEST);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCETYPE, ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
		queryCapability.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		RioResource dialog = service.createInlinedResource(IConstants.OSLC_SELECTIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "Change Request Selector");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "CR Picker");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, ICmConstants.RIO_CM_SELECTION_RESOURCE_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, ICmConstants.RIO_CM_SELECTION_RESOURCE_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/selector/" + ICmConstants.SERVICE_CHANGEREQUEST);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
		dialog = service.createInlinedResource(IConstants.OSLC_CREATIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "Change Request Creator");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "CR Creator");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, ICmConstants.RIO_CM_SELECTION_RESOURCE_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, ICmConstants.RIO_CM_SELECTION_RESOURCE_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/creator/" + ICmConstants.SERVICE_CHANGEREQUEST);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
		
	}

}
