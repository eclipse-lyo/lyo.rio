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
 *    David Terry - TRS 2.0 compliant implementation
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
import org.eclipse.lyo.core.trs.Modification;
import org.eclipse.lyo.core.trs.Page;
import org.eclipse.lyo.core.trs.TRSConstants;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;

/**
 * @author ernest
 * 
 *         This class manages a set of objects that together comprise the
 *         resources of a TRS feed. The Base and ChangeLogs are paged in a
 *         TreeMap with the key being the page number. The Base TreeMap is
 *         initialized with a set of all known resources returned by
 *         IResourceUtil. Change events are added to the current change log page
 *         (which is the last ChangeLog object that was added) until the page is
 *         full.
 * 
 */

public class TRSObject {

	private final TreeMap<Long, Base> trs_base_map = new TreeMap<Long, Base>();
	private final TreeMap<Long, ChangeLog> trs_changelog_map = new TreeMap<Long, ChangeLog>();
	private URI trs_uri = null;
	private int trs_curr_changelog_size = 0;
	private Long trs_curr_changelog_page = -1L;
	private ChangeLog trs_curr_changelog = null;
	private AbstractResource trs_prev_changelog = null;
	private ChangeEvent last_change_event = null;	
	private ChangeEvent last_cut_off_event = null;
	public boolean trs_base_initialized = false;
	private IResourceUtil resourceUtil;
	private int PAGE_SIZE = 3;

	/**
	 * @param resourceUtil - IResourceUtil object capable of return an array of all the resources known to the
	 * 				server to prime the Base resources.
	 * @param trs_uri - uri root for where the trs endpoints begin.  This is used for setting the urls for references
	 *				to other resources in the TRS feed.
	 */
	public TRSObject(IResourceUtil resourceUtil, URI trs_uri) {
		super();
		this.resourceUtil = resourceUtil;
		this.trs_uri = trs_uri;
		initialize();
	}

	/**
	 * This will initialize the paged Base resources with the array of known resources
	 * returned from the given IResourceUtil.  At the very least, a single empty Base resource
	 * will be created.  Page size is a fixed PAGE_SIZE, but boundaries can be defined by the
	 * implementation (size of the data, server load, etc)
	 */
	private void initialize() {
		synchronized (trs_base_map) {
			trs_base_initialized = true;
			List<URI> uris = resourceUtil.getAllResourceURIs();
	
			// initialize TRS Base
			int currentPageSize = 0;
			Long currentPageNumber = 1L;
			Base currentBase = null;
	
			// if there are no known resources, then create an empty base object
			if (uris.size() == 0) {
				currentBase = createNewBase(currentPageNumber);
				getTrsBaseMapInner().put(currentPageNumber, currentBase);
				return;
			}
	
			// inventory of all CR resources
			for (URI uri : uris) {

				// If current size is zero, then create a new base page.
				if (currentPageSize == 0) {
					// Point the current Base to the previous Base page by URI before creating
					// the new one
					if (currentBase != null) {
						// At this point the currentPageNumber tracks the next 
						// page in the sequence since we are preparing to create
						// a new base page.  Do some trickery so we can properly
						// set the next page object.
						URI previousPage = getBaseResourceURI(currentPageNumber - 1);
						URI nextPage = getBaseResourceURI(currentPageNumber);
						currentBase.setNextPage(getNextBasePage(currentBase, previousPage, nextPage));
					}
						
					currentBase = createNewBase(currentPageNumber);
					getTrsBaseMapInner().put(currentPageNumber, currentBase);
				}
	
				// add resource into TRS base
				currentBase.getMembers().add(uri);
				currentPageSize++;
	
				// Have we reached the page boundary?
				if (currentPageSize == PAGE_SIZE) {
					currentPageSize = 0;
					currentPageNumber++;
				}
			}
		}
	}

	/**
	 * @return Return the current changelog.  If there are no changes, then return the empty change log
	 */
	public AbstractChangeLog getCurrentChangeLog() {
		AbstractChangeLog changeLog = getTrsChangelogMapInner().isEmpty() ? new EmptyChangeLog() : trs_curr_changelog;
		return changeLog;
	}

