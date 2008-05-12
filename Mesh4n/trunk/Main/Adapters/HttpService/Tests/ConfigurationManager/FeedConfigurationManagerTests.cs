using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using Mesh4n.Adapters.HttpService.Configuration;
using System.Collections.Specialized;
using System.Configuration;
using Mesh4n.Tests;
using NUnit.Framework;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestFixture]
	public class FeedConfigurationManagerTests : TestFixtureBase
	{
		const string FeedsFolder = "feeds";

		[TestFixtureSetUp]
		public void TestInitialize()
		{
			if (Directory.Exists(FeedsFolder))
				Directory.Delete(FeedsFolder, true);
		}
		
		[Test]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ShouldThrowIfPathIsNull()
		{
			NameValueCollection attributes = new NameValueCollection();
			new FeedConfigurationManager().Initialize(attributes);
		}

		[Test]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ShouldThrowIfPathIsEmpty()
		{
			NameValueCollection attributes = new NameValueCollection();
			attributes.Add("configurationPath", string.Empty);
			new FeedConfigurationManager().Initialize(attributes);
		}

		[Test]
		public void ShouldInitializeWithAbsolutePath()
		{
			FeedConfigurationManager manager = GetManagerInstance(FeedsFolder);
			Assert.AreEqual(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, FeedsFolder), manager.ConfigurationPath);
		}

		[Test]
		public void ShouldGetAllEntries()
		{
			FeedConfigurationManager manager = GetManagerInstance("c:\\feeds");

			FeedConfigurationEntry entry1 = new XamlFeedConfigurationEntry("entry1",
				"title 1", "description 1", new MockRepository());

			FeedConfigurationEntry entry2 = new XamlFeedConfigurationEntry("entry2",
				"title 2", "description 2", new MockSyncAdapter());

			manager.Save(entry1);
			manager.Save(entry2);

			IEnumerable<FeedConfigurationEntry> entries = manager.LoadAll();
			
			Assert.AreEqual(2, Count(entries));
		}

		[Test]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfSaveJobIsNull()
		{
			FeedConfigurationManager manager = GetManagerInstance(FeedsFolder);
			manager.Save(null);
		}

		[Test]
		public void ShouldSaveAndLoadEntry()
		{
			FeedConfigurationManager manager = GetManagerInstance(FeedsFolder);

			FeedConfigurationEntry entry = new XamlFeedConfigurationEntry("entry1",
				"title 1", "description 1", new MockSyncAdapter());

			manager.Save(entry);
						
			entry = manager.Load("entry1");
			
			Assert.IsNotNull(entry);
			Assert.AreEqual("entry1", entry.Name);
			Assert.AreEqual("title 1", entry.Title);
			Assert.AreEqual("description 1", entry.Description);
			Assert.IsInstanceOfType(typeof(MockSyncAdapter), entry.SyncAdapter);
		}

		[Test]
		public void ShouldOverwriteExistingConfiguration()
		{
			FeedConfigurationManager manager = GetManagerInstance(FeedsFolder);

			FeedConfigurationEntry entry1 = new XamlFeedConfigurationEntry("entry1",
				"title 1", "description 1", new MockSyncAdapter());

			manager.Save(entry1);

			entry1 = new XamlFeedConfigurationEntry("entry1",
				"title new", "description new", new MockSyncAdapter());
			
			manager.Save(entry1);

			entry1 = manager.Load("entry1");
			
			Assert.IsNotNull(entry1);
			Assert.AreEqual("title new", entry1.Title);
			Assert.AreEqual("description new", entry1.Description);
			Assert.IsInstanceOfType(typeof(MockSyncAdapter), entry1.SyncAdapter);
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfInvalidFileContents()
		{
			FeedConfigurationManager manager = GetManagerInstance(FeedsFolder);
			Directory.CreateDirectory(Path.Combine(FeedsFolder, "myFeed"));

			File.WriteAllText(Path.Combine(FeedsFolder, "myFeed\\MyFeed.xaml"), "<invalid xml\"");

			manager.Load("myFeed");
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfNotSaveXamlFeedConfigurationEntry()
		{
			FeedConfigurationManager manager = GetManagerInstance(FeedsFolder);
			FeedConfigurationEntry entry1 = new FeedConfigurationEntry("entry1",
				"title 1", "description 1", new MockSyncAdapter());

			manager.Save(entry1);
		}

		protected virtual FeedConfigurationManager GetManagerInstance(string path)
		{
			NameValueCollection attributes = new NameValueCollection();
			attributes.Add("configurationPath", path);

			FeedConfigurationManager manager = new FeedConfigurationManager();
			manager.Initialize(attributes);

			return manager;
		}
	}
}
