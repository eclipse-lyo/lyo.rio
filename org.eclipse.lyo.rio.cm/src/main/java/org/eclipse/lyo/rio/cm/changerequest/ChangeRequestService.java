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
package org.eclipse.lyo.rio.cm.changerequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.lyo.rio.cm.common.ICmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.l10n.Messages;
import org.eclipse.lyo.rio.query.PName;
import org.eclipse.lyo.rio.query.SimpleQueryBuilder;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.JsonFormatter2;
import org.eclipse.lyo.rio.store.JsonFormatter2.IMultiValueResolver;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.store.RioValue;
import org.eclipse.lyo.rio.store.XmlFormatter;
import org.eclipse.lyo.rio.util.StringUtils;
import org.eclipse.lyo.rio.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Servlet implementation class ChangeRequest
 */
public class ChangeRequestService extends RioBaseService {

	private static final long serialVersionUID = -5280734755943517104L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			RioStore store = this.getStore();
			
			URI reqUri = URI.create(request.getRequestURI());
			String path = reqUri.getPath();
			StringTokenizer st = new StringTokenizer(path,"/");
			if( st.countTokens() > 2 ) {  // /rio-cm/changerequeast/{resId}
				
				OslcResource resource = store.getOslcResource(request.getRequestURL().toString());
				if( resource == null ) {
					throw new RioServiceException(IConstants.SC_NOT_FOUND);
				}
				if( !resource.isRdfType(ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST) ) {
					throw new RioServiceException("Not a Change Request Resource");
				}
				String accept = request.getHeader(IConstants.HDR_ACCEPT);
				if( this.willAccept(IConstants.CT_HTML, request) ) {
					ChangeRequest cr = new ChangeRequest(resource.getUri());
					cr.addStatements(resource.getStatements());
					request.setAttribute("changerequest", cr); //$NON-NLS-1$
					RequestDispatcher rd = request.getRequestDispatcher("/cm/changerequest_view.jsp"); //$NON-NLS-1$
					rd.forward(request, response);
				} else if( this.willAccept(IConstants.CT_RDF_XML, request) ) {
					response.setHeader(IConstants.HDR_ETAG, resource.getETag());
					String lm = StringUtils.rfc2822(resource.getModified());
					response.setHeader(IConstants.HDR_LAST_MODIFIED, lm);
					response.setStatus(IConstants.SC_OK);
					String content = XmlFormatter.formatResource(resource, ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST); 
					response.getWriter().write(content);
					if( this.willAccept(IConstants.CT_XML, request) ) {
						response.setContentType(IConstants.CT_XML);
					} else {
						response.setContentType(IConstants.CT_RDF_XML);
					}
					response.setContentLength(content.getBytes().length);
				} else if( this.willAccept(IConstants.CT_APP_N_TRIPLES, request) ) {
					response.setContentType(IConstants.CT_JSON); 
					response.getWriter().write(resource.dumpNTriples()); 
					response.setStatus(IConstants.SC_OK);
				} else if( this.willAccept(IConstants.CT_JSON, request) ) {
					JsonFormatter2 formatter = new JsonFormatter2(new IMultiValueResolver() {
						@Override
						public boolean isMultiValued(String property) {
							if( IConstants.DCTERMS_CREATOR.equals(property) || 
									IConstants.DCTERMS_CONTRIBUTOR.equals(property) ||
									IConstants.DCTERMS_SUBJECT.equals(property) ||
									IConstants.DCTERMS_TYPE.equals(property) ||
									ICmConstants.OSLC_CM_RELATEDCHANGEREQUEST.equals(property) ||
									ICmConstants.OSLC_CM_AFFECTSPLANITEM.equals(property) ||
									ICmConstants.OSLC_CM_AFFECTEDBYDEFECT.equals(property) ||
									ICmConstants.OSLC_CM_TRACKSREQUIREMENT.equals(property) ||
									ICmConstants.OSLC_CM_IMPLEMENTSREQUIREMENT.equals(property) ||
									ICmConstants.OSLC_CM_AFFECTSREQUIREMENT.equals(property) ||
									ICmConstants.OSLC_CM_TESTEDBYTESTCASE.equals(property) ||
									ICmConstants.OSLC_CM_AFFECTSTESTRESULT.equals(property) ||
									ICmConstants.OSLC_CM_BLOCKSTESTEXECUTIONRECORD.equals(property) ||
									ICmConstants.OSLC_CM_RELATEDTESTEXECUTIONRECORD.equals(property) ||
									ICmConstants.OSLC_CM_RELATEDTESTCASE.equals(property) ||
									ICmConstants.OSLC_CM_RELATEDTESTPLAN.equals(property) ||
									ICmConstants.OSLC_CM_RELATEDTESTSCRIPT.equals(property) ||
									ICmConstants.OSLC_CM_TRACKSCHANGESET.equals(property) ||
									IConstants.OSLC_SERVICEPROVIDER.equals(property) ) {
								return true;
							}  
							return false;
						}
					});
					formatter.addNamespacePrefix(ICmConstants.OSLC_CM_NAMESPACE, ICmConstants.OSLC_CM_PREFIX);
					String json = formatter.format(resource);
					response.setContentType(IConstants.CT_JSON); 
					response.getWriter().write(json); 
					response.setStatus(IConstants.SC_OK);
				} else if( this.willAccept(IConstants.CT_OSLC_COMPACT, request) ) {
					response.setContentType(IConstants.CT_OSLC_COMPACT);
					response.setStatus(IConstants.SC_OK);
					String content = compactDocument(resource);
					response.setContentLength(content.getBytes().length);
					response.getWriter().write(content);
				} else {
					throw new RioServiceException(IConstants.SC_UNSUPPORTED_MEDIA_TYPE, Messages.getString("ChangeRequest.UnableToAccept") + accept ); //$NON-NLS-1$
				}

			} else {
				processQuery(request, response);
			}
		} catch (RioServerException e) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e);
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			RioStore store = this.getStore();
			OslcResource resource = store.getOslcResource(request.getRequestURL().toString());
			if( resource == null ) {
				throw new RioServiceException(IConstants.SC_NOT_FOUND);
			}
			
			checkConditionalHeaders(request, resource);

			// cache the created and creator
			Date created = resource.getCreated();
			String creator = resource.getCreator();
			
			// ok, then we update this resource
			String contentType = request.getContentType();
			if( !contentType.startsWith(IConstants.CT_RDF_XML) ) {
				throw new RioServiceException(IConstants.SC_UNSUPPORTED_MEDIA_TYPE);
			}
			
			ServletInputStream content = request.getInputStream();
			OslcResource updatedResource = new OslcResource(resource.getUri());
			List<RioStatement> statements = store.parse(resource.getUri(), content, contentType);
			updatedResource.addStatements(statements);
			updatedResource.setCreated(created);
			updatedResource.setCreator(creator);
			String userId = request.getRemoteUser();
			String userUri = this.getUserUri(userId);
			store.update(updatedResource, userUri);
			
			updatedResource = store.getOslcResource(resource.getUri());
			response.setStatus(IConstants.SC_OK);
			response.addHeader(IConstants.HDR_ETAG, updatedResource.getETag());
			response.addHeader(IConstants.HDR_LOCATION, updatedResource.getUri());
			String lastModified = StringUtils.rfc2822(updatedResource.getModified());
			response.addHeader(IConstants.HDR_LAST_MODIFIED, lastModified);
			
		} catch (RioServerException e) {
			throw new RioServiceException(IConstants.SC_BAD, e);
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			RioStore store = this.getStore();
			OslcResource resource = store.getOslcResource(request.getRequestURL().toString());
			if( resource == null ) {
				throw new RioServiceException(IConstants.SC_NOT_FOUND);
			}
			checkConditionalHeaders(request, resource);
			store.remove(resource); 

			
		} catch (RioServerException e) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e);
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String contentType = request.getContentType();
		ServletInputStream content = request.getInputStream();
		
		RioStore store = this.getStore();
		if( RioStore.rdfFormatFromContentType(contentType) != null ) {
			try {
				String resUri = store.nextAvailableUri(ICmConstants.SERVICE_CHANGEREQUEST);
				ChangeRequest resource = new ChangeRequest(resUri);
				List<RioStatement> statements = store.parse(resUri, content, contentType);
				resource.addStatements(statements);
				
				// make sure the required properties are in there
				if( resource.getFirstStatement(null, ICmConstants.OSLC_CM_APPROVED, null) == null ) {
					resource.setApproved(false);
				}
				if( resource.getFirstStatement(null, ICmConstants.OSLC_CM_TERM_REVIEWED, null) == null ) {
					resource.setReviewed(false);
				}
				if( resource.getFirstStatement(null, ICmConstants.OSLC_CM_VERIFIED, null) == null ) {
					resource.setVerified(false);
				}
				RioStatement statusStatement = resource.getFirstStatement(null, ICmConstants.OSLC_CM_STATUS, null); 
				if(  statusStatement == null ) {
					resource.setStatus(ICmConstants.OSLC_CM_STATUS_SUBMITTED);
				} else {
					String status = statusStatement.getObject().stringValue();
					resource.setStatus(status);
				}
				
				// now set user
				String userUri = getUserUri(request.getRemoteUser());

				// if it parsed, then add it to the store.
				store.update(resource, userUri);
				
				// now get it back, to find 
				OslcResource returnedResource = store.getOslcResource(resource.getUri());
				Date created = returnedResource.getCreated();
				String eTag = returnedResource.getETag();
				
				response.setStatus(IConstants.SC_CREATED);
				response.setHeader(IConstants.HDR_LOCATION, resource.getUri());
				response.setHeader(IConstants.HDR_LAST_MODIFIED, StringUtils.rfc2822(created) );
				response.setHeader(IConstants.HDR_ETAG, eTag );

			} catch (RioServerException e) {
				throw new RioServiceException(IConstants.SC_BAD, e);
			}
		}
	}
	
	private void processQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try{
			String prefix = req.getParameter("oslc.prefix"); //$NON-NLS-1$
			String select = req.getParameter("oslc.select"); //$NON-NLS-1$
			String where = req.getParameter("oslc.where"); //$NON-NLS-1$
			String orderBy = req.getParameter("oslc.orderBy"); //$NON-NLS-1$
			String searchTerms = req.getParameter("oslc.searchTerms"); //$NON-NLS-1$
			
			SimpleQueryBuilder queryBuilder = new SimpleQueryBuilder();
			queryBuilder.parsePrefix(prefix);
			queryBuilder.addPrefix(ICmConstants.OSLC_CM_NAMESPACE, ICmConstants.OSLC_CM_PREFIX);
			queryBuilder.parseSelect(select);
			queryBuilder.parseWhere("uri", where); //$NON-NLS-1$
			queryBuilder.parseSelect(orderBy);
			queryBuilder.parseSearchTerms(searchTerms);
			
			String sparql = queryBuilder.getQueryString(ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
			
			RioStore store = this.getStore();
			String queryUri = req.getRequestURL().toString() + '?' + req.getQueryString();
			
			List<Map<String, RioValue>> results = store.query(IConstants.SPARQL, sparql, 100);
			
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
			title.setTextContent(Messages.getString("ResourceQuery.Title"));
			
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
	
	@SuppressWarnings("nls")
	private String compactDocument(OslcResource resource ) throws RioServiceException {
		String title = XmlUtils.encode(resource.getTitle());
		String shortTitle = "ChangeRequest " + resource.getIdentifier();
		String resourceUri = resource.getUri();
		try{
			String resUri = URLEncoder.encode(resourceUri, IConstants.TEXT_ENCODING);
			String iconUrl = this.getBaseUrl() + "/oslc.png";
			String smUrl = this.getBaseUrl() + "/compact/" + ICmConstants.SERVICE_CHANGEREQUEST + "?uri=" + resUri + "&amp;type=small";
			String lgUrl = this.getBaseUrl() + "/compact/" + ICmConstants.SERVICE_CHANGEREQUEST + "?uri=" + resUri + "&amp;type=large";
			String doc = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
				"<rdf:RDF \n" +
				"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +
				"   xmlns:dcterms=\"http://purl.org/dc/terms/\" \n" +
				"   xmlns:oslc=\"http://open-services.net/ns/core#\"> \n" +
				" <oslc:Compact \n" +
				"   rdf:about=\"" + resourceUri + "\"> \n" +
				"   <dcterms:title>" + title + "</dcterms:title> \n" +
				"   <oslc:shortTitle>" + shortTitle + "</oslc:shortTitle> \n" +
				"   <oslc:icon rdf:resource=\"" + iconUrl + "\" /> \n" +
				"   <oslc:smallPreview> \n" +
				"      <oslc:Preview> \n" +
				"         <oslc:document rdf:resource=\"" + smUrl + "\" /> \n" +
				"         <oslc:hintWidth>500px</oslc:hintWidth> \n" +
				"         <oslc:hintHeight>120px</oslc:hintHeight> \n" +
				"      </oslc:Preview> \n" +
				"   </oslc:smallPreview> \n" +
				"   <oslc:largePreview> \n" +
				"      <oslc:Preview> \n" +
				"         <oslc:document rdf:resource=\"" + lgUrl + "\" /> \n" +
				"         <oslc:hintWidth>500px</oslc:hintWidth> \n" +
				"         <oslc:hintHeight>500px</oslc:hintHeight> \n" +
				"      </oslc:Preview> \n" +
				"   </oslc:largePreview> \n" +
				" </oslc:Compact> \n" +
				"</rdf:RDF>";
			return doc;
		} catch( UnsupportedEncodingException ex ) {
			throw new RioServiceException(ex);
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
 