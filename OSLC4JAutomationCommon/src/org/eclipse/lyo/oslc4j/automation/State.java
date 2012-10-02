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
 *     Michael Fiedler       - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.automation;

import java.net.URI;

public enum State {
	New(Constants.AUTOMATION_NAMESPACE + "new"),
	Queued(Constants.AUTOMATION_NAMESPACE + "queued"),
	InProgress(Constants.AUTOMATION_NAMESPACE + "inProgress"),
	Canceling(Constants.AUTOMATION_NAMESPACE + "canceling"),
	Canceled(Constants.AUTOMATION_NAMESPACE + "canceled"),
	Complete(Constants.AUTOMATION_NAMESPACE + "complete");

	private final String uri;

	State(final String uri) {
		this.uri = uri;
	}

	@Override
    public String toString() {
		return uri;
	}

	public static State fromString(final String string) {
        final State[] values = State.values();
        for (final State value : values) {
            if (value.uri.equals(string)) {
                return value;
            }
        }
        return null;
    }

	public static State fromURI(final URI uri) {
		return fromString(uri.toString());
	}
}
