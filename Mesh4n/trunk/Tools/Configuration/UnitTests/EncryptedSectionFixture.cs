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
using System.Collections.Generic;
using System.Text;
using Microsoft.Practices.Mobile.Configuration.Tests.Mocks;
using System.IO;
using System.Xml;
using System.Security.Cryptography;

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	//	Note: This code is very, very early code. As written right now it is not at all
	//		  secure. We will be refactoring the code later to separate the key and
	//		  initialization vector from the encrypted data. Keeping them both together
	//		  is not at all secure because anyone can then decrypt the data.
	[TestClass]
	public class EncryptedSectionFixture
	{
		private static string sectionXml = @"
					<MyCustomSection>
						<MenuItems>
							<add id=""1"" site=""FileDropDown"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							<add id=""2"" site=""MainMenu"" label=""&amp;Help"" register=""true"" registrationsite=""Help"" />
							<add id=""3"" site=""Help"" label=""&amp;About..."" commandname=""HelpAbout"" key=""F1"" />
						</MenuItems>
					</MyCustomSection>
				";

		[TestMethod]
		public void ConfigurationManagerUsesProviderToDecryptSection()
		{
			byte[] key;
			byte[] encryptedData = RijndaelConfigurationProviderFixture.EncryptSectionData(sectionXml, out key);
			string xmlString = "<EncryptedSection>" + Convert.ToBase64String(encryptedData) + "</EncryptedSection>";
			StringReader reader = new StringReader(xmlString);
			XmlTextReader xml = new XmlTextReader(reader);

			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);

			MockShellItemsSection section = (MockShellItemsSection)ConfigurationManager.GetSectionFromXml(typeof(MockShellItemsSection), xml, provider);
			Assert.IsNotNull(section, "Section was null");
			Assert.IsNotNull(section.MenuItems, "MenuItems was null");
			Assert.AreEqual(3, section.MenuItems.Count);
		}

		[TestMethod]
		public void CanGetEncryptedSectionFromConfigurationManager()
		{
			byte[] key;
			byte[] encryptedData = RijndaelConfigurationProviderFixture.EncryptSectionData(sectionXml, out key);
			string xmlString = "<EncryptedSection>" + Convert.ToBase64String(encryptedData) + "</EncryptedSection>";
			StringReader reader = new StringReader(xmlString);
			XmlTextReader xml = new XmlTextReader(reader);

			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);
			ConfigurationManager.ProtectedConfigurationProvider = provider;

			MockShellItemsSection section = (MockShellItemsSection)ConfigurationManager.GetSectionFromXml(typeof(MockShellItemsSection), xml);
			Assert.IsNotNull(section, "Section was null");
			Assert.IsNotNull(section.MenuItems, "MenuItems was null");
			Assert.AreEqual(3, section.MenuItems.Count);
		}

		[TestMethod]
		public void CanGetEncryptedSectionFromConfigurationInstance()
		{
			string assemblyName;
			if (Environment.OSVersion.Platform == PlatformID.WinCE)
				assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests.CF";
			else
				assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests";

			string configXml = @"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
						<EncryptedSection name=""MyCustomSection"">{1}</EncryptedSection>
					</configuration>
				";

			byte[] key;
			byte[] encryptedData = RijndaelConfigurationProviderFixture.EncryptSectionData(sectionXml, out key);

			configXml = String.Format(configXml, assemblyName, Convert.ToBase64String(encryptedData));

			StringReader reader = new StringReader(configXml);
			XmlTextReader xml = new XmlTextReader(reader);

			ProtectedConfigurationProvider provider = new RijndaelConfigurationProvider(key);

			Configuration configuration = new Configuration(xml);
			configuration.ProtectedConfigurationProvider = provider;

			MockShellItemsSection section = (MockShellItemsSection)configuration.GetSection("MyCustomSection");
			Assert.IsNotNull(section, "Section was null");
			Assert.IsNotNull(section.MenuItems, "MenuItems was null");
			Assert.AreEqual(3, section.MenuItems.Count);
		}

		[TestMethod]
		public void ConfigurationInstanceWillUseProviderInConfigurationManager()
		{
			string assemblyName;
			if (Environment.OSVersion.Platform == PlatformID.WinCE)
				assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests.CF";
			else
				assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests";

			string configXml = @"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
						<EncryptedSection name=""MyCustomSection"">{1}</EncryptedSection>
					</configuration>
				";

			byte[] key;
			byte[] encryptedData = RijndaelConfigurationProviderFixture.EncryptSectionData(sectionXml, out key);

			configXml = String.Format(configXml, assemblyName, Convert.ToBase64String(encryptedData));

			StringReader reader = new StringReader(configXml);
			XmlTextReader xml = new XmlTextReader(reader);

			ConfigurationManager.ProtectedConfigurationProvider = new RijndaelConfigurationProvider(key);

			Configuration configuration = new Configuration(xml);

			MockShellItemsSection section = (MockShellItemsSection)configuration.GetSection("MyCustomSection");
			Assert.IsNotNull(section, "Section was null");
			Assert.IsNotNull(section.MenuItems, "MenuItems was null");
			Assert.AreEqual(3, section.MenuItems.Count);
		}
	}
}
