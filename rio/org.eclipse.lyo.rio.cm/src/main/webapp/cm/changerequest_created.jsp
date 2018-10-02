<html>
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
<%@ page import="org.eclipse.lyo.rio.core.IConstants" %>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%@ page import="org.eclipse.lyo.rio.cm.changerequest.ChangeRequest" %>
<%@ page import="org.eclipse.lyo.rio.cm.changerequest.ChangeRequest" %>
<%@ page import="org.eclipse.lyo.rio.util.StringUtils" %>
<%
ChangeRequest resource = (ChangeRequest)request.getAttribute("resource");
response.setStatus(IConstants.SC_CREATED);
response.setHeader(IConstants.HDR_LOCATION, resource.getUri());
response.setHeader(IConstants.HDR_ETAG, resource.getETag());
response.setHeader(IConstants.HDR_LAST_MODIFIED, StringUtils.rfc2822(resource.getModified()));
%> 
<head>
<script type="text/javascript">
   // Step #2: read the return URL 
   var returnURL = window.name;

   // Step #4: send the response via the window.name variable 
   var response = "oslc-response:{\"oslc:results\" \: [{ \"oslc:label\" : \"<%= resource.getTitle() %>\", \"rdf:resource\" : \"<%= resource.getUri() %>\"}]}";
   window.name = response;

   // Step #5: indicate that user has responded 
   window.location.href = returnURL;
</script>
</head>
<body>
</body>
</html>
