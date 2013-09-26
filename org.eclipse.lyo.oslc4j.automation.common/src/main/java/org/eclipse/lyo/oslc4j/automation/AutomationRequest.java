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
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.lyo.oslc4j.core.annotation.OslcAllowedValue;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

@OslcResourceShape(title = "Automation Request Resource Shape", describes = AutomationConstants.TYPE_AUTOMATION_REQUEST)
@OslcNamespace(AutomationConstants.AUTOMATION_NAMESPACE)
/**
 * @see http://open-services.net/wiki/automation/OSLC-Automation-Specification-Version-2.0/#Resource_AutomationRequest
 */
public class AutomationRequest
extends AutomationResource
{
	private final Set<String>   subjects                    = new TreeSet<String>();
    private final Set<URI>      states                      = new TreeSet<URI>();
    private final Set<ParameterInstance> inputParameters    = new TreeSet<ParameterInstance>();
    
    private String   description;
    private URI      desiredState;
    private Link      executesAutomationPlan;

	public AutomationRequest()
	{
		super();
		
		rdfTypes.add(URI.create(AutomationConstants.TYPE_AUTOMATION_REQUEST));
	}
	
    public AutomationRequest(final URI about)
     {
         super(about);

		rdfTypes.add(URI.create(AutomationConstants.TYPE_AUTOMATION_REQUEST));
     }

    protected URI getRdfType() {
    	return URI.create(AutomationConstants.TYPE_AUTOMATION_REQUEST);
    }
    
    public void addSubject(final String subject)
    {
        this.subjects.add(subject);
    }

    public void addState(final URI state)
    {
        this.states.add(state);
    }
    
    public void addInputParameter(final ParameterInstance parameter)
    {
        this.inputParameters.add(parameter);
    }
    
    @OslcDescription("Descriptive text (reference: Dublin Core) about resource represented as rich text in XHTML content.")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "description")
    @OslcTitle("Description")
    @OslcValueType(ValueType.XMLLiteral)
    public String getDescription()
    {
        return description;
    }

    @OslcDescription("Used to indicate the desired state of the Automation Request based on values defined by the service provider.")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "desiredState")
    @OslcName("desiredState")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcTitle("Desired State")
    @OslcAllowedValue({
    	AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_NEW,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_IN_PROGRESS,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_QUEUED,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_CANCELING,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_CANCELED,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_COMPLETE})
    public URI getDesiredState()
    {
        return desiredState;
    }
    
    @OslcDescription("Automation Plan run by the Automation Request.")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "executesAutomationPlan")
    @OslcName("executesAutomationPlan")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcTitle("Executes Automation Plan")
    public Link getExecutesAutomationPlan()
    {
        return executesAutomationPlan;
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

    @OslcDescription("Used to indicate the state of the automation request based on values defined by the service provider.")
    @OslcOccurs(Occurs.OneOrMany)
    @OslcReadOnly(true)
    @OslcName("state")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "state")
    @OslcTitle("States")
    @OslcAllowedValue({
    	AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_NEW,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_IN_PROGRESS,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_QUEUED,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_CANCELING,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_CANCELED,
		AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.STATE_COMPLETE})
    public URI[] getStates()
    {
        return states.toArray(new URI[states.size()]);
    }

    
    @OslcDescription("Parameters provided when Automation Requests are created.")
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcName("inputParameter")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "inputParameter")
    @OslcReadOnly(false)
    @OslcValueType(ValueType.LocalResource)
    @OslcTitle("Input Parameter")
    public ParameterInstance[] getInputParameters()
    {
        return inputParameters.toArray(new ParameterInstance[inputParameters.size()]);
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public void setDesiredState(final URI desiredState)
    {
        this.desiredState = desiredState;
    }

    public void setExecutesAutomationPlan(final Link executesAutomationPlan)
    {
        this.executesAutomationPlan = executesAutomationPlan;
    }
    
    public void setSubjects(final String[] subjects)
    {
        this.subjects.clear();

        if (subjects != null)
        {
            this.subjects.addAll(Arrays.asList(subjects));
        }
    }

    public void setStates(final URI[] states)
    {
        this.states.clear();

        if (states != null)
        {
            this.states.addAll(Arrays.asList(states));
        }
    }
    
    public void setInputParameters(final ParameterInstance[] parameters)
    {
        this.inputParameters.clear();

        if (parameters != null)
        {
            this.inputParameters.addAll(Arrays.asList(parameters));
        }
    }

}
