using System;
using System.Collections.Generic;
using System.Text;
using System.ComponentModel;
using System.Configuration;
using Mesh4n.Adapters.HttpService.Properties;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public class FeedConfigurationEntry
	{
		private string name;
		private string title;
		private string description;
		private Type syncAdapterType;
		private ISyncAdapter syncAdapter;

		protected FeedConfigurationEntry()
		{
		}

		public FeedConfigurationEntry(string name, string title, string description, 
			Type syncAdapterType)
		{
			Guard.ArgumentNotNullOrEmptyString(name, "name");
			Guard.ArgumentNotNullOrEmptyString(title, "title");
			Guard.ArgumentNotNullOrEmptyString(description, "description");

			this.name = name;
			this.title = title;
			this.description = description;
			this.syncAdapterType = syncAdapterType;

			this.syncAdapter = this.CreateSyncAdapterInstance(this.syncAdapterType);
		}

		public FeedConfigurationEntry(string name, string title, string description,
			ISyncAdapter syncAdapter)
		{
			Guard.ArgumentNotNullOrEmptyString(name, "name");
			Guard.ArgumentNotNullOrEmptyString(title, "title");
			Guard.ArgumentNotNullOrEmptyString(description, "description");

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
		
		[TypeConverter(typeof(TypeNameConverter))]
		public Type SyncAdapterType
		{
			get { return syncAdapterType; }
			set { syncAdapterType = value; }
		}

		public ISyncAdapter SyncAdapter
		{
			get { return syncAdapter; }
			protected set { syncAdapter = value; }
		}

		protected ISyncAdapter CreateSyncAdapterInstance(Type type)
		{
			object adapter = Activator.CreateInstance(type);
			if (!(adapter is ISyncAdapter))
				throw new ArgumentException(string.Format(Resources.InvalidSyncAdapterType,
					type.AssemblyQualifiedName));

			return (ISyncAdapter)adapter;
		}
	}
}
