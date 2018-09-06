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
package org.eclipse.lyo.rio.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.query.OslcWhereHelper.OslcWhereParseException;



public class SimpleQueryBuilder {
	
	private String where = null;
	private Map<String,PName> propNames;
	private Map<String,String> selects = new HashMap<String,String>();
	private List<String> searchTerms = new ArrayList<String>();

	public void parseSelect(String selectParameter) {
		
	}
	
	public void parseSearchTerms(String searchTermsParameter) {
		//?uri dcterms:title ?title .
//		  FILTER( regex(?title,"not","i") || regex(?title,"mal","i") ).
		
		
//		StringTokenizer st = new StringTokenizer(searchTermsParameter,",");
//		
//		searchTerms.add(term);
		
	}
	
	public void parsePrefix(String prefixParameter) {
		if( prefixParameter == null ) return; // nothing to do
		StringTokenizer st = new StringTokenizer(prefixParameter,","); //$NON-NLS-1$
		while( st.hasMoreTokens() ) {
			String token = st.nextToken();
			int pos = token.indexOf('=');
			String prefix = token.substring(0,pos);
			String namespace = token.substring(pos+1);
			namespace = stripAngleBrackets(namespace);
			if( prefix != null && prefix.length()> 0 && namespace != null && namespace.length()>0 ) {
				prefixes.put(namespace, prefix);	
			}
		}
		
	}
	
	private String stripAngleBrackets(String exp){
		if( exp.startsWith("<") ) { //$NON-NLS-1$
			exp = exp.substring(1);
		} 
		if( exp.endsWith(">")) { //$NON-NLS-1$
			exp = exp.substring(0,exp.length()-1);
		}
		return exp;
	}
	
	public void parseWhere(String uriVar, String whereExp) throws OslcWhereParseException {
		if( whereExp == null ) return; // nothing to do
		OslcWhereHelper whereConverter = new OslcWhereHelper();
		this.where = whereConverter.convertToWhere(whereExp, uriVar);
	}
	
	public void setWhere(String where) {
		this.where = where;
	}
	
	public String getWhere(){
		return where;
	}
	
	public void appendWhere( String whereExp ) {
		if( this.where == null ) {
			where = whereExp;
		} else {
			where += '\n' + whereExp;
		}
	}
	
	public Map<String,PName> getPropertyNames(){
		return propNames;
	}
	
	public String getQueryString(String resourceType) {
		StringBuilder sb = new StringBuilder();
		initPrefixes(); 
		Set<String> prefixNs = prefixes.keySet();
		for (String ns : prefixNs) {
			sb.append("PREFIX " + prefixes.get(ns) + ": <" + ns + ">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		sb.append("SELECT DISTINCT ?resUri ?uri ?title\n"  ); //$NON-NLS-1$
		sb.append("WHERE {\n" ); //$NON-NLS-1$
		if( where != null ) sb.append(where + '\n');
		if( resourceType != null ) {
			sb.append("?uri rdf:type <" + resourceType + "> .\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("}"); //$NON-NLS-1$
		return sb.toString();
	}
	
	public void addPrefix(String namespace, String prefix ){
		prefixes.put(namespace, prefix);
	}
	
	private Map<String,String> prefixes = new HashMap<String,String>();
	private void initPrefixes() {
		prefixes.put(IConstants.RDF_NAMESPACE, IConstants.RDF_PREFIX);
		prefixes.put(IConstants.DCTERMS_NAMESPACE, IConstants.DCTERMS_PREFIX);
	}

}
