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
 
    Michael Fiedler	- initial implementation

--%>

<%@page import="org.eclipse.lyo.oslc4j.qualitymanagement.TestScript"%>
<%@page import="org.eclipse.lyo.oslc4j.qualitymanagement.QmResource"%>
<%@page import="org.eclipse.lyo.oslc4j.core.model.Property"%>
<%@page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider"%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    TestScript testScript = (TestScript) request.getAttribute("testScript");

%>
<html>
		
<head>

	<script type="text/javascript" src="../../web/qmDialogs.js"></script>
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
    		<h2 class="ui-widget-header">OSLC4J Quality Management Test Script</h2>
    		<h3><c:out value="${testScript.identifier}"/>: <c:out value="${testScript.title}"/></h3>
    		<table id="resourceTable">
    			<tr>
    				<td>ID</td>
    				<td><c:out value="${testScript.identifier}"/></td>
    			</tr>
    			<tr>
    				<td>Title</td>
    				<td><c:out value="${testScript.title}"/></td>
    			</tr>
    			<tr>
    				<td>Description</td>
    				<td><c:out value="${testScript.description}"/></td>
    			</tr>
    			<tr>
    				<td>Created</td> 
    				<td><%=testScript.getCreated().toLocaleString()%></td>
    			</tr>
				<tr>
    				<td>Modified</td> 
    				<td><%=testScript.getModified().toLocaleString()%></td>
    			</tr>
    			<tr></tr><tr></tr>
    				
    		</table>				
    	</div>
	    	
</body>
</html>

