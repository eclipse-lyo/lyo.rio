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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.eclipse.lyo.oslc.v3.sample.vocab.OSLC;
import org.eclipse.lyo.oslc.v3.sample.vocab.OSLC_CM;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.lyo.oslc.v3.sample.Constants.APPLICATION_JSON_LD;
import static org.eclipse.lyo.oslc.v3.sample.Constants.LDP;
import static org.eclipse.lyo.oslc.v3.sample.Constants.TEXT_TURTLE;

@Path("/bugs")
public class BugContainer {
	@Context HttpServletResponse httpResponse;
	@Context UriInfo uriInfo;
	@Context HttpHeaders requestHeaders;

	private Set<String> include = new HashSet<String>();
	private Set<String> omit = new HashSet<String>();

	@GET
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getBugContainer() {
		setContainerResponseHeaders();

		Model response = ModelFactory.createDefaultModel();
		Resource container =
				response.createResource(uriInfo.getAbsolutePath().toString(),
				                        response.getResource(LDP + "BasicContainer"));
		container.addProperty(DCTerms.title, "Bug Container");

		// Check the Prefer header to see what to include or omit.
		parsePrefer();

		// Use a read lock so the containment triples match the ETag.
		Persistence.getInstance().readLock();
		try {
			String etag = Persistence.getInstance().getETag();
			testIfNoneMatch(etag);
			setETagHeader(etag);

			// Include dialogs?
			if (include.contains(OSLC.NS + "PreferDialog")) {
				setPreferenceAppliedHeader();
				createDialogResource(response);
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

		return response;
	}

	private void testIfNoneMatch(String etag) {
		final String ifNoneMatch = requestHeaders.getHeaderString(ETag.IF_NONE_MATCH_HEADER);
		if (ETag.matches(ifNoneMatch, etag)) {
			throw new WebApplicationException(Status.NOT_MODIFIED);
		}
	}

	/**
	 * Sets the Preference-Applied httpResponse header for preference <code>return=representation</code>.
	 */
	private void setPreferenceAppliedHeader() {
		this.httpResponse.setHeader("Preference-Applied", "return=representation");
	}

	@OPTIONS
	public void bugContainerOptions() {
		setContainerResponseHeaders();
	}

	@GET
	@Path("creationDialog")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getCreationDialogDescriptor() {
		Model response = createDialogModel();
		return response;
	}

	@OPTIONS
	@Path("creationDialog")
	public void creationDialogDescriptorOptions() {
		// Prefill is not yet supported.
		httpResponse.addHeader("Allow", "GET,HEAD,OPTIONS");
	}

	@GET
	@Path("{id}")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getBug() {
		Persistence.getInstance().readLock();
		final Model response;
		try {
			response = Persistence.getInstance().getBugModel(requestURI());
		} finally {
			Persistence.getInstance().end();
		}

		if (response == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		setResourceResponseHeaders();
		String etag = ETag.generate(response);
		testIfNoneMatch(etag);
		setETagHeader(etag);

		return response;
	}

	@DELETE
	@Path("{id}")
	public Response deleteBug() {
		Persistence.getInstance().writeLock();
		try {
			verifyBugExists();
			Persistence.getInstance().removeBug(requestURI());
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

		setResourceResponseHeaders();
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
		} finally {
			Persistence.getInstance().end();
		}

		return Response.created(location).build();
	}

	private void setContainerResponseHeaders() {
		// LDP Headers
		httpResponse.addHeader("Link", "<" + LDP + "Resource"+">;rel=type");
		httpResponse.addHeader("Link", "<" + LDP + "BasicContainer>;rel=type");
		httpResponse.addHeader("Allow", "GET,HEAD,POST,OPTIONS");
		httpResponse.addHeader("Accept-Post", TEXT_TURTLE + "," + APPLICATION_JSON + "," + APPLICATION_JSON);

		// LDP constrainedBy header should point to the resource shape
		URI shape = uriInfo.getBaseUriBuilder().path("../Defect-shape.ttl").build().normalize();
		httpResponse.addHeader("Link", "<" + shape + ">;rel=\"" + Constants.LINK_REL_CONSTRAINED_BY + "\"");

		// OSLC Creation Dialog
		httpResponse.addHeader("Link", "<" + getDialogURI() + ">;rel=\"" + OSLC.NS + "creationDialog\"");
	}

	private URI getDialogURI() {
		return uriInfo.getBaseUriBuilder().path("bugs/creationDialog").build();
	}

	private void setResourceResponseHeaders() {
		httpResponse.addHeader("Link", "<" + LDP + "Resource"+">;rel=type");
		httpResponse.addHeader("Allow", "GET,HEAD,OPTIONS,DELETE");
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
	}

	private void setETagHeader(String etag) {
		httpResponse.addHeader("ETag", etag);
	}

	private String requestURI() {
		return uriInfo.getAbsolutePath().toString();
	}

	private void verifyBugExists() {
		if (!Persistence.getInstance().exists(requestURI())) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	private void parsePrefer() {
		String prefer = requestHeaders.getHeaderString("Prefer");
		if (prefer != null) {
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
}
