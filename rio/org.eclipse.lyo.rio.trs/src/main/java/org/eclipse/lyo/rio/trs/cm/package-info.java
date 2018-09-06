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

@OslcSchema ({
   @OslcNamespaceDefinition(prefix = Constants.CHANGE_MANAGEMENT_NAMESPACE_PREFIX,         namespaceURI = Constants.CHANGE_MANAGEMENT_NAMESPACE),
   @OslcNamespaceDefinition(prefix = Constants.FOAF_NAMESPACE_PREFIX,                      namespaceURI = Constants.FOAF_NAMESPACE),
   @OslcNamespaceDefinition(prefix = Constants.QUALITY_MANAGEMENT_PREFIX,                  namespaceURI = Constants.QUALITY_MANAGEMENT_NAMESPACE),
   @OslcNamespaceDefinition(prefix = Constants.REQUIREMENTS_MANAGEMENT_PREFIX,             namespaceURI = Constants.REQUIREMENTS_MANAGEMENT_NAMESPACE),
   @OslcNamespaceDefinition(prefix = Constants.SOFTWARE_CONFIGURATION_MANAGEMENT_PREFIX,   namespaceURI = Constants.SOFTWARE_CONFIGURATION_MANAGEMENT_NAMESPACE)
 })

package org.eclipse.lyo.rio.trs.cm;

import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespaceDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcSchema;

