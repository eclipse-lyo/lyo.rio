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
<%@ page contentType="text/html" language="java" %>
<%@ page import="java.net.*,java.util.*,org.eclipse.lyo.oslc.am.resource.Resource" %>
<%
Resource resource = (Resource) request.getAttribute("resource");
String uri = resource.getUri();
String title = resource.getTitle();
String identifier = resource.getIdentifier();
String description = resource.getDescription();
Date modified = resource.getModified();
Date created = resource.getCreated();
String creator = resource.getCreator();
String contributor = resource.getContributor();
%>
<html>
<head>
<title>OSLC AM Resource: <%=title %> (<%=identifier %>)</title>
<link rel="SHORTCUT ICON" href="oslc.png">
</head>
<body>
Large Compact Preview<hr/>
URI: <%=uri %><br/>
Title: <%=title %><br/>
Description: <%=title %><br/>
Identifier: <%=identifier %><br/>
Description: <%=description %><br/>
Created: <%=created%><br/>
Creator: <%=creator%><br/>
Last Modified: <%=modified%><br/>
Contributor: <%=contributor%><br/>
</body>
</html>