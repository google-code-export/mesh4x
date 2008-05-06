using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Activation;
using Mesh4n.Adapters.HttpService.Configuration;
using System.ServiceModel;
using System.ServiceModel.Channels;

namespace Mesh4n.Adapters.HttpService
{
	[AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
	public class SyncService : ISyncService
	{
		IFeedConfigurationManager configurationManager;

		public SyncService()
		{
			this.configurationManager = SyncServiceConfigurationSection.GetConfigurationManager();
		}

		public SyncService(IFeedConfigurationManager configurationManager)
		{
			this.configurationManager = configurationManager;
		}

		public Message GetFeeds()
		{
			FeedConfiguration configuration = configurationManager.Load();

			List<Item> items = new List<Item>();
			foreach (FeedConfigurationEntry entry in configuration.Values)
			{
				Item item = new Item(new XmlItem(entry.Title, entry.Description, null), null);
				items.Add(item);
			}

			Feed feed = new Feed("FeedSync Feeds", "/feeds", "List of available feedsync feeds");
			return Message.CreateMessage(MessageVersion.None, "", new FeedBodyWriter(feed));
		}

		public Message GetFeed(string name)
		{
			FeedConfiguration configuration = configurationManager.Load();
			if (!configuration.ContainsKey(name))
			{
				throw new InvalidOperationException(
					string.Format("The feed {0} is not configured in the system.", name));
			}

			FeedConfigurationEntry entry = configuration[name];
			
			IEnumerable<Item> items = entry.SyncAdapter.GetAll();
			
			Feed feed = new Feed(entry.Title, "/feeds/" + name, entry.Description);
			return Message.CreateMessage(MessageVersion.None, "", new FeedBodyWriter(feed, items));
		}
	}
}
