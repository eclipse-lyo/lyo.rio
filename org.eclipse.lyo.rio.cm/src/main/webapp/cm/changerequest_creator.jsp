<%@ page import="org.eclipse.lyo.rio.core.IConstants" %>
<%@ page import="org.eclipse.lyo.rio.store.RioStore" %>
<%
RioStore store = RioStore.getStore();
String uriBase = store.getUriBase();
%>
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
<title>RIO - Change Request Creator</title>
<link rel="SHORTCUT ICON" href="../oslc.png">
<script language="JavaScript">
function postForm() {
	document.crForm.submit();
	window.close();
}
</script>
</head>
<body>
<form name="crForm" action="<%=uriBase%>/creator/changerequest" method="post">
Title: (required)<br/>
<input name="title" type="text" style="width:400px" id="title" /><br/>
<select name="status" >
  <option value="Submitted">Submitted</option>
  <option value="In Progress">In Progress</option>
  <option value="Done">Done</option>
  <option value="Fixed">Fixed</option>
</select><br/>
<input name="reviewed" type="checkbox"/>Reviewed&nbsp;
<input name="verified" type="checkbox"/>Verified&nbsp;
<input name="approved" type="checkbox"/>Approved<br/>
Description: (optional):<br/>
<textarea name="description" rows="3" cols="100" style="width:400px" id="description"></textarea>
<br/>
<button type="submit" onclick="javascript: window.opener.document.location.href=window.opener.document.location.href;window.close();">Create</button>
<button type="button" onclick="javascript: window.close()">Cancel</button>
</form>
</body>
</html>

