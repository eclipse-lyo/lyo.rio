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
 *     Paul McMahan <pmcmahan@us.ibm.com>        - initial implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation.resources;

import java.net.URI;
import java.util.Set;
import org.eclipse.lyo.oslc4j.client.OslcRestClient;
import org.eclipse.lyo.oslc4j.core.model.CreationFactory;
import org.eclipse.lyo.oslc4j.core.model.Service;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;

public class AutomationUtils {

	public static URI getCreation(Set<Class<?>> PROVIDERS, URI serviceProvider, URI targetDomain, URI targetResourceType) 
	{
    	OslcRestClient spClient = new OslcRestClient(PROVIDERS,serviceProvider,"application/rdf+xml");
    	ServiceProvider sp = spClient.getOslcResource(ServiceProvider.class);
    	if (sp != null) 
    	{
    		for (Service svc : sp.getServices())
    		{
    			if (svc.getDomain().equals(targetDomain))
    			{
    				for (CreationFactory cf : svc.getCreationFactories())
    				{
    					for (URI resourceType : cf.getResourceTypes())
    					{
	    					if (resourceType.equals(targetResourceType))
	    					{   						
	    						return cf.getCreation();
	    					
	    					}
    					}
    				}
    			}
    		}
    	}
    	return null;
	}
}
