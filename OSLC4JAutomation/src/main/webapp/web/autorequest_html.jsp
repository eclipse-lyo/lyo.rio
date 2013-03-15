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
 
    Michael Fiedler	- initial implementation

--%>

<%@page import="org.eclipse.lyo.oslc4j.automation.AutomationRequest"%>
<%@page import="org.eclipse.lyo.oslc4j.automation.AutomationResource"%>
<%@page import="org.eclipse.lyo.oslc4j.automation.ParameterInstance"%>
<%@page import="org.eclipse.lyo.oslc4j.core.model.Link"%>
<%@page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider"%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%
    AutomationRequest autoRequest = (AutomationRequest) request.getAttribute("autoRequest");

	Link autoPlanLink = autoRequest.getExecutesAutomationPlan();
	if (autoPlanLink != null) {
		if (autoPlanLink.getLabel() == null || autoPlanLink.getLabel().isEmpty())
			autoPlanLink.setLabel(autoPlanLink.getValue().toString());
	}
	
	ParameterInstance[] properties = autoRequest.getInputParameters();

%>
<html>
		
<head>

	<script type="text/javascript" src="../../web/autoDialogs.js"></script>
	<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="../../web/jquery-ui-1.8.18.custom.min.js"></script>
	<script>
		$().ready(function(){ 
			 $("th").each(function(){
			 
			  $(this).addClass("ui-state-default");
			 
			  });
			 $("td").each(function(){
			 
			  $(this).addClass("ui-widget-content");
			 
			  });
			 $("tr").hover(
			     function()
			     {
			      $(this).children("td").addClass("ui-state-hover");
			     },
			     function()
			     {
			      $(this).children("td").removeClass("ui-state-hover");
			     }
			    );
			 $("tr").click(function(){
			   
			   $(this).children("td").toggleClass("ui-state-highlight");
			  });
			 
		}); 
	</script>
	<link rel="stylesheet" type="text/css" href="../../web/jquery-ui-1.8.18.custom.css"></link>
	<style>
		#logo {
			padding-top: 35px;
		}
		body  {
			width: 700px;
			padding: 50px 0px 0px 50px;
		}
		td {
			font-weight: bold
		}
		#resourceTable {
		    width: 700px;
		}
	</style>

</head>

<body>		
		<div>
    		<img src="http://open-services.net/css/images/logo-forflip.png" id="logo" alt="O" width="71" height="80" align= "left"/>
    	</div>
    	<div>
    		<h2 class="ui-widget-header">OSLC Automation Request</h2>
    		<h3><%= autoRequest.getIdentifier() %>: <%= autoRequest.getTitle() %></h3>
    		<table id="resourceTable">
    			<tr>
    				<td>ID</td>
    				<td><%= autoRequest.getIdentifier() %></td>
    			</tr>
    			<tr>
    				<td>Title</td>
    				<td><%= autoRequest.getTitle() %></td>
    			</tr>
    			<tr>
    				<td>Created</td> 
    				<td><%= autoRequest.getCreated().toLocaleString() %></td>
    			</tr>
				<tr>
    				<td>State</td> 
    				<td><%= autoRequest.getStates()[0].toString() %></td>
    			</tr>
    			<tr>
    				<td>Automation Plan</td> 
    				<td><a href="<%= autoPlanLink.getValue().toString() %>"><%= autoPlanLink.getLabel() %></a></td>
    			</tr>
    			<% if (properties.length > 0) { %>
    				<tr>
    					<td>Parameters:<td>
    				</tr>  					
    				<% for (int i=0; i < properties.length; i++ ) { %>
    					<tr>
    						<td><%= properties[i].getName() %></td>
    						<td><%= properties[i].getValue() %></td>
    					</tr>
    				<%  } 
    			} %>

			</table>	
    	</div>
	    	
</body>
</html>

