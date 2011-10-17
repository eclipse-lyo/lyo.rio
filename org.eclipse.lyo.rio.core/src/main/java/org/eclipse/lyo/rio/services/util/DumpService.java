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
package org.eclipse.lyo.rio.services.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.core.IConstants;
import org.eclipse.lyo.rio.l10n.Messages;
import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioResource;
import org.eclipse.lyo.rio.store.RioStatement;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.util.StringUtils;

public class DumpService extends RioBaseService {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("nls")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			RioStore store = RioStore.getStore();
			PrintWriter writer = response.getWriter();
			writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
			writer.write("<html>");
			writer.write("<head>");
			writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
			writer.write("<link rel=\"SHORTCUT ICON\" href=\"oslc.png\">");
			writer.write("<title>Dumping OSLC RDF Store</title>");
			writer.write("</head>");
			writer.write("<body>");
			writer.write("<p><a href=\"" + store.getUriBase() + "/index.jsp\">Home</a></p>");
			dumpHtml(writer);
			writer.write("</body>");
			writer.write("</html>");
			response.setContentType(IConstants.CT_HTML); 
			response.setStatus(IConstants.SC_OK);
		} catch( Exception e ) {
			throw new RioServiceException(500, e);
		}
	}

	public void dumpHtml(Writer writer) throws RioServiceException {
		RioStore store = getStore();
		List<String> resourceContextIds = store.getResourceContexts(); 
		try{
			writer.write( "<h1>Dumping Repository:</h1>" ); //$NON-NLS-1$
			for (String resourceContextId : resourceContextIds) {

				String encodedUri = URLEncoder.encode(resourceContextId, IConstants.TEXT_ENCODING);
				String editUri = "edit?uri=" + encodedUri;
				writer.write( "<p><b>" + Messages.getString("RioStore.Resource") 
						+ ": <a href=\"" + resourceContextId + "\">" 
						+ resourceContextId + "</a>&nbsp;<a href=\"" + editUri + "\">Edit</a>&nbsp;<a href=\"" + editUri + "&delete\">Delete</a></b></p>"); 
				writer.write( "<blockquote><pre>"); 

				RioResource resource = store.getResource(resourceContextId);
				List<RioStatement> statements = resource.getStatements();
				for (RioStatement rioStatement : statements) {
					writer.write( StringUtils.forHtml(rioStatement.toString()) + " . \n" );
				}
				writer.write( "</pre></blockquote>"); //$NON-NLS-1$ 
			
			}
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	
}
