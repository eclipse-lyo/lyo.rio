<%@page import="org.eclipse.lyo.rio.store.XmlFormatter"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.core.IConstants,org.eclipse.lyo.rio.store.RioResource,org.eclipse.lyo.oslc.am.linktype.*,java.util.*,org.eclipse.lyo.rio.util.*" %>
<%@ page import="org.eclipse.lyo.oslc.am.common.IAmConstants" %>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
LinkType linkType = (LinkType) request.getAttribute("linkType");
String about = linkType.getAbout();
String label = linkType.getLabel();
String comments = linkType.getComments();
String editUri = linkType.getUri();
String created = linkType.getCreated().toString();
String modified = linkType.getModified().toString();
String eTag = linkType.getETag();
XmlFormatter formatter = new XmlFormatter();
formatter.addNamespacePrefix("http://open-services.net/ns/am#", "oslc_am");
String rawRdf = formatter.format(linkType, IAmConstants.OSLC_AM_TYPE_LINKTYPE);
String rdfXml = StringUtils.forHtml(rawRdf);
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
<title>RIO AM Link Type View (<%=label%>)</title>
<link rel="SHORTCUT ICON" href="../oslc.png">
</head>
<body>
<p><a href="<%=uriBase%>/index.jsp">Home</a></p>
<h1>Link Type: <%=label %></h1>
<table>
<tr><td>About</td><td><%=about %></td></tr>
<tr><td>Label</td><td><%=label %></td></tr>
<tr><td>Comments</td><td><%=comments %></td></tr>
<tr><td>Edit URI</td><td><%=editUri %></td></tr>
<tr><td>Created</td><td><%=created %></td></tr>
<tr><td>Modified</td><td><%=modified %></td></tr>
<tr><td>ETag</td><td><%=eTag %></td></tr>
</table>
<h3>RDF/XML</h3>
<pre><%=rdfXml %></pre>

</body>
</html>