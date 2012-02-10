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
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.changemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.TreeMap;

import javax.xml.datatype.DatatypeConfigurationException;

import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.util.FileUtils;

public class Persistence
{
    private final static TreeMap<Long, ChangeRequest> CHANGE_REQUESTS_MAP = new TreeMap<Long, ChangeRequest>();

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
                  URISyntaxException
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

            final Object[] resources = JenaModelHelper.fromJenaModel(model,
                                                                     ChangeRequest.class);

            synchronized(CHANGE_REQUESTS_MAP)
            {
                CHANGE_REQUESTS_MAP.clear();

                if (resources != null)
                {
                    for (final Object resource : resources)
                    {
                        if (resource instanceof ChangeRequest)
                        {
                            final ChangeRequest changeRequest = (ChangeRequest) resource;

                            final String identifier = changeRequest.getIdentifier();

                            final long longIdentifier = Long.parseLong(identifier);

                            MAX_IDENTIFIER = Math.max(longIdentifier, MAX_IDENTIFIER);

                            CHANGE_REQUESTS_MAP.put(Long.valueOf(longIdentifier),
                                                    changeRequest);
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

        final ChangeRequest[] changeRequests = getChangeRequests();

        final Model model = JenaModelHelper.createJenaModel(changeRequests);

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
        synchronized(CHANGE_REQUESTS_MAP)
        {
            return ++MAX_IDENTIFIER;
        }
    }

    public static ChangeRequest[] getChangeRequests()
    {
        synchronized (CHANGE_REQUESTS_MAP)
        {
            return CHANGE_REQUESTS_MAP.values().toArray(new ChangeRequest[CHANGE_REQUESTS_MAP.size()]);
        }
    }

    public static ChangeRequest getChangeRequest(final String identifier)
    {
        synchronized (CHANGE_REQUESTS_MAP)
        {
            return CHANGE_REQUESTS_MAP.get(Long.valueOf(identifier));
        }
    }

    public static void addChangeRequest(final ChangeRequest changeRequest)
    {
        synchronized (CHANGE_REQUESTS_MAP)
        {
            CHANGE_REQUESTS_MAP.put(Long.valueOf(changeRequest.getIdentifier()),
                                    changeRequest);
        }
    }

    public static ChangeRequest updateChangeRequest(final String        identifier,
                                                    final ChangeRequest changeRequest)
    {
        final Long longIdentifier = Long.valueOf(identifier);

        synchronized (CHANGE_REQUESTS_MAP)
        {
            final ChangeRequest existingChangeRequest = CHANGE_REQUESTS_MAP.get(longIdentifier);

            if (existingChangeRequest != null)
            {
                CHANGE_REQUESTS_MAP.put(longIdentifier,
                                        changeRequest);

                return changeRequest;
            }
        }

        return null;
    }

    public static ChangeRequest deleteChangeRequest(final String identifier)
    {
        synchronized (CHANGE_REQUESTS_MAP)
        {
            return CHANGE_REQUESTS_MAP.remove(Long.valueOf(identifier));
        }
    }
}
