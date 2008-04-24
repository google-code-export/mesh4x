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
	public class ConfigurationElementCollectionFixture
	{
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
		}
		[TestMethod]
		public void BaseGetCanRetrieveElementWithNonStringKey()
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

			MockShellItemsSection section = (MockShellItemsSection)ConfigurationManager.GetSectionFromXml(typeof(MockShellItemsSection), xml);
			MenuItemElement item = section.MenuItems.GetMenuItem(2);
			Assert.IsNotNull(item);
			Assert.AreEqual("MainMenu", item.Site);
		}

		[TestMethod]
		public void ConfigurationElementSupportsBaseTypesAttributes()
		{
			//Int16
			//Int32
			//Int64
			//UInt16
			//UInt32
			//UInt64
			//Char
			//Double
			//Byte
			//DateTime
			StringReader reader = new StringReader(@"
					<MyCustomSection>
						<SeveralTypesSection>
							<add name=""A"" Int16Attribute=""16"" Int32Attribute=""10"" Int64Attribute=""1"" 
											UInt16Attribute=""75"" UInt32Attribute=""1024"" UInt64Attribute=""2048"" 
											CharAttribute=""a"" DoubleAttribute="""+ (5.14).ToString() + @""" ByteAttribute=""1""
											StringAttribute=""Some string""
											DateTimeAttribute=""01/01/2006"" />
							<add name=""B"" />
						</SeveralTypesSection>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			MockSeveralTypesSection section = (MockSeveralTypesSection)ConfigurationManager.GetSectionFromXml(typeof(MockSeveralTypesSection), xml);
			MockSeveralAttributeTypesElement item = section.Items.GetItem("A");
			Assert.IsNotNull(item);
			Assert.AreEqual((Int16)16, item.Int16Attribute);
			Assert.AreEqual((Int32)10, item.Int32Attribute);
			Assert.AreEqual((Int64)1, item.Int64Attribute);
			Assert.AreEqual((UInt16)75, item.Uint16Attribute);
			Assert.AreEqual((UInt32)1024, item.Uint32Attribute);
			Assert.AreEqual((UInt64)2048, item.Uint64Attribute);
			Assert.AreEqual('a', item.CharAttribute);
			Assert.AreEqual(5.14, item.DoubleAttribute);
			Assert.AreEqual((byte)1, item.ByteAttribute);
			Assert.AreEqual(DateTime.Parse(@"01/01/2006"), item.DateTimeAttribute);
			Assert.AreEqual("Some string", item.StringAttribute);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void ConfigurationElementThrowsForUnsupportedType()
		{
			StringReader reader = new StringReader(@"
					<MyCustomSection>
						<SeveralTypesSection>
							<add name=""A"" ArrayAttribute="""" />
						</SeveralTypesSection>
					</MyCustomSection>
				");
			XmlReader xml = new XmlTextReader(reader);

			MockSeveralTypesSection section = (MockSeveralTypesSection)ConfigurationManager.GetSectionFromXml(typeof(MockSeveralTypesSection), xml);
		}
	}
}
