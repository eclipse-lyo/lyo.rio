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
package org.eclipse.lyo.rio.cm.web;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.lyo.rio.cm.changerequest.ChangeRequest;
import org.eclipse.lyo.rio.cm.common.ICmConstants;

import org.eclipse.lyo.rio.services.RioBaseService;
import org.eclipse.lyo.rio.services.RioServiceException;
import org.eclipse.lyo.rio.store.RioStore;
import org.eclipse.lyo.rio.util.RandomTextGenerator;


public class ChangeRequestGeneratorService extends RioBaseService {

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
				int titleLen = rnd.nextInt(3) + 2;
				RandomTextGenerator gen = new RandomTextGenerator();
				String title = gen.generateText(titleLen);
				int descriptionLen = rnd.nextInt(50) + 50;
				String description = gen.generateText(descriptionLen);
				
				String uri = store.nextAvailableUri(ICmConstants.SERVICE_CHANGEREQUEST);
				ChangeRequest cr = new ChangeRequest(uri);
				cr.addRdfType(ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
				cr.setTitle(title);
				cr.setDescription(description);
				
				// set status 
				int statusIndex = rnd.nextInt(4);
				cr.setStatus( ICmConstants.OSLC_CM_STATUS_VALUES[statusIndex] );
				cr.setApproved(rnd.nextBoolean());
				cr.setReviewed(rnd.nextBoolean());
				cr.setVerified(rnd.nextBoolean());
				
				store.update(cr, null);

			}
		} catch( Exception e ) {
            e.printStackTrace();
			throw new RioServiceException(e);
		}
		
		response.sendRedirect( "/" + store.getServerContext() + "/list/changerequest"); //$NON-NLS-1$
		
	}
	

}
