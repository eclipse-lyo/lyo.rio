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

import org.eclipse.lyo.oslc.am.common.IAmConstants;
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
		this.addUriProperty(IConstants.OSLC_DETAILS, urlBase + "/about.jsp");
		this.setStringProperty(IConstants.DCTERMS_TITLE, "RIO AM Services");
		this.setStringProperty(IConstants.DCTERMS_DESCRIPTION, "RIO AM Services" );
		
		RioResource publisher = this.createInlinedResource(IConstants.DCTERMS_PUBLISHER, IConstants.OSLC_TYPE_PUBLISHER);
		publisher.setStringProperty(IConstants.DCTERMS_TITLE, IAmConstants.RIO_AM_PUBLISHER_TITLE);
		publisher.setStringProperty(IConstants.DCTERMS_IDENTIFIER, IAmConstants.RIO_AM_PUBLISHER_IDENTIFIER);
		
		Map<String, String> predefinedMappings = RioStore.getPredefinedNamespaceMappings();
		Set<Entry<String, String>> entries = predefinedMappings.entrySet();
		for (Entry<String, String> prefixMapping : entries) {
			RioResource prefixDef = this.createInlinedResource(IConstants.OSLC_PREFIXDEFINITION, IConstants.OSLC_TYPE_PREFIXDEFINITION );
			prefixDef.setStringProperty(IConstants.OSLC_PREFIX_PROP, prefixMapping.getValue());
			prefixDef.setStringProperty(IConstants.OSLC_PREFIXBASE, prefixMapping.getKey());
		}
		RioResource prefixDef = this.createInlinedResource(IConstants.OSLC_PREFIXDEFINITION, IConstants.OSLC_TYPE_PREFIXDEFINITION );
		prefixDef.setStringProperty(IConstants.OSLC_PREFIX_PROP, IAmConstants.OSLC_AM_PREFIX);
		prefixDef.setStringProperty(IConstants.OSLC_PREFIXBASE, IAmConstants.OSLC_AM_NAMESPACE);
		
		// AM services
		
		RioResource service = this.createInlinedResource(IConstants.OSLC_SERVICE, IConstants.OSLC_TYPE_SERVICE);
		service.setStringProperty(IConstants.OSLC_DOMAIN, IAmConstants.OSLC_AM_NAMESPACE);
		
		// AM Resource
		
		String resShapeUrl = urlBase + "/shapes?type=Resource"; 

		RioResource creationFactory = service.createInlinedResource(IConstants.OSLC_CREATIONFACTORY, IConstants.OSLC_TYPE_CREATIONFACTORY);
		creationFactory.setStringProperty(IConstants.DCTERMS_TITLE, "AM Resource Factory");
		creationFactory.setStringProperty(IConstants.OSLC_LABEL, "AMR Factory");
		creationFactory.setUriProperty(IConstants.OSLC_CREATION, urlBase + '/' + IAmConstants.SERVICE_RESOURCE);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCETYPE, IAmConstants.OSLC_AM_TYPE_RESOURCE);
		creationFactory.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		RioResource queryCapability = service.createInlinedResource(IConstants.OSLC_QUERYCAPABILITY, IConstants.OSLC_TYPE_QUERYCAPABILITY);
		queryCapability.setStringProperty(IConstants.DCTERMS_TITLE, "AM Resource Query");
		queryCapability.setStringProperty(IConstants.OSLC_LABEL, "AMR Query");
		queryCapability.setUriProperty(IConstants.OSLC_QUERYBASE, urlBase + '/' + IAmConstants.SERVICE_RESOURCE);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCETYPE, IAmConstants.OSLC_AM_TYPE_RESOURCE);
		queryCapability.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		RioResource dialog = service.createInlinedResource(IConstants.OSLC_SELECTIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "AM Resource Selector");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "AMR Picker");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, IAmConstants.RIO_AM_SELECTION_RESOURCE_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, IAmConstants.RIO_AM_SELECTION_RESOURCE_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/selector/" + IAmConstants.SERVICE_RESOURCE);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, IAmConstants.OSLC_AM_TYPE_RESOURCE);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
		dialog = service.createInlinedResource(IConstants.OSLC_CREATIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "AM Resource Creator");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "AMR Creator");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, IAmConstants.RIO_AM_SELECTION_RESOURCE_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, IAmConstants.RIO_AM_SELECTION_RESOURCE_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/creator/" + IAmConstants.SERVICE_RESOURCE);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, IAmConstants.OSLC_AM_TYPE_RESOURCE);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
		// AM LinkType

		resShapeUrl = urlBase + "/shapes?type=LinkType"; 

		creationFactory = service.createInlinedResource(IConstants.OSLC_CREATIONFACTORY, IConstants.OSLC_TYPE_CREATIONFACTORY);
		creationFactory.setStringProperty(IConstants.DCTERMS_TITLE, "AM LinkType Factory");
		creationFactory.setStringProperty(IConstants.OSLC_LABEL, "LTR Factory");
		creationFactory.setUriProperty(IConstants.OSLC_CREATION, urlBase + '/' + IAmConstants.SERVICE_LINKTYPE);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCETYPE, IAmConstants.OSLC_AM_TYPE_LINKTYPE);
		creationFactory.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		creationFactory.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		queryCapability = service.createInlinedResource(IConstants.OSLC_QUERYCAPABILITY, IConstants.OSLC_TYPE_QUERYCAPABILITY);
		queryCapability.setStringProperty(IConstants.DCTERMS_TITLE, "AM Resource Query");
		queryCapability.setStringProperty(IConstants.OSLC_LABEL, "AMR Query");
		queryCapability.setUriProperty(IConstants.OSLC_QUERYBASE, urlBase + '/' + IAmConstants.SERVICE_LINKTYPE);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCETYPE, IAmConstants.OSLC_AM_TYPE_LINKTYPE);
		queryCapability.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		queryCapability.setUriProperty(IConstants.OSLC_RESOURCESHAPE, resShapeUrl);
		
		dialog = service.createInlinedResource(IConstants.OSLC_SELECTIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "AM Resource Selector");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "AMR Picker");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, IAmConstants.RIO_AM_SELECTION_RESOURCE_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, IAmConstants.RIO_AM_SELECTION_RESOURCE_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/selector/" + IAmConstants.SERVICE_LINKTYPE);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, IAmConstants.OSLC_AM_TYPE_LINKTYPE);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
		dialog = service.createInlinedResource(IConstants.OSLC_CREATIONDIALOG, IConstants.OSLC_TYPE_DIALOG);
		dialog.setStringProperty(IConstants.DCTERMS_TITLE, "AM Resource Creator");
		dialog.setStringProperty(IConstants.OSLC_LABEL, "AMR Creator");
		dialog.setStringProperty(IConstants.OSLC_HINTHEIGHT, IAmConstants.RIO_AM_SELECTION_RESOURCE_HEIGHT);
		dialog.setStringProperty(IConstants.OSLC_HINTWIDTH, IAmConstants.RIO_AM_SELECTION_RESOURCE_WIDTH);
		dialog.setUriProperty(IConstants.OSLC_DIALOG, urlBase + "/creator/" + IAmConstants.SERVICE_LINKTYPE);
		dialog.setUriProperty(IConstants.OSLC_RESOURCETYPE, IAmConstants.OSLC_AM_TYPE_LINKTYPE);
		dialog.setUriProperty(IConstants.OSLC_USAGE, IConstants.OSLC_DEFAULT);
		
	}

}
