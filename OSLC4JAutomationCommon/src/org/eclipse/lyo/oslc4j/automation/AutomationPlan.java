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
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRange;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.Property;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

@OslcResourceShape(title = "Automation Resource Shape", describes = Constants.TYPE_AUTO_PLAN)
@OslcNamespace(Constants.AUTOMATION_NAMESPACE)
/**
 * @see http://open-services.net/wiki/automation/OSLC-Automation-Specification-Version-2.0/#Resource_AutomationPlan
 */
public final class AutomationPlan
       extends AutomationResource
{

    private final Set<String>   subjects                    = new TreeSet<String>();
    private final Set<Property> paramDefinitions            = new TreeSet<Property>();

    private String   description;

	public AutomationPlan()
	{
		super();
	}

    protected URI getRdfType() {
    	return URI.create(Constants.TYPE_AUTO_PLAN);
    }

    public void addSubject(final String subject)
    {
        this.subjects.add(subject);
    }
    
    public void addParamDefinitions(final Property paramDefinition)
    {
    	this.paramDefinitions.add(paramDefinition);
    }


    @OslcDescription("Descriptive text (reference: Dublin Core) about resource represented as rich text in XHTML content.")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "description")
    @OslcTitle("Description")
    @OslcValueType(ValueType.XMLLiteral)
    public String getDescription()
    {
        return description;
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
    
    @OslcDescription("The definition of a parameter for this Automation Plan.")
    @OslcName("parameterDefinition")
    @OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE + "parameterDefinition")
    @OslcValueType(ValueType.LocalResource)
    @OslcReadOnly(false)
    @OslcRange(OslcConstants.TYPE_PROPERTY)
    @OslcTitle("Parameter Definition")
    public String[] getParamDefinitions()
    {
        return paramDefinitions.toArray(new String[paramDefinitions.size()]);
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
    
    public void setParamDefinitions(final Property[] paramDefinitions)
    {
        this.paramDefinitions.clear();

        if (paramDefinitions != null)
        {
            this.paramDefinitions.addAll(Arrays.asList(paramDefinitions));
        }
    }

}
