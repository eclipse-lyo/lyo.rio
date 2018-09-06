<!DOCTYPE html>
<%--
 Copyright (c) 2011, 2014 IBM Corporation.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 
 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.
 
 Contributors:
 
    Sam Padgett		 - initial API and implementation
    Michael Fiedler	 - adapt for OSLC4J
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.net.*,java.util.*" %>
<%@ page import="org.eclipse.lyo.oslc4j.automation.AutomationResource" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AutomationResource autoResource = (AutomationResource) request.getAttribute("autoResource");
%>
<html>
<head>
<title>Automation Resource: <c:out value="${autoResource.title}"/> (<c:out value="${autoResource.identifier}"/>)</title>
</head>
<body>
Large Compact Preview<hr/>
Resource Type: <%= autoResource.getClass().getSimpleName()%><br/>
URI: <a href="<%= autoResource.getAbout().toString()%>"><%= autoResource.getAbout().toString()%></a> <br/>
Title: <c:out value="${autoResource.title}"/><br/>
Description: <c:out value="${autoResource.description}">No Description Available</c:out><br/>
Identifier: <c:out value="${autoResource.identifier}"/><br/>
Created: <%= autoResource.getCreated().toLocaleString()%><br/>
Last Modified: <%= autoResource.getModified().toLocaleString()%><br/>
</body>
</html>