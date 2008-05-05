using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Xml;
using System.IO;
using System.Xml.Serialization;

namespace SyndicationModel.Tests
{
	/// <summary>
	/// </summary>
	[TestClass]
	public class ItemElementExtensionTestFixture
	{
		public ItemElementExtensionTestFixture()
		{
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowExceptionWhenNullReader()
		{
			new ItemElementExtension(null);
		}

		[TestMethod]
		[ExpectedException(typeof(InvalidOperationException))]
		public void ShouldThrowExceptionWhenInvalidReaderPosition()
		{
			string xml = "<payload><innerElement></innerElement></payload>";

			XmlReader reader = XmlReader.Create(new StringReader(xml));
			while (reader.Read()) ;

			new ItemElementExtension(reader);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowExceptionWhenNullExtension()
		{
			new ItemElementExtension(null, null);
		}

		[TestMethod]
		public void ShouldGetExtensionData()
		{
			Customer customer = new Customer { FullName="Foo Bar" };
			ItemElementExtension extension = new ItemElementExtension(customer, null);

			Customer customerExt = extension.GetObject<Customer>(new XmlSerializer(typeof(Customer)));

			Assert.AreEqual(customer.FullName, customerExt.FullName);
		}

		[TestMethod]
		public void ShouldGetExtensionDataFromXml()
		{
			Customer customer = new Customer { FullName = "Foo Bar" };
			XmlSerializer serializer = new XmlSerializer(typeof(Customer));

			StringWriter sw = new StringWriter();
			XmlWriter xw = XmlWriter.Create(sw);

			serializer.Serialize(xw, customer);

			xw.Close();

			XmlReader xr = XmlReader.Create(new StringReader(sw.ToString()));

			ItemElementExtension extension = new ItemElementExtension(xr);

			Customer customerExt = extension.GetObject<Customer>(new XmlSerializer(typeof(Customer)));

			Assert.AreEqual(customer.FullName, customerExt.FullName);
		}

		[TestMethod]
		public void ShouldGetOuternameAndNamespaceFromReader()
		{
			string xml = "<x:payload xmlns:x='test'><innerElement></innerElement></x:payload>";

			XmlReader reader = XmlReader.Create(new StringReader(xml));

			ItemElementExtension extension = new ItemElementExtension(reader);
			
			Assert.AreEqual("payload", extension.OuterName);
			Assert.AreEqual("test", extension.OuterNamespace);
		}

		[TestMethod]
		public void ShouldGetOuternameAndNamespaceFromExtensionData()
		{
			Customer customer = new Customer { FullName = "Foo Bar" };
			ItemElementExtension extension = new ItemElementExtension(customer, null);

			Assert.AreEqual("Customer", extension.OuterName);
			Assert.AreEqual("", extension.OuterNamespace);
		}

		[TestMethod]
		public void ShouldWriteXml()
		{
			string xml = "<x:payload xmlns:x=\"test\"><innerElement></innerElement></x:payload>";

			XmlReader reader = XmlReader.Create(new StringReader(xml));

			ItemElementExtension extension = new ItemElementExtension(reader);

			XmlWriterSettings settings = new XmlWriterSettings();
			settings.OmitXmlDeclaration = true;
			settings.CloseOutput = true;

			StringWriter sw = new StringWriter();
			XmlWriter xw = XmlWriter.Create(sw, settings);

			extension.WriteTo(xw);

			xw.Close();

			Assert.AreEqual(xml, sw.ToString());
		}

		[TestMethod]
		public void ShouldWriteXmlFromExtensionData()
		{
			Customer customer = new Customer { FullName = "Foo Bar" };

			ItemElementExtension extension = new ItemElementExtension(customer, null);

			XmlWriterSettings settings = new XmlWriterSettings();
			settings.OmitXmlDeclaration = true;
			settings.CloseOutput = true;

			StringWriter sw = new StringWriter();
			XmlWriter xw = XmlWriter.Create(sw, settings);

			extension.WriteTo(xw);

			xw.Close();

			string expectedXml = "<Customer xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><FullName>Foo Bar</FullName></Customer>";
			Assert.AreEqual(expectedXml, sw.ToString());
		}
		
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
