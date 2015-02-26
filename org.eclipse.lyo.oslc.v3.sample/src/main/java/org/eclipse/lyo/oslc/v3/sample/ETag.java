/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
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
 *     Samuel Padgett       - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc.v3.sample;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.hp.hpl.jena.rdf.model.Model;

public class ETag {
	/**
	 * Create a weak ETag value from a Jena model.
	 *
	 * @param m the model that represents the HTTP response body
	 * @return an ETag value
	 *
	 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">HTTP 1.1: Section 14.19 - ETag</a>
	 */
	public static String generate(Model m) {
		// Get the MD5 hash of the model as N-Triples.
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		m.write(out,  "N-TRIPLE");
		String md5 = DigestUtils.md5Hex(out.toByteArray());

		// Create a weak entity tag from the MD5 hash.
		return weakETag(md5);
	}

	public static String generateRandom() {
		return weakETag(UUID.randomUUID().toString());
	}

	/**
	 * Tests an If-Match or If-None-Match header value against an ETag, handling
	 * wildcards and comma-separated header values.
	 *
	 * @param ifMatch
	 *            the If-Match or If-None-Match header value (or null)
	 * @param etag
	 *            the entity tag
	 * @return true iff the values match
	 */
	public static boolean matches(String ifMatch, String etag) {
		if (ifMatch == null) {
			return false;
		}

		if (ifMatch.equals("*")) {
			return true;
		}

		for (String s : ifMatch.split(" *, *")) {
			if (s.equals(etag)) {
				return true;
			}
		}

		return false;
	}

	private static String weakETag(String value) {
		return "W/\"" + value + "\"";
	}
}
