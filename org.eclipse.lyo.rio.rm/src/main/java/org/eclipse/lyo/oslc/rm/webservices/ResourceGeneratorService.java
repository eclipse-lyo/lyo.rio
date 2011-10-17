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
package org.eclipse.lyo.oslc.rm.webservices;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.oslc.rm.common.IRmConstants;
import org.eclipse.lyo.oslc.rm.services.requirement.Requirement;

import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.util.RandomTextGenerator;


public class ResourceGeneratorService extends RioBaseService {

	private static final long serialVersionUID = 8339173914617471636L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String countParam = request.getParameter("count"); //$NON-NLS-1$
		int count = 10;
		if( countParam != null  ) {
			count = Integer.parseInt(countParam);
		}
		
		Random rnd = new Random(System.currentTimeMillis());
		RioStore store = this.getStore();
		
		try{
			for(int i=0;i<count;i++) {
				int titleLen = rnd.nextInt(6) + 2;
				RandomTextGenerator gen = new RandomTextGenerator();
				String title = gen.generateText(titleLen);
				String shortTitle = gen.generateText(titleLen/2);
				int descriptionLen = rnd.nextInt(50) + 50;
				String description = gen.generateText(descriptionLen);
				
				String uri = store.nextAvailableUri(IRmConstants.SERVICE_REQUIREMENT);
				Requirement resource = new Requirement(uri);
				resource.addRdfType(IRmConstants.OSLC_RM_TYPE_REQUIREMENT);
				resource.setTitle(title);
				resource.setDescription(description);
				resource.setShortTitle(shortTitle);
				store.update(resource, null);

			}
		} catch( Exception e ) {
            e.printStackTrace();
			throw new RioServiceException(e);
		}
		
		response.sendRedirect( "/" + store.getServerContext() + "/list/requirement"); //$NON-NLS-1$
		
	}

}
