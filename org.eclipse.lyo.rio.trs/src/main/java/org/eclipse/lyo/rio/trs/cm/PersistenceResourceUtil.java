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

package org.eclipse.lyo.rio.trs.cm;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.lyo.rio.trs.util.IResourceUtil;

public final class PersistenceResourceUtil
       implements IResourceUtil
{
	public static PersistenceResourceUtil instance = new PersistenceResourceUtil();

	@Override
	public List<URI> getAllResourceURIs() {
		List<URI> uris = new ArrayList<URI>();
		
		ChangeRequest[] allChangeRequests = Persistence.getAllChangeRequests();
		
		for (ChangeRequest currentRequest : allChangeRequests) {
			uris.add(currentRequest.getAbout());
		}
		
		return uris;
	}
}
