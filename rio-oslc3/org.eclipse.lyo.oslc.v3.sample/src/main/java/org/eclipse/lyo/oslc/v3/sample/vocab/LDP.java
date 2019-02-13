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
 *	 Samuel Padgett	   - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc.v3.sample.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class LDP {
	private static Model model = ModelFactory.createDefaultModel();
	/**
	 * LDP namespace
	 *
	 * @see <a href="http://www.w3.org/TR/ldp/">Linked Data Platform 1.0</a>
	 */
	public static final String NS = "http://www.w3.org/ns/ldp#";

	public static final Resource Resource = model.createResource(NS + "Resource");
	public static final Resource BasicContainer = model.createResource(NS + "BasicContainer");

	public static final Property contains = model.createProperty(NS + "contains");

	/**
	 * @see <a href="http://www.w3.org/TR/ldp/#ldpr-gen-pubclireqs">LDP 4.2.1.6</a>
	 */
	public static final String LINK_REL_CONSTRAINED_BY = NS + "constrainedBy";
}
