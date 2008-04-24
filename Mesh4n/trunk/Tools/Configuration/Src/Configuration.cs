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
using System.Xml;
using System.Xml.Schema;
using System.IO;
using System.Reflection;
using System.Globalization;

namespace Microsoft.Practices.Mobile.Configuration
{
	public class Configuration
	{
		private ConfigurationSectionRepository repository;
		private ConfigurationSectionCollection sections = new ConfigurationSectionCollection();
		private ProtectedConfigurationProvider protectedProvider;
		private Configuration parent;

		/// <summary>
		///		The constructor that allows you to specify a repository and a parent.
		/// </summary>
		/// <param name="repository">
		///		This is a <see cref="ConfigurationSectionRepository"/> instance or subclass instance
		///		that provides access to the section information used to create <see cref="ConfigurationSection"/>
		///		instances.
		/// </param>
		/// <param name="parent">
		///		Where to look for configuration sections if this Configuration object doesn't have
		///		a section.
		/// </param>
		public Configuration(ConfigurationSectionRepository repository, Configuration parent)
		{
			Init(repository, parent);
		}

		/// <summary>
		///		The constructor that allows you to specify the source of section information.
		/// </summary>
		/// <param name="repository">
		///		An instance of <see cref="ConfigurationSectionRepository"/>, or a subclass, that
		///		provides the data for sections.
		/// </param>
		public Configuration(ConfigurationSectionRepository repository)
			: this(repository, null)
		{
		}

		//
		//	<configuration>
		//		<configSections>
		//			<section name="..." type="..." />z
		//			...
		//		</configSections>
		//		section data...
		//	</configuration>
		//
		/// <summary>
		///		Constructor that takes an <see cref="XmlReader"/> as the source of configuration
		///		data, as well as parent that will be searched for sections not found in directly
		///		in this instance. The format of the data must be the same as App.Config.
		/// </summary>
		/// <param name="xml">
		///		The source of the XML data for the configuration information (sections and configuration
		///		data).
		/// </param>
		/// <param name="parent"></param>
		public Configuration(XmlReader xml, Configuration parent)
		{
			Init(new ConfigurationXmlRepository(xml), parent);
		}

		/// <summary>
		///		Constructor that takes an <see cref="XmlReader"/> as the source of configuration
		///		data. The format of the data must be the same as App.Config.
		/// </summary>
		/// <param name="xml">
		///		The source of the XML data for the configuration information (sections and configuration
		///		data).
		/// </param>
		public Configuration(XmlReader xml)
			: this(xml, null)
		{
		}

		private void Init(ConfigurationSectionRepository repository, Configuration parent)
		{
			this.repository = repository;
			this.parent = parent;
		}

		/// <summary>
		///		Retrieves a section instance. If you supplied a parent <see cref="Configuration"/>
		///		instance to the constructor, it will search in the parent instance for sections
		///		it can't find in this instance.
		/// </summary>
		/// <param name="sectionName">Name of the section to find.</param>
		/// <returns>
		///		An instance of <see cref="ConfigurationSection"/>, or a subclass, instantiated
		///		from the section data. Returns null if a section with that name doesn't exist.
		/// </returns>
		public ConfigurationSection GetSection(string sectionName)
		{
			lock (sections)
			{
				ConfigurationSection section = sections[sectionName];
				if (section != null)
					return section;

				//
				// We didn't find a section already built up, so now it's time to see if we can find
				// this section in the repository.
				//
				ConfigurationSectionInfo info = repository.GetSectionInfo(sectionName);
				if (info == null)
				{
					if (parent != null)
						return parent.GetSection(sectionName);
					else
						return null;
				}

				if (info.SectionXml == null)
				{
					string message = String.Format(CultureInfo.CurrentCulture, Properties.Resources.MissingSectionData, sectionName);
					throw new ConfigurationErrorsException(message);
				}

				section = ConfigurationManager.CreateFromTypeString(info.TypeString);
				if (info.SectionXml != null && info.SectionXml.Length > 0)
				{
					string sectionXml = info.SectionXml;
					if (info.IsEncrypted)
					{
						if (protectedProvider != null)
							sectionXml = protectedProvider.Decrypt(sectionXml);
						else
							sectionXml = ConfigurationManager.ProtectedConfigurationProvider.Decrypt(sectionXml);
					}

					XmlReader xml = XmlReader.Create(new StringReader(sectionXml));
					ConfigurationManager.GetSectionFromXml(section, xml);
				}
				sections.Add(sectionName, section);

				return section;
			}
		}

		/// <summary>
		///		Gets or sets the object that will be used to decrypt sections that have been encrypted.
		/// </summary>
		public ProtectedConfigurationProvider ProtectedConfigurationProvider
		{
			get { return protectedProvider; }
			set { protectedProvider = value; }
		}
	
	}
}
