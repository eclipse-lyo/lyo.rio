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
package org.eclipse.lyo.oslc4j.changemanagement.test;

import java.net.URISyntaxException;

import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

public class TestChangeManagementRdfXml
       extends TestBase
{
    public TestChangeManagementRdfXml()
    {
        super();
    }

    public void testResourceShape()
           throws URISyntaxException
    {
        testResourceShape(OslcMediaType.APPLICATION_RDF_XML);
    }

    public void testCreate()
           throws URISyntaxException
    {
        testCreate(OslcMediaType.APPLICATION_RDF_XML);
    }

    public void testRetrieve()
           throws URISyntaxException
    {
        testRetrieve(OslcMediaType.APPLICATION_RDF_XML);
    }

    public void testRetrieves()
           throws URISyntaxException
    {
        testRetrieves(OslcMediaType.APPLICATION_RDF_XML);
    }

    public void testCompact()
           throws URISyntaxException
    {
        testCompact(OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML,
                    OslcMediaType.APPLICATION_RDF_XML);
    }

    public void testUpdate()
           throws URISyntaxException
    {
        testUpdate(OslcMediaType.APPLICATION_RDF_XML);
    }

    public void testDelete()
    {
        testDelete(OslcMediaType.APPLICATION_RDF_XML);
    }
}