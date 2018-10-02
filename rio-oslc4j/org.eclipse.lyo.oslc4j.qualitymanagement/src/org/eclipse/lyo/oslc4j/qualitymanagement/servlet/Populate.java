/*******************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation.
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
package org.eclipse.lyo.oslc4j.qualitymanagement.servlet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.qualitymanagement.Persistence;
import org.eclipse.lyo.oslc4j.qualitymanagement.QmResource;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestCase;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestExecutionRecord;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestPlan;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestResult;
import org.eclipse.lyo.oslc4j.qualitymanagement.TestScript;

final class Populate
{
    private final String basePath;
    private final URI    serviceProviderURI;

    public Populate(final String  basePath,
                    final URI     serviceProviderURI)
    {
        super();

        this.basePath           = basePath;
        this.serviceProviderURI = serviceProviderURI;
    }

    public void fixup()
    {
        final QmResource[] qmResources = Persistence.getQmResources();

        for (final QmResource qmResource : qmResources)
        {
        	qmResource.setServiceProvider(serviceProviderURI);
        }
    }

    public void populate()
           throws URISyntaxException
    {    	
    	TestScript testScript = createTestScript("Lyo Rio Test Script", "This is a sample test script that lists the steps used to conduct the tests", "testScripts");
    	Persistence.addResource(testScript);
    	
    	TestCase testCase = createTestCase("Lyo Rio Test Case", "This test case will determine whether a system exhibits the correct behavior under specific circumstances", "testCases");
    	Persistence.addResource(testCase);
    	
    	TestPlan testPlan = createTestPlan("Lyo Rio Test Plan", "This test plan will focus on testing the reference implementations of the OSLC domains.", testCase, "testPlans");
    	Persistence.addResource(testPlan);
    	
    	TestExecutionRecord testExecution = createTestExecutionRecord("Lyo Rio Test Execution Record", testPlan, testCase, "testExecutionRecords");
    	Persistence.addResource(testExecution);
    	
    	TestResult testResult = createTestResult("Lyo Rio Test Result", "Sample Status", testPlan, testScript, testExecution, "testResults");
    	Persistence.addResource(testResult);
    	
    }

    private TestPlan createTestPlan (final String   title,
                                     final String   description,
                                     final TestCase testCase,
                                     final String   path)
            throws URISyntaxException
    {
        final TestPlan testPlan = new TestPlan();
        
        final long identifier = Persistence.getNextIdentifier();

        final URI about = new URI(basePath + "/" + path + "/" + identifier);

        testPlan.setAbout(about);
        testPlan.setIdentifier(String.valueOf(identifier));
        testPlan.setServiceProvider(serviceProviderURI);
        testPlan.setTitle(title);
        testPlan.setDescription(description);
        
        Link[] link = {new Link(testCase.getAbout())};
        
        testPlan.setUsesTestCases(link);

        final Date date = new Date();
        testPlan.setCreated(date);
        testPlan.setModified(date);
        
        return testPlan;
    }
    
    private TestScript createTestScript (final String   title,
    									 final String   description,
    									 final String   path)
			throws URISyntaxException
	{
    	final TestScript testScript = new TestScript();
    	
    	final long identifier = Persistence.getNextIdentifier();
    	
    	final URI about = new URI(basePath + "/" + path + "/" + identifier);
    	
    	testScript.setAbout(about);
    	testScript.setIdentifier(String.valueOf(identifier));
    	testScript.setServiceProvider(serviceProviderURI);
    	testScript.setTitle(title);
    	testScript.setDescription(description);
    	
    	final Date date = new Date();
    	testScript.setCreated(date);
    	testScript.setModified(date);
    	
    	return testScript;
	}
 
    private TestCase createTestCase (final String   title,
       							     final String   description,
    								 final String   path)
    		throws URISyntaxException
    {
    	final TestCase testCase = new TestCase();

    	final long identifier = Persistence.getNextIdentifier();

    	final URI about = new URI(basePath + "/" + path + "/" + identifier);

    	testCase.setAbout(about);
    	testCase.setIdentifier(String.valueOf(identifier));
    	testCase.setServiceProvider(serviceProviderURI);
    	testCase.setTitle(title);
    	testCase.setDescription(description);

    	final Date date = new Date();
    	testCase.setCreated(date);
    	testCase.setModified(date);

    	return testCase;
    }
    
    private TestResult createTestResult(final String 	 		  title,
    									final String			  status,
    									final TestPlan  		  testPlan,
    									final TestScript          testScript,
    									final TestExecutionRecord testExecute,
    									final String              path)
    		throws URISyntaxException
   	{
    	final TestResult testResult = new TestResult();
    	
    	final long identifier = Persistence.getNextIdentifier();

    	final URI about = new URI(basePath + "/" + path + "/" + identifier);
    	
    	testResult.setAbout(about);
    	testResult.setIdentifier(String.valueOf(identifier));
    	testResult.setServiceProvider(serviceProviderURI);
    	testResult.setTitle(title);
    	testResult.setStatus(status);
    	
    	testResult.setReportsOnTestPlan(new Link(testPlan.getAbout()));
    	testResult.setExecutesTestScript(new Link(testScript.getAbout()));
    	testResult.setProducedByTestExecutionRecord(new Link(testExecute.getAbout()));
    	
    	final Date date = new Date();
    	testResult.setCreated(date);
    	testResult.setModified(date);
    	
    	return testResult;
   	}
    
    private TestExecutionRecord createTestExecutionRecord(final String 	 title,
    													  final TestPlan testPlan,
    													  final TestCase testCase,
														  final String   path)
			throws URISyntaxException
	{
    	final TestExecutionRecord testExecutionRecord = new TestExecutionRecord();

    	final long identifier = Persistence.getNextIdentifier();

    	final URI about = new URI(basePath + "/" + path + "/" + identifier);

    	testExecutionRecord.setAbout(about);
    	testExecutionRecord.setIdentifier(String.valueOf(identifier));
    	testExecutionRecord.setServiceProvider(serviceProviderURI);
    	testExecutionRecord.setTitle(title);
    	
    	testExecutionRecord.setReportsOnTestPlan(new Link(testPlan.getAbout()));
    	testExecutionRecord.setRunsTestCase(new Link(testCase.getAbout()));
    	
    	final Date date = new Date();
    	testExecutionRecord.setCreated(date);
    	testExecutionRecord.setModified(date);

    	return testExecutionRecord;
	}
}
