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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lyo.rio.store.RioValue.RioValueType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.sail.memory.MemoryStore;

public class RdfXmlFormatter {
	
	private Map<String,String> namespacePrefixes = new HashMap<String,String>();
	private SailRepository repository;
	
	public RdfXmlFormatter() throws RioServerException  {
		try{
			this.repository = new SailRepository(new MemoryStore());
			repository.initialize();
		} catch( Exception e ) {
			throw new RioServerException(e);
		}
	}
	
	public class RioPrettyRdfXmlWriter extends RDFXMLPrettyWriter {

		public RioPrettyRdfXmlWriter(OutputStream out, Map<String,String> prefixes) {
			super(out);
			
			Set<String> keys= prefixes.keySet();
			for (String ns : keys) {
				String pr = prefixes.get(ns);
				this.setNamespace(pr, ns, true);
			}

			Map<String, String> storePrefixes = RioStore.getPredefinedNamespaceMappings();
			keys = storePrefixes.keySet();
			for (String ns : keys) {
				String pr = storePrefixes.get(ns);
				this.setNamespace(pr, ns, true);
			}
			if( prefixes != null ) {
			}
		}
	}
	


	/*
	 * Resource
	 */
	static public String formatResource(RioResource resource ) throws RioServerException {
		RdfXmlFormatter formatter = new RdfXmlFormatter();
		return formatter.format(resource);
	}

	public void addNamespacePrefix(String ns, String prefix) {
		this.namespacePrefixes.put(ns, prefix);
	}
	
	public String formatPretty(RioResource resource ) throws RioServerException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		RDFWriter rdfWriter = new RioPrettyRdfXmlWriter(out, this.namespacePrefixes);
		return _format(resource, rdfWriter, out);
	}	
	
	public String format(RioResource resource ) throws RioServerException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		RDFXMLWriter rdfWriter = new RDFXMLWriter(out);
		return _format(resource, rdfWriter, out);
	}	
	
	public String _format(RioResource resource, RDFWriter rdfWriter, ByteArrayOutputStream out ) throws RioServerException {
		String prettyRdfXml = null;
		RepositoryConnection conn = null;
		HashMap<String, Resource> bnMap = new HashMap<String,Resource>();
		// make sure there are some statements with this context
		try {
			conn = repository.getConnection();
			ValueFactory vf = conn.getValueFactory();
			rdfWriter.startRDF();
			List<RioStatement> rioStatements = resource.getStatements();
			
			// first pass get and organize blank nodes
			for (RioStatement rioStatement : rioStatements) {
				String rioSubject = rioStatement.getSubject();
				if( rioStatement.isBNode() ) {
					if( bnMap.get(rioSubject) == null ) {
						bnMap.put(rioSubject, vf.createBNode());
					}
				}
			}
			
			for (RioStatement rioStatement : rioStatements) {
				URI pred = vf.createURI(rioStatement.getPredicate());
				Resource subj = null;
				String rioSubject = rioStatement.getSubject();
				if( rioStatement.isBNode() ) {
					subj = bnMap.get(rioSubject);
				} else {
					subj = vf.createURI(rioSubject);
				}
				Value val = null;
				RioValue rioVal = rioStatement.getObject();
				if( rioVal.getType() == RioValueType.URI ) {
					val = vf.createURI(rioVal.stringValue());
				} else if( rioVal.getType() == RioValueType.BLANK_NODE ) {
					val = bnMap.get(rioVal.toString()); 
				} else if( rioVal.getType() == RioValueType.BOOLEAN ) {
					val = vf.createLiteral(rioVal.booleanValue());
				} else if( rioVal.getType() == RioValueType.CALENDAR ) {
					val = vf.createLiteral(rioVal.xmlGregorianCalendarValue());
				} else if( rioVal.getType() == RioValueType.DECIMAL ) {
					val = vf.createLiteral(rioVal.doubleValue());
				} else if( rioVal.getType() == RioValueType.INTEGER ) {
					val = vf.createLiteral(rioVal.intValue());
				} else {
					val = vf.createLiteral(rioVal.stringValue());
				}
				
				Statement statement = vf.createStatement(subj, pred, val);
				System.out.println( statement );
				rdfWriter.handleStatement(statement);
			}
			rdfWriter.endRDF();
			prettyRdfXml = out.toString("UTF-8");
		} catch (Exception e) {
			throw new RioServerException(e);
		} finally {
			if( conn != null ) {
				try {
					conn.close();
				} catch (RepositoryException e) {
					throw new RioServerException(e);
				}
			}
		}
		return prettyRdfXml;
	}
	

}
