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
import org.eclipse.lyo.oslc4j.qualitymanagement.TestScript;
import org.eclipse.lyo.oslc4j.qualitymanagement.test.TestQualityManagement;

public class TestScriptTest extends TestQualityManagement<TestScript> {

	public TestScriptTest() {
		super(TestScript.class);
	}

	@Override
	protected TestScript getResource() {
		TestScript newTestCase = new TestScript();
		
		newTestCase.addContributor(URI.create("http://myserver/mycmapp/users/bob"));
		newTestCase.addCreator(URI.create("http://myserver/mycmapp/users/jane"));
		newTestCase.setDescription("Steps for validating accessibilty using a screen reader.");
		newTestCase.setInstanceShape(URI.create("http://example.com/shapes/testscript"));
		newTestCase.addRelatedChangeRequest(new Link(URI.create("http://myserver/mycmapp/bugs/1235"), "Bug 1235"));
		newTestCase.setTitle("Accessibility validation");
		newTestCase.addValidatesRequirement(new Link(URI.create("http://myserver/reqtool/req/34ef31af")));
		newTestCase.addValidatesRequirement(new Link(URI.create("http://remoteserver/reqrepo/project1/req456"), "Standards compliance"));
		newTestCase.setExecutionInstructions(URI.create("http://remoteserver/accessibilitytesting.xml"));
		
		return newTestCase;
	}

	@Override
	protected String getResourceType() {
		return Constants.TYPE_TEST_SCRIPT;
	}

}
