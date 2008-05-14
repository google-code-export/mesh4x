<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="CreateFeed.aspx.cs" Inherits="WebHost.Admin.CreateFeed" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>Create a new feed</title>
</head>
<body>
	<h1>Create a new feed</h1>
    <form id="form1" runat="server">
    <div>
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<asp:Label runat="server" ID="lblFeedName" Text="Feed Name:"></asp:Label>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
					<asp:TextBox runat="server" ID="txtFeedName" Columns="50"></asp:TextBox>
				</td>
			</tr>
			<tr>
				<td>
					<asp:Label runat="server" ID="lblFeedTitle" Text="Feed Title:"></asp:Label>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
					<asp:TextBox runat="server" ID="txtFeedTitle" Columns="75"></asp:TextBox>
				</td>
			</tr>
			<tr>
				<td>
					<asp:Label runat="server" ID="lblFeedDescription" Text="Feed Description:"></asp:Label>
				</td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
					<asp:TextBox runat="server" ID="txtFeedDescription" Columns="75"></asp:TextBox>
				</td>
			</tr>
			<tr>
				<td colspan="3"><br /></td>
			</tr>
			<tr>
				<td colspan="3">
					<asp:RequiredFieldValidator runat="server" ID="rfvName" ControlToValidate="txtFeedName" Display="Dynamic" ErrorMessage="The feed name is required<br/>" ></asp:RequiredFieldValidator>
					<asp:RequiredFieldValidator runat="server" ID="rfvTitle" ControlToValidate="txtFeedTitle" Display="Dynamic" ErrorMessage="The feed title is required<br/>" ></asp:RequiredFieldValidator>
					<asp:RequiredFieldValidator runat="server" ID="rfvDescription" ControlToValidate="txtFeedDescription" Display="Dynamic" ErrorMessage="The feed description is required<br/>" ></asp:RequiredFieldValidator>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<asp:Button runat="server" ID="btnSubmit" Text="Create Feed" 
						onclick="btnSubmit_Click" style="height: 26px" />
				</td>
			</tr>
		</table>
    </div>
    <br /><asp:HyperLink ID="HyperLink1" runat="server" NavigateUrl="~/Admin/Feeds.aspx">Go back to the feeds list</asp:HyperLink>
    </form>
</body>
</html>
