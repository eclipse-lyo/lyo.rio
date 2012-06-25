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
import org.eclipse.lyo.oslc4j.qualitymanagement.TestResult;

public class TestResultTest extends TestQualityManagement<TestResult> {

	public TestResultTest() {
		super(TestResult.class);
	}

	@Override
	protected TestResult getResource() {
		TestResult newTestResult = new TestResult();

		newTestResult.setExecutesTestScript(new Link(URI.create("http://myserver/myqmapp/project1/script123"), "Test Script 123"));
		newTestResult.setProducedByTestExecutionRecord(new Link(URI.create("http://myserver/myqmapp/project1/ter333"), "Test Execution Record 333"));
		newTestResult.setReportsOnTestCase(new Link(URI.create("http://myserver/myqmapp/project1/case444"), "Test Case 444"));
		newTestResult.setReportsOnTestPlan(new Link(URI.create("http://myserver/myqmapp/project1/plan555"), "Test Plan 555"));
		newTestResult.setStatus("failed");
		newTestResult.addAffectedByChangeRequest(new Link(URI.create("http://myserver/mycmapp/bugs/1235"), "Bug 1235"));
		newTestResult.setTitle("Accessibility failure on Mac OSX");
		newTestResult.setInstanceShape(URI.create("http://example.com/shapes/testresult"));
		
		return newTestResult;
	}

	@Override
	protected String getResourceType() {
		return Constants.TYPE_TEST_RESULT;
	}
	
}
