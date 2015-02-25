/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *     Samuel Padgett       - initial API and implementation
 *******************************************************************************/

// JSON-LD context for creating bugs
var context = {
	oslc_cm: "http://open-services.net/ns/cm#",
	dcterms: "http://purl.org/dc/terms/",
	Defect: "oslc_cm:Defect",
	description: "dcterms:description",
	severity: {
		"@id": "oslc_cm:severity",
		"@type": "@id"
	},
	title: "dcterms:title"
};

// Some sample defects to create.
var sampleBugs = [{
	title: "Product Z is too blue.",
	severity: "oslc_cm:Normal",
	description: "Let's use some other colors, OK?"
}, {
	title: "Product Z isn't blue enough.",
	severity: "oslc_cm:Normal",
	description: "I thought we wanted the UI to look really blue? What happened?"
}, {
	title: "Product Z crashes on startup",
	severity: "oslc_cm:Blocker",
	description: "I'm completed blocked! We need a fix ASAP."
}, {
	title: "Typo on login page",
	severity: "oslc_cm:Minor",
	description: "User is spelled 'luser'. I'm going to assume this is a mistake."
}];

function createBug(bug) {
	// Post the form as JSON-LD to the bug container.
	var content = $.extend({
		"@id": "",
		"@type": "Defect",
		"@context": context
	}, bug);

	return $.ajax({
		url: 'r/bugs',
		data: JSON.stringify(content),
		type: 'post',
		contentType: 'application/ld+json'
	});
}

function createNextSample(i) {
	var bug = sampleBugs[i];
	var request = createBug(bug);
	request.done(function(data, status, xhr) {
		// Add the link.
		var location = xhr.getResponseHeader('Location');
		if (location) {
			addLink(location, bug.title);
		} else {
			$('#message').text('Error creating bug: ' + bug.title + '. Stopping.');
			return;
		}

		// Create the next bug.
		i++;
		if (i < sampleBugs.length) {
			createNextSample(i);
		} else {
			$('#message').text(sampleBugs.length + ' sample bugs created!');
		}
	});
	request.fail(function() {
		$('#message').text('Error creating bug: ' + bug.title + '. Stopping.');
	});
}

function createSampleBugs() {
	createNextSample(0);
}

function showDialog() {
	var dialog = $('.dialog');
	// Do nothing if it's already showing.
	if (dialog.is(':hidden')) {
		$('<iframe/>', {
			src: 'newBug.html'
		}).css({
			border: 0,
			width: '450px',
			height: '395px'
		}).appendTo('#dialogContainer');
		dialog.fadeIn('fast');
	}
}

function getCompact(uri) {
	return $.ajax(uri, {
		headers: {
			Accept: 'application/json',
			Prefer: 'return=representation; include="http://open-services.net/ns/core#PreferCompact"'
		}
	});
}

function createIcon(src) {
	return $('<img/>', {
		'class': 'icon',
	   	src: src
   	});
}

function createPreview(link, compact) {
	var preview = compact.smallPreview || compact.largePreview;
	if (!preview) {
		return null;
	}

	// Position the preview to the right of the link with some padding.
	var offset = link.offset();
	var previewDiv = $('<div class="preview"/>').css({
		left: offset.left + link.width() + 25 + 'px',
		display: 'none'
	});

	if (compact.title) {
		var titleDiv = $('<div class="previewTitle"/>');
		if (compact.icon) {
			// Add an icon before the link.
			createIcon(compact.icon).appendTo(titleDiv);
		}

		// FIXME: markup?
		$('<span/>').text(compact.title).appendTo(titleDiv);
		previewDiv.append(titleDiv);
	}

	var document = preview.document;
	var width = preview.hintWidth || '400px';
	var height = preview.hintHeight || '300px';
	$('<iframe/>', {
			src: document
	}).css({
			width: width,
			height: height,
			border: 0
	}).appendTo(previewDiv);
	link.after(previewDiv);

	return previewDiv;
}

