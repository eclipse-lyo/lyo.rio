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

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.Property;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

@OslcResourceShape(title = "Automation Plan Resource Shape", describes = AutomationConstants.TYPE_AUTOMATION_PLAN)
@OslcNamespace(AutomationConstants.AUTOMATION_NAMESPACE)
/**
 * @see http://open-services.net/wiki/automation/OSLC-Automation-Specification-Version-2.0/#Resource_AutomationPlan
 */
public class AutomationPlan
extends AutomationResource 

{

    private final Set<String>   subjects                    = new TreeSet<String>();
    private final Set<Property> parameterDefinitions        = new TreeSet<Property>();
    
    private String   description;

	public AutomationPlan()
	{
		super();
		
		rdfTypes.add(URI.create(AutomationConstants.TYPE_AUTOMATION_PLAN));
	}
	
    public AutomationPlan(final URI about)
     {
         super(about);

		rdfTypes.add(URI.create(AutomationConstants.TYPE_AUTOMATION_PLAN));
     }
    
    protected URI getRdfType() {
    	return URI.create(AutomationConstants.TYPE_AUTOMATION_PLAN);
    }

    public void addSubject(final String subject)
    {
        this.subjects.add(subject);
    }

    public void addParameterDefinition(final Property parameter)
    {
        this.parameterDefinitions.add(parameter);
    }

    @OslcDescription("Descriptive text (reference: Dublin Core) about resource represented as rich text in XHTML content.")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "description")
    @OslcTitle("Description")
    @OslcValueType(ValueType.XMLLiteral)
    public String getDescription()
    {
        return description;
    }

    @OslcDescription("Timestamp last latest resource modification.")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "modified")
    @OslcReadOnly
    @OslcTitle("Modified")
    public Date getModified()
    {
        return modified;
    }

    @OslcDescription("Tag or keyword for a resource. Each occurrence of a dcterms:subject property denotes an additional tag for the resource.")
    @OslcName("subject")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "subject")
    @OslcReadOnly(false)
    @OslcTitle("Subjects")
    public String[] getSubjects()
    {
        return subjects.toArray(new String[subjects.size()]);
    }

    @OslcDescription("The parameter definitions for the automation plan.")
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcName("parameterDefinition")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "parameterDefinition")
    @OslcValueType(ValueType.LocalResource)
    @OslcTitle("Parameter Definitions")
    public Property[] getParameterDefinitions()
    {
        return parameterDefinitions.toArray(new Property[parameterDefinitions.size()]);
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public void setSubjects(final String[] subjects)
    {
        this.subjects.clear();

        if (subjects != null)
        {
            this.subjects.addAll(Arrays.asList(subjects));
        }
    }

    public void setParameterDefinitions(final Property[] parameterDefinitions)
    {
        this.parameterDefinitions.clear();

        if (parameterDefinitions != null)
        {
            this.parameterDefinitions.addAll(Arrays.asList(parameterDefinitions));
        }
    }

}
