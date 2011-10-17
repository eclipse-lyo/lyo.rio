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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioServerException;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.util.XmlUtils;


/**
 * Servlet implementation class Setup
 */
public class SetupService extends HttpServlet {
	private static final long serialVersionUID = 1L;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// first determine if the properties a have already been set
		String realpath = getRealPath("WEB-INF/rio.properties"); //$NON-NLS-1$
		Properties  p  = new Properties();
		try {
	        p.load(new FileInputStream(realpath));
	    } catch (Exception intentionallyIgnored) {
	    }

	    String host = p.getProperty("host"); //$NON-NLS-1$
		String context = p.getProperty("context"); //$//$NON-NLS-1$
		String repoPath = p.getProperty("repoPath"); //$NON-NLS-1$
		String binPath = p.getProperty("binPath"); //$NON-NLS-1$
		String url = req.getParameter("url"); //$NON-NLS-1$
		
		if( host != null && repoPath != null && binPath != null && url != null ) {
			// then just initialize and move on
			
			try {
				RioStore.initalizeStore(repoPath, binPath, host, context);
			} catch (RioServerException e) {
				throw new RioServiceException(e);
			}
			// strip off the host and context for the dispatcher
			URI uri = URI.create(url);
			String urlPath = uri.getPath();
			urlPath = urlPath.substring( urlPath.indexOf('/', 1) );
			RequestDispatcher rd = req.getRequestDispatcher(urlPath);
			rd.forward(req, resp);
			return;
		}
		
		String reqUrl = req.getRequestURL().toString();
		URI reqUri = URI.create(reqUrl);
		int port = reqUri.getPort();
		if( host == null ) {
			host = reqUri.getScheme() + "://" + reqUri.getHost() + ':' + Integer.toString(port);; //$NON-NLS-1$
		}
		
		if( repoPath == null ) {
			repoPath = getServletContext().getRealPath("rio/rio-rm/repo"); 
			if( repoPath == null ) {
				// try one last default 
				repoPath = "/rio/rio-rm/repo";
			}
		}
		if( binPath == null ) {
			binPath = getServletContext().getRealPath("rio/rio-rm/bin");
			if( binPath == null ) {
				binPath = "/rio/rio-rm/bin";
			}
		}
		req.setAttribute("host", XmlUtils.encode(host) ); //$NON-NLS-1$
		req.setAttribute("context", XmlUtils.encode(context) ); //$NON-NLS-1$
		req.setAttribute("repoPath", XmlUtils.encode(repoPath) ); //$NON-NLS-1$
		req.setAttribute("binPath", XmlUtils.encode(binPath) ); //$NON-NLS-1$
		req.setAttribute("port", Integer.toString(port) ); //$NON-NLS-1$
		RequestDispatcher rd = req.getRequestDispatcher("/setup.jsp"); //$NON-NLS-1$
		rd.forward(req, resp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String host = request.getParameter("host"); //$NON-NLS-1$
		String context = request.getParameter("context"); //$NON-NLS-1$
		String repoPath = request.getParameter("repoPath"); //$NON-NLS-1$
		String binPath = request.getParameter("binPath"); //$NON-NLS-1$
		
		if( host != null && repoPath != null ) {
			String realpath = getRealPath("WEB-INF/rio.properties"); //$NON-NLS-1$
			Properties  p  = new Properties();
			p.setProperty("host", host); //$NON-NLS-1$
			p.setProperty("context", context); //$NON-NLS-1$
			p.setProperty("repoPath", repoPath); //$NON-NLS-1$
			p.setProperty("binPath", binPath); //$NON-NLS-1$
			try {
		        p.store(new FileOutputStream(realpath), null);
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		
		try {
			RioStore.initalizeStore(repoPath, binPath, host, context);
		} catch (RioServerException e) {
			throw new RioServiceException(e);
		}
		
		response.sendRedirect("index.jsp"); //$NON-NLS-1$
	}

	/**
	 * Get ServletContext's realPath, falling back to 'context.realpath' property if not available.
	 */
	protected String getRealPath(String path) {
		String realpath = getServletContext().getRealPath(path); //$NON-NLS-1$
		if (realpath == null) {
			realpath = System.getProperty("context.realpath") + "/" + path;
		}
		return realpath;
	}
}
