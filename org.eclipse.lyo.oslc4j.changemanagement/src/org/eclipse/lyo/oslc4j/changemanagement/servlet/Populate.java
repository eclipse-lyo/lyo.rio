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
package org.eclipse.lyo.oslc4j.changemanagement.servlet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.eclipse.lyo.oslc4j.changemanagement.ChangeRequest;
import org.eclipse.lyo.oslc4j.changemanagement.Persistence;
import org.eclipse.lyo.oslc4j.changemanagement.Severity;
import org.eclipse.lyo.oslc4j.changemanagement.Type;

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
        final ChangeRequest[] changeRequests = Persistence.getChangeRequests();

        for (final ChangeRequest changeRequest : changeRequests)
        {
            changeRequest.setServiceProvider(serviceProviderURI);
        }
    }

    public void populate()
           throws URISyntaxException
    {
        persistChangeRequest(createChangeRequest("Unable to execute Apache Tomcat due to missing Java runtime environment (JRE).",
                                                 Severity.Blocker,
                                                 "Apache Tomcat requires JRE"));

        persistChangeRequest(createChangeRequest("Operating system password required to be alphanumeric with at least eight characters.",
                                                 Severity.Critical,
                                                 "Password complexity rules"));

        persistChangeRequest(createChangeRequest("Misspelling in error message in XYZZY product.",
                                                 Severity.Minor,
                                                 "Error message mispelling"));
    }

    private static ChangeRequest createChangeRequest(final String   description,
                                                     final Severity severity,
                                                     final String   title)
            throws URISyntaxException
    {
        final ChangeRequest changeRequest = new ChangeRequest();

        changeRequest.setApproved(Boolean.FALSE);
        changeRequest.setClosed(Boolean.FALSE);
        changeRequest.addDctermsType(Type.Defect.toString());
        changeRequest.setDescription(description);
        changeRequest.setFixed(Boolean.FALSE);
        changeRequest.setInProgress(Boolean.FALSE);
        changeRequest.setReviewed(Boolean.FALSE);
        changeRequest.setSeverity(severity.toString());
        changeRequest.setShortTitle(title);
        changeRequest.setStatus("Submitted");
        changeRequest.setTitle(title);
        changeRequest.setVerified(Boolean.FALSE);

        return changeRequest;
    }

    private void persistChangeRequest(final ChangeRequest changeRequest)
            throws URISyntaxException
    {
        final long identifier = Persistence.getNextIdentifier();

        final URI about = new URI(basePath + "/changeRequests/" + identifier);

        changeRequest.setAbout(about);
        changeRequest.setIdentifier(String.valueOf(identifier));
        changeRequest.setServiceProvider(serviceProviderURI);

        final Date date = new Date();

        changeRequest.setCreated(date);
        changeRequest.setModified(date);

        Persistence.addChangeRequest(changeRequest);
    }
}
