using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Mesh4n.Adapters.HttpService.Configuration;
using System.ServiceModel.Channels;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestClass]
	public class SyncServiceTests
	{
		public SyncServiceTests()
		{
		}

		[TestMethod]
		public void ShouldGetFeeds()
		{
			MockConfigurationManager manager = new MockConfigurationManager();
			manager.FeedConfiguration.Add("entry1", 
				new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", new MockSyncAdapter()));
			
			SyncService syncService = new SyncService(manager);
			Message message = syncService.GetFeeds();

			Assert.IsNotNull(message);
 		}

		[TestMethod]
		public void ShouldGetFeed()
		{
			MockConfigurationManager manager = new MockConfigurationManager();
			manager.FeedConfiguration.Add("entry1",
				new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", new MockSyncAdapter()));

			SyncService syncService = new SyncService(manager);
			Message message = syncService.GetFeed("entry1");

			Assert.IsNotNull(message);
		}

		[TestMethod]
		[ExpectedException(typeof(InvalidOperationException))]
		public void ShouldThrowIfInvalidFeed()
		{
			MockConfigurationManager manager = new MockConfigurationManager();
			
			SyncService syncService = new SyncService(manager);
			Message message = syncService.GetFeed("entry1");
		}
	}
}
