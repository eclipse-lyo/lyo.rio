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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.lyo.oslc.am.common.IAmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.query.PName;
import org.eclipse.lyo.rio.query.SimpleQueryBuilder;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.store.RioValue;
import org.eclipse.lyo.rio.util.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Servlet implementation class ResourceQuery
 */
public class LinkTypeQueryService extends RioBaseService {

	private static final long serialVersionUID = 5367608750519943810L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processQuery(req, resp);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processQuery(req, resp);
	}
	
	private void processQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try{
			String prefix = req.getParameter("oslc.prefix"); //$NON-NLS-1$
			String select = req.getParameter("oslc.select"); //$NON-NLS-1$
			String where = req.getParameter("oslc.where"); //$NON-NLS-1$
			String orderBy = req.getParameter("oslc.orderBy"); //$NON-NLS-1$
			String searchTerms = req.getParameter("oslc.searchTerms"); //$NON-NLS-1$
			
			SimpleQueryBuilder queryBuilder = new SimpleQueryBuilder();
			queryBuilder.addPrefix(IAmConstants.OSLC_AM_NAMESPACE, IAmConstants.OSLC_AM_PREFIX);
			queryBuilder.parsePrefix(prefix);
			queryBuilder.parseSelect(select);
			queryBuilder.parseWhere("uri", where); //$NON-NLS-1$
			queryBuilder.parseSelect(orderBy);
			queryBuilder.parseSearchTerms(searchTerms);
			
			String sparql = queryBuilder.getQueryString(IAmConstants.OSLC_AM_TYPE_LINKTYPE);
			
			RioStore store = this.getStore();
			String queryUri = req.getRequestURL().toString();
			
			List<Map<String, RioValue>> results = store.query(IConstants.SPARQL, sparql, 100);
			
			// TODO: create propnames
			Map<String, PName> propNames = queryBuilder.getPropertyNames();
			
			String responseResource = buildResponseResource(results, propNames, queryUri);
			resp.setContentType(IConstants.CT_RDF_XML); 
			resp.getWriter().write(responseResource); 
			resp.setStatus(IConstants.SC_OK);
			
		} catch( Exception e ) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e);
		}
	}
	
	private String buildResponseResource(List<Map<String, RioValue>> results, Map<String, PName> propNames, String reqUri) throws RioServiceException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element rdf = doc.createElementNS(IConstants.RDF_NAMESPACE, IConstants.RDF_TYPE_PTERM_RDF);
			doc.appendChild(rdf);
			
			Set<String> namespaces = namespacePrefixes.keySet();
			for (String namespace : namespaces) {
				rdf.setAttribute("xmlns:" + namespacePrefixes.get(namespace), namespace); //$NON-NLS-1$
			}
			
			Element responseElement = doc.createElementNS(IConstants.OSLC_NAMESPACE, IConstants.OSLC_PTERM_RESPONSEINFO);
			responseElement.setAttributeNS(IConstants.RDF_NAMESPACE, IConstants.RDF_PTERM_ABOUT, reqUri);
			rdf.appendChild(responseElement);
			
			Element title = doc.createElementNS(IConstants.DCTERMS_NAMESPACE, IConstants.DCTERMS_PTERM_TITLE);
			responseElement.appendChild(title);
			title.setTextContent("Messages.QueryService_QUERY_RESULTS_TITLE");
			
			Element rdfDescr = doc.createElementNS(IConstants.RDF_NAMESPACE, IConstants.RDF_TYPE_PTERM_DESCRIPTION);
			rdf.appendChild(rdfDescr);
			rdfDescr.setAttributeNS(IConstants.RDF_NAMESPACE, IConstants.RDF_PTERM_ABOUT, reqUri);
			
			Iterator<Map<String, RioValue>> iterator = results.iterator();
			while( iterator.hasNext() ) {
				
				Element rdfMem = doc.createElementNS(IConstants.RDFS_NAMESPACE, IConstants.RDFS_PTERM_MEMBER);
				rdfDescr.appendChild(rdfMem);
				Map<String, RioValue> map = iterator.next();
				RioValue uri = map.get("uri"); //$NON-NLS-1$
				rdfMem.setAttributeNS(IConstants.RDF_NAMESPACE, IConstants.RDF_PTERM_RESOURCE, uri.stringValue());
			}
				
			return XmlUtils.prettyPrint(doc);
			
		} catch (ParserConfigurationException e) {
			throw new RioServiceException(e);
		} finally {
			
		}
	}
	
	static private Map<String,String> namespacePrefixes = new HashMap<String,String>();
	static  {
		namespacePrefixes.put(IConstants.RDF_NAMESPACE, IConstants.RDF_PREFIX);
		namespacePrefixes.put(IConstants.RDFS_NAMESPACE, IConstants.RDFS_PREFIX);
		namespacePrefixes.put(IConstants.OSLC_NAMESPACE, IConstants.OSLC_PREFIX);
		namespacePrefixes.put(IConstants.DCTERMS_NAMESPACE, IConstants.DCTERMS_PREFIX);
	}
}
