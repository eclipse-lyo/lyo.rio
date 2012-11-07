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

<%@page import="org.eclipse.lyo.oslc4j.automation.AutomationResource"%>
<%@page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider"%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%
    List<AutomationResource> autoResources = (List<AutomationResource>) request.getAttribute("results");		
	
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
		
<style type="text/css">
body {margin: 0 auto;}
p { text-indent: 25px;}



#reslist {
    width: 600px;
	text-align: left;
	float: left;
	margin-left: 10px;
	clear: both;
}
#head {
    width: 600px;
	text-align: center;
	float: left;
	margin-left: 10px;
	clear: both;
}
img {
	margin-top: 10px;
	margin-left: 10px;
}

#thebody {	
	width: 600px;
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
	<div id="thebody">		
		<div id="head">
    		<img src="http://open-services.net/css/images/logo-forflip.png" alt="O" width="71" height="80" align= "left"/>
    		<h1> <font face="verdana">OSLC Collection </font></h1>
    	</div>
    	<div id="head">
	    	<h2>Query Results</h2>
	    </div>
        <div id="reslist"> 
        	<br/><br/>
                <% for (AutomationResource autoResource : autoResources) { %>                
                <p><a href="<%=autoResource.getAbout() %>">
                	<%=autoResource.getTitle() %></a></p>
			    <% } %>
        </div>   
		<div id="footer">
			<div class="intro"></div>
			<div class="outro">
				
					<p>Brought to you by <a href="http://eclipse.org/lyo">Eclipse Lyo</a></p><br />
				<br></br>
			</div>
		</div>
	</div>
</body>
</html>

