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

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.wink.client.ClientResponse;
import org.eclipse.lyo.oslc4j.changemanagement.ChangeRequest;
import org.eclipse.lyo.oslc4j.changemanagement.Constants;
import org.eclipse.lyo.oslc4j.changemanagement.Severity;
import org.eclipse.lyo.oslc4j.changemanagement.Type;
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

public abstract class TestBase
       extends TestCase
{
    private static final Set<Class<?>> PROVIDERS = new HashSet<Class<?>>();

    static
    {
        PROVIDERS.addAll(JenaProvidersRegistry.getProviders());
        PROVIDERS.addAll(Json4JProvidersRegistry.getProviders());
    }

    private static URI CREATED_CHANGE_REQUEST_URI;

    protected TestBase()
    {
        super();
    }

    private static String getCreation(final String mediaType,
                                      final String type)
    {
        final ServiceProvider[] serviceProviders = new ServiceProviderRegistryClient(PROVIDERS, mediaType).getServiceProviders();

        for (final ServiceProvider serviceProvider : serviceProviders)
        {
            final Service[] services = serviceProvider.getServices();

            for (final Service service : services)
            {
                if (Constants.CHANGE_MANAGEMENT_DOMAIN.equals(String.valueOf(service.getDomain())))
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
                if (Constants.CHANGE_MANAGEMENT_DOMAIN.equals(String.valueOf(service.getDomain())))
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
                if (Constants.CHANGE_MANAGEMENT_DOMAIN.equals(String.valueOf(service.getDomain())))
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

    private static void verifyChangeRequest(final String        mediaType,
                                            final ChangeRequest changeRequest,
                                            final boolean       recurse)
            throws URISyntaxException
    {
        assertNotNull(changeRequest);

        final URI    aboutURI           = changeRequest.getAbout();
        final Date   createdDate        = changeRequest.getCreated();
        final String identifierString   = changeRequest.getIdentifier();
        final Date   modifiedDate       = changeRequest.getModified();
        final URI[]  rdfTypesURIs       = changeRequest.getRdfTypes();
        final URI    serviceProviderURI = changeRequest.getServiceProvider();

        assertNotNull(aboutURI);
        assertNotNull(createdDate);
        assertNotNull(identifierString);
        assertNotNull(modifiedDate);
        assertNotNull(rdfTypesURIs);
        assertNotNull(serviceProviderURI);

        assertTrue(aboutURI.toString().endsWith(identifierString));
        assertTrue(modifiedDate.equals(createdDate) || modifiedDate.after(createdDate));
        assertTrue(new HashSet<URI>(Arrays.asList(rdfTypesURIs)).contains(new URI(Constants.TYPE_CHANGE_REQUEST)));

        if (recurse)
        {
            final OslcRestClient aboutOSLCRestClient = new OslcRestClient(PROVIDERS,
                                                                          aboutURI,
                                                                          mediaType);

            verifyChangeRequest(mediaType,
                                aboutOSLCRestClient.getOslcResource(changeRequest.getClass()),
                                false);

            final OslcRestClient serviceProviderOSLCRestClient = new OslcRestClient(PROVIDERS,
                                                                                    serviceProviderURI,
                                                                                    mediaType);

            final ServiceProvider serviceProvider = serviceProviderOSLCRestClient.getOslcResource(ServiceProvider.class);

            assertNotNull(serviceProvider);
        }
    }

    private static void verifyCompact(final String  mediaType,
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

        verifyChangeRequest(mediaType,
                            aboutOSLCRestClient.getOslcResource(ChangeRequest.class),
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
                                                             Constants.TYPE_CHANGE_REQUEST);

        verifyResourceShape(resourceShape,
                            Constants.TYPE_CHANGE_REQUEST);
    }

    protected void testCompact(final String compactMediaType,
                               final String normalMediaType)
              throws URISyntaxException
    {
        assertNotNull(CREATED_CHANGE_REQUEST_URI);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 CREATED_CHANGE_REQUEST_URI,
                                                                 compactMediaType);

        final Compact compact = oslcRestClient.getOslcResource(Compact.class);

        verifyCompact(normalMediaType,
                      compact);
    }

    protected void testCreate(final String mediaType)
              throws URISyntaxException
    {
        CREATED_CHANGE_REQUEST_URI = null;

        final ChangeRequest changeRequest = new ChangeRequest();

        changeRequest.addContributor(new URI("http://myserver/mycmapp/users/bob"));
        changeRequest.addCreator(new URI("http://myserver/mycmapp/users/bob"));
        changeRequest.addDctermsType(Type.Defect.toString());
        changeRequest.setDescription("Invalid installation instructions indicating invalid patches to be applied.");
        changeRequest.setDiscussedBy(new URI("http://example.com/bugs/2314/discussion"));
        changeRequest.setInstanceShape(new URI("http://example.com/shapes/defect"));
        changeRequest.addRelatedChangeRequest(new URI("http://myserver/mycmapp/bugs/1235"));
        changeRequest.setSeverity(Severity.Major.toString());
        changeRequest.setShortTitle("Bug 2314");
        changeRequest.setStatus("InProgress");
        changeRequest.addSubject("doc");
        changeRequest.addSubject("install");
        changeRequest.setTitle("Invalid installation instructions");
        changeRequest.addTracksRequirement(new URI("http://myserver/reqtool/req/34ef31af"));
        changeRequest.addTracksRequirement(new URI("http://remoteserver/reqrepo/project1/req456"));

        final String creation = getCreation(mediaType,
                                            Constants.TYPE_CHANGE_REQUEST);

        assertNotNull(creation);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 creation,
                                                                 mediaType);

        final ChangeRequest addedChangeRequest = oslcRestClient.addOslcResource(changeRequest);

        verifyChangeRequest(mediaType,
                            addedChangeRequest,
                            true);

        CREATED_CHANGE_REQUEST_URI = addedChangeRequest.getAbout();
    }

    protected void testDelete(final String mediaType)
    {
        assertNotNull(CREATED_CHANGE_REQUEST_URI);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 CREATED_CHANGE_REQUEST_URI,
                                                                 mediaType);

        final ClientResponse clientResponse = oslcRestClient.removeOslcResourceReturnClientResponse();

        assertNotNull(clientResponse);
        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, clientResponse.getStatusCode());

        assertNull(oslcRestClient.getOslcResource(ChangeRequest.class));

        CREATED_CHANGE_REQUEST_URI = null;
    }

    protected void testRetrieve(final String mediaType)
              throws URISyntaxException
    {
        assertNotNull(CREATED_CHANGE_REQUEST_URI);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 CREATED_CHANGE_REQUEST_URI,
                                                                 mediaType);

        final ChangeRequest changeRequest = oslcRestClient.getOslcResource(ChangeRequest.class);

        verifyChangeRequest(mediaType,
                            changeRequest,
                            true);
    }

    protected void testRetrieves(final String mediaType)
              throws URISyntaxException
    {
        assertNotNull(CREATED_CHANGE_REQUEST_URI);

        final String queryBase = getQueryBase(mediaType,
                                              Constants.TYPE_CHANGE_REQUEST);

        assertNotNull(queryBase);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 queryBase,
                                                                 mediaType);

        final ChangeRequest[] changeRequests = oslcRestClient.getOslcResources(ChangeRequest[].class);

        assertNotNull(changeRequests);
        assertTrue(changeRequests.length > 0);

        boolean found = false;

        for (final ChangeRequest changeRequest : changeRequests)
        {
            verifyChangeRequest(mediaType,
                                changeRequest,
                                true);

            if (CREATED_CHANGE_REQUEST_URI.equals(changeRequest.getAbout()))
            {
                found = true;
            }
        }

        assertTrue(found);
    }

    protected void testUpdate(final String mediaType)
              throws URISyntaxException
    {
        assertNotNull(CREATED_CHANGE_REQUEST_URI);

        final OslcRestClient oslcRestClient = new OslcRestClient(PROVIDERS,
                                                                 CREATED_CHANGE_REQUEST_URI,
                                                                 mediaType);

        final ChangeRequest changeRequest = oslcRestClient.getOslcResource(ChangeRequest.class);

        verifyChangeRequest(mediaType,
                            changeRequest,
                            true);

        assertNull(changeRequest.isApproved());
        assertNull(changeRequest.getCloseDate());

        final Date closeDate = new Date();

        changeRequest.setApproved(Boolean.TRUE);
        changeRequest.setCloseDate(closeDate);

        final ClientResponse clientResponse = oslcRestClient.updateOslcResourceReturnClientResponse(changeRequest);

        assertNotNull(clientResponse);
        assertEquals(HttpURLConnection.HTTP_OK, clientResponse.getStatusCode());

        final ChangeRequest updatedChangeRequest = oslcRestClient.getOslcResource(ChangeRequest.class);

        verifyChangeRequest(mediaType,
                            updatedChangeRequest,
                            true);

        assertEquals(changeRequest.getAbout(), updatedChangeRequest.getAbout());
        assertEquals(Boolean.TRUE, updatedChangeRequest.isApproved());
        assertEquals(closeDate, updatedChangeRequest.getCloseDate());
        assertFalse(changeRequest.getModified().equals(updatedChangeRequest.getModified()));
        assertTrue(updatedChangeRequest.getModified().after(updatedChangeRequest.getCreated()));
    }
}