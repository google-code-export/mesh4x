using System;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public class FeedConfigurationEntry
	{
		private string name;
		private string title;
		private string description;
		private ISyncAdapter syncAdapter;

		protected FeedConfigurationEntry()
		{
		}

		public FeedConfigurationEntry(string name, string title, string description,
			ISyncAdapter syncAdapter)
		{
			Guard.ArgumentNotNullOrEmptyString(name, "name");
			Guard.ArgumentNotNullOrEmptyString(title, "title");
			Guard.ArgumentNotNullOrEmptyString(description, "description");
			Guard.ArgumentNotNull(syncAdapter, "syncAdapter");
			
			this.name = name;
			this.title = title;
			this.description = description;
			this.syncAdapter = syncAdapter;
		}

		public string Name
		{
			get { return name; }
			set { name = value; }
		}

		public string Title
		{
			get { return title; }
			set { title = value; }
		}

		public string Description
		{
			get { return description; }
			set { description = value; }
		}
		
		public ISyncAdapter SyncAdapter
		{
			get { return syncAdapter; }
			set { syncAdapter = value; }
		}
	}
}
