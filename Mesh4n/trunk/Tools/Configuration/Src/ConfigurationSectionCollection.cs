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

using System;
using System.Collections.Generic;
using System.Text;
using System.Collections.Specialized;

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		A collection of <see cref="ConfigurationSection"/> objects.
	/// </summary>
	public class ConfigurationSectionCollection : NameObjectCollectionBase
	{
		private Dictionary<string, ConfigurationSection> sections = new Dictionary<string, ConfigurationSection>();

		public ConfigurationSectionCollection()
		{
		}

		/// <summary>
		///		Gets or sets a section, which is an instance of <see cref="ConfigurationSection"/>.
		/// </summary>
		/// <param name="sectionName">Name of the section to get or set.</param>
		/// <returns>The section retrieved from this collection, or null if it doesn't exist.</returns>
		public ConfigurationSection this[string sectionName]
		{
			get
			{
				if (sections.ContainsKey(sectionName))
					return sections[sectionName];
				else
					return null;
			}
			set { sections[sectionName] = value; }
		}

		/// <summary>
		///		Adds a new section to this collection.
		/// </summary>
		/// <param name="name">Name of the section to add.</param>
		/// <param name="section">The section to add.</param>
		/// <exception cref="ConfigurationErrorsException">
		///		Throws this exception if the name is null or empty.
		/// </exception>
		public void Add(string name, ConfigurationSection section)
		{
			if (name == null || name.Trim().Length == 0)
				throw new ConfigurationErrorsException(Properties.Resources.ConfigurationFailure);

			sections.Add(name, section);
		}
	}
}
