/*******************************************************************************
* Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and conditions in the IBM International Program License Agreement (IPLA).
* © Copyright IBM Corporation 2012, 2013. All Rights Reserved. 
* U.S. Government Users Restricted Rights: Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/

function create(baseUrl){
		
	var form = document.getElementById("Create");
	xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4 && (xmlhttp.status==201)) {
			txt = xmlhttp.responseText;
			resp = eval('(' + txt + ')');
			// provide a status message.
			var oStatus = document.getElementById("status");
			if (oStatus.hasChildNodes()){    
				oStatus.removeChild(oStatus.childNodes[0]); 
			}
			oStatus.appendChild(document.createTextNode("Change Request " + resp.ID + " : " + resp.title + " has been created successfully." ));
		}
	};
 	var postData=""; 
 
	if (form.title) {
		postData += "&title="+encodeURIComponent(form.title.value);	
	}
	if (form.description) {
		postData += "&description="+encodeURIComponent(form.description.value);	
	}
	
	postData += "&filedAgainst="+encodeURIComponent(form.filedAgainst.value);
	
	xmlhttp.open("POST", baseUrl, true);
	xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlhttp.setRequestHeader("Content-length",postData.length);
	xmlhttp.send(postData);	
}

function sendCancelResponse() {
	var oslcResponse = 'oslc-response:{ "oslc:results": [ ]}';
	
	if (window.location.hash == '#oslc-core-windowName-1.0') {       
  	  // Window Name protocol in use
        respondWithWindowName(oslcResponse);
	} else if (window.location.hash == '#oslc-core-postMessage-1.0') {
    	// Post Message protocol in use
		respondWithPostMessage(oslcResponse);
	} 
}

function respondWithPostMessage(/*string*/ response) {
	if( window.parent != null ) {
		window.parent.postMessage(response, "*");
	} else {
		window.postMessage(response, "*");
	}
}

function cancel(){
	sendCancelResponse();
}

var o_CR = {};
function loadCR(baseUrl){
	var form = document.getElementById("Modify");
	xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", baseUrl, true);
	xmlhttp.setRequestHeader("Accept", "application/json");
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4 && (xmlhttp.status==200)) {
			o_CR = JSON.parse(xmlhttp.responseText);
			form.title.value = o_CR["dcterms:title"];
			form.description.value = o_CR["dcterms:description"];
			if (o_CR["dcterms:accessRights"] == "Server") {
				form.filedAgainst.selectedIndex = 0;
			} else {
				form.filedAgainst.selectedIndex = 1;
			}
		}
	}; 
	xmlhttp.send(null);
}
function modify(baseUrl){
	var form = document.getElementById("Modify");
	
	if ((o_CR["dcterms:description"] !== form.description.value) || (o_CR["dcterms:title"] !== form.title.value)
			|| (o_CR["dcterms:accessRights"] !== form.filedAgainst.value))
	{
		o_CR["dcterms:description"] = form.description.value;
		o_CR["dcterms:title"] = form.title.value;
		o_CR["dcterms:accessRights"] = form.filedAgainst.value;
		
		var jsonString = JSON.stringify(o_CR);
		xmlhttp = new XMLHttpRequest();
		xmlhttp.open("PUT", baseUrl, true);
		xmlhttp.setRequestHeader("Content-type", "application/json");
	    xmlhttp.setRequestHeader("Content-length", jsonString.length);
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState==4 && (xmlhttp.status==200)) {
				txt = xmlhttp.responseText;
				resp = eval('(' + txt + ')');
				// provide a status message.
				var oStatus = document.getElementById("status");
				if (oStatus.hasChildNodes()){    
					oStatus.removeChild(oStatus.childNodes[0]); 
				}
				oStatus.appendChild(document.createTextNode("Change Request " + resp.ID + " : " + resp.title + " has been modified successfully." ));
			}
		}; 
		xmlhttp.send(jsonString);
	}
}

function ModifyCutOff(baseUrl){
	
	var form = document.getElementById("ModifyCutOff");
	xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4 && (xmlhttp.status==200)) {
			txt = xmlhttp.responseText;
			resp = eval('(' + txt + ')');
			// provide a status message.
			var oStatus = document.getElementById("status");
			if (oStatus.hasChildNodes()){    
				oStatus.removeChild(oStatus.childNodes[0]); 
			}
			oStatus.appendChild(document.createTextNode("Cut Off Event set at " + resp.event ));
		}
	};
 	var postData=""; 
 
	if (form.event) {
		postData += "&event="+encodeURIComponent(form.event.value);	
	}
	
	xmlhttp.open("POST", baseUrl, true);
	xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlhttp.setRequestHeader("Content-length",postData.length);
	xmlhttp.send(postData);	
}

function deleteResource(resUrl, currentUrl){
	xmlhttp = new XMLHttpRequest();
	xmlhttp.open("DELETE", resUrl, true);	
	xmlhttp.send();
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4 && (xmlhttp.status==200)) {
		    var oNode = document.getElementById(resUrl);
		    
		    if (oNode.parentNode) {   
		    	oNode.parentNode.removeChild(oNode); 
		    } 			
		}
	}	
}
