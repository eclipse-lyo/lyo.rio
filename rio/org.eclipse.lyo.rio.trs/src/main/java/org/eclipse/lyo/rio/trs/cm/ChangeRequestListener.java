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
 *    David Terry - Initial implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.cm;


/**
 * Interface defining callback for registered listeners of Change Request CRUD
 * events.
 *
 */
public interface ChangeRequestListener {
	public void changeRequestAltered(ChangeRequest changeRequest, String type);
}
