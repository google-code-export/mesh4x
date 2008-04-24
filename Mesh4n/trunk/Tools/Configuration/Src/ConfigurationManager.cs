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
using System.Reflection;
using System.IO;

namespace Microsoft.Practices.Mobile.Configuration
{
	//
	// This is a replacement class for the class in the full framework in System.Configuration,
	// which isn't present in the compact framework. Here is an overview of how this class
	// behaves.
	//
	//	1.	The App.config file will be loaded and parsed.
	//	2.	When a configuration section is requested, we'll return an existing instance,
	//		if we have one. Otherwise, we'll create a new instance as follows:
	//			1.	We'll find the matching <section> element in the configuration/configSections
	//				path of the XML document.
	//			2.	We'll use the type and assembly information to create a new instance of the
	//				class using the information in the <section> element.
	//			3.	We'll find the XML node that matches the <section>'s name attribute by looking
	//				directly under the root.
	//			4.	We'll pass this XML node to the instance of the object that we just created.
	//
	public static class ConfigurationManager
	{
		private static ProtectedConfigurationProvider protectedProvider;
		private static object lockObject = new Object();
		private static Configuration configuration;
		private static bool attemptedLoad;

		/// <summary>
		///		This method provides you access to a "section" of the App.config file. This is done
		///		with two pieces. First, a section element in configuration/configSections defines
		///		the type and assembly for a subclass of ConfigurationSection that can process
		///		the XML for a "section". Second, an XML node (with children) with the same name
		///		as the section that your subclass of ConfigurationSection will process.
		/// </summary>
		/// <param name="sectionName">
		///		Name of the section that we want to retrieve. There must be a matching &lt;section&gt;
		///		element under configuration/configSections, as well as an XML element with this name
		///		under the XML root, in the App.config file.
		/// </param>
		/// <returns>
		///		A subclass of ConfigurationSection that you use to parse your custom section.
		///	</returns>
		///	<exception cref="ConfigurationErrorsException">
		///		Throws this exception if there was an error creating the instance of the section.
		///	</exception>
		public static ConfigurationSection GetSection(string sectionName)
		{
			Guard.ArgumentNotNullOrEmptyString(sectionName, "sectionName");

			AttemptConfigFileLoad();

			if (configuration != null)
				 return configuration.GetSection(sectionName);
			else
				return null;
		}

		/// <summary>
		///		This method is provided primarliy to help with unit testing. It allows us to reload
		///		configuration data each time so we can check for errors that occur during loading.
		/// </summary>
		public static void ClearCache()
		{
			attemptedLoad = false;
		}

		/// <summary>
		///		Gets or sets the Configuration object that provides the sections. You can set this at
		///		application start if you want the static methods of this class to read data from a
		///		source other than App.Config.
		/// </summary>
		public static Configuration Configuration
		{
			get { return configuration; }
			set { configuration = value; }
		}

		/// <summary>
		///		Given a string that contains type information, create an instance of the object, which
		///		must be a subclass of <see cref="ConfigurationSection"/>.
		/// </summary>
		/// <param name="typeString">The string that contains the type information for a class.</param>
		/// <returns>A new instance of the type.</returns>
		/// <exception cref="ConfigurationErrorsException">
		///		Throws this exception if the type string is null or empty. Or if there was an error
		///		creating an instance of the type.
		/// </exception>
		public static ConfigurationSection CreateFromTypeString(string typeString)
		{
			if (typeString == null || typeString.Trim().Length == 0)
				throw new ConfigurationErrorsException(Properties.Resources.ConfigurationFailure);

			try
			{
				Type sectionType = Type.GetType(typeString);

				if (sectionType == null)
				{
					string[] sectionTypeInfo = typeString.Split(',');

					//
					// The Assembly.Load on the compact framework fails if there is a space in front
					// of a name, so we have to trim extra spaces.
					//
					sectionTypeInfo[0] = sectionTypeInfo[0].Trim();
					sectionTypeInfo[1] = sectionTypeInfo[1].Trim();

					Assembly module = Assembly.Load(sectionTypeInfo[1]);
					sectionType = module.GetType(sectionTypeInfo[0]);
					if (sectionType == null)
					{
						throw new ConfigurationErrorsException(String.Format(
							System.Globalization.CultureInfo.CurrentCulture,
							Properties.Resources.TypeLoadError, typeString));
					}
				}
					
				object sectionTypeInstance = Activator.CreateInstance(sectionType);
				return (ConfigurationSection)sectionTypeInstance;
			}
			catch (Exception ex)
			{
				throw new ConfigurationErrorsException(ex.Message);
			}
		}

		/// <summary>
		///		Creates a new <see cref="Configuration"/> instance loaded with the data in <paramref name="xml"/>.
		/// </summary>
		/// <param name="xml">
		///		Source of the configuration data, which must have the same format as the App.Config file.
		/// </param>
		/// <returns>A new <see cref="Configuration"/> instance.</returns>
		public static Configuration GetConfiguration(XmlReader xml)
		{
			Guard.ArgumentNotNull(xml, "xml");

			return new Configuration(xml);
		}

