using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public class XamlFeedConfigurationEntry : FeedConfigurationEntry, ISupportInitialize
	{
		public XamlFeedConfigurationEntry()
			: base()
		{
		}

		public XamlFeedConfigurationEntry(string name, string title, string description, 
			Type syncAdapterType)
			:base(name, title, description, syncAdapterType)
		{
		}

		#region ISupportInitialize Members

		public void BeginInit()
		{
		}

		public void EndInit()
		{
			Guard.ArgumentNotNullOrEmptyString(this.Name, "Name");
			Guard.ArgumentNotNullOrEmptyString(this.Title, "Title");
			Guard.ArgumentNotNullOrEmptyString(this.Description, "Description");

			base.SyncAdapter = CreateSyncAdapterInstance(this.SyncAdapterType);
		}

		#endregion
	}
}
