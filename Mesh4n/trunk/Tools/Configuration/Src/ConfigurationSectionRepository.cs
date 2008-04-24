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
using System.Collections;

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		The Configuration class obtains configuration section XML and type information from
	///		an instance of this class. You can supply a subclass that obtains this information
	///		from any source, not just an XML file. The subclass <see cref="ConfigurationXmlRepository"/>
	///		provides section information from any <see cref="XmlReader"/> source.
	/// </summary>
	public class ConfigurationSectionRepository
	{
		private Dictionary<string, ConfigurationSectionInfo> sections = new Dictionary<string, ConfigurationSectionInfo>();

		/// <summary>
		///		The constructor.
		/// </summary>
		public ConfigurationSectionRepository()
		{
		}

		/// <summary>
		///		Allows you to add section information to the in-memory list maintained by this
		///		instance.
		/// </summary>
		/// <param name="sectionName">Name used to retrieve the section information.</param>
		/// <param name="typeString">
		///		A "type, assembly" string used to create an instance of a ConfigurationSection subclass.
		/// </param>
		/// <param name="sectionXml">
		///		The XML data used to initialize the ConfigurationSection instance.
		/// </param>
		/// <exception cref="ArguemntNullException">
		///		Throws this exception if either of the <paramref name="sectionname"/> or <paramref name="typeString"/>
		///		parameters is null.
		/// </exception>
		/// <exception cref="ArguemntException">
		///		Throws this exception if either of the <paramref name="sectionname"/> or <paramref name="typeString"/>
		///		parameters is empty.
		/// </exception>
		public void Add(string sectionName, string typeString, string sectionXml)
		{
			Guard.ArgumentNotNullOrEmptyString(typeString, "typeString");
			Guard.ArgumentNotNullOrEmptyString(sectionName, "sectionName");

			ConfigurationSectionInfo info = new ConfigurationSectionInfo(sectionName, typeString, sectionXml);
			sections.Add(sectionName, info);
		}

		/// <summary>
		///		Returns either information about a configuration section, or null if a section
		///		with that name doesn't exist in this repository.
		/// </summary>
		/// <param name="sectionName">Name of the section to look for.</param>
		/// <returns>The information about a section, or null if the section doesn't exist.</returns>
		public virtual ConfigurationSectionInfo GetSectionInfo(string sectionName)
		{
			ConfigurationSectionInfo info;
			sections.TryGetValue(sectionName, out info);
			return info;
		}

		/// <summary>
		///		Provide subclasses access to the list of sections currently instantiated.
		/// </summary>
		protected IDictionary<string, ConfigurationSectionInfo> Sections
		{
			get { return sections; }
		}
	}
}
