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

namespace Microsoft.Practices.Mobile.Configuration.Tests.Mocks
{
	class MockSeveralTypesSection : ConfigurationSection
	{
		[ConfigurationProperty("SeveralTypesSection", IsDefaultCollection = true)]
		public SeveralTypeElementCollection Items
		{
			get
			{
				return (SeveralTypeElementCollection)(this["SeveralTypesSection"]);
			}
		}
	}

	class SeveralTypeElementCollection : ConfigurationElementCollection
	{
		protected override ConfigurationElement CreateNewElement()
		{
			return new MockSeveralAttributeTypesElement();
		}

		protected override object GetElementKey(ConfigurationElement element)
		{
			MockSeveralAttributeTypesElement e = (MockSeveralAttributeTypesElement)element;

			return e.Name;
		}

		public MockSeveralAttributeTypesElement GetItem(string name)
		{
			return (MockSeveralAttributeTypesElement)BaseGet(name);
		}
	}

	public class MockSeveralAttributeTypesElement : ConfigurationElement
	{
		[ConfigurationProperty("name")]
		public string Name
		{
			get { return (string)this["name"]; }
		}

		[ConfigurationProperty("Int16Attribute")]
		public Int16 Int16Attribute
		{
			get { return (Int16)this["Int16Attribute"]; }
		}

		[ConfigurationProperty("Int32Attribute")]
		public Int32 Int32Attribute
		{
			get { return (Int32)this["Int32Attribute"]; }
		}

		[ConfigurationProperty("Int64Attribute")]
		public Int64 Int64Attribute
		{
			get { return (Int64)this["Int64Attribute"]; }
		}

		[ConfigurationProperty("UInt16Attribute")]
		public UInt16 Uint16Attribute
		{
			get { return (UInt16)this["UInt16Attribute"]; }
		}

		[ConfigurationProperty("UInt32Attribute")]
		public UInt32 Uint32Attribute
		{
			get { return (UInt32)this["UInt32Attribute"]; }
		}

		[ConfigurationProperty("UInt64Attribute")]
		public UInt64 Uint64Attribute
		{
			get { return (UInt64)this["UInt64Attribute"]; }
		}

		[ConfigurationProperty("CharAttribute")]
		public char CharAttribute
		{
			get { return (char)this["CharAttribute"]; }
		}

		[ConfigurationProperty("DoubleAttribute")]
		public double DoubleAttribute
		{
			get { return (double)this["DoubleAttribute"]; }
		}

		[ConfigurationProperty("ByteAttribute")]
		public byte ByteAttribute
		{
			get { return (byte)this["ByteAttribute"]; }
		}

		[ConfigurationProperty("DateTimeAttribute")]
		public DateTime DateTimeAttribute
		{
			get { return (DateTime)this["DateTimeAttribute"]; }
		}

		[ConfigurationProperty("StringAttribute")]
		public string StringAttribute
		{
			get { return (string)this["StringAttribute"]; }
		}

		[ConfigurationProperty("ArrayAttribute")]
		public Array ArrayAttribute
		{
			get { return null; }
		}
	}
}
