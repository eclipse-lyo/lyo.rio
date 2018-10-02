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
package org.eclipse.lyo.rio.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.util.XmlUtils;



public class RioError {
	private List<RioExtendedError> extendedErrors = new ArrayList<RioExtendedError>();
	private int status;
	private String msg;
	
	public RioError(int status, String message ){
		this.status = status;
		if( message != null ) {
			this.msg = XmlUtils.encode(message);
		} else {
			this.msg = ""; //$NON-NLS-1$
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(IConstants.XML_DECLARATION); 
		sb.append("<" + IConstants.RDF_TYPE_PTERM_RDF + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ 
		sb.append(IConstants.RDF_XMLS_DECL);  
		sb.append(IConstants.OSLC_XMLS_DECL);  
		sb.append(">\n");   //$NON-NLS-1$
		sb.append("\t<" + IConstants.OSLC_TYPE_PTERM_ERROR + ">\n"); //$NON-NLS-1$ //$NON-NLS-2$ 
		sb.append("\t\t<oslc:statusCode>" + status + "</oslc:statusCode>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\t\t<oslc:message>" + msg + "</oslc:message>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\t</" + IConstants.OSLC_TYPE_PTERM_ERROR + ">\n"); //$NON-NLS-1$ //$NON-NLS-2$
		
		for (RioExtendedError ext : extendedErrors) {
			sb.append(ext.toString());
		}
		
		
		sb.append("</" + IConstants.RDF_TYPE_PTERM_RDF + ">\n"); //$NON-NLS-1$ //$NON-NLS-2$ 
		return sb.toString();
	}

}
