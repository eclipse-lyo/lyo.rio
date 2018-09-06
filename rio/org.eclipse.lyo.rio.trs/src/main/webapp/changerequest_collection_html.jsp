<!DOCTYPE html>

<%--
 Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and conditions in the IBM International Program License Agreement (IPLA).
 © Copyright IBM Corporation 2012, 2013. All Rights Reserved. 
 U.S. Government Users Restricted Rights: Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 --%>
 
<%@page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@page import="org.eclipse.lyo.rio.trs.cm.ChangeRequest"%>
<%@page import="org.eclipse.lyo.oslc4j.core.model.ResponseInfoArray;" %> 

<%
	ResponseInfoArray<ChangeRequest> changeRequests = (ResponseInfoArray<ChangeRequest>) request.getAttribute("results");
	Object[] arrCRO =  changeRequests.array();	
	String nextPageUri = (String)request.getAttribute("nextPageUri");
	String currentPageUri = (String)request.getAttribute("currentPageUri");
	String scriptPath = (String) request.getContextPath()+"/TestApp.js";
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
		<script type="text/javascript" src='<%= scriptPath %>'>
		</script>
	</head>
	<body onload="">

		<div id="jlip_test-body">  
			<div id="page-index">
	
				<h1>Query Results</h1>

                <% for (int i = 0; i < arrCRO.length; i++) { %>    
                <%   ChangeRequest  changeRequest = (ChangeRequest) arrCRO[i]; %> 
                <div id='<%= changeRequest.getAbout() %>'>	  
                <p>Summary: <%= changeRequest.getTitle() %><br /><a href="<%= changeRequest.getAbout() %>">
                	<%= changeRequest.getAbout() %></a> 
                	<input type="button"
							value="Delete"
							onclick="javascript:deleteResource( '<%= changeRequest.getAbout() %>','<%= currentPageUri %>'  )">
                	</p>
                </div>
			    <% } %>
            	<% if (nextPageUri != null) { %><a href="<%= nextPageUri %>">Next Page</a><% } %>

			</div>
		</div>
		
		
		<%-- --%>
		
	</body>
</html>
