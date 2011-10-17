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
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.oslc.am.common.IAmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.l10n.Messages;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.JsonFormatter2;
import org.eclipse.lyo.rio.store.JsonFormatter2.IMultiValueResolver;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.store.XmlFormatter;
import org.eclipse.lyo.rio.util.StringUtils;
import org.eclipse.lyo.rio.util.XmlUtils;


/**
 * Servlet implementation class Resource
 */
public class LinkTypeService extends RioBaseService {

	private static final long serialVersionUID = -5280734755943517104L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			RioStore store = this.getStore();
			LinkTypeHelper helper = new LinkTypeHelper();
			String resUri = request.getRequestURL().toString();
			LinkType linkType = helper.getLinkTypeFromResourceUri(store, resUri);

			if( linkType == null ) {
				throw new RioServiceException(IConstants.SC_NOT_FOUND);
			}
			String accept = request.getHeader(IConstants.HDR_ACCEPT);
			if( this.willAccept(IConstants.CT_HTML, request) ) {
				request.setAttribute("linkType", linkType); //$NON-NLS-1$
				RequestDispatcher rd = request.getRequestDispatcher("/am/linktype_view.jsp"); //$NON-NLS-1$
				rd.forward(request, response);
			} else if( this.willAccept(IConstants.CT_JSON, request) ) {
				JsonFormatter2 formatter = new JsonFormatter2(new IMultiValueResolver() {
					@Override
					public boolean isMultiValued(String property) {
						if( IConstants.DCTERMS_CREATOR.equals(property) || 
								IConstants.DCTERMS_CONTRIBUTOR.equals(property) ||
								IConstants.OSLC_SERVICEPROVIDER.equals(property) ) {
							return true;
						}  
						return false;
					}
				});
				formatter.addNamespacePrefix(IAmConstants.OSLC_AM_NAMESPACE, IAmConstants.OSLC_AM_PREFIX);
				String json = formatter.format(linkType);
				response.setContentType(IConstants.CT_JSON); 
				response.getWriter().write(json); 
				response.setStatus(IConstants.SC_OK);
			} else if( this.willAccept(IConstants.CT_APP_N_TRIPLES, request) ) {
				response.setContentType(IConstants.CT_JSON); 
				response.getWriter().write(linkType.dumpNTriples()); 
				response.setStatus(IConstants.SC_OK);
			} else if( accept == null || this.willAccept(IConstants.CT_RDF_XML, request) ) {
				XmlFormatter formatter = new XmlFormatter();
				formatter.addNamespacePrefix(IAmConstants.OSLC_AM_NAMESPACE, IAmConstants.OSLC_AM_PREFIX);
				String content = formatter.format(linkType, IAmConstants.OSLC_AM_TYPE_LINKTYPE);
				response.getWriter().write(content);
				response.setContentType(IConstants.CT_RDF_XML);
				response.setContentLength(content.getBytes().length);
				response.setHeader(IConstants.HDR_ETAG, linkType.getETag());
				String lm = StringUtils.rfc2822(linkType.getModified());
				response.setHeader(IConstants.HDR_LAST_MODIFIED, lm);
				response.setStatus(IConstants.SC_OK);
			} else if( this.willAccept(IConstants.CT_OSLC_COMPACT, request) ) {
				String content = compactDocument(linkType);
				response.setStatus(IConstants.SC_OK);
				response.setContentType(IConstants.CT_OSLC_COMPACT);
				response.setContentLength(content.getBytes().length);
				response.getWriter().write(content);
			} else {
				throw new RioServiceException(IConstants.SC_UNSUPPORTED_MEDIA_TYPE, Messages.getString("Resource.UnableToAccept") + accept ); //$NON-NLS-1$
			}
			
		} catch (Exception e) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e);
		}
	}

	
	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			RioStore store = this.getStore();
			LinkTypeHelper helper = new LinkTypeHelper();
			LinkType linkType = helper.getLinkTypeFromResourceUri(store,request.getRequestURL().toString());
			if( linkType == null ) {
				throw new RioServiceException(IConstants.SC_NOT_FOUND);
			}
			
			checkConditionalHeaders(request, linkType);
			
			String contentType = request.getContentType();
			if( !IConstants.CT_RDF_XML.equals(contentType) ) {
				throw new RioServiceException(IConstants.SC_UNSUPPORTED_MEDIA_TYPE);
			}
			
			ServletInputStream content = request.getInputStream();
			String uri = store.nextAvailableUri(IAmConstants.SERVICE_LINKTYPE); 
			List<RioStatement> statements = store.parse(request.getRequestURL().toString(), content, contentType);
			String about = statements.get(0).getSubject();
			LinkType updatedLinkType = new LinkType(uri,about); 
			updatedLinkType.addStatements(statements);
			
			String userId = request.getRemoteUser();
			String userUri = this.getUserUri(userId);
			store.update(updatedLinkType, userUri); 
			
			updatedLinkType = helper.getLinkTypeFromResourceUri(store,request.getRequestURL().toString());
			response.setStatus(IConstants.SC_OK);
			response.addHeader(IConstants.HDR_ETAG, updatedLinkType.getETag());
			response.addHeader(IConstants.HDR_LOCATION, updatedLinkType.getUri());
			String lastModified = StringUtils.rfc2822(updatedLinkType.getModified());
			response.addHeader(IConstants.HDR_LAST_MODIFIED, lastModified);
			
			
		} catch (Exception e) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e);
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			RioStore store = this.getStore();
			String uri = request.getRequestURL().toString();
			OslcResource resource = store.getOslcResource(uri);
			if( resource == null ) {
				throw new RioServiceException(IConstants.SC_NOT_FOUND);
			}
			
			checkConditionalHeaders(request, resource);
			store.remove(resource); 
			
		} catch (RioServerException e) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e);
		}
	}
	
	@SuppressWarnings("nls")
	private String compactDocument(LinkType linkType ) throws RioServiceException {
		String title = XmlUtils.encode(linkType.getLabel());
		String editUri = linkType.getUri();
		String iconUrl = this.getBaseUrl() + '/' + "oslc.png";
		String smUrl = this.getBaseUrl() + '/' + "compact/linktype";
		String doc = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
			"<rdf:RDF \n" +
			"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +
			"   xmlns:dcterms=\"http://purl.org/dc/terms/\" \n" +
			"   xmlns:oslc=\"http://open-services.net/ns/core#\"> \n" +
			" <oslc:Compact \n" +
			"   rdf:about=\"" + editUri + "\"> \n" +
			"   <dcterms:title>" + title + "</dcterms:title> \n" +
			"   <oslc:icon rdf:resource=\"" + iconUrl + "\" /> \n" +
			"   <oslc:smallPreview> \n" +
			"      <oslc:Preview> \n" +
			"         <oslc:document rdf:resource=\"" + smUrl + "\" /> \n" +
			"         <oslc:hintWidth>40em</oslc:hintWidth> \n" +
			"         <oslc:hintHeight>5em</oslc:hintHeight> \n" +
			"      </oslc:Preview> \n" +
			"   </oslc:smallPreview> \n" +
			" </oslc:Compact> \n" +
			"</rdf:RDF>";
		return doc;
	}

}
 