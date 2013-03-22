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
 *    David Terry - Add Listener Mechanisms
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.cm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;

import javax.ws.rs.WebApplicationException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JContext;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JMarshaller;

import com.hp.hpl.jena.graph.impl.FileGraph.NotifyOnClose;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;

public class Persistence {
	private final static TreeMap<Long, ChangeRequest> CHANGE_REQUESTS_MAP = new TreeMap<Long, ChangeRequest>();
	private static boolean CHANGE_REQUESTS_LOADED = false;
	private static long MAX_IDENTIFIER;
	public static int PAGE_SIZE = 3;
	
	private static Set<ChangeRequestListener> listeners = new HashSet<ChangeRequestListener>();
	
	private Persistence() {
		super();
	}
	
	private static boolean fileload(final String fileName)
			throws DatatypeConfigurationException, FileNotFoundException,
			IllegalAccessException, IllegalArgumentException,
			InstantiationException, InvocationTargetException,
			OslcCoreApplicationException, URISyntaxException,
			SecurityException, NoSuchMethodException {

		final File file = new File(fileName);

		if ((file.exists()) && (file.isFile()) && (file.canRead())) {
			  final Model model = ModelFactory.createDefaultModel();

			  model.read(new FileInputStream(file), null, FileUtils.langXMLAbbrev);

			final Object[] resources = JenaModelHelper.fromJenaModel(model,
					ChangeRequest.class);
						
			synchronized (CHANGE_REQUESTS_MAP) {
				CHANGE_REQUESTS_MAP.clear();

				if (resources != null) {
					for (final Object resource : resources) {
						if (resource instanceof ChangeRequest) {
							final ChangeRequest changeRequest = (ChangeRequest) resource;

							final String identifier = changeRequest
									.getIdentifier();

							final long longIdentifier = Long
									.parseLong(identifier);

							MAX_IDENTIFIER = Math.max(longIdentifier,
									MAX_IDENTIFIER);

							CHANGE_REQUESTS_MAP
									.put(Long.valueOf(longIdentifier),
											changeRequest);
						}
					}
				}
			}

			return true;
		}
		else {
			//ChangeRequestResource
		}

		return false;
	}

