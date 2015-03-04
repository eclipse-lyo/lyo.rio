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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
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
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.riot.Lang;
import org.apache.log4j.Logger;
import org.eclipse.lyo.oslc.v3.sample.vocab.LDP;
import org.eclipse.lyo.oslc.v3.sample.vocab.OSLC;
import org.eclipse.lyo.oslc.v3.sample.vocab.OSLC_CM;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DCTerms;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.eclipse.lyo.oslc.v3.sample.MediaTypeContants.*;
import static org.eclipse.lyo.oslc.v3.sample.Headers.*;
import static org.eclipse.lyo.oslc.v3.sample.vocab.LDP.LINK_REL_CONSTRAINED_BY;
import static org.eclipse.lyo.oslc.v3.sample.vocab.OSLC.LINK_REL_COMPACT;
import static org.eclipse.lyo.oslc.v3.sample.vocab.OSLC.LINK_REL_CREATION_DIALOG;

@Path("/bugs")
public class BugContainer {
	private static final Logger logger = Logger.getLogger(BugContainer.class);

	@Context private HttpServletRequest request;
	@Context private HttpServletResponse response;
	@Context private UriInfo uriInfo;
	@Context private HttpHeaders headers;

	private static final String PREVIEW_WIDTH = "400px";
	private static final String PREVIEW_HEIGHT = "200px";

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

	@GET
	@Produces(TEXT_TURTLE)
	public StreamingOutput getContainerTurtle() {
		return getContainer(Lang.TURTLE);
	}

	@GET
	@Produces({ APPLICATION_JSON_LD, APPLICATION_JSON })
	public StreamingOutput getContainerJSONLD() {
		return getContainer(Lang.JSONLD);
	}

	public StreamingOutput getContainer(final Lang lang) {
		setContainerResponseHeaders();
		final String requestURI = getRequestURI();
		return new StreamingOutput() {
			public void write(OutputStream out) throws IOException, WebApplicationException {
				Persistence.getInstance().readLock();
				try {
					Model model = ModelFactory.createDefaultModel();
					Resource container =
							model.createResource(requestURI,
							                     LDP.BasicContainer);
					container.addProperty(DCTerms.title, "Bug Container");

					// Check the Prefer header to see what to include or omit.
					parsePrefer();

					// Include dialogs?
					if (include.contains(OSLC.NS + "PreferDialog")) {
						setPreferenceAppliedHeader();
						createDialogResource(model);
					}

					// Include containment by default. This is up to the server.
					boolean includeContainment = true;

					// Include containment?
					if (include.contains(LDP.NS + "PreferContainment")) {
						setPreferenceAppliedHeader();
					} else if (include.contains(LDP.NS + "PreferMinimalContainer")
							|| omit.contains(LDP.NS + "PreferContainment")) {
						setPreferenceAppliedHeader();
						includeContainment = false;
					}

					if (includeContainment) {
						Persistence.getInstance().addContainmentTriples(container);
					}

					respond(out, model, lang.getName(), requestURI);
				} finally {
					Persistence.getInstance().end();
				}
			}
		};
	}

	@OPTIONS
	public void bugContainerOptions() {
		setContainerResponseHeaders(false);
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
		response.addHeader(ALLOW, "GET,HEAD,OPTIONS");
	}

	@GET
	@Path("{id}")
	@Produces(TEXT_TURTLE)
	public StreamingOutput getBugTurtle() {
		return getBug(Lang.TURTLE);
	}

	@GET
	@Path("{id}")
	@Produces(APPLICATION_JSON_LD)
	public StreamingOutput getBugJSONLD() {
		return getBug(Lang.JSONLD);
	}

	public StreamingOutput getBug(final Lang lang) {
		final String bugURI = getRequestURI();
		return new StreamingOutput() {
			public void write(OutputStream out) throws IOException, WebApplicationException {
				Persistence.getInstance().readLock();
				try {
					final Model bugModel = getBugModel(bugURI);
					setBugResponseHeaders();

					// Check the Prefer header to see what to include or omit.
					parsePrefer();
					if (include.contains(OSLC.NS + "PreferCompact")) {
						// Compact requested.
						setPreferenceAppliedHeader();
						final Resource bug = bugModel.getResource(bugURI);
						Model compactModel = createCompactModel(getBugLabel(bug), bugURI);
						// Add in the Bug triples (not required, but we have them).
						compactModel.add(bugModel);
						respond(out, compactModel, lang.getName(), bugURI);
					} else {
						respond(out, bugModel, lang.getName(), bugURI);
					}
				} finally {
					Persistence.getInstance().end();
				}
			}
		};
	}

