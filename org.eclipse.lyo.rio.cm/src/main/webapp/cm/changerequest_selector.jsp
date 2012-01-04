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
<script type="text/javascript">
function search(){
	var ie = window.navigator.userAgent.indexOf("MSIE");
	list = document.getElementById("results");
	list.options.length = 0;
	xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			// populate results
			txt = xmlhttp.responseText;
			resp = eval('(' + txt + ')');
			for( i=0; i<resp.results.length; i=i+1 ) {
				var item=document.createElement('option');
				item.text = resp.results[i].title;
				item.value = resp.results[i].resource;
				if (ie > 0) {
	 				list.add(item); 
				} else {
	 				list.add(item, null); 
				}
			}
		}
	}
	//xmlhttp.setRequestHeader("Accept","application/json");
	terms = document.getElementById("searchTerms").value;
	xmlhttp.open("GET","<%=uriBase%>/selector/changerequest?terms=" + escape(terms),true);
	xmlhttp.send();
}

function select(){
	list = document.getElementById("results");
	if( list.length>0 && list.selectedIndex >= 0 ) {
		option = list.options[list.selectedIndex];

		var oslcResponse = 'oslc-response:{ "oslc:results": [ ' +  
			' { "oslc:label":"' + option.text + '", "rdf:resource":"' + option.value + '"} ' + 
		' ]}';
		
		if (window.location.hash == '#oslc-core-windowName-1.0') {       
      	  // Window Name protocol in use
	        respondWithWindowName(oslcResponse);
		} else if (window.location.hash == '#oslc-core-postMessage-1.0') {
        	// Post Message protocol in use
			respondWithPostMessage(oslcResponse);
		} 
	}
}

function respondWithWindowName(/*string*/ response) {
   // Step #2: read the return URL
   var returnURL = window.name;

   // Step #4: send the response via the window.name variable
   window.name = response;

   // Step #5: indicate that user has responded
   window.location.href = returnURL;
   
}

function respondWithPostMessage(/*string*/ response) {
	if( window.parent != null ) {
		window.parent.postMessage(response, "*");
	} else {
		window.postMessage(response, "*");
	}
}

function cancel(){
	window.parent.close();
}
</script>
<link rel="SHORTCUT ICON" href="../oslc.png">
</head>
<body>
<input type="text" style="width: 200px" id="searchTerms" />
<button type="button" onclick="search()">Search</button>
<br/><b>Change Requests</b><br/>
<table>
<tr><td><select id="results" size="10" style="width: 300px"></select></td></tr>
<tr><td><button type="button" onclick="javascript: select();">OK</button>
<button type="button" onclick="javascript: cancel()">Cancel</button></td></tr>
</table>
</body>
</html>