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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.store.XmlFormatter"%>
<%@ page import="java.util.*,org.eclipse.lyo.rio.util.*" %>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%@ page import="org.eclipse.lyo.rio.cm.changerequest.ChangeRequest" %>
<%@ page import="org.eclipse.lyo.rio.cm.common.ICmConstants" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
ChangeRequest cr = (ChangeRequest) request.getAttribute("changerequest"); 
String uri = cr.getUri();
String title = cr.getTitle();
String description = cr.getDescription();
String created = cr.getCreated().toString();
String modified = cr.getModified().toString();
String eTag = cr.getETag();
String status = cr.getStatus();
String isStatusSubmitted = "";
if( "Submitted".equals(status) ) isStatusSubmitted = "selected";
String isStatusInProgress = "";
if( "In Progress".equals(status) ) isStatusInProgress = "selected";
String isStatusDone = "";
if( "Done".equals(status) ) isStatusDone = "selected";
String isStatusFixed = "";
if( "Fixed".equals(status) ) isStatusFixed = "selected";
boolean isApproved = cr.isApproved();
String isApprovedCheck = "";
if( isApproved ) isApprovedCheck = "checked";
boolean isReviewed = cr.isReviewed();
String isReviewedCheck = "";
if( isReviewed ) isReviewedCheck = "checked";
boolean isVerified = cr.isVerified();
String isVerifiedCheck = "";
if( isVerified ) isVerifiedCheck = "checked"; 
boolean isClosed = cr.isClosed();
boolean isFixed = cr.isFixed();
boolean isInProgress = cr.isInProgress();
Collection<String> relatedChangeRequests = cr.getRelatedChangeRequests();
Collection<String> affectedByPlanItem = cr.getAffectsPlanItem();
Collection<String> affectedByDefect = cr.getAffectedByDefect();
XmlFormatter formatter = new XmlFormatter();
formatter.addNamespacePrefix(ICmConstants.OSLC_CM_NAMESPACE,ICmConstants.OSLC_CM_PREFIX); 
String rawRdf = formatter.format(cr, ICmConstants.OSLC_CM_TYPE_CHANGEREQUEST);
String rdfXml = XmlUtils.encode(rawRdf);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Change Request View (<%=uri%>)</title>
<link rel="SHORTCUT ICON" href="../oslc.png">
</head>
<body>
<p><a href="<%=uriBase%>/index.jsp">Home</a></p>
<h1>OSLC Change Request: <%=title %></h1>
<form action="<%=uriBase%>/web/changerequest" method="POST">
<input type="hidden" name="uri" value="<%=uri %>"/>
<input type="hidden" name="eTag" value="<%=eTag %>"/>
<table>
<tr><td>URI:</td><td><b><%=uri %></b></td></tr>
<tr><td>Title:</td><td><input name="title" type="text" size="80" value="<%=title %>"/></td></tr>
<tr><td>Created:</td><td><b><%=created %></b></td></tr>
<tr><td>Modified:</td><td><b><%=modified %></b></td></tr>
<tr><td>ETag:</td><td><b><%=eTag %></b></td></tr>
<tr><td>Status:</td><td>
<select name="status" >
  <option value="Submitted" <%=isStatusSubmitted %>>Submitted</option>
  <option value="In Progress" <%=isStatusInProgress %>>In Progress</option>
  <option value="Done" <%=isStatusDone%>>Done</option>
  <option value="Fixed" <%=isStatusFixed%>>Fixed</option>
</select></td></tr>
<tr><td/><td>
<input name="reviewed" type="checkbox" <%=isReviewedCheck %>>Reviewed&nbsp;
<input name="verified" type="checkbox" <%=isVerifiedCheck %>>Verified&nbsp;
<input name="approved" type="checkbox" <%=isApprovedCheck %>>Approved
</td></tr>
<tr><td>Description</td><td>
<textarea name="description" rows="10" cols="80" id="description"><%=description %></textarea>
</td></tr>
<tr><td>Status Flags</td>
<td>Closed: <b><%=isClosed %></b>&nbsp;Fixed: <b><%=isFixed %></b>&nbsp;
In Progress: <b><%=isInProgress %></b>&nbsp;
</td></tr>
<tr><td>Related Change Requests:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( relatedChangeRequests.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String related : relatedChangeRequests ) { %>
<tr><td></td><td><%=related %></td></tr>
<% } %>
<tr><td>Affected By Plan Item:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( affectedByPlanItem.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String related : affectedByPlanItem ) { %>
<tr><td></td><td><%=related %></td></tr>
<% } %>
<tr><td>Affected By Defect:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( affectedByDefect.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %> 
<% for( String related : affectedByDefect ) { %>
<tr><td></td><td><%=related %></td></tr>
<% } %>
</table>
<input type="submit" value="Save"/>
</form>
<h3>RDF/XML</h3>
<form action="<%=uriBase%>/web/changerequest" method="POST">
<input type="hidden" name="uri" value="<%=uri %>"/>
<input type="hidden" name="eTag" value="<%=eTag %>"/>
<pre><textarea name="rdfxml" rows=20 cols=132 id="rdfxml"><%=rdfXml %></textarea></pre>
<input type="submit" name="Save" value="SaveRdf"/>
</form>
</body>
</html>