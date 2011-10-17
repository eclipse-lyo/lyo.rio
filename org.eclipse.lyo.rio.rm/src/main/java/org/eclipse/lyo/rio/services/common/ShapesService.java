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
package org.eclipse.lyo.rio.services.common;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.oslc.rm.common.IRmConstants;
import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.util.StringUtils;


/**
 * Servlet implementation class Shapes
 */
public class ShapesService extends RioBaseService {
	private static final long serialVersionUID = -6726427630793373702L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type"); //$NON-NLS-1$
		if( type == null ) {
			throw new RioServiceException(IConstants.SC_BAD, "Shape type missing");
		}
		
		if( IRmConstants.OSLC_RM_TYPE_TERM_REQUIREMENT.equals(type) ) { 
			InputStream is = getClass().getResourceAsStream("resource_shape.xml"); //$NON-NLS-1$
			String shape = StringUtils.isToString(is);
			String about = request.getRequestURL().toString() + "?type=" + IRmConstants.OSLC_RM_TYPE_TERM_REQUIREMENT; //$NON-NLS-1$
			shape = shape.replaceAll("\\$\\{about\\}", about); //$NON-NLS-1$
			response.getWriter().write(shape);
			response.setContentType(IConstants.CT_RDF_XML);
			response.setStatus(IConstants.SC_OK);
		} else if( IRmConstants.OSLC_RM_TYPE_TERM_REQUIREMENTCOLLECTION.equals(type) ) {
			InputStream is = getClass().getResourceAsStream("linktype_shape.xml"); //$NON-NLS-1$
			String shape = StringUtils.isToString(is);
			String about = request.getRequestURL().toString() + "?type=" + IRmConstants.OSLC_RM_TYPE_TERM_REQUIREMENTCOLLECTION; //$NON-NLS-1$
			shape = shape.replaceAll("\\$\\{about\\}", about); //$NON-NLS-1$
			response.getWriter().write(shape);
			response.setContentType(IConstants.CT_RDF_XML);
			response.setStatus(IConstants.SC_OK);
		} else {
			throw new RioServiceException(IConstants.SC_NOT_FOUND, "Unknown type");
		}
		
	}

}
