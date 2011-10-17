<%@ page import="java.util.*,org.eclipse.lyo.rio.util.*,org.eclipse.lyo.rio.store.*" %>
<%@ page import="org.eclipse.lyo.rio.core.IConstants" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
OslcResource resource = (OslcResource) request.getAttribute("resource"); 
String title = resource.getTitle();
String uri = resource.getUri();
RioValue sourceVal = resource.getFirstPropertyValue(IConstants.DCTERMS_SOURCE);
String imgHref = sourceVal.stringValue();
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
<title>Slide Resource View (<%=uri%>)</title>
<link rel="SHORTCUT ICON" href="../oslc.png">
</head>
<body>
<p><a href="<%=uriBase %>/index.jsp">Home</a></p>
<h2>Slide Resource: <%=title %></h2>
<img src="<%=imgHref %>"/>
</body>
</html>