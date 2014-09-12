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
package org.eclipse.lyo.oslc4j.automation.test;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.wink.client.ClientResponse;
import org.eclipse.lyo.oslc4j.automation.AutomationConstants;
import org.eclipse.lyo.oslc4j.automation.AutomationResource;
import org.eclipse.lyo.oslc4j.client.OslcRestClient;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryClient;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.CreationFactory;
import org.eclipse.lyo.oslc4j.core.model.QueryCapability;
import org.eclipse.lyo.oslc4j.core.model.ResourceShape;
import org.eclipse.lyo.oslc4j.core.model.Service;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;
import org.eclipse.lyo.oslc4j.provider.json4j.Json4JProvidersRegistry;

public abstract class TestBase<T extends AutomationResource>
       extends TestCase
{
    private static final Set<Class<?>> PROVIDERS = new HashSet<Class<?>>();

    static
    {
        PROVIDERS.addAll(JenaProvidersRegistry.getProviders());
        PROVIDERS.addAll(Json4JProvidersRegistry.getProviders());
    }

    private static URI CREATED_AUTO_RESOURCE_URI;

    private final Class<T> resourceType;
    private final Class<T[]> resourceArrayType;

    @SuppressWarnings("unchecked")
	protected TestBase(Class<T> resourceType)
    {
        super();
        this.resourceType = resourceType;
        this.resourceArrayType =  (Class<T[]>) Array.newInstance(resourceType, 0).getClass();
    }

    protected abstract T getResource();

    protected abstract String getResourceType();


    private static String getCreation(final String mediaType,
                                      final String type)
    {
        final ServiceProvider[] serviceProviders = new ServiceProviderRegistryClient(PROVIDERS, mediaType).getServiceProviders();

        for (final ServiceProvider serviceProvider : serviceProviders)
        {
            final Service[] services = serviceProvider.getServices();

            for (final Service service : services)
            {
                if (AutomationConstants.AUTOMATION_DOMAIN.equals(String.valueOf(service.getDomain())))
                {
                    final CreationFactory[] creationFactories = service.getCreationFactories();

                    for (final CreationFactory creationFactory : creationFactories)
                    {
                        final URI[] resourceTypes = creationFactory.getResourceTypes();

                        for (final URI resourceType : resourceTypes)
                        {
                            if (resourceType.toString().equals(type))
                            {
                                return creationFactory.getCreation().toString();
                            }
                        }
                    }
                }
            }
        }

        fail("Unable to retrieve creation for type '" + type + "'");

        return null;
    }

    private static String getQueryBase(final String mediaType,
                                       final String type)
    {
        final ServiceProvider[] serviceProviders = new ServiceProviderRegistryClient(PROVIDERS, mediaType).getServiceProviders();

        for (final ServiceProvider serviceProvider : serviceProviders)
        {
            final Service[] services = serviceProvider.getServices();

            for (final Service service : services)
            {
                if (AutomationConstants.AUTOMATION_DOMAIN.equals(String.valueOf(service.getDomain())))
                {
                    final QueryCapability[] queryCapabilities = service.getQueryCapabilities();

                    for (final QueryCapability queryCapability : queryCapabilities)
                    {
                        final URI[] resourceTypes = queryCapability.getResourceTypes();

                        for (final URI resourceType : resourceTypes)
                        {
                            if (resourceType.toString().equals(type))
                            {
                                return queryCapability.getQueryBase().toString();
                            }
                        }
                    }
                }
            }
        }

        fail("Unable to retrieve queryBase for type '" + type + "'");

        return null;
    }

    private static ResourceShape getResourceShape(final String mediaType,
                                                  final String type)
    {
        final ServiceProvider[] serviceProviders = new ServiceProviderRegistryClient(PROVIDERS, mediaType).getServiceProviders();

        for (final ServiceProvider serviceProvider : serviceProviders)
        {
            final Service[] services = serviceProvider.getServices();

            for (final Service service : services)
            {
                if (AutomationConstants.AUTOMATION_DOMAIN.equals(String.valueOf(service.getDomain())))
                {
                    final QueryCapability[] queryCapabilities = service.getQueryCapabilities();

                    for (final QueryCapability queryCapability : queryCapabilities)
                    {
                        final URI[] resourceTypes = queryCapability.getResourceTypes();

                        for (final URI resourceType : resourceTypes)
                        {
                            if (resourceType.toString().equals(type))
                            {
                                final URI resourceShape = queryCapability.getResourceShape();

                                if (resourceShape != null)
                                {
                                    final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                                             resourceShape,
                                                                                             mediaType);

                                    return oslcRestClient.getOslcResource(ResourceShape.class);
                                }
                            }
                        }
                    }
                }
            }
        }

        fail("Unable to retrieve resource shape for type '" + type + "'");

        return null;
    }

    private void verifyAutoResource(final String        mediaType,
                                            final AutomationResource autoResource,
                                            final boolean       recurse)
            throws URISyntaxException
    {
        assertNotNull(autoResource);

        final URI    aboutURI           = autoResource.getAbout();
        final Date   createdDate        = autoResource.getCreated();
        final String identifierString   = autoResource.getIdentifier();
        final Date   modifiedDate       = autoResource.getModified();
        final URI[]  rdfTypesURIs       = autoResource.getRdfTypes();
        final URI    serviceProviderURI = autoResource.getServiceProvider();

        assertNotNull(aboutURI);
        assertNotNull(createdDate);
        assertNotNull(identifierString);
        assertNotNull(modifiedDate);
        assertNotNull(rdfTypesURIs);
        assertNotNull(serviceProviderURI);

        assertTrue(aboutURI.toString().endsWith(identifierString));
        assertTrue(modifiedDate.equals(createdDate) || modifiedDate.after(createdDate));

        assertTrue(new HashSet<URI>(Arrays.asList(rdfTypesURIs)).contains(new URI(getResourceType())));

        if (recurse)
        {
            final OslcRestClient aboutOSLCRestClient = new OslcRestClient(PROVIDERS,
                                                                          aboutURI,
                                                                          mediaType);

            verifyAutoResource(mediaType,
                                aboutOSLCRestClient.getOslcResource(resourceType),
                                false);

            final OslcRestClient serviceProviderOSLCRestClient = new OslcRestClient(PROVIDERS,
                                                                                    serviceProviderURI,
                                                                                    mediaType);

            final ServiceProvider serviceProvider = serviceProviderOSLCRestClient.getOslcResource(ServiceProvider.class);

            assertNotNull(serviceProvider);
        }
    }

    private void verifyCompact(final String  mediaType,
                                      final Compact compact)
            throws URISyntaxException
    {
        assertNotNull(compact);

        final URI    aboutURI         = compact.getAbout();
        final String shortTitleString = compact.getShortTitle();
        final String titleString      = compact.getTitle();

        assertNotNull(aboutURI);
        assertNotNull(shortTitleString);
        assertNotNull(titleString);

        final OslcRestClient aboutOSLCRestClient = new OslcRestClient(PROVIDERS,
                                                                      aboutURI,
                                                                      mediaType);

        verifyAutoResource(mediaType,
                            aboutOSLCRestClient.getOslcResource(resourceType),
                            false);
    }

    private static void verifyResourceShape(final ResourceShape resourceShape,
                                            final String        type)
            throws URISyntaxException
    {
        assertNotNull(resourceShape);

        final URI[] describes = resourceShape.getDescribes();
        assertNotNull(describes);
        assertTrue(describes.length > 0);

        if (type != null)
        {
            assertTrue(Arrays.asList(describes).contains(new URI(type)));
        }

        final org.eclipse.lyo.oslc4j.core.model.Property[] properties = resourceShape.getProperties();

        assertNotNull(properties);
        assertTrue(properties.length > 0);

        for (final org.eclipse.lyo.oslc4j.core.model.Property property : properties)
        {
            final String name               = property.getName();
            final URI    propertyDefinition = property.getPropertyDefinition();

            assertNotNull(property.getDescription());
            assertNotNull(name);
            assertNotNull(property.getOccurs());
            assertNotNull(propertyDefinition);
            assertNotNull(property.getTitle());
            assertNotNull(property.getValueType());

            assertTrue("propertyDefinition [" + propertyDefinition.toString() + "], name [" + name + "]",
                       propertyDefinition.toString().endsWith(name));
        }
    }

    protected void testResourceShape(final String mediaType)
              throws URISyntaxException
    {
        final ResourceShape resourceShape = getResourceShape(mediaType,
                                                             getResourceType());

        verifyResourceShape(resourceShape,
                            getResourceType());
    }

    protected void testCompact(final String compactMediaType,
                               final String normalMediaType)
              throws URISyntaxException
    {
        assertNotNull(CREATED_AUTO_RESOURCE_URI);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 CREATED_AUTO_RESOURCE_URI,
                                                                 compactMediaType);

        final Compact compact = oslcRestClient.getOslcResource(Compact.class);

        verifyCompact(normalMediaType,
                      compact);
    }

    protected void testCreate(final String mediaType)
              throws URISyntaxException
    {
        CREATED_AUTO_RESOURCE_URI = null;

        final AutomationResource autoResource = getResource();

        final String creation = getCreation(mediaType,
                                            getResourceType());

        assertNotNull(creation);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 creation,
                                                                 mediaType);

        final AutomationResource addedResource = oslcRestClient.addOslcResource(autoResource);

        verifyAutoResource(mediaType,
        		addedResource,
                            true);

        CREATED_AUTO_RESOURCE_URI = addedResource.getAbout();
    }

    protected void testDelete(final String mediaType)
    {
        assertNotNull(CREATED_AUTO_RESOURCE_URI);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 CREATED_AUTO_RESOURCE_URI,
                                                                 mediaType);

        final ClientResponse clientResponse = oslcRestClient.removeOslcResourceReturnClientResponse();

        assertNotNull(clientResponse);
        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, clientResponse.getStatusCode());

        assertNull(oslcRestClient.getOslcResource(resourceType));

        CREATED_AUTO_RESOURCE_URI = null;
    }

    protected void testRetrieve(final String mediaType)
              throws URISyntaxException
    {
        assertNotNull(CREATED_AUTO_RESOURCE_URI);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 CREATED_AUTO_RESOURCE_URI,
                                                                 mediaType);

        final AutomationResource autoResource = oslcRestClient.getOslcResource(resourceType);

        verifyAutoResource(mediaType,
                            autoResource,
                            true);
    }

    protected void testRetrieves(final String mediaType)
              throws URISyntaxException
    {
        assertNotNull(CREATED_AUTO_RESOURCE_URI);

        final String queryBase = getQueryBase(mediaType,
                                              getResourceType());

        assertNotNull(queryBase);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 queryBase,
                                                                 mediaType);


        final AutomationResource[] autoResources = oslcRestClient.getOslcResources(resourceArrayType);

        assertNotNull(autoResources);
        assertTrue(autoResources.length > 0);

        boolean found = false;

        for (final AutomationResource autoResource : autoResources)
        {
            verifyAutoResource(mediaType,
                                autoResource,
                                true);

            if (CREATED_AUTO_RESOURCE_URI.equals(autoResource.getAbout()))
            {
                found = true;
            }
        }

        assertTrue(found);
    }

    protected void testUpdate(final String mediaType)
              throws URISyntaxException
    {
        assertNotNull(CREATED_AUTO_RESOURCE_URI);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 CREATED_AUTO_RESOURCE_URI,
                                                                 mediaType);

        final AutomationResource autoResource = oslcRestClient.getOslcResource(resourceType);

        verifyAutoResource(mediaType,
                            autoResource,
                            true);

        String updatedTitle = "title " + System.currentTimeMillis();
        autoResource.setTitle(updatedTitle);

        final ClientResponse clientResponse = oslcRestClient.updateOslcResourceReturnClientResponse(autoResource);

        assertNotNull(clientResponse);
        assertEquals(HttpURLConnection.HTTP_OK, clientResponse.getStatusCode());

        final AutomationResource updatedAutoResource = oslcRestClient.getOslcResource(resourceType);

        verifyAutoResource(mediaType,
                            updatedAutoResource,
                            true);

        assertEquals(autoResource.getAbout(), updatedAutoResource.getAbout());
        assertEquals(updatedTitle, updatedAutoResource.getTitle());
        assertFalse(autoResource.getModified().equals(updatedAutoResource.getModified()));
        assertTrue(updatedAutoResource.getModified().after(updatedAutoResource.getCreated()));
    }
}
