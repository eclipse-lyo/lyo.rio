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
<%@ page import="org.eclipse.lyo.rio.oslc4j.cm.ChangeRequest" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
ChangeRequest changeRequest = (ChangeRequest) request.getAttribute("changeRequest");
%>
<html>
<head>
<title>Change Request: <c:out value="${changeRequest.title}"/> (<c:out value="${changeRequest.identifier}"/>)</title>
</head>
<body>
Large Compact Preview<hr/>
URI: <a href="<%= changeRequest.getAbout() %>"><%= changeRequest.getAbout() %></a> <br/>
Title: <c:out value="${changeRequest.title}"/><br/>
Description: <c:out value="${changeRequest.description}">No Description Available</c:out><br/>
Identifier: <c:out value="${changeRequest.identifier}"/><br/>
Created: <%= changeRequest.getCreated().toLocaleString() %><br/>
Last Modified: <%= changeRequest.getModified().toLocaleString() %><br/>
</body>
</html>
