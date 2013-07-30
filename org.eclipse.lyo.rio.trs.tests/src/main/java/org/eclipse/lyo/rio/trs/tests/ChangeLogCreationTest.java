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
 *    Joseph Leong, Sujeet Mishra - Initial implementation
 *******************************************************************************/

package org.eclipse.lyo.rio.trs.tests;

import static org.eclipse.lyo.core.trs.TRSConstants.FileSep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.DefaultedHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.eclipse.lyo.rio.trs.tests.utils.EasySSLClient;
import org.eclipse.lyo.rio.trs.tests.utils.FetchException;
import org.eclipse.lyo.rio.trs.tests.utils.ITRSVocabulary;
import org.eclipse.lyo.rio.trs.tests.utils.InvalidTRSException;
import org.eclipse.lyo.rio.trs.tests.utils.Messages;
import org.eclipse.lyo.rio.trs.tests.utils.SendException;
import org.eclipse.lyo.rio.trs.tests.utils.SendUtil;
import org.eclipse.lyo.rio.trs.tests.utils.TestCore;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * This test is used to verify that a resource created 
 * through the oslc:creationFactory of an OSLC Provider
 * which implements the TRS specification includes the 
 * resource and the associated creation event trs:Creation
 * @author Sujeet Mishra
 *
 */
public class ChangeLogCreationTest extends TestCore{
	private static Properties prop = null;
	private static HttpClient httpClient = null;
	private static Resource trsResource = null;
	private static HttpContext httpContext = null;
	private static String createdResourceUrl = null;
	
	@BeforeClass
	public static void setupOnce() {
		try {
			prop = getConfigPropertiesInstance();
			String resCreationFactoryUri = prop.getProperty("configResourceCreationFactoryUri");
			String resCreationContent = (prop.getProperty("configResContentFile").equals("")?prop.getProperty("configResContent"):readFileAsString(new File(RESOURCES + FileSep + prop.getProperty("configResContentFile"))));
			String resContentType = prop.getProperty("configContentType");
			String trsEndpoint = prop.getProperty("configTrsEndpoint");
			String acceptType = prop.getProperty("acceptType");
			
			httpClient = new EasySSLClient().getClient();
			httpContext = 
					new DefaultedHttpContext(new BasicHttpContext(), new SyncBasicHttpContext(null));
			
			//Create a resource using the resource creation factory.. oslc:CreationFactory
			createdResourceUrl=SendUtil.createResource(resCreationFactoryUri, httpClient, httpContext, resContentType,resCreationContent);
			
			
			trsResource = getResource(trsEndpoint, httpClient, httpContext, acceptType);	
		} catch (FileNotFoundException e) {
			terminateTest(Messages.getServerString("tests.general.config.properties.missing"), e);
		} catch (IOException e) {
			terminateTest(Messages.getServerString("tests.general.config.properties.unreadable"), e);
		} catch (FetchException e) {
			terminateTest(Messages.getServerString("tests.general.trs.fetch.error"), e);
		} catch (SendException e) {
			terminateTest(Messages.getServerString("tests.general.trs.send.error"), e);
		} catch (Exception e) {
			terminateTest(null, e);
		} 
	}
	
	@Test
	public void testChangeLogHasChangeProperty() {
		try {
			Resource changeLogResource =
					trsResource.getPropertyResourceValue(ITRSVocabulary.CHANGELOG_PROPERTY);
				
			if (changeLogResource != null && !changeLogResource.equals(RDF.nil)) {
				if (!changeLogResource.hasProperty(ITRSVocabulary.CHANGE_PROPERTY)) {
					throw new InvalidTRSException(
						Messages.getServerString("validators.missing.trs.change.property")); //$NON-NLS-1$
				}
				
			}
		} catch (InvalidTRSException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(MessageFormat.format(
					Messages.getServerString("tests.general.error"),
					e.getLocalizedMessage()));
		} 
	}
	
	@Test
	public void testChangeLogEventChangedPropertyHasCreatedResource() {
		boolean matchFound = false;
		try {
			// Get the overall model, we will need it to follow trs:change 
			// references in the change log to the actual change event later.
			Model rdfModel = trsResource.getModel();
			
			// Get the trs resource's change log
			Resource changeLogResource =
					trsResource.getPropertyResourceValue(ITRSVocabulary.CHANGELOG_PROPERTY);
				
			if (changeLogResource != null && !changeLogResource.equals(RDF.nil)
						&& changeLogResource.hasProperty(ITRSVocabulary.CHANGE_PROPERTY)) 
			{	
				// Iterate over all trs:change properties referenced by the change log
				StmtIterator iter = changeLogResource.listProperties(ITRSVocabulary.CHANGE_PROPERTY);
				
				while (iter.hasNext()) {
					Statement trsChangeReference = iter.nextStatement();
					
					// Obtain the actual change event resource using the URI 
					// mentioned in the change log's trs:change property we are
					// currently examining
					Resource changeEvent = rdfModel.getResource(trsChangeReference.getObject().toString());
							
					if (RDF.nil.getURI().equals(changeEvent.getURI()))
						break;
					
					// Now that we have the change event, determine if it matches
					// the change event created during the test's setup method
					if (changeEvent.hasProperty(ITRSVocabulary.CHANGED_PROPERTY))
					{
						if (changeEvent
								.getProperty(ITRSVocabulary.CHANGED_PROPERTY)
								.getObject().toString()
								.equalsIgnoreCase(createdResourceUrl)) {
							matchFound=true;
							
							//Match Found .. now check  if the correct event type is present
							if (!changeEvent.hasProperty(RDF.type,
									ITRSVocabulary.CREATION_RESOURCE)) {
								throw new InvalidTRSException(
										Messages.getServerString("validators.invalid.trs.changed.property")); //$NON-NLS-1$
							}
							break;
						}
					}
				}
				if(!matchFound)
				{
					throw new InvalidTRSException(
							Messages.getServerString("validators.invalid.trs.changed.property")); //$NON-NLS-1$
				}
				
			}
		} catch (InvalidTRSException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(MessageFormat.format(
					Messages.getServerString("tests.general.error"),
					e.getLocalizedMessage()));
		} 
	}
}
