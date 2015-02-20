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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.lyo.oslc.v3.sample.provider.ModelMessageBodyReader;
import org.eclipse.lyo.oslc.v3.sample.provider.ModelMessageBodyWriter;

public class Application extends javax.ws.rs.core.Application {
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();

		// Resources
		classes.add(BugContainer.class);
		
		// Providers
		classes.add(ModelMessageBodyReader.class);
		classes.add(ModelMessageBodyWriter.class);

		return classes;
	}
}
