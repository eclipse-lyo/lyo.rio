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

import org.eclipse.lyo.oslc4j.automation.AutomationConstants;
import org.eclipse.lyo.oslc4j.automation.AutomationPlan;
import org.eclipse.lyo.oslc4j.automation.Property;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

public class AutomationPlanTest extends TestAutomation<AutomationPlan> {

	public AutomationPlanTest() {
		super(AutomationPlan.class);
	}

	@Override
	protected AutomationPlan getResource() {
		final AutomationPlan newAutoPlan = new AutomationPlan();
		
		newAutoPlan.addContributor(URI.create("http://myserver/mycmapp/users/bob"));
		newAutoPlan.addCreator(URI.create("http://myserver/mycmapp/users/jane"));
		newAutoPlan.addSubject("subject1");

		final Property param1 = new Property("myParameter", Occurs.ExactlyOne, ValueType.String);
		newAutoPlan.addParameterDefinition(param1);
		
		newAutoPlan.setInstanceShape(URI.create("http://example.com/shapes/autoplan"));
		newAutoPlan.setTitle("Build Plan for Product X");
		newAutoPlan.setDescription("Here is the description of the build plan");

		return newAutoPlan;
	}

	@Override
	protected String getResourceType() {
		return AutomationConstants.TYPE_AUTOMATION_PLAN;
	}

}
