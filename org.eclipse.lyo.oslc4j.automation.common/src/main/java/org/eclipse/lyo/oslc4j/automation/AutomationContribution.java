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

import java.net.URI;

import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;


@OslcResourceShape(title = "Automation Contribution Resource Shape", describes = "AutomationContribution")
@OslcNamespace(AutomationConstants.AUTOMATION_NAMESPACE)
public class AutomationContribution
       extends AbstractResource
{
    //all predicates of a Contribution are stored in the extendedProperties
    

    public AutomationContribution()
     {
         super();

     }

     public AutomationContribution(final URI rdfType)
     {
    	 super();
         addType(rdfType);
     }    
    
}
