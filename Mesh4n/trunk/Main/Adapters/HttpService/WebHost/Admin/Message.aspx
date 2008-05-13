<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Message.aspx.cs" Inherits="Admin_Message" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>Feed created successfully</title>
</head>
<body>
    <form id="form1" runat="server">
    <div>
		<asp:Label runat="server" ID="lblMessage" Text="The feed {0} was created successfully."></asp:Label> <br /><br />
		<asp:HyperLink runat="server" ID="lnkFeed" NavigateUrl="../Service.svc/feeds/{0}">Navigate to the feed</asp:HyperLink>  
    </div>
    </form>
</body>
</html>
