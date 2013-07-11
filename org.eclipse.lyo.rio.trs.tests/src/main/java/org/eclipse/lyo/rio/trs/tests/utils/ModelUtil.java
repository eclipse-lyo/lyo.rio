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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ModelUtil {
	/**
	 * @param bytes
	 *            Serialized RDF/XML model
	 * @return Deserialized RDF model
	 * @throws JenaException
	 */
	static public Model deserializeModel(byte[] bytes) throws JenaException {
		if (bytes == null)
			throw new IllegalArgumentException(
					Messages.getServerString("model.util.bytes.null")); //$NON-NLS-1$

		return deserializeModel(new ByteArrayInputStream(bytes));
	}

	/**
	 * @param bytes
	 *            Serialized RDF/XML model
	 * @param lang
	 *            See
	 *            {@link com.hp.hpl.jena.rdf.model.Model#read(java.io.InputStream, String, String)}
	 * @return Deserialized RDF model
	 * @throws JenaException
	 */
	static public Model deserializeModel(byte[] bytes, String lang) throws JenaException {
		if (bytes == null)
			throw new IllegalArgumentException(
					Messages.getServerString("model.util.bytes.null")); //$NON-NLS-1$

		return deserializeModel(new ByteArrayInputStream(bytes), lang);
	}

	/**
	 * @param in
	 *            Serialized RDF/XML model
	 * @return Deserialized RDF model
	 * @throws JenaException
	 */
	static public Model deserializeModel(InputStream in) throws JenaException {
		return deserializeModel(in, null);
	}

	/**
	 * @param in
	 *            Serialized RDF model
	 * @param lang
	 *            See
	 *            {@link com.hp.hpl.jena.rdf.model.Model#read(java.io.InputStream, String, String)}
	 * @return Deserialized RDF model
	 * @throws JenaException
	 */
	static public Model deserializeModel(InputStream in, String lang) throws JenaException {
		if (in == null)
			throw new IllegalArgumentException(
					Messages.getServerString("model.util.inputstream.null")); //$NON-NLS-1$

		Model model = ModelUtil.createDefaultModel();

		try {
			model.read(in, null, lang);
		} catch (Throwable t) {
			// Temp ignore - TODO ignore empty stream specifically
			return model;
		}

		return model;
	}

	/**
	 * @param model
	 *            Input model
	 * @return Serialized RDF/XML model
	 * @throws JenaException
	 */
	static public byte[] serializeModel(Model model) throws JenaException {
		return serializeModel(model, (String) null);
	}

	/**
	 * @param model
	 *            Input Model
	 * @param lang
	 *            See
	 *            {@link com.hp.hpl.jena.rdf.model.Model#read(java.io.InputStream, String, String)}
	 * @return Serialized model
	 * @throws JenaException
	 */
	static public byte[] serializeModel(Model model, String lang) throws JenaException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		serializeModel(model, lang, out);
		return out.toByteArray();
	}

	/**
	 * @param model
	 *            Input model
	 * @param out
	 *            Serialized RDF/XML model
	 * @throws JenaException
	 */
	static public void serializeModel(Model model, OutputStream out) throws JenaException {
		serializeModel(model, null, out);
	}

	/**
	 * @param model
	 *            Input Model
	 * @param lang
	 *            See
	 *            {@link com.hp.hpl.jena.rdf.model.Model#read(java.io.InputStream, String, String)}
	 * @param out
	 *            Serialized model
	 * @throws JenaException
	 */
	static public void serializeModel(Model model, String lang, OutputStream out)
		throws JenaException {
		if (model == null)
			throw new IllegalArgumentException(
					Messages.getServerString("model.util.model.null")); //$NON-NLS-1$
		if (out == null)
			throw new IllegalArgumentException(
					Messages.getServerString("model.util.outputstream.null")); //$NON-NLS-1$
		// Avoid 'WARN com.hp.hpl.jena.xmloutput.impl.BaseXMLWriter - Namespace prefix 'j.1' is
		// reserved by Jena.'
		for (String prefix : new HashSet<String>(model.getNsPrefixMap().keySet())) {
			model.removeNsPrefix(prefix);
		}
		model.write(out, lang);
	}

	public static Model getLocalModel(String path) {
		InputStream in = ModelUtil.class.getResourceAsStream(path);
		Model model = ModelUtil.createDefaultModel();
		model.read(in, null, FileUtils.guessLang(path, FileUtils.langXML));
		return model;
	}

	public static String getVocabularyTitle(Model vocabulary, String uri) {
		Resource nsResource = vocabulary.createResource(uri);
		if (nsResource.hasProperty(DCTerms.title)) {
			return nsResource.getProperty(DCTerms.title).getString();
		} else if (nsResource.hasProperty(DC.title)) {
			return nsResource.getProperty(DC.title).getString();
		} else if (nsResource.hasProperty(RDFS.label))
			return nsResource.getProperty(RDFS.label).getString();
		return null;
	}

	/*
	 * This is a utility method to workaround WAS classloading issues with jena
	 */
	public static Model createDefaultModel() {
		Model model = null;
		// save the current thread class loader (TCCL)
		ClassLoader ctxClassloader = Thread.currentThread().getContextClassLoader();
		try {
			// Get the java class loader to allow jena to use xml/xerces dependencies
			Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
			model = ModelFactory.createDefaultModel();
		} finally {
			// put it back the way it was
			Thread.currentThread().setContextClassLoader(ctxClassloader);
		}
		return model;
	}
}
