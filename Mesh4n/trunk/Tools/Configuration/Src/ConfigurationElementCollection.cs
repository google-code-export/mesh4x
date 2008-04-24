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
using System.Collections.Specialized;
using System.Xml;

namespace Microsoft.Practices.Mobile.Configuration
{
	/// <summary>
	///		Manages a list of <see cref="ConfigurationElement"/> instances.
	/// </summary>
	public abstract class ConfigurationElementCollection : ConfigurationElement, IEnumerable
	{
		/// <summary>
		///		Override this method to create a new elemnt of the correct type for your collection
		///		subclass.
		/// </summary>
		/// <returns>
		///		A new instance of the <see cref="ConfigurationElement"/> subclass that is the correct
		///		type for your collection.
		/// </returns>
		protected abstract ConfigurationElement CreateNewElement();

		protected virtual ConfigurationElement CreateNewElement(string elementName, XmlReader reader)
		{
			return CreateNewElement();
		}

		/// <summary>
		///		Override this method to return a value that will identify an element. This string
		///		is the same value you'll use in the indexer to retrieve an element.
		/// </summary>
		/// <param name="element"></param>
		/// <returns></returns>
		protected abstract Object GetElementKey(ConfigurationElement element);

		private HybridDictionary elements = new HybridDictionary();

		/// <summary>
		///		Provides access to the list of elements in this collection.
		/// </summary>
		/// <returns>A new enumerator.</returns>
		public IEnumerator GetEnumerator()
		{
			return elements.Values.GetEnumerator();
		}

		/// <summary>
		///		This method is called by the base class when it encounters an element it doesn't
		///		recognize during deserialization of the XML. This method handles the add element 
		///		to add an element to this collection.
		/// </summary>
		/// <param name="elementName">Name of the element. This method only looks for "add".</param>
		/// <param name="reader">The XML data for this element.</param>
		/// <returns>
		///		true if this method processed the element, and false if it didn't recognize the
		///		element.
		///	</returns>
		///	<exception cref="ConfigurationErrorsException">
		///		Thrown if there is an error processing the XML for this element.
		///	</exception>
		protected override bool OnDeserializeUnrecognizedElement(string elementName, XmlReader reader)
		{
			if (elementName == "add")
			{
				ConfigurationElement element = CreateNewElement(elementName, reader);
				element.DeserializeElement(reader);
				BaseAdd(element);
				return true;
			}

			return false;
		}

		/// <summary>
		///		Gets the number of elements in this collection.
		/// </summary>
		public int Count
		{
			get { return elements.Count; }
		}

		/// <summary>
		///		Gets an element from the collection.
		/// </summary>
		/// <param name="key">The name of the element to retrieve.</param>
		/// <returns>A <see cref="ConfigurationElement"/> instance.</returns>
		protected internal ConfigurationElement BaseGet(object key)
		{
			return (ConfigurationElement) elements[key];
		}

		/// <summary>
		///		Call this method to add an element to this collection.
		/// </summary>
		/// <param name="element">The element you want to add to the collection.</param>
		protected virtual void BaseAdd(ConfigurationElement element)
		{
			elements[GetElementKey(element)] = element;
		}

		/// <summary>
		///		Call this method to remove an element from the list based on it's name.
		/// </summary>
		/// <param name="key">The name of the element to remove.</param>
		protected internal void BaseRemove(object key)
		{
			elements.Remove(key);
		}
	}
}
