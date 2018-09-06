<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.core.IConstants" %>
<%@ page import="java.util.*, org.eclipse.lyo.rio.store.*, org.eclipse.lyo.rio.util.*" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
String queryExp = (String) request.getAttribute("queryExp");
if( queryExp == null ) {
	queryExp = 
		"PREFIX dc:<http://purl.org/dc/terms/>\n" +
		"SELECT ?title ?uri \n" + 
		"WHERE {\n" +
		"  ?uri dc:title ?title. \n" +
		"}";
}
queryExp = XmlUtils.encode(queryExp);
List<Map<String,RioValue>> results = (List<Map<String,RioValue>>) request.getAttribute("results");
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
<title>RDF Store SPARQL</title>
</head>
<body>
<p><a href="<%=uriBase%>">Home</a></p>
<h3>SPARQL</h3>
<form method=post action="sparql">
<textarea rows="15" cols="100" name="queryExp"><%=queryExp %></textarea><br/>
<input type="submit" name="Submit" value="Query"/>
</form>
<hr/>
<% if( results != null  ) { %>
<h3>Results</h3>
<table border=1">
<%
	boolean needsHeaderRow = true;
	Iterator<Map<String,RioValue>> rowIterator = results.iterator();
	while( rowIterator.hasNext() ) {
		Map<String,RioValue> row = (Map<String,RioValue>) rowIterator.next();
		Set<String> columns = row.keySet();
		if( needsHeaderRow ) {
%>         <tr> <%
			for(String heading : columns ) {
				RioValue val = row.get(heading);
%>    		    <th><%=heading %><br/>(<%=val.getType() %>)</th>  <%
			}
%>		   </tr> <%
			needsHeaderRow = false;
		}
%>         <tr> <%
		for(String col : columns ) {
			RioValue val = row.get(col);
%>    		    <td><%=val.stringValue() %></td>  <%
		}
%>		   </tr> <%
%>
	<tr>
<%
	}
%>
<%} %>
</table>
</body>
</html>