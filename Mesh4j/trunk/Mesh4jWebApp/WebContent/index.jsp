<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Mesh4x - Sync RESTful server</title>
	<link media="screen" type="text/css" href="/mesh4x/scripts/style.css" rel="stylesheet"></link>
</head>
<body>
	<div id="header">
		<a href="http://www.instedd.org" target="_blank"><img id="logo" src="/mesh4x/images/logo_instedd.gif" border="0"/></a>
	</div>
	<br>
	<hr>
	<br>
	<h1>Welcome to mesh4x!!!</h1>
	<br>
	<hr>
	<br>
	<ul>
		<li>
			<p><b>How to get all available Mesh</b>&nbsp;&nbsp;<a class="atomRssLink" href="/mesh4x/feeds?format=atom10">atom</a>&nbsp;|&nbsp;<a href="/mesh4x/feeds?format=rss20">rss</a></p>
			<div class="description">Method: GET</div>
			<div class="description">URL: /feeds?format[atom10/rss20]&[plain]&nbsp;&nbsp;&nbsp;(*)</div>
			<br>
		</li>
		<li>
			<p><b>How to add/update a new own Mesh</b></p>
			<div class="description">Method: POST</div>
			<div class="description">URL: /feeds?format[atom10/rss20]&nbsp;&nbsp;&nbsp;(*)</div>
			<div class="description">Parameters:</div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="action" type="hidden text" value="uploadMeshDefinition"</div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="newSourceID" type="text" value="myMesh"<div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="title" type="text" value="my own mesh"</div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="description" type="text" value="my mesh is the best mesh"</dic>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="format" type=text value=rss20/atom10</div>
			<br>
		</li>
		<li>
			<p><b>How to get all available feeds for a Mesh</b></p>
			<div class="description">Method: GET</div>
			<div class="description">URL: /feeds/{meshName}?format[atom10/rss20]&[plain]&nbsp;&nbsp;&nbsp;(*)</div>
			<br>
		</li>
		<li>
			<p><b>How to add/update a new own Feed</b></p>
			<div class="description">Method: POST</div>
			<div class="description">URL: /feeds/{meshName}?format[atom10/rss20]&nbsp;&nbsp;&nbsp;(*)</div>
			<div class="description">Parameters:</div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="action" type="hidden text" value="uploadMeshDefinition"</div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="newSourceID" type="text" value="myMesh/myFeed"</div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="title" type="text" value="my own feed</div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="description" type="text" value="my feed is the best feed"</div>
			<div class="description">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name="format" type=text value=rss20/atom10</div>
			<br>
		</li>
		<li>
			<p><b>How to get a Feed</b></p>
			<div class="description">Method: GET</div>
			<div class="description">URL: /feeds/{meshName}/{feedName}?format[atom10/rss20]&[plain]&nbsp;&nbsp;&nbsp;(*)</div>
			<br>
		</li>
		<li><p><b>How to synchronize a Feed</b></p>
			<div class="description">Method: POST</div>
			<div class="description">URL: /feeds/{meshName}/{feedName}?format[atom10/rss20]&nbsp;&nbsp;&nbsp;(*)</div>
		</li>
	</ul>
	<br>
	<p>* Parameter <b>format</b> is optional, rss20 value is assigned by default</p>
	<p>* Parameter <b>plain</b> is optional, result feed not contains deleted items and sync information</p>
	<br>
	<hr>
	<br>
	<h2>Create or update you own Mesh:</h2>
	<br>
	<form action="/mesh4x/feeds" method="post">
		<input type="hidden" id="action" name="action" value="uploadMeshDefinition">
		by: <input id="by" name="by" type="text" value="user name">
		name: <input id="newSourceID" name="newSourceID" type="text" value="myMesh">
		title: <input id="title" name="title" type="text" value="my own mesh">
		description: <input id="description" name="description" type="text" value="my mesh is the best mesh">
		format: <select id="format" name="format"><option>rss20</option><option>atom10</option></select>
		<br>
		<br>
		<button type="submit">Add</button>
	</form>
	<br>
	<br>
	<h2>Create or update you own Feed:</h2>
	<br>
	<form action="/mesh4x/feeds" method="post">
		<input type="hidden" id="action" name="action" value="uploadMeshDefinition">
		by: <input id="by" name="by" type="text" value="user name">
		name: <input id="newSourceID" name="newSourceID" type="text" value="myMesh/myFeed">
		title: <input id="title" name="title" type="text" value="my own feed">
		description: <input id="description" name="description" type="text" value="my feed is the best feed">
		format: <select id="format" name="format"><option>rss20</option><option>atom10</option></select>
		<br>schema: <textarea id="schema" name="schema"></textarea>
		mappings: <textarea id="mappings" name="mappings"></textarea>
		<br>
		<br>
		<button type="submit">Add</button>
	</form>
	<br>
	<br>
	<h2>Clean you own Feed:</h2>
	<br>
	<form action="/mesh4x/feeds" method="post">
		<input type="hidden" id="action" name="action" value="clean">
		by: <input id="by" name="by" type="text" value="user name">
		name: <input id="sourceID" name="sourceID" type="text" value="myMesh/myFeed">
		<br>
		<br>
		<button type="submit">Clean</button>
	</form>
	<br>
	<br>
	<h2>Delete you own Feed:</h2>
	<br>
	<form action="/mesh4x/feeds" method="post">
		<input type="hidden" id="action" name="action" value="delete">
		by: <input id="by" name="by" type="text" value="user name">
		name: <input id="sourceID" name="sourceID" type="text" value="myMesh/myFeed">
		<br>
		<br>
		<button type="submit">Delete</button>
	</form>
</body>
</html>