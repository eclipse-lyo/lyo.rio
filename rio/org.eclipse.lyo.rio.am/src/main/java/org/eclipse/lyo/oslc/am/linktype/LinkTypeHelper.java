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

import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.eclipse.lyo.oslc.am.common.IAmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.store.RioValue;
import org.eclipse.lyo.rio.store.RioValue.RioValueType;
import org.eclipse.lyo.rio.store.UnrecognizedValueTypeException;

public class LinkTypeHelper {

	public LinkType getLinkTypeFromAboutUri(RioStore store, String aboutUri) throws RioServerException, RioServiceException  {
		
		// run query that gets the graph(s) (i.e. resource) that has a statement 
		// with the about as a subject and dcterms:title as a predicate (we don't care about the actual value here)  
		try {
			// pull out only the statements appropriate for link types
			List<RioStatement> statements = store.findStatements(aboutUri, IConstants.RDFS_LABEL, null, null);
			// there should only be one, so we can pick the first if any are returned
			if( !statements.isEmpty() ) {
				RioStatement statement = statements.get(0);
				String resource = statement.getContext();
				return getLinkTypeFromResourceUri(store, resource);
			} else {
				throw new RioServiceException(IConstants.SC_BAD, "Invalid Link Type Resource");
			}

		} catch (Exception e) {
			throw new RioServiceException(e);
		}
	}
	
	public LinkType getLinkTypeFromResourceUri(RioStore store, String resUri) throws RioServerException, UnrecognizedValueTypeException, DatatypeConfigurationException  {
		
		OslcResource resource = store.getOslcResource(resUri);
		RioValue value = new RioValue(RioValueType.URI, IAmConstants.OSLC_AM_TYPE_LINKTYPE);
		RioStatement statement = resource.getFirstStatement(null, IConstants.RDF_TYPE, value);
		if( statement == null ) {
			throw new RioServerException("Invalid link type URI resource");
		}
		
		String about = statement.getSubject(); 
		
		statement = resource.getFirstStatement(about, IConstants.RDFS_LABEL, null);
		String label = statement.getObject().stringValue();
		
		statement = resource.getFirstStatement(about, IConstants.RDFS_COMMENTS, null);
		String comments = statement.getObject().stringValue();

		LinkType linkType = new LinkType(resUri, about, label, comments);
		linkType.addStatements(resource.getStatements()); // add other server defined properties like created, modified, ...
		return linkType;

	}
	
	public void remove(RioStore store, LinkType linkType) throws RioServerException  {
		String context = linkType.getUri();
		store.clear(context);
	}
	
	
}
