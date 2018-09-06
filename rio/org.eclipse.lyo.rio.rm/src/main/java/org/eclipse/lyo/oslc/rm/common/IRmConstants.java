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
package org.eclipse.lyo.oslc.rm.common;

@SuppressWarnings("nls")
public interface IRmConstants {
	
	// server constants
	public static final String SERVER_CONTEXT = "rio-rm";  
	public static final String SERVER_SCHEME = "http://";  
	public static final int DEFAULT_MAX_RESULTS = 100;
	
	//
	public static final String RIO_RM_PUBLISHER_TITLE = "Open Services for Lifecycle Collaboration in Requirements Management"; 
	public static final String RIO_RM_PUBLISHER_IDENTIFIER = "open-services.net/rio/rm"; 
	public static final String RIO_RM_ICON = "oslc.png"; 
	
	// service segments
	public static final String SERVICE_REQUIREMENT = "requirement";  
	public static final String SERVICE_REQCOL = "reqcol";  
	public static final String SERVICE_FACTORY_REQUIREMENT= "factory/requirement";  
	public static final String SERVICE_FACTORY_REQCOL = "factory/reqcol";  
	public static final String SERVICE_SELECTOR_REQUIREMENT = "selector/requirement";  
	public static final String SERVICE_SELECTOR_REQCOL = "selector/reqcol";  
	public static final String SERVICE_CREATOR_REQUIREMENT = "creator/requirement";  
	public static final String SERVICE_CREATOR_REQCOL = "creator/reqcol";  
	
	// RIO
	public static final String RIO_PUBLISHER_TITLE = "Open Services for Lifecycle Collaboration in Requirements Management"; 
	public static final String RIO_PUBLISHER_IDENTIFIER = "open-services.net/ri/rm"; 
	public static final String RIO_ICON = "oslc.png"; 
	
	public static final String RIO_RM_URI = "http://open-services.net/ri/rm"; 
	public static final String RIO_RM_NAMESPACE = "http://open-services.net/ri/rm/"; 
	public static final String RIO_RM_PREFIX = "rio"; 
	public static final String RIO_RM_XMLS_DECL = "\txmlns:" + RIO_RM_PREFIX + "=\"" +  RIO_RM_NAMESPACE + "\"\n";  

	public static final String RIO_RM_TERM_SEARCHTERMS = RIO_RM_NAMESPACE + "searchTerms";  
	public static final String RIO_RM_PTERM_SEARCHTERMS = RIO_RM_PREFIX + ":searchTerms";  
	public static final String RIO_RM_SEARCHTERMS = RIO_RM_NAMESPACE + "searchTerms";  
	public static final String RIO_RM_UNKNOWN_USER_ID = "_UNKNOWN_USER_";
	
	public static final String RIO_RM_SELECTION_REQUIREMENT_WIDTH = "300px";
	public static final String RIO_RM_SELECTION_REQUIREMENT_HEIGHT = "300px";
	public static final String RIO_RM_SELECTION_REQCOL_WIDTH = "300px";
	public static final String RIO_RM_SELECTION_REQCOL_HEIGHT = "300px";
	public static final String RIO_RM_CREATION_REQUIREMENT_WIDTH = "300px";
	public static final String RIO_RM_CREATION_REQUIREMENT_HEIGHT = "300px";
	public static final String RIO_RM_CREATION_REQCOL_WIDTH = "300px";
	public static final String RIO_RM_CREATION_REQCOL_HEIGHT = "300px";
	
	// oslc_rm
	public static final String OSLC_RM_NAMESPACE = "http://open-services.net/ns/rm#";
	public static final String OSLC_RM_PREFIX = "oslc_rm";
	public static final String OSLC_RM_XMLS_DECL = "\txmlns:" + OSLC_RM_PREFIX + "=\"" + OSLC_RM_NAMESPACE + "\"\n";

	// oslc_rm types 
	public static final String OSLC_RM_TYPE_TERM_REQUIREMENT = "Requirement";
	public static final String OSLC_RM_TYPE_PTERM_REQUIREMENT = OSLC_RM_PREFIX + ':' + OSLC_RM_TYPE_TERM_REQUIREMENT;
	public static final String OSLC_RM_TYPE_REQUIREMENT = OSLC_RM_NAMESPACE + OSLC_RM_TYPE_TERM_REQUIREMENT;
	public static final String OSLC_RM_TYPE_TERM_REQUIREMENTCOLLECTION = "RequirementCollection";
	public static final String OSLC_RM_TYPE_PTERM_REQUIREMENTCOLLECTION = OSLC_RM_PREFIX + ':' + OSLC_RM_TYPE_TERM_REQUIREMENTCOLLECTION;
	public static final String OSLC_RM_TYPE_REQUIREMENTCOLLECTION = OSLC_RM_NAMESPACE + OSLC_RM_TYPE_TERM_REQUIREMENTCOLLECTION;

	// oslc_rm properties 
	public static final String OSLC_RM_TERM_ELABORATEDBY = "elaboratedBy";
	public static final String OSLC_RM_PTERM_ELABORATEDBY = OSLC_RM_PREFIX + ':' + OSLC_RM_TERM_ELABORATEDBY;
	public static final String OSLC_RM_ELABORATEDBY = OSLC_RM_NAMESPACE + OSLC_RM_TERM_ELABORATEDBY;
	public static final String OSLC_RM_TERM_SPECIFIEDBY = "specifiedBy";
	public static final String OSLC_RM_PTERM_SPECIFIEDBY = OSLC_RM_PREFIX + ':' + OSLC_RM_TERM_SPECIFIEDBY;
	public static final String OSLC_RM_SPECIFIEDBY = OSLC_RM_NAMESPACE + OSLC_RM_TERM_SPECIFIEDBY;
	public static final String OSLC_RM_TERM_AFFECTEDBY = "affectedBy";
	public static final String OSLC_RM_PTERM_AFFECTEDBY = OSLC_RM_PREFIX + ':' + OSLC_RM_TERM_AFFECTEDBY;
	public static final String OSLC_RM_AFFECTEDBY = OSLC_RM_NAMESPACE + OSLC_RM_TERM_AFFECTEDBY;
	public static final String OSLC_RM_TERM_TRACKEDBY = "trackedBy";
	public static final String OSLC_RM_PTERM_TRACKEDBY = OSLC_RM_PREFIX + ':' + OSLC_RM_TERM_TRACKEDBY;
	public static final String OSLC_RM_TRACKEDBY = OSLC_RM_NAMESPACE + OSLC_RM_TERM_TRACKEDBY;
	public static final String OSLC_RM_TERM_IMPLEMENTEDBY = "implementedBy";
	public static final String OSLC_RM_PTERM_IMPLEMENTEDBY = OSLC_RM_PREFIX + ':' + OSLC_RM_TERM_IMPLEMENTEDBY;
	public static final String OSLC_RM_IMPLEMENTEDBY = OSLC_RM_NAMESPACE + OSLC_RM_TERM_IMPLEMENTEDBY;
	public static final String OSLC_RM_TERM_VALIDATEDBY = "validatedBy";
	public static final String OSLC_RM_PTERM_VALIDATEDBY = OSLC_RM_PREFIX + ':' + OSLC_RM_TERM_VALIDATEDBY;
	public static final String OSLC_RM_VALIDATEDBY = OSLC_RM_NAMESPACE + OSLC_RM_TERM_VALIDATEDBY;
	
	// SysML Relationship types
	public static final String UML4SYSML_NAMESPACE = "http://www.omg.org/spec/SysML/20100301/UML4SysML#";
	public static final String UML4SYSML_PREFIX = "uml4sysml";
	public static final String UML4SYSML_TERM_NESTEDCLASSIFIER = "nestedClassifier";  
	public static final String UML4SYSML_PTERM_NESTEDCLASSIFIER = UML4SYSML_PREFIX + ':' + UML4SYSML_TERM_NESTEDCLASSIFIER;  
	public static final String UML4SYSML_NESTEDCLASSIFIER = UML4SYSML_NAMESPACE + UML4SYSML_TERM_NESTEDCLASSIFIER;  

	public static final String UML4SYSML_TERM_TRACE = "trace";  
	public static final String UML4SYSML_PTERM_TRACE = UML4SYSML_PREFIX + ':' + UML4SYSML_TERM_TRACE;  
	public static final String UML4SYSML_TRACE = UML4SYSML_NAMESPACE + UML4SYSML_TERM_TRACE;  

	public static final String SYSMLREQUIREMENTS_NAMESPACE = "http://www.omg.org/spec/SysML/20100301/SysML/Requirements#";
	public static final String SYSMLREQUIREMENTS_PREFIX = "sysmlrequirements";
	public static final String SYSMLREQUIREMENTS_TERM_DERIVEREQT = "deriveReqt";  
	public static final String SYSMLREQUIREMENTS_PTERM_DERIVEREQT = SYSMLREQUIREMENTS_PREFIX + ':' + SYSMLREQUIREMENTS_TERM_DERIVEREQT;  
	public static final String SYSMLREQUIREMENTS_DERIVEREQT = SYSMLREQUIREMENTS_NAMESPACE + SYSMLREQUIREMENTS_TERM_DERIVEREQT;  

	public static final String SYSMLREQUIREMENTS_TERM_SATISFY = "satisfy";  
	public static final String SYSMLREQUIREMENTS_PTERM_SATISFY = SYSMLREQUIREMENTS_PREFIX + ':' + SYSMLREQUIREMENTS_TERM_SATISFY;  
	public static final String SYSMLREQUIREMENTS_SATISFY = SYSMLREQUIREMENTS_NAMESPACE + SYSMLREQUIREMENTS_TERM_SATISFY;  

	public static final String SYSMLREQUIREMENTS_TERM_VERIFY = "verify";  
	public static final String SYSMLREQUIREMENTS_PTERM_VERIFY = SYSMLREQUIREMENTS_PREFIX + ':' + SYSMLREQUIREMENTS_TERM_VERIFY;  
	public static final String SYSMLREQUIREMENTS_VERIFY = SYSMLREQUIREMENTS_NAMESPACE + SYSMLREQUIREMENTS_TERM_VERIFY;  

	public static final String SYSMLREQUIREMENTS_TERM_REFINE = "refine";  
	public static final String SYSMLREQUIREMENTS_PTERM_REFINE = SYSMLREQUIREMENTS_PREFIX + ':' + SYSMLREQUIREMENTS_TERM_REFINE;  
	public static final String SYSMLREQUIREMENTS_REFINE = SYSMLREQUIREMENTS_NAMESPACE + SYSMLREQUIREMENTS_TERM_REFINE;  

	// OSLC RequirementCollection Relationship types
	public static final String OSLC_RM_TERM_USES = "uses";  
	public static final String OSLC_RM_PTERM_USES = OSLC_RM_PREFIX + ':' + OSLC_RM_TERM_USES;  
	public static final String OSLC_RM_USES = OSLC_RM_NAMESPACE + OSLC_RM_TERM_USES;  

}
