using System.Collections.Generic;
using System.Collections.Specialized;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public interface IFeedConfigurationManager
	{
		void Initialize(NameValueCollection attributes);
		void Save(FeedConfigurationEntry entry);
		FeedConfigurationEntry Load(string feedName);
		IEnumerable<FeedConfigurationEntry> LoadAll();
	}
}
