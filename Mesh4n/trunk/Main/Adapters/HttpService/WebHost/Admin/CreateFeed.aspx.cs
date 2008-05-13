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
using Mesh4n.Adapters.HttpService;
using Mesh4n.Adapters.HttpService.Configuration;
using Mesh4n.Adapters.Data;

public partial class Admin_CreateFeed : System.Web.UI.Page
{
	protected void Page_Load(object sender, EventArgs e)
	{

	}
	protected void btnSubmit_Click(object sender, EventArgs e)
	{
		SqlDbFactory factory = new SqlDbFactory();
		factory.ConnectionString = ConfigurationManager.ConnectionStrings["SyncAdapters"].ConnectionString;

		GenericSyncAdapter adapter = new GenericSyncAdapter(factory, txtFeedName.Text);
		
		XamlFeedConfigurationEntry entry = new XamlFeedConfigurationEntry(
			txtFeedName.Text, txtFeedTitle.Text, txtFeedDescription.Text, adapter);
		
		IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
		manager.Save(entry);

		Response.Redirect("Message.aspx?feed=" + txtFeedName.Text, false);
	}
}