	/*
	 * Handle JSON specially so we can return the correct Compact JSON response if requested.
	 */
	@GET
	@Path("{id}")
	@Produces({ APPLICATION_JSON })
	public StreamingOutput getBugJSON() {
		final String bugURI = getRequestURI();
		return new StreamingOutput() {
			public void write(OutputStream out) throws IOException, WebApplicationException {
				Persistence.getInstance().readLock();
				try {
					final Model bugModel = getBugModel(bugURI);
					setBugResponseHeaders();

					parsePrefer();
					if (include.contains(OSLC.NS + "PreferCompact")) {
						// Compact requested. Return the JSON Compact representation.
						setPreferenceAppliedHeader();

						final JsonObject jsonResponse = new JsonObject();
						final JsonObject compact = createCompactJSON(bugURI, bugModel);
						jsonResponse.put("compact", compact);

						respond(out, jsonResponse);
					} else {
						// Return the Bug as JSON-LD.
						respond(out, bugModel, Lang.JSONLD.getName(), bugURI);
					}
				} finally {
					Persistence.getInstance().end();
				}
			}
		};
	}

	@GET
	@Path("{id}")
	@Produces(TEXT_HTML)
	public void getBugHTML()
			throws ServletException, IOException {
		final String bugURI = getRequestURI();
		forwardBugJSP(bugURI, "/WEB-INF/bug.jsp");
	}

	private JsonObject createCompactJSON(final String bugURI, final Model bugModel) {
		final JsonObject compact = new JsonObject();

		final Resource bug = bugModel.getResource(bugURI);
		compact.put("title", getBugLabel(bug));
		compact.put("icon", getIconURI().toString());

		final JsonObject preview = new JsonObject();
		final String document = getRequestURIBuilder().path("preview").build().toString();
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
		Persistence.getInstance().readLock();
		try {
			final Model bugModel = getBugModel(bugURI);
			final Resource bug = bugModel.getResource(bugURI);
			final String label = getBugLabel(bug);

			return createCompactModel(label, bugURI);
		} finally {
			Persistence.getInstance().end();
		}
	}

	@GET
	@Path("{id}/compact")
	@Produces({ APPLICATION_JSON })
	public StreamingOutput getCompactJSON(@PathParam("id") final String id) {
		return new StreamingOutput() {
			public void write(OutputStream out) throws IOException, WebApplicationException {
				final JsonObject compact;
				final String bugURI = getBugURI(id);
				Persistence.getInstance().readLock();
				try {
					final Model bugModel = getBugModel(bugURI);
					compact = createCompactJSON(bugURI, bugModel);
				} finally {
					Persistence.getInstance().end();
				}

				compact.output(new IndentedWriter(out));
			}
		};
	}

	@GET
	@Path("{id}/preview")
	@Produces(TEXT_HTML)
	public void getBugPreview(@PathParam("id") String id)
			throws ServletException, IOException {
		final String bugURI = getBugURI(id);
		forwardBugJSP(bugURI, "/WEB-INF/preview.jsp");
	}

	public void forwardBugJSP(final String bugURI, final String path)
			throws ServletException, IOException {
		Persistence.getInstance().readLock();
		try {
			final Model model = getBugModel(bugURI);
			setBugResponseHeaders();
			setBugAttributes(model, bugURI);
		} finally {
			Persistence.getInstance().end();
		}
		request.getRequestDispatcher(path).forward(request, response);
	}

