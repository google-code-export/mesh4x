<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="Feeds.aspx.cs" Inherits="WebHost.Admin.Feeds" %>
<%@ Import Namespace="Mesh4n.Adapters.HttpService.Configuration" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>Available Feeds</title>
</head>
<body>
    <form id="form1" runat="server">
<h1>Available Feeds</h1>
    <div>
    <ul>
		<% foreach (FeedConfigurationEntry entry in this.FeedEntries)
		{ %>
			<li><a href="Feed.aspx?Feed=<%= entry.Name %>"><%= entry.Name %></a>&nbsp;&nbsp;
		(<a href="../Service.svc/feeds/<%= entry.Name %>">rss</a>,&nbsp;
		<a href="http://maps.google.com?q=<%= GetFullPath("~/Service.svc/feeds/" + entry.Name) %>?format=kml">map</a>,&nbsp
		<a href="../Service.svc/feeds/<%= entry.Name %>?format=kml">kml</a>,&nbsp
		<a href="../Service.svc/feeds/<%= entry.Name %>?format=kmlnet">auto-updating kml</a>)&nbsp;
		<a href="<%= Page.ClientScript.GetPostBackClientHyperlink(this, entry.Name) %>" style="color: Gray" title="Delete this feed?">[x]</a>
		</li>
		<% } %>
    </ul>
		<br /><br />
		<asp:HyperLink runat="server" NavigateUrl="~/Admin/CreateFeed.aspx">Create a new feed</asp:HyperLink><br />
		<asp:LinkButton runat="server" ID="lnkRemoveAll" Text="Delete All Feeds" 
			onclick="lnkRemoveAll_Click"></asp:LinkButton>
    </div>
    </form>
</body>
</html>
