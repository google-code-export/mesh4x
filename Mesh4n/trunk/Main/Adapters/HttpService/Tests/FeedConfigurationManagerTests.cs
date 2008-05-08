using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.IO;
using Mesh4n.Adapters.HttpService.Configuration;
using System.Collections.Specialized;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestClass]
	public class FeedConfigurationManagerTests
	{
		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfPathIsNull()
		{
			NameValueCollection attributes = new NameValueCollection();
			new FeedConfigurationManager().Initialize(attributes);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowIfPathIsEmpty()
		{
			NameValueCollection attributes = new NameValueCollection();
			attributes.Add("configurationPath", string.Empty);
			new FeedConfigurationManager().Initialize(attributes);
		}

		[TestMethod]
		public void ShouldInitializePath()
		{
			FeedConfigurationManager manager = GetManagerInstance("feeds.xaml");
			Assert.AreEqual("feeds.xaml", manager.ConfigurationPath);
		}

		[TestMethod]
		public void ShouldGetAllEntries()
		{
			FeedConfigurationManager manager = GetManagerInstance("feeds.xaml");

			FeedConfigurationEntry entry1 = new XamlFeedConfigurationEntry("entry1",
				"title 1", "description 1", typeof(MockSyncAdapter));

			FeedConfigurationEntry entry2 = new XamlFeedConfigurationEntry("entry2",
				"title 2", "description 2", typeof(MockSyncAdapter));

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add(entry1.Name, entry1);
			configuration.Add(entry2.Name, entry2);

			manager.Save(configuration);

			configuration = manager.Load();
			
			Assert.AreEqual(2, configuration.Count);
		}

		[ExpectedException(typeof(ArgumentNullException))]
		[TestMethod]
		public void ShouldThrowIfSaveJobIsNull()
		{
			FeedConfigurationManager manager = GetManagerInstance("feeds.xaml");
			manager.Save(null);
		}

		[TestMethod]
		public void ShouldSaveAndLoadEntry()
		{
			FeedConfigurationManager manager = GetManagerInstance("feeds.xaml");

			FeedConfigurationEntry entry = new XamlFeedConfigurationEntry("entry1",
				"title 1", "description 1", typeof(MockSyncAdapter));

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add(entry.Name, entry);
			
			manager.Save(configuration);

			configuration = manager.Load();
			Assert.AreEqual(1, configuration.Count);

			entry = configuration["entry1"];
			Assert.AreEqual("entry1", entry.Name);
			Assert.AreEqual("title 1", entry.Title);
			Assert.AreEqual("description 1", entry.Description);
			Assert.IsInstanceOfType(entry.SyncAdapter, typeof(MockSyncAdapter));
		}

		[TestMethod]
		public void ShouldOverwriteExistingConfiguration()
		{
			FeedConfigurationManager manager = GetManagerInstance("feeds.xaml");

			FeedConfigurationEntry entry1 = new XamlFeedConfigurationEntry("entry1",
				"title 1", "description 1", typeof(MockSyncAdapter));

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add(entry1.Name, entry1);
			
			manager.Save(configuration);

			manager.Save(new FeedConfiguration());
			Assert.AreEqual(0, manager.Load().Count);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowIfInvalidFileContents()
		{
			FeedConfigurationManager manager = GetManagerInstance("feeds.xaml");
			File.WriteAllText("feeds.xaml", "<invalid xml\"");

			manager.Load();
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