	private void setBugAttributes(Model model, String bugURI)
			throws ServletException, IOException {
		request.setAttribute("baseURI", getBaseURI());
		request.setAttribute("bugURI", bugURI);
		final Resource r = model.getResource(bugURI);

		final Statement title = r.getProperty(DCTerms.title);
		if (title != null && title.getObject().isLiteral()) {
			request.setAttribute("title", title.getString());
		}

		final Statement severity = r.getProperty(OSLC_CM.severity);
		if (severity != null && severity.getObject().isURIResource()) {
			String uri = severity.getResource().getURI();
			request.setAttribute("severity", severityLabels.get(uri));
		}

		final Statement description = r.getProperty(DCTerms.description);
		if (description != null && description.getObject().isLiteral()) {
			request.setAttribute("description", description.getString());
		}

		request.setAttribute("created", getDate(r.getProperty(DCTerms.created)));
	}

	private Date getDate(Statement s) {
		if (s == null || !s.getObject().isLiteral()) {
			return null;
		}

		try {
			return DatatypeFactory.newInstance().
				newXMLGregorianCalendar(s.getString()).
				toGregorianCalendar().getTime();
		} catch (IllegalArgumentException e) {
			logger.warn(String.format("Invalid date format <%s>" + s.getString()), e);
			return null;
		} catch (DatatypeConfigurationException e) {
			logger.error("Error initializing datatype factory", e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	@DELETE
	@Path("{id}")
	public Response deleteBug() {
		Persistence.getInstance().writeLock();
		try {
			verifyBugExists();
			Persistence.getInstance().removeBugModel(getRequestURI());
			Persistence.getInstance().commit();
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
		} finally {
			Persistence.getInstance().end();
		}

		setBugResponseHeaders(false);
	}

	@POST
	@Consumes(TEXT_TURTLE)
	public Response createBugTurtle(InputStream in) {
		return createBug(in, Lang.TURTLE);
	}

	@POST
	@Consumes({ APPLICATION_JSON_LD, APPLICATION_JSON })
	public Response createBugJSON(InputStream in) {
		return createBug(in, Lang.JSONLD);
	}

	private Response createBug(InputStream in, Lang lang) {
		setContainerResponseHeaders();

		// Create a URI.
		final String id = UUID.randomUUID().toString();
		final URI location = getRequestURIBuilder().path(id).build();

		// Read the model ourselves so we can set the correct base to resolve relative URIs.
		final Model m = ModelFactory.createDefaultModel();
		try {
			m.read(in, location.toString(), lang.getName());

			// Add a dcterms:created triple
			Resource r = m.createResource(location.toString());
			if (!r.hasProperty(DCTerms.created)) {
				r.addProperty(DCTerms.created,
							  m.createTypedLiteral(Calendar.getInstance()));
			}
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		Persistence.getInstance().writeLock();
		try {
			Persistence.getInstance().addBugModel(m, location);
			Persistence.getInstance().commit();
		} finally {
			Persistence.getInstance().end();
		}

		return Response.created(location).build();
	}

	@GET
	@Path("sparql")
	@Produces(TEXT_HTML)
	public void showSPARQLPage()
			throws ServletException, IOException {
		request.setAttribute("baseURI", getBaseURI());
		request.setAttribute("endpoint", getRequestURI());
		request.getRequestDispatcher("/WEB-INF/sparql.jsp").
			forward(request, response);
	}

	@POST
	@Path("sparql")
	@Consumes(APPLICATION_SPARQL_QUERY)
	@Produces(APPLICATION_SPARQL_RESULTS_JSON)
	public StreamingOutput postQuery(final String queryString) {
		return new StreamingOutput() {
			public void write(OutputStream out) throws IOException, WebApplicationException {
				Persistence.getInstance().readLock();
				try {
					Persistence.getInstance().query(queryString, out);
				} catch (Exception e) {
					throw new WebApplicationException(Response.status(Status.BAD_REQUEST).build());
				} finally {
					Persistence.getInstance().end();
				}
			}
		};
	}

	private void respond(final OutputStream out,
	                     final Model m,
	                     final String lang,
	                     final String base) throws IOException {
		String etag = ETag.generate(m, lang, base);
		testIfNoneMatch(etag);
		setETagHeader(etag);
		m.write(out, lang, base);
	}

	private void respond(final OutputStream out,
	                     final JsonObject json) throws IOException {
		String etag = ETag.generate(json);
		testIfNoneMatch(etag);
		setETagHeader(etag);
		json.output(new IndentedWriter(out));
	}

	private String getBaseURI() {
		return getBaseUriBuilder().path("..").build().normalize().toString().replaceAll("/$", "");
	}

	private URI getStaticResource(String path) {
		return getBaseUriBuilder().path("..").path(path).build().normalize();
	}

	private String getBugURI(String id) {
		return getBaseUriBuilder().path("bugs/{id}").build(id).toString();
	}

	private URI getDialogURI() {
		return getBaseUriBuilder().path("bugs/creationDialog").build();
	}

	private URI getIconURI() {
		return getStaticResource("oslc-16x16.png");
	}

	private UriBuilder getRequestURIBuilder() {
		// uriInfo.getRequestUriBuilder() incorrectly adds the default port in
		// some environments (e.g., Bluemix)
		return UriBuilder.fromUri(getRequestURI());
	}

	private UriBuilder getBaseUriBuilder() {
		// uriInfo.getBaseUriBuilder() incorrectly adds the default port in
		// some environments (e.g., Bluemix)
		try {
			final String basePath = uriInfo.getBaseUri().getRawPath();
			final URI baseURI = new URI(getRequestURI()).resolve(basePath);

			return UriBuilder.fromUri(baseURI);
		} catch (URISyntaxException e) {
			// Should never happen.
			logger.error("Error determining base URI", e);
			throw new RuntimeException(e);
		}
	}

	private String getRequestURI() {
		return request.getRequestURL().toString();
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
		String document = getStaticResource("creationDialog.html").toString();
		dialog.addProperty(OSLC.dialog, m.createResource(document));
		dialog.addProperty(OSLC.hintWidth, "450px");
		dialog.addProperty(OSLC.hintHeight, "395px");
	}

	private Model getBugModel(String uri) {
		final Model model = Persistence.getInstance().getBugModel(uri);
		if (model == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return model;
	}

	private void setLinkHeader(URI uri, String relation) {
		setLinkHeader(uri.toString(), relation);
	}

	private void setLinkHeader(String uri, String relation) {
		response.addHeader(LINK, "<" + uri + ">; rel=\"" + relation + "\"");
	}

	private void setBugResponseHeaders() {
		setBugResponseHeaders(true);
	}

	private void setBugResponseHeaders(boolean includeCacheHeaders) {
		response.addHeader(ALLOW, "GET,HEAD,OPTIONS,DELETE");
		if (includeCacheHeaders) {
			response.addHeader(CACHE_CONTROL, "no-cache");
			response.addHeader(VARY, "Accept,Prefer");
		}
		setLinkHeader(LDP.Resource.getURI(), LINK_REL_TYPE);
		setLinkHeader(uriInfo.getAbsolutePathBuilder().path("compact").build(), LINK_REL_COMPACT);
	}

	private void setContainerResponseHeaders() {
		setContainerResponseHeaders(true);
	}

	private void setContainerResponseHeaders(boolean includeCacheHeaders) {
		// LDP Headers
		response.addHeader(ALLOW, "GET,HEAD,POST,OPTIONS");
		response.addHeader(ACCEPT_POST, TEXT_TURTLE + "," + APPLICATION_JSON + "," + APPLICATION_JSON);
		if (includeCacheHeaders) {
			response.addHeader(CACHE_CONTROL, "no-cache");
			response.addHeader(VARY, "Accept,Prefer");
		}
		setLinkHeader(LDP.Resource.getURI(), LINK_REL_TYPE);
		setLinkHeader(LDP.BasicContainer.getURI(), LINK_REL_TYPE);

		// LDP constrainedBy header should point to the resource shape
		URI shape = uriInfo.getBaseUriBuilder().path("../Defect-shape.ttl").build().normalize();
		setLinkHeader(shape, LINK_REL_CONSTRAINED_BY);

		// OSLC Creation Dialog
		setLinkHeader(getDialogURI(), LINK_REL_CREATION_DIALOG);
	}

	private void setETagHeader(String etag) {
		response.addHeader(ETAG, etag);
	}

	private void verifyBugExists() {
		if (!Persistence.getInstance().exists(getRequestURI())) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	private void parsePrefer() {
		final List<String> preferValues = headers.getRequestHeader(PREFER);
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
		final List<String> ifNoneMatchValues = headers.getRequestHeader(IF_NONE_MATCH);
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
		response.setHeader(PREFERENCE_APPLIED, "return=representation");
	}
}