function updateLink(link, compact) {
	if (compact.title) {
		// FIXME: markup?
		link.text(compact.title);
	}

	if (compact.icon) {
		// Add an icon before the link.
		createIcon(compact.icon).insertBefore(link);
	}
}

function showOnHover(link, preview) {
	if (!preview) {
		return;
	}

	// Track whether the user moved the mouse into the preview itself.
	var mouseInsidePreview = false;
	var cancel = false;

	// Show the preview on hover.
	link.hover(function() {
		cancel = false;
		// Don't show preview until the mouse lingers just a bit.
		// cancel will be true if the mouse leaves the area before
		// the setTimeout callback.
		setTimeout(function() {
			if (cancel) {
				return;
			}

			// Hide any other previews and show this one.
			$('.preview').fadeOut('fast').promise().done(function() {
				if (preview.is(':hidden')) preview.fadeIn('fast');
			});
		}, 350);
	}, function() {
		cancel = true;

		// User is no longer hovering over the link.
		// Allow the user to move the mouse into the preview without it
		// disappearing.
		setTimeout(function() {
			if (!mouseInsidePreview) {
				preview.fadeOut('fast');
			}
		}, 500);
	});

	preview.hover(function() {
		mouseInsidePreview = true;
	}, function() {
		// Hide the preview when the user mouses out of the
		// preview node.
		mouseInsidePreview = false;
		preview.fadeOut('fast');
	});
}

function setupPreview(link, uri) {
	// Request the Compact resource.
	getCompact(uri).done(function(data) {
		if (!data.compact) {
			return;
		}

		// Update the icon and title of the link.
		updateLink(link, data.compact);

		// Create the preview.
		var preview = createPreview(link, data.compact);
		showOnHover(link, preview);
	});
}

function addLink(uri, label) {
	var div = $('<div/>');
	var link = $('<a/>', {
		href: uri
	}).text(label || uri).appendTo(div);
	$('#bugs').append(div);
	setupPreview(link, uri);
}

window.addEventListener("message", function(event) {
	var sameOrigin =
		location.protocol + '//' + location.hostname +
		(location.port ? ':' + location.port : '');
	if (event.origin !== sameOrigin) {
		return;
	}

	// Make sure the message starts with oslc-response:
	var message = event.data;
	if (message.indexOf("oslc-response:") !== 0) {
		return;
	}

	// Handle each result.
	var response = JSON.parse(message.substr("oslc-response:".length));
	var results = response["oslc:results"];
	for (var i = 0; i < results.length; i++) {
		var label = results[i]["oslc:label"];
		var uri = results[i]["rdf:resource"];
		addLink(uri, label);
	}

	// Remove the dialog from the page.
	$('.dialog').fadeOut('fast', function() {
		$('#dialogContainer').empty();
	});
}, false);

function loadBugs() {
	var query =
		'PREFIX dcterms: <http://purl.org/dc/terms/> \
		 PREFIX oslc_cm: <http://open-services.net/ns/cm#> \
		 SELECT * WHERE { \
			 ?defect a oslc_cm:Defect ;\
			   dcterms:title ?title . \
		 } LIMIT 50';

	var request = $.ajax('r/bugs/sparql', {
		headers: {
			Accept: 'application/sparql-results+json',
			'Content-Type': 'application/sparql-query'
		},
		data: query,
		type: 'post',
	});

	request.done(function(data) {
		if (!data.results.bindings.length) {
			// No bugs. Offer to create some.
			$('<span>No bugs here. </span>').appendTo('#message');
			$('<a/>', {
				href: '#'
			}).text('Create some?').
				on('click', createSampleBugs).
				appendTo('#message');
		} else {
			$.each(data.results.bindings, function(i, result) {
				var uri = result.defect.value;
				var label = result.title.value;
				addLink(uri, label);
			});
		}
	});
}

$(document).ready(function() {
	$('#openBug').on('click', function() {
		$('#message').empty();
		showDialog();
	});
	$('#createSample').on('click', function() {
		$('#message').empty();
		createSampleBugs();
	});
	loadBugs();
});