	private static void save(final String fileName) {
		
		final ChangeRequest[] changeRequests = getAllChangeRequests();
		OSLC4JContext context = OSLC4JContext.newInstance();
		OSLC4JMarshaller marshaller = context.createMarshaller();
		try {
			marshaller.marshal(changeRequests, new FileOutputStream(fileName));
		} catch (WebApplicationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static long getNextIdentifier() {
		synchronized (CHANGE_REQUESTS_MAP) {
			if (!CHANGE_REQUESTS_LOADED)
				initialize();
			return ++MAX_IDENTIFIER;
		}
	}

	public static int getTotalChangeRequests(){
		synchronized (CHANGE_REQUESTS_MAP) {
			if (!CHANGE_REQUESTS_LOADED)
				initialize();
			return CHANGE_REQUESTS_MAP.size();
		}
	}
	
	public static ChangeRequest[] getChangeRequests(final int page) {
		synchronized (CHANGE_REQUESTS_MAP) {
			int startIndex = page * PAGE_SIZE;
			int total = getTotalChangeRequests();
			int numResults = PAGE_SIZE;
			ChangeRequest[] ret = null;
			
			if(total == 0) ret = new ChangeRequest[0];
			else if(startIndex >= total) throw new ArrayIndexOutOfBoundsException();
			else{
			if(total-startIndex < PAGE_SIZE) numResults = total-startIndex;
				ChangeRequest[] requests = CHANGE_REQUESTS_MAP.values().toArray(
							new ChangeRequest[CHANGE_REQUESTS_MAP.size()]);
				ret = new ChangeRequest[numResults];
				System.arraycopy(requests, startIndex, ret, 0, numResults);
			}
			return ret;
		}
	}

	public static void initialize() {
		try {
			CHANGE_REQUESTS_LOADED = true;
			
			fileload(Constants.PATH_FLAT_FILE);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (OslcCoreApplicationException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static ChangeRequest[] getAllChangeRequests() {
		synchronized (CHANGE_REQUESTS_MAP) {
			ChangeRequest[] ret = null;
			
			if(CHANGE_REQUESTS_MAP.size() == 0 && !CHANGE_REQUESTS_LOADED){
				initialize();
			}
			// if still empty then return empty array
			if(CHANGE_REQUESTS_MAP.size() == 0){
				ret = new ChangeRequest[0];
			}
			else{
				ChangeRequest[] requests = CHANGE_REQUESTS_MAP.values().toArray(
							new ChangeRequest[CHANGE_REQUESTS_MAP.size()]);
				ret = new ChangeRequest[CHANGE_REQUESTS_MAP.size()];
				System.arraycopy(requests, 0, ret, 0, CHANGE_REQUESTS_MAP.size());
			}
			return ret;
		}
	}
	
	public static ChangeRequest getChangeRequest(final String identifier) {
		synchronized (CHANGE_REQUESTS_MAP) {
			if (!CHANGE_REQUESTS_LOADED)
				initialize();
			
			return CHANGE_REQUESTS_MAP.get(Long.valueOf(identifier));
		}
	}

	public static ChangeRequest createChangeRequest(final String description,
			final String title, final String filedAgainst)
			throws URISyntaxException {
		final ChangeRequest changeRequest = new ChangeRequest();

		changeRequest.setApproved(Boolean.FALSE);
		changeRequest.setClosed(Boolean.FALSE);
		changeRequest.addDctermsType(Type.Defect.toString());
		changeRequest.setDescription(description);
		changeRequest.setFiledAgainst(filedAgainst);
		changeRequest.setFixed(Boolean.FALSE);
		changeRequest.setInProgress(Boolean.FALSE);
		changeRequest.setReviewed(Boolean.FALSE);
		changeRequest.setShortTitle(title);
		changeRequest.setStatus("Submitted");
		changeRequest.setTitle(title);
		changeRequest.setVerified(Boolean.FALSE);

		return changeRequest;
	}

	public static ChangeRequest persistChangeRequest(final URI basePath,
			final ChangeRequest changeRequest) throws URISyntaxException {
		final long identifier = Persistence.getNextIdentifier();

		final URI about = basePath.resolve("./changeRequests/" + identifier);

		changeRequest.setAbout(about);
		changeRequest.setIdentifier(String.valueOf(identifier));
		// changeRequest.setServiceProvider(serviceProviderURI);

		final Date date = new Date();

		changeRequest.setCreated(date);
		changeRequest.setModified(date);

		Persistence.addChangeRequest(changeRequest);
		
		return changeRequest;
	}
	
	private static void addChangeRequest(final ChangeRequest changeRequest) {
		synchronized (CHANGE_REQUESTS_MAP) {
			CHANGE_REQUESTS_MAP.put(
					Long.valueOf(changeRequest.getIdentifier()), changeRequest);
			
			// save out to file here				
			save(Constants.PATH_FLAT_FILE);
			
			// Notify any listeners of creation event
			notifyListeners(changeRequest, "create");
		}
	}

	public static ChangeRequest updateChangeRequest(final String identifier,
			final ChangeRequest changeRequest) {
		final Long longIdentifier = Long.valueOf(identifier);

		synchronized (CHANGE_REQUESTS_MAP) {
			final ChangeRequest existingChangeRequest = CHANGE_REQUESTS_MAP
					.get(longIdentifier);

			if (existingChangeRequest != null) {
				CHANGE_REQUESTS_MAP.put(longIdentifier, changeRequest);

				// save out to file here				
				save(Constants.PATH_FLAT_FILE);
				
				// Notify any listeners of the update event
				notifyListeners(changeRequest, "update");
				
				return changeRequest;
			}
		}

		return null;
	}

	public static ChangeRequest deleteChangeRequest(final String identifier) {
		synchronized (CHANGE_REQUESTS_MAP) {
			ChangeRequest result = CHANGE_REQUESTS_MAP.remove(Long.valueOf(identifier));

			// this means we successfully removed something with the given identifier
			if (result != null) {
				// save out to file here				
				save(Constants.PATH_FLAT_FILE);
				
				// Notify any listeners of the deletion event
				notifyListeners(result, "delete");
			}
			
			return result; 
		}
	}
	
	public static void addListner(ChangeRequestListener listener) {
		listeners.add(listener);
	}

	public static void removeListener(ChangeRequestListener listener) {
		listeners.remove(listener);
	}

	private static void notifyListeners(ChangeRequest changeRequest, String type) {
		Iterator<ChangeRequestListener> iter = listeners.iterator();

		while (iter.hasNext()) {
			iter.next().changeRequestAltered(changeRequest, type);
		}
	}
}
