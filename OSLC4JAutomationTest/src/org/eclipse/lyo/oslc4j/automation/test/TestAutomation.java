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
package org.eclipse.lyo.oslc4j.automation.test;

import java.net.URISyntaxException;

import org.eclipse.lyo.oslc4j.automation.AutomationResource;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

public abstract class TestQualityManagement<T extends AutomationResource>
       extends TestBase<T>
{
    public TestQualityManagement(Class<T> resourceType)
    {
        super(resourceType);
    }

    public void testResourceShape()
           throws URISyntaxException
    {
        testResourceShape(OslcMediaType.APPLICATION_JSON);
        testResourceShape(OslcMediaType.APPLICATION_RDF_XML);
        testResourceShape(OslcMediaType.APPLICATION_XML);
    }

    public void testCreate()
           throws URISyntaxException
    {
        testCreate(OslcMediaType.APPLICATION_JSON);
        testCreate(OslcMediaType.APPLICATION_RDF_XML);
        testCreate(OslcMediaType.APPLICATION_XML);
    }

    public void testRetrieve()
           throws URISyntaxException
    {
        testRetrieve(OslcMediaType.APPLICATION_JSON);
        testRetrieve(OslcMediaType.APPLICATION_RDF_XML);
        testRetrieve(OslcMediaType.APPLICATION_XML);
    }

    public void testRetrieves()
           throws URISyntaxException
    {
        testRetrieves(OslcMediaType.APPLICATION_JSON);
        testRetrieves(OslcMediaType.APPLICATION_RDF_XML);
        testRetrieves(OslcMediaType.APPLICATION_XML);
    }

    public void testCompact()
           throws URISyntaxException
    {
        testCompact(OslcMediaType.APPLICATION_X_OSLC_COMPACT_JSON,
                    OslcMediaType.APPLICATION_JSON);
        testCompact(OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML,
                OslcMediaType.APPLICATION_RDF_XML);
    }

    public void testUpdate()
           throws URISyntaxException
    {
        testUpdate(OslcMediaType.APPLICATION_JSON);
        testUpdate(OslcMediaType.APPLICATION_RDF_XML);
        testUpdate(OslcMediaType.APPLICATION_XML);
}

    public void testDelete()
    {
        testDelete(OslcMediaType.APPLICATION_RDF_XML);
    }
}