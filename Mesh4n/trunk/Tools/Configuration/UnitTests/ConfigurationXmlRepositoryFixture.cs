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
	[TestClass]
	public class ConfigurationXmlRepositoryFixture
	{
#if PocketPC
		private const string assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests";
#else
		private const string assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests.CF";
#endif

		[TestMethod]
		public void ParsingIgnoresUnknownSectionAttributes()
		{
			string sectionXml = AppConfig.GetResourceString("App-with-unknown-attributes.config");
			StringReader reader = new StringReader(sectionXml);
			XmlTextReader xml = new XmlTextReader(reader);

			MockXmlRepository repository = new MockXmlRepository(xml);
			Assert.IsTrue(repository.Sections.ContainsKey("CompositeUI"));
		}

		[TestMethod]
		public void SectionWithoutSectionTypeThrowsMeaningfullException()
		{
			string sectionXml = AppConfig.GetResourceString("App-without-sectiontype.config");
			StringReader reader = new StringReader(sectionXml);
			XmlTextReader xml = new XmlTextReader(reader);

			try
			{
				MockXmlRepository repository = new MockXmlRepository(xml);
			}
			catch (ConfigurationException ex)
			{
				Assert.IsTrue(ex.Message.IndexOf("type") >= 0);
			}
		}

		[TestMethod]
		public void SectionWithoutSectionNameThrowsMeaningfullException()
		{
			string sectionXml = AppConfig.GetResourceString("App-without-sectionname.config");
			StringReader reader = new StringReader(sectionXml);
			XmlTextReader xml = new XmlTextReader(reader);

			try
			{
				MockXmlRepository repository = new MockXmlRepository(xml);
			}
			catch (ConfigurationException ex)
			{
				Assert.IsTrue(ex.Message.IndexOf("name")>=0);
			}
		}

		[TestMethod]
		public void SectionWithEmptySectionTypeThrowsMeaningfullException()
		{
			string sectionXml = AppConfig.GetResourceString("App-with-empty-sectiontype.config");
			StringReader reader = new StringReader(sectionXml);
			XmlTextReader xml = new XmlTextReader(reader);

			try
			{
				MockXmlRepository repository = new MockXmlRepository(xml);
			}
			catch (ConfigurationException ex)
			{
				Assert.IsTrue(ex.Message.IndexOf("type")>=0);
			}
		}

		[TestMethod]
		public void SectionWithEmptySectionNameThrowsMeaningfullException()
		{
			string configXml = AppConfig.GetResourceString("App-with-empty-sectionname.config");
			StringReader reader = new StringReader(configXml);
			XmlTextReader xml = new XmlTextReader(reader);

			try
			{
				MockXmlRepository repository = new MockXmlRepository(xml);
			}
			catch (ConfigurationException ex)
			{
				Assert.IsTrue(ex.Message.IndexOf("name")>=0);
			}
		}

		[TestMethod]
		public void OrdinarySectionIsntMarkedAsEncrypted()
		{
			string configXml = @"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, Junk"" />
						</configSections>
						<MyCustomSection><junk/></MyCustomSection>
					</configuration>
				";

			StringReader reader = new StringReader(configXml);
			XmlTextReader xml = new XmlTextReader(reader);

			ConfigurationXmlRepository repository = new ConfigurationXmlRepository(xml);
			ConfigurationSectionInfo info = repository.GetSectionInfo("MyCustomSection");
			Assert.AreEqual("MyCustomSection", info.SectionName);
			Assert.AreEqual("<MyCustomSection><junk /></MyCustomSection>", info.SectionXml);
			Assert.IsFalse(info.IsEncrypted);
		}

		[TestMethod]
		public void EncryptedSectionDataIsAdded()
		{
			string configXml = @"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, Junk"" />
						</configSections>
						<EncryptedSection name=""MyCustomSection"">{0}</EncryptedSection>
					</configuration>
				";

			string sectionXml = "<MyCustomSection/>";

			byte[] key;
			string encryptedSection = Convert.ToBase64String(RijndaelConfigurationProviderFixture.EncryptSectionData(sectionXml, out key));

			configXml = String.Format(configXml, encryptedSection);

			StringReader reader = new StringReader(configXml);
			XmlTextReader xml = new XmlTextReader(reader);

			MockXmlRepository repository = new MockXmlRepository(xml);
			ConfigurationSectionInfo info = repository.Sections["MyCustomSection"];
			Assert.IsTrue(info.IsEncrypted);
			Assert.AreEqual(encryptedSection, info.SectionXml);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void DuplicateSectionElementThrows()
		{
			string configXml = @"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, Junk"" />
						</configSections>
						<MyCustomSection/>
						<MyCustomSection/>
					</configuration>
				";

			StringReader reader = new StringReader(configXml);
			XmlTextReader xml = new XmlTextReader(reader);

			MockXmlRepository repository = new MockXmlRepository(xml);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ParseErrorExceptionContainsInnerExceptionWithDetailedError()
		{
			string configXml = @"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, Junk"" />
						</configSections>
						<BadXmlElement
					</configuration>
				";

			StringReader reader = new StringReader(configXml);
			XmlTextReader xml = new XmlTextReader(reader);

			try
			{
				MockXmlRepository repository = new MockXmlRepository(xml);
			}
			catch (ConfigurationErrorsException ex)
			{
				Assert.IsNotNull(ex.InnerException);
				throw;
			}
		}

		class MockXmlRepository : ConfigurationXmlRepository
		{
			public MockXmlRepository(XmlReader reader) : base(reader) {}

			public new IDictionary<string, ConfigurationSectionInfo> Sections
			{
				get { return base.Sections; }
			}
		}
	}
}
