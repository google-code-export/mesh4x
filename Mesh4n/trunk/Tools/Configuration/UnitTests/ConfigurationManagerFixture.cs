//===============================================================================
// Microsoft patterns & practices
// Mobile Client Software Factory - July 2006
//===============================================================================
// Copyright  Microsoft Corporation.  All rights reserved.
// THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY
// OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT
// LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE.
//===============================================================================
// The example companies, organizations, products, domain names,
// e-mail addresses, logos, people, places, and events depicted
// herein are fictitious.  No association with any real company,
// organization, product, domain name, email address, logo, person,
// places, or events is intended or should be inferred.
//===============================================================================

#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Text;
using System.Collections.Generic;
using Microsoft.Practices.Mobile.Configuration;
using Microsoft.Practices.Mobile.Configuration.Tests.Mocks;
using Microsoft.Practices.TestUtilities;
using System.Threading;
using System.IO;
using System.Xml;


namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	/// <summary>
	///		This class contains unit tests for the ConfigurationManager class that we
	///		had to implement because this functionality is missing from the Compact
	///		Framework, but used by CAB.
	/// </summary>
	[TestClass]
	public class ConfigurationManagerFixture
	{
		[TestInitialize]
		public void InitializeConfigurationManager()
		{
			ConfigurationManager.ClearCache();
		}

		[TestMethod]
		public void GetSectionReturnsNullIfNoAppConfigFilePresent()
		{
			object section = ConfigurationManager.GetSection("junk");
			Assert.IsNull(section);
		}

		[TestMethod]
		public void GetSectionReturnsNullForNonExistSection()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				object section = ConfigurationManager.GetSection("junk");
				Assert.IsNull(section);
			}
		}

		[TestMethod]
		public void GetSectionSucceedsForValidSection()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				object section = ConfigurationManager.GetSection("CompositeUI");
				Assert.IsInstanceOfType(section, typeof(MockSettingsSection));
			}
		}

		[TestMethod]
		public void GetSectionReturnsValidSectionWithServices()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				object section = ConfigurationManager.GetSection("CompositeUI");
				MockSettingsSection settingSection = section as MockSettingsSection;
				Assert.IsNotNull(settingSection.Services);
				Assert.AreEqual(1, settingSection.Services.Count);
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void GetSectionThrowsWhenSectionDataMissing()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				object section = ConfigurationManager.GetSection("MissingSection");
				Assert.IsNotNull(section);
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void GetSectionThrowsExceptionWhenAssemblyMissing()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				object section = ConfigurationManager.GetSection("MissingAssembly");
			}
		}

		[TestMethod]
		public void GetSectionReturnsValidShellItemsSection()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				object section = ConfigurationManager.GetSection("ShellItems");
				MockShellItemsSection shellItems = (MockShellItemsSection) section;

				Assert.IsNotNull(shellItems.MenuItems);
				Assert.AreEqual(3, shellItems.MenuItems.Count);
			}
		}

		[TestMethod]
		public void CanEnumerateShellItemsSectionContents()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				MockShellItemsSection shellItems = (MockShellItemsSection)ConfigurationManager.GetSection("ShellItems");

				foreach (MenuItemElement menu in shellItems.MenuItems)
				{
				}
			}
		}

		[TestMethod]
		public void ShellItemsHaveCorrectValues()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				MockShellItemsSection shellItems = (MockShellItemsSection)ConfigurationManager.GetSection("ShellItems");

				foreach (MenuItemElement menu in shellItems.MenuItems)
				{
					switch (menu.ID)
					{
						case 1:
							Assert.AreEqual("FileDropDown", menu.Site);
							Assert.AreEqual("E&xit", menu.Label);
							Assert.AreEqual("FileExit", menu.CommandName);
							Assert.AreEqual("Alt, F4", menu.Key);
							break;

						case 2:
							Assert.AreEqual("MainMenu", menu.Site);
							Assert.AreEqual("&Help", menu.Label);
							Assert.AreEqual(true, menu.Register);
							Assert.AreEqual("Help", menu.RegistrationSite);
							break;

						case 3:
							break;

						default:
							Assert.Fail("Incorrect ID value.");
							break;
					}
				}
			}
		}

		[TestMethod]
		public void NewSettingSectionHasZeroServices()
		{
			MockSettingsSection settingSection = new MockSettingsSection();
			Assert.AreEqual(0, settingSection.Services.Count);
		}

		[TestMethod]
		public void CollectionItemLoadsEmbeddedChildCollectionElements()
		{
			using (TestResourceFile resFile = AppConfig.GetFile())
			{
				MockEndpointSection endpointSecion = (MockEndpointSection)ConfigurationManager.GetSection("Endpoints");
				Assert.AreEqual(2, endpointSecion.EndpointsItems.Count);
				Assert.AreEqual("http://default/MyHost", endpointSecion.EndpointsItems.GetEndpoint("MyHost").Address);
				Assert.AreEqual("http://work/MyHost", endpointSecion.EndpointsItems.GetEndpoint("MyHost").Networks.GetNetwork("Work").Address);
			}
		}

		[TestMethod]
		public void CanPassConfigurationSource()
		{
			string assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests";
#if PocketPC
			assemblyName += ".CF";
#endif
			string sectionXml = @"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.SimpleSection, {0}"" />
						</configSections>
						<MyCustomSection name=""junk""/>
					</configuration>
				";
			StringReader reader = new StringReader(String.Format(sectionXml, assemblyName));
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = new Configuration(xml);
			ConfigurationManager.Configuration = configuration;
			ConfigurationSection section1 = ConfigurationManager.GetSection("MyCustomSection");
		}

		[TestMethod]
		public void MultiplePropertiesWithSameConfigurationPropertyNameDontThrow()
		{
			string sectionXml = @"<MyCustomSection name=""junk""/>";

			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = XmlTextReader.Create(reader);
			MultipleNameSection section = (MultipleNameSection) ConfigurationManager.GetSectionFromXml(typeof(MultipleNameSection), xml);
			Assert.AreEqual("junk", section.Name);
			Assert.AreEqual("junk", section.SameName);
		}

		public class MultipleNameSection : ConfigurationSection
		{
			[ConfigurationProperty("name")]
			public string Name
			{
				get { return (string)this["name"]; }
			}

			[ConfigurationProperty("name")]
			public string SameName
			{
				get { return (string) this["name"]; }
			}
		}

		//[TestMethod]
		//public void GetSectionIsThreadSafe()
		//{
		//    using (new TestResourceFile(@"Configuration\App.config", false))
		//    {
		//        bool finished1 = false;
		//        bool finished2 = false;

		//        Thread thread1 = new Thread(delegate()
		//            {
		//                object section = ConfigurationManager.GetSection("CompositeUI");
		//                finished1 = (section != null);
		//            });
		//        Thread thread2 = new Thread(delegate()
		//            {
		//                object section = ConfigurationManager.GetSection("CompositeUI");
		//                finished2 = (section != null);
		//            });
		//        thread1.Start();
		//        thread2.Start();
		//        thread1.Join(10000);
		//        thread2.Join(10000);

		//        Assert.IsTrue(finished1);
		//        Assert.IsTrue(finished2);
		//    }
		//}
	}
}
