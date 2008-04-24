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
using System.Configuration;
using Microsoft.Practices.Mobile.Configuration;

namespace Microsoft.Practices.Mobile.Configuration.Tests.Mocks
{
	/// <summary>
	/// List of services configured for the <see cref="CabApplication{TWorkItem}"/>.
	/// </summary>
	//[ConfigurationCollection(typeof (ServiceElement))]
	public class MockServiceElementCollection : ConfigurationElementCollection, IEnumerable<MockServiceElement>
	{
		/// <summary>
		/// Creates a new <see cref="ServiceElement"/>.
		/// </summary>
		protected override ConfigurationElement CreateNewElement()
		{
			return new MockServiceElement();
		}

		/// <summary>
		/// Retrieves the key for the configuration element.
		/// </summary>
		protected override object GetElementKey(ConfigurationElement element)
		{
			return ((MockServiceElement)element).ServiceType;
		}

		/// <summary>
		/// Provides access to the service elements by the type of service registered.
		/// </summary>
		public MockServiceElement this[Type serviceType]
		{
			get { return (MockServiceElement)base.BaseGet(serviceType); }
			set
			{
				if (base.BaseGet(serviceType) != null)
				{
					base.BaseRemove(serviceType);
				}
				base.BaseAdd(value);
			}
		}

		/// <summary>
		/// Adds a new service to the collection.
		/// </summary>
		public void Add(MockServiceElement service)
		{
			base.BaseAdd(service);
		}

		/// <summary>
		/// Removes a service from the collection.
		/// </summary>
		public void Remove(Type serviceType)
		{
			base.BaseRemove(serviceType);
		}

		#region IEnumerable<ServiceElement> Members

		/// <summary>
		/// Enumerates the services in the collection.
		/// </summary>
		public new IEnumerator<MockServiceElement> GetEnumerator()
		{
			int count = base.Count;
			for (int i = 0; i < count; i++)
			{
				yield return (MockServiceElement)base.BaseGet(i);
			}
		}

		#endregion
	}
}