<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%@ page import="org.eclipse.lyo.rio.cm.common.ICmConstants" %>
<%
if( !RioStore.isStoreInitialized() ) {
	String url = "setup?url=/" + ICmConstants.SERVER_CONTEXT + "/index.jsp";
	response.sendRedirect(url);	
}
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
<link rel="SHORTCUT ICON" href="oslc.png">
<title>Simple OSLC CM Reference Implementation </title>
<script language="JavaScript">
function dialog(url,width,height) {
     var ret = window.showModalDialog(url + "#oslc-core-windowName-1.0",
           "","dialogWidth:" + width + "px; dialogHeight:" + height + "px; center:yes, resizable: yes, status: no, help: no");
}
</script>
<base target="_self" />
</head>
<body>
<table><tr><td><img src="oslcLg.png"/></td><td><h2>Home:<br/>Reference Implementation OSLC (RIO)<br/> for Change Management</h2></td></tr></table>
<p><a href="about.jsp">About RIO-CM</a></p>
<p><b>Services</b></p>
<blockquote>
<p><a href="catalog">Catalog</a> (OSLC Catalog document)</p>
<p><a href="services">Service Provider</a> (OSLC ServiceProvider document)</p>
<p><a href="javascript: dialog('creator/changerequest',450,250)">UI Change Request Creator</a> (Delegated UI resource creator)</p>
<p><a href="javascript: dialog('selector/changerequest',350,300)">UI Change Request Picker</a> (Delegated UI resource selector)</p>
</blockquote>
<p><b>Server Admin Tasks and Utilities:</b></p>
<blockquote>
<p><form action="generate/changerequest" method="post">
<input value="Generate" type="submit">&nbsp;<input type="text" name="count" value="30" size=3/>Resources
</form>
<p/>
<p><a href="sparql">SPARQL</a> (Free form SPARQL of RDF Store)</p>
<p><a href="list/changerequest">Listing of Change Requests</a> (listing of all stored change request resources)</p>
<p><a href="dump">Dump Repository</a> (NTriple dump of entire RDF Store)</p>
<p><a href="clean">Totally wipe clean</a> the triple store</p>
<p><a href="setup">Reconfigure RDF</a>  store and binary locations (setup)</p>
</blockquote>
</body>
</html>