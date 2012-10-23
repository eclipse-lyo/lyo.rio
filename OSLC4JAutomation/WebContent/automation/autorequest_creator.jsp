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
 
    Sam Padgett 	 - initial API and implementation
    Michael Fiedler	 - adapted for OSLC4J
    Malcolm McKinney - design and UI integration
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%
	
	String serviceProvider = (String)request.getAttribute("serviceProvider");
	String creatorUri = (String) request.getAttribute("creatorUri");
	
%>
<html>
<head>

<script type="text/javascript" src="../../cmDialogs.js"></script>

<style type="text/css">
body {	
	margin: 0 auto;
}

h1 {
   padding: 15px 0 0 5px;
   float: left;
   font-size: 55px;
   margin-top: 50px;
   }
  
td {
	width:100px;
	height:55px;
	text-align:center;
} 
   
img {
	margin-top: 50px;
	margin-left: 77px;
}
#thebody {	
	
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
	<div id="thebody">
		
    	<img src="http://oslc-tools.sourceforge.net/images/OSLC_FullCol_Title.png" alt="Logo" width="71" height="80" align= "left"/>
    	<h1> <font face="verdana">SLC Creation </font></h1>
 
    <form id="Create" method="POST" >
			
			
		<table style="clear: both;">
			
				<tr>
					<th class="field_label required">Summary:</th>
					<td><input name="title" class="required text_input"
						type="text" style="width: 400px; border-radius: 5px;" id="title" required autofocus></td>
				</tr>
			
	
				<tr>
					<th class="field_label required">Status:</th>
					<td><input name="status" class="required text_input"
						type="text" style="width: 400px; border-radius: 5px" id="status" required autofocus></td>
				</tr>
		
		
				<tr>
					<th class="field_label">Description:</th>
					<td><textarea style="width: 400px; height: 150px; border-radius: 7px"
							id="description" name="description"></textarea></td>
				</tr>
						
				<tr>
					<td></td>
					<td>
						<input type="button" value="Cancel" onclick="javascript: cancel()">
						<input type="button" value="Submit" onclick="javascript: create( '<%= creatorUri %>' )">
						
					</td>
				</tr>
			</table>

			<div style="width: 500px;">				
			</div>			
		</form>

		<div style="clear: both;"></div>
	</div>
</body>
</html>

