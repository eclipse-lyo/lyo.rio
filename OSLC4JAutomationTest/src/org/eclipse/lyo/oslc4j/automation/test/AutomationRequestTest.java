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
package org.eclipse.lyo.oslc4j.automation.test;

import java.net.URI;



import org.eclipse.lyo.oslc4j.automation.Constants;
import org.eclipse.lyo.oslc4j.automation.AutomationRequest;
import org.eclipse.lyo.oslc4j.automation.ParameterInstance;
import org.eclipse.lyo.oslc4j.automation.State;
import org.eclipse.lyo.oslc4j.core.model.Link;


public class AutomationRequestTest extends TestAutomation<AutomationRequest> {

	public AutomationRequestTest() {
		super(AutomationRequest.class);
	}

	@Override
	protected AutomationRequest getResource() {
		AutomationRequest newAutoRequest = new AutomationRequest();
		
		newAutoRequest.addContributor(URI.create("http://myserver/mycmapp/users/bob"));
		newAutoRequest.addCreator(URI.create("http://myserver/mycmapp/users/jane"));

		newAutoRequest.addState(URI.create(State.New.toString()));
		ParameterInstance param1 = new ParameterInstance();
		param1.setName("param1");
		param1.setDescription("Description of param1");
		param1.setValue("value of param1");
		newAutoRequest.addInputParameter(param1);
		
		newAutoRequest.setExecutesAutomationPlan(new Link(URI.create("http://example.com/automation/autoPlans/123"),"Build Plan 123"));
		newAutoRequest.setInstanceShape(URI.create("http://example.com/shapes/autorequest"));
		newAutoRequest.setTitle("Build Request for Product X");
		newAutoRequest.setDescription("Here is the description of the build request");

		return newAutoRequest;
	}

	@Override
	protected String getResourceType() {
		return Constants.TYPE_AUTO_REQUEST;
	}

}
