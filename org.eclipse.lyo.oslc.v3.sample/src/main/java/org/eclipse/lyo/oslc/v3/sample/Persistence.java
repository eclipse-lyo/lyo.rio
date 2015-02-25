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

import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.block.FileMode;
import com.hp.hpl.jena.tdb.sys.SystemTDB;

import static org.eclipse.lyo.oslc.v3.sample.Constants.LDP;

/**
 * Stores resources using Jena TDB. Each resource has its own graph in the
 * dataset where the graph name is the resource URI. Defaults to an in-memory
 * dataset unless system property <code>dataset.dir</code> is set.
 */
public class Persistence {
	private final static Persistence instance = new Persistence();

	public static final String DATASET_DIR_PROP = "dataset.dir";
	private final static String DATASET_DIR = System.getProperty(DATASET_DIR_PROP);

	private final Dataset dataset;

	private Persistence() {
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

	/**
	 * Begins a read transaction.
	 *
	 * @see <a href="https://jena.apache.org/documentation/tdb/tdb_transactions.html">TDB Transactions</a>
	 */
	public void readLock() {
		dataset.begin(ReadWrite.READ);
	}

	/**
	 * Begins a write transaction.
	 *
	 * @see <a href="https://jena.apache.org/documentation/tdb/tdb_transactions.html">TDB Transactions</a>
	 */
	public void writeLock() {
		dataset.begin(ReadWrite.WRITE);
	}

	/**
	 * Commits the changes.
	 *
	 * @see <a href="https://jena.apache.org/documentation/tdb/tdb_transactions.html">TDB Transactions</a>
	 */
	public void commit() {
		dataset.commit();
	}

	/**
	 * Ends the transaction.
	 *
	 * @see <a href="https://jena.apache.org/documentation/tdb/tdb_transactions.html">TDB Transactions</a>
	 */
	public void end() {
		dataset.end();
	}

	public void addContainmentTriples(Resource container) {
		final Model model = container.getModel();
		final Iterator<Node> iter = dataset.asDatasetGraph().listGraphNodes();
		while (iter.hasNext()) {
			Node n = iter.next();
			container.addProperty(model.createProperty(LDP, "contains"),
			                      model.createResource(n.getURI()));
		}
	}

	public void addBugModel(Model m, URI location) {
		dataset.addNamedModel(location.toString(), m);
	}

	public Model getBugModel(String uri) {
		if (!dataset.containsNamedModel(uri)) {
			return null;
		}

		return dataset.getNamedModel(uri);
	}

	public void removeBugModel(String uri) {
		dataset.removeNamedModel(uri);
	}

	public boolean exists(String uri) {
		return dataset.containsNamedModel(uri);
	}

	public void query(String queryString, OutputStream out) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		qexec.getContext().set(TDB.symUnionDefaultGraph, true);
		ResultSet result = qexec.execSelect();
		try {
			ResultSetFormatter.outputAsJSON(out, result);
		} finally {
			qexec.close();
		}
	}
}
