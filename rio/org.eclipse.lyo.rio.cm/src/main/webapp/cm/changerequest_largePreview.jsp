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
<%@ page import="java.net.*,java.util.*" %>
<%@ page import="java.net.*,java.util.*,java.text.SimpleDateFormat" %>
<%@ page import="org.eclipse.lyo.rio.cm.changerequest.ChangeRequest" %>
<%
ChangeRequest cr = (ChangeRequest) request.getAttribute("changerequest");
String uri = cr.getUri();
String title = cr.getTitle();
Date createdDate = cr.getCreated();
SimpleDateFormat formatter = new SimpleDateFormat();
String created = formatter.format(createdDate);
Date modifiedDate = cr.getModified();
String modified = formatter.format(modifiedDate);
String identifier = cr.getIdentifier();
String description = cr.getDescription();
String creator = cr.getCreator();
String contributor = cr.getContributor();
%>
<html>
<head>
<title>OSLC AM Resource: <%=title %> (<%=identifier %>)</title>
<link rel="SHORTCUT ICON" href="/rio-cm/oslc.png">
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