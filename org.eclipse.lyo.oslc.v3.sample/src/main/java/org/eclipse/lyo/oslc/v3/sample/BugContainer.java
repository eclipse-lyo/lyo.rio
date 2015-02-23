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

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.digest.DigestUtils;
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
	@Context HttpServletResponse response;
	@Context UriInfo uriInfo;

	@GET
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getBugContainer() {
		setContainerResponseHeaders();

		Model response = ModelFactory.createDefaultModel();
		Resource container =
				response.createResource(uriInfo.getAbsolutePath().toString(),
				                        response.getResource(LDP + "BasicContainer"));
		container.addProperty(DCTerms.title, "Bug Container");
		Persistence.getInstance().addContainmentTriples(container);
		setETagHeader(response);

		return response;
	}

	@OPTIONS
	public void bugContainerOptions() {
		setContainerResponseHeaders();
	}

	@GET
	@Path("creationDialog")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getCreationDialogDescriptor() {
		setDialogDescriptorResponseHeaders();
		Model response = createDialogModel();
		setETagHeader(response);

		return response;
	}

	@OPTIONS
	@Path("creationDialog")
	public void creationDialogDescriptorOptions() {
		setDialogDescriptorResponseHeaders();
	}

	@GET
	@Path("{id}")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getBug() {
		Model response = Persistence.getInstance().getBugModel(requestURI());
		setResourceResponseHeaders();
		setETagHeader(response);

		return response;
	}

	@DELETE
	@Path("{id}")
	public Response deleteBug() {
		Persistence.getInstance().removeBug(requestURI());
		return Response.noContent().build();
	}

	@OPTIONS
	@Path("{id}")
	public void bugOptions() {
		Persistence.getInstance().verifyBugExists(requestURI());
		setResourceResponseHeaders();
	}

	@POST
	@Consumes({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Response createBug(Model m) {
		if (System.getProperty("constrainContent") != null
				&& !m.contains(m.getResource(""),
				               RDF.type,
				               OSLC_CM.Defect)) {
			return Response.status(Status.CONFLICT).build();
		}

		String id = UUID.randomUUID().toString();
		URI location = uriInfo.getAbsolutePathBuilder().path(id).build();
		Persistence.getInstance().addBugModel(m, location);
		setContainerResponseHeaders();

		return Response.created(location).build();
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
		URI creationDialog = uriInfo.getAbsolutePathBuilder().path("creationDialog").build();
		response.addHeader("Link", "<" + creationDialog + ">;rel=\"" + OSLC.NS + "creationDialog\"");
	}

	private void setResourceResponseHeaders() {
		response.addHeader("Link", "<" + LDP + "Resource"+">;rel=type");
		response.addHeader("Allow", "GET,HEAD,OPTIONS,DELETE");
	}

	private void setDialogDescriptorResponseHeaders() {
		response.addHeader("Link", "<" + LDP + "Resource"+">;rel=type");
		response.addHeader("Allow", "GET,HEAD,OPTIONS");
	}

	private Model createDialogModel() {
		Model m = ModelFactory.createDefaultModel();
		Resource dialog = m.createResource(uriInfo.getAbsolutePath().toString(), OSLC.Dialog);
		dialog.addProperty(OSLC.label, "Open Bug");
		String document = uriInfo.getBaseUriBuilder().path("../newBug.html").build().normalize().toString();
		dialog.addProperty(OSLC.dialog, m.createResource(document));

		return m;
	}

	/**
	 * Create a weak ETag value from a Jena model.
	 *
	 * @param m the model that represents the HTTP response body
	 * @return an ETag value
	 *
	 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">HTTP 1.1: Section 14.19 - ETag</a>
	 */
	private String getETag(Model m) {
		// Get the MD5 hash of the model as N-Triples.
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		m.write(out,  "N-TRIPLE");
		String md5 = DigestUtils.md5Hex(out.toByteArray());

		// Create a weak entity tag from the MD5 hash.
		return "W/\"" + md5 + "\"";
	}

	private void setETagHeader(Model m) {
		response.addHeader("ETag", getETag(m));
	}

	private String requestURI() {
		return uriInfo.getAbsolutePath().toString();
	}
}
