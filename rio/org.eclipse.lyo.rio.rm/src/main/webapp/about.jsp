<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.core.IConstants" %>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
    Copyright (c) 2011 IBM Corporation.
   
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License v1.0
    and Eclipse Distribution License v. 1.0 which accompanies this distribution. 
   
    The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
    and the Eclipse Distribution License is available at 
    http://www.eclipse.org/org/documents/edl-v10.php.
   
    Contributors:
   
       Jim Conallen - initial API and implementation
 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="SHORTCUT ICON" href="oslc.png">
<title>About RIO OSLC-RM</title>
</head>
<body>
<table><tr><td><a href="<%=uriBase%>"><img border="0" src="oslcLg.png"/></a></td><td><h2>About:<br/>Reference Implementation OSLC (RIO)<br/> for Requirements Management</h2></td></tr></table>
<p>This service provider is a minimal reference implementation of the Open Services for 
Lifecycle Connectivity (OSLC) Requirements Management specification.  The official specification
is available on the <a href="http://open-services.net/bin/view/Main/RmSpecificationV2">OSLC RM Wiki</a></p>

<p>This application is distributed under a <a href="http://www.eclipse.org/org/documents/edl-v10.php">Eclipse Distribution License Version 1.0</a> and <a href="http://www.eclipse.org/legal/epl-v10.html">Eclipse Public License 1.0</a>.</p> 

<h3>Powered by:</h3>
<blockquote>
<p><b><a href="http://www.openrdf.org/">OpenRDF (Sesame)</a></b> an open source RDF framework with support 
for RDF Schema inferencing and querying, is the primary foundation for this server.  All resource data 
is managed as RDF in this server. Distributed under <a href="license/openrdf.jsp">BSD License</a></p>  
<p><b><a href="http://www.antlr.org/">ANTLR</a></b> a parser generator (with runtime) is used to parse Simple 
OSLC Query syntax. Distributed under <a href="license/antlr.jsp">BSD License</a></p>
<p><b><a href="http://www.slf4j.org/">Simple Logging Facade for Java (SLF4J)</a></b> is the logging framework 
required by Sesame. Distributed under <a href="license/slf4j.jsp">MIT License</a>.
</blockquote>
</body>
</html>