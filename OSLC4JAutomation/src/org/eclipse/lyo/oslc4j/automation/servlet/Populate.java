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
 *     Michael Fiedler         - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation.servlet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.eclipse.lyo.oslc4j.automation.Persistence;
import org.eclipse.lyo.oslc4j.automation.AutomationResource;
import org.eclipse.lyo.oslc4j.automation.AutomationPlan;

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
        final AutomationResource[] autoResources = Persistence.getAutoResources();

        for (final AutomationResource autoResource : autoResources)
        {
        	autoResource.setServiceProvider(serviceProviderURI);
        }
    }

    public void populate()
           throws URISyntaxException
    {
    	AutomationPlan autoPlan = createAutomationPlan("Lyo Rio Automation Plan", "This is a sample OSLC Automation Plan.", "autoPlans");
    	Persistence.addResource(autoPlan);
    }

    private AutomationPlan createAutomationPlan (final String   title,
                                                 final String   description,
                                                 final String path)
            throws URISyntaxException
    {
        final AutomationPlan autoPlan = new AutomationPlan();
        
        final long identifier = Persistence.getNextIdentifier();

        final URI about = new URI(basePath + "/" + path + "/" + identifier);

        autoPlan.setAbout(about);
        autoPlan.setIdentifier(String.valueOf(identifier));
        autoPlan.setServiceProvider(serviceProviderURI);
        autoPlan.setTitle(title);
        autoPlan.setDescription(description);

        final Date date = new Date();
        autoPlan.setCreated(date);
        autoPlan.setModified(date);
        
        return autoPlan;
    }
    
}
