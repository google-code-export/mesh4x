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
using System.IO;
using System.Xml;
using Microsoft.Practices.Mobile.Configuration.Tests.Mocks;

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	[TestClass]
	public class ConfigurationFixture
	{
#if PocketPC
		private const string assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests.CF";
#else
		private const string assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests";
#endif
		[TestMethod]
		public void GetConfigurationForEmptyXmlWorks()
		{
			StringReader reader = new StringReader("");
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = new Configuration(xml);
		}

		[TestMethod]
		public void EmptyConfigurationLoadsCorrectly()
		{
			StringReader reader = new StringReader(@"
					<configuration>
					</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = new Configuration(xml);
			Assert.IsNotNull(configuration);
		}

		[TestMethod]
		public void EmptyConfigurationElementLoadsCorrectly()
		{
			StringReader reader = new StringReader(@"
					<configuration/>
				");
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = new Configuration(xml);
			Assert.IsNotNull(configuration);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ThrowsIfSectionTypeCannotLoad()
		{
			string sectionXml = @"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Foo, Bar"" />
						</configSections>
					</configuration>";
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = new Configuration(xml);
			object section = configuration.GetSection("MyCustomSection");
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void GetSectionThrowsWhenSectionDataMissing()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = new Configuration(xml);
			configuration.GetSection("MyCustomSection");
		}

		[TestMethod]
		public void GetSectionAlwaysReturnsSameInstance()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.SimpleSection, {0}"" />
						</configSections>
						<MyCustomSection name=""junk""/>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = new Configuration(xml);
			ConfigurationSection section1 = configuration.GetSection("MyCustomSection");
			Assert.IsNotNull(section1);
			ConfigurationSection section2 = configuration.GetSection("MyCustomSection");
			Assert.AreSame(section1, section2);
		}

		[TestMethod]
		public void GetSectionAlsoChecksParentConfiguration()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.SimpleSection, {0}"" />
						</configSections>
						<MyCustomSection name=""junk""/>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);
			Configuration parent = new Configuration(xml);

			sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyChildSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.SimpleSection, {0}"" />
						</configSections>
						<MyChildSection name=""junk""/>
					</configuration>
				", assemblyName);
			reader = new StringReader(sectionXml);
			xml = new XmlTextReader(reader);
			Configuration child = new Configuration(xml, parent);

			ConfigurationSection section = child.GetSection("MyChildSection");
			Assert.IsNotNull(section, "Didn't find MyChildSection in child");

			section = child.GetSection("MyCustomSection");
			Assert.IsNotNull(section, "Didn't find MyCustomSection in child");

			section = parent.GetSection("MyChildSection");
			Assert.IsNull(section);
		}
	}
}
