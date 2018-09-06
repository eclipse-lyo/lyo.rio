<%@ page import="java.util.*,org.eclipse.lyo.rio.util.*,org.eclipse.lyo.rio.store.*" %>
<%@ page import="org.eclipse.lyo.rio.core.IConstants" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
OslcResource resource = (OslcResource) request.getAttribute("resource"); 
String title = resource.getTitle();
String uri = resource.getUri();
List<RioValue> valueSlideList = resource.getSeq("http://open-services.net/ri/rio/slides");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
    Copyright (c) 2011 IBM Corporation.
   
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License v1.0
    and Eclipse Distribution License v. 1.0 which accompanies this distribution. 
   
    The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
    and the Eclipse Distribution License is available at 
    http://www.eclipse.org/org/documents/edl-v10.php.
   
    Contributors:
   
       Jim Conallen - initial API and implementation
 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Deck Resource View (<%=uri%>)</title>
<link rel="SHORTCUT ICON" href="../oslc.png">
</head>
<body>
<p><a href="<%=uriBase %>/index.jsp">Home</a></p>
<h2>Deck Resource: <%=title %></h2>
<h2>Slides</h2>
<table>
<%
int slideNo = 0;
for( RioValue valSlide : valueSlideList ) { 
  ++slideNo;
  // a little trick to get the img URI for this slide without extra javascripting
  String slideUri = valSlide.stringValue();
  String[] segs = slideUri.split("/");
  String imgHref = "http://" + segs[2] + "/" + segs[3] + "/source/" + segs[5];
%>
<tr>
  <td><a href="<%=valSlide.stringValue() %>">Slide #<%=slideNo%></a></td>
  <td><img src=<%=imgHref%> height="150" width="200"/></td>
</tr>
<% } %>
</table>
</body>
</html>