<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="Feeds.aspx.cs" Inherits="WebHost.Admin.Feeds" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>Available Feeds</title>
</head>
<body>
    <form id="form1" runat="server">
<h1>Available Feeds</h1>
    <div>
    <asp:Repeater runat="server" ID="rptFeeds">
		<ItemTemplate>
			<li><a href="Feed.aspx?Feed=<%# DataBinder.Eval(Container.DataItem, "Name") %>">
      <%# DataBinder.Eval(Container.DataItem, "Name") %></a></li>
		</ItemTemplate>
	</asp:Repeater>
		<br /><br />
		<asp:HyperLink runat="server" NavigateUrl="~/Admin/CreateFeed.aspx">Create a new feed</asp:HyperLink><br />
		<asp:LinkButton runat="server" ID="lnkRemoveAll" Text="Remove All Feeds" 
			onclick="lnkRemoveAll_Click"></asp:LinkButton>
    </div>
    </form>
</body>
</html>
