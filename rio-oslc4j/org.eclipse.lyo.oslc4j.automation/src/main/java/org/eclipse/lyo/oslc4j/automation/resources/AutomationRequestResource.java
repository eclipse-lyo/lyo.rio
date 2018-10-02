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
 *		Michael Fiedler			- initial API and implementation
 *		Samuel Padgett			- remove deprecated call to Property.getDefaultValue()
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.apache.wink.client.ClientResponse;
import org.eclipse.lyo.oslc4j.automation.AutomationConstants;
import org.eclipse.lyo.oslc4j.automation.AutomationContribution;
import org.eclipse.lyo.oslc4j.automation.AutomationPlan;
import org.eclipse.lyo.oslc4j.automation.AutomationRequest;
import org.eclipse.lyo.oslc4j.automation.AutomationResource;
import org.eclipse.lyo.oslc4j.automation.AutomationResult;
import org.eclipse.lyo.oslc4j.automation.ParameterInstance;
import org.eclipse.lyo.oslc4j.automation.Persistence;
import org.eclipse.lyo.oslc4j.automation.Property;
import org.eclipse.lyo.oslc4j.automation.servlet.ServiceProviderSingleton;
import org.eclipse.lyo.oslc4j.client.OslcRestClient;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;

@OslcService(AutomationConstants.AUTOMATION_DOMAIN)
@Path("autoRequests")
public class AutomationRequestResource extends BaseAutoResource<AutomationRequest> {

	private static final String REMOTE_COMMAND_PLAN = "Remote Command Automation Plan";
	private static final String  UNIT_TEST_AUTO_PLAN = "UnitTestPlan";
	private static final Set<Class<?>> PROVIDERS = JenaProvidersRegistry.getProviders();

	private static final Logger logger = Logger.getLogger(AutomationRequestResource.class.getName());

	public AutomationRequestResource () {
		super(AutomationRequest.class);
	}

	protected AutomationRequestResource(Class<AutomationRequest> resourceType) {
		super(resourceType);
	}

	@OslcDialogs(
	{
		@OslcDialog
		(
			 title = "Automation Request Selection Dialog",
			 label = "Automation Request Selection Dialog",
			 uri = "autoRequests/selector",
			 hintWidth = "1000px",
			 hintHeight = "600px",
			 resourceTypes = {AutomationConstants.TYPE_AUTOMATION_REQUEST},
			 usages = {OslcConstants.OSLC_USAGE_DEFAULT}
		),
		@OslcDialog
		(
			 title = "Automation Request List Dialog",
			 label = "Automation Request List Dialog",
			 uri = "",
			 hintWidth = "1000px",
			 hintHeight = "600px",
			 resourceTypes = {AutomationConstants.TYPE_AUTOMATION_REQUEST},
			 usages = {AutomationConstants.USAGE_LIST}
		)
	})
	@OslcQueryCapability
	(
		title = "Automation Request Query Capability",
		label = "Automation Request Catalog Query",
		resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + AutomationConstants.PATH_AUTOMATION_REQUEST,
		resourceTypes = {AutomationConstants.TYPE_AUTOMATION_REQUEST},
		usages = {OslcConstants.OSLC_USAGE_DEFAULT}
	)
	@GET
	@Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	public AutomationRequest[] getResources(@QueryParam("oslc.where") final String where)
	{
		return super.getResources(where);
	}

	@GET
	@Path("{resourceId}")
	@Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	public AutomationRequest getResource(@Context					   final HttpServletResponse httpServletResponse,
										 @PathParam("resourceId")	   final String				 resourceId)
	{
		return super.getResource(httpServletResponse, resourceId);
	}

	@GET
	@Path("{resourceId}")
	@Produces({MediaType.TEXT_HTML})
	public Response getResource(@Context				 final HttpServletRequest  httpServletRequest,
								@Context				 final HttpServletResponse httpServletResponse,
								@PathParam("resourceId") final String			   resourceId)
	{

		httpServletRequest.setAttribute("autoRequest",super.getResource(httpServletResponse, resourceId));

		try {
			RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/autorequest_html.jsp");
			rd.forward(httpServletRequest, httpServletResponse);

		} catch (Exception e) {
			throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
		}


		throw new WebApplicationException(Status.NOT_FOUND);
	}

