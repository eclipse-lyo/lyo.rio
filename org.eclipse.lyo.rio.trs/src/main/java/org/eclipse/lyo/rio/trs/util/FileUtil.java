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
 *    Ernest Mah - Initial implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.eclipse.lyo.core.utils.marshallers.OSLC4JContext;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JMarshaller;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;


public class FileUtil {
	
	public static Object[] fileload(final String fileName, List<Class<?>> objectTypes)
			throws DatatypeConfigurationException, FileNotFoundException,
			IllegalAccessException, IllegalArgumentException,
			InstantiationException, InvocationTargetException,
			OslcCoreApplicationException, URISyntaxException,
			SecurityException, NoSuchMethodException {

		final File file = new File(fileName);

		if ((file.exists()) && (file.isFile()) && (file.canRead())) {
			  final Model model = FileManager.get().loadModel(fileName);

			  ArrayList<Object> objects = new ArrayList<Object>();
			  
			  for (Class<?> objectType : objectTypes) {
				  Object[] objectResources = JenaModelHelper.fromJenaModel(model,
							objectType);
				  objects.addAll(Arrays.asList(objectResources));
			  }

			return objects.toArray();
		}

		return null;
	}
	
	public static void save(Object[] objects, final String fileName) {
		
		OSLC4JContext context = OSLC4JContext.newInstance();
		OSLC4JMarshaller marshaller = context.createMarshaller();
		try {
			marshaller.marshal(objects, new FileOutputStream(fileName));
		} catch (WebApplicationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
