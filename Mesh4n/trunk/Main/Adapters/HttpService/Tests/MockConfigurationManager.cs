using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Mesh4n.Adapters.HttpService.Configuration;

namespace Mesh4n.Adapters.HttpService.Tests
{
	class MockConfigurationManager : IFeedConfigurationManager
	{
		public FeedConfiguration FeedConfiguration = new FeedConfiguration();

		#region IFeedConfigurationManager Members

		public void Initialize(System.Collections.Specialized.NameValueCollection attributes)
		{
		}

		public void Save(FeedConfiguration configuration)
		{
		}

		public FeedConfiguration Load()
		{
			return this.FeedConfiguration;
		}

		#endregion
	}
}
