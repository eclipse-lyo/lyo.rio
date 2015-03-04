<!DOCTYPE html>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
  <meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=no'>
    <title><c:out value="${title}" default="Untitled"/></title>
    <link rel="stylesheet" type="text/css" href="${baseURI}/style/common.css">
    <link rel="stylesheet" type="text/css" href="${baseURI}/style/bug.css">
    <link rel="shortcut icon" href="${baseURI}oslc-16x16.png">
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script type="text/javascript">
        function show(mediaType) {
            var request = $.ajax('', {
                dataType: 'text',
                headers: {
                    Accept: mediaType
                }
            });

            request.done(function(content) {
                $('#rdf').text(content).show();
            });

            request.fail(function() {
                alert('Could not get resource.');
            });
        }

        $(document).ready(function() {
            $('#turtle').on('click', function(e) {
                e.preventDefault();
                show('text/turtle');
            });
            $('#json').on('click', function(e) {
                e.preventDefault();
                show('application/ld+json');
            });
        });
    </script>
</head>
<body>
    <img class="logo" src="${baseURI}/oslc-192x192.png">
    <h1><c:out value="${title}" default="Untitled"/></h1>
    <div class="small"><a href="#" id="turtle">Turtle</a> | <a href="#" id="json">JSON-LD</a></div>
    <div>
        <label for="severity">Severity:</label>
        <span id="severity"><c:out value="${severity}" default="Unassigned"/></span>
    </div>
    <div>
        <label for="created">Created:</label>
        <span id="created"><fmt:formatDate value="${created}"
            type="both" dateStyle="full" timeStyle="short"/></span>
    </div>
    <div>
        <label for="description">Description:</label>
    </div>
    <div>
        <div class="description" id="description"><c:out value="${description}" default="No description."/></div>
    </div>
    <div style="display: none;" id="rdf" class="fixed"></div>
    <div class="back">
        <a href="${baseURI}">&lt; Back to Bugs</a>
    </div>
</body>
</html>
