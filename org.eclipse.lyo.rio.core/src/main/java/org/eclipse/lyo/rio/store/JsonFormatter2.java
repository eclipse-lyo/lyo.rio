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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioValue;
import org.eclipse.lyo.rio.util.StringUtils;

public class JsonFormatter2 {
	
	private RioResource resource = null;
	private Map<String,String> namespacePrefixes = new HashMap<String,String>();
	private IMultiValueResolver multiValueResolver;
	
	public JsonFormatter2( IMultiValueResolver resolver ) {
		this.multiValueResolver = resolver;
	}

	public void addNamespacePrefix(String ns, String prefix) {
		this.namespacePrefixes.put(ns, prefix);
	}
	
	public String format(RioResource resource ) throws RioServerException {
		this.resource = new RioResource(resource.getUri());  // make a copy of the resource, that we can modify
		this.resource.addStatements(resource.getStatements()); 
		initPrefixes();
		
		StringBuffer sb = new StringBuffer();
		sb.append("{\n");
		sb.append("\t\"" + IConstants.RDF_PTERM_ABOUT + "\" : " + quote(resource.getUri()) );
		
		List<RioStatement> types = resource.getStatements(resource.getUri(), IConstants.RDF_TYPE, null);
		appendProperties( sb, IConstants.RDF_TYPE, types );
		sb.append(",\n");
		
		addChildren(sb, resource.getUri(), "\t");

		// now do prefixes
		sb.append(",\n\t\"prefixes\": {\n" );
		// add prefixes
		Set<String> namespaces = this.namespacePrefixes.keySet();
		boolean first = true;
		for (String namespace : namespaces) {
			String prefix = namespacePrefixes.get(namespace);
			if( first ) {
				first = false;
			} else {
				sb.append(",\n" );
			}
			sb.append( "\t\t\"" + encode(prefix) + "\" : \"" + encode(namespace) + "\"");
		}
		sb.append("\n\t}" );
		
		sb.append("\n}");
		return sb.toString();
	}
	
	private void addChildren(StringBuffer sb, String subject, String indent) throws RioServerException{
		boolean first = true;
		Map<String, List<RioStatement>> stMap = getStatementsByProperty(subject);
		Set<String> propSet = stMap.keySet();
		for (String prop : propSet) {
			if( !IConstants.RDF_TYPE.equals(prop) ) {
				RdfTerm pTerm = new RdfTerm(prop);
				if( first ) {
					first = false;
				} else {
					sb.append(",\n" );
				}
				List<RioStatement> props = stMap.get(prop);
				if( props.size() > 1 || this.isMultiValued(prop) ) {
					sb.append( indent + "\t\"" + pTerm + "\" : [ \n" );
					boolean sFirst = true;
					for (RioStatement s : props) {
						if( sFirst ) {
							sFirst = false;
						} else {
							sb.append(",\n" );
						}
						sb.append( indent + "\t\t" + toJson(s, indent) );
					}
					sb.append( "\n" + indent + "\t]" );	
				} else {
					// single valued
					RioStatement st = props.get(0);
					sb.append( indent + "\t\"" + pTerm + "\" : " + toJson(st, indent) );	
				}
			}
		}

	}
	
	private boolean isMultiValued(String property) {
		if( this.multiValueResolver != null ) {
			return this.multiValueResolver.isMultiValued(property);
		}
		return false;
	}

	private Map<String,List<RioStatement>> getStatementsByProperty(String subject) {
		Map<String,List<RioStatement>> statementsForProp = new HashMap<String,List<RioStatement>>();
		List<RioStatement> statements = resource.getStatements(subject, null, null);
		for (RioStatement rioStatement : statements) {
			if( statementsForProp.containsKey(rioStatement.getPredicate()) ) {
				statementsForProp.get(rioStatement.getPredicate()).add(rioStatement);
			} else {
				// init with new list
				ArrayList<RioStatement> list = new ArrayList<RioStatement>();
				list.add(rioStatement);
				statementsForProp.put(rioStatement.getPredicate(), list);
			}
		}
		return statementsForProp;
	}
	