	@GET
	@Path("creator")
	@Produces({MediaType.TEXT_HTML, MediaType.WILDCARD})

	public void autoRequestCreator(@Context					final HttpServletRequest httpServletRequest,
								   @Context					final HttpServletResponse httpServletResponse,
								   @Context					final UriInfo uriInfo,
								   @QueryParam("autoPlan")	final String autoPlan)
	{
		httpServletRequest.setAttribute("creatorUri",uriInfo.getAbsolutePath().toString());

		if (autoPlan == null)
		{
			Map<String,String> autoPlanIDs = new HashMap<String,String>();

			for (AutomationResource thisResource:Persistence.getAutoResources())
			{
				if (thisResource.getClass().equals(AutomationPlan.class))
				{
					autoPlanIDs.put(thisResource.getIdentifier(), thisResource.getTitle());
				}
			}
			try {
				httpServletRequest.setAttribute("autoPlans", autoPlanIDs);
				RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/autorequest_creator.jsp");
				rd.forward(httpServletRequest, httpServletResponse);
			} catch (Exception e) {
				throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
			}
		} else {
			AutomationPlan selectedPlan = Persistence.getAutoResource(autoPlan, AutomationPlan.class);
			if (selectedPlan != null)
			{
				Map<String,Object> params = new HashMap<String,Object>();
				for (Property paramDefinition : selectedPlan.getParameterDefinitions())
				{
					Object defaultValue = paramDefinition.getDefaultValueObject();
					if (defaultValue == null)
						defaultValue="";
					params.put(paramDefinition.getName(), defaultValue);

				}
				try {
					httpServletRequest.setAttribute("params", params);
					RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/web/autorequest_params_json.jsp");
					rd.forward(httpServletRequest, httpServletResponse);
				} catch (Exception e) {
					throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
				}
			}
		}
	}

