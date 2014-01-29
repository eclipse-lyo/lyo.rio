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
<%@ page import="org.eclipse.lyo.oslc4j.qualitymanagement.QmResource" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
QmResource qmRequest = (QmResource) request.getAttribute("qmRequest");
%>
<html>
<head>
<title>: <c:out value="${qmRequest.getTitle()}"/> (<c:out value="${qmRequest.getIdentifier()}"/>)</title>
</head>
<body>
Large Compact Preview<hr/>
Resource Type: <c:out value="${qmRequest.getClass().getSimpleName()}"/><br/>
URI: <a href="<c:out value="${qmRequest.getAbout().toString()}"/>"><c:out value="${qmRequest.getAbout().toString()}"/></a> <br/>
Title: <c:out value="${qmRequest.getTitle()}"/><br/>
Description: <c:out value="${qmRequest.getDescription()}">No Description Available</c:out><br/>
Identifier: <c:out value="${qmRequest.getIdentifier()}"/><br/>
Created: <c:out value="${qmRequest.getCreated().toLocaleString()}"/><br/>
Last Modified: <c:out value="${qmRequest.getModified().toLocaleString()}"/><br/>
</body>
</html>