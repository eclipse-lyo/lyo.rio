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
 *     Paul McMahan <pmcmahan@us.ibm.com>        - initial implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation;


public interface AutomationConstants
{
    String AUTOMATION_DOMAIN          = "http://open-services.net/ns/auto#";
    String AUTOMATION_NAMESPACE       = "http://open-services.net/ns/auto#";
    String AUTOMATION_PREFIX          = "oslc_auto";
    String FOAF_NAMESPACE             = "http://xmlns.com/foaf/0.1/";
    String FOAF_NAMESPACE_PREFIX      = "foaf";

    String TYPE_AUTOMATION_PLAN       = AUTOMATION_NAMESPACE + "AutomationPlan";
    String TYPE_AUTOMATION_REQUEST    = AUTOMATION_NAMESPACE + "AutomationRequest";
    String TYPE_AUTOMATION_RESULT     = AUTOMATION_NAMESPACE + "AutomationResult";
    String TYPE_PARAMETER_INSTANCE    = AUTOMATION_NAMESPACE + "ParameterInstance";
    String TYPE_PERSON                = FOAF_NAMESPACE + "Person";
    
    String STATE_NEW                  = AUTOMATION_NAMESPACE + "new";
    String STATE_QUEUED               = AUTOMATION_NAMESPACE + "queued";
    String STATE_IN_PROGRESS          = AUTOMATION_NAMESPACE + "inProgress";
    String STATE_CANCELING            = AUTOMATION_NAMESPACE + "canceling";
    String STATE_CANCELED             = AUTOMATION_NAMESPACE + "canceled";
    String STATE_COMPLETE             = AUTOMATION_NAMESPACE + "complete";
    
    String VERDICT_UNAVAILABLE        = AUTOMATION_NAMESPACE + "unavailable";
    String VERDICT_PASSED             = AUTOMATION_NAMESPACE + "passed";
    String VERDICT_WARNING            = AUTOMATION_NAMESPACE + "warning";
    String VERDICT_FAILED             = AUTOMATION_NAMESPACE + "failed";
    String VERDICT_ERROR              = AUTOMATION_NAMESPACE + "error";
    
    String USAGE_LIST = AUTOMATION_NAMESPACE + "list";
    
    String PATH_AUTOMATION_PLAN    = "autoPlan";
    String PATH_AUTOMATION_REQUEST = "autoRequest";
    String PATH_AUTOMATION_RESULT  = "autoResult";

}
