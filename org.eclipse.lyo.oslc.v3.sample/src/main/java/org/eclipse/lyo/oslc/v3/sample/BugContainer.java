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
import java.util.Iterator;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.block.FileMode;
import com.hp.hpl.jena.tdb.sys.SystemTDB;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.lyo.oslc.v3.sample.Constants.APPLICATION_JSON_LD;
import static org.eclipse.lyo.oslc.v3.sample.Constants.LDP;
import static org.eclipse.lyo.oslc.v3.sample.Constants.OSLC_CM;
import static org.eclipse.lyo.oslc.v3.sample.Constants.TEXT_TURTLE;

@Path("/bugs")
public class BugContainer {
	public static final String DATASET_DIR_PROP = "dataset.dir";

	private final static String DATASET_DIR = System.getProperty(DATASET_DIR_PROP);
	static {
		TDB.getContext().set(TDB.symUnionDefaultGraph, true);
		TDB.setOptimizerWarningFlag(false);
		SystemTDB.setFileMode(FileMode.direct) ;
		System.out.println("Using dataset directory: " + DATASET_DIR);
		if (DATASET_DIR == null) {
			dataset = TDBFactory.createDataset();
		} else {
			dataset = TDBFactory.createDataset(DATASET_DIR);
		}
	}

	private static final Dataset dataset;

	@Context HttpServletResponse response;
	@Context UriInfo uriInfo;

	@GET
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getBugs() {
		setContainerResponseHeaders();

		// Get the union of all graphs.
		Model response = ModelFactory.createDefaultModel();
		Resource container =
				response.createResource(uriInfo.getAbsolutePath().toString(),
				                        response.getResource(LDP + "BasicContainer"));
		container.addProperty(DCTerms.title, "Bug Container");
		addContainmentTriples(response, container);

		return response;
	}

	@OPTIONS
	public void bugContainerOptions() {
		setContainerResponseHeaders();
	}

	@GET
	@Path("{id}")
	@Produces({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Model getBug() {
		// Get the union of all graphs.
		Model response = getBugModel(uriInfo.getAbsolutePath());
		if (response == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		setResourceResponseHeaders();
		return response;
	}

	@OPTIONS
	@Path("{id}")
	public void bugOptions() {
		// Get the union of all graphs.
		Model response = getBugModel(uriInfo.getAbsolutePath());
		if (response == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		setResourceResponseHeaders();
	}

	@POST
	@Consumes({ TEXT_TURTLE, APPLICATION_JSON_LD, APPLICATION_JSON })
	public Response createBug(Model m) {
		// Is the null-relative resource a Defect?
		if (!m.contains(m.getResource(""),
		                RDF.type,
		                m.getResource(OSLC_CM + "Defect"))) {
			// TODO: constrainedBy link header
			return Response.status(Status.CONFLICT).build();
		}

		String id = UUID.randomUUID().toString();
		URI location = uriInfo.getAbsolutePathBuilder().path(id).build();
		addBugModel(m, location);
		setContainerResponseHeaders();

		return Response.created(location).build();
	}

	private void setContainerResponseHeaders() {
		response.addHeader("Link", "<" + LDP + "Resource"+">;rel=type,<" + LDP + "Container>;rel=type");
		response.addHeader("Allow", "GET,HEAD,POST,OPTIONS");
		response.addHeader("Allow-Post", TEXT_TURTLE + "," + APPLICATION_JSON + "," + APPLICATION_JSON);
	}

	private void setResourceResponseHeaders() {
		response.addHeader("Link", "<" + LDP + "Resource"+">;rel=type");
		response.addHeader("Allow", "GET,HEAD,OPTIONS");
	}

	private void addContainmentTriples(Model response, Resource container) {
		dataset.begin(ReadWrite.READ);
		try {
			final Iterator<Node> iter = dataset.asDatasetGraph().listGraphNodes();
			while (iter.hasNext()) {
				Node n = iter.next();
				container.addProperty(response.getProperty(LDP, "contains"),
				                      response.getResource(n.getURI()));
			}
		} finally {
			dataset.end();
		}
	}

	private void addBugModel(Model m, URI location) {
		dataset.begin(ReadWrite.WRITE);
		try {
			dataset.addNamedModel(location.toString(), m);
			dataset.commit();
		} finally {
			dataset.end();
		}
	}

	private Model getBugModel(URI uri) {
		dataset.begin(ReadWrite.READ);
		try {
			return dataset.getNamedModel(uri.toString());
		} finally {
			dataset.end();
		}
	}
}
