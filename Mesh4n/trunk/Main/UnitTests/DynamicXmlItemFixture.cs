#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;
using System.Xml;

namespace SimpleSharing.Tests
{
	[TestClass]
	public class DynamicXmlItemFixture
	{
		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullTitleExpression()
		{
			new DynamicXmlItem(Guid.NewGuid().ToString(), null, "foo", 
				GetDummyPayload(), new object());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullDescriptionExpression()
		{
			new DynamicXmlItem(Guid.NewGuid().ToString(), "foo", null, 
				GetDummyPayload(), new object());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyTitleExpression()
		{
			new DynamicXmlItem(Guid.NewGuid().ToString(), "", "foo", 
				GetDummyPayload(), new object());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfEmptyDescriptionExpression()
		{
			new DynamicXmlItem(Guid.NewGuid().ToString(), "foo", "", 
				GetDummyPayload(), new object());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullDataExpression()
		{
			new DynamicXmlItem(Guid.NewGuid().ToString(), "foo", "foo",
				GetDummyPayload(), null);
		}

		[TestMethod]
		public void ShouldReturnSameValueIfNoExpression()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "title", 
				"description", GetDummyPayload(), new object());

			Assert.AreEqual("title", item.Title);
			Assert.AreEqual("description", item.Description);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfNoMemberFoundInTitleExpression()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "{foo}", 
				"description", GetDummyPayload(), new object());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfNoMemberFoundInDescriptionExpression()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "title", "{foo}",
				GetDummyPayload(), new object());
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowIfMemberIsMethodWithArgs()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "{GetValueWithArg}",
				"{GetValueWithArg}", GetDummyPayload(), new Data());
		}

		[TestMethod]
		public void ShouldEvaluateToFieldValue()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "{FieldValue}",
				"description", GetDummyPayload(), new Data());

			Assert.AreEqual("FieldValue", item.Title);
		}

		[TestMethod]
		public void ShouldEvaluateToPropertyValue()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "{PropertyValue}",
				"description", GetDummyPayload(), new Data());

			Assert.AreEqual("PropertyValue", item.Title);
		}

		[TestMethod]
		public void ShouldEvaluateToMethodValue()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "{MethodValue}", 
				"description", GetDummyPayload(), new Data());

			Assert.AreEqual("MethodValue", item.Title);
		}

		[TestMethod]
		public void ShouldEvaluateToMethodOverloadWithoutArgument()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "{MethodOverload}",
				"description", GetDummyPayload(), new Data());

			Assert.AreEqual("MethodOverload1", item.Title);
		}

		[ExpectedException(typeof(ArgumentException))]
		[TestMethod]
		public void ShouldThrowIfParameterlessMethodNotFound()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), "{MethodWithParameterOverload}",
				"description", GetDummyPayload(), new Data());
		}
		
		[TestMethod]
		public void ShouldConcatenateValues()
		{
			IXmlItem item = new DynamicXmlItem(Guid.NewGuid().ToString(), 
				"{FieldValue}, {PropertyValue} - {MethodValue}", "description", 
				GetDummyPayload(), new Data());

			Assert.AreEqual("FieldValue, PropertyValue - MethodValue", item.Title);
		}

		public class Data
		{
			public string FieldValue = "FieldValue";

			public string PropertyValue
			{
				get { return "PropertyValue"; }
			}

			public string MethodValue()
			{
				return "MethodValue";
			}

			public string GetValueWithArg(int id)
			{
				return "Foo";
			}

			public string MethodOverload()
			{
				return "MethodOverload1";
			}

			public string MethodOverload(string param)
			{
				return "MethodOverload2";
			}

			public string MethodWithParameterOverload(string param)
			{
				return "";
			}

			public string MethodWithParameterOverload(int param)
			{
				return "";
			}
		}

		private XmlElement GetDummyPayload()
		{
			XmlDocument doc = new XmlDocument();
			doc.LoadXml("<foo/>");

			return doc.DocumentElement;
		}
	}
}
