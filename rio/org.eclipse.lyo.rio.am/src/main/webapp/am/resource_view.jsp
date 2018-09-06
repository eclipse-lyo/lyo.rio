<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.core.IConstants" %>
<%@ page import="org.eclipse.lyo.rio.store.*"%>
<%@ page import="org.eclipse.lyo.oslc.am.resource.Resource,java.util.*,org.eclipse.lyo.rio.util.*" %>
<%@ page import="org.eclipse.lyo.oslc.am.common.IAmConstants" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
Resource resource = (Resource) request.getAttribute("resource"); 
String uri = resource.getUri();
String title = resource.getTitle();
String description = resource.getDescription();
String created = resource.getCreated().toString();
String modified = resource.getModified().toString();
String eTag = resource.getETag();
XmlFormatter formatter = new XmlFormatter();
formatter.addNamespacePrefix(IAmConstants.OSLC_AM_NAMESPACE,IAmConstants.OSLC_AM_PREFIX); 
String rawRdf = formatter.format(resource, IAmConstants.OSLC_AM_TYPE_RESOURCE);
String rdfXml = XmlUtils.encode(rawRdf);
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
<title>RIO AM Resource View (<%=uri%>)</title>
<link rel="SHORTCUT ICON" href="../oslc.png">
</head>
<body>
<p><a href="<%=uriBase%>/index.jsp">Home</a></p>
<h1>OSLC AM Resource: <%=title %></h1>
<form action="<%=uriBase%>/web/resource" method="POST">
<input type="hidden" name="uri" value="<%=uri %>"/>
<input type="hidden" name="eTag" value="<%=eTag %>"/>
<table>
<tr><td>URI:</td><td><b><%=uri %></b></td></tr>
<tr><td>Title:</td><td><b><input type="text" name="title" size=80 value="<%=title %>"/></b></td></tr>
<tr><td>Created:</td><td><b><%=created %></b></td></tr>
<tr><td>Modified:</td><td><b><%=modified %></b></td></tr>
<tr><td>ETag:</td><td><b><%=eTag %></b></td></tr>
<tr><td>Description:</td><td>
<textarea name="description" rows="10" cols="80" id="description"><%=description %></textarea>
</td></tr>
</table>
<input type="submit" name="Save" value="Save"/>
</form>
<h3>RDF/XML</h3>
<form action="<%=uriBase%>/web/resource" method="POST">
<input type="hidden" name="uri" value="<%=uri %>"/>
<input type="hidden" name="eTag" value="<%=eTag %>"/>
<pre><textarea name="rdfxml" rows=20 cols=132 id="rdfxml"><%=rdfXml %></textarea></pre>
<input type="submit" name="Save" value="SaveRdf"/>
</form>
</body>
</html>