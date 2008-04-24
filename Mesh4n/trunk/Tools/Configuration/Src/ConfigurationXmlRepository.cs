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
using System.Globalization;

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		This subclass of <see cref="ConfigurationSectionRepository"/> parses XML from an
	///		<see cref="XmlReader"/> and saves it in the list of section data.
	/// </summary>
	public class ConfigurationXmlRepository : ConfigurationSectionRepository
	{
		/// <summary>
		///		Consturctor that takes an <see cref="XmlReader"/> instance as the source of
		///		configuration data. The format must conform to the App.Config requirements.
		/// </summary>
		/// <param name="xml"></param>
		public ConfigurationXmlRepository(XmlReader xml) : base()
		{
			if (xml == null || xml.EOF)
				return;

			try
			{
				if (xml.NodeType == XmlNodeType.None)
					xml.Read();
			}
			catch
			{
				return;
			}

			ParseXml(xml);
		}

		private void ParseXml(XmlReader xml)
		{
			try
			{
				MoveToNextElement(xml);
				if (xml.EOF || xml.Name != "configuration")
					throw new ConfigurationErrorsException(Properties.Resources.ConfigurationFailure);

				LoadConfigSections(xml);
				while (!xml.EOF)
				{
					LoadConfigSection(xml);

					if (xml.NodeType != XmlNodeType.Element)
						MoveToNextElement(xml);
				}
			}
			catch (ConfigurationErrorsException)
			{
				throw;
			}
			catch (Exception ex)
			{
				throw new ConfigurationErrorsException(Properties.Resources.ConfigurationFailure, ex);
			}
		}

		//
		// Reads the <configSections> children and creates an empty ConfigurationSection instance 
		// of the correct type for each <section> element.
		//
		private void LoadConfigSections(XmlReader xml)
		{
			MoveToNextElement(xml);				// Move to <configSections>, if present
			if (!xml.EOF && xml.Name != "configSections")
				throw new ConfigurationErrorsException(Properties.Resources.ConfigurationFailure);

			int depth = xml.Depth;				// Depth of <configSections>

			while (MoveToNextElement(xml) && xml.Depth > depth)
			{
				if (xml.Name != "section")
					throw new ConfigurationErrorsException(Properties.Resources.ConfigurationFailure);

				string name = null;
				string typeString = null;

				if (xml.HasAttributes)
				{
					while (xml.MoveToNextAttribute())
					{
						string attributeName = xml.Name;
						xml.ReadAttributeValue();

						switch (attributeName)
						{
							case "name":
								name = xml.Value;
								break;

							case "type":
								typeString = xml.Value;
								break;

							default:
								break;
								// Ignore unknown attributes.
								//throw new ConfigurationErrorsException(Properties.Resources.ConfigurationFailure);
						}
					}

					if (name != null)
						name = name.Trim();
					if (typeString != null)
						typeString = typeString.Trim();

					if (String.IsNullOrEmpty(name))
					{
						throw new ConfigurationErrorsException(String.Format(
							CultureInfo.CurrentCulture,
							Properties.Resources.MissingAttribute,
							"name"));
					}
					
					if (String.IsNullOrEmpty(typeString))
					{
						throw new ConfigurationErrorsException(String.Format(
							CultureInfo.CurrentCulture,
							Properties.Resources.MissingAttribute,
							"type"));
					}

					Add(name, typeString, null);
				}
			}
		}

		/// <summary>
		///		Reads the 
		/// </summary>
		/// <param name="xml"></param>
		private void LoadConfigSection(XmlReader xml)
		{
			ConfigurationSectionInfo info;
			if (xml.Name == "EncryptedSection")
				LoadEncryptedSection(xml);
			else
			{
				Sections.TryGetValue(xml.Name, out info);
				if (info != null)
				{
					if (info.SectionXml != null)
						throw new ConfigurationErrorsException(Properties.Resources.DuplicationSectionData);
					info.SectionXml = xml.ReadOuterXml();
				}
				else
				{
					xml.Skip();
				}
			}

			if (xml.NodeType != XmlNodeType.Element)
				MoveToNextElement(xml);
		}

		private void LoadEncryptedSection(XmlReader xml)
		{
			xml.MoveToFirstAttribute();

			if (xml.Name != "name")
				throw new ConfigurationErrorsException(Properties.Resources.EncryptedMissingName);

			xml.ReadAttributeValue();
			string name = xml.Value;

			xml.MoveToElement();
			string value = xml.ReadInnerXml();

			ConfigurationSectionInfo info;
			Sections.TryGetValue(name, out info);
			if (info != null)
			{
				info.SectionXml = value;
				info.IsEncrypted = true;
			}
		}

		private static bool MoveToNextElement(XmlReader xml)
		{
			if (xml.NodeType == XmlNodeType.Element)
				xml.Read();

			while (!xml.EOF && xml.NodeType != XmlNodeType.Element)
				xml.Read();

			return !xml.EOF && xml.NodeType == XmlNodeType.Element;
		}

	}
}
