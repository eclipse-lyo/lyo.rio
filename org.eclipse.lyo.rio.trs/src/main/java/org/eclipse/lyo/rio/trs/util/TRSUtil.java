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
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import org.eclipse.lyo.core.trs.AbstractChangeLog;
import org.eclipse.lyo.core.trs.Base;
import org.eclipse.lyo.core.trs.ChangeEvent;
import org.eclipse.lyo.core.trs.ChangeLog;
import org.eclipse.lyo.core.trs.Creation;
import org.eclipse.lyo.core.trs.Deletion;
import org.eclipse.lyo.core.trs.Modification;
import org.eclipse.lyo.core.trs.TRSConstants;

public class TRSUtil {
	
	/**
	 * insertEventTypeToChangeLog - Insert an event corresponding to the trsEvent for the resource
	 * located at 
	 * @param trsEvent - One of TRS_TYPE_CREATION, TRS_TYPE_MODIFICATION or TRS_TYPE_DELETION
	 * @param resource - URI of the resource that has undergone the change identified by trsEvent
	 */
	public static void insertEventTypeToChangeLog(String trsEvent, URI resource) {
		ChangeEvent event = null;			
		// increment the event number to maintain event order
		int eventNumber = (TRSUtil.LATEST_EVENT != null) ? TRSUtil.LATEST_EVENT.getOrder() + 1 : 0;
		
		if (trsEvent.equals(TRSConstants.TRS_TYPE_CREATION)) {
			event = new Creation(getCurrentTimeStampURN(), resource, eventNumber);
		}
		else if (trsEvent.equals(TRSConstants.TRS_TYPE_MODIFICATION)) {
			event = new Modification(getCurrentTimeStampURN(), resource, eventNumber);
		}
		else if (trsEvent.equals(TRSConstants.TRS_TYPE_DELETION)) {
			event = new Deletion(getCurrentTimeStampURN(), resource, eventNumber);
		}
		// add the event to each inner container helper obj representing JAXRS and generic implementation.
		for (int i = 0 ; i <innerHelpr.length; i++ )
			innerHelpr[i].insertEventToPagedChangeLog(event, resource);

	}

	// if required, this routine will update the URI
	// and prime the base resources
	public static void initialize(URI baseURI) {		
		updateTRSResourceURI(baseURI);
		// initialize each inner container helper obj representing JAXRS and generic implementation.
		for (int i = 0 ; i <innerHelpr.length; i++ )
			innerHelpr[i].initialize();
		
	}
	private static boolean IsGenericImplDesired(URI inURI){
		String sUri = inURI.getPath();
		if (sUri.contains(TRS_GENERIC_MARKER))
			return true;
		else
			return false;		
	}
	
	public static TreeMap<Long, Base> getTrsBase(URI inURI) {
		if (!TRS_URI_INITIALIZED) {	
			initialize(inURI);
		}
		// from uri find out which Inner container to access...
		if(!IsGenericImplDesired(inURI))
			return innerHelpr[0].getTrsBaseInner(); 
		else
			return innerHelpr[1].getTrsBaseInner(); 
	}	
	
	public static void updateTRSResourceURI(URI resource) {
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
			    innerHelpr[i] =  new TRSUtilHelper();
				URI trs_Uri;
				if (i == 0){
					trs_Uri = resource.resolve("/" + sContext + TRS_URI_PATH);
				}
				else{
					trs_Uri = resource.resolve("/" + sContext + TRS_URI_PATH2);
				}
				innerHelpr[i].set_TRS_URI(trs_Uri);
			}					
		}
	}
	
	public static TreeMap<Long, ChangeLog> getTrsChangelogMap(URI inURI) {
		if (!TRS_URI_INITIALIZED) {	
			initialize(inURI);
		}
		// from uri find out which Inner container to access...
		if(!IsGenericImplDesired(inURI))
			return innerHelpr[0].getTrsChangelogMapInner();
		else
			return innerHelpr[1].getTrsChangelogMapInner();
	}
	
	public static AbstractChangeLog getCurrentChangelog(URI inURI) {
		if (!TRS_URI_INITIALIZED) {	
			initialize(inURI);
		}
		// from uri find out which Inner container to access...
		if(!IsGenericImplDesired(inURI))
			return innerHelpr[0].getCurrentChangeLogInner();
		else
			return innerHelpr[1].getCurrentChangeLogInner();
	}
	
	public static final int PAGE_SIZE = 3;
	public final static String TRS_URI_PATH = "/rest/trs/";
	public final static String TRS_URI_PATH2 = "/restx/trs/";
	public final static String TRS_GENERIC_MARKER = "/restx/";
		
	// inner container helper obj representing JAXRS and generic implementation.
	private static TRSUtilHelper[] innerHelpr = new TRSUtilHelper[2]; 
	public static boolean TRS_URI_INITIALIZED = false;

	private static URI getCurrentTimeStampURN() {
		URI timestampURI = null;
		try {
			timestampURI = new URI("urn:urn-3:cm1.example.com:" + getCurrentTimeStamp());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 
		return timestampURI;
	}

	private static String getCurrentTimeStamp() {
		Date currDate = new Date();
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SS");
		String currDateStr = dateFormatGmt.format(currDate);
		return currDateStr;
	}
	
	public static void modifyCutoffEvent(ChangeEvent inCutOffEvent)	{
		for (int i = 0 ; i <innerHelpr.length; i++ )
			innerHelpr[i].modifyCutoffEventInner(inCutOffEvent);		
	}
	 
	public static ChangeEvent getChangeEvent (String uriAbout) {
		// since the same set of event is kept in both the helper's changelog;  access the first helper. 
		return innerHelpr[0].getChangeEventInner(uriAbout);
	}
	public static ChangeEvent LATEST_EVENT;

}