	@POST
	@Path("creator")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({MediaType.APPLICATION_JSON})
	public Response createHtmlAutoRequest (@Context final HttpServletRequest  httpServletRequest,
										   @Context final HttpServletResponse httpServletResponse,
										   MultivaluedMap<String, String>	  formParams)
	{
		Response response;
		String planId = formParams.getFirst("planId");
		if ((planId != null) && !planId.isEmpty())
		{
			AutomationPlan autoPlan = Persistence.getAutoResource(planId, AutomationPlan.class);
			if (autoPlan != null)
			{
				AutomationRequest newRequest = new AutomationRequest();
				newRequest.setTitle("Execution request for: " + autoPlan.getTitle());
				newRequest.setExecutesAutomationPlan(new Link(autoPlan.getAbout()));

				//Add all other formParams (other than planId) as parameter instances

				for (String paramName : formParams.keySet())
				{
					if (!paramName.equalsIgnoreCase("planId") && ! paramName.isEmpty())
					{
						ParameterInstance paramInstance = new ParameterInstance();
						try {
							paramInstance.setName(URLDecoder.decode(paramName,"utf-8"));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//assuming no duplicates for now
						paramInstance.setValue(formParams.getFirst(paramName));
						newRequest.addInputParameter(paramInstance);
					}
				}

				final URI creationFactory = AutomationUtils.getCreation(PROVIDERS,
																		ServiceProviderSingleton.getServiceProviderURI(),
																		URI.create(AutomationConstants.AUTOMATION_DOMAIN),
																		URI.create(AutomationConstants.TYPE_AUTOMATION_REQUEST));
				OslcRestClient client = new OslcRestClient(PROVIDERS, creationFactory, "application/rdf+xml");


				AutomationRequest createdResource = client.addOslcResource(newRequest);
				try
				{
					if (createdResource != null)
					{

						httpServletResponse.setContentType("application/json");
						httpServletResponse.setStatus(Status.CREATED.getStatusCode());
						httpServletResponse.addHeader("Location", createdResource.getAbout().toString());

						response = Response.created(createdResource.getAbout()).entity(createdResource).build();


					} else {
						final org.eclipse.lyo.oslc4j.core.model.Error error = new org.eclipse.lyo.oslc4j.core.model.Error();
						error.setStatusCode(Status.INTERNAL_SERVER_ERROR.toString());
						error.setMessage("Error creating automation request");
						response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
					}

				} catch (Exception e)
				{
					throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
				}
			} else {
				final org.eclipse.lyo.oslc4j.core.model.Error error = new org.eclipse.lyo.oslc4j.core.model.Error();
				error.setStatusCode(Status.NOT_FOUND.toString());
				error.setMessage("Requested AutomationPlan not found");
				response = Response.status(Status.NOT_FOUND).entity(error).build();
			}
		} else {
			final org.eclipse.lyo.oslc4j.core.model.Error error = new org.eclipse.lyo.oslc4j.core.model.Error();
			error.setStatusCode(Status.INTERNAL_SERVER_ERROR.toString());
			error.setMessage("planId parameter missing or invalid");
			response = Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		return response;

	}

	@OslcDialog
	(
		 title = "Automation Request Creation Dialog",
		 label = "Automation Request Creation Dialog",
		 uri = "autoRequests/creator",
		 hintWidth = "1000px",
		 hintHeight = "600px",
		 resourceTypes = {AutomationConstants.TYPE_AUTOMATION_REQUEST},
		 usages = {OslcConstants.OSLC_USAGE_DEFAULT}
	)
	@OslcCreationFactory
	(
		 title = "Automation Request Creation Factory",
		 label = "Automation Request Creation",
		 resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + AutomationConstants.PATH_AUTOMATION_REQUEST},
		 resourceTypes = {AutomationConstants.TYPE_AUTOMATION_REQUEST},
		 usages = {OslcConstants.OSLC_USAGE_DEFAULT}
	)
	@POST
	@Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	@Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	public Response addResource(@Context final HttpServletRequest  httpServletRequest,
								@Context final HttpServletResponse httpServletResponse,
										 final AutomationRequest	   resource)
		   throws URISyntaxException
	{
		String errorMessage = null;

		//first, persist the AutomationRequest so that it gets its subject URI
		final Response response = super.addResource(httpServletRequest, httpServletResponse, resource);

		final Link autoPlanLink = resource.getExecutesAutomationPlan();

		if (autoPlanLink != null) {
			//special test for unit test automation plan - don't try to fetch it, don't create result
			if (autoPlanLink.getLabel() != null && autoPlanLink.getLabel().equals(UNIT_TEST_AUTO_PLAN)) {
				return response;
			}
			else {
				//fetch the Automation Plan
				final OslcRestClient client = new OslcRestClient(PROVIDERS, autoPlanLink.getValue(), "application/rdf+xml");
				final AutomationPlan autoPlan =  client.getOslcResource(AutomationPlan.class);

				final AutomationResult autoResult = buildAutoResult(resource);
				//If the automation plan is the special Remote Command plan, execute it

				if (autoResult != null)
				{
					if (autoPlan != null && autoPlan.getTitle().equals(REMOTE_COMMAND_PLAN)) {

						final AutomationResult updatedResult = executeCommand(resource,autoResult);
						if (updatedResult != null)
						{
							return Response.created(resource.getAbout()).entity(resource).build();
						}
						else
						{
							errorMessage = "Could not update Automation Result";
						}
					}
				} else {
					errorMessage = "Could not create Automation Result";
				}
			}
		} else {
			buildAutoResult(resource);
		}

		if (errorMessage != null)
		{
			final org.eclipse.lyo.oslc4j.core.model.Error error = new org.eclipse.lyo.oslc4j.core.model.Error();
			error.setMessage("Could not execute remote command automation plan");
			error.setStatusCode(Status.INTERNAL_SERVER_ERROR.toString());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
		} else {

			return response;
		}
	}

	@PUT
	@Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
	@Path("{resourceId}")
	public Response updateResource(@Context						 final HttpServletResponse httpServletResponse,
								   @HeaderParam("If-Match")		 final String			   eTagHeader,
								   @PathParam("resourceId")		 final String			   resourceId,
																 final AutomationRequest	   resource)
	{
		if (resource.getDesiredState() != null)
		{
			resource.setStates(new URI[]{resource.getDesiredState()});
		}
		return super.updateResource(httpServletResponse, eTagHeader, resourceId, resource);
	}

	@DELETE
	@Path("{resourceId}")
	public Response deleteResource(@PathParam("resourceId") final String identifier)
	{
		return super.deleteResource(identifier);
	}

	private AutomationResult buildAutoResult(AutomationRequest request)
	{
		AutomationResult newResult = null;

		final URI creationFactory = AutomationUtils.getCreation(PROVIDERS,
														  request.getServiceProvider(),
														  URI.create(AutomationConstants.AUTOMATION_DOMAIN),
														  URI.create(AutomationConstants.TYPE_AUTOMATION_RESULT));
		if (creationFactory != null)
		{
			final AutomationResult result = new AutomationResult();
			result.setInputParameters(request.getInputParameters());
			result.setOutputParameters(request.getInputParameters());
			result.addState(URI.create(AutomationConstants.STATE_NEW));
			result.addVerdict(URI.create(AutomationConstants.VERDICT_UNAVAILABLE));
			result.setProducedByAutomationRequest(new Link(request.getAbout()));
			if (request.getExecutesAutomationPlan() != null) {
				result.setReportsOnAutomationPlan(request
						.getExecutesAutomationPlan());
			}
			final OslcRestClient resultClient = new OslcRestClient(PROVIDERS,
					creationFactory, "application/rdf+xml");
			try {
				newResult = resultClient.addOslcResource(result);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not create new AutomationResult",
						e);
			}
		}

		return newResult;
	}

	private AutomationResult executeCommand(AutomationRequest request, AutomationResult result)
	{
		AutomationResult updatedResult = null;

		//Execute the command in the request inputParameter.  Throw an exception if there is
		//an error executing the command or the "command" inputParameter cannot be found.
		try {

			boolean foundCommandParm = false;
			for (ParameterInstance p : request.getInputParameters())
			{
				if (p.getName().equals("command"))
				{
					foundCommandParm = true;
					final String command = p.getValue();
					final Process process = Runtime.getRuntime().exec(command);
					final BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
					final BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

					String line = null;
					final StringBuffer stdoutBuffer = new StringBuffer();
					while ((line = stdout.readLine()) != null)
					{
						stdoutBuffer.append(line);
					}

					line = null;
					final StringBuffer stderrBuffer = new StringBuffer();
					while ((line = stderr.readLine()) != null)
					{
						stderrBuffer.append(line);
					}

					final AutomationContribution contrib = new AutomationContribution(URI.create("http://example.com/myns#commandResult"));

					final Map<QName,Object> contribMap = new HashMap<QName,Object>();
					contribMap.put(new QName("http://example.com/myns#stdOut"), stdoutBuffer.toString());
					contribMap.put(new QName("http://example.com/myns#stdErr"), stderrBuffer.toString());
					contrib.setExtendedProperties(contribMap);

					result.addContribution(contrib);
					result.setStates (new URI[]{URI.create(AutomationConstants.STATE_COMPLETE)});
					result.setVerdicts (new URI[]{URI.create(AutomationConstants.VERDICT_PASSED)});


				}
			}
			if (!foundCommandParm) throw new IOException("Command not found");
		} catch (IOException e) {
			final AutomationContribution contrib = new AutomationContribution(URI.create("http://example.com/mnyns#commandError"));
			final Map<QName,Object> contribMap = new HashMap<QName,Object>();
			contribMap.put(new QName("http://example.com/myns#reason"),e.getMessage());
			contrib.setExtendedProperties(contribMap);
			result.addContribution(contrib);
			result.setStates (new URI[]{URI.create(AutomationConstants.STATE_COMPLETE)});
			result.setVerdicts (new URI[]{URI.create(AutomationConstants.VERDICT_ERROR)});
		}

		//Update the AutomationResult with the verdict and results
		final OslcRestClient client = new OslcRestClient(PROVIDERS, result.getAbout(), "application/rdf+xml");
		final ClientResponse clientResponse = client.updateOslcResourceReturnClientResponse(result);
		if (clientResponse.getStatusCode() == Status.OK.getStatusCode())
		{
			updatedResult = client.getOslcResource(AutomationResult.class);
		}

		return updatedResult;
	}



}