	/**
	 * @param page - page is the page number for the Base object being requested
	 * @return null is returned if page number does not exist in the paged Base resources
	 */
	public Base getBasePage(Long page) {
		// our base map should never be empty, but just in case
		if (trs_base_map.isEmpty() || !trs_base_map.containsKey(page)) {
			return null;
		}
		return trs_base_map.get(page);
	}

	/**
	 * @param page page is the page number for the ChangeLog object being requested
	 * @return null if there are no change logs or if the given page number does not exist in the paged ChangeLog resources
	 */
	public ChangeLog getChangeLogPage(Long page) {
		if (trs_changelog_map.isEmpty() || !trs_changelog_map.containsKey(page)) {
			return null;
		}
		return trs_changelog_map.get(page);
	}

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
	public void insertEventTypeToChangeLog(String trsEvent, URI resource, URI changeURN) {
		ChangeEvent event = null;
		// increment the event number to maintain event order
		int eventNumber = (last_change_event != null) ? last_change_event.getOrder() + 1 : 0;
	
		if (trsEvent.equals(TRSConstants.TRS_TYPE_CREATION)) {
			event = new Creation(changeURN, resource,
					eventNumber);
		} else if (trsEvent.equals(TRSConstants.TRS_TYPE_MODIFICATION)) {
			event = new Modification(changeURN, resource,
					eventNumber);
		} else if (trsEvent.equals(TRSConstants.TRS_TYPE_DELETION)) {
			event = new Deletion(changeURN, resource,
					eventNumber);
		}
	
		insertEventToPagedChangeLog(event, resource);
	
	}

	/**
	 * @param event ChangeEvent to insert into the change log
	 * @param resource URI of the resource that has undergone the change identified the ChangeEvent
 
	 */
	private void insertEventToPagedChangeLog(ChangeEvent event, URI resource) {
		synchronized (trs_changelog_map) {
			// Create a change log entry if the current size is zero
			if (trs_curr_changelog_size == 0) {
				trs_curr_changelog_page++;
				
				trs_prev_changelog = trs_curr_changelog;
				trs_curr_changelog = new ChangeLog();
				getTrsChangelogMapInner().put(trs_curr_changelog_page, trs_curr_changelog);
				// The latest change log has an about URI with no page reference
				trs_curr_changelog.setAbout(getTRSResourceURI("changelog"));
	
				if (trs_curr_changelog_page > 0) {
					// update the previous changelog's about URI to be the current change log number - 1.
					// It used to be the most current change log so it had an about URI without a page number
					URI previousURI = getTRSResourceURI("changelog/" + Long.toString(trs_curr_changelog_page - 1));
					trs_prev_changelog.setAbout(previousURI);
					// point the current change log to the previous change log URI
					trs_curr_changelog.setPrevious(previousURI);
				}
			}
	
			// add resource into current changelog
			trs_curr_changelog.getChange().add(0, event);
			trs_curr_changelog_size++;
			last_change_event = event;
	
			// Have we reached the page boundary? If so, then set the size to zero
			// so next event to come in will trigger the creation of another page
			if (trs_curr_changelog_size == PAGE_SIZE) {
				trs_curr_changelog_size = 0;
			}
		}
	}

	/**
	 * @param cutOffEvent ChangeEvent to set the base resource cutoff event to
	 */
	private void setCutOffEventInner(ChangeEvent cutOffEvent) {
		Collection<Base> baseValues = getTrsBaseMapInner().values();
		for (Base base : baseValues) {
			base.setCutoffEvent(cutOffEvent.getAbout());
		}
	}

