using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Xml;
using System.Xml.Serialization;
using System.IO;

namespace SyndicationModel.Tests
{
	/// <summary>
	/// Summary description for ItemElementExtensionCollectionTestFixture
	/// </summary>
	[TestClass]
	public class ItemElementExtensionCollectionTestFixture
	{
		public ItemElementExtensionCollectionTestFixture()
		{
		}

		[TestMethod]
		public void ShouldPopulateItemsFromXml()
		{
			string xml = @"<payload>
					<innerElement1>1</innerElement1>
					<innerElement2>2</innerElement2>
					<innerElement3>3</innerElement3>
				</payload>";

			ItemElementExtensionCollection collection = new ItemElementExtensionCollection(xml);
			Assert.AreEqual(3, collection.Count);

			Assert.AreEqual("innerElement1", collection[0].OuterName);
			Assert.AreEqual("innerElement2", collection[1].OuterName);
			Assert.AreEqual("innerElement3", collection[2].OuterName);
		}

		[TestMethod]
		public void ShouldCloneCollection()
		{
			string xml = @"<payload>
					<innerElement1>1</innerElement1>
					<innerElement2>2</innerElement2>
					<innerElement3>3</innerElement3>
				</payload>";

			ItemElementExtensionCollection collection = new ItemElementExtensionCollection(xml);

			ItemElementExtensionCollection cloned = new ItemElementExtensionCollection(collection);
			Assert.AreEqual(3, cloned.Count);

			Assert.AreEqual("innerElement1", cloned[0].OuterName);
			Assert.AreEqual("innerElement2", cloned[1].OuterName);
			Assert.AreEqual("innerElement3", cloned[2].OuterName);
		}

		[TestMethod]
		public void ShouldAddItemFromXmlReader()
		{
			ItemElementExtensionCollection collection = new ItemElementExtensionCollection();

			string xml = "<payload><innerElement></innerElement></payload>";

			XmlReader reader = XmlReader.Create(new StringReader(xml));
			collection.Add(reader);

			Assert.AreEqual(1, collection.Count);
			Assert.AreEqual("payload", collection[0].OuterName);
		}

		[TestMethod]
		public void ShouldAddItemFromDataExtension()
		{
			ItemElementExtensionCollection collection = new ItemElementExtensionCollection();

			Customer customer = new Customer { FullName = "Foo Bar" };
			ItemElementExtension extension = new ItemElementExtension(customer, null);

			collection.Add(extension);

			Assert.AreEqual(1, collection.Count);
			Assert.AreEqual("Customer", collection[0].OuterName);
			Assert.AreEqual("urn:test", collection[0].OuterNamespace);
		}

		[XmlRoot(ElementName="Customer", Namespace="urn:test")]
		public class Customer
		{
			[XmlElement]
			public string FullName
			{
				get;
				set;
			}
		}
	}
}
