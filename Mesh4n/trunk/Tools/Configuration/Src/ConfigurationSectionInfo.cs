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

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		This class is used by <see cref="ConfigurtionSectionRepository"/>  to return information
	///		about a configuration section.
	/// </summary>
	public class ConfigurationSectionInfo
	{
		private string sectionName;
		private string typeString;
		private string sectionXml;
		private bool isEncrypted;

		/// <summary>
		///		The constructor for this read-only data object.
		/// </summary>
		/// <param name="typeString">String used to create an instance of a class.</param>
		/// <param name="sectionXml">The XML data used to create an instance of the type.</param>
		public ConfigurationSectionInfo(string sectionName, string typeString, string sectionXml)
		{
			this.sectionName = sectionName;
			this.typeString = typeString;
			this.sectionXml = sectionXml;
		}

		/// <summary>
		///		Gets or sets whether this section is encrypted.
		/// </summary>
		public bool IsEncrypted
		{
			get { return isEncrypted; }
			set { isEncrypted = value; }
		}

		/// <summary>
		///		Gets the name of the section.
		/// </summary>
		public string SectionName
		{
			get { return sectionName; }
		}

		/// <summary>
		///		Gets the XML for the section data.
		/// </summary>
		public string SectionXml
		{
			get { return sectionXml; }
			internal set { sectionXml = value; }
		}

		/// <summary>
		///		Gets the string that contains "type, assembly" used to create an instance of the type.
		/// </summary>
		public string TypeString
		{
			get { return typeString; }
		}
	}
}
