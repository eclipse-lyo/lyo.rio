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
			loadBugs();
		}
	});
	request.fail(function() {
		$('#message').text('Error creating bug: ' + bug.title + '. Stopping.');
		loadBugs();
	});
}

function createSampleBugs() {
	createNextSample(0);
}

function showDialog() {
	$('<iframe/>', {
		src: 'newBug.html'
	}).css({
		border: 0,
		width: '450px',
		height: '395px'
	}).appendTo('#dialogContainer');
	$('.dialog').fadeIn('fast');
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

	var offset = link.offset();
	var previewDiv = $('<div class="preview"/>').css({
			top: offset.top + 30 + "px",
			left: offset.left + 10 + "px",
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
	previewDiv.appendTo('body');

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
				console.log('faded out done');
				preview.fadeIn('fast');
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
	$('#message').text('Bug created!');
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
		$('#message').text('Bug created!');
	}

	loadBugs();

	// Remove the dialog from the page.
	$('.dialog').fadeOut('fast', function() {
		$('#dialogContainer').empty();
	});
}, false);

function loadBugs() {
	var request = $.ajax('r/bugs', {
		headers: {
			Accept: 'text/turtle',
			Prefer: 'return=representation; include="http://www.w3.org/ns/ldp#PreferContainment http://open-services.net/ns/core#PreferDialog"'
		}
	});

	request.done(function(data) {
		$('#container').text(data);
	});
}

$(document).ready(function() {
	$('#openBug').on('click', showDialog);
	$('#createSample').on('click', createSampleBugs);
	loadBugs();
});
