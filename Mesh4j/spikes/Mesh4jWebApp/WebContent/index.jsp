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
			<p><b>How to get all available feeds</b>&nbsp;&nbsp;<a class="atomRssLink" href="/mesh4x/feeds?format=atom10">atom</a>&nbsp;|&nbsp;<a href="/mesh4x/feeds?format=rss20">rss</a></p>
			<div class="description">Method: GET</div>
			<div class="description">URL: /feeds?format[atom10/rss20]&nbsp;&nbsp;&nbsp;(*)</div>
		</li>
		<br>
		<li>
			<p><b>How to add a new own Feed</b></p>
			<div class="description">Method: POST</div>
			<div class="description">URL: /feeds?format[atom10/rss20]&nbsp;&nbsp;&nbsp;(*)</div>
		</li>
		<br>
		<li>
			<p><b>How to get a Feed</b></p>
			<div class="description">Method: GET</div>
			<div class="description">URL: /feeds/{feedName}?format[atom10/rss20]&nbsp;&nbsp;&nbsp;(*)</div>
		</li>
		<br>
		<li><p><b>How to synchronize a Feed</b></p>
			<div class="description">Method: POST</div>
			<div class="description">URL: /feeds/{feedName}?format[atom10/rss20]&nbsp;&nbsp;&nbsp;(*)</div>
		</li>
	</ul>
	<br>
	<p>* If the <b>format</b> is empty a rss value is assigned by default</p>
	<br>
	<hr>
	<br>
	<h2>Create you own Feed:</h2>
	<br>
	<form action="/mesh4x/feeds" method="post">
		name: <input id="newSourceID" name="newSourceID" type="text" value="myFeed">
		title: <input id="title" name="title" type="text" value="my own feed">
		description: <input id="description" name="description" type="text" value="my feed is the best feed">
		format: <select id="format" name="format"><option>rss20</option><option>atom10</option></select>
		<br>
		<br>
		<button type="submit">Add</button>
	</form>
</body>
</html>