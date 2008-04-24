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
using System.Xml;
using System.IO;

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	[TestClass]
	public class ConfigurationSectionRepositoryFixture
	{
		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void AddingNullTypeThrows()
		{
			ConfigurationSectionRepository repository = new ConfigurationSectionRepository();
			repository.Add("testSection", null, null);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void AddingEmptyTypeThrows()
		{
			ConfigurationSectionRepository repository = new ConfigurationSectionRepository();
			repository.Add("testSection", String.Empty, null);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void AddingNullSectionNameThrows()
		{
			ConfigurationSectionRepository repository = new ConfigurationSectionRepository();
			repository.Add(null, "junk", null);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void AddingEmptySectionNameThrows()
		{
			ConfigurationSectionRepository repository = new ConfigurationSectionRepository();
			repository.Add(String.Empty, "junk", null);
		}

		[TestMethod]
		public void RequestForNonExistantSectionReturnsNull()
		{
			ConfigurationSectionRepository repository = new ConfigurationSectionRepository();

			ConfigurationSectionInfo info = repository.GetSectionInfo("testSection");
			Assert.IsNull(info);
		}

		[TestMethod]
		public void GetReturnsAddedSection()
		{
			ConfigurationSectionRepository repository = new ConfigurationSectionRepository();
			string type = "Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockSettingsSection, Microsoft.Practices.Mobile.Configuration.Tests";
			string xml = "<test>";

			repository.Add("testSection", type, xml);
			ConfigurationSectionInfo info = repository.GetSectionInfo("testSection");
			Assert.IsNotNull(info);
			Assert.AreEqual(type, info.TypeString);
			Assert.AreEqual(xml, info.SectionXml);
		}

		[TestMethod]
		public void RepositoryAllowsNullXmlReader()
		{
			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(null);
		}

		[TestMethod]
		public void RepositoryAllowsEmptyXml()
		{
			StringReader reader = new StringReader(@"");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void RootElementMustHaveCorrectName()
		{
			StringReader reader = new StringReader(@"
					<NoConfiguration>
					</NoConfiguration>
				");
			XmlReader xml = new XmlTextReader(reader);

			try
			{
				ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
			}
			catch (Exception ex)
			{
				Assert.AreEqual("Configuration system failed to initialize", ex.Message);
				throw;
			}
		}

		[TestMethod]
		public void EmptyConfigurationLoads()
		{
			StringReader reader = new StringReader(@"
					<configuration>
					</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
			Assert.IsNotNull(repository);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ConfigurationXmlFailsIfSectionContainsInvalidElement()
		{
			StringReader reader = new StringReader(@"
					<configuration>
						<configSections>
							<junk />
						</configSections>
					</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			try
			{
				ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
			}
			catch (Exception ex)
			{
				Assert.AreEqual("Configuration system failed to initialize", ex.Message);
				throw;
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void SectionElementMustIncludeNameAttribute()
		{
			StringReader reader = new StringReader(@"
					<configuration>
						<configSections>
							<section type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, Microsoft.Practices.Mobile.Configuration.Tests"" />
						</configSections>
					</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void SectionElementMustIncludeTypeAttribute()
		{
			StringReader reader = new StringReader(@"
					<configuration>
						<configSections>
							<section name=""test"" />
						</configSections>
					</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
		}

		[TestMethod]
		public void RepositoryIgnoresCommentsInSection()
		{
			StringReader reader = new StringReader(@"
					<configuration>
						<configSections>
							<!--Comment-->
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, Microsoft.Practices.Mobile.Configuration.Tests"" />
						</configSections>
					</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
		}

		[TestMethod]
		public void EmptyConfigurationElementLoads()
		{
			StringReader reader = new StringReader(@"
					<configuration/>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
		}

		[TestMethod]
		public void ConfigurationRepositoryParsesXml()
		{
			StringReader reader = new StringReader(@"
				<configuration>
					<configSections>
						<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk, Microsoft.Practices.Mobile.Configuration.Tests"" />
					</configSections>
				</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
			ConfigurationSectionInfo info = repository.GetSectionInfo("MyCustomSection");
			Assert.IsNotNull(info);
			Assert.IsNull(info.SectionXml);
			Assert.IsNotNull(info.TypeString);
			Assert.IsTrue(info.TypeString.Length > 0);
		}

		[TestMethod]
		public void RepositoryParsesMultipleSections()
		{
			StringReader reader = new StringReader(@"
				<configuration>
					<configSections>
						<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk, Microsoft.Practices.Mobile.Configuration.Tests"" />
						<section name=""MyCustomSection2"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk2, Microsoft.Practices.Mobile.Configuration.Tests"" />
					</configSections>
				</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
			ConfigurationSectionInfo info = repository.GetSectionInfo("MyCustomSection");
			Assert.IsNotNull(info, "Didn't find MyCustomeSection");
			ConfigurationSectionInfo info2 = repository.GetSectionInfo("MyCustomSection2");
			Assert.IsNotNull(info2, "Didn't find MyCustomeSection2");

			Assert.AreNotSame(info, info2);
		}

		[TestMethod]
		public void RepositoryIsCaseSensitiveForSectionNames()
		{
			StringReader reader = new StringReader(@"
				<configuration>
					<configSections>
						<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk, Microsoft.Practices.Mobile.Configuration.Tests"" />
					</configSections>
				</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
			ConfigurationSectionInfo info = repository.GetSectionInfo("MycustomSection");
			Assert.IsNull(info);
		}

		[TestMethod]
		public void RepositoryReturnsSectionXml()
		{
			StringReader reader = new StringReader(@"
				<configuration>
					<configSections>
						<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk, Microsoft.Practices.Mobile.Configuration.Tests"" />
						<section name=""MyCustomSection2"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk, Microsoft.Practices.Mobile.Configuration.Tests"" />
					</configSections>
					<MyCustomSection2 />
					<MyCustomSection test=""test""/>
				</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			ConfigurationSectionRepository repository = new ConfigurationXmlRepository(xml);
			ConfigurationSectionInfo info = repository.GetSectionInfo("MyCustomSection");

			string sectionData = @"<MyCustomSection test=""test"" />";
			Assert.AreEqual(sectionData, info.SectionXml);
		}
	}
}
