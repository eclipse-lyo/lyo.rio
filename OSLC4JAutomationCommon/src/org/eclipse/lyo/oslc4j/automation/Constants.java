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
 *     Michael Fiedler         - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation;


public interface Constants
{
    public static String FOAF_NAMESPACE                              = "http://xmlns.com/foaf/0.1/";
    public static String FOAF_NAMESPACE_PREFIX                       = "foaf";
    public static String AUTOMATION_NAMESPACE                        = "http://open-services.net/ns/auto#";
    public static String AUTOMATION_DOMAIN                           = "http://open-services.net/ns/auto#";
    public static String AUTOMATION_PREFIX                = "oslc_auto";

    public static String TYPE_PERSON                = FOAF_NAMESPACE + "Person";

    public static String TYPE_AUTO_PLAN             = AUTOMATION_NAMESPACE + "AutomationPlan";
    public static String TYPE_AUTO_REQUEST          = AUTOMATION_NAMESPACE + "AutomationRequest";
    public static String TYPE_AUTO_RESULT           = AUTOMATION_NAMESPACE + "AutomationResult";
    public static String TYPE_AUTO_PARM_INSTANCE    = AUTOMATION_NAMESPACE + "ParameterInstance";

    public static String PATH_AUTO_PLAN    = "autoPlan";
    public static String PATH_AUTO_REQUEST = "autoRequest";
    public static String PATH_AUTO_RESULT  = "autoResult";

    public static String USAGE_LIST = AUTOMATION_NAMESPACE + "list";
}
