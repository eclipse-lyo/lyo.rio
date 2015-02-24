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

	/**
	 * An ETag for the container based on the containment. The other properties
	 * of the container are not editable, so we can simply create a new ETag
	 * value when bugs are added or removed.
	 */
	private String etag;

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
		etag = ETag.generateRandom();
	}

	public static Persistence getInstance() {
		return instance;
	}

	public void readLock() {
		dataset.begin(ReadWrite.READ);
	}

	public void writeLock() {
		dataset.begin(ReadWrite.WRITE);
	}

	public void commit() {
		dataset.commit();
	}

	public void end() {
		dataset.end();
	}

	/**
	 * Gets the ETag for the container.
	 *
	 * @return
	 */
	public String getETag() {
		return etag;
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

		// Generate a new ETag value.
		etag = ETag.generateRandom();
	}

	public Model getBugModel(String uri) {
		return dataset.getNamedModel(uri);
	}

	public void removeBug(String uri) {
		dataset.removeNamedModel(uri);

		// Generate a new ETag value.
		etag = ETag.generateRandom();
	}

	public boolean exists(String uri) {
		return dataset.containsNamedModel(uri);
	}
}
