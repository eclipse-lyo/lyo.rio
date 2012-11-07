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
    Michael Fiedler	 - adapt for OSLC4J
    Malcolm McKinney - design and UI integration
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.net.*,java.util.*" %>
<%@ page import="java.net.*,java.util.*,java.text.SimpleDateFormat" %>
<%@ page import="org.eclipse.lyo.oslc4j.changemanagement.ChangeRequest" %>
<%

String created, modified, uri, title, identifier;

ChangeRequest changeRequest = (ChangeRequest)request.getAttribute("changeRequest");

	Date createdDate = (Date) changeRequest.getCreated(); 
	SimpleDateFormat formatter = new SimpleDateFormat();
	created = formatter.format(createdDate);
	Date modifiedDate = (Date) changeRequest.getModified();
	modified = formatter.format(modifiedDate);
	uri = request.getRequestURI();
	title = changeRequest.getTitle();
	identifier = changeRequest.getIdentifier()+""; 

%>
<html>
<style type="text/css">
body {	
	width: 64%;
	margin: 0 auto;
}

h1 {
   padding: 15px 0 0 5px;
   float: left;
   font-size: 55px;
   margin-top: 50px;
   margin-bottom: 50px;
   }

   
img {
	margin-top: 50px;
	display: block;
	float: left;
}

p{	margin-left: 200px;
	
}

#thebody {	
	width: 64%;
	margin: 0 auto;
    width: 30em;
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
    	<h1> <font face="verdana">SLC Change Request </font></h1>
    
    	<br></br>
    	<br></br>
    
<p>Title: <%= title %></p>
<p>Identifier: <%= identifier %></p>
<p>Created: <%= created %></p>
<p>Last Modified: <%= modified %></p>
  </div>
</body>
</html>