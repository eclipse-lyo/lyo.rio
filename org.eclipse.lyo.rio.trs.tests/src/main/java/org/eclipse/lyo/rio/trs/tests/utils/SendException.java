/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
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
 *    Joseph Leong, Sujeet Mishra - Initial implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.tests.utils;

/**
 * A exception that is thrown when creating/updating a resource encounters an exception
 */
public class SendException extends Exception {
	
	private static final long serialVersionUID = -7111348864477190261L;

	public SendException(String message) {
		super(message);
	}

	public SendException(Throwable th) {
		super(th);
	}

	public SendException(String message, Throwable th) {
		super(message, th);
	}
}
