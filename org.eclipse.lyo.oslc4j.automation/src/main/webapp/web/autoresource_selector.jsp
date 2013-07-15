<!DOCTYPE html>
<%--
 Copyright (c) 2011, 2012 IBM Corporation.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 
 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.
 
 Contributors:
 
    Sam Padgett		 - initial API and implementation
    Michael Fiedler	 - adapted for OSLC4J
    Malcolm McKinney - design and UI integration
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>

<%
	String selectionUri = (String) request.getAttribute("selectionUri");
    String resourceType = (String) request.getAttribute("resourceType");  		
%>
<html>
<head>

	<script type="text/javascript" src="../../web/autoDialogs.js"></script>
	<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="../../web/jquery-ui-1.8.18.custom.min.js"></script>
	<script>
	$(function() {
	    $("#search").click(function() {
	    	search('<%= selectionUri %>');
	     });
	    $("#cancel").click(function() {
	        cancel();
	     });
	    $("#select").click(function() {
	    	select();
	    });
	  });
	</script>
	<link rel="stylesheet" type="text/css" href="../../web/jquery-ui-1.8.18.custom.css"></link>
	<style>
		#logo {
			padding-top: 20px;
		}
		.selector  {
			width: 600px;
		}
		.searchbox {
			padding-bottom: 20px;
		}
	</style>

</head>

<body class="selector">

    	<img id="logo" src="http://open-services.net/css/images/logo-forflip.png" alt="Logo" width="71" height="80" align= "left"/>
    	<h2 class="ui-widget-header">OSLC Selection Dialog</h2>
    	<h3> <font face="verdana">Resource Type: <%= resourceType %></font></h3>
    	<p id="searchMessage"></p>
 		<p id="loadingMessage" style="display: none;"></p> 
		<div class=searchbox>
			<input type="search" class="ui-widget input" id="searchterms" size="40" placeholder="Enter resource title search string" autofocus>
			<button class="ui-widget-button" id="search" type="button">Search</button>
		</div>
		<div class="ui-widget-content">
			<select  class="ui-widget-select selector" id="results" size=8></select>
			<button class="ui-widget-button" id="cancel" type="button">Cancel</button>
			<button class="ui-widget-button" id="select" type="button">OK</button>
		</div>

</body>
</html>