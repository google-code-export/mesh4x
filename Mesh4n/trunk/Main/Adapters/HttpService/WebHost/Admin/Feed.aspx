<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="Feed.aspx.cs" Inherits="WebHost.Admin.Feed" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>Feed details</title>
</head>
<body>
	<h1><asp:Label runat="server" ID="lblName"/> feed</h1>
    <form id="form1" runat="server">
	Title: <asp:TextBox runat="server" ID="txtTitle" Columns="75"></asp:TextBox><br />
	Description: <asp:TextBox runat="server" ID="txtDescription" Columns="75"></asp:TextBox> &nbsp;&nbsp; 
	<asp:Button runat="server" Text="Update" ID="btnUpdate" 
		onclick="btnUpdate_Click" />
    <br /><br />
    <div>
		<asp:HyperLink ID="lnkRssFeed" runat="server" NavigateUrl="../Service.svc/feeds/{0}">Rss</asp:HyperLink><br />
		<asp:HyperLink ID="lnkKmlFeed" runat="server" NavigateUrl="../Service.svc/feeds/{0}?format=kml">Kml</asp:HyperLink><br />
		<asp:HyperLink ID="lnkKmlNetworkFeed" runat="server" NavigateUrl="../Service.svc/feeds/{0}?format=kmlnet">Kml Network</asp:HyperLink><br />
    </div>
    <br />
    <asp:HyperLink runat="server" NavigateUrl="~/Admin/Feeds.aspx">Go back to the feeds list</asp:HyperLink>
    </form>
</body>
</html>
