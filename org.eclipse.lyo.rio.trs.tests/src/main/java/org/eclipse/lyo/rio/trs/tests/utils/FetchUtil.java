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
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

import javax.ws.rs.HttpMethod;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;
import org.eclipse.lyo.core.trs.HttpConstants;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * A class that provides a utility methods to fetch HTTP resources as well as
 * process fetch responses
 */
public class FetchUtil {
	/**
	 * Fetches a resource. In case of success, this method
	 * returns an instance of the {@link Model}. 
	 * 
	 * @param uri
	 *            resource uri to fetch
	 * @param httpClient
	 *            client used to fetch the resource
	 * @param httpContext
	 *            http context to use for the call
	 * @param acceptType 
	 * 			  value to use in the accept header
	 * @throws InterruptedException
	 *             if the thread is interrupted
	 * @throws FetchException
	 *             if an error occurs while fetching the resource.
	 * @throws IOException
	 *             if an error occurs while updating the retryable error
	 *             information into the error handler
	 */
	public static Model fetchResource(String uri, HttpClient httpClient, HttpContext httpContext, String acceptType) 
	throws InterruptedException, FetchException 
	{
		if (uri == null)
			throw new IllegalArgumentException(
					Messages.getServerString("fetch.util.uri.null")); //$NON-NLS-1$
		if (httpClient == null)
			throw new IllegalArgumentException(
					Messages.getServerString("fetch.util.httpclient.null")); //$NON-NLS-1$
		
		Model model = null;
		
		try {
			new URL(uri); // Make sure URL is valid
			
			HttpGet get = new HttpGet(uri);
			
			get.setHeader(HttpConstants.ACCEPT, acceptType);

			// Caches must revalidate with origin server. This is to prevent a cache
			// from serving stale data. We may still get a cached response if the
			// origin server responds to the revalidation with a 304.
			// See Unspecified end-to-end revalidation:
			// http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4
			get.addHeader(HttpConstants.CACHE_CONTROL, "max-age=0"); //$NON-NLS-1$
			
			try {
				model = httpClient.execute(get, new RDFModelResponseHandler(uri), httpContext);
			} catch (HttpResponseException e1) {
				if (e1.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
					// If authentication is required attempt OAuth authentication.
					// This uses two legged OAuth which requires a functional
					// user be already configured on the resource server.
					Properties prop = TestCore.getConfigPropertiesInstance();
					String consumerKey = prop.getProperty("consumerKey");
					String consumerSecret = prop.getProperty("consumerSecret");
					String authorizationTokenURL = prop.getProperty("OAuthURL");
					String oAuthRealm = prop.getProperty("OAuthRealm");
					
					// Using the information from the config.properties file
					// construct the authentication header to use in our GET request
					OAuthServiceProvider provider = new OAuthServiceProvider(null, authorizationTokenURL, null);
					OAuthConsumer consumer = new OAuthConsumer("",consumerKey,consumerSecret,provider);
					OAuthAccessor accessor = new OAuthAccessor(consumer);
					accessor.accessToken = "";

					try {
						OAuthMessage message = accessor.newRequestMessage(HttpMethod.GET, prop.getProperty("configTrsEndpoint"), null);
						String authHeader = message.getAuthorizationHeader(oAuthRealm);
						get.setHeader("Authorization",authHeader);
						get.setHeader("OSLC-Core-Version", "2.0");
						
						model = httpClient.execute(get, new RDFModelResponseHandler(uri), httpContext);
					} catch (OAuthException e) {
						TestCore.terminateTest(Messages.getServerString("fetch.util.authentication.failure"), e);
					}
				} 
			}
		} catch (Exception e) {
			String uriLocation = Messages.getServerString("fetch.util.uri.unidentifiable"); //$NON-NLS-1$
			
			if(uri != null && !uri.isEmpty())
			{
				uriLocation = uri;
			}
				
			throw new FetchException(MessageFormat.format(
					Messages.getServerString("fetch.util.retrieve.error"), //$NON-NLS-1$
					uriLocation)); 
		}

		return model;
	}
}
