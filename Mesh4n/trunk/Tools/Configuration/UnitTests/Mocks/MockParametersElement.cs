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

using System.Collections.Specialized;
using System.Configuration;
using Microsoft.Practices.Mobile.Configuration;

namespace Microsoft.Practices.Mobile.Configuration.Tests.Mocks
{
	/// <summary>
	/// Base class for those configuration elements that support receiving 
	/// arbitrary name-value pairs through configuration.
	/// </summary>
	public abstract class MockParametersElement : ConfigurationElement
	{
		/// <summary>
		/// Properties we're creating on the fly as unrecognized attributes appear.
		/// </summary>
		//private ConfigurationPropertyCollection dynamicProperties = new ConfigurationPropertyCollection();

		private NameValueCollection configAttributes = new NameValueCollection();

		/// <summary>
		/// Constructor for use by derived classes.
		/// </summary>
		protected MockParametersElement()
		{
		}

		/// <summary>
		/// Retrieves the accumulated properties for the element, which include 
		/// the dynamically generated ones.
		/// </summary>
		//protected override ConfigurationPropertyCollection Properties
		//{
		//    get
		//    {
		//        ConfigurationPropertyCollection baseprops = base.Properties;
		//        foreach (ConfigurationProperty dynprop in dynamicProperties)
		//        {
		//            baseprops.Add(dynprop);
		//        }
		//        return baseprops;
		//    }
		//}

		/// <summary>
		/// Parameters received by the element as attributes in the configuration file.
		/// </summary>
		public NameValueCollection Parameters
		{
			get { return configAttributes; }
		}

		/// <summary>
		/// Create a new property on the fly for the attribute.
		/// </summary>
		//protected override bool OnDeserializeUnrecognizedAttribute(string name, string value)
		//{
		//    ConfigurationProperty dynprop = new ConfigurationProperty(name, typeof (string), value);
		//    dynamicProperties.Add(dynprop);
		//    this[dynprop] = value; // Add the value to values bag
		//    configAttributes.Add(name, value);
		//    return true;
		//}
	}
}