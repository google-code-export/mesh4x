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
using System.IO;

namespace WebHost.Admin
{

	public partial class Feeds : System.Web.UI.Page, IPostBackEventHandler
	{
		protected IEnumerable<FeedConfigurationEntry> FeedEntries
		{
			get
			{
				return SyncServiceConfigurationSection.GetConfigurationManager().LoadAll();
			}
		}

		protected void Page_Load(object sender, EventArgs e)
		{
		}

		protected void lnkRemoveAll_Click(object sender, EventArgs e)
		{
			IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
			IEnumerable<FeedConfigurationEntry> entries = manager.LoadAll();

			foreach (FeedConfigurationEntry entry in entries)
			{
				manager.Delete(entry.Name);
			}
		}

		public void RaisePostBackEvent(string eventArgument)
		{
			IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
			manager.Delete(eventArgument);
		}

		protected string GetFullPath(string resourceUrl)
		{
			string full = VirtualPathUtility.ToAbsolute(resourceUrl);

			return new Uri(
				new UriBuilder(Request.Url.Scheme, Request.Url.Host, Request.Url.Port, HttpRuntime.AppDomainAppVirtualPath).Uri,
				full).ToString();
		}
	}
}