	private String toJson(RioStatement rioStatement, String indent) throws RioServerException  {
		try{
			StringBuffer sb = new StringBuffer();
			RioValue value = rioStatement.getObject();
			switch( value.getType() ) {
			case BOOLEAN: {
				sb.append( rioStatement.getObject().booleanValue() );
				break;
			}
			case INTEGER: {
				sb.append( rioStatement.getObject().intValue() );
				break;
			}
			case DECIMAL: {
				sb.append( rioStatement.getObject().doubleValue() );
				break;
			}
			case CALENDAR: {
				String dStr = StringUtils.xsdDateTime(rioStatement.getObject().dateValue());
				sb.append( quote( dStr)  );
				break;
			}
			case XMLLiteral: 
			case STRING: {
				sb.append( quote( rioStatement.getObject().stringValue()) );
				break;
			}
			case URI: {
				sb.append( " { \"rdf:resource\" : " + quote( rioStatement.getObject().stringValue()) + "}");
				break;
			}
			case BLANK_NODE: {
				// inlined resource
				sb.append(indent + "{\n");
				addChildren(sb, rioStatement.getObject().stringValue(), indent + "\t");
				sb.append("\n" + indent + "}");
//				List<RioStatement> childStatements = this.resource.getStatements(rioStatement.getObject().stringValue(), null, null); 
//				sb.append( "{\n");
//				boolean first = true;
//				for (RioStatement rs : childStatements) {
//					String pred = rs.getPredicate();
//					if( !IConstants.RDF_TYPE.equals(pred) ) {
//						RdfTerm pTerm = new RdfTerm(pred);
//						if( first ) {
//							first = false;
//						} else {
//							sb.append(",\n" );
//						}
//						sb.append( indent + "\t\t\"" + pTerm + "\" : " + toJson(rs, indent+"\t" ) );
//					}
//				}
//				sb.append( "\n\t" + indent + " }");
				break;
			}
			}
			return sb.toString();
		} catch( Exception e ) {
			throw new RioServerException(e);
		}
	}
	
	private void appendProperties(StringBuffer sb, String property, List<RioStatement> statements) throws RioServerException {
		RdfTerm term = new RdfTerm( property );
		sb.append(",\n\t\"" + term.prefixedTerm() + "\" : [\n" );
		boolean first = true;
		try{
			for (RioStatement rioStatement : statements) {
				if( first ) {
					first = false;
				} else {
					sb.append(",\n");
				}
				sb.append("\t" + this.toJson(rioStatement, "") );
			}
		} catch( Exception e ) {
			throw new RioServerException(e);
		}
		sb.append("\n\t]" );
		
	}

//	private void appendProperty(StringBuffer sb, String property, RioStatement statement) throws RioServerException  {
//		RdfTerm term = new RdfTerm( property );
//		try{
//			sb.append(",\n\t\"" + term.prefixedTerm() + "\" : \"" + this.asJsonValue(statement) + "\"" );
//		} catch( Exception e ) {
//			throw new RioServerException(e);
//		}
//	}

	private String quote(String str) {
		return "\"" + encode(str) + "\"";
	}
	private String encode(String str) {
		if( str == null || str.isEmpty() ) {
			return "\"\"";
		}
		StringBuffer sb = new StringBuffer();
		int len = str.length();
		for(int i=0;i<len;i++) {
			char c = str.charAt(i);
			switch (c) {
            case '\\':
            	sb.append("\\\\");
            	break;
            case '"':
                sb.append("\\\"");
                break;
//            case '/':
//                sb.append("\\/");
//                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            default:
            	if( c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100') ) {
            		String hexStr = Integer.toHexString(c);
            		int hlen = hexStr.length();
            		while( hlen < 4 ) {
            			hexStr = '0' + hexStr;
            			hlen = hexStr.length();
            		}
            		sb.append("\\u" + hexStr );
            	} else {
            		sb.append(c);
            	}
			}
		}
		return sb.toString();
	}

	private class RdfTerm {
		public String namespace;
		public String term;
		public String prefix;
		public RdfTerm( String uri ) {
			int pos = uri.lastIndexOf('#');
			if( pos < 0 ) {
				pos = uri.lastIndexOf('/');
			}
			this.namespace = uri.substring(0,pos+1);
			this.term = uri.substring(pos+1);
			this.prefix = getPrefix(namespace);
			
		}
		public String prefixedTerm(){
			return prefix + ':' + encode(term);
		}
		public String toString(){
			return prefixedTerm();
		}
		
	}

	private String getPrefix(String namespace) {
		String prefix = namespacePrefixes.get(namespace);
		if( prefix == null ) {
			// then find the next available prefix
			int i=0;
			String pre = "pr" + i; //$NON-NLS-1$
			Collection<String> prefixes = namespacePrefixes.values();
			while( prefixes.contains(pre) ) {
				i++;
				pre = "pr" + i; //$NON-NLS-1$
			}
			namespacePrefixes.put(namespace, pre);
			prefix = pre;
		}
		return prefix;
	}
	
	private void initPrefixes() {
		namespacePrefixes.put(IConstants.RDF_NAMESPACE, IConstants.RDF_PREFIX);
		namespacePrefixes.put(IConstants.OSLC_NAMESPACE, IConstants.OSLC_PREFIX);
		namespacePrefixes.put(IConstants.DCTERMS_NAMESPACE, IConstants.DCTERMS_PREFIX);
		namespacePrefixes.put(IConstants.RIO_NAMESPACE, IConstants.RIO_PREFIX);
	}

	public interface IMultiValueResolver {
		public boolean isMultiValued(String property);
	}

}

