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
package org.eclipse.lyo.oslc4j.qualitymanagement.test;

import java.net.URI;

import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.qualitymanagement.Constants;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestPlan;

public class TestPlanTest extends TestQualityManagement<TestPlan> {

	public TestPlanTest() {
		super(TestPlan.class);
	}

	@Override
	protected TestPlan getResource() {
		TestPlan newTestPlan = new TestPlan();
		
		newTestPlan.addContributor(URI.create("http://myserver/mycmapp/users/bob"));
		newTestPlan.addCreator(URI.create("http://myserver/mycmapp/users/jane"));
		newTestPlan.setInstanceShape(URI.create("http://example.com/shapes/testplan"));
		newTestPlan.addRelatedChangeRequest(new Link(URI.create("http://myserver/mycmapp/bugs/1235"), "Bug 1235"));
		newTestPlan.setTitle("Systems Verification Plan for Product X");
		newTestPlan.addUsesTestCase(new Link(URI.create("http://myserver/myqmapp/project1/case123"), "Test Case 123"));
		newTestPlan.addUsesTestCase(new Link(URI.create("http://myserver/myqmapp/project1/case345"), "Test Case 456"));
		newTestPlan.addValidatesRequirementCollection(new Link(URI.create("http://remoteserver/rmapp/project1/collection111"), "Requirement Collection 111"));
		newTestPlan.addValidatesRequirementCollection(new Link(URI.create("http://remoteserver/rmapp/project1/collection222"), "Requirement Collection 222"));
		
		return newTestPlan;
	}

	@Override
	protected String getResourceType() {
		return Constants.TYPE_TEST_PLAN;
	}

}
