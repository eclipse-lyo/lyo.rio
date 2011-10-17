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
<%@ page import="java.net.*,java.util.*,javax.servlet.*,org.eclipse.lyo.oslc.rm.services.requirementcollection.RequirementCollection" %>
<%
RequirementCollection reqcol = (RequirementCollection) request.getAttribute("requirement");
String uri = reqcol.getUri();
String title = reqcol.getTitle();
String shortTitle = reqcol.getShortTitle();
String identifier = reqcol.getIdentifier();
String description = reqcol.getDescription();
Date modified = reqcol.getModified();
Date created = reqcol.getCreated();
String creator = reqcol.getCreator();
String contributor = reqcol.getContributor();
%>
<html>
<head>
<title>OSLC RM Requirement Collection: <%=title %> (<%=identifier %>)</title>
<link rel="SHORTCUT ICON" href="oslc.png">
</head>
<body>
Large Compact Preview<hr/>
URI: <%=uri %><br/>
Title: <%=title %><br/>
ShortTitle: <%=shortTitle %><br/>
Identifier: <%=identifier %><br/>
Description: <%=description %><br/>
Created: <%=created%><br/>
Creator: <%=creator%><br/>
Last Modified: <%=modified%><br/>
Contributor: <%=contributor%><br/>
</body>
</html>