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
 *    Sujeet Mishra - Initial implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.tests.utils;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.eclipse.lyo.core.trs.HttpConstants;

/**
 * This Class contains methods to send data to the server 
 * in the form of HTTP POST or HTTP PUT calls 
 */
public class SendUtil {
	
	/**
	 * 
	 * @param uri             resource uri for creation factory
	 * @param httpClient      client used to post to the uri
	 * @param httpContext     http context to use for the call
	 * @param contentType     content type to be used in the creation 
	 * @param content         content to be used in the creation
	 * @throws SendException  if an error occurs in posting to the uri
	 */
	public static String createResource(String uri, HttpClient httpClient, HttpContext httpContext, String contentType, String content) throws SendException
	{
		String createdResourceUri="";
		if (uri == null)
			throw new IllegalArgumentException(
					Messages.getServerString("send.util.uri.null")); //$NON-NLS-1$
		if (httpClient == null)
			throw new IllegalArgumentException(
					Messages.getServerString("send.util.httpclient.null")); //$NON-NLS-1$
		try {
			new URL(uri); // Make sure URL is valid
			
			HttpPost post = new HttpPost(uri);
			StringEntity entity = new StringEntity(content);
			post.setEntity(entity);
			post.setHeader(HttpConstants.ACCEPT, HttpConstants.CT_APPLICATION_RDF_XML);//$NON-NLS-1$
			post.addHeader(HttpConstants.CONTENT_TYPE, contentType);
			post.addHeader(HttpConstants.CACHE_CONTROL, "max-age=0"); //$NON-NLS-1$
			HttpResponse resp = httpClient.execute(post);
			
			try {
				if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
					HttpErrorHandler.responseToException(resp);
				}
				createdResourceUri=resp.getFirstHeader(HttpConstants.LOCATION).getValue();
				HttpResponseUtil.finalize(resp);
			}finally {
				try {
					if (entity != null) {
						EntityUtils.consume(entity);
					}
				} catch (IOException e) {
					// ignore
				}
			}

			
		} catch (Exception e) {
			String uriLocation = Messages.getServerString("send.util.uri.unidentifiable"); //$NON-NLS-1$
			
			if(uri != null && !uri.isEmpty())
			{
				uriLocation = uri;
			}
			throw new SendException(MessageFormat.format(
					Messages.getServerString("send.util.retrieve.error"), //$NON-NLS-1$
					uriLocation)); 
		}
		
		return createdResourceUri;
	}
	
	
	/**
	 * 
	 * @param uri             resource uri for update
	 * @param httpClient      client used to put data to the uri
	 * @param httpContext     http context to use for the call
	 * @param content         content to be used in the updation 
	 * @throws SendException  if an error occurs in putting data to the uri
	 */
	
	public static boolean updateResource(String uri, HttpClient httpClient, HttpContext httpContext,String contentType, String content) throws SendException
	{
		boolean resourceUpdated=false;
		if (uri == null)
			throw new IllegalArgumentException(
					Messages.getServerString("send.util.uri.null")); //$NON-NLS-1$
		if (httpClient == null)
			throw new IllegalArgumentException(
					Messages.getServerString("send.util.httpclient.null")); //$NON-NLS-1$
		try {
			new URL(uri); // Make sure URL is valid
			
			HttpPut put = new HttpPut(uri);
			StringEntity entity = new StringEntity(content);
			put.setEntity(entity);
			put.setHeader(HttpConstants.ACCEPT, HttpConstants.CT_APPLICATION_RDF_XML);
			put.addHeader(HttpConstants.CONTENT_TYPE, contentType);
			put.addHeader(HttpConstants.CACHE_CONTROL, "max-age=0"); //$NON-NLS-1$
			HttpResponse resp = httpClient.execute(put);
			
			try {
				if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					HttpErrorHandler.responseToException(resp);
				}
				resourceUpdated=true;
				HttpResponseUtil.finalize(resp);
			}finally {
				try {
					if (entity != null) {
						EntityUtils.consume(entity);
					}
				} catch (IOException e) {
					// ignore
				}
			}

			
		} catch (Exception e) {
			String uriLocation = Messages.getServerString("send.util.uri.unidentifiable"); //$NON-NLS-1$
			
			if(uri != null && !uri.isEmpty())
			{
				uriLocation = uri;
			}
			throw new SendException(MessageFormat.format(
					Messages.getServerString("send.util.retrieve.error"), //$NON-NLS-1$
					uriLocation)); 
		}

		return resourceUpdated;
	}

}
