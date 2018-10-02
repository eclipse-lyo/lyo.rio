/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation.
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
 *    Jim Conallen - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.rio.core;

@SuppressWarnings("nls")
public interface IRioConstants {

	public static final String SERVER_SCHEME = "http://";  
	public static final int DEFAULT_MAX_RESULTS = 100;
	
	// RIO
	public static final String RIO_PUBLISHER_TITLE = "Open Services for Lifecycle Collaboration in Architecture Management"; 
	public static final String RIO_PUBLISHER_IDENTIFIER = "open-services.net/ri/am"; 
	public static final String RIO_ICON = "oslc.png"; 
	
	public static final String RIO_URI = "http://open-services.net/ri"; 
	public static final String RIO_NAMESPACE = "http://open-services.net/ri/"; 
	public static final String RIO_PREFIX = "rio"; 
	public static final String RIO_XMLS_DECL = "\txmlns:" + RIO_PREFIX + "=\"" +  RIO_NAMESPACE + "\"\n";  

	public static final String URI_COUNTER = RIO_NAMESPACE + "uriCounter"; 
	public static final String URI_SERVER = RIO_NAMESPACE + "server"; 

	public static final String RIO_UNKNOWN_USER_ID = "_UNKNOWN_USER_";


	// additional content types
	public static final String CT_APP_X_VND_MSPPT = "application/vnd.ms-powerpoint"; 

}
