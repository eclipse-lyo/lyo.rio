/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
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
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation;

import java.net.URI;

public enum Verdict {
	Unavailable(Constants.AUTOMATION_NAMESPACE + "unavailable"),
	Fail(Constants.AUTOMATION_NAMESPACE + "fail"),
	Pass(Constants.AUTOMATION_NAMESPACE + "pass"),
	Warning(Constants.AUTOMATION_NAMESPACE + "warning"),
	Error(Constants.AUTOMATION_NAMESPACE + "error");

	private final String uri;

	Verdict(final String uri) {
		this.uri = uri;
	}

	@Override
    public String toString() {
		return uri;
	}

	public static Verdict fromString(final String string) {
        final Verdict[] values = Verdict.values();
        for (final Verdict value : values) {
            if (value.uri.equals(string)) {
                return value;
            }
        }
        return null;
    }

	public static Verdict fromURI(final URI uri) {
		return fromString(uri.toString());
	}
}
