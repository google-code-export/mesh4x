using System;
using System.Collections.Generic;
using System.Text;
using System.Collections.Specialized;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public interface IFeedConfigurationManager
	{
		void Initialize(NameValueCollection attributes);
		void Save(FeedConfiguration configuration);
		FeedConfiguration Load();
	}
}
