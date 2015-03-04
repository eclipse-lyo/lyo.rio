<!DOCTYPE html>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!--
  Copyright (c) 2015 IBM Corporation.

   All rights reserved. This program and the accompanying materials
   are made available under the terms of the Eclipse Public License v1.0
   and Eclipse Distribution License v. 1.0 which accompanies this distribution.

   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
   and the Eclipse Distribution License is available at
   http://www.eclipse.org/org/documents/edl-v10.php.

   Contributors:

      Samuel Padgett - initial API and implementation
-->

<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=no'>
    <title>SPARQL Endpoint - OSLC 3.0 Reference Implementation</title>
    <link rel="stylesheet" type="text/css" href="${baseURI}/style/common.css">
    <link rel="shortcut icon" href="${baseURI}/oslc-16x16.png">
    <style type="text/css">
        textarea {
	    display: block;
	    font-family: monospace;
	    font-size: 12px;
	    height: 200px;
	    margin: 10px 0;
	    width: 500px;
        }

	form {
	    margin-bottom: 15px;
	}

	table, th, td {
	    border-collapse: collapse;
	    border: 1px solid black;
	    padding: 5px;
	}

	#result {
	    margin-top: 10px;
	}

	.hint {
	    color: #777;
	}

	@media only screen and (max-device-width: 760px) {
	    textarea {
		-moz-box-sizing: border-box;
		-webkit-box-sizing: border-box;
		box-sizing: border-box;
		font-size: 14px;
		height: 150px;
		width: 95%;
	    }
	}
    </style>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script type="text/javascript">
	function displayTable(data) {
	    var table = $('<table/>');
	    var thead = $('<thead/>').appendTo(table);
	    var tr = $('<tr/>').appendTo(thead);
	    var vars = [];
	    $.each(data.head.vars, function(i, v) {
		vars.push(v);
		$('<th/>').text(v).appendTo(thead);
	    });

	    var tbody = $('<tbody/>').appendTo(table);
	    $.each(data.results.bindings, function(i, binding) {
		var tr = $('<tr/>').appendTo(tbody);
		$.each(vars, function(i, v) {
		    var td = $('<td/>');
		    var col = binding[v];
		    if (col.type === 'uri') {
			$('<a/>', {
				href: col.value
			}).text(col.value).appendTo(td);
		    } else {
			td.text(col.value);
		    }
		    tr.append(td);
		});
	    });

	    $('#result').empty().append(table);
	}

	function exec(query) {
	    var request = $.ajax('${endpoint}', {
		data: query,
		type: 'post',
		headers: {
		    Accept: 'application/sparql-results+json',
		    'Content-Type': 'application/sparql-query'
		},
		cache: false
	    });

	    request.done(displayTable);
            request.fail(function() {
                alert('Error running query.');
            });
	}

	$(function() {
	    $("form").submit(function(e) {
                var query = $("#query").val();
		exec(query);
		e.preventDefault();
	    });
	});
    </script>
</head>

<body>
<img class="logo" src="${baseURI}/oslc-192x192.png">
<h1>SPARQL Endpoint</h1>

<form>
<label for="query">Query:</label>
<textarea id="query" autofocus required>PREFIX dcterms: &lt;http://purl.org/dc/terms/&gt;
PREFIX oslc_cm: &lt;http://open-services.net/ns/cm#&gt;
SELECT *
WHERE {
    ?defect a oslc_cm:Defect ;
        dcterms:title ?title .
} LIMIT 10
</textarea>
<input type="submit" name="Run Query"></input>
</form>

<label for="result">Result:</label>
<div id="result"><span class="hint">Click submit to run a query</span></div>

</body>
</html>
