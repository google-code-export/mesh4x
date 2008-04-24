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
using Microsoft.Practices.Mobile.Configuration.Tests.Mocks;

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	[TestClass]
	public class ConfigurationStorageOverrideFixture
	{
#if PocketPC
		private string assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests.CF";
#else
		private string assemblyName = "Microsoft.Practices.Mobile.Configuration.Tests";
#endif
		[TestMethod]
		public void CanTurnXmlIntoConfigurationSection()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection/>
				");
			XmlReader xml = new XmlTextReader(reader);

			MockCustomSection section = (MockCustomSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockCustomSection), xml);

			Assert.IsNotNull(section);
		}

		[TestMethod]
		public void CreatTypeFromStringWorks()
		{
			string type = String.Format("Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}", assemblyName);
			ConfigurationSection section = ConfigurationManager.CreateFromTypeString(type);
			Assert.IsNotNull(section);
			Assert.IsInstanceOfType(section, typeof(MockShellItemsSection));
		}

		[TestMethod]
		public void SectionPropertiesAreLoaded()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection test=""This is a test"" someValue=""12345"" />
				");
			XmlReader xml = new XmlTextReader(reader);

			MockCustomSection section = (MockCustomSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockCustomSection), xml);

			Assert.IsNotNull(section);
			Assert.AreEqual(12345, section.SomeValue);
			Assert.AreEqual("This is a test", section.Test);
		}

		[TestMethod]
		public void SectionPropertiesAreLoadedIntoExistingInstance()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection test=""This is a test"" someValue=""12345"" />
				");
			XmlReader xml = new XmlTextReader(reader);

			MockCustomSection section = new MockCustomSection();
			ConfigurationManager.GetSectionFromXml(section, xml);

			Assert.IsNotNull(section);
			Assert.AreEqual(12345, section.SomeValue);
			Assert.AreEqual("This is a test", section.Test);
		}

		[TestMethod]
		public void SectionChildIsLoadedAlongWithSectionProperties()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection test=""This is a test"" someValue=""12345"">
						<Child name=""Test Name""/>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			MockCustomSection section = (MockCustomSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockCustomSection), xml);

			Assert.IsNotNull(section, "Section should not have been null");
			Assert.AreEqual(12345, section.SomeValue);
			Assert.AreEqual("This is a test", section.Test);

			Assert.IsNotNull(section.Child, "section.Child should not have been null");
			Assert.IsNotNull(section.Child.Name, "section.Child.Name should not be null");
			Assert.AreEqual("Test Name", section.Child.Name);
		}

		[TestMethod]
		public void PropertyAsChildElementLoadsCorrectly()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection test=""This is a test"" someValue=""12345"">
						<Child>
							<name>Test Name</name>
						</Child>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			MockCustomSection section = (MockCustomSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockCustomSection), xml);

			Assert.IsNotNull(section, "Section should not have been null");
			Assert.AreEqual(12345, section.SomeValue);
			Assert.AreEqual("This is a test", section.Test);

			Assert.IsNotNull(section.Child, "section.Child should not have been null");
			Assert.IsNotNull(section.Child.Name, "section.Child.Name should not be null");
			Assert.AreEqual("Test Name", section.Child.Name);
		}

		[TestMethod]
		public void TwoConfigurationElementPropertiesLoad()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection test=""This is a test"" someValue=""12345"">
						<Child>
							<name>Test Name</name>
						</Child>
						<AnotherChild name=""another""/>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			MockCustomSection section = (MockCustomSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockCustomSection), xml);

			Assert.AreEqual("Test Name", section.Child.Name);
			Assert.IsNotNull(section.AnotherChild);
			Assert.AreEqual("another", section.AnotherChild.Name);
		}

		[TestMethod]
		public void InitOnClosedElementReadsUpToNextElement()
		{
			StringReader reader = new StringReader(@"
					<Parent>
						<MyCustomSection test=""This is a test"" someValue=""12345""/>
						<NextChild/>
					</Parent>
				");
			XmlReader xml = new XmlTextReader(reader);

			while (!(xml.NodeType == XmlNodeType.Element && String.Compare(xml.Name, "MyCustomSection") == 0))
				xml.Read();

			MockCustomSection section = (MockCustomSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockCustomSection), xml);
			Assert.AreEqual(XmlNodeType.Element, xml.NodeType);
			Assert.AreEqual("NextChild", xml.Name);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void UnknownAttributeThrowsException()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection test=""This is a test"" someValue=""12345"" junk=""junk""/>
				");
			XmlReader xml = new XmlTextReader(reader);

			try
			{
				MockCustomSection section = (MockCustomSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockCustomSection), xml);
			}
			catch (ConfigurationErrorsException ex)
			{
				//
				// Note: We're having to catch this exception so we can do the Assert.AreEqual
				//		 because the VSTS ExpectedException doesn't compare the optional message
				//		 string value to the thrown exception's value.
				//
				Assert.AreEqual("Unrecognized attribute 'junk'.", ex.Message);
				throw;
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void UnknownElementThrowsException()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection test=""This is a test"" someValue=""12345"">
						<UnknownElement/>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			try
			{
				MockCustomSection section = (MockCustomSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockCustomSection), xml);
			}
			catch (ConfigurationErrorsException ex)
			{
				Assert.AreEqual("Unrecognized element 'UnknownElement'.", ex.Message);
				throw;
			}
		}

		[TestMethod]
		public void CollectionPropertyIsLoaded()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection>
						<MenuItems>
							<add id=""1"" site=""FileDropDown"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							<add id=""2"" site=""MainMenu"" label=""&amp;Help"" register=""true"" registrationsite=""Help"" />
							<add id=""3"" site=""Help"" label=""&amp;About..."" commandname=""HelpAbout"" key=""F1"" />
						</MenuItems>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			MockShellItemsSection section = (MockShellItemsSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockShellItemsSection), xml);
			Assert.IsNotNull(section, "Expected section not to be null");
			Assert.IsNotNull(section.MenuItems, "Expected MenuItems not to be null");
			Assert.AreEqual(3, section.MenuItems.Count);

			int count = 0;
			foreach (MenuItemElement menu in section.MenuItems)
			{
				Assert.AreEqual("FileDropDown", menu.Site);
				Assert.AreEqual(1, menu.ID);
				count++;
				break;
			}
			Assert.AreEqual(1, count);
		}

		[TestMethod]
		public void CollectionPropertyIsLoadedWhenACommentIsInsideSection()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection>
						<MenuItems>
						<!--Comment-->
							<add id=""1"" site=""FileDropDown"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							<add id=""2"" site=""MainMenu"" label=""&amp;Help"" register=""true"" registrationsite=""Help"" />
							<add id=""3"" site=""Help"" label=""&amp;About..."" commandname=""HelpAbout"" key=""F1"" />
						</MenuItems>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			MockShellItemsSection section = (MockShellItemsSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockShellItemsSection), xml);
			Assert.IsNotNull(section, "Expected section not to be null");
			Assert.IsNotNull(section.MenuItems, "Expected MenuItems not to be null");
			Assert.AreEqual(3, section.MenuItems.Count);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void MissingRequiredPropertyThrowsException()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection>
						<MenuItems>
							<add id=""1"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
						</MenuItems>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			try
			{
				MockShellItemsSection section = (MockShellItemsSection)ConfigurationManager.GetSectionFromXml(typeof(Mocks.MockShellItemsSection), xml);
			}
			catch (Exception ex)
			{
				Assert.AreEqual("Required element is missing: 'site'.", ex.Message);
				throw;
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ConfigurationXmlFailsIfRootWrongName()
		{
			StringReader reader = new StringReader(@"
					<NoConfiguration>
					</NoConfiguration>
				");
			XmlReader xml = new XmlTextReader(reader);

			try
			{
				Configuration configuration = ConfigurationManager.GetConfiguration(xml);
			}
			catch (Exception ex)
			{
				Assert.AreEqual("Configuration system failed to initialize", ex.Message);
				throw;
			}
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
				Configuration configuration = ConfigurationManager.GetConfiguration(xml);
			}
			catch (Exception ex)
			{
				Assert.AreEqual("Configuration system failed to initialize", ex.Message);
				throw;
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ConfigurationXmlFailsIfSectionAttributeMissingName()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ConfigurationXmlFailsIfTypeAttributeMissing()
		{
			StringReader reader = new StringReader(@"
					<configuration>
						<configSections>
							<section name=""test"" />
						</configSections>
					</configuration>
				");
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
		}

		[TestMethod]
		public void ConfigurationXmlIgnoresCommentsInSection()
		{
			string sectionXml =String.Format(@"
					<configuration>
						<configSections>
							<!--Comment-->
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
		}

		[TestMethod]
		public void ConfigurationXmlIgnoresTypesThatDontExist()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk, {0}"" />
						</configSections>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void GetSectionThrowsWhenTypeDosntExist()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk, {0}"" />
						</configSections>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
			configuration.GetSection("MyCustomSection");
		}

		[TestMethod]
		public void GetSectionReturnsNullForNonExistantSection()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.Junk, {0}"" />
						</configSections>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
			ConfigurationSection section = configuration.GetSection("junk");
			Assert.IsNull(section);
		}

		[TestMethod]
		public void CreateConfigurationFromXml()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
						<MyCustomSection>
							<MenuItems>
								<add id=""1"" site=""FileDropDown"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							</MenuItems>
						</MyCustomSection>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
			Assert.IsNotNull(configuration);

			MockShellItemsSection section = (MockShellItemsSection)configuration.GetSection("MyCustomSection");
			Assert.IsNotNull(section);
		}

		[TestMethod]
		public void SectionFromXmlLoadsCustomSection()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
						<MyCustomSection>
							<MenuItems>
								<add id=""1"" site=""FileDropDown"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							</MenuItems>
						</MyCustomSection>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
			MockShellItemsSection section = (MockShellItemsSection)configuration.GetSection("MyCustomSection");
			Assert.AreEqual(1, section.MenuItems.Count);
		}

		[TestMethod]
		public void ExtraXmlIsAllowed()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
						<MyCustomSection>
							<MenuItems>
								<add id=""1"" site=""FileDropDown"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							</MenuItems>
						</MyCustomSection>
						<ExtraJunk>
						</ExtraJunk>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
			MockShellItemsSection section = (MockShellItemsSection)configuration.GetSection("MyCustomSection");
			Assert.AreEqual(1, section.MenuItems.Count);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ConfigSectionsElementMustAppearFirst()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<MyCustomSection>
							<MenuItems>
								<add id=""1"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							</MenuItems>
						</MyCustomSection>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			try
			{
				Configuration configuration = ConfigurationManager.GetConfiguration(xml);
			}
			catch (Exception ex)
			{
				Assert.AreEqual("Configuration system failed to initialize", ex.Message);
				throw;
			}
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void DuplicateSectionDataElementsThrows()
		{
			string sectionXml = String.Format(@"
					<configuration>
						<configSections>
							<section name=""MyCustomSection"" type=""Microsoft.Practices.Mobile.Configuration.Tests.Mocks.MockShellItemsSection, {0}"" />
						</configSections>
						<MyCustomSection>
							<MenuItems>
								<add id=""1"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							</MenuItems>
						</MyCustomSection>
						<MyCustomSection>
							<MenuItems>
								<add id=""1"" label=""E&amp;xit"" commandname=""FileExit"" key=""Alt, F4"" />
							</MenuItems>
						</MyCustomSection>
					</configuration>
				", assemblyName);
			StringReader reader = new StringReader(sectionXml);
			XmlReader xml = new XmlTextReader(reader);

			Configuration configuration = ConfigurationManager.GetConfiguration(xml);
		}
	}


	public class MockConfigurationSectionA : ConfigurationSection
	{
	}
}
