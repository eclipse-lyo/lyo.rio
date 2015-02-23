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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.block.FileMode;
import com.hp.hpl.jena.tdb.sys.SystemTDB;

import static org.eclipse.lyo.oslc.v3.sample.Constants.LDP;

public class Persistence {
	private final static Persistence instance = new Persistence();

	public static final String DATASET_DIR_PROP = "dataset.dir";
	private final static String DATASET_DIR = System.getProperty(DATASET_DIR_PROP);

	private final Dataset dataset;

	private Persistence() {
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

	public static Persistence getInstance() {
		return instance;
	}

	public void addContainmentTriples(Model response, Resource container) {
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

	public void addBugModel(Model m, URI location) {
		dataset.begin(ReadWrite.WRITE);
		try {
			dataset.addNamedModel(location.toString(), m);
			dataset.commit();
		} finally {
			dataset.end();
		}
	}

	public Model getBugModel(String uri) {
		dataset.begin(ReadWrite.READ);
		try {
			verifyBugExists(uri);
			return dataset.getNamedModel(uri);
		} finally {
			dataset.end();
		}
	}

	public void removeBug(String uri) {
		dataset.begin(ReadWrite.WRITE);
		try {
			verifyBugExists(uri);
			dataset.removeNamedModel(uri);
			dataset.commit();
		} finally {
			dataset.end();
		}
	}

	public void verifyBugExists(String uri) {
		final boolean newTransaction = !dataset.isInTransaction();
		if (newTransaction) {
			dataset.begin(ReadWrite.READ);
		}

		try {
			if (!dataset.containsNamedModel(uri)) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
		} finally {
			if (newTransaction) {
				dataset.end();
			}
		}
	}
}
