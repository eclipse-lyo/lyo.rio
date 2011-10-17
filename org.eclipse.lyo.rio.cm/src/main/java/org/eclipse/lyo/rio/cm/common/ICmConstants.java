/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation.
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
 *    Jim Conallen - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.rio.cm.common;

import org.eclipse.lyo.rio.core.IConstants;

@SuppressWarnings("nls")
public interface ICmConstants extends IConstants {
	
	// server constants
	public static final String SERVER_CONTEXT = "rio-cm";  
	public static final int DEFAULT_MAX_RESULTS = 100;
	
	public static final String RIO_CM_PUBLISHER_TITLE = "Open Services for Lifecycle Collaboration in Change Management"; 
	public static final String RIO_CM_PUBLISHER_IDENTIFIER = "open-services.net/rio/cm"; 
	public static final String RIO_CM_ICON = "oslc.png"; 
	
	// service segments
	public static final String SERVICE_CHANGEREQUEST = "changerequest";		  
	public static final String SERVICE_FACTORY_CHANGEREQUEST = "factory/changerequest";  
	public static final String SERVICE_SELECTOR_CHANGEREQUEST = "selector/changerequest";  
	public static final String SERVICE_CREATOR_CHANGEREQUEST = "creator/changerequest";  
	
	// RIO
	public static final String RIO_CM_NAMESPACE = "http://open-services.net/rio/cm/"; 
	public static final String RIO_CM_PREFIX = "rio_cm"; 
	public static final String RIO_CM_XMLS_DECL = "\txmlns:" + RIO_CM_PREFIX + "=\"" +  RIO_CM_NAMESPACE + "\"\n";  

	public static final String RIO_CM_SELECTION_RESOURCE_WIDTH = "300px";
	public static final String RIO_CM_SELECTION_RESOURCE_HEIGHT = "300px";
	public static final String RIO_CM_CREATION_RESOURCE_WIDTH = "300px";
	public static final String RIO_CM_CREATION_RESOURCE_HEIGHT = "250px";

	// OSLC CM Change Requirements Properties
	public static final String OSLC_CM_STATUS_SUBMITTED = "Submitted";
	public static final String OSLC_CM_STATUS_IN_PROGRESS = "In Progress";
	public static final String OSLC_CM_STATUS_DONE = "Done";
	public static final String OSLC_CM_STATUS_FIXED = "Fixed";
	
	public static final String[] OSLC_CM_STATUS_VALUES = { 
		OSLC_CM_STATUS_SUBMITTED, 
		OSLC_CM_STATUS_IN_PROGRESS, 
		OSLC_CM_STATUS_DONE, 
		OSLC_CM_STATUS_FIXED 
	};

	// oslc_cm
	public static final String OSLC_CM_NAMESPACE = "http://open-services.net/ns/cm#";
	public static final String OSLC_CM_PREFIX = "oslc_cm";
	public static final String OSLC_CM_XMLS_DECL = "\txmlns:" + OSLC_CM_PREFIX + "=\"" + OSLC_CM_NAMESPACE + "\"\n";

	// oslc_cm types 
	public static final String OSLC_CM_TYPE_TERM_CHANGEREQUEST = "ChangeRequest";
	public static final String OSLC_CM_TYPE_PTERM_CHANGEREQUEST = OSLC_CM_PREFIX + ':' + OSLC_CM_TYPE_TERM_CHANGEREQUEST;
	public static final String OSLC_CM_TYPE_CHANGEREQUEST = OSLC_CM_NAMESPACE + OSLC_CM_TYPE_TERM_CHANGEREQUEST;

