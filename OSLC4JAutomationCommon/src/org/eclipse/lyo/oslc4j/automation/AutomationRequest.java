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
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

@OslcResourceShape(title = "Automation Resource Shape", describes = Constants.TYPE_AUTO_REQUEST)
@OslcNamespace(Constants.AUTOMATION_NAMESPACE)
/**
 * @see http://open-services.net/wiki/automation/OSLC-Automation-Specification-Version-2.0/#Resource_AutomationRequest
 */
public final class AutomationRequest
       extends AutomationResource
{
    private final Set<State>                 states                      = new TreeSet<State>();
    private final Set<ParameterInstance>     inputParameters             = new TreeSet<ParameterInstance>();
    

    private String   description;
    private State    desiredState;
    private Link     executesAutomationPlan;

    public AutomationRequest()
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

    public void addInputParameter(final ParameterInstance inputParameter)
    {
        this.inputParameters.add(inputParameter);
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
	@OslcTitle("Automation Request States")
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
    
    @OslcDescription("Descriptive text (reference: Dublin Core) about resource represented as rich text in XHTML content.")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "description")
    @OslcTitle("Description")
    @OslcValueType(ValueType.XMLLiteral)
    public String getDescription()
    {
        return description;
    }

    @OslcDescription("Automation Plan run by the Automation Request.")
    @OslcName("executesAutomationPlan")
    @OslcPropertyDefinition(Constants.AUTOMATION_NAMESPACE+ "executesAutomationPlan")
    @OslcRange(Constants.TYPE_AUTO_PLAN)
    @OslcReadOnly(false)
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcTitle("Automation Plan Executed")
    public Link getExecutesAutomationPlan()
    {
        return executesAutomationPlan;
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
    
    public void setInputParameters(final ParameterInstance[] inputParameters) {
    	this.inputParameters.clear();
    	if (inputParameters != null) {
    		this.inputParameters.addAll(Arrays.asList(inputParameters));
    	}
    }

    public void setExecutesAutomationPlan(final Link executesAutomationPlan)
    {
        this.executesAutomationPlan = executesAutomationPlan;

    }
    
    public void setDescription(final String description)
    {
        this.description = description;
    }
    
    public void setDesiredState(final URI desiredState)
    {
        this.desiredState = State.fromURI(desiredState);

    }

}
