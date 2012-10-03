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
 *    Michael Fiedler         - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation.test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;



import org.eclipse.lyo.oslc4j.automation.AutomationConstants;
import org.eclipse.lyo.oslc4j.automation.AutomationContribution;
import org.eclipse.lyo.oslc4j.automation.AutomationResult;
import org.eclipse.lyo.oslc4j.automation.ParameterInstance;
import org.eclipse.lyo.oslc4j.core.model.Link;


public class AutomationResultTest extends TestAutomation<AutomationResult> {

	public AutomationResultTest() {
		super(AutomationResult.class);
	}

	@Override
	protected AutomationResult getResource() {
		final AutomationResult newAutoResult = new AutomationResult();
		
		newAutoResult.addContributor(URI.create("http://myserver/mycmapp/users/bob"));
		newAutoResult.addCreator(URI.create("http://myserver/mycmapp/users/jane"));
		newAutoResult.addState(URI.create(AutomationConstants.STATE_COMPLETE));
		newAutoResult.addVerdict(URI.create(AutomationConstants.VERDICT_PASSED));
		
		final ParameterInstance param1 = new ParameterInstance();
		param1.setName("param1");
		param1.setDescription("Description of param1");
		param1.setValue("value of param1");
		newAutoResult.addInputParameter(param1);
		newAutoResult.addOutputParameter(param1);
		
		final AutomationContribution contrib = new AutomationContribution(URI.create("http://example.com/myns#myContribution"));
		
		final Map<QName,Object> contribMap = new HashMap<QName,Object>();
		contribMap.put(new QName("http://example.com/myns#myContribution"), "http://example.com/buildResults/789");
		contrib.setExtendedProperties(contribMap);
		//QName contributionQname = new QName(AutomationConstants.AUTOMATION_DOMAIN, "contribution");
		//newAutoResult.getExtendedProperties().put(contributionQname, contrib);
		newAutoResult.addContribution(contrib);

		newAutoResult.setReportsOnAutomationPlan(new Link(URI.create("http://example.com/automation/autoPlans/123"),"Build Plan 123"));
		newAutoResult.setProducedByAutomationRequest(new Link(URI.create("http://example.com/automation/autoRequests/456"),"Build Request 456"));
		newAutoResult.setInstanceShape(URI.create("http://example.com/shapes/autorequest"));
		newAutoResult.setTitle("Build Request for Product X");
		

		return newAutoResult;
	}

	@Override
	protected String getResourceType() {
		return AutomationConstants.TYPE_AUTOMATION_RESULT;
	}

}
