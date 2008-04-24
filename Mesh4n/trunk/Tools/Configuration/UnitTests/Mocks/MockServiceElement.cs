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
using System.ComponentModel;
using System.Configuration;
using Microsoft.Practices.Mobile.Configuration;

namespace Microsoft.Practices.Mobile.Configuration.Tests.Mocks
{
	/// <summary>
	/// Contains the definition of a service.
	/// </summary>
	public class MockServiceElement : MockParametersElement
	{
		/// <summary>
		/// Optional type used to expose the service. If not provided, the 
		/// <see cref="InstanceType"/> will be used to publish the service.
		/// </summary>
		[ConfigurationProperty("serviceType", IsKey = true, IsRequired = true)]
		//[TypeConverter(typeof(TypeNameConverter))]
		public Type ServiceType
		{
			get { return (Type)this["serviceType"]; }
			set { this["serviceType"] = value; }
		}

		/// <summary>
		/// The type of the service implementation to instantiate.
		/// </summary>
		[ConfigurationProperty("instanceType", IsRequired = true)]
		//[TypeConverter(typeof(TypeNameConverter))]
		public Type InstanceType
		{
			get { return (Type)this["instanceType"]; }
			set { this["instanceType"] = value; }
		}
	}
}