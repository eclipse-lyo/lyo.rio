/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
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
 *     Samuel Padgett - initial implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation.common.test;

import org.eclipse.lyo.oslc4j.automation.AutomationContribution;
import org.eclipse.lyo.oslc4j.automation.AutomationResult;
import org.junit.Test;

public class AutomationResultTest {
	@Test
	public void automationResultTest() {
		final AutomationResult automationResult = new AutomationResult();
		automationResult.addContribution(new AutomationContribution());
		automationResult.addContribution(new AutomationContribution());
	}
}
