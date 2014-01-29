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
 
    Sam Padgett 	 - initial API and implementation
    Michael Fiedler	 - adapted for OSLC4J
    Malcolm McKinney - design and UI integration
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String creatorUri = (String) request.getAttribute("creatorUri");
%>
<html>
<head>

<script type="text/javascript" src="../../web/qmDialogs.js"></script>
<script type="text/javascript"
	src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript"
	src="../../web/jquery-ui-1.8.18.custom.min.js"></script>

<script>
	$(function() {
	    $("#submit").click(function() {
	        create('<%=creatorUri%>');
		});
		$("#cancel").click(function() {
			cancel();
		});
	});
</script>

<link rel="stylesheet" type="text/css"
	href="../../web/jquery-ui-1.8.18.custom.css"></link>
<style>
#logo {
	padding-top: 20px;
}

body {
	width: 700px;
}
</style>

</head>

<body>
	<div>
		<img src="http://open-services.net/css/images/logo-forflip.png"
			alt="Logo" id="logo" width="71" height="80" align="left"
			style="padding-top: 20px" />
	</div>
	<div>
		<h2 class="ui-widget-header">Test Case Creation Dialog</h2>
		
		<form class="ui-widget input" id="Create" method="POST">
			<table class="ui-widget-content" id="inputTable">
				<tr>
					<th colspan="2"><p id="createStart">Input Information:</p></th>
				</tr>
				<tr>
					<td><label for="title">Title:</label>
					<td><input type="text" name="user" id="title"></td>
				</tr>
				<tr>
					<td><label for="description">Description:</label>
					<td><textarea rows = "2" cols = "30" name="description" id="description"></textarea></td>
				</tr>
				<th>Test Plan:</th>
				<td>
				
					<select class="ui-widget select" id="plan">
						<c:forEach items="${testPlans}" var="getPlan"> 
							<option value="${getPlan.identifier}" id="testPlan"><c:out value="${getPlan.identifier}"/>: <c:out value="${getPlan.title}"/></option>
						</c:forEach>
					</select>
					
				</td>
				<tr>
					<td></td>
					<td><input class="ui-button" type="button" id=cancel
						value="Cancel"> <input class="ui-button" type="button"
						id="submit" value="Submit"></td>
				</tr>
			</table>

		</form>
	</div>
</body>
</html>

