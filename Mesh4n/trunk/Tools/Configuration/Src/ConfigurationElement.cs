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
using System.Collections.Specialized;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Reflection;
using System.Globalization;

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		<para>
	///			This class is a replacement for the System.Configuration class built into the full
	///			.NET framework. We've implemented just enough to supply the needs of CAB.
	///		</para>
	///		<para>
	///			There are basically two forms that an element can take: either a leaf node, which will
	///			have attributes, or collection, which contains child nodes. Leaf nodes should be
	///			subclasses of this class, whereas collections should be subclasses of
	///			ConfigurationElementCollection, which is a direct subclass of this class.
	///		</para>
	/// </summary>
	public abstract class ConfigurationElement
	{
		private Dictionary<string, object> values = new Dictionary<string, object>();
		private Dictionary<string, ConfigurationPropertyInfo> propInfos;

		private class ConfigurationPropertyInfo
		{
			public List<PropertyInfo> PropInfos;
			public ConfigurationPropertyAttribute PropAttrib;
			public ConfigurationElement Element;
			public bool valueSet;
		}

		protected ConfigurationElement()
		{
			PropertyInfo[] props = this.GetType().GetProperties();
			propInfos = new Dictionary<string, ConfigurationPropertyInfo>(props.Length);

			foreach (PropertyInfo propInfo in props)
				CreatePropertyInstance(propInfo);
		}

		/// <summary>
		///		Gets or sets a property of this element.
		/// </summary>
		/// <param name="propertyName">Name of the item.</param>
		/// <returns>The property, or null if the element doesn't exist.</returns>
		protected internal virtual object this[string propertyName]
		{
			get
			{
				if (values.ContainsKey(propertyName))
					return values[propertyName];
				else
					return null;
			}
			set { values[propertyName] = value; }
		}

		/// <summary>
		///		Called while deserializing a ConfigurationElement when it encounters an XML element
		///		that it doesn't recognize (for example, as a property of the subclass).
		/// </summary>
		/// <param name="elementName">Name of the element that it couldn't find</param>
		/// <param name="reader">The <see cref="XMLReader"/> positioned on the unknown element.</param>
		/// <returns>false indicates it is unknown and hasn't been read.</returns>
		protected virtual bool OnDeserializeUnrecognizedElement(string elementName, XmlReader reader)
		{
			return false;
		}

		//
		// This code finds all properties of this class marked with the [ConfigurationProperty]
		// attribute and creates an empty instance of each type that is a subclass of the
		// ConfigurationElement class. It then places this instance into our property bag, using
		// the value in the Name property of ConfigurationPropertyAttribute.
		//
		private void CreatePropertyInstance(PropertyInfo propInfo)
		{
			object[] attribs = propInfo.GetCustomAttributes(typeof(ConfigurationPropertyAttribute), true);

			for (int i = 0; i < attribs.Length; i++)
			{
				ConfigurationPropertyAttribute attrib = (ConfigurationPropertyAttribute)attribs[i];
				string elementName = attrib.Name;

				if (propInfos.ContainsKey(elementName))	// Is there already a property with this configuration attribute name?
				{										// Yes, don't need to create a new instance
					propInfos[elementName].PropInfos.Add(propInfo);
				}
				else
				{
					ConfigurationPropertyInfo info = new ConfigurationPropertyInfo();
					info.PropAttrib = attrib;
					info.PropInfos = new List<PropertyInfo>();
					info.PropInfos.Add(propInfo);

					if (propInfo.PropertyType.IsSubclassOf(typeof(ConfigurationElement)))
						info.Element = CreateConfigurationElementInstance(propInfo, elementName);
					this.propInfos.Add(elementName, info);
				}
			}
		}

		private ConfigurationElement CreateConfigurationElementInstance(PropertyInfo property, string elementName)
		{
			object value = Activator.CreateInstance(property.PropertyType);
			ConfigurationElement element = (ConfigurationElement)value;
			values[elementName] = element;
			return element;
		}

		//
		// This method takes as input XML that describes the configuration object and any child
		// elements. Here is the format for that data:
		//
		//	<node prop="value" .../>
		//
		//	<node prop="value" .../>
		//		<prop2>value</prop2>
		//		<child>...
		//	</node>
		//
		protected internal virtual void DeserializeElement(XmlReader xml)
		{
			while (!xml.EOF && xml.NodeType != XmlNodeType.Element)
				xml.Read();

			int startDepth = xml.Depth;

			if (xml.HasAttributes)
			{
				//
				// Get the list of all the attributes from the root element. Attributes
				// correspond to types that are not derived from ConfigurationElement.
				// Properties derived from ConfigurationElement are present as children
				// in the XML tree.
				//
				while (xml.MoveToNextAttribute())
				{
					string attributeName = xml.Name;
					string attributeValue = xml.ReadContentAsString();

					if (propInfos.ContainsKey(attributeName))
					{
						ConfigurationPropertyInfo info = propInfos[attributeName];
						LoadPropertyValue(info, attributeValue);
					}
					else
					{
						if (!OnDeserializeUnrecognizedAttribute(attributeName, attributeValue))
						{
							string message = Properties.Resources.UnrecognizedAttribute;
							message = String.Format(CultureInfo.CurrentCulture, message, attributeName);
							throw new ConfigurationErrorsException(message);
						}
					}
				}
			}

			// Move past element or last attribute value
			xml.Read();

			do
			{
				//
				// Load properties that are represented as child elements in the XML.
				//
				while (!xml.EOF && xml.NodeType != XmlNodeType.Element && xml.NodeType != XmlNodeType.EndElement)
					xml.Read();

				if (xml.Depth <= startDepth || xml.NodeType != XmlNodeType.Element)
					break;

				if (propInfos.ContainsKey(xml.Name))
				{
					ConfigurationPropertyInfo info = propInfos[xml.Name];
					if (info.Element != null)
						info.Element.DeserializeElement(xml);
					else
						LoadPropertyValue(info, xml.ReadElementContentAsString());

					info.valueSet = true;
				}
				else
				{
					if (!OnDeserializeUnrecognizedElement(xml.Name, xml))
					{
						string message = Properties.Resources.UnrecognizedElement;
						throw new ConfigurationErrorsException(String.Format(CultureInfo.CurrentCulture, message, xml.Name));
					}
				}

				if (xml.NodeType == XmlNodeType.EndElement)
					xml.Read();
			} while (true);

			//
			// Check that all required properties are present.
			//
			foreach (ConfigurationPropertyInfo propInfo in propInfos.Values)
			{
				if (propInfo.PropAttrib.IsRequired && !propInfo.valueSet)
				{
					string message = Properties.Resources.MissingElement;
					throw new ConfigurationErrorsException(String.Format(CultureInfo.CurrentCulture, message, propInfo.PropAttrib.Name));
				}
			}

			this.propInfos = null;					// Don't need once we're done with Init().
		}

		/// <summary>Gets a value indicating whether an unknown attribute is encountered during deserialization.</summary>
		/// <returns>true when an unknown attribute is encountered while deserializing.</returns>
		/// <param name="name">The name of the unrecognized attribute.</param>
		/// <param name="value">The value of the unrecognized attribute.</param>
		protected virtual bool OnDeserializeUnrecognizedAttribute(string name, string value)
		{
			return false;
		}

		/// <summary>
		/// Deserializes a scalar value present in the XML, converting it to the appropriate target type.
		/// </summary>
		/// <param name="targetType">The type for the returned value.</param>
		/// <param name="serializedValue">The string value to deserialize.</param>
		protected virtual object DeserializeValue(Type targetType, string serializedValue)
		{
			object value = null;

			if (targetType == typeof(string))
			{
				value = serializedValue;
			}
			else if (targetType == typeof(int))
			{
				value = Convert.ToInt32(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(Int16))
			{
				value = Convert.ToInt16(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(Int32))
			{
				value = Convert.ToInt32(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(Int64))
			{
				value = Convert.ToInt64(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(UInt16))
			{
				value = Convert.ToUInt16(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(UInt32))
			{
				value = Convert.ToUInt32(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(UInt64))
			{
				value = Convert.ToUInt64(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(char))
			{
				value = Convert.ToChar(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(double))
			{
				value = Convert.ToDouble(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(DateTime))
			{
				value = Convert.ToDateTime(serializedValue, CultureInfo.CurrentCulture);
			}
			else if (targetType == typeof(byte))
			{
				value = Convert.ToByte(serializedValue, CultureInfo.InvariantCulture);
			}
			else if (targetType == typeof(Type))
			{
				string[] typeInfo = serializedValue.Split(',');
				//
				// The Assembly.Load on the compact framework fails if there is a space in front
				// of a name, so we have to trim extra spaces.
				//
				typeInfo[0] = typeInfo[0].Trim();
				typeInfo[1] = typeInfo[1].Trim();
				value = Assembly.Load(typeInfo[1]).GetType(typeInfo[0]);
			}
			else if (targetType == typeof(bool))
			{
				value = Convert.ToBoolean(serializedValue, CultureInfo.InvariantCulture);
			}
			else
				throw new ConfigurationErrorsException(Properties.Resources.UnsupportedPropertyType);

			return value;
		}

		private void LoadPropertyValue(ConfigurationPropertyInfo info, string stringValue)
		{
			Type propertyType = info.PropInfos[0].PropertyType;
			object value = DeserializeValue(propertyType, stringValue);

			if (value != null)
			{
				if (!propertyType.IsAssignableFrom(value.GetType()))
				{
					throw new InvalidOperationException(String.Format(
						CultureInfo.CurrentCulture,
						Properties.Resources.WrongConvertedType,
						propertyType));
				}

				this[info.PropAttrib.Name] = value;
			}

			info.valueSet = true;
		}
	}
}
