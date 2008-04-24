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
	///		Allows you to mark properties in subclasses of <see cref="ConfigurationElement"/> so they
	///		will automatically be deserialized by the <see cref="Configuration"/> class.
	/// </summary>
	[AttributeUsage(AttributeTargets.Property)]
	public sealed class ConfigurationPropertyAttribute : Attribute
	{
		private string name;
		private bool isRequired;
		private bool isKey;
		private bool isDefaultCollection;

		public ConfigurationPropertyAttribute(string name)
		{
			this.name = name;
		}

		/// <summary>
		///		The name to use for this property in the <see cref="ConfigurationElement"/>'s
		///		collection of elements.
		/// </summary>
		public string Name
		{
			get { return name; }
		}

		/// <summary>
		///		Provided for compatibility with the full framework, but ignored.
		/// </summary>
		public bool IsDefaultCollection
		{
			get { return isDefaultCollection; }
			set { isDefaultCollection = value; }
		}

		/// <summary>
		///		Provided for compatibility with the full framework, but ignored.
		/// </summary>
		public bool IsKey
		{
			get { return isKey; }
			set { isKey = value; }
		}

		/// <summary>
		///		When your class is deserized from the configuration XML, the <see cref="ConfigurationElemet"/>
		///		class will make sure this property is present in the data and throw an error if it isn't.
		/// </summary>
		public bool IsRequired
		{
			get { return isRequired; }
			set { isRequired = value; }
		}
	
	}
}
