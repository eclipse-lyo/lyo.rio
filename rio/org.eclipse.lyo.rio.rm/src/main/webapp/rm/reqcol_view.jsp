<%@ page import="org.eclipse.lyo.rio.store.XmlFormatter"%>
<%@ page import="org.eclipse.lyo.rio.core.IConstants" %>
<%@ page import="org.eclipse.lyo.oslc.rm.services.requirementcollection.RequirementCollection,java.util.*,org.eclipse.lyo.rio.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%@ page import="org.eclipse.lyo.oslc.rm.common.IRmConstants" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
RequirementCollection requirement = (RequirementCollection) request.getAttribute("reqcol"); 
String uri = requirement.getUri();
String title = requirement.getTitle();
String shortTitle = requirement.getShortTitle();
String description = requirement.getDescription();
String created = requirement.getCreated().toString();
String modified = requirement.getModified().toString();
String eTag = requirement.getETag();
Collection<String> elaboratedBy = requirement.getElaboratedBy();
Collection<String> specifiedBy = requirement.getSpecifiedBy();
Collection<String> affectedBy = requirement.getAffectedBy();
Collection<String> trackedBy = requirement.getTrackedBy();
Collection<String> implementedBy = requirement.getImplementedBy();
Collection<String> validatedBy = requirement.getValidatedBy();
Collection<String> nestedClassifier = requirement.getNestedClassifier();
Collection<String> trace = requirement.getTrace();
Collection<String> deriveReqt = requirement.getDeriveReqt();
Collection<String> satisfy = requirement.getSatisfy();
Collection<String> verify = requirement.getVerify();
Collection<String> refine = requirement.getRefine();
Collection<String> isVersionOf = requirement.getIsVersionOf();
Collection<String> hasVersion = requirement.getHasVersion();
XmlFormatter formatter = new XmlFormatter();
formatter.addNamespacePrefix(IRmConstants.OSLC_RM_NAMESPACE,IRmConstants.OSLC_RM_PREFIX); 
String rawRdf = formatter.format(requirement,IRmConstants.OSLC_RM_TYPE_TERM_REQUIREMENTCOLLECTION);
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
<title>RIO RM Requirement View (<%=uri%>)</title>
<link rel="SHORTCUT ICON" href="../oslc.png">
<script>
var oslcPrefix;
var rdfPrefix;
var divId;
function getPrefix(xmlDoc,ns) {
	var attrs = xmlDoc.documentElement.attributes;
	for( var i=0; i<attrs.length; i++ ) {
		if( attrs[i].nodeValue == ns ) {
			var name = attrs[i].nodeName;
			var pos = name.indexOf(":");
			return ( name.substring(pos+1) );
		}
	}
}
function hover(uri,id){
	divId = id;
	var req = new XMLHttpRequest();  
	req.open('GET', uri, true);  
	req.setRequestHeader('Accept', 'application/x-oslc-compact+xml');
	req.onreadystatechange = function (aEvt) {  
		if (req.readyState == 4) {  
			if(req.status == 200) {  
				var xmlDoc = req.responseXML;
				oslcPrefix = getPrefix(xmlDoc, 'http://open-services.net/ns/core#');
				rdfPrefix = getPrefix(xmlDoc, 'http://www.w3.org/1999/02/22-rdf-syntax-ns#');
				var smPreview = xmlDoc.documentElement.getElementsByTagName(oslcPrefix + ':smallPreview')[0];
				if( smPreview ) {
					var oslcDoc = smPreview.getElementsByTagName(oslcPrefix + ':document')[0];
					if( oslcDoc ) {
						var url = oslcDoc.getAttribute(rdfPrefix + ':resource');
						if( oslcDoc ) {
							var div = document.getElementById(divId);
							if( div ) {
								var elmHintWidth = smPreview.getElementsByTagName(oslcPrefix + ':hintWidth')[0];
								var divWidth = elmHintWidth.textContent;
								var elmHintHeight = smPreview.getElementsByTagName(oslcPrefix + ':hintHeight')[0];
								var divHeight = elmHintHeight.textContent;
								div.innerHTML = '<'+'object type="text/html" data="'+url+'" width="'+divWidth+'" height="'+divHeight+'" style="background-color:#ffffee; border-style:solid;border-width:2px;"><\/object>';
							}
						}
					}
				}
			}   
		}  
	};
	req.send(null); 
}
function closeHover() { 
	if( divId ) {
		var elmDiv = document.getElementById(divId);
		if( elmDiv ) {
			elmDiv.innerHTML = '';
			elmDiv.width = null;
			elmDiv.height = null;
		}
	}
}
</script>
</head>
<body>
<p><a href="<%=uriBase%>/index.jsp">Home</a></p>
<h1>OSLC RM Resource: <%=title %></h1>
<form action="<%=uriBase%>/web/requirement" method="POST">
<input type="hidden" name="uri" value="<%=uri %>"/>
<input type="hidden" name="eTag" value="<%=eTag %>"/>
<table>
<tr><td>URI:</td><td><b><%=uri %></b></td></tr>
<tr><td>Title:</td><td><b><input type="text" name="title" size=80 value="<%=title %>"/></b></td></tr>
<tr><td>ShortTitle:</td><td><b><input type="text" name="shortTitle" size=80 value="<%=shortTitle %>"/></b></td></tr>
<tr><td>Created:</td><td><b><%=created %></b></td></tr>
<tr><td>Modified:</td><td><b><%=modified %></b></td></tr>
<tr><td>ETag:</td><td><b><%=eTag %></b></td></tr>
<tr><td>Description:</td><td>
<textarea name="description" rows="10" cols="80" id="description"><%=description %></textarea>
</td></tr>
<tr><td colspan="2" align="center"><b>Version History</b></td></tr>
<tr><td>Is Version Of:</td><td></td></tr>
<%if( isVersionOf.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String isVersionOfLink : isVersionOf ) {
	int pos = isVersionOfLink.lastIndexOf('/');
	String id = isVersionOfLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=isVersionOfLink %>" onmouseover="hover('<%=isVersionOfLink %>','d<%=id %>');" onmouseout="closeHover();"><%=isVersionOfLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Has Version:</td><td></td></tr>
<%if( hasVersion.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String hasVersionLink : hasVersion ) {
	int pos = hasVersionLink.lastIndexOf('/');
	String id = hasVersionLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=hasVersionLink %>" onmouseover="hover('<%=hasVersionLink %>','d<%=id %>');" onmouseout="closeHover();"><%=hasVersionLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td colspan="2" align="center"><b>OSLC RM Defined Link Types</b></td></tr>
<tr><td>Elaborated By:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( elaboratedBy.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String elaboratedByLink : elaboratedBy ) {
	int pos = elaboratedByLink.lastIndexOf('/');
	String id = elaboratedByLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=elaboratedByLink %>" onmouseover="hover('<%=elaboratedByLink %>','d<%=id %>');" onmouseout="closeHover();"><%=elaboratedByLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Specified By:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( specifiedBy.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String specifiedByLink : specifiedBy ) {
	int pos = specifiedByLink.lastIndexOf('/');
	String id = specifiedByLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=specifiedByLink %>" onmouseover="hover('<%=specifiedByLink %>','d<%=id %>');" onmouseout="closeHover();"><%=specifiedByLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Affected By:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( affectedBy.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String affectedByLink : affectedBy ) {
	int pos = affectedByLink.lastIndexOf('/');
	String id = affectedByLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=affectedByLink %>" onmouseover="hover('<%=affectedByLink %>','d<%=id %>');" onmouseout="closeHover();"><%=affectedByLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Tracked By:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( trackedBy.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String trackedByLink : trackedBy ) {
	int pos = trackedByLink.lastIndexOf('/');
	String id = trackedByLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=trackedByLink %>" onmouseover="hover('<%=trackedByLink %>','d<%=id %>');" onmouseout="closeHover();"><%=trackedByLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Implemented By:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( implementedBy.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String implementedByLink : trackedBy ) {
	int pos = implementedByLink.lastIndexOf('/');
	String id = implementedByLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=implementedByLink %>" onmouseover="hover('<%=implementedByLink %>','d<%=id %>');" onmouseout="closeHover();"><%=implementedByLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Validated By:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( validatedBy.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String validatedByLink : validatedBy ) {
	int pos = validatedByLink.lastIndexOf('/');
	String id = validatedByLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=validatedByLink %>" onmouseover="hover('<%=validatedByLink %>','d<%=id %>');" onmouseout="closeHover();"><%=validatedByLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td colspan="2" align="center"><b>SysML Defined Link Types</b></td></tr>
<tr><td>Nested Classifier:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( nestedClassifier.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String nestedClassifierLink : nestedClassifier ) {
	int pos = nestedClassifierLink.lastIndexOf('/');
	String id = nestedClassifierLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=nestedClassifierLink %>" onmouseover="hover('<%=nestedClassifierLink %>','d<%=id %>');" onmouseout="closeHover();"><%=nestedClassifierLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Derive Dependency:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( deriveReqt.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String deriveReqtLink : deriveReqt ) { 
	int pos = deriveReqtLink.lastIndexOf('/');
	String id = deriveReqtLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=deriveReqtLink %>" onmouseover="hover('<%=deriveReqtLink %>','d<%=id %>');" onmouseout="closeHover();"><%=deriveReqtLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Satisfy Dependency:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( satisfy.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String satisfyLink : satisfy ) {
	int pos = satisfyLink.lastIndexOf('/');
	String id = satisfyLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=satisfyLink %>" onmouseover="hover('<%=satisfyLink %>','d<%=id %>');" onmouseout="closeHover();"><%=satisfyLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Verify Dependency:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( verify.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String verifyLink : verify ) {
	int pos = verifyLink.lastIndexOf('/');
	String id = verifyLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=verifyLink %>" onmouseover="hover('<%=verifyLink %>','d<%=id %>');" onmouseout="closeHover();"><%=verifyLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
<tr><td>Trace Dependency:</td><td><a href="javascript: alert('To be implemented')">Add</a></td></tr>
<%if( trace.size() == 0 ) { %><tr><td></td><td>(none)</td></tr><%} %>
<% for( String traceLink : trace ) {
	int pos = traceLink.lastIndexOf('/');
	String id = traceLink.substring(pos+1);
%>
<tr><td>
</td><td><a href="<%=traceLink %>" onmouseover="hover('<%=traceLink %>','d<%=id %>');" onmouseout="closeHover();"><%=traceLink %></a>
<div id="d<%=id %>"></div>
</td></tr>
<% } %>
</table>
<input type="submit" value="Save"/>
</form>
<h3>RDF/XML</h3>
<form action="<%=uriBase%>/web/requirement" method="POST">
<input type="hidden" name="uri" value="<%=uri %>"/>
<input type="hidden" name="eTag" value="<%=eTag %>"/>
<pre><textarea name="rdfxml" rows=20 cols=132 id="rdfxml"><%=rdfXml %></textarea></pre>
<input type="submit" name="Save" value="SaveRdf"/>
</form>

</body>
</html>