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
package org.eclipse.lyo.oslc4j.qualitymanagement.servlet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.eclipse.lyo.oslc4j.qualitymanagement.Persistence;
import org.eclipse.lyo.oslc4j.qualitymanagement.QmResource;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestPlan;

final class Populate
{
    private final String basePath;
    private final URI    serviceProviderURI;

    public Populate(final String  basePath,
                    final URI     serviceProviderURI)
    {
        super();

        this.basePath           = basePath;
        this.serviceProviderURI = serviceProviderURI;
    }

    public void fixup()
    {
        final QmResource[] qmResources = Persistence.getQmResources();

        for (final QmResource qmResource : qmResources)
        {
        	qmResource.setServiceProvider(serviceProviderURI);
        }
    }

    public void populate()
           throws URISyntaxException
    {
    	TestPlan testPlan = createTestPlan("Lyo Rio Test Plan", "This test plan will focus on testing the reference implementations of the OSLC domains.", "testPlans");
    	Persistence.addResource(testPlan);
    }

    private TestPlan createTestPlan (final String   title,
                                     final String   description,
                                     final String path)
            throws URISyntaxException
    {
        final TestPlan testPlan = new TestPlan();
        
        final long identifier = Persistence.getNextIdentifier();

        final URI about = new URI(basePath + "/" + path + "/" + identifier);

        testPlan.setAbout(about);
        testPlan.setIdentifier(String.valueOf(identifier));
        testPlan.setServiceProvider(serviceProviderURI);
        testPlan.setTitle(title);
        testPlan.setDescription(description);

        final Date date = new Date();
        testPlan.setCreated(date);
        testPlan.setModified(date);
        
        return testPlan;
    }
    
}
