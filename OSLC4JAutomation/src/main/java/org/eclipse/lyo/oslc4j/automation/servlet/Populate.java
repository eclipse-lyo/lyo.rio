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

import org.eclipse.lyo.oslc4j.automation.AutomationConstants;
import org.eclipse.lyo.oslc4j.automation.AutomationRequest;
import org.eclipse.lyo.oslc4j.automation.AutomationResult;
import org.eclipse.lyo.oslc4j.automation.ParameterInstance;
import org.eclipse.lyo.oslc4j.automation.Persistence;
import org.eclipse.lyo.oslc4j.automation.AutomationResource;
import org.eclipse.lyo.oslc4j.automation.AutomationPlan;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.Property;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

final class Populate
{
    private final String basePath;
    private final URI    serviceProviderURI;
    
    private static final String REMOTE_COMMAND_PLAN = "Remote Command Automation Plan";

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
    	AutomationPlan autoPlan = createAutomationPlan(REMOTE_COMMAND_PLAN, 
    			                                       "This automation plan will attempt to execute the system command provided " +
    			                                       "in the command parameter of the Automation request",
    			                                       "autoPlans");
    	Persistence.addResource(autoPlan);
    	
    	AutomationRequest autoRequest = createAutomationRequest("Sample Remote Command Automation Request",
    				                                            "A sample automation request for copy/paste - adjust all URLs accordingly",
    				                                            "autoRequests",
    				                                            autoPlan);
    	Persistence.addResource(autoRequest);
    	
    	AutomationResult autoResult = createAutomationResult("Sample Automation Result",
    													     "A sample automation result - does not represent an actual execution",
    													     "autoResults",
    													     autoPlan,
    													     autoRequest);
    	Persistence.addResource(autoResult);
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
        
        Property param1 = new Property("command", Occurs.ExactlyOne, new URI(AutomationConstants.AUTOMATION_NAMESPACE + "parameterDefinition"), ValueType.String);
        autoPlan.addParameterDefinition(param1);
        

        final Date date = new Date();
        autoPlan.setCreated(date);
        autoPlan.setModified(date);
        
        return autoPlan;
    }
	
	private AutomationRequest createAutomationRequest(final String title,
			                                          final String description,
			                                          final String path,
			                                          final AutomationPlan autoPlan) throws URISyntaxException
	{
		final AutomationRequest autoRequest = new AutomationRequest();
		
		final long identifier = Persistence.getNextIdentifier();

        final URI about = new URI(basePath + "/" + path + "/" + identifier);

        autoRequest.setAbout(about);
        autoRequest.setIdentifier(String.valueOf(identifier));
        autoRequest.setServiceProvider(serviceProviderURI);
        autoRequest.setTitle(title);
        autoRequest.setDescription(description);
        
        final ParameterInstance param1 = new ParameterInstance();
        param1.setName("command");
        param1.setValue("ls");
        
        autoRequest.addInputParameter(param1);
        
        autoRequest.setExecutesAutomationPlan(new Link(autoPlan.getAbout()));
        
        final Date date = new Date();
        autoRequest.setCreated(date);
        autoRequest.setModified(date);
        
        return autoRequest;
	}
	
	private AutomationResult createAutomationResult(final String title,
            final String description,
            final String path,
            final AutomationPlan autoPlan,
            final AutomationRequest autoRequest) throws URISyntaxException
	{
		final AutomationResult autoResult = new AutomationResult();
		
		final long identifier = Persistence.getNextIdentifier();
		
		final URI about = new URI(basePath + "/" + path + "/" + identifier);
		
		autoResult.setAbout(about);
		autoResult.setIdentifier(String.valueOf(identifier));
		autoResult.setServiceProvider(serviceProviderURI);
		autoResult.setTitle(title);

		
		final ParameterInstance param1 = new ParameterInstance();
		param1.setName("command");
		param1.setValue("ls");
		
		autoResult.addInputParameter(param1);
		
		autoResult.setReportsOnAutomationPlan(new Link(autoPlan.getAbout()));
		autoResult.setProducedByAutomationRequest(new Link(autoRequest.getAbout()));
		
		final Date date = new Date();
		autoResult.setCreated(date);
		autoResult.setModified(date);
		
		return autoResult;
	}
	
    
}
