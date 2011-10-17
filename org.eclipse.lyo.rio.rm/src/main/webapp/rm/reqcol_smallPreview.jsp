<!--
Copyright (c) 2011 IBM Corporation.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Distribution License v. 1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/org/documents/edl-v10.php.
 
Contributors:
    Jim Conallen
 -->
<%@ page contentType="text/html" language="java" %>
<%@ page import="java.net.*,java.util.*,java.text.SimpleDateFormat,org.eclipse.lyo.rio.store.OslcResource" %>
<%
OslcResource requirement = (OslcResource) request.getAttribute("reqcol"); 
String uri = requirement.getUri();
String title = requirement.getTitle();
Date createdDate = requirement.getCreated();
SimpleDateFormat formatter = new SimpleDateFormat();
String created = formatter.format(createdDate);

String identifier = requirement.getIdentifier();
%>
<html>
<head>
<title>OSLC RM Requirement: <%=title %> (<%=identifier %>)</title>
<link rel="SHORTCUT ICON" href="oslc.png">
</head>
<body>
<i>Compact Preview (Small)</i><br/>
URI: <%=uri %><br/>
Title: <%=title %><br/>
Identifier: <%=identifier %><br/>
Created: <%=created %>
</body>
</html>