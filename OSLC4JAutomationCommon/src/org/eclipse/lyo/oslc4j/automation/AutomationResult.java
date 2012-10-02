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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.lyo.oslc4j.core.annotation.OslcAllowedValue;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRange;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

@OslcResourceShape(title = "Automation Resource Shape", describes = Constants.TYPE_AUTO_RESULT)
@OslcNamespace(Constants.AUTOMATION_NAMESPACE)
/**
 * @see http://open-services.net/wiki/automation/OSLC-Automation-Specification-Version-2.0/#Resource_AutomationRequest
 */
public final class AutomationResult
       extends AutomationResource
{
    private final Set<State>                 states                      = new TreeSet<State>();
    private final Set<Verdict>               verdicts                    = new TreeSet<Verdict>();
    private final Set<ParameterInstance>     inputParameters             = new TreeSet<ParameterInstance>();
    private final Set<ParameterInstance>     outputParameters            = new TreeSet<ParameterInstance>();
    private final Set<Contribution>          hasContributions            = new TreeSet<Contribution>();
    
    private State    desiredState;
    private Link     reportsOnAutomationPlan;
    private Link     producedByAutomationRequest;

    public AutomationResult()
    {
        super();
    }

    protected URI getRdfType() {
    	return URI.create(Constants.TYPE_AUTO_REQUEST);
    }
    
    public void addState(final URI state)
    {
        this.states.add(State.fromURI(state));
    }
    
    public void addVerdict(final URI verdict) 
    {
    	this.verdicts.add(Verdict.fromURI(verdict));
    }

    public void addInputParameter(final ParameterInstance inputParameter)
    {
        this.inputParameters.add(inputParameter);
    }
    
    public void addOutputParameter(final ParameterInstance outputParameter)
    {
        this.outputParameters.add(outputParameter);
    }
    
    public void addHasContributions(final Contribution hasContribution)
    {
    	this.hasContributions.add(hasContribution);
    }
    
	@OslcAllowedValue({Constants.AUTOMATION_NAMESPACE + "new",
		Constants.AUTOMATION_NAMESPACE + "inProgress",
		Constants.AUTOMATION_NAMESPACE + "queued",
		Constants.AUTOMATION_NAMESPACE + "canceling",
		Constants.AUTOMATION_NAMESPACE + "canceled",
		Constants.AUTOMATION_NAMESPACE + "complete"})	
	@OslcDescription("See list of allowed values for oslc_auto:state")
	@OslcValueType(ValueType.Resource)
	@OslcOccurs(Occurs.OneOrMany)
	@OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE + "state")
	@OslcReadOnly
	@OslcTitle("Automation Result States")
	public URI[] getStates() {
		List<URI> returnStates = new ArrayList<URI>();
		for (State state : this.states) {
			if (state != null) {
				try {
					URI thisState = new URI(state.toString());
					returnStates.add(thisState);
				} catch (final URISyntaxException exception) {
					// This should never happen since we control the possible values of the ValueType enum.
					throw new RuntimeException(exception);
				}
			}
		}

		return returnStates.toArray(new URI[states.size()]);
	}
	
	@OslcAllowedValue({Constants.AUTOMATION_NAMESPACE + "pass",
		Constants.AUTOMATION_NAMESPACE + "fail",
		Constants.AUTOMATION_NAMESPACE + "warn",
		Constants.AUTOMATION_NAMESPACE + "error",
		Constants.AUTOMATION_NAMESPACE + "unavailable"})	
	@OslcDescription("See list of allowed values for oslc_auto:verdict")
	@OslcValueType(ValueType.Resource)
	@OslcOccurs(Occurs.OneOrMany)
	@OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE + "verdict")
	@OslcReadOnly
	@OslcTitle("Automation Result Verdicts")
	public URI[] getVerdicts() {
		List<URI> returnVerdicts = new ArrayList<URI>();
		for (Verdict verdict : this.verdicts) {
			if (verdict != null) {
				try {
					URI thisVerdict = new URI(verdict.toString());
					returnVerdicts.add(thisVerdict);
				} catch (final URISyntaxException exception) {
					// This should never happen since we control the possible values of the ValueType enum.
					throw new RuntimeException(exception);
				}
			}
		}

		return returnVerdicts.toArray(new URI[verdicts.size()]);
	}
    
    @OslcDescription("Parameters provided when Automation Requests are created")
    @OslcName("inputParameter")
    @OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE + "inputParameter")
    @OslcRange(Constants.TYPE_AUTO_PARM_INSTANCE)
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcTitle("Input Parameters")
    public ParameterInstance[] getInputParameters()
    {
        return inputParameters.toArray(new ParameterInstance[inputParameters.size()]);
    }
    
    @OslcDescription("Parameters associated with the automation execution which produced this Result")
    @OslcName("outputParameter")
    @OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE + "outputParameter")
    @OslcValueType(ValueType.LocalResource)
    @OslcRange(Constants.TYPE_AUTO_PARM_INSTANCE)
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcTitle("Output Parameters")
    public ParameterInstance[] getOutputParameters()
    {
        return outputParameters.toArray(new ParameterInstance[outputParameters.size()]);
    }
    
    @OslcDescription("A result contribution associated with this automation result. ")
    @OslcName("hasContribution")
    @OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE + "hasContribution")
    @OslcValueType(ValueType.LocalResource)
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcTitle("Result Contribution")
    public Contribution[] getHasContributions()
    {
        return hasContributions.toArray(new Contribution[hasContributions.size()]);
    }

    @OslcDescription("Automation Plan which the Automation Result reports on.")
    @OslcName("reportsOnAutomationPlan")
    @OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE+ "reportsOnAutomationPlan")
    @OslcRange(Constants.TYPE_AUTO_PLAN)
    @OslcReadOnly(false)
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcTitle("Automation Plan Reported On")
    public Link getReportsOnAutomationPlan()
    {
        return reportsOnAutomationPlan;
    }
    
    @OslcDescription("Automation Request which produced the Automation Result.")
    @OslcName("producedByAutomationRequest")
    @OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE+ "producedByAutomationRequest")
    @OslcRange(Constants.TYPE_AUTO_REQUEST)
    @OslcReadOnly(false)
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcTitle("Automation Request Executed")
    public Link getProducedByAutomationRequest()
    {
        return producedByAutomationRequest;
    }
    
    @OslcAllowedValue({Constants.AUTOMATION_NAMESPACE + "new",
		Constants.AUTOMATION_NAMESPACE + "inProgress",
		Constants.AUTOMATION_NAMESPACE + "queued",
		Constants.AUTOMATION_NAMESPACE + "canceling",
		Constants.AUTOMATION_NAMESPACE + "canceled",
		Constants.AUTOMATION_NAMESPACE + "complete"})
    @OslcDescription("Used to indicate the desired state of the Automation Request based on values defined by the service provider.")
    @OslcName("desiredState")
    @OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE+ "desiredState")
    @OslcReadOnly(false)
    @OslcValueType(ValueType.Resource)
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcTitle("Desired State")
    public URI getDesiredState()
    {
    	try {
           return new URI(desiredState.toString());
    	} catch (final URISyntaxException exception) {
            // This should never happen since we control the possible values of the ValueType enum.
            throw new RuntimeException(exception);
        }
    }
    
    public void setStates(final URI[] states)
    {
        this.states.clear();

        for (URI state : states) {
        	if (state != null)
        	{
        		this.states.add(State.fromURI(state));
        	}
        }
    }
    
    public void setVerdicts(final URI[] verdicts)
    {
        this.verdicts.clear();

        for (URI verdict : verdicts) {
        	if (verdict != null)
        	{
        		this.verdicts.add(Verdict.fromURI(verdict));
        	}
        }
    }
    
    public void setInputParameters(final ParameterInstance[] inputParameters) {
    	this.inputParameters.clear();
    	if (inputParameters != null) {
    		this.inputParameters.addAll(Arrays.asList(inputParameters));
    	}
    }
    
    public void setOutputParameters(final ParameterInstance[] outputParameters) {
    	this.outputParameters.clear();
    	if (outputParameters != null) {
    		this.outputParameters.addAll(Arrays.asList(outputParameters));
    	}
    }
    
    public void setHasContributions(final Contribution[] hasContributions) {
    	this.hasContributions.clear();
    	if (hasContributions != null) {
    		this.hasContributions.addAll(Arrays.asList(hasContributions));
    	}
    }

    public void setProducedByAutomationRequest(final Link producedByAutomationRequest)
    {
        this.producedByAutomationRequest = producedByAutomationRequest;

    }
    
    public void setReportsOnAutomationPlan(final Link reportsOnAutomationPlan)
    {
        this.reportsOnAutomationPlan = reportsOnAutomationPlan;

    }
    
    public void setDesiredState(final URI desiredState)
    {
        this.desiredState = State.fromURI(desiredState);

    }

}
