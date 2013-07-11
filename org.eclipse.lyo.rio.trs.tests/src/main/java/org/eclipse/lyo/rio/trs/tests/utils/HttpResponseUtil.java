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
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;


public class HttpResponseUtil {
	/**
	 * Marks an HTTP response as final and disposes of any system resources
	 * referenced by the response.
	 * <p>
	 * Clients should aggressively call this method as soon as they no longer need the response to
	 * reduce contention over possibly scarce system resources.
	 * </p>
	 * <p>
	 * Clients should <strong>not</strong> attempt to access the HTTP response after calling this
	 * method.
	 * </p>
	 * @param response the HTTP response to finalize
	 */
	public static void finalize(final HttpResponse response) {
		if (response == null)
			return;
		HttpEntity entity = response.getEntity();
		try {
			if (entity != null) {
				InputStream is = entity.getContent();
				if (is != null) {
					is.close();
				}
			}
		} catch (IOException e) { /* ignored */
		}
	}

	private HttpResponseUtil() {
		super();
		throw new UnsupportedOperationException(
				Messages.getServerString("http.response.util.no.instance")); //$NON-NLS-1$
	}

}