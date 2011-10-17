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
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;

/**
 * Servlet implementation class Users
 */
public class UsersService extends RioBaseService {

	private static final long serialVersionUID = 7026212382207417492L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String reqUrl = request.getRequestURL().toString();
		URI reqUri = URI.create(reqUrl);
		String urlPath = reqUri.getPath();
		String[] segments = urlPath.split("/");
		if( segments.length != 4 ) {
			throw new RioServiceException(IConstants.SC_BAD, "Invalid user URI");
		}
		
		String rdf = 
			"<rdf:RDF \n" +  
			"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +     
			"xmlns:foaf=\"http://http://xmlns.com/foaf/0.1/\"> \n" +   
			"<foaf:Person rdf:about=\"" + reqUri + "\"/>" +
			"</rdf:RDF>";
		
		response.getWriter().write(rdf);
		response.setContentType(IConstants.CT_RDF_XML);
		response.setStatus(IConstants.SC_OK);

		// return a FOAF resource
//		String id = segments[3];
//		<foaf:Person rdf:about="#danbri" xmlns:foaf="http://xmlns.com/foaf/0.1/">
//		  <foaf:name>Dan Brickley</foaf:name>
//		  <foaf:givenName></foaf:givenName>
//		  <foaf:familyName></foaf:familyName>
//		  <foaf:mbox></foaf:mbox>
//		</foaf:Person>

		
	}

}
