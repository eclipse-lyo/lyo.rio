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
package org.eclipse.lyo.oslc.am.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.oslc.am.common.IAmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.l10n.Messages;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.OslcResource;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.util.StringUtils;

/**
 * Servlet implementation class Resource
 */
public class ResourceSourceService extends RioBaseService {
	private static final long serialVersionUID = 4096103354264604073L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String reqUri[] = request.getRequestURI().split("/"); //$NON-NLS-1$
		if( reqUri.length != 4 ) {
			throw new RioServiceException(IConstants.SC_BAD, "Invalid source URI");
		}

		InputStream is = null;
		ServletOutputStream out = null;
		String id = reqUri[3];
		try {
			RioStore store = this.getStore();
			String uri = getBaseUrl() + '/' + IAmConstants.SERVICE_RESOURCE + '/' + id;
			RioResource rioResource = store.getResource(uri);
			Resource resource = new Resource(rioResource); // wrap as generic AM resource
			String sourceContentType = resource.getSourceContentType();
			String accept = request.getHeader(IConstants.HDR_ACCEPT);
			if( accept == null || accept.indexOf(sourceContentType)>=0 || accept.indexOf("*/*")>=0 ) {
				response.setContentType(sourceContentType);
				is = store.getBinaryResource(id);
				out = response.getOutputStream();
				byte[] bytes = new byte[1024];
				int bytesRead;
				while ((bytesRead = is.read(bytes)) != -1) {
				    out.write(bytes, 0, bytesRead);
				}
			} else {
				throw new RioServiceException(IConstants.SC_BAD, "Source content type mismatch");
			}
		} catch (FileNotFoundException e) {
			throw new RioServiceException(IConstants.SC_NOT_FOUND, "Resource source not found");
		} catch (RioServerException e) {
			throw new RioServiceException(IConstants.SC_NOT_FOUND, "OSLC AM Resource not found");
		} catch (Exception e) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e.getMessage());
		} finally {
			if( is != null ) is.close();
			if( out != null ) out.close();			
		}
		
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ifMatch = request.getHeader(IConstants.HDR_IF_MATCH);
		String ifUnmodifiedSince = request.getHeader(IConstants.HDR_IF_UNMODIFIED_SINCE);
		if( ifMatch == null && ifUnmodifiedSince == null ) {
			this.reportError(IConstants.SC_PRECONDITION_FAILED, 
					Messages.getString("Resource.ConditionalHeaderRequired"), request, response);  //$NON-NLS-1$
		}
		
		try {
			RioStore store = this.getStore();
			OslcResource resource = store.getOslcResource(request.getRequestURL().toString());
			if( resource == null ) {
				throw new RioServiceException(IConstants.SC_NOT_FOUND);
			}
			
			if( ifMatch != null ) {
				String eTag = resource.getETag();
				if( !ifMatch.equals(eTag) ) {
					throw new RioServiceException(IConstants.SC_PRECONDITION_FAILED, "ETag mismatch");
				}
				if( ifUnmodifiedSince != null ) {
					Date unmodifiedSince = StringUtils.rfc2822(ifUnmodifiedSince);
					Date modified = resource.getModified();
					if( !unmodifiedSince.after(modified) ) {
						throw new RioServiceException(IConstants.SC_PRECONDITION_FAILED, "Last modified after supplied If-Unmodified-Since value");
					}
				}
			}
			
			// ok, then we update this resource
			String contentType = request.getContentType();
			ServletInputStream content = request.getInputStream();
			String resUri = resource.getUri();
			RioResource updatedResource = new RioResource(resUri);
			List<RioStatement> statements = store.parse(resUri, content, contentType);
			updatedResource.addStatements(statements);
			String userUri = getUserUri(request.getRemoteUser());
			store.update(updatedResource, userUri); 
			
		} catch (RioServerException e) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e);
		} catch (ParseException e) {
			throw new RioServiceException(IConstants.SC_PRECONDITION_FAILED, "Invalid If-Unmodified-Since Header");
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ifMatch = request.getHeader(IConstants.HDR_IF_MATCH);
		String ifUnmodifiedSince = request.getHeader(IConstants.HDR_IF_UNMODIFIED_SINCE);
		if( ifMatch == null && ifUnmodifiedSince == null ) {
			this.reportError(IConstants.SC_PRECONDITION_FAILED, 
					Messages.getString("Resource.ConditionalHeaderRequired"), request, response);  //$NON-NLS-1$
		}
		
		try {
			RioStore store = this.getStore();
			OslcResource resource = store.getOslcResource(request.getRequestURL().toString());
			if( resource == null ) {
				throw new RioServiceException(IConstants.SC_NOT_FOUND);
			}
			
			if( ifMatch != null ) {
				String eTag = resource.getETag();
				if( !ifMatch.equals(eTag) ) {
					throw new RioServiceException(IConstants.SC_PRECONDITION_FAILED, "ETag mismatch");
				}
				if( ifUnmodifiedSince != null ) {
					Date unmodifiedSince = StringUtils.rfc2822(ifUnmodifiedSince);
					Date modified = resource.getModified();
					if( !unmodifiedSince.after(modified) ) {
						throw new RioServiceException(IConstants.SC_PRECONDITION_FAILED, "Last modified after supplied If-Unmodified-Since value");
					}
				}
			}
			
			// ok, then we remove this resource
			store.remove(resource); 

			
		} catch (RioServerException e) {
			throw new RioServiceException(IConstants.SC_INTERNAL_ERROR, e);
		} catch (ParseException e) {
			throw new RioServiceException(IConstants.SC_PRECONDITION_FAILED, "Invalid If-Unmodified-Since Header");
		}
	}

//	@SuppressWarnings("nls")
//	private String compactDocument(RioResource resource ) throws RioServiceException {
//		String title = XmlUtils.encode(resource.getTitle());
//		String shortTitle = "Resource " + resource.getIdentifier();
//		String resourceUri = resource.getUri();
//		try{
//			String resUri = URLEncoder.encode(resourceUri, IAmConstants.TEXT_ENCODING);
//			String iconUrl = this.getBaseUrl() + "oslc.png";
//			String smUrl = this.getBaseUrl() + "compact/resource?uri=" + resUri + "&type=small";
//			String lgUrl = this.getBaseUrl() + "compact/resource?uri=" + resUri + "&type=large";
//			String doc = 
//				"<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
//				"<rdf:RDF \n" +
//				"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +
//				"   xmlns:dcterms=\"http://purl.org/dc/terms/\" \n" +
//				"   xmlns:oslc=\"http://open-services.net/ns/core#\"> \n" +
//				" <oslc:Compact \n" +
//				"   rdf:about=\"" + resourceUri + "\"> \n" +
//				"   <dcterms:title>" + title + "</dcterms:title> \n" +
//				"   <oslc:shortTitle>" + shortTitle + "</oslc:shortTitle> \n" +
//				"   <oslc:icon rdf:resource=\"" + iconUrl + "\" /> \n" +
//				"   <oslc:smallPreview> \n" +
//				"      <oslc:Preview> \n" +
//				"         <oslc:document rdf:resource=\"" + smUrl + "\" /> \n" +
//				"         <oslc:hintWidth> 60em </oslc:hintWidth> \n" +
//				"         <oslc:hintHeight> 20em </oslc:hintHeight> \n" +
//				"      </oslc:Preview> \n" +
//				"   </oslc:smallPreview> \n" +
//				"   <oslc:largePreview> \n" +
//				"      <oslc:Preview> \n" +
//				"         <oslc:document rdf:resource=\"" + lgUrl + "\" /> \n" +
//				"         <oslc:hintWidth> 120em </oslc:hintWidth> \n" +
//				"         <oslc:hintHeight> 60em </oslc:hintHeight> \n" +
//				"      </oslc:Preview> \n" +
//				"   </oslc:largePreview> \n" +
//				" </oslc:Compact> \n" +
//				"</rdf:RDF>";
//			return doc;
//		} catch( UnsupportedEncodingException ex ) {
//			throw new RioServiceException(ex);
//		}
//	}
	
}
 