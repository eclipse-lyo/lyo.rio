/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
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
 *     Samuel Padgett       - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc.v3.sample;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.jena.atlas.json.JsonObject;
import org.eclipse.lyo.oslc.v3.sample.vocab.OSLC;
import org.eclipse.lyo.oslc.v3.sample.vocab.OSLC_CM;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.lyo.oslc.v3.sample.Constants.APPLICATION_JSON_LD;
import static org.eclipse.lyo.oslc.v3.sample.Constants.LDP;
import static org.eclipse.lyo.oslc.v3.sample.Constants.TEXT_TURTLE;

@Path("/bugs")
public class BugContainer {
	@Context private HttpServletRequest request;
	@Context private HttpServletResponse response;
	@Context private UriInfo uriInfo;
	@Context private HttpHeaders headers;

	private static final String PREVIEW_HEIGHT = "150px";
	private static final String PREVIEW_WIDTH = "300px";

	private Set<String> include = new HashSet<String>();
	private Set<String> omit = new HashSet<String>();

	private static Map<String, String> severityLabels = new HashMap<String, String>();
	static {
		severityLabels.put(OSLC_CM.NS + "Blocker", "Blocker");
		severityLabels.put(OSLC_CM.NS + "Critical", "Critical");
		severityLabels.put(OSLC_CM.NS + "Major", "Major");
		severityLabels.put(OSLC_CM.NS + "Normal", "Normal");
		severityLabels.put(OSLC_CM.NS + "Minor", "Minor");
		severityLabels.put(OSLC_CM.NS + "SeverityUnassigned", "Unassigned");
	}

	/**
	 * An ETag for the container. A new ETag is generated when bugs are added or
	 * removed. (Other container properties are not editable.)
	 *
	 * <p>We manage ETags this way instead of using MD5 so that we can have
	 * consistent ETags for containers regardless of the Prefer headers.
	 */
	private static String etag = ETag.generateRandom();

	@GET
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getBugContainer() {
		setContainerResponseHeaders();

		Model model = ModelFactory.createDefaultModel();
		Resource container =
				model.createResource(uriInfo.getAbsolutePath().toString(),
				                        model.getResource(LDP + "BasicContainer"));
		container.addProperty(DCTerms.title, "Bug Container");

		// Check the Prefer header to see what to include or omit.
		parsePrefer();

		// Use a read lock so the containment triples match the ETag.
		Persistence.getInstance().readLock();
		try {
			testIfNoneMatch(etag);
			setETagHeader(etag);

			// Include dialogs?
			if (include.contains(OSLC.NS + "PreferDialog")) {
				setPreferenceAppliedHeader();
				createDialogResource(model);
			}

			// Include containment by default. This is up to the server.
			boolean includeContainment = true;

			// Include containment?
			if (include.contains(LDP + "PreferContainment")) {
				setPreferenceAppliedHeader();
			} else if (include.contains(LDP + "PreferMinimalContainer")
					|| omit.contains(LDP + "PreferContainment")) {
				setPreferenceAppliedHeader();
				includeContainment = false;
			}

			if (includeContainment) {
				Persistence.getInstance().addContainmentTriples(container);
			}
		} finally {
			Persistence.getInstance().end();
		}

		return model;
	}

	@OPTIONS
	public void bugContainerOptions() {
		setContainerResponseHeaders();
	}

	@GET
	@Path("creationDialog")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getCreationDialogDescriptor() {
		return createDialogModel();
	}

	@OPTIONS
	@Path("creationDialog")
	public void creationDialogDescriptorOptions() {
		// Prefill is not yet supported.
		response.addHeader("Allow", "GET,HEAD,OPTIONS");
	}

	@GET
	@Path("{id}")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD })
	public Model getBugRDF() {
		final String bugURI = getRequestURI();
		final Model bugModel = getBugModel(getRequestURI());
		setBugResponseHeaders();

		final String etag = ETag.generate(bugModel);
		testIfNoneMatch(etag);
		setETagHeader(etag);

		// Check the Prefer header to see what to include or omit.
		parsePrefer();
		if (!include.contains(OSLC.NS + "PreferCompact")) {
			// Nothing special to do. Return the bug model.
			return bugModel;
		}

		// Compact requested.
		setPreferenceAppliedHeader();
		final Resource bug = bugModel.getResource("");
		Model compactModel = createCompactModel(getBugLabel(bug), bugURI);

		// Add in the Bug triples (not required, but we have them).
		compactModel.add(bugModel);

		return compactModel;
	}

	/*
	 * Handle JSON specially so we can return the correct Compact JSON response if requested.
	 */
	@GET
	@Path("{id}")
	@Produces({ APPLICATION_JSON })
	public Response getBugJSON() {
		final String bugURI = getRequestURI();
		final Model bugModel = getBugModel(bugURI);
		setBugResponseHeaders();

		final String etag = ETag.generate(bugModel);
		testIfNoneMatch(etag);
		setETagHeader(etag);

		parsePrefer();
		if (!include.contains(OSLC.NS + "PreferCompact")) {
			// Return the Bug as JSON-LD.
			return Response.ok(bugModel).build();
		}

		// Compact requested. Return the JSON Compact representation.
		setPreferenceAppliedHeader();

		final JsonObject jsonResponse = new JsonObject();
		final JsonObject compact = createCompactJSON(bugURI, bugModel);
		jsonResponse.put("compact", compact);

		return Response.ok(jsonResponse.toString()).build();
	}

	private JsonObject createCompactJSON(final String bugURI, final Model bugModel) {
		final JsonObject compact = new JsonObject();

		final Resource bug = bugModel.getResource("");
		compact.put("title", getBugLabel(bug));
		compact.put("icon", getIconURI().toString());

		final JsonObject preview = new JsonObject();
		final String document = UriBuilder.fromUri(bugURI).path("preview").build().toString();
		preview.put("document", document);
		preview.put("hintWidth", PREVIEW_WIDTH);
		preview.put("hintHeight", PREVIEW_HEIGHT);
		compact.put("smallPreview", preview);

		return compact;
	}

	@GET
	@Path("{id}/compact")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD })
	public Model getCompactRDF(@PathParam("id") String id) {
		final String bugURI = getBugURI(id);
		final Model bugModel = getBugModel(bugURI);
		final Resource bug = bugModel.getResource("");
		final String label = getBugLabel(bug);

		return createCompactModel(label, bugURI);
	}

	@GET
	@Path("{id}/compact")
	@Produces({ APPLICATION_JSON })
	public Response getCompactJSON(@PathParam("id") String id) {
		final String bugURI = getBugURI(id);
		final Model bugModel = getBugModel(bugURI);
		JsonObject compact = createCompactJSON(bugURI, bugModel);

		return Response.ok(compact.toString()).build();
	}

	@GET
	@Path("{id}/preview")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public void getBugPreview(@PathParam("id") String id) throws ServletException, IOException {
		request.setAttribute("baseURI", getBaseURI());

		final String bugURI = getBugURI(id);
		final Model model = getBugModel(bugURI);
		final Resource r = model.getResource("");

		final Statement severity = r.getProperty(OSLC_CM.severity);
		if (severity != null && severity.getObject().isURIResource()) {
			String uri = severity.getResource().getURI();
			request.setAttribute("severity", severityLabels.get(uri));
		}

		final Statement description = r.getProperty(DCTerms.description);
		if (description != null && description.getObject().isLiteral()) {
			request.setAttribute("description", description.getString());
		}

		request.getRequestDispatcher("/WEB-INF/preview.jsp").forward(request, response);
	}

	@DELETE
	@Path("{id}")
	public Response deleteBug() {
		Persistence.getInstance().writeLock();
		try {
			verifyBugExists();
			Persistence.getInstance().removeBugModel(getRequestURI());
			Persistence.getInstance().commit();
			etag = ETag.generateRandom();
		} finally {
			Persistence.getInstance().end();
		}

		return Response.noContent().build();
	}

	@OPTIONS
	@Path("{id}")
	public void bugOptions() {
		Persistence.getInstance().readLock();
		try {
			verifyBugExists();
			etag = ETag.generateRandom();
		} finally {
			Persistence.getInstance().end();
		}

		setBugResponseHeaders();
	}

	@POST
	@Consumes({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Response createBug(Model m) {
		setContainerResponseHeaders();

		if (System.getProperty("constrainContent") != null
				&& !m.contains(m.getResource(""),
				               RDF.type,
				               OSLC_CM.Defect)) {
			return Response.status(Status.CONFLICT).build();
		}

		String id = UUID.randomUUID().toString();
		URI location = uriInfo.getAbsolutePathBuilder().path(id).build();
		Persistence.getInstance().writeLock();
		try {
			Persistence.getInstance().addBugModel(m, location);
			Persistence.getInstance().commit();
			etag = ETag.generateRandom();
		} finally {
			Persistence.getInstance().end();
		}

		return Response.created(location).build();
	}

	private String getBaseURI() {
		return uriInfo.getBaseUriBuilder().path("..").build().normalize().toString().replaceAll("/$", "");
	}

	private String getBugURI(String id) {
		return uriInfo.getBaseUriBuilder().path("bugs/{id}").build(id).toString();
	}

	private URI getDialogURI() {
		return uriInfo.getBaseUriBuilder().path("bugs/creationDialog").build();
	}

	private URI getIconURI() {
		return uriInfo.getBaseUriBuilder().path("../oslc-16x16.png").build().normalize();
	}

	private String getRequestURI() {
		return uriInfo.getAbsolutePath().toString();
	}

	private String getBugLabel(final Resource bug) {
		final Statement title = bug.getProperty(DCTerms.title);
		if (title == null || !title.getObject().isLiteral()) {
			return null;
		}

		return title.getString();
	}

	private Model createCompactModel(String title, String bugURI) {
		Model m = ModelFactory.createDefaultModel();
		String compactURI = UriBuilder.fromUri(bugURI).path("compact").build().toString();
		Resource compact = m.createResource(compactURI, OSLC.Compact);
		if (title != null) {
			compact.addProperty(DCTerms.title, title);
		}
		compact.addProperty(OSLC.icon, m.createResource(getIconURI().toString()));

		Resource preview = m.createResource(OSLC.Preview);
		String document = UriBuilder.fromUri(bugURI).path("preview").build().toString();
		preview.addProperty(OSLC.document, m.createResource(document));
		preview.addProperty(OSLC.hintWidth, PREVIEW_WIDTH);
		preview.addProperty(OSLC.hintHeight, PREVIEW_HEIGHT);

		compact.addProperty(OSLC.smallPreview, preview);

		return m;
	}

	private Model createDialogModel() {
		Model m = ModelFactory.createDefaultModel();
		createDialogResource(m);

		return m;
	}

	private void createDialogResource(Model m) {
		Resource dialog = m.createResource(getDialogURI().toString(), OSLC.Dialog);
		dialog.addProperty(OSLC.label, "Open Bug");
		String document = uriInfo.getBaseUriBuilder().path("../newBug.html").build().normalize().toString();
		dialog.addProperty(OSLC.dialog, m.createResource(document));
		dialog.addProperty(OSLC.hintWidth, "450px");
		dialog.addProperty(OSLC.hintHeight, "395px");
	}

	private Model getBugModel(String uri) {
		Persistence.getInstance().readLock();
		final Model model;
		try {
			model = Persistence.getInstance().getBugModel(uri);
		} finally {
			Persistence.getInstance().end();
		}

		if (model == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return model;
	}

	private void setBugResponseHeaders() {
		response.addHeader("Allow", "GET,HEAD,OPTIONS,DELETE");
		response.addHeader("Link", "<" + LDP + "Resource"+">;rel=type");
		response.addHeader("Link", "<" + uriInfo.getAbsolutePathBuilder().path("compact").build() + ">;rel=\"" + OSLC.Compact.getURI() + "\"");
	}

	private void setContainerResponseHeaders() {
		// LDP Headers
		response.addHeader("Link", "<" + LDP + "Resource"+">;rel=type");
		response.addHeader("Link", "<" + LDP + "BasicContainer>;rel=type");
		response.addHeader("Allow", "GET,HEAD,POST,OPTIONS");
		response.addHeader("Accept-Post", TEXT_TURTLE + "," + APPLICATION_JSON + "," + APPLICATION_JSON);

		// LDP constrainedBy header should point to the resource shape
		URI shape = uriInfo.getBaseUriBuilder().path("../Defect-shape.ttl").build().normalize();
		response.addHeader("Link", "<" + shape + ">;rel=\"" + Constants.LINK_REL_CONSTRAINED_BY + "\"");

		// OSLC Creation Dialog
		response.addHeader("Link", "<" + getDialogURI() + ">;rel=\"" + OSLC.NS + "creationDialog\"");
	}

	private void setETagHeader(String etag) {
		response.addHeader("ETag", etag);
	}

	private void verifyBugExists() {
		if (!Persistence.getInstance().exists(getRequestURI())) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	private void parsePrefer() {
		final List<String> preferValues = headers.getRequestHeader("Prefer");
		if (preferValues == null) {
			return;
		}

		for (String prefer : preferValues) {
			HeaderElement[] preferElements = BasicHeaderValueParser.parseElements(prefer, null);
			for (HeaderElement e : preferElements) {
				if ("return".equals(e.getName()) && "representation".equals(e.getValue())) {
					addValues(e.getParameterByName("include"), include);
					addValues(e.getParameterByName("omit"), omit);
				}
			}
		}
	}

	private void addValues(NameValuePair parameter, Set<String> values) {
		if (parameter != null) {
			String parameterValue = parameter.getValue();
			if (parameterValue != null) {
				for (String s : parameterValue.split(" ")) {
					values.add(s);
				}
			}
		}
	}

	private void testIfNoneMatch(String etag) {
		final List<String> ifNoneMatchValues = headers.getRequestHeader(ETag.IF_NONE_MATCH_HEADER);
		if (ifNoneMatchValues == null) {
			return;
		}

		for (String ifNoneMatch : ifNoneMatchValues) {
			if (ETag.matches(ifNoneMatch, etag)) {
				throw new WebApplicationException(Status.NOT_MODIFIED);
			}
		}
	}

	/**
	 * Sets the Preference-Applied response header for preference <code>return=representation</code>.
	 */
	private void setPreferenceAppliedHeader() {
		response.setHeader("Preference-Applied", "return=representation");
	}
}
