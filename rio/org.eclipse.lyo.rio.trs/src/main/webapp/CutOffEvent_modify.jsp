<!DOCTYPE html>

<%--
 Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and conditions in the IBM International Program License Agreement (IPLA).
 © Copyright IBM Corporation 2012. All Rights Reserved. 
 U.S. Government Users Restricted Rights: Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 --%>
 
 <%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
 <%
 	String modifyCutOffUri = request.getContextPath() + "/rest/trs/ModifyCutOff";
 	String scriptPath = (String) request.getContextPath()+"/TestApp.js";
 %>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>Reference App OSLC Adapter: Modify CutOff Event</title>
 <script type="text/javascript" src='<%= scriptPath %>'></script>
</head>
<body>
<body style="padding: 10px;">
	<div id="changereq-body">
    	<form id="ModifyCutOff" method="POST" class="enter_CutOff_form">
			<table style="clear: both;">

				<tr>
					<th class="field_label required">CutOff Event:</th>
					<td><input name="CutOffEvent" class="required text_input"
						type="text" style="width: 400px" id="event" required autofocus></td>
				</tr>
				
				<tr>
					<td></td>
					<td>
						<input type="button"
							value="Modify CutOff Event"
							onclick="javascript: ModifyCutOff( '<%= modifyCutOffUri %>' )">
						<input type="button" value="Cancel" onclick="javascript: cancel()">
					</td>
				</tr>
						
			
			</table>
		</form>
		<div id="hint">Provide URL(rdf:about) of the Change Event from <a href="${pageContext.request.contextPath}/rest/trs">Change Log</a> to be set as new Cutoff event. </div>	
	<div style="clear: both;">
	<p></p>
	</div>
	<div id="status"></div>	

	</div>
</body>
</html>