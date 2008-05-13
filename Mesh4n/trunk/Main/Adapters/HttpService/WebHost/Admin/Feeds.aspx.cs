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
using System.Xml.Linq;

public partial class Admin_Feeds : System.Web.UI.Page
{
	protected void Page_Load(object sender, EventArgs e)
	{
		string feedName = Request.QueryString["feed"];

		lnkRssFeed.NavigateUrl = string.Format(lnkRssFeed.NavigateUrl, feedName);
		lnkKmlFeed.NavigateUrl = string.Format(lnkKmlFeed.NavigateUrl, feedName);
		lnkKmlNetworkFeed.NavigateUrl = string.Format(lnkKmlNetworkFeed.NavigateUrl, feedName);
	}
}