		/// <summary>
		///		Creates an instance using <paramref name="sectionType"/> as the type. This type must be
		///		a subclass of <see cref="ConfigurationSection"/>. It then loads the XML section information
		///		into this new instance.
		/// </summary>
		/// <param name="sectionType">Type of the <see cref="ConfigurationSection"/> subclass to create.</param>
		/// <param name="xml">
		///		Source of the XML section data used to deserialize the subclass of <see cref="ConfigurationSection"/>.
		/// </param>
		/// <returns>A new section with the section data loaded.</returns>
		/// <exception cref="ConfigurationErrorsException">
		///		Throws this exception if there was an error creating or deserializing the type.
		/// </exception>
		public static ConfigurationSection GetSectionFromXml(Type sectionType, XmlReader xml)
		{
			Guard.ArgumentNotNull(sectionType, "sectionType");
			Guard.ArgumentNotNull(xml, "xml");

			return GetSectionFromXml(sectionType, xml, protectedProvider);
		}

		/// <summary>
		///		Creates an instance using <paramref name="sectionType"/> as the type. This type must be
		///		a subclass of <see cref="ConfigurationSection"/>. It then loads the XML section information
		///		into this new instance.
		/// </summary>
		/// <param name="sectionType">Type of the <see cref="ConfigurationSection"/> subclass to create.</param>
		/// <param name="xml">
		///		Source of the XML section data used to deserialize the subclass of <see cref="ConfigurationSection"/>.
		/// </param>
		/// <param name="provider">The provider that will be used to decrypte sections that are encrypted.</param>
		/// <returns>A new section with the section data loaded.</returns>
		/// <exception cref="ConfigurationErrorsException">
		///		Throws this exception if there was an error creating or deserializing the type.
		/// </exception>
		public static ConfigurationSection GetSectionFromXml(Type sectionType, XmlReader xml, ProtectedConfigurationProvider provider)
		{
			Guard.ArgumentNotNull(sectionType, "sectionType");
			Guard.ArgumentNotNull(xml, "xml");

			try
			{
				ConfigurationSection configSection = (ConfigurationSection)Activator.CreateInstance(sectionType);
				GetSectionFromXml(configSection, xml, provider);

				return configSection;
			}
			catch (Exception ex)
			{
				throw new ConfigurationErrorsException(ex.Message);
			}
		}

		/// <summary>
		///		Initializes an existing instance of <see cref="ConfigurationSection"/> with the section
		///		data from <paramref name="xml"/>.
		/// </summary>
		/// <param name="section">The existing instance to deserialize.</param>
		/// <param name="xml">The section data to load into <paramref name="section"/>.</param>
		/// <exception cref="ConfigurationErrorsException">
		///		Throws this exception if there was an error creating or deserializing the type.
		/// </exception>
		public static void GetSectionFromXml(ConfigurationSection section, XmlReader xml)
		{
			GetSectionFromXml(section, xml, protectedProvider);
		}

		/// <summary>
		///		Initializes an existing instance of <see cref="ConfigurationSection"/> with the section
		///		data from <paramref name="xml"/>.
		/// </summary>
		/// <param name="section">The existing instance to deserialize.</param>
		/// <param name="xml">The section data to load into <paramref name="section"/>.</param>
		/// <param name="provider">The provider that will be used to decrypte sections that are encrypted.</param>
		/// <exception cref="ConfigurationErrorsException">
		///		Throws this exception if there was an error creating or deserializing the type.
		/// </exception>
		public static void GetSectionFromXml(ConfigurationSection section, XmlReader xml, ProtectedConfigurationProvider provider)
		{
			Guard.ArgumentNotNull(section, "section");
			Guard.ArgumentNotNull(xml, "xml");

			try
			{
				while (!xml.EOF && xml.NodeType != XmlNodeType.Element)
					xml.Read();

				if (xml.Name == "EncryptedSection")
				{
					InitEncryptedSection(section, xml, provider);
				}
				else
					section.DeserializeElement(xml);
			}
			catch (Exception ex)
			{
				throw new ConfigurationErrorsException(ex.Message);
			}
		}

		/// <summary>
		///		Gets or sets the instance of <see cref="ProtectedConfigurationProvider"/> that handles
		///		decrypting encrypted sections in the config XML.
		/// </summary>
		public static ProtectedConfigurationProvider ProtectedConfigurationProvider
		{
			get { return protectedProvider; }
			set { protectedProvider = value; }
		}

		/// <summary>
		/// </summary>
		/// <param name="section"></param>
		/// <param name="xml"></param>
		private static void InitEncryptedSection(ConfigurationSection section, XmlReader xml, ProtectedConfigurationProvider provider)
		{
			xml.MoveToElement();
			string encryptedXml = xml.ReadInnerXml();

			string sectionData = provider.Decrypt(encryptedXml);
			section.DeserializeElement(new XmlTextReader(new StringReader(sectionData)));
		}

		private static void AttemptConfigFileLoad()
		{
			lock (lockObject)
			{
				if (attemptedLoad)
					return;

				try
				{
					LoadConfigurationFile();
				}
				finally
				{
					attemptedLoad = true;
				}
			}
		}

		private static void LoadConfigurationFile()
		{
			string filename = Path.Combine(DirectoryUtils.BaseDirectory, "App.config");
			if (File.Exists(filename))
			{
				using (XmlReader reader = XmlReader.Create(filename))
				{
					configuration = new Configuration(reader);
				}
			}
		}
	}
}
