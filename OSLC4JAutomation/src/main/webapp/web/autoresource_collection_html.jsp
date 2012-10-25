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
 
    Sam Padgett	  	- initial API and implementation
    Michael Fiedler	- adapted for OSLC4J
    Malcolm McKinney - design and UI integration
--%>

<%@page import="org.eclipse.lyo.oslc4j.changemanagement.ChangeRequest"%>
<%@page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider"%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%
    List<ChangeRequest> changeRequests = (List<ChangeRequest>) request.getAttribute("results");
    ServiceProvider serviceProvider = (ServiceProvider) request.getAttribute("serviceProvider");		
	
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
		
<style type="text/css">
body {margin: 0 auto;}
p { text-indent: 25px;}

h1 {
   padding: 15px 0 0 5px;
   float: left;
   font-size: 55px;
   margin-top: 50px;
   }

h2 {
	text-align: center;
}
img {
	margin-top: 50px;
	margin-left: 60px;
}

#thebody {	
	width: 64%;
	margin: 0 auto;
    width: 27em;
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
		
    	<img src="http://oslc-tools.sourceforge.net/images/OSLC_FullCol_Title.png" alt="O" width="71" height="80" align= "left"/>
    	<h1> <font face="verdana">SLC Collection </font></h1>
    	<br> </br>
    	<br> </br>
    	
    
				<h2>Query Results</h2>

                <% for (ChangeRequest changeRequest : changeRequests) { %>                
                <p>Summary: <%= changeRequest.getTitle() %><br /><a href="<%= changeRequest.getAbout() %>">
                	<%= changeRequest.getAbout() %></a></p>
			    <% } %>
           
		<div id="footer">
			<div class="intro"></div>
			<div class="outro">
				
					<b>OSLC Tools Adapter Server 0.1</b> brought to you by <a href="http://eclipse.org/lyo">Eclipse Lyo</a><br />
				<br></br>
		</div>
			</div>
		</div>
	</body>
</html>