	// oslc_cm properties 
	public static final String OSLC_CM_TERM_RELATEDCHANGEREQUEST = "relatedChangeRequest";
	public static final String OSLC_CM_PTERM_RELATEDCHANGEREQUEST = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_RELATEDCHANGEREQUEST;
	public static final String OSLC_CM_RELATEDCHANGEREQUEST = OSLC_CM_NAMESPACE + OSLC_CM_TERM_RELATEDCHANGEREQUEST;
	public static final String OSLC_CM_TERM_AFFECTSPLANITEM = "affectsPlanItem";
	public static final String OSLC_CM_PTERM_AFFECTSPLANITEM = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_AFFECTSPLANITEM;
	public static final String OSLC_CM_AFFECTSPLANITEM = OSLC_CM_NAMESPACE + OSLC_CM_TERM_AFFECTSPLANITEM;
	public static final String OSLC_CM_TERM_AFFECTEDBYDEFECT = "affectedByDefect";
	public static final String OSLC_CM_PTERM_AFFECTEDBYDEFECT = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_AFFECTEDBYDEFECT;
	public static final String OSLC_CM_AFFECTEDBYDEFECT = OSLC_CM_NAMESPACE + OSLC_CM_TERM_AFFECTEDBYDEFECT;
	public static final String OSLC_CM_TERM_TRACKSREQUIREMENT = "tracksRequirement";
	public static final String OSLC_CM_PTERM_TRACKSREQUIREMENT = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_TRACKSREQUIREMENT;
	public static final String OSLC_CM_TRACKSREQUIREMENT = OSLC_CM_NAMESPACE + OSLC_CM_TERM_TRACKSREQUIREMENT;
	public static final String OSLC_CM_TERM_IMPLEMENTSREQUIREMENT = "implementsRequirement";
	public static final String OSLC_CM_PTERM_IMPLEMENTSREQUIREMENT = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_IMPLEMENTSREQUIREMENT;
	public static final String OSLC_CM_IMPLEMENTSREQUIREMENT = OSLC_CM_NAMESPACE + OSLC_CM_TERM_IMPLEMENTSREQUIREMENT;
	public static final String OSLC_CM_TERM_AFFECTSREQUIREMENT = "affectsRequirement";
	public static final String OSLC_CM_PTERM_AFFECTSREQUIREMENT = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_AFFECTSREQUIREMENT;
	public static final String OSLC_CM_AFFECTSREQUIREMENT = OSLC_CM_NAMESPACE + OSLC_CM_TERM_AFFECTSREQUIREMENT;
	public static final String OSLC_CM_TERM_TESTEDBYTESTCASE = "testedByTestCase";
	public static final String OSLC_CM_PTERM_TESTEDBYTESTCASE = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_TESTEDBYTESTCASE;
	public static final String OSLC_CM_TESTEDBYTESTCASE = OSLC_CM_NAMESPACE + OSLC_CM_TERM_TESTEDBYTESTCASE;
	public static final String OSLC_CM_TERM_AFFECTSTESTRESULT = "affectsTestResult";
	public static final String OSLC_CM_PTERM_AFFECTSTESTRESULT = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_AFFECTSTESTRESULT;
	public static final String OSLC_CM_AFFECTSTESTRESULT = OSLC_CM_NAMESPACE + OSLC_CM_TERM_AFFECTSTESTRESULT;
	public static final String OSLC_CM_TERM_BLOCKSTESTEXECUTIONRECORD = "blocksTestExecutionRecord";
	public static final String OSLC_CM_PTERM_BLOCKSTESTEXECUTIONRECORD = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_BLOCKSTESTEXECUTIONRECORD;
	public static final String OSLC_CM_BLOCKSTESTEXECUTIONRECORD = OSLC_CM_NAMESPACE + OSLC_CM_TERM_BLOCKSTESTEXECUTIONRECORD;
	public static final String OSLC_CM_TERM_RELATEDTESTEXECUTIONRECORD = "relatedTestExecutionRecord";
	public static final String OSLC_CM_PTERM_RELATEDTESTEXECUTIONRECORD = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_RELATEDTESTEXECUTIONRECORD;
	public static final String OSLC_CM_RELATEDTESTEXECUTIONRECORD = OSLC_CM_NAMESPACE + OSLC_CM_TERM_RELATEDTESTEXECUTIONRECORD;
	public static final String OSLC_CM_TERM_RELATEDTESTCASE = "relatedTestCase";
	public static final String OSLC_CM_PTERM_RELATEDTESTCASE = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_RELATEDTESTCASE;
	public static final String OSLC_CM_RELATEDTESTCASE = OSLC_CM_NAMESPACE + OSLC_CM_TERM_RELATEDTESTCASE;
	public static final String OSLC_CM_TERM_RELATEDTESTPLAN = "relatedTestPlan";
	public static final String OSLC_CM_PTERM_RELATEDTESTPLAN = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_RELATEDTESTPLAN;
	public static final String OSLC_CM_RELATEDTESTPLAN = OSLC_CM_NAMESPACE + OSLC_CM_TERM_RELATEDTESTPLAN;
	public static final String OSLC_CM_TERM_RELATEDTESTSCRIPT = "relatedTestScript";
	public static final String OSLC_CM_PTERM_RELATEDTESTSCRIPT = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_RELATEDTESTSCRIPT;
	public static final String OSLC_CM_RELATEDTESTSCRIPT = OSLC_CM_NAMESPACE + OSLC_CM_TERM_RELATEDTESTSCRIPT;
	public static final String OSLC_CM_TERM_TRACKSCHANGESET = "tracksChangeSet";
	public static final String OSLC_CM_PTERM_TRACKSCHANGESET = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_TRACKSCHANGESET;
	public static final String OSLC_CM_TRACKSCHANGESET = OSLC_CM_NAMESPACE + OSLC_CM_TERM_TRACKSCHANGESET;
	public static final String OSLC_CM_TERM_CLOSED = "closed";
	public static final String OSLC_CM_PTERM_CLOSED = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_CLOSED;
	public static final String OSLC_CM_CLOSED = OSLC_CM_NAMESPACE + OSLC_CM_TERM_CLOSED;
	public static final String OSLC_CM_TERM_INPROGRESS = "inprogress";
	public static final String OSLC_CM_PTERM_INPROGRESS = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_INPROGRESS;
	public static final String OSLC_CM_INPROGRESS = OSLC_CM_NAMESPACE + OSLC_CM_TERM_INPROGRESS;
	public static final String OSLC_CM_TERM_FIXED = "fixed";
	public static final String OSLC_CM_PTERM_FIXED = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_FIXED;
	public static final String OSLC_CM_FIXED = OSLC_CM_NAMESPACE + OSLC_CM_TERM_FIXED;
	public static final String OSLC_CM_TERM_APPROVED = "approved";
	public static final String OSLC_CM_PTERM_APPROVED = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_APPROVED;
	public static final String OSLC_CM_APPROVED = OSLC_CM_NAMESPACE + OSLC_CM_TERM_APPROVED;
	public static final String OSLC_CM_TERM_REVIEWED = "reviewed";
	public static final String OSLC_CM_PTERM_REVIEWED = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_REVIEWED;
	public static final String OSLC_CM_REVIEWED = OSLC_CM_NAMESPACE + OSLC_CM_TERM_REVIEWED;
	public static final String OSLC_CM_TERM_VERIFIED = "verified";
	public static final String OSLC_CM_PTERM_VERIFIED = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_VERIFIED;
	public static final String OSLC_CM_VERIFIED = OSLC_CM_NAMESPACE + OSLC_CM_TERM_VERIFIED;
	public static final String OSLC_CM_TERM_STATUS = "status";
	public static final String OSLC_CM_PTERM_STATUS = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_STATUS;
	public static final String OSLC_CM_STATUS = OSLC_CM_NAMESPACE + OSLC_CM_TERM_STATUS;
	public static final String OSLC_CM_TERM_CLOSEDATE = "closeDate";
	public static final String OSLC_CM_PTERM_CLOSEDATE = OSLC_CM_PREFIX + ':' + OSLC_CM_TERM_CLOSEDATE;
	public static final String OSLC_CM_CLOSEDATE = OSLC_CM_NAMESPACE + OSLC_CM_TERM_CLOSEDATE;
	
}
