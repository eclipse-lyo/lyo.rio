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
 *    Ernest Mah - Initial implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.util;

import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
	private static final String CONFIG_PROPERTIES = "config.properties";
	
	private static Properties prop = null;
		
	/**
	 * This method returns the list of properties found in the config.properties
	 * file.  This file provides a mechanism for users to control the operation
	 * of the reference application.
	 */
	public static Properties getPropertiesInstance() 
	{
		if (prop == null) {
			prop = new Properties();
		
			try {
				prop.load(ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IllegalStateException();
			}
		}

		return prop;
	}
}
