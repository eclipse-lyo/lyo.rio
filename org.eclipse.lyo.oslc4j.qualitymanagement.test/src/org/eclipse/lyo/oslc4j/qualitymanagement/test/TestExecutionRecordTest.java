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
import org.eclipse.lyo.oslc4j.qualitymanagement.TestExecutionRecord;

public class TestExecutionRecordTest extends TestQualityManagement<TestExecutionRecord> {

	public TestExecutionRecordTest() {
		super(TestExecutionRecord.class);
	}

	@Override
	protected TestExecutionRecord getResource() {
		TestExecutionRecord newTestExecutionRecord = new TestExecutionRecord();

		newTestExecutionRecord.addContributor(URI.create("http://myserver/mycmapp/users/bob"));
		newTestExecutionRecord.addCreator(URI.create("http://myserver/mycmapp/users/jane"));
		newTestExecutionRecord.setInstanceShape(URI.create("http://example.com/shapes/testexecutionrecord"));
		newTestExecutionRecord.addRelatedChangeRequest(new Link(URI.create("http://myserver/mycmapp/bugs/1235"), "Bug 1235"));
		newTestExecutionRecord.setTitle("Accessibility validation for Mac OSX");
		newTestExecutionRecord.setReportsOnTestPlan(new Link(URI.create("http://myserver/myqmapp/project1/plan123"), "Test Plan 123"));
		newTestExecutionRecord.setRunsOnTestEnvironment(URI.create("http://remoteserver/environment1"));
		newTestExecutionRecord.setRunsTestCase(new Link(URI.create("http://myserver/myqmapp/project1/case123"), "Test Case 123"));
		newTestExecutionRecord.addBlockedByChangeRequest(new Link(URI.create("http://myserver/mycmapp/bugs/3456"), "Bug 3456"));
		
		return newTestExecutionRecord;
	}

	@Override
	protected String getResourceType() {
		return Constants.TYPE_TEST_EXECUTION_RECORD;
	}

}
