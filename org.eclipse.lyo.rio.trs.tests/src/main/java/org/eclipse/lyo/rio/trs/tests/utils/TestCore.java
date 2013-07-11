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

package org.eclipse.lyo.rio.trs.tests.utils;

import static org.eclipse.lyo.core.trs.TRSConstants.FileSep;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class TestCore {
	
	protected static final String RESOURCES = 
			"src" + FileSep + "main" + FileSep + "resources";
	private static final String CONFIG_PROPERTIES = 
			RESOURCES + FileSep + "config.properties";
	
	protected static void printResource(Resource resource) {
		for(Statement stmt : resource.listProperties().toList()) {
			System.out.println(stmt.getSubject().getLocalName() + " " + stmt.getPredicate().getLocalName() + " " + stmt.getObject().toString());
		}
	}
	
	protected static Resource getResource(String uri, HttpClient httpClient, HttpContext httpContext, String acceptType) 
	throws  InterruptedException, FetchException
	{
		Model model = FetchUtil.fetchResource(uri, httpClient, httpContext, acceptType);
			
		return model.createResource(uri);
	}

	protected static Properties getConfigPropertiesInstance() 
	throws FileNotFoundException, IOException 
	{
		Properties prop = new Properties();
		
		prop.load(new FileInputStream(CONFIG_PROPERTIES));
		
		return prop;
	}
	
	protected static void terminateTest(String addlMsg, Exception e) {
		String errorMsg = Messages.getServerString("tests.core.error");
		
		if(addlMsg != null) {
			errorMsg += addlMsg;
		}
		
		System.out.println(errorMsg);
		e.printStackTrace();
		
		System.exit(1);
	}
	
	protected StmtIterator getStatementsForProp(Resource res, Property prop) {
		return res.listProperties(prop);
	}
	
	/**
	 * This method is used to read the contents
	 * of a file as a String
	 * @param f
	 * @return
	 */
	protected static String readFileAsString(File f) {

		StringBuilder stringBuilder = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(f);
		} catch (FileNotFoundException e) {
			return null;
		}

		try {
			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine() + "\n");
			}
		} finally {
			scanner.close();
		}
		return stringBuilder.toString();
	}
}
