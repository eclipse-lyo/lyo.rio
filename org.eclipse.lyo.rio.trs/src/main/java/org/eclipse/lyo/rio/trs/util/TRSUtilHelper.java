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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.lyo.core.trs.AbstractChangeLog;
import org.eclipse.lyo.core.trs.Base;
import org.eclipse.lyo.core.trs.ChangeEvent;
import org.eclipse.lyo.core.trs.ChangeLog;
import org.eclipse.lyo.core.trs.Creation;
import org.eclipse.lyo.core.trs.Deletion;
import org.eclipse.lyo.core.trs.EmptyChangeLog;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.rio.trs.cm.ChangeRequest;
import org.eclipse.lyo.rio.trs.cm.Persistence;

public class TRSUtilHelper {

	private final  TreeMap<Long, Base> trs_base_map = new TreeMap<Long, Base>();
	private final  TreeMap<Long, ChangeLog> trs_changelog_map = new TreeMap<Long, ChangeLog>();
	private URI trs_uri = null;
	private int  trs_curr_changelog_size = 0;
	private Long trs_curr_changelog_page = -1L;
	private ChangeLog trs_curr_changelog = null;
	private AbstractResource trs_prev_changelog = null;
	private ChangeEvent last_cut_off_event = null;
	public boolean trs_base_initialized = false;
	
	public AbstractChangeLog getCurrentChangeLogInner(){
		 AbstractChangeLog changeLog = getTrsChangelogMapInner().isEmpty() ? new EmptyChangeLog() : trs_curr_changelog; 
		 return changeLog;			
	}
	
	public URI get_TRS_URI()
	{
		return trs_uri;
	}
	
	public void set_TRS_URI(URI inUri)
	{
		 trs_uri = inUri;
	}
	
	private Base createNewBase() {
		Base newBase = new Base();
		newBase.setAbout(getBaseResourceURI(null));
		try {
			newBase.setCutoffEvent(new URI(TRSConstants.RDF_NIL));
			newBase.setNextPage(new URI(TRSConstants.RDF_NIL));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return newBase;
	}
	
	public void initialize() {
		 synchronized (trs_base_map) {
			trs_base_initialized = true;
			
			// initialize TRS Base
			ChangeRequest[] changeRequests = Persistence.getAllChangeRequests();
			int currentPageSize = 0;
			Long currentPageNumber = 1L;
			Base currentBase = null;
		
			if (changeRequests.length == 0) {
				currentBase = createNewBase();
				getTrsBaseMapInner().put(currentPageNumber, currentBase);
				return;
			}
			
			// inventory of all CR resources
			for (ChangeRequest changeRequest : changeRequests) {
				// Create a new base entry
				if (currentPageSize == 0) {
					if (currentBase != null)
						currentBase.setNextPage(getBaseResourceURI(currentPageNumber));
					currentBase = createNewBase();
					getTrsBaseMapInner().put(currentPageNumber, currentBase);
				}
				
				// add resource into TRS base
				currentBase.getMembers().add(changeRequest.getAbout());
				currentPageSize++;
				
				// Have we reached the page boundary?
				if (currentPageSize == TRSUtil.PAGE_SIZE) {
					currentPageSize = 0;
					currentPageNumber++;
				}
			}
		}
	}
	public TreeMap<Long, Base> getTrsBaseInner() {
		if (!trs_base_initialized) {
			initialize();
		}
		return getTrsBaseMapInner();
	}
	
	private TreeMap<Long, Base> getTrsBaseMapInner() {
		return trs_base_map;
	}
	
	public TreeMap<Long, ChangeLog> getTrsChangelogMapInner() {
		return trs_changelog_map;
	}
	
	private URI getBaseResourceURI(Long pageNumber) {
		if (pageNumber == null)
			return trs_uri.resolve("./" + TRSConstants.TRS_TERM_BASE+"/");
		else
			return trs_uri.resolve("./" + TRSConstants.TRS_TERM_BASE+"/"+pageNumber);
	}
	
	public void insertEventToPagedChangeLog(ChangeEvent event, URI resource) {
		synchronized (trs_changelog_map) {
			TRSUtil.updateTRSResourceURI(resource);
			
			// Create a new base entry
			if (trs_curr_changelog_size == 0) {
				trs_curr_changelog_page++;
				
				trs_prev_changelog = trs_curr_changelog;
				trs_curr_changelog = new ChangeLog();
				getTrsChangelogMapInner().put(trs_curr_changelog_page, trs_curr_changelog);
				trs_curr_changelog.setAbout(getTRSResourceURI("changelog"));
	
		        if (trs_curr_changelog_page > 0) {
		        	URI previousURI = getTRSResourceURI("changelog/"+Long.toString(trs_curr_changelog_page-1));
		        	trs_curr_changelog.setPrevious(previousURI);
		        	trs_prev_changelog.setAbout(previousURI);
		        }
			}
	
			// add resource into current changelog
			trs_curr_changelog.getChanges().add(0, event);
			trs_curr_changelog_size++;
			TRSUtil.LATEST_EVENT = event;
			
			// Have we reached the page boundary?
			if (trs_curr_changelog_size == Persistence.PAGE_SIZE) {
				trs_curr_changelog_size = 0;
			}
		}
	}

	private URI getTRSResourceURI(String path) {
		if (path == null)
			return trs_uri;
		else
			return trs_uri.resolve(path);
	}
	
	public void setCutOffEventInner(ChangeEvent cutOffEvent) {
		Collection<Base> baseValues = getTrsBaseMapInner().values();
		for (Base base : baseValues) {
			base.setCutoffEvent(cutOffEvent.getAbout());
		}
	}
	
	public void modifyCutoffEventInner(ChangeEvent inCutOffEvent)	{
		synchronized (trs_changelog_map) {
			List<ChangeEvent> ListofEvents2Compact = new ArrayList<ChangeEvent>();
			boolean bNewCutOffFound = false;
				
			// 1. Iterate change log entries if inCutOffEvent is found 
			for (long  page = trs_curr_changelog_page; page >= 0 ; page-- ) {
				 ChangeLog aChangeLog = getTrsChangelogMapInner().get((long) page);
				 List<ChangeEvent> aListofevents = aChangeLog.getChanges();
				 for(ChangeEvent e : aListofevents) {
					 if (last_cut_off_event != null) {
						 if (e.equals(last_cut_off_event)) {
							 break;
						 }						 
					 }
						 
					 if (bNewCutOffFound == false) {
						 if (e.equals(inCutOffEvent)) {
							 bNewCutOffFound = true;
							 ListofEvents2Compact.add(e);
						 }
					 }
					 else {
						 // 1.a. Start adding each entry into ActionList till 
						 // CurrentCutoffevent or end of list of Changelog is found.
						 ListofEvents2Compact.add(e);
					 }
				 }	 			 
			}
			// set last cutoff event for next iteration.
			last_cut_off_event = inCutOffEvent;
			
			//  1.b. take action for each entry in the ActionList in reverse order i.e. oldest change event first.
			for (int ii = ListofEvents2Compact.size()-1; ii >= 0; ii--) {
				ChangeEvent e2 = ListofEvents2Compact.get(ii);
				modifyBase(e2);			
			}
			// 2. Set the cutOffEvent
			setCutOffEventInner(inCutOffEvent);
		}
	}
	
	 private void modifyBase(ChangeEvent e) {
		 synchronized (trs_base_map) {
			 Collection<Base> baseValues = getTrsBaseMapInner().values();
			 // if Creation then add resource to base(last page).
			 if (e.getClass().equals(Creation.class)) {
							  
				 int index = 0; 
				 int size = baseValues.size();
				 Base currentBase = null;
				 for (Base base : baseValues) {
					 if (index == (baseValues.size()-1)) {
						 // get the last base page
						 currentBase = base;
					 }
					 index++;
					}
				 
				 // add the resource corresponding to the current event to the base
			 	 if (currentBase != null) {
			 		 currentBase.getMembers().add(e.getChanged());
				 }			 
			 }
			 // for modification do nothing. 
			 
			 // for deletion we need to delete the resource from the base page (where it's currently exists).
			 if (e.getClass().equals(Deletion.class)) {
				 for (Base base : baseValues) {
					 List<URI> resources = base.getMembers();
					 for (URI uriRes : resources) {
						 // if we get the match delete the resource and exit.
						 if (uriRes.equals(e.getChanged())) {
							 base.getMembers().remove(e.getChanged());
							 break;
						 }
					 }
				 }			 
			 }	 
		 }
	 }	
	 
	 public ChangeEvent getChangeEventInner (String uriAbout) {
		 ChangeEvent result = null;
		 
		 for (int  page = 0; page <= trs_curr_changelog_page; page++) {
				 ChangeLog aChangeLog = getTrsChangelogMapInner().get((long) page);
				 List<ChangeEvent> aListofevents = aChangeLog.getChanges();
				 for(ChangeEvent e: aListofevents) {
					 if (uriAbout.equals(e.getAbout().toString())) {
						 result = e;
						 break;
					 }
				 }
			}
		 
		 return result;	
	 }
}
