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
<%@ page import="java.util.Map" %>
<%
	
	String creatorUri = (String) request.getAttribute("creatorUri");
    Map<String,String> autoPlans = (Map<String,String>) request.getAttribute("autoPlans");

%>
<html>
<head>

<script type="text/javascript" src="../../web/autoDialogs.js"></script>

<style type="text/css">
body {	
	margin: 0 auto;
}

h2 {
   padding: 15px 0 0 5px;
   float: left;
   margin-top: 20px;
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
	width: 550px;
	margin: 0 auto;
	background-color: #FAFAD2;
	border: 5px solid #191970;
	border-radius:35px;
	-moz-box-shadow: -5px -5px 5px #888;
	-webkit-box-shadow: -5px -5px 5px #888;
	box-shadow: -5px -5px 5px #888;
}

td {
	width:100px;
	height:20px;
	text-align:center;
} 
</style>
</head>

<body style="padding: 50px;">
	<div id="thebody">
		
    	<img src="http://open-services.net/css/images/logo-forflip.png" alt="Logo" width="71" height="80" align= "left"/>
    	<h2> <font face="verdana">Automation Request Creation Dialog </font></h2>

 
    <form id="Create" method="POST" >
			
			
		<table style="clear: both;" id="inputTable">
			
				<tr>
					<th class="field_label">Automation Plan:</th>
					<td><select id="plan">
							<%
								for (String id : autoPlans.keySet()) {
									String title = autoPlans.get(id);
							%>
							<option value="<%=id%>"><%=id%>: <%=title%></option>
							<%
								}
							%>
					</select></td>
					<td>
						<button style="float: left; margin-left: 10px; margin-bottom: 5px;" type="button"
				             onclick="requestParams( '<%= creatorUri %>' )">Get Params</button>
				    </td>
				</tr>
				<tr>
					<td>
						<div style="float: left; margin-left: 15px;" id="paramInput"><p><b>Parameters:</b></p></div>		
					</td>
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

