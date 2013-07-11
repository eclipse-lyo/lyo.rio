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

package org.eclipse.lyo.rio.trs.util;

import java.net.URI;

import org.eclipse.lyo.core.trs.ChangeEvent;

/**
 * @author ernest
 *
 * This class is used to manage a set of TRSObject's.  Typical applications would just use
 * the TRSObject directly.  The sole purpose of this class is to manage a set of TRSObjects
 * that represent changes to the same type of resource.  In this reference application we 
 * show surfacing a TRS feed through JAX-RS and then also through simple servlets.
 * TRSUtil is needed to make sure the same change is propogated to both TRSObjects.  The reason
 * we need more than one is that the objects contained in the TRSObject have
 * references to urls to access other resources in the same set (say several pages of base
 * resources or several pages of change logs). 
 */
public class TRSUtil {
	/**
	 * insertEventTypeToChangeLog - Insert an event corresponding to the
	 * trsEvent for the resource located at
	 * 
	 * @param trsEvent
	 *            - One of TRS_TYPE_CREATION, TRS_TYPE_MODIFICATION or
	 *            TRS_TYPE_DELETION
	 * @param resource
	 *            - URI of the resource that has undergone the change identified
	 *            by trsEvent
	 * @param changeURN
	 *            - URN (uniform resource name) uniquely identifying a change
	 *            event. The reference application makes calls to
	 *            TRSUtil.getCurrentTimeStampURN() to generate a URN based on
	 *            the current time. In a real implementation a true URN that can
	 *            persist across server restarts should likely be used.
	 */
	public static void insertEventTypeToChangeLog(String trsEvent, URI resource, URI changeURN) {
		// add the event to each inner container helper obj representing JAXRS and generic implementation.
		for (int i = 0 ; i <innerHelpr.length; i++ )
			innerHelpr[i].insertEventTypeToChangeLog(trsEvent, resource, changeURN);
	}

	// if required, this routine will update the URI
	// and prime the base resources
	public static void initialize(IResourceUtil resourceUtil, URI baseURI) {		
		updateTRSResourceURI(resourceUtil, baseURI);
	}
	
	private static boolean IsGenericImplDesired(URI inURI){
		String sUri = inURI.getPath();
		if (sUri.contains(TRS_GENERIC_MARKER))
			return true;
		else
			return false;		
	}
	
	public static TRSObject getTrsObject(IResourceUtil resourceUtil, URI inURI) {
		if (!TRS_URI_INITIALIZED) {	
			initialize(resourceUtil, inURI);
		}
		// from uri find out which Inner container to access...
		if(!IsGenericImplDesired(inURI))
			return innerHelpr[0];
		else
			return innerHelpr[1];
	}
	
	public static void updateTRSResourceURI(IResourceUtil resourceUtil, URI resource) {
		if (!TRS_URI_INITIALIZED) {	
			String sPath = resource.getPath();
			String sContext = null;
			
			// need to access the context (refapps_xxx) from URI as we are using it to support multiple TRS apps.
			String[] subParts = sPath.split("/");
			if (subParts.length > 1) 
				sContext = subParts[1];
			
			TRS_URI_INITIALIZED = true;
			// initialize both inner container helper obj representing JAXRS and generic implementation.
			for (int i = 0 ; i <innerHelpr.length; i++ ){
				URI trs_Uri;
				if (i == 0){
					trs_Uri = resource.resolve("/" + sContext + TRS_URI_PATH);
				}
				else{
					trs_Uri = resource.resolve("/" + sContext + TRS_URI_PATH2);
				}
			    innerHelpr[i] =  new TRSObject(resourceUtil, trs_Uri, ConfigUtil.getPropertiesInstance().getProperty("ChangeLogFile"));
			}					
		}
	}

	public static void modifyCutoffEvent(ChangeEvent inCutOffEvent)	{
		for (int i = 0 ; i <innerHelpr.length; i++ )
			innerHelpr[i].modifyCutoffEventInner(inCutOffEvent);		
	}
	 
	public static ChangeEvent getChangeEvent (String uriAbout) {
		// since the same set of event is kept in both the helper's changelog;  access the first helper. 
		return innerHelpr[0].getChangeEventInner(uriAbout);
	}
	
	public final static String TRS_URI_PATH = "/rest/trs/";
	public final static String TRS_URI_PATH2 = "/restx/trs/";
	public final static String TRS_GENERIC_MARKER = "/restx/";
		
	// inner container helper obj representing JAXRS and generic implementation.
	private static TRSObject[] innerHelpr = new TRSObject[2]; 
	public static boolean TRS_URI_INITIALIZED = false;
}
