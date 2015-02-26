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
package org.eclipse.lyo.oslc.v3.sample.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.jena.riot.Lang;

import com.hp.hpl.jena.rdf.model.Model;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.lyo.oslc.v3.sample.MediaTypeContants.APPLICATION_JSON_LD;
import static org.eclipse.lyo.oslc.v3.sample.MediaTypeContants.TEXT_TURTLE;

@Provider
@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
public class ModelMessageBodyWriter implements MessageBodyWriter<Model> {
	@Override
	public boolean isWriteable(Class<?> type,
							   Type genericType,
							   Annotation[] annotations,
							   MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(Model t,
						Class<?> type,
						Type genericType,
						Annotation[] annotations,
						MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Model model,
						Class<?> type,
						Type genericType,
						Annotation[] annotations,
						MediaType mediaType,
						MultivaluedMap<String, Object> httpHeaders,
						OutputStream entityStream) throws IOException,
			WebApplicationException {
		final Lang lang;
		if (mediaType.isCompatible(MediaType.valueOf("text/turtle"))) {
			lang = Lang.TURTLE;
		} else {
			lang = Lang.JSONLD;
		}

		model.write(entityStream, lang.getName(), "");
	}
}
