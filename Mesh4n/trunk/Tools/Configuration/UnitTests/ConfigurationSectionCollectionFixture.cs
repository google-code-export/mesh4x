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

using System.Collections.Generic;
using System.Text;
using System;

namespace Microsoft.Practices.Mobile.Configuration.Tests
{
	[TestClass]
	public class ConfigurationSectionCollectionFixture
	{
		[TestMethod]
		public void ConfigurationSectionColllectionAllowsNullParent()
		{
			ConfigurationSectionCollection sections = new ConfigurationSectionCollection();
			Assert.IsNotNull(sections);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void AddingSectionWithNullNameThrows()
		{
			ConfigurationSectionCollection sections = new ConfigurationSectionCollection();
			ConfigurationSection section = new ConfigurationSection();
			sections.Add(null, section);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void AddingSectionWithEmptyNameThrows()
		{
			ConfigurationSectionCollection sections = new ConfigurationSectionCollection();
			ConfigurationSection section = new ConfigurationSection();
			sections.Add(String.Empty, section);
		}

		[TestMethod]
		[ExpectedException(typeof(ConfigurationErrorsException))]
		public void AddingSectionWithSpacesOnlyForNameThrows()
		{
			ConfigurationSectionCollection sections = new ConfigurationSectionCollection();
			ConfigurationSection section = new ConfigurationSection();
			sections.Add("  ", section);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void AddingSectionWithDuplicateNameThrows()
		{
			ConfigurationSectionCollection sections = new ConfigurationSectionCollection();
			ConfigurationSection section1 = new ConfigurationSection();
			ConfigurationSection section2 = new ConfigurationSection();
			sections.Add("test", section1);
			sections.Add("test", section2);
		}

		[TestMethod]
		public void AccessingNonExistantSectionReturnsNull()
		{
			ConfigurationSectionCollection sections = new ConfigurationSectionCollection();
			ConfigurationSection section = sections["junk"];
			Assert.IsNull(section);
		}

		[TestMethod]
		public void IndexerReturnsAddedSection()
		{
			ConfigurationSectionCollection sections = new ConfigurationSectionCollection();
			ConfigurationSection section1 = new ConfigurationSection();
			ConfigurationSection section2 = new ConfigurationSection();
			sections.Add("test1", section1);
			sections.Add("test2", section2);

			ConfigurationSection section = sections["test1"];
			Assert.AreSame(section1, section);

			section = sections["test2"];
			Assert.AreSame(section2, section);
		}
	}
}
