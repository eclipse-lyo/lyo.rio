/*!*****************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation.
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
package org.eclipse.lyo.rio.oslc4j.cm.servlet;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryClient;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;
import org.eclipse.lyo.rio.oslc4j.cm.util.Persistence;
import org.eclipse.lyo.rio.oslc4j.cm.util.Populate;
import org.eclipse.lyo.rio.oslc4j.cm.util.RioCmServiceProviderFactory;
import org.eclipse.lyo.rio.oslc4j.cm.util.ServiceProviderSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletListener
       implements ServletContextListener
{
    private final static Logger log = LoggerFactory.getLogger(ServletListener.class);
    private static final String PROPERTY_URI = ServletListener.class.getPackage().getName() + ".uri";

    //    private static final String PROPERTY_PORT   = ServletListener.class.getPackage().getName() + ".port";
//    private static final String PROPERTY_SCHEME = ServletListener.class.getPackage().getName() + ".scheme";
//    private static final String SERVICE_PATH    = "/services"; //root path for the JAX-RS services
//    private static final String HOST = getHost();

    private static final int REGISTRATION_DELAY = 5000; //Delay before contacting OSLC4JRegistry

    private ServiceProviderRegistryClient client;

    public ServletListener()
    {
        super();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        final String basePath = generateBasePath(servletContextEvent.getServletContext());

        if (client != null) {
            // always deregister
            try {
                client.deregisterServiceProvider(ServiceProviderSingleton.getServiceProviderURI());
            } catch (final Exception exception) {
                log.error("Unable to deregister with service provider catalog", exception);
            }
            finally {
                client = null;
                ServiceProviderSingleton.setServiceProviderURI(null);
            }

            /*//Don't try to deregister if catalog is on same HOST as us.
        	//TODOx:  Use a regex that accounts for port as well.
        	if (! client.getClient().getUri().contains(HOST)) {

        	}*/
        }

        try {
            Persistence.save(basePath);
        } catch (final Exception exception) {
            log.error("Unable to save", exception);
        }
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent)
    {
        final String basePath = generateBasePath(servletContextEvent.getServletContext());
        log.info("Base path of the RIO/OSLC4j CM adaptor is {}", basePath);
        // TODO Andrew@2019-02-01: rename
        String servletUrlPattern = "services/";
        log.info("Servlet path of the RIO/OSLC4j CM adaptor is {}", servletUrlPattern);

        try {
            OSLC4JUtils.setPublicURI(basePath);
            OSLC4JUtils.setServletPath(servletUrlPattern);
        } catch (MalformedURLException e) {
            log.error("servletListner encountered MalformedURLException.", e);
        } catch (IllegalArgumentException e) {
            log.error("servletListner encountered IllegalArgumentException.", e);
        }

        Timer timer = new Timer();
        timer.schedule(new RegistrationTask(basePath), REGISTRATION_DELAY);

    }

    private static String generateBasePath(final ServletContext servletContext)
    {
        String baseUri = getSystemProperty(PROPERTY_URI);
        if (baseUri == null) {
            baseUri = getInitParameter(servletContext, PROPERTY_URI);
        }

        return UriBuilder.fromUri(baseUri).path(servletContext.getContextPath()).build().toASCIIString();
    }

    private static String getInitParameter(final ServletContext servletContext, final String parameterName) {
        final String parameterValue = servletContext.getInitParameter(parameterName);
        log.debug("Context init parameter {}={}", parameterName, parameterValue);
        return parameterValue;
    }

    private static String getSystemProperty(final String propertyName) {
        final String propertyValue = System.getProperty(propertyName);
        log.debug("System property {}={}", propertyName, propertyValue);
        return propertyValue;
    }

    private class RegistrationTask extends TimerTask {
        String basePath;

        RegistrationTask(String basePath) {
            this.basePath = basePath;
        }

        @Override
        public void run() {
            final URI serviceProviderURI;
            try {
                final ServiceProvider serviceProvider = RioCmServiceProviderFactory.createServiceProvider(basePath);
                log.debug("Registering Service Provider {} ({})", serviceProvider.getIdentifier(),
                        serviceProvider.getAbout()
                );
                client = new ServiceProviderRegistryClient(JenaProvidersRegistry.getProviders());

                serviceProviderURI = client.registerServiceProvider(serviceProvider);
                log.debug("Registered URI: {} (id={})", serviceProviderURI, serviceProvider.getIdentifier());

                ServiceProviderSingleton.setServiceProviderURI(serviceProviderURI);

                log.info("Service provider registration complete.");
            } catch (final Exception exception) {
                client = null;

                log.error("Unable to register with service provider catalog", exception);

                return;
            }

            try {
                final Populate populate = new Populate(basePath, serviceProviderURI);

                if (Persistence.load(basePath)) {
                    // References to ServiceProvider have to be updated
                    populate.fixup();
                } else {
                    populate.populate();
                }
            } catch (final Exception exception) {
                log.error("Unable to load", exception);
            }

        }

    }
}
