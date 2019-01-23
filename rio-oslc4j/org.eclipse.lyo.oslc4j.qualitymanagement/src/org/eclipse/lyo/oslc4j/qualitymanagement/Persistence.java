/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
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
 *     Paul McMahan         - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.qualitymanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.datatype.DatatypeConfigurationException;

import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.util.FileUtils;

public class Persistence
{
    private final static TreeMap<Long, QmResource> RESOURCES_MAP = new TreeMap<Long, QmResource>();

    private static long MAX_IDENTIFIER;

    private Persistence()
    {
        super();
    }

    public static boolean load(final String uriString)
           throws DatatypeConfigurationException,
                  FileNotFoundException,
                  IllegalAccessException,
                  IllegalArgumentException,
                  InstantiationException,
                  InvocationTargetException,
                  OslcCoreApplicationException,
                  URISyntaxException,
                  SecurityException,
                  NoSuchMethodException
    {
        final String fileName = createFileName(uriString);

        final File file = new File(fileName);

        if ((file.exists()) &&
            (file.isFile()) &&
            (file.canRead()))
        {
            final Model model = ModelFactory.createDefaultModel();

            model.read(new FileInputStream(file),
                       null,
                       FileUtils.langXMLAbbrev);
            
            synchronized(RESOURCES_MAP)
            {
                RESOURCES_MAP.clear();
                
	            Class<?>[] qmResourceClasses = new Class[] {TestPlan.class, TestCase.class, TestScript.class, TestExecutionRecord.class, TestResult.class};
	
	            for (Class<?> clazz : qmResourceClasses) {
	            	
		            final Object[] resources = JenaModelHelper.fromJenaModel(model,clazz);

					if (resources != null) {
						for (final Object resource : resources) {
							if (resource instanceof QmResource) {
								final QmResource qmResource = (QmResource) resource;

								final String identifier = qmResource
										.getIdentifier();

								final long longIdentifier = Long
										.parseLong(identifier);

								MAX_IDENTIFIER = Math.max(longIdentifier,
										MAX_IDENTIFIER);

								RESOURCES_MAP.put(Long.valueOf(longIdentifier),
										qmResource);
							}
						}
					}
	            	
	            }
            }

            return true;
        }

        return false;
    }

    public static void save(final String uriString)
           throws URISyntaxException,
                  OslcCoreApplicationException,
                  IllegalArgumentException,
                  DatatypeConfigurationException,
                  IllegalAccessException,
                  InvocationTargetException,
                  FileNotFoundException
    {
        final String fileName = createFileName(uriString);

        final QmResource[] qmResources = getQmResources();

        final Model model = JenaModelHelper.createJenaModel(qmResources);

        if (model != null)
        {
            final RDFWriter writer = model.getWriter(FileUtils.langXMLAbbrev);

            writer.setProperty("showXmlDeclaration", "true");

            writer.write(model, new FileOutputStream(fileName), null);
        }
    }

    private static String createFileName(final String uriString)
            throws URISyntaxException
    {
        final URI uri = new URI(uriString);

        final String host = uri.getHost();
        final int    port = uri.getPort();
        final String path = uri.getPath();

        final String tmpDir = System.getProperty("java.io.tmpdir");

        final String fileName = tmpDir + "/" + host + "_" + port + path.replace('/', '_').replace('\\', '_') + ".xml";

        return fileName;
    }

    public static long getNextIdentifier()
    {
        synchronized(RESOURCES_MAP)
        {
            return ++MAX_IDENTIFIER;
        }
    }
    
    public static QmResource[] getQmResources()
    {
        synchronized (RESOURCES_MAP)
        {
            return RESOURCES_MAP.values().toArray(new QmResource[RESOURCES_MAP.size()]);
        }
    }

    public static <T extends QmResource> List<T> getQmResources(final Class<T> clazz)
    {
    	List<T> resources = new ArrayList<T>();
    	
        synchronized (RESOURCES_MAP)
        {
        	for (QmResource resource : RESOURCES_MAP.values()) {
        		if (clazz.isInstance(resource)) { 
        			resources.add((T)resource);
        		}
        	}
        	return resources;
        }
    }

    public static <T extends QmResource> T getQmResource(final String identifier, final Class<T> clazz)
    {
        synchronized (RESOURCES_MAP)
        {
            return (T) RESOURCES_MAP.get(Long.valueOf(identifier));
        }
    }

    public static void addResource(final QmResource resource)
    {
        synchronized (RESOURCES_MAP)
        {
            RESOURCES_MAP.put(Long.valueOf(resource.getIdentifier()),
                                    resource);
        }
    }

    public static <T extends QmResource> T updateResource(final String        identifier,
                                   final T    resource)
    {
        final Long longIdentifier = Long.valueOf(identifier);

        synchronized (RESOURCES_MAP)
        {
            final QmResource existingResource = RESOURCES_MAP.get(longIdentifier);

            if (existingResource != null)
            {
                RESOURCES_MAP.put(longIdentifier,
                		resource);

                return 
                		resource;
            }
        }

        return null;
    }

    public static <T extends QmResource> T deleteResource(final String identifier)
    {
        synchronized (RESOURCES_MAP)
        {
            return (T) RESOURCES_MAP.remove(Long.valueOf(identifier));
        }
    }
}
