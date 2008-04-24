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

			  //<Endpoints>
			  //  <EndpointItems>
			  //    <add Name="MyHost=" Address="http://default/MyHost" UserName="defaultUser" Password="4444">
			  //      <NetworkItems>
			  //        <add Name="Work" Address="http://work/MyHost" UserName="chris" Password="3333"/>
			  //      </NetworkItems>
			  //    </add>
			  //    <add Name="NoDefault="/>
			  //  </EndpointItems>
			  //</Endpoints>

namespace Microsoft.Practices.Mobile.Configuration.Tests.Mocks
{
	public class MockEndpointSection : ConfigurationSection
	{
		[ConfigurationProperty("EndpointItems")]
		public MockEndpointsElementCollection EndpointsItems
		{
			get { return (MockEndpointsElementCollection)(this["EndpointItems"]); }
		}
	}

	public class MockEndpointsElementCollection : ConfigurationElementCollection
	{
		protected override ConfigurationElement CreateNewElement()
		{
			return new MockEndpointsElement();
		}

		protected override object GetElementKey(ConfigurationElement element)
		{
			return ((MockEndpointsElement) element).Name;
		}

		public MockEndpointsElement GetEndpoint(string name)
		{
			return (MockEndpointsElement) (BaseGet(name));
		}
	}

	public class MockEndpointsElement : ConfigurationElement
	{
		[ConfigurationProperty("Name", IsRequired = true, IsKey = true)]
		public string Name
		{
			get { return (string)this["Name"]; }
			set { this["Name"] = value; }
		}

		[ConfigurationProperty("Address", IsRequired = false)]
		public string Address
		{
			get { return (string)this["Address"]; }
			set { this["Address"] = value; }
		}

		[ConfigurationProperty("NetworkItems", IsRequired = false)]
		public MockNetworksCollection Networks
		{
			get { return (MockNetworksCollection)this["NetworkItems"]; }
			set { this["NetworkItems"] = value; }
		}
	}

	public class MockNetworksCollection : ConfigurationElementCollection
	{
		protected override ConfigurationElement CreateNewElement()
		{
			return new MockNetworkElement();
		}

		protected override object GetElementKey(ConfigurationElement element)
		{
			return ((MockNetworkElement)element).Name;
		}

		public MockNetworkElement GetNetwork(string name)
		{
			return (MockNetworkElement)BaseGet(name);
		}
	}

	public class MockNetworkElement : ConfigurationElement
	{
		[ConfigurationProperty("Name", IsRequired = true, IsKey = true)]
		public string Name
		{
			get { return (string)this["Name"]; }
			set { this["Name"] = value; }
		}
		[ConfigurationProperty("Address", IsRequired = true)]
		public string Address
		{
			get { return (string)this["Address"]; }
			set { this["Address"] = value; }
		}
	}
}
