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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


public class HttpErrorHandler {

	/**
	 * Handle a possible HTTP error response.
	 * 
	 * @param response
	 *            The HTTP response to handle; must not be <code>null</code>
	 * @throws HttpResponseException
	 *             if the response status code maps to an exception class
	 */
	public static void responseToException(HttpResponse response)
	throws HttpResponseException 
	{
		if (response == null)
			throw new IllegalArgumentException(
					Messages.getServerString("http.error.handler.null.argument")); //$NON-NLS-1$

		Integer status = Integer.valueOf(response.getStatusLine().getStatusCode());

		//Create detail message from response status line and body
		String reasonPhrase = response.getStatusLine().getReasonPhrase();
				
		StringBuilder message = new StringBuilder(reasonPhrase == null ? "" : reasonPhrase); //$NON-NLS-1$
		
		if (response.getEntity() != null) {
			try {
				String body = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				if (body != null && body.length() != 0) {
					message.append('\n');
					message.append(body);
				}
			} catch (IOException e) { } // ignore, since the original error needs to be reported
		}

		throw new HttpResponseException(status, message.toString());
	}
}
