<!DOCTYPE html>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--
 Copyright (c) 2015 IBM Corporation.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.

 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.

 Contributors:

    Sam Padgett		 - initial API and implementation
-->
<html lang="en">
<head>
    <meta charset="utf-8" />
    <title><c:out value="${title}" default="Bug Preview"/></title>
    <link rel="stylesheet" type="text/css" href="${baseURI}/preview.css">
    <link rel="shortcut icon" href="${baseURI}oslc-16x16.png">
</head>
<body>
    <img class="logo" src="${baseURI}/oslc-192x192.png">
    <div>
        <label for="severity">Severity:</label>
        <span id="severity"><c:out value="${severity}" default="Unassigned"/></span>
    </div>
    <div>
        <label for="description">Description:</label>
    </div>
    <div>
        <div class="description" id="description"><c:out value="${description}" default="No description."/></div>
    </div>
</body>
</html>
