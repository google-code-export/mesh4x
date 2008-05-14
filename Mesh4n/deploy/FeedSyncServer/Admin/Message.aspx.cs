using System;
using System.Collections;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;

namespace WebHost.Admin
{
	public partial class Message : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			string feedName = this.Request.QueryString["feed"];

			lblMessage.Text = string.Format(lblMessage.Text, feedName);
			lnkFeed.NavigateUrl = string.Format(lnkFeed.NavigateUrl, feedName);
		}
	}
}