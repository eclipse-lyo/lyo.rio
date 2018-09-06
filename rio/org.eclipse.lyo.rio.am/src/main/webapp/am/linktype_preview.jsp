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
<%@ page import="java.net.*,java.util.*,org.eclipse.lyo.oslc.am.linktype.*" %>
<%
	LinkType linkType = (LinkType) request.getAttribute("linkType");
String uri = linkType.getUri();
String about = linkType.getAbout();
String title = linkType.getLabel();
String description = linkType.getComments();
%>
<html>
<head>
<link rel="SHORTCUT ICON" href="../oslc.png">
<title>OSLC AM Link Type: <%=title %> (<%=about%>)</title>
</head>
<body>
Link Type URI: <%=about %><br/>
Edit URI: <%=uri %>
Title: <%=title %><br/>
Description: <%=title %><br/>
</body>
</html>