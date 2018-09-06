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
package org.eclipse.lyo.rio.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.RioValue.RioValueType;

public class RioResource {
	
	protected String uri;
	protected String resourceContext;
	protected List<RioStatement> statements = new ArrayList<RioStatement>();
	protected Map<String,RioResource> inlinedResources = new HashMap<String,RioResource>();
	protected RioResource parentResource;
	
	public RioResource(String uri) throws RioServerException {
		this.uri = uri;
		this.resourceContext = uri;
	}

	public RioResource(String uri, String resourceContext) throws RioServerException {
		this.uri = uri;
		this.resourceContext = resourceContext;
	}

	public RioResource(String uri, List<RioStatement> statements) {
		this.uri = uri;
		this.resourceContext = uri;
		this.addStatements(statements);
	}
	
	public RioResource(String uri, String resourceContext, List<RioStatement> statements) {
		this.uri = uri;
		this.resourceContext = resourceContext;
		this.addStatements(statements);
	}
	
	public String getUri(){
		return uri;
	}
	
	/**
	 *  
	 * @param uri
	 * @throws RioServerException
	 */
	public void setUri(String uri) throws RioServerException {
		// need to re-index everything
		if( uri == null || uri.isEmpty() ) throw new RioServerException("Resource URI must be valid or blank node");
		
		try{
			if( this.parentResource != null ) {
				// need to update dad
				this.parentResource.inlinedResources.remove(this.uri);
				this.parentResource.inlinedResources.put(uri, this);
			} else {
				// this is also a resource context change 
				this.resourceContext = uri;
				List<RioStatement> allStatements = this.getStatements();
				for (RioStatement rioStatement : allStatements) {
					rioStatement.setContext(uri);
				}
			}
			
			// update immediate properties
			for( RioStatement statement : statements ) {
				statement.setSubject(uri);
				RioValue obj = statement.getObject();
				if(  ( obj.getType() == RioValueType.BLANK_NODE || obj.getType() == RioValueType.URI) && 
						uri.equals(obj.stringValue()) ) {
					RioValue updatedObj = null;
					if( uri.startsWith("http://") || uri.startsWith("https://") ) {
						updatedObj = new RioValue(RioValueType.URI, uri);
					} else {
						updatedObj = new RioValue(RioValueType.BLANK_NODE, uri);
					}
					statement.setObject(updatedObj);
				}
			}
			// now recurse into inlined resources, and update objects
			for(RioResource rioResource : this.inlinedResources.values() ) {
				rioResource.reindexProperties(this.uri, uri);
			}
			
			
			// finally update this uri
			this.uri = uri;
			
		}catch( UnrecognizedValueTypeException e) {
			throw new RioServerException(e);
		}
		
	}

	private void reindexProperties(String oldUri, String updatedUri) throws RioServerException {
		try{
			
			for( RioStatement statement : statements ) {
				RioValue obj = statement.getObject();
				if( ( obj.getType() == RioValueType.BLANK_NODE || obj.getType() == RioValueType.URI) 
						&& oldUri.equals(obj.stringValue()) ) {
					RioValue updatedObj = null;
					if( updatedUri.startsWith("http://") || updatedUri.startsWith("https://") ) {
						updatedObj = new RioValue(RioValueType.URI, updatedUri);
					} else {
						updatedObj = new RioValue(RioValueType.BLANK_NODE, updatedUri);
					}
					statement.setObject(updatedObj);
				}
			}
			
			for(RioResource resource : this.inlinedResources.values() ) {
				resource.reindexProperties(oldUri, updatedUri);
			}
			
		}catch( UnrecognizedValueTypeException e) {
			throw new RioServerException(e);
		}

	}
	
	public String getResourceContext(){
		return resourceContext;
	}
	
	public void setResourceContext(String resourceContext){
		// update all statements!
	}
	
	public void addStatements(List<RioStatement> statements ) {
		this.statements.addAll(statements);
	}

	public void addStatement(RioStatement rioStatement) {
		this.statements.add(rioStatement);
	}

	public List<RioStatement> getStatements(){
		List<RioStatement> allStatements = new ArrayList<RioStatement>(this.statements);
		for (RioResource inlinedResource : this.inlinedResources.values()) {
			allStatements.addAll( inlinedResource.getStatements());
		}
		return Collections.unmodifiableList(allStatements);
	}
	
	public List<RioStatement> getStatements(String subject, String predicate, RioValue value) {
		ArrayList<RioStatement> filteredStatements = new ArrayList<RioStatement>();
		for (RioStatement rioStatement : this.getStatements()) { // be sure to use all child statements too
			if( subject != null && !subject.equals(rioStatement.getSubject() ) ) continue;
			if( predicate != null && !predicate.equals(rioStatement.getPredicate() ) ) continue;
			if( value != null ) {
				if( value.getType() == rioStatement.getObject().getType() ) {
					if( !value.toString().equals(rioStatement.getObject().toString()) ) continue;
				} else {
					continue;
				}
			}
			filteredStatements.add(rioStatement);
		}
		
		return filteredStatements;
		
	}

	public RioStatement getFirstStatement(String subject, String predicate, RioValue value) {
		for (RioStatement rioStatement : statements) {
			if( subject != null && !subject.equals(rioStatement.getSubject() ) ) continue;
			if( predicate != null && !predicate.equals(rioStatement.getPredicate() ) ) continue;
			if( value != null ) {
				if( value.getType() == rioStatement.getObject().getType() ) {
					if( !value.toString().equals(rioStatement.getObject().toString()) ) continue;
				} else {
					continue;
				}
			}
			return rioStatement;
		}
		return null;
	}
	
	public RioValue getFirstPropertyValue(String predicateUri) {
		RioStatement statement = getFirstStatement(uri, predicateUri, null);
		if( statement != null ) {
			return statement.getObject();
		}
		return null;
	}


	public void addRdfType(String rdfTypeUri) throws RioServerException {
		addProperty(IConstants.RDF_TYPE, RioValueType.URI, rdfTypeUri, false);
	}
	
	public boolean isRdfType(String rdfTypeUri) throws RioServerException {
		List<RioStatement> rdftypes = this.getStatements(uri, IConstants.RDF_TYPE, null);
		for (RioStatement rioStatement : rdftypes) {
			RioValue obj = rioStatement.getObject();
			if( rdfTypeUri.equals(obj.stringValue())) return true;
		}
		return false;
	}

	private void insertInlinedResource( String propertyUri, RioResource rioResource ) throws RioServerException {
		try{
			String bn = null;
			if( this.uri.startsWith("_:") ) { 
				bn = uri + '_' + inlinedResources.size();
			} else {
				bn = "_:bn" +  + inlinedResources.size();
			}
			RioValue blankNode = RioValue.createBlankNodeValue(bn);
			RioStatement statement = new RioStatement(uri, propertyUri, blankNode, uri);
			statements.add(statement);
			rioResource.setUri(bn);
			rioResource.setResourceContext(this.resourceContext);
			inlinedResources.put(blankNode.stringValue(),rioResource);
			rioResource.parentResource = this;
		} catch ( Exception e ) {
			throw new RioServerException(e);
		}
	}

	
	
	public RioResource createInlinedResource( String propertyUri, String typeUri ) throws RioServerException {
		try{
			String bn = null;
			if( this.uri.startsWith("_:") ) { 
				bn = uri + '_' + inlinedResources.size();
			} else {
				bn = "_:bn" +  + inlinedResources.size();
			}
			RioValue blankNode = RioValue.createBlankNodeValue(bn);
			RioStatement statement = new RioStatement(uri, propertyUri, blankNode, uri);
			statements.add(statement);
			RioResource resource = new RioResource(blankNode.stringValue(), uri);
			inlinedResources.put(blankNode.stringValue(),resource);
			if( typeUri != null ) {
				RioValue val = RioValue.createUriValue(typeUri);
				resource.createProperty(IConstants.RDF_TYPE, val, true);
			}
			resource.parentResource = this;
			return resource;
		} catch ( Exception e ) {
			throw new RioServerException(e);
		}
	}

	public RioResource getFirstInlinedResource( String propertyUri ) throws RioServerException {
		RioStatement statement = getFirstStatement(uri, propertyUri, null);
		if( statement != null ) {
			RioValue obj = statement.getObject();
			if( obj.getType() == RioValueType.BLANK_NODE ) {
				return getInlinedResource(obj.toString());
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getInlinedResources( String propertyUri ) throws RioServerException {
		List<T> inlinedResources = new ArrayList<T>();
		List<RioStatement> inlinedStatements = getStatements(uri, propertyUri, null);
		for (RioStatement rioStatement : inlinedStatements) {
			RioValue obj = rioStatement.getObject();
			inlinedResources.add((T) getInlinedResource(obj.toString()));
		}
		return inlinedResources;
	}

	
	public RioResource getInlinedResource( String bnodeID ) throws RioServerException {
		return this.inlinedResources.get(bnodeID);
	}
	
	public void removeInlinedResource(String bnodeId) throws RioServerException {
		this.inlinedResources.remove(bnodeId);
	} 
	
	
	/**
	 * change owner of the resource to this one 
	 * @param propertyUri
	 * @param rioResource
	 * @param typeUri
	 * @throws RioServerException 
	 */
	public void setInlinedResource(String propertyUri, RioResource rioResource) throws RioServerException {
		RioResource oldResource = this.getFirstInlinedResource(propertyUri);
		if(oldResource != null ) {
			// remove it
			String bnode = oldResource.getUri();
			this.removeInlinedResource(bnode);
		} 
		insertInlinedResource(propertyUri, rioResource);
	}

	// String
	
	public void setStringProperty( String propertyUri, String value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createStringValue(value);
			this.createProperty(propertyUri, lyoVal, true);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}
	
	public void addStringProperty( String propertyUri, String value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createStringValue(value);
			this.createProperty(propertyUri, lyoVal, false);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public boolean hasStringProperty(String propertyUri ) {
		return hasPropertyOfType(propertyUri, RioValueType.STRING);
	}
	
	public String getFirstStringProperty(String propertyUri ) {
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.STRING)) {
				return val.stringValue();
			}
		}
		return null;
	}
	
	public Collection<String> getStringProperties(String propertyUri ) {
		List<String> propertyValues = new ArrayList<String>(); 
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.STRING)) {
				propertyValues.add(val.stringValue());
			}
		}
		return propertyValues;
	}

	// Integer
	
	public void setIntegerProperty( String propertyUri, int value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createIntegerValue(value);
			this.createProperty(propertyUri, lyoVal, true);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public void addIntegerProperty( String propertyUri, int value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createIntegerValue(value);
			this.createProperty(propertyUri, lyoVal, false);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}
	
	public boolean hasIntegerProperty(String propertyUri ) {
		return hasPropertyOfType(propertyUri, RioValueType.INTEGER);
	}
	
	public Integer getFirstIntegerProperty(String propertyUri ) throws RioServerException {
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.INTEGER)) {
				try {
					return val.intValue();
				} catch (IncompatibleValueException e) {
					throw new RioServerException(e);
				}
			}
		}
		return null;
	}
	
	public Collection<Integer> getIntegerProperties(String propertyUri ) {
		List<Integer> propertyValues = new ArrayList<Integer>(); 
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.INTEGER)) {
				try {
					propertyValues.add(val.intValue());
				} catch (IncompatibleValueException e) {
					// log this?  don't abort since the client probably interested only in the ints that are working
				}
			}
		}
		return propertyValues;
	}
	
	// Decimal
	
	public void setDecimalProperty( String propertyUri, double value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createDecimalValue(value);
			this.createProperty(propertyUri, lyoVal, true);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public void addDecimalProperty( String propertyUri, double value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createDecimalValue(value);
			this.createProperty(propertyUri, lyoVal, false);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public boolean hasDecimalProperty(String propertyUri ) {
		return hasPropertyOfType(propertyUri, RioValueType.DECIMAL);
	}
	
	public Double getFirstDecimalProperty(String propertyUri ) throws RioServerException {
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.DECIMAL)) {
				try {
					return val.doubleValue();
				} catch (IncompatibleValueException e) {
					throw new RioServerException(e);
				}
			}
		}
		return null;
	}
	
	public Collection<Double> getDecimalProperties(String propertyUri ) {
		List<Double> propertyValues = new ArrayList<Double>(); 
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.DECIMAL)) {
				try {
					propertyValues.add(val.doubleValue());
				} catch (IncompatibleValueException e) {
					// log this?  don't abort since the client probably interested only in the ints that are working
				}
			}
		}
		return propertyValues;
	}

	// Date
	
	public void setDateProperty( String propertyUri, Date value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createCalendarValue(value);
			this.createProperty(propertyUri, lyoVal, true);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}
	
	public void addDateProperty( String propertyUri, Date value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createCalendarValue(value);
			this.createProperty(propertyUri, lyoVal, false);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public boolean hasDateProperty(String propertyUri ) {
		return hasPropertyOfType(propertyUri, RioValueType.CALENDAR);
	}
	
	public Date getFirstDateProperty(String propertyUri ) throws RioServerException {
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.CALENDAR)) {
				try {
					return val.dateValue();
				} catch (IncompatibleValueException e) {
					throw new RioServerException(e);
				}
			}
		}
		return null;
	}
	
	public Collection<Date> getDateProperties(String propertyUri ) {
		List<Date> propertyValues = new ArrayList<Date>(); 
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.CALENDAR)) {
				try {
					propertyValues.add(val.dateValue());
				} catch (IncompatibleValueException e) {
					// log this?  don't abort since the client probably interested only in the ints that are working
				}
			}
		}
		return propertyValues;
	}

	// URI 
	
	public void setUriProperty( String propertyUri, String value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createUriValue(value);
			this.createProperty(propertyUri, lyoVal, true);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public void addUriProperty( String propertyUri, String value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createUriValue(value);
			this.createProperty(propertyUri, lyoVal, false);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public void removeUriProperty( String propertyUri, String uri) throws RioServerException {
		try {
			RioValue value = RioValue.createUriValue(uri);
			RioStatement stmt = this.getFirstStatement(getUri(), propertyUri, value);
			if( stmt != null ) {
				statements.remove(stmt);
			}
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public boolean hasUriProperty(String propertyUri ) {
		return hasPropertyOfType(propertyUri, RioValueType.URI);
	}
	
	public String getFirstUriProperty(String propertyUri ) {
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.URI)) {
				return val.stringValue();
			}
		}
		return null;
	}
	
	public List<String> getUriProperties(String propertyUri ) {
		List<String> propertyValues = new ArrayList<String>(); 
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.URI)) {
				propertyValues.add(val.stringValue());
			}
		}
		return propertyValues;
	}

	// Boolean
	
	public void setBooleanProperty( String propertyUri, boolean value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createBooleanValue(value);
			this.createProperty(propertyUri, lyoVal, true);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public void addBooleanProperty( String propertyUri, boolean value) throws RioServerException {
		try {
			RioValue lyoVal = RioValue.createBooleanValue(value);
			this.createProperty(propertyUri, lyoVal, false);
		} catch (Exception e) {
			throw new RioServerException(e);
		}
	}

	public boolean hasBooleanProperty(String propertyUri ) {
		return hasPropertyOfType(propertyUri, RioValueType.BOOLEAN);
	}
	
	public boolean getFirstBooleanProperty(String propertyUri, boolean defaultValue ) {
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.BOOLEAN)) {
				try {
					return val.booleanValue();
				} catch (IncompatibleValueException e) {
					// log this?  it should never happen
				}
			}
		}
		return defaultValue;
	}
	
	public Collection<Boolean> getBooleanProperties(String propertyUri ) {
		List<Boolean> propertyValues = new ArrayList<Boolean>(); 
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(RioValueType.BOOLEAN)) {
				try {
					propertyValues.add(val.booleanValue());
				} catch (IncompatibleValueException e) {
					// log this?  it should never happen
				}
			}
		}
		return propertyValues;
	}


	// Common property methods
	
	protected void addProperty(String propertyUri, RioValueType valType, Object value, boolean exclusive) 
			throws RioServerException {
		createProperty(this.uri, propertyUri, valType, value, exclusive);
	}

	protected void createProperty(String subjectUri, String propertyUri, RioValueType valType, Object value, boolean exclusive)
			throws RioServerException {
		RioValue rioValue = null;
		try {
			rioValue = new RioValue(valType, value);
		} catch (UnrecognizedValueTypeException e) {
			// should NEVER happen
			throw new RioServerException(e);
		}
		createProperty(subjectUri, propertyUri, rioValue, exclusive);
	}

	protected void createProperty(String propertyUri, RioValue value, boolean exclusive) throws RioServerException {
		createProperty(this.uri, propertyUri, value, exclusive);
	}

	protected void createProperty(String subjectUri, String propertyUri, RioValue value, boolean exclusive) throws RioServerException {
		List<RioStatement> statements = this.getStatements(subjectUri, propertyUri, null);
		if ((exclusive || value == null)) {
			for (RioStatement rioStatement : statements) {
				this.statements.remove(rioStatement);
			}
		}

		if (value == null || value.getValue() == null)
			return; // our work is finished

		RioStatement statement = new RioStatement(subjectUri, propertyUri, value, resourceContext);
		this.statements.add(statement);
	}
	
	public boolean hasPropertyOfType( String propertyUri, RioValueType type) {
		List<RioStatement> strStatements = this.getStatements(this.uri, propertyUri, null);
		for (RioStatement rioStatement : strStatements) {
			RioValue val = rioStatement.getObject();
			if( val.getType().equals(type)) {
				return true;
			}
		}
		return false;
	}
	

	
//
//	 Ordered list methods (rdf sequences)
//	
	
	public void appendToSeq(String propertyUri, RioValue value) throws RioServerException {
		if (value == null)
			return; // our work is finished
		String propName = extractLastSegment(propertyUri);
		RioStatement statement = null;
		List<RioStatement> statements = this.getStatements(null, propertyUri,
				null);
		if (statements.size() == 0) {
			// create it
			try {
				RioValue bnVal = new RioValue(RioValueType.BLANK_NODE, propName);
				statement = new RioStatement(uri, propertyUri, bnVal, resourceContext);
				this.addStatement(statement);
				RioValue rdfSeqVal = new RioValue(RioValueType.URI,
						IConstants.RDF_SEQ);
				RioStatement collTypeStatement = new RioStatement(propName,
						IConstants.RDF_TYPE, rdfSeqVal, resourceContext);
				this.addStatement(collTypeStatement);
			} catch (Exception e) {
				throw new RioServerException(e);
			}
		} else {
			statement = statements.get(0);
		}

		// now that we have the blank node
		String bNodeId = statement.getObject().stringValue();
		// get all other elements in this collection
		statements = this.getStatements(bNodeId, null, null);
		int count = statements.size() - 1; // -1 to subtract the rdf:type from the list

		statement = new RioStatement(bNodeId, IConstants.RDF_NAMESPACE + "_" + (count + 1), value, uri);
		this.statements.add(statement);
	}

	public List<RioValue> getSeq(String propertyUri) throws RioServerException {
		List<RioValue> seqValues = new ArrayList<RioValue>();
		String propName = extractLastSegment(propertyUri);
		List<RioStatement> statements = this
				.getStatements(propName, null, null);

		// TODO: sort them by predicate
		for (RioStatement rioStatement : statements) {
			RioValue obj = rioStatement.getObject();
			String pred = rioStatement.getPredicate();
			if (pred.startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#_")) { 
				seqValues.add(obj);
			}
		}
		return seqValues;
	}

	public void removeSeq(String propertyUri) throws RioServerException {
		List<RioStatement> statements = this.getStatements(null, propertyUri,
				null);
		for (RioStatement rioStatement : statements) {
			List<RioStatement> childStatements = this.getStatements(
					rioStatement.getObject().stringValue(), null, null);
			this.statements.removeAll(childStatements);
		}
		this.statements.removeAll(statements);
	}

	protected String extractLastSegment(String uri) {
		int pos = uri.lastIndexOf('#');
		if( pos > 0 ) {
			return uri.substring(pos + 1);
		}
		pos = uri.lastIndexOf('/');
		return uri.substring(pos + 1);
	}
	
	public String dumpNTriples(){
		StringBuffer sb = new StringBuffer();
		List<RioStatement> allStatements = this.getStatements();
		for (RioStatement rioStatement : allStatements) {
			sb.append( rioStatement.toString() + " .\n");
		}
		
		return sb.toString();
	}

}
