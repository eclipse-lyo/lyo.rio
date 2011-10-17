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

import java.util.List;


import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.eclipse.lyo.rio.query.OslcWhereLexer;
import org.eclipse.lyo.rio.query.OslcWhereParser;
import org.eclipse.lyo.rio.query.OslcWhereParser.oslc_where_return;


public class OslcWhereHelper {
	
	private int valueCounter = 0;
	private int wildcardCounter = 0;
	private int compoundCounter = 0;
	
	public String convertToWhere( String oslcWhere, String uriVar ) throws OslcWhereParseException {
		ANTLRStringStream input = new ANTLRStringStream(oslcWhere);
		OslcWhereLexer lexer = new OslcWhereLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		OslcWhereParser parser = new OslcWhereParser(tokens);
		try{
			oslc_where_return whereRet = parser.oslc_where();
			if( parser.getErrors().size()>0 ) {
				StringBuilder sb = new StringBuilder();
				for( String errorMsg : parser.getErrors() ) {
					sb.append( errorMsg + '\n' );
				}
				throw new OslcWhereParseException( oslcWhere + '\n' + sb.toString());
			}
			String subject = '?' + uriVar;
			String sparqlWhere = convertToWhere(whereRet.tree, subject);
			return sparqlWhere;
		} catch (RecognitionException re ) {
			throw new OslcWhereParseException(re.getMessage());
		} 
	}

	@SuppressWarnings("unchecked")
	private String convertToWhere(CommonTree tree, String subject) throws OslcWhereParseException {
		StringBuffer sb = new StringBuffer();
		List<CommonTree> children = (List<CommonTree>) tree.getChildren();
		for (CommonTree child : children) {
			int type = child.getType();
			switch (type) {
			case OslcWhereParser.PNAME_LN :
			case OslcWhereParser.ASTERISK :
				// just a single term, can skip the loop
				return convertSimpleTerm(tree, subject);
			case OslcWhereParser.SIMPLE_TERM :
				sb.append( convertSimpleTerm(child, subject) + '\n' );
				break;
			case OslcWhereParser.COMPOUND_TERM :
				sb.append( convertCompoundTerm(child, subject) + '\n');
				break;
			case OslcWhereParser.IN_TERM :
				sb.append( convertInTerm(child, subject) + '\n' );
				break;
			}
		}
		return sb.toString().trim();
	}
	
	private String convertCompoundTerm(CommonTree term, String subject) throws OslcWhereParseException {
		int count = term.getChildCount();
		if( count != 2 ) throw new OslcWhereParseException("OslcWhere2SparqlWhere_IncorrectNumberOfPartsForCompoundTerm" + count);
		Tree trParentProperty = term.getChild(0);
		CommonTree trTerms = (CommonTree) term.getChild(1);
		String compoundNode = getCompoundBlankNode();
		
		return subject + ' ' + trParentProperty.toString() + ' ' + compoundNode + ".\n" +  //$NON-NLS-1$
			convertToWhere( trTerms, compoundNode );
		
	}
	
	private String convertValue(Tree trValue) throws OslcWhereParseException {
		int valType = trValue.getType();
		if( valType == OslcWhereParser.TYPED_VALUE ) {
			return trValue.getChild(0).toString() + "^^" + trValue.getChild(1).toString(); //$NON-NLS-1$
		} else if( valType == OslcWhereParser.LANGED_VALUE ) {
			// don't include the langtag, sesame sparql doesn't accept it.
			return trValue.getChild(0).toString(); 
		}
		return trValue.toString();
	}

	private String convertSimpleTerm(CommonTree term, String subject) throws OslcWhereParseException {
		int count = term.getChildCount();
		if( count != 3 ) throw new OslcWhereParseException("OslcWhere2SparqlWhere_IncorrectNumberOfPartsForSimpleTerm" + count);
		Tree trProperty = term.getChild(0);
		String prop = null;
		if( trProperty.getType() == OslcWhereParser.ASTERISK ) {
			prop = getNextWildcardBlankNode();
		} else {
			prop = trProperty.toString();
		}
		
		Tree trValue = term.getChild(2);
		
		Tree trOperand = term.getChild(1);
		if( trOperand.getType() == OslcWhereParser.EQUAL ) {
			return subject + ' ' + prop + ' ' + convertValue(trValue) + '.';
		}

		// use blank node and FILTER
		String node = getNextValueBlankNode();
		String exp = subject + ' ' + prop + ' ' + node + ".\n" + //$NON-NLS-1$
			"FILTER(" + node + ' ' + trOperand.toString() + ' ' + convertValue(trValue) + ")."; //$NON-NLS-1$ //$NON-NLS-2$
		
		return exp;
	}

	private String convertInTerm(CommonTree term, String subject) throws OslcWhereParseException {
		return ""; //$NON-NLS-1$
	}
	
	
	private String getCompoundBlankNode(){
		return "?_c" + compoundCounter++; //$NON-NLS-1$
	}
	
	private String getNextValueBlankNode(){
		return "?_v" + valueCounter++; //$NON-NLS-1$
	}

	private String getNextWildcardBlankNode(){
		return "?_w" + wildcardCounter++; //$NON-NLS-1$
	}

	public static class OslcWhereParseException extends Exception {
		private static final long serialVersionUID = -3949536173714561942L;
		public OslcWhereParseException(String msg) {
			super(msg);
		}
	}
	
	
	
}
