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
using System.Collections.Generic;

namespace WebHost.Admin
{

	public partial class Feeds : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			if (!Page.IsPostBack)
			{
				BindEntriesToRepeater();
			}
		}

		private void BindEntriesToRepeater()
		{
			IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
			IEnumerable<FeedConfigurationEntry> entries = manager.LoadAll();

			this.rptFeeds.DataSource = entries;
			this.rptFeeds.DataBind();
		}

		protected void lnkRemoveAll_Click(object sender, EventArgs e)
		{
			IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
			IEnumerable<FeedConfigurationEntry> entries = manager.LoadAll();

			foreach (FeedConfigurationEntry entry in entries)
			{
				manager.Delete(entry.Name);
			}

			BindEntriesToRepeater();
		}
	}
}
