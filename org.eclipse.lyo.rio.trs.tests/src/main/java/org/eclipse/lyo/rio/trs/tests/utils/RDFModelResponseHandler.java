/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
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
 *    Joseph Leong, Sujeet Mishra - Initial implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.tests.utils;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.eclipse.lyo.core.trs.HttpConstants;
import org.openjena.riot.ContentType;
import org.openjena.riot.Lang;
import org.openjena.riot.WebContent;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RDFModelResponseHandler implements ResponseHandler<Model> {
	final Node base;

	protected int statusCode = 0;
	protected String reason = null;

	public RDFModelResponseHandler(Node base) {
		this.base = base;
	}

	public RDFModelResponseHandler(String base) {
		this(ResourceFactory.createResource(base).asNode());
	}

	@Override
	public Model handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		statusCode = response.getStatusLine().getStatusCode();
		reason = response.getStatusLine().getReasonPhrase();
		Model model = ModelUtil.createDefaultModel();
		HttpEntity entity = response.getEntity();
		try {
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				HttpErrorHandler.responseToException(response);
			}

			if (entity == null)
				
				throw new InvalidRDFResourceException(
						MessageFormat.format(
								Messages.getServerString("rdf.model.response.helper.missing.rdf"), //$NON-NLS-1$
								base.getURI())); 

			String rdfFormat = null;
			final Header contentTypeHeader = response.getFirstHeader(HttpConstants.CONTENT_TYPE);
			if (contentTypeHeader != null) {
				final ContentType contentType = ContentType.parse(contentTypeHeader.getValue());
				if (contentType != null) {
					Lang lang = WebContent.contentTypeToLang(contentType.getContentType());
					if (lang != null) {
						rdfFormat = lang.getName();
					} else if (HttpConstants.CT_TEXT_XML.equals(contentType.getContentType())) {
						rdfFormat = Lang.RDFXML.getName(); // attempt RDF
						// parsing anyway
					}
				}
			}

			if (rdfFormat == null)
				throw new ClientProtocolException(
						MessageFormat.format(
								Messages.getServerString("rdf.model.response.helper.bad.content.type"), //$NON-NLS-1$
								base.getURI()));
			
			String content = EntityUtils.toString(entity, HTTP.UTF_8);
			try {
				model.read(new StringReader(content), base.getURI(), rdfFormat);
			} catch (Exception e) {
				if (e.getMessage().contains("Interrupt") //$NON-NLS-1$
						|| Thread.currentThread().isInterrupted()) {
					Thread.currentThread().interrupt();
					throw (IOException) new IOException().initCause(e);
				}
				
				throw new InvalidRDFResourceException(
						MessageFormat.format(
								Messages.getServerString("rdf.model.response.helper.unparseable.rdf"), //$NON-NLS-1$
								base.getURI(), 
								content), e); 
			}

			HttpResponseUtil.finalize(response);
		} finally {
			try {
				if (entity != null) {
					EntityUtils.consume(entity);
				}
			} catch (IOException e) {
				// ignore
			}
		}
		return model;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getReasonPhrase() {
		return reason;
	}
}