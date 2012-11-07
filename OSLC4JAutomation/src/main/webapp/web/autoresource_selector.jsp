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

<style type="text/css">
body {margin: 0 auto;}

h2 {
   padding: 15px 0 0 5px;
   float: left;
   margin-top: 50px;
   margin-left: 25px;
   margin-bottom: 25px;
   }
   
h3 {
   padding: 15px 0 0 5px;
   float: left;
   margin-top: 25px;
   margin-left: 25px;
   }
   
img {
	margin-top: 50px;
	margin-left: 25px;
	margin-bottom: 25px;
}

#thebody {	
	width: 500px;
	margin: 0 auto;
	background-color: #FAFAD2;
	border: 5px solid #191970;
	border-radius:35px;
	-moz-box-shadow: -5px -5px 5px #888;
	-webkit-box-shadow: -5px -5px 5px #888;
	box-shadow: -5px -5px 5px #888;
}

</style>
</head>
<body style="padding: 50px;">
	<%-- Padding --%>
	
	<div id="thebody">
		
    	<img src="http://open-services.net/css/images/logo-forflip.png" alt="Logo" width="71" height="80" align= "left"/>
    	<h2> <font face="verdana">OSLC Selection Dialog</font></h2>
    	<div>
    		<h3> <font face="verdana">Resource Type: <%= resourceType %></font></h3>
    		<p id="searchMessage"></p>
 			<p id="loadingMessage" style="display: none;"></p> 
    	</div>
	
		<div>
			<input type="search" style="width: 300px; margin-left: 25px; float: left; border-radius: 5px " id="searchTerms" placeholder="Enter resource title search string" autofocus>
			<button style="float: left; margin-left: 20px; margin-bottom: 20px;" type="button"
				onclick="search( '<%= selectionUri %>' )">Search</button>
		</div>

		<div style="margin-top: 5px;">
			<select id="results" size="8" style="width: 400px; margin-left: 25px;border-radius: 5px; "></select>
		</div>

		<div style="width: 150px; margin-left: 245px; margin-top: 20px;">
			<button style="float: left; margin-bottom: 20px" type="button"
				onclick="javascript: cancel()">Cancel</button>
			<button style="float: right;" type="button"
				onclick="javascript: select();">OK</button>
		</div>
		
		<%-- So the buttons don't float outside the content area. --%>
		<div style="clear: both;"></div>

	</div>
</body>
</html>