/*!*****************************************************************************
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
package org.eclipse.lyo.rio.oslc4j.cm.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.lyo.rio.oslc4j.cm.Constants;
import org.eclipse.lyo.rio.oslc4j.cm.services.ChangeRequestResource;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryURIs;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.PrefixDefinition;
import org.eclipse.lyo.oslc4j.core.model.Publisher;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RioCmServiceProviderFactory
{
    private final static Logger log = LoggerFactory.getLogger(RioCmServiceProviderFactory.class);
    private static Class<?>[] RESOURCE_CLASSES =
    {
        ChangeRequestResource.class
    };

    private RioCmServiceProviderFactory()
    {
        super();
    }

    static ServiceProvider createServiceProvider(final String baseURI)
           throws OslcCoreApplicationException, URISyntaxException
    {
        final String registryUri = ServiceProviderRegistryURIs.getUIURI();
        log.info("Initialising with a Registry @ {}", registryUri);
        log.debug("Creating a Service Provider @ {}", baseURI);
        final ServiceProvider serviceProvider = ServiceProviderFactory.createServiceProvider(baseURI,
                registryUri, "OSLC Lyo Change Management Service Provider",
                "Reference Implementation OSLC Eclipse Lyo Change Management Service Provider",
                new Publisher("Eclipse Lyo", "urn:oslc:ServiceProvider"), RESOURCE_CLASSES
        );
        URI[] detailsURIs = {new URI(baseURI)};
        serviceProvider.setDetails(detailsURIs);

        final PrefixDefinition[] prefixDefinitions =
        {
            new PrefixDefinition(OslcConstants.DCTERMS_NAMESPACE_PREFIX,             new URI(OslcConstants.DCTERMS_NAMESPACE)),
            new PrefixDefinition(OslcConstants.OSLC_CORE_NAMESPACE_PREFIX,           new URI(OslcConstants.OSLC_CORE_NAMESPACE)),
            new PrefixDefinition(OslcConstants.OSLC_DATA_NAMESPACE_PREFIX,           new URI(OslcConstants.OSLC_DATA_NAMESPACE)),
            new PrefixDefinition(OslcConstants.RDF_NAMESPACE_PREFIX,                 new URI(OslcConstants.RDF_NAMESPACE)),
            new PrefixDefinition(OslcConstants.RDFS_NAMESPACE_PREFIX,                new URI(OslcConstants.RDFS_NAMESPACE)),
            new PrefixDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE_PREFIX,       new URI(Constants.CHANGE_MANAGEMENT_NAMESPACE)),
            new PrefixDefinition(Constants.FOAF_NAMESPACE_PREFIX,                    new URI(Constants.FOAF_NAMESPACE)),
            new PrefixDefinition(Constants.QUALITY_MANAGEMENT_PREFIX,                new URI(Constants.QUALITY_MANAGEMENT_NAMESPACE)),
            new PrefixDefinition(Constants.REQUIREMENTS_MANAGEMENT_PREFIX,           new URI(Constants.REQUIREMENTS_MANAGEMENT_NAMESPACE)),
            new PrefixDefinition(Constants.SOFTWARE_CONFIGURATION_MANAGEMENT_PREFIX, new URI(Constants.SOFTWARE_CONFIGURATION_MANAGEMENT_NAMESPACE))
        };

        serviceProvider.setPrefixDefinitions(prefixDefinitions);

        return serviceProvider;
    }
}
