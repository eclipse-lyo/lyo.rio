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
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JContext;
import org.eclipse.lyo.core.utils.marshallers.OSLC4JMarshaller;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.eclipse.lyo.rio.trs.util.ConfigUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;

public class Persistence {
	private final static TreeMap<Long, ChangeRequest> CHANGE_REQUESTS_MAP = new TreeMap<Long, ChangeRequest>();
	private static boolean CHANGE_REQUESTS_LOADED = false;
	private static long MAX_IDENTIFIER;
	public static int PAGE_SIZE = 3;
	
	private static Set<ChangeRequestListener> listeners = new HashSet<ChangeRequestListener>();
	private static final Logger logger = Logger.getLogger(Persistence.class);
	private static final ResourceBundle bundle = ResourceBundle.getBundle("Messages");
	
	private Persistence() {
		super();
	}
	
	private static boolean fileload(final String fileName)
			throws DatatypeConfigurationException, FileNotFoundException,
			IllegalAccessException, IllegalArgumentException,
			InstantiationException, InvocationTargetException,
			OslcCoreApplicationException, URISyntaxException,
			SecurityException, NoSuchMethodException {
		logger.debug("Entering fileload method in Persistence.java class. Param1: " + fileName);
		
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
			logger.debug("Exiting fileload method in Persistence.java class. Return: true");
			return true;
		}
		else {
			//ChangeRequestResource
		}

		logger.debug("Exiting fileload method in Persistence.java class. Return: false");
		return false;
	}

	private static void save(final String fileName) {
		logger.debug("Entering save method in Persistence.java class. Param1: " + fileName);
		
		final ChangeRequest[] changeRequests = getAllChangeRequests();
		OSLC4JContext context = OSLC4JContext.newInstance();
		OSLC4JMarshaller marshaller = context.createMarshaller();
		try {
			marshaller.marshal(changeRequests, new FileOutputStream(fileName));
		} catch (Exception e) {
			logger.error(MessageFormat.format(bundle.getString("SAVE_FAILED"), fileName), e);
		}
		logger.debug("Exiting fileload method in Persistence.java class.");
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
			logger.debug("Entering getChangeRequests method in Persistence class. Param1: " + page);			
			
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
			
			logger.debug("Exiting getChangeRequests method in Persistence class");
			return ret;
		}
	}

	public static void initialize() {
		logger.debug("Entering initialize method in Persistence class");
		
		try {
			fileload(ConfigUtil.getPropertiesInstance().getProperty("ChangeRequestsFile"));
			
			CHANGE_REQUESTS_LOADED = true;
		} catch (Exception e) {
			logger.error(bundle.getString("INITIALIZATION_FAILURE"));
		}
		
		logger.debug("Exiting initialize method in Persistence class");
	}
	
	public static ChangeRequest[] getAllChangeRequests() {
		synchronized (CHANGE_REQUESTS_MAP) {
			logger.debug("Entering getAllChangeRequests method in Persistence class.");
			
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
			
			logger.debug("Exiting getAllChangeRequests method in Persistence class.");
			return ret;
		}
	}
	
	public static ChangeRequest getChangeRequest(final String identifier) {
		synchronized (CHANGE_REQUESTS_MAP) {
			logger.debug("Entering getChangeRequest method in Persistence class.  Param1: " + identifier);
			
			if (!CHANGE_REQUESTS_LOADED)
				initialize();
			
			logger.debug("Exiting getChangeRequest method in Persistence class.");
			return CHANGE_REQUESTS_MAP.get(Long.valueOf(identifier));
		}
	}

	public static ChangeRequest createChangeRequest(final String description,
			final String title, final String filedAgainst)
			throws URISyntaxException {
		logger.debug("Entering createChangeRequest method in Persistence class. Param1: " + description + " Param2: " + title + " Param3: " + filedAgainst);
		
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

		logger.debug("Exiting createChangeRequest method in Persistence class.");
		return changeRequest;
	}

	public static ChangeRequest persistChangeRequest(final URI basePath,
			final ChangeRequest changeRequest) throws URISyntaxException {
		logger.debug("Entering persistChangeRequest method in Persistence class. Param1: " + basePath.toString());
		
		final long identifier = Persistence.getNextIdentifier();

		final URI about = basePath.resolve("./changeRequests/" + identifier);

		changeRequest.setAbout(about);
		changeRequest.setIdentifier(String.valueOf(identifier));
		// changeRequest.setServiceProvider(serviceProviderURI);

		final Date date = new Date();

		changeRequest.setCreated(date);
		changeRequest.setModified(date);

		Persistence.addChangeRequest(changeRequest);
		
		logger.debug("Exiting persistChangeRequests method in Persistence class.");
		return changeRequest;
	}
	
	private static void addChangeRequest(final ChangeRequest changeRequest) {
		synchronized (CHANGE_REQUESTS_MAP) {
			logger.debug("Entering addChangeRequest method in Persistence class.");
			
			CHANGE_REQUESTS_MAP.put(
					Long.valueOf(changeRequest.getIdentifier()), changeRequest);
			
			// save out to file here				
			save(ConfigUtil.getPropertiesInstance().getProperty("ChangeRequestsFile"));
			
			// Notify any listeners of creation event
			notifyListeners(changeRequest, "create");
			
			logger.debug("Exiting addChangeRequest method in Persistence class.");
		}
	}

	public static ChangeRequest updateChangeRequest(final String identifier,
			final ChangeRequest changeRequest) {
		logger.debug("Entering updateChangeRequest method in Persistence class. Param1: " + identifier);
		
		final Long longIdentifier = Long.valueOf(identifier);

		synchronized (CHANGE_REQUESTS_MAP) {
			final ChangeRequest existingChangeRequest = CHANGE_REQUESTS_MAP
					.get(longIdentifier);

			if (existingChangeRequest != null) {
				CHANGE_REQUESTS_MAP.put(longIdentifier, changeRequest);

				// save out to file here				
				save(ConfigUtil.getPropertiesInstance().getProperty("ChangeRequestsFile"));
				
				// Notify any listeners of the update event
				notifyListeners(changeRequest, "update");
				
				logger.debug("Exiting updateChangeRequest method in Persistence class.");
				return changeRequest;
			}
		}

		logger.debug("Exiting updateChangeRequest method in Persistence class. Return is null");
		return null;
	}

	public static ChangeRequest deleteChangeRequest(final String identifier) {
		synchronized (CHANGE_REQUESTS_MAP) {
			logger.debug("Entering deleteChangeRequest method in Persistence class. Param1: " + identifier);
			
			ChangeRequest result = CHANGE_REQUESTS_MAP.remove(Long.valueOf(identifier));

			// this means we successfully removed something with the given identifier
			if (result != null) {
				// save out to file here				
				save(ConfigUtil.getPropertiesInstance().getProperty("ChangeRequestsFile"));
				
				// Notify any listeners of the deletion event
				notifyListeners(result, "delete");
			}
			
			logger.debug("Exiting deleteChangeRequest method in Persistence class.");
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
		logger.debug("Entering notifyListeners method in Persistence class. Param2: " + type);
		
		Iterator<ChangeRequestListener> iter = listeners.iterator();

		while (iter.hasNext()) {
			iter.next().changeRequestAltered(changeRequest, type);
		}
		
		logger.debug("Exiting notifyListener method in Persistence class.");
	}
}
