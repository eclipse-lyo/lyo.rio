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
package org.eclipse.lyo.oslc.v3.sample.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class OSLC {
    private static Model model = ModelFactory.createDefaultModel();
	public static final String NS = "http://open-services.net/ns/core#";

    public static final Resource Compact = model.createResource(NS + "Compact");
    public static final Resource Dialog = model.createResource(NS + "Dialog");
    public static final Resource Preview = model.createResource(NS + "Preview");

    public static final Property creationType = model.createProperty(NS + "creationType");
    public static final Property label = model.createProperty(NS + "label");
    public static final Property dialog = model.createProperty(NS + "dialog");
    public static final Property document = model.createProperty(NS + "document");
    public static final Property hintHeight = model.createProperty(NS + "hintHeight");
    public static final Property hintWidth = model.createProperty(NS + "hintWidth");
    public static final Property icon = model.createProperty(NS + "icon");
    public static final Property largePreview = model.createProperty(NS + "largePreview");
    public static final Property smallPreview = model.createProperty(NS + "smallPreview");

	public static final String LINK_REL_COMPACT = Compact.getURI().toString();
	public static final String LINK_REL_CREATION_DIALOG = NS + "creationDialog";
}
