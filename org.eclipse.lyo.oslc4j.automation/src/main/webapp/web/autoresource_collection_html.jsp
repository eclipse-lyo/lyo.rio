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
    String resourceType = (String) request.getAttribute("resourceType");
	
%>
<html>
	
<head>

	<link rel="stylesheet" type="text/css" href="../web/jquery-ui-1.8.18.custom.css"></link>
	<style>
		#logo {
			padding-top: 35px;
		}
		body  {
			width: 700px;
			padding: 50px 0px 0px 50px;
		}
	</style>

</head>

<body>		
		<div>
    		<img src="http://open-services.net/css/images/logo-forflip.png" id="logo" alt="O" width="71" height="80" align= "left"/>
    	</div>
    	<div>
    		<h2 class="ui-widget-header">OSLC Automation <%=resourceType %> Collection </h2>
	    	<h3>Query Results</h3>
        	<div class="ui-widget-content"> 
                <% for (AutomationResource autoResource : autoResources) { %>                
                <p><a href="<%=autoResource.getAbout() %>">
                	<%=autoResource.getTitle() %></a></p>
			    <% } %>
        	</div>   
		</div>
</body>
</html>

