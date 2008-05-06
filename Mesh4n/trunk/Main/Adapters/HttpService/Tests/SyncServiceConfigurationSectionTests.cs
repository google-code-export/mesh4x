using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Configuration;
using Mesh4n.Adapters.HttpService.Configuration;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestClass]
	public class SyncServiceConfigurationSectionTests
	{
		public SyncServiceConfigurationSectionTests()
		{
		}

		[TestMethod]
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

		[TestMethod]
		public void ShouldGetSectionInstance()
		{
			SyncServiceConfigurationSection configurationSection = SyncServiceConfigurationSection.GetSection();
			Assert.IsNotNull(configurationSection);
		}

		[TestMethod]
		public void ShouldGetFeedManagerInstance()
		{
			IFeedConfigurationManager manager = SyncServiceConfigurationSection.GetConfigurationManager();
			
			Assert.IsNotNull(manager);
			Assert.IsInstanceOfType(manager, typeof(FeedConfigurationManager));
		}
	}
}
