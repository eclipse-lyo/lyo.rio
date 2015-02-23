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

import javax.ws.rs.core.MediaType;

public class Constants {
	// Media types
	public static final String TEXT_TURTLE = "text/turtle";
	public static final String APPLICATION_JSON_LD = "application/ld+json";

	/**
	 * LDP namespace
	 *
	 * @see <a href="http://www.w3.org/TR/ldp/">Linked Data Platform 1.0</a>
	 */
	public static final String LDP = "http://www.w3.org/ns/ldp#";

	/**
	 * @see <a href="http://www.w3.org/TR/ldp/#ldpr-gen-pubclireqs">LDP 4.2.1.6</a>
	 */
	public static final String LINK_REL_CONSTRAINED_BY = LDP + "constrainedBy";
}
