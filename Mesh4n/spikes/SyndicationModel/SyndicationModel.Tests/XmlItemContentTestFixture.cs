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
	[TestClass]
	public class XmlItemContentTestFixture
	{
		public XmlItemContentTestFixture()
		{
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowExceptionWhenNullReader()
		{
			new XmlItemContent((XmlReader)null);
		}

		[TestMethod]
		[ExpectedException(typeof(InvalidOperationException))]
		public void ShouldThrowExceptionWhenInvalidReaderPosition()
		{
			string xml = "<payload><innerElement></innerElement></payload>";

			XmlReader reader = XmlReader.Create(new StringReader(xml));
			while (reader.Read()) ;

			new XmlItemContent(reader);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowExceptionWhenNullExtensionData()
		{
			new XmlItemContent((ItemElementExtension)null);
		}

		[TestMethod]
		public void ShouldHaveDefaultType()
		{
			XmlItemContent content = new XmlItemContent(new Customer(), null);
			Assert.AreEqual("text/xml", content.Type);
		}

		[TestMethod]
		public void ShouldCreateItemExtension()
		{
			XmlItemContent content = new XmlItemContent(new Customer(), new XmlSerializer(typeof(Customer)));
			Assert.IsNotNull(content.Extension);
		}

		[TestMethod]
		public void ShouldCloneContent()
		{
			XmlItemContent content = new XmlItemContent(new Customer(), new XmlSerializer(typeof(Customer)), "type");
			XmlItemContent cloned = (XmlItemContent)content.Clone();

			Assert.AreEqual(content.Type, cloned.Type);
			Assert.AreEqual(content.Extension, cloned.Extension);
		}

		[TestMethod]
		public void ShouldReadExtensionAttributes()
		{
			string xml = "<payload type='text' otherAttribute='foo'><innerElement></innerElement></payload>";

			XmlReader reader = XmlReader.Create(new StringReader(xml));
			XmlItemContent content = new XmlItemContent(reader);

			Assert.AreEqual("text", content.Type);
			Assert.IsTrue(content.AttributeExtensions.ContainsKey(new XmlQualifiedName("otherAttribute")));
			Assert.AreEqual("foo", content.AttributeExtensions[new XmlQualifiedName("otherAttribute")]);
		}

		[TestMethod]
		public void ShouldGetReaderAtContent()
		{
			string xml = "<payload type='text' otherAttribute='foo'><innerElement></innerElement></payload>";
			XmlReader reader = XmlReader.Create(new StringReader(xml));
			XmlItemContent content = new XmlItemContent(reader);

			XmlReader anotherReader = content.GetReaderAtContent();
			Assert.IsNotNull(anotherReader);
			Assert.IsTrue(anotherReader.IsStartElement());
			Assert.AreEqual("payload", anotherReader.LocalName);
		}

		[TestMethod]
		public void ShouldReadExtensionData()
		{
			Customer customer = new Customer { FullName = "Foo Bar" };
			XmlItemContent content = new XmlItemContent(customer, null);

			Customer customerExt = content.ReadContent<Customer>();
			
			Assert.AreEqual(customer.FullName, customerExt.FullName);
		}

		[TestMethod]
		public void ShouldReadExtensionDataFromXml()
		{
			Customer customer = new Customer { FullName = "Foo Bar" };
			XmlSerializer serializer = new XmlSerializer(typeof(Customer));

			StringWriter sw = new StringWriter();
			XmlWriter xw = XmlWriter.Create(sw);

			serializer.Serialize(xw, customer);

			xw.Close();

			XmlReader xr = XmlReader.Create(new StringReader(sw.ToString()));

			XmlItemContent content = new XmlItemContent(xr);

			Customer customerExt = content.ReadContent<Customer>(new XmlSerializer(typeof(Customer)));

			Assert.AreEqual(customer.FullName, customerExt.FullName);
		}

		[TestMethod]
		public void ShouldWriteContentFromXml()
		{
			string xml = "<x:payload xmlns:x=\"test\"><innerElement></innerElement></x:payload>";
			string expectedXml = "<outerElement type=\"text/xml\"><x:payload xmlns:x=\"test\"><innerElement></innerElement></x:payload></outerElement>";

			XmlReader reader = XmlReader.Create(new StringReader(xml));

			XmlItemContent content = new XmlItemContent(reader);

			XmlWriterSettings settings = new XmlWriterSettings();
			settings.OmitXmlDeclaration = true;
			settings.CloseOutput = true;

			StringWriter sw = new StringWriter();
			XmlWriter xw = XmlWriter.Create(sw, settings);

			content.WriteTo(xw, "outerElement", "");

			xw.Close();

			Assert.AreEqual(expectedXml, sw.ToString());
		}

		[TestMethod]
		public void ShouldWriteContentFromExtensionData()
		{
			string expectedXml = "<outerElement type=\"text/xml\"><Customer xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><FullName>Foo Bar</FullName></Customer></outerElement>";

			Customer customer = new Customer { FullName = "Foo Bar" };

			XmlItemContent content = new XmlItemContent(customer, null);

			XmlWriterSettings settings = new XmlWriterSettings();
			settings.OmitXmlDeclaration = true;
			settings.CloseOutput = true;

			StringWriter sw = new StringWriter();
			XmlWriter xw = XmlWriter.Create(sw, settings);

			content.WriteTo(xw, "outerElement", "");

			xw.Close();
			
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
