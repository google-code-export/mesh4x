using System;
using System.Data;
using System.Configuration;
using System.Collections;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Web.UI.HtmlControls;
using SimpleSharing;
using CustomerLibrary;
using System.Data.Common;
using Microsoft.Practices.EnterpriseLibrary.Data;

namespace CustomerSite
{
	public partial class _Default : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			customerSource.ObjectCreating += customerSource_ObjectCreating;
		}

		void customerSource_ObjectCreating(object sender, ObjectDataSourceEventArgs e)
		{
			CustomerDataAccess dac = new CustomerDataAccess(
				DatabaseFactory.CreateDatabase("CustomerDB"));
			e.ObjectInstance = dac;
		}
	}
}
