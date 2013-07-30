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
 *    Joseph Leong - Initial implementation
 *******************************************************************************/
package org.eclipse.lyo.rio.trs.tests.utils;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

public class EasySSLClient {
	private DefaultHttpClient httpClient;
	
	public EasySSLClient() throws Exception 
	{
		httpClient = new DefaultHttpClient();
		initCookiePolicy();
		initSSLPolicy();
	}
	
	void initCookiePolicy() {
		httpClient.setCookieStore(new BasicCookieStore());
	}
 
	void initSSLPolicy() throws Exception {
		Scheme http1 = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		Scheme http2 = new Scheme("http", 8080, PlainSocketFactory.getSocketFactory());
		Scheme http3 = new Scheme("http", 8082, PlainSocketFactory.getSocketFactory());
		Scheme http4 = new Scheme("http", 9080, PlainSocketFactory.getSocketFactory());
		Scheme http5 = new Scheme("http", 9081, PlainSocketFactory.getSocketFactory());
		
		SSLSocketFactory sf = null;
		
		try {
			sf = getEasySSLSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to initialize SSL HttpClient");
		} 
		
		Scheme https1 = new Scheme("https", 443, sf);
		Scheme https2 = new Scheme("https", 8443, sf);
		Scheme https3 = new Scheme("https", 9443, sf);
		Scheme https4 = new Scheme("https", 9444, sf);
		
		SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
		
		schemeRegistry.register(http1);
		schemeRegistry.register(http2);
		schemeRegistry.register(http3);
		schemeRegistry.register(http4);
		schemeRegistry.register(http5);
		schemeRegistry.register(https1);
		schemeRegistry.register(https2);
		schemeRegistry.register(https3);
		schemeRegistry.register(https4);
	}
 
	private SSLSocketFactory getEasySSLSocketFactory() 
	throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException 
	{
		TrustStrategy trustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] x509Certificates,
					String s) throws CertificateException {
				return true; // Accept Self-Signed Certs
			}
		};

		SSLSocketFactory sslSocketFactory = null;
		
		//Bypass check for hostname verification
		sslSocketFactory = 
				new SSLSocketFactory(trustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		return sslSocketFactory;
	}
	
	public DefaultHttpClient getClient() {
		return this.httpClient;
	}
}
