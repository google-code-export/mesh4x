using System;
using System.Collections.Generic;
using System.Text;
using System.ComponentModel;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public class FeedConfigurationEntry : ISupportInitialize
	{
		private string name;
		private string title;
		private string description;
		private ISyncAdapter syncAdapter;
		private string syncAdapterType;

		public FeedConfigurationEntry()
		{
		}

		public FeedConfigurationEntry(string name, string title, string description, 
			ISyncAdapter syncAdapter)
		{
			this.name = name;
			this.title = title;
			this.description = description;
			this.syncAdapter = syncAdapter;

			Validate();
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

		public string SyncAdapterType
		{
			get { return syncAdapterType; }
			set { syncAdapterType = value; }
		}

		public ISyncAdapter SyncAdapter
		{
			get { return syncAdapter; }
		}

		protected virtual void Validate()
		{
			Guard.ArgumentNotNullOrEmptyString(name, "Name");
			Guard.ArgumentNotNullOrEmptyString(title, "Title");
			Guard.ArgumentNotNullOrEmptyString(description, "Description");
			Guard.ArgumentNotNull(syncAdapter, "SyncAdapter");
		}

		public void BeginInit()
		{
		}

		public void EndInit()
		{
			Type type = Type.GetType(this.SyncAdapterType, true, true);
			this.syncAdapter = (ISyncAdapter)Activator.CreateInstance(type);

			Validate();
		}
	}
}
