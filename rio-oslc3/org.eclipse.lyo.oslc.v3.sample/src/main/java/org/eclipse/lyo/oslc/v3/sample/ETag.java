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

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.jena.rdf.model.Model;

public class ETag {
	private static final Logger logger = LogManager.getLogger(ETag.class);

	/**
	 * Create an ETag value from a Jena model.
	 *
	 * @param m the model that represents the HTTP response body
	 * @param lang the serialization language
	 * @param base the base URI
	 * @return an ETag value
	 * @throws IOException on I/O errors
	 *
	 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">HTTP 1.1: Section 14.19 - ETag</a>
	 */
	public static String generate(final Model m,
								  final String lang,
								  final String base) throws IOException {
		final PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out = new PipedOutputStream(in);
		new Thread(new Runnable() {
			public void run() {
				m.write(out, lang, base);
				try {
					out.close();
				} catch (IOException e) {
					logger.error("Error creating MD5 hash of Model", e);
				}
			}
		}).start();

		return generate(in);
	}

	public static String generate(final JsonObject object) throws IOException {
		final PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out = new PipedOutputStream(in);
		new Thread(new Runnable() {
			public void run() {
				object.output(new IndentedWriter(out));
				try {
					out.close();
				} catch (IOException e) {
					logger.error("Error creating MD5 hash of JSON", e);
				}
			}
		}).start();

		return generate(in);
	}

	private static String generate(final InputStream in) throws IOException {
		final String md5 = DigestUtils.md5Hex(in);
		return quote(md5);
	}

	public static String quote(String etag) {
		// ETags are quoted strings
		return "\"" + etag + "\"";
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
}