	public void modifyCutoffEventInner(ChangeEvent inCutOffEvent) {
		synchronized (trs_changelog_map) {
			List<ChangeEvent> ListofEvents2Compact = new ArrayList<ChangeEvent>();
			boolean bNewCutOffFound = false;
	
			// 1. Iterate change log entries if inCutOffEvent is found
			for (long page = trs_curr_changelog_page; page >= 0; page--) {
				ChangeLog aChangeLog = getTrsChangelogMapInner().get((long) page);
				List<ChangeEvent> aListofevents = aChangeLog.getChange();
				for (ChangeEvent e : aListofevents) {
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
					} else {
						// 1.a. Start adding each entry into ActionList till
						// CurrentCutoffevent or end of list of Changelog is
						// found.
						ListofEvents2Compact.add(e);
					}
				}
			}
			// set last cutoff event for next iteration.
			last_cut_off_event = inCutOffEvent;
	
			// 1.b. take action for each entry in the ActionList in reverse
			// order i.e. oldest change event first.
			for (int ii = ListofEvents2Compact.size() - 1; ii >= 0; ii--) {
				ChangeEvent e2 = ListofEvents2Compact.get(ii);
				modifyBase(e2);
			}
			// 2. Set the cutOffEvent
			setCutOffEventInner(inCutOffEvent);
		}
	}

	/**
	 * @param uriAbout about URI for the change event being requested
	 * @return null if the change event with the given uri can not be found or the change event with the given uri
	 */
	public ChangeEvent getChangeEventInner(String uriAbout) {
		ChangeEvent result = null;
	
		for (int page = 0; page <= trs_curr_changelog_page; page++) {
			ChangeLog aChangeLog = getTrsChangelogMapInner().get((long) page);
			List<ChangeEvent> aListofevents = aChangeLog.getChange();
			for (ChangeEvent e : aListofevents) {
				if (uriAbout.equals(e.getAbout().toString())) {
					result = e;
					break;
				}
			}
		}
	
		return result;
	}

	private Base createNewBase(long currentPageNumber) {
		Base newBase = new Base();
		newBase.setAbout(getBaseResourceURI(null));
		try {
			newBase.setCutoffEvent(new URI(TRSConstants.RDF_NIL));
			newBase.setNextPage(getNextBasePage(newBase, getBaseResourceURI(currentPageNumber), new URI(TRSConstants.RDF_NIL)));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return newBase;
	}

	private TreeMap<Long, Base> getTrsBaseMapInner() {
		return trs_base_map;
	}

	private TreeMap<Long, ChangeLog> getTrsChangelogMapInner() {
		return trs_changelog_map;
	}

	/**
	 * Prepare the LDP object and make it so it points to the current base page
	 * as well as the next base page.
	 * 
	 * @param currentBase
	 *            Reference to the main base endpoint (the page belongs to this
	 *            base resource)
	 * @param currentPage
	 *            URI of the current base page being manipulated
	 * @param nextPage
	 *            URI to the next page in the sequence
	 */
	private Page getNextBasePage(Base currentBase, URI currentPage, URI nextPage) {
		Page ldpNextPage = new Page();

		ldpNextPage.setAbout(currentPage);

		ldpNextPage.setNextPage(nextPage);
		
		ldpNextPage.setPageOf(currentBase);
		
		return ldpNextPage;
	}
	
	private URI getBaseResourceURI(Long pageNumber) {
		if (pageNumber == null)
			return trs_uri.resolve("./" + TRSConstants.TRS_TERM_BASE + "/");
		else
			return trs_uri.resolve("./" + TRSConstants.TRS_TERM_BASE + "/" + pageNumber);
	}

	private URI getTRSResourceURI(String path) {
		if (path == null)
			return trs_uri;
		else
			return trs_uri.resolve(path);
	}

	/**
	 * This routine will update the base resources to include all resources up to the given change event.
	 * @param changeEvent change event process up to for inclusion the the base resource
	 */
	private void modifyBase(ChangeEvent changeEvent) {
		synchronized (trs_base_map) {
			Collection<Base> baseValues = getTrsBaseMapInner().values();
			// if Creation then add resource to base(last page).
			if (changeEvent.getClass().equals(Creation.class)) {

				int index = 0;
				int size = baseValues.size();
				Base currentBase = null;
				for (Base base : baseValues) {
					if (index == (size - 1)) {
						// get the last base page
						currentBase = base;
					}
					index++;
				}

				// add the resource corresponding to the current event to the base
				if (currentBase != null) {
					currentBase.getMembers().add(changeEvent.getChanged());
				}
			}
			// for modification do nothing.

			// for deletion we need to delete the resource from the base page
			// (where it's currently exists).
			if (changeEvent.getClass().equals(Deletion.class)) {
				for (Base base : baseValues) {
					List<URI> resources = base.getMembers();
					for (URI uriRes : resources) {
						// if we get the match delete the resource and exit.
						if (uriRes.equals(changeEvent.getChanged())) {
							base.getMembers().remove(changeEvent.getChanged());
							break;
						}
					}
				}
			}
		}
	}
}
