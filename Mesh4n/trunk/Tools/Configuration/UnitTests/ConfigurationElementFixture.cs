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

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	[TestClass]
	public class ConfigurationElementFixture
	{
		private TSection GetSection<TSection>(string xml, string sectionName)
			where TSection : ConfigurationSection
		{
			string sectionXml = String.Format(xml, typeof(TSection).AssemblyQualifiedName);
			StringReader reader = new StringReader(sectionXml);

			Configuration configuration = new Configuration(XmlReader.Create(reader));
			ConfigurationManager.Configuration = configuration;
			return (TSection)ConfigurationManager.GetSection(sectionName);
		}

		[TestMethod]
		public void ConfigurationElementCanHandleUnrecognizedAttributes()
        {
			MockSection section = GetSection<MockSection>(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""{0}"" />
						</configSections>
						<MyCustomSection>
							<providers>
								<add name=""Foo"" bar=""baz"" />
							</providers>
						</MyCustomSection>
					</configuration>", "MyCustomSection");

			Assert.AreEqual(1, section.Providers.Count);
			Assert.AreEqual("baz", section.Providers["Foo"].Attributes["bar"]);

		}

		class MockSection : ConfigurationSection
		{
			[ConfigurationProperty("providers")]
			public MockCollection Providers
			{
				get { return (MockCollection)this["providers"]; }
				set { this["providers"] = value; }
			}
		}

		class MockCollection : ConfigurationElementCollection
		{
			protected override object GetElementKey(ConfigurationElement element)
			{
				return ((MockElement)element).Name;
			}

			protected override ConfigurationElement CreateNewElement()
			{
				return new MockElement();
			}

			/// <summary>
			///	Gets an element from the collection with the given key.
			/// </summary>
			public MockElement this[string name]
			{
				get { return (MockElement)BaseGet(name); }
			}
		}

		class MockElement : ConfigurationElement
		{
			public Dictionary<string, string> Attributes = new Dictionary<string, string>();

			protected override bool OnDeserializeUnrecognizedAttribute(string name, string value)
			{
				Attributes.Add(name, value);
				return true;
			}

			[ConfigurationProperty("name")]
			public string Name
			{
				get { return (string)this["name"]; }
				set { this["name"] = value; }
			}
		}

	}
}
