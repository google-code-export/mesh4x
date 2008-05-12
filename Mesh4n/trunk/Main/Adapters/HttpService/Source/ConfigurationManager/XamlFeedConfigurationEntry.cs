using System;
using System.ComponentModel;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public class XamlFeedConfigurationEntry : FeedConfigurationEntry, ISupportInitialize
	{
		public XamlFeedConfigurationEntry()
			: base()
		{
		}

		public XamlFeedConfigurationEntry(string name, string title, string description, ISyncAdapter syncAdapter)
			:base(name, title, description, syncAdapter)
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
			Guard.ArgumentNotNull(this.SyncAdapter, "SyncAdapter");
		}

		#endregion
	}
}
