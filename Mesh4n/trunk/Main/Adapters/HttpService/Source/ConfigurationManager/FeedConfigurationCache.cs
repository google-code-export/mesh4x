using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Caching;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public interface IFeedConfigurationCache
	{
		void AddEntry(string file, FeedConfigurationEntry entry);
		FeedConfigurationEntry GetEntry(string feedName);
	}

	internal class FeedConfigurationCache : IFeedConfigurationCache
	{
		public void AddEntry(string file, FeedConfigurationEntry entry)
		{
			HttpRuntime.Cache.Add(entry.Name, entry,
				new CacheDependency(file), Cache.NoAbsoluteExpiration,
				TimeSpan.FromMinutes(30),
				CacheItemPriority.Normal,
				null);
		}

		public FeedConfigurationEntry GetEntry(string feedName)
		{
			return (FeedConfigurationEntry)HttpRuntime.Cache[feedName];
		}
	}
}
