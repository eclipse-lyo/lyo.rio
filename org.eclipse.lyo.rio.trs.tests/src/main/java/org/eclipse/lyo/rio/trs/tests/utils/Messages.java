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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "org.eclipse.lyo.testsuite.server.trsutils.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
		.getBundle(Messages.BUNDLE_NAME);

	private Messages() {
		// DO Nothing
	}

	public static String getServerString(String key) {
		try {
			return Messages.RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			Messages.checkForWrongKey(key);
			return '!' + key + '!';
		}
	}

	private static void checkForWrongKey(String key) {
		String prefix = "_NoId."; //$NON-NLS-1$
		boolean prefixExists = key.startsWith(prefix);
		String newKey = prefixExists == true ? key.substring(prefix.length()) : prefix + key;
		try {
			Messages.RESOURCE_BUNDLE.getString(newKey);
			String error =
					String.format(
						"The message key \"%s\" is wrong.  It should be \"%s\".", key, newKey); //$NON-NLS-1$
			System.err.println(error);
			throw new RuntimeException(error);
		} catch (MissingResourceException e) {
			// No-op
		}
	}
}