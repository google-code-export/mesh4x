using System;
using System.Collections.Generic;
using System.Text;
using System.Configuration;
using Mesh4n.Adapters.HttpService.Configuration;
using NUnit.Framework;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestFixture]
	public class SyncServiceConfigurationSectionTests
	{
		public SyncServiceConfigurationSectionTests()
		{
		}

		[Test]
		public void ShouldLoadConfigurationFromFile()
		{
			SyncServiceConfigurationSection configurationSection = 
				(SyncServiceConfigurationSection)ConfigurationManager.GetSection(SyncServiceConfigurationSection.SectionName);

			Assert.IsNotNull(configurationSection);
			Assert.IsNotNull(configurationSection.ConfigurationManager);
			Assert.AreEqual("Mesh4n.Adapters.HttpService.Configuration.FeedConfigurationManager, Mesh4n.Adapters.HttpService",
				configurationSection.ConfigurationManager.TypeName);
			Assert.IsNotNull(configurationSection.ConfigurationManager.Attributes["configurationPath"]);
		}

		[Test]
		public void ShouldGetSectionInstance()
		{
			SyncServiceConfigurationSection configurationSection = SyncServiceConfigurationSection.GetSection();
			Assert.IsNotNull(configurationSection);
		}

		[Test]
		public void ShouldGetFeedManagerInstance()
		{
			IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
			
			Assert.IsNotNull(manager);
			Assert.IsInstanceOfType(typeof(FeedConfigurationManager), manager);
		}
	}
}
