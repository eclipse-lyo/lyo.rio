package org.eclipse.lyo.oslc4j.automation;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lyo.oslc4j.client.OslcRestClient;
import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryClient;
import org.eclipse.lyo.oslc4j.core.model.CreationFactory;
import org.eclipse.lyo.oslc4j.core.model.QueryCapability;
import org.eclipse.lyo.oslc4j.core.model.ResourceShape;
import org.eclipse.lyo.oslc4j.core.model.Service;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;
import org.eclipse.lyo.oslc4j.provider.json4j.Json4JProvidersRegistry;

public class ServiceProviderHelper {

	protected static final Set<Class<?>> PROVIDERS = new HashSet<Class<?>>();

	static {
		PROVIDERS.addAll(JenaProvidersRegistry.getProviders());
		PROVIDERS.addAll(Json4JProvidersRegistry.getProviders());
	}

	protected static String getCreation(final String mediaType,
			final String domain, final String type) {
		final ServiceProvider[] serviceProviders = new ServiceProviderRegistryClient(
				PROVIDERS, mediaType).getServiceProviders();

		for (final ServiceProvider serviceProvider : serviceProviders) {
			final Service[] services = serviceProvider.getServices();

			for (final Service service : services) {
				if (domain.equals(String.valueOf(service.getDomain()))) {
					final CreationFactory[] creationFactories = service
							.getCreationFactories();

					for (final CreationFactory creationFactory : creationFactories) {
						final URI[] resourceTypes = creationFactory
								.getResourceTypes();

						for (final URI resourceType : resourceTypes) {
							if (resourceType.toString().equals(type)) {
								return creationFactory.getCreation().toString();
							}
						}
					}
				}
			}
		}

		return null;
	}

	protected static String getQueryBase(final String mediaType,
			final String desiredUsage, final String type) {
		final ServiceProvider[] serviceProviders = new ServiceProviderRegistryClient(
				PROVIDERS, mediaType).getServiceProviders();

		for (final ServiceProvider serviceProvider : serviceProviders) {
			final Service[] services = serviceProvider.getServices();

			for (final Service service : services) {
				if (AutomationConstants.AUTOMATION_DOMAIN.equals(String.valueOf(service
						.getDomain()))) {
					final QueryCapability[] queryCapabilities = service
							.getQueryCapabilities();

					for (final QueryCapability queryCapability : queryCapabilities) {
						final URI[] resourceTypes = queryCapability
								.getResourceTypes();
						final URI[] usages = queryCapability.getUsages();

						boolean usageFound = false;

						for (final URI usage : usages) {
							if (usage.toString().equals(desiredUsage)) {
								usageFound = true;

								break;
							}
						}

						if (usageFound) {
							for (final URI resourceType : resourceTypes) {
								if (resourceType.toString().equals(type)) {
									return queryCapability.getQueryBase()
											.toString();
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	protected static ResourceShape getResourceShape(final String mediaType,
			final String type) {
		final ServiceProvider[] serviceProviders = new ServiceProviderRegistryClient(
				PROVIDERS, mediaType).getServiceProviders();

		for (final ServiceProvider serviceProvider : serviceProviders) {
			final Service[] services = serviceProvider.getServices();

			for (final Service service : services) {
				if (AutomationConstants.AUTOMATION_DOMAIN.equals(String.valueOf(service
						.getDomain()))) {
					final QueryCapability[] queryCapabilities = service
							.getQueryCapabilities();

					for (final QueryCapability queryCapability : queryCapabilities) {
						final URI[] resourceTypes = queryCapability
								.getResourceTypes();

						for (final URI resourceType : resourceTypes) {
							if (resourceType.toString().equals(type)) {
								final URI resourceShape = queryCapability
										.getResourceShape();

								if (resourceShape != null) {
									final OslcRestClient oslcRestClient = new OslcRestClient(
											PROVIDERS, resourceShape, mediaType);

									return oslcRestClient
											.getOslcResource(ResourceShape.class);
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

}
