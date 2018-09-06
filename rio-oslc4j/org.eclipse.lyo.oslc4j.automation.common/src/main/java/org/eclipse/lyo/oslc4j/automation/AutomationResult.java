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
 *     Paul McMahan <pmcmahan@us.ibm.com>   - initial implementation
 *     Samuel Padgett <spadgett@us.ibm.com> - fix ClassCastException adding contributions to result
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
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

@OslcResourceShape(title = "Automation Result Resource Shape", describes = AutomationConstants.TYPE_AUTOMATION_RESULT)
@OslcNamespace(AutomationConstants.AUTOMATION_NAMESPACE)
/**
 * @see http://open-services.net/wiki/automation/OSLC-Automation-Specification-Version-2.0/#Resource_AutomationResult
 */
public class AutomationResult
extends AutomationResource
{
	private final Set<String>   subjects                    = new TreeSet<String>();
    private final Set<URI>      states                      = new TreeSet<URI>();
    private final Set<URI>      verdicts                    = new TreeSet<URI>();
    private final Set<Object>   contributions               = new HashSet<Object>();
    private final Set<ParameterInstance> inputParameters    = new TreeSet<ParameterInstance>();
    private final Set<ParameterInstance> outputParameters   = new TreeSet<ParameterInstance>();
    
    private URI      desiredState;
    private Link      producedByAutomationRequest;
    private Link      reportsOnAutomationPlan;

	public AutomationResult()
	{
		super();
		
		rdfTypes.add(URI.create(AutomationConstants.TYPE_AUTOMATION_RESULT));
	}
	
    public AutomationResult(final URI about)
     {
         super(about);

		rdfTypes.add(URI.create(AutomationConstants.TYPE_AUTOMATION_RESULT));
     }

    protected URI getRdfType() {
    	return URI.create(AutomationConstants.TYPE_AUTOMATION_RESULT);
    }
    
    public void addSubject(final String subject)
    {
        this.subjects.add(subject);
    }

    public void addState(final URI state)
    {
        this.states.add(state);
    }
    
    public void addVerdict(final URI verdict)
    {
        this.verdicts.add(verdict);
    }
    
    public void addContribution(final AutomationContribution contribution)
    {
        this.contributions.add(contribution);
    }
    
    public void addInputParameter(final ParameterInstance parameter)
    {
        this.inputParameters.add(parameter);
    }
    
    public void addOutputParameter(final ParameterInstance parameter)
    {
        this.outputParameters.add(parameter);
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

    @OslcDescription("Used to indicate the state of the automation result based on values defined by the service provider.")
    @OslcOccurs(Occurs.OneOrMany)
    @OslcReadOnly(true)
    @OslcName("state")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "state")
    @OslcTitle("State")
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
    
    @OslcDescription("A result contribution associated with this automation result.")
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcName("contribution")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "contribution")
    @OslcValueType(ValueType.LocalResource)
    @OslcTitle("Contribution")
    public AutomationContribution[] getContributions()
    {
        return contributions.toArray(new AutomationContribution[contributions.size()]);
    }
    
    @OslcDescription("Used to indicate the verdict of the automation result based on values defined by the service provider.")
    @OslcOccurs(Occurs.OneOrMany)
    @OslcName("verdict")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "verdict")
    @OslcTitle("Verdict")
    @OslcAllowedValue({
    	AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.VERDICT_PASSED,
    	AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.VERDICT_FAILED,
    	AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.VERDICT_WARNING,
    	AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.VERDICT_ERROR,
    	AutomationConstants.AUTOMATION_NAMESPACE + AutomationConstants.VERDICT_UNAVAILABLE})
    public URI[] getVerdicts()
    {
        return verdicts.toArray(new URI[verdicts.size()]);
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
    
    @OslcDescription("Automation Request which produced the Automation Result.")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "producedByAutomationRequest")
    @OslcName("producedByAutomationRequest")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcTitle("Produced By Automation Request")
    public Link getProducedByAutomationRequest()
    {
        return producedByAutomationRequest;
    }
    
    @OslcDescription("Automation Plan which the Automation Result reports on.")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "reportsOnAutomationPlan")
    @OslcName("reportsOnAutomationPlan")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcTitle("Reports On Automation Plan")
    public Link getReportsOnAutomationPlan()
    {
        return reportsOnAutomationPlan;
    }
    
    @OslcDescription("A copy of the parameters provided during creation of the Automation Request which produced this Automation Result.")
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcName("inputParameter")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "inputParameter")
    @OslcReadOnly(true)
    @OslcValueType(ValueType.LocalResource)
    @OslcTitle("Input Parameter")
    public ParameterInstance[] getInputParameters()
    {
        return inputParameters.toArray(new ParameterInstance[inputParameters.size()]);
    }

    @OslcDescription("Automation Result output parameters are parameters associated with the automation execution which produced this Result. This includes the final value of all parameters used to initiate the execution and any additional parameters which may have been created during automation execution by the service provider or external agents.")
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcName("outputParameter")
    @OslcPropertyDefinition(AutomationConstants.AUTOMATION_NAMESPACE + "outputParameter")
    @OslcValueType(ValueType.LocalResource)
    @OslcTitle("Output Parameter")
    public ParameterInstance[] getOutputParameters()
    {
        return outputParameters.toArray(new ParameterInstance[outputParameters.size()]);
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
    
    public void setVerdicts(final URI[] verdicts)
    {
        this.verdicts.clear();

        if (verdicts != null)
        {
            this.verdicts.addAll(Arrays.asList(verdicts));
        }
    }
    
    public void setContributions(final AutomationContribution[] contributions)
    {
        this.contributions.clear();

        if (contributions != null)
        {
            this.contributions.addAll(Arrays.asList(contributions));
        }
    }
    
    public void setDesiredState(final URI desiredState)
    {
        this.desiredState = desiredState;
    }

    public void setProducedByAutomationRequest(final Link producedByAutomationRequest)
    {
        this.producedByAutomationRequest = producedByAutomationRequest;
    }
    
    public void setReportsOnAutomationPlan(final Link reportsOnAutomationPlan)
    {
        this.reportsOnAutomationPlan = reportsOnAutomationPlan;
    }
    
    public void setInputParameters(final ParameterInstance[] parameters)
    {
        this.inputParameters.clear();

        if (parameters != null)
        {
            this.inputParameters.addAll(Arrays.asList(parameters));
        }
    }
    
    public void setOutputParameters(final ParameterInstance[] parameters)
    {
        this.outputParameters.clear();

        if (parameters != null)
        {
            this.outputParameters.addAll(Arrays.asList(parameters));
        }
    }
    

}
