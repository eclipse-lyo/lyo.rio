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
%>
<html>
<head>
<script type="text/javascript" src="../../autoDialogs.js"></script>

<style type="text/css">
body {margin: 0 auto;}

h1 {
   padding: 15px 0 0 5px;
   float: left;
   font-size: 55px;
   margin-top: 50px;
   }
   
img {
	margin-top: 50px;
	margin-left: 77px;
}

#thebody {	
	width: 64%;
	margin: 0 auto;
    width: 25em;
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
		
    	<img src="http://oslc-tools.sourceforge.net/images/OSLC_FullCol_Title.png" alt="Logo" width="71" height="80" align= "left"/>
    	<h1> <font face="verdana">OSLC Selection </font></h1>
    	<br></br>
    	<br></br>
		 
		 <p id="searchMessage"></p>

	 	<p id="loadingMessage" style="display: none;"></p> 
			
		<div>
			<input type="search" style="width: 300px; margin-left: 130px; margin-bottom: 20px; border-radius: 5px " id="searchTerms" placeholder="Describe an Automation Resource" autofocus>
			<button type="button"
				onclick="search( '<%= selectionUri %>' )">Search</button>
		</div>

		<div style="margin-top: 5px;">
			<select id="results" size="8" style="width: 400px; margin-left: 130px;border-radius: 5px; "></select>
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