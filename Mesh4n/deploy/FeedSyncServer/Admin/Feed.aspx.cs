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
using Mesh4n.Adapters.HttpService.Configuration;
using Mesh4n.Adapters.HttpService;

namespace WebHost.Admin
{
	public partial class Feed : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			if (!Page.IsPostBack)
			{
				string feedName = Request.QueryString["feed"];
				ViewState["FeedName"] = feedName;

				IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
				FeedConfigurationEntry entry = manager.Load(feedName);

				lblName.Text = entry.Name;
				txtTitle.Text = entry.Title;
				txtDescription.Text = entry.Description;

				lnkRssFeed.NavigateUrl = string.Format(lnkRssFeed.NavigateUrl, feedName);
				lnkKmlFeed.NavigateUrl = string.Format(lnkKmlFeed.NavigateUrl, feedName);
				lnkKmlNetworkFeed.NavigateUrl = string.Format(lnkKmlNetworkFeed.NavigateUrl, feedName);
			}
		}

		protected void btnUpdate_Click(object sender, EventArgs e)
		{
			IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
			FeedConfigurationEntry entry = manager.Load((string)ViewState["FeedName"]);

			entry.Title = txtTitle.Text;
			entry.Description = txtDescription.Text;

			manager.Save(entry);
		}
	}
}