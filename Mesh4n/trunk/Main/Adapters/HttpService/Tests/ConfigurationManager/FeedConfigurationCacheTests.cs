using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using Mesh4n.Adapters.HttpService.Configuration;
using System.IO;

namespace Mesh4n.Adapters.HttpService.Tests.Configuration
{
	[TestFixture]
	public class FeedConfigurationCacheTests
	{
		public FeedConfigurationCacheTests()
		{
		}

		[Test]
		public void ShouldAddAndGetEntry()
		{
			FeedConfigurationEntry entry = new FeedConfigurationEntry("foo", "foo title", "foo description", new MockSyncAdapter());
			
			FeedConfigurationCache cache = new FeedConfigurationCache();
			cache.AddEntry(Path.GetFullPath("foo.txt"), entry);

			FeedConfigurationEntry cachedEntry = cache.GetEntry("foo");

			Assert.IsNotNull(cachedEntry);
			Assert.AreEqual(entry, cachedEntry);
		}

		[Test]
		[Ignore]
		public void ShouldInvalidateEntryIfFileChanges()
		{
			string fileName = Path.GetFullPath(Guid.NewGuid().ToString() + ".txt");
			File.Create(fileName).Close();

			FeedConfigurationEntry entry = new FeedConfigurationEntry("foo", "foo title", "foo description", new MockSyncAdapter());

			FeedConfigurationCache cache = new FeedConfigurationCache();
			cache.AddEntry(fileName, entry);

			File.Delete(fileName);

			FeedConfigurationEntry cachedEntry = cache.GetEntry("foo");
			Assert.IsNull(cachedEntry);
		}

		[Test]
		public void ShouldRemoveEntry()
		{
			string fileName = Path.GetFullPath(Guid.NewGuid().ToString() + ".txt");

			FeedConfigurationEntry entry = new FeedConfigurationEntry("foo", "foo title", "foo description", new MockSyncAdapter());

			FeedConfigurationCache cache = new FeedConfigurationCache();
			cache.AddEntry(fileName, entry);

			cache.RemoveEntry(entry.Name);

			Assert.IsNull(cache.GetEntry(entry.Name));
		}

		[Test]
		public void ShouldNotFailIfRemoveTwice()
		{
			string fileName = Path.GetFullPath(Guid.NewGuid().ToString() + ".txt");

			FeedConfigurationEntry entry = new FeedConfigurationEntry("foo", "foo title", "foo description", new MockSyncAdapter());

			FeedConfigurationCache cache = new FeedConfigurationCache();
			cache.AddEntry(fileName, entry);

			cache.RemoveEntry(entry.Name);
			cache.RemoveEntry(entry.Name);
		}

	}
}
