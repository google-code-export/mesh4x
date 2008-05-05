using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Xml;
using System.IO;

namespace SyndicationModel.Tests
{
	/// <summary>
	/// Summary description for UnitTest1
	/// </summary>
	[TestClass]
	public class ItemContentFixture
	{
		public ItemContentFixture()
		{
		}

		[TestMethod]
		public void ShouldKeepExtensions()
		{
			MockItemContent content = new MockItemContent();
			content.AttributeExtensions.Add(new System.Xml.XmlQualifiedName("test", "urn:test"), "value");

			Assert.AreEqual(1, content.AttributeExtensions.Count);
			Assert.IsTrue(content.AttributeExtensions.ContainsKey(new XmlQualifiedName("test", "urn:test")));
			Assert.AreEqual("value", content.AttributeExtensions[new XmlQualifiedName("test", "urn:test")]);
		}

		[TestMethod]
		public void ShouldCopyExtensions()
		{
			MockItemContent content = new MockItemContent();
			content.AttributeExtensions.Add(new System.Xml.XmlQualifiedName("test", "urn:test"), "value");

			ItemContent cloned = content.Clone();
			Assert.AreEqual(1, cloned.AttributeExtensions.Count);
			Assert.IsTrue(cloned.AttributeExtensions.ContainsKey(new XmlQualifiedName("test", "urn:test")));
			Assert.AreEqual("value", cloned.AttributeExtensions[new XmlQualifiedName("test", "urn:test")]);
		}

		[TestMethod]
		public void ShouldWriteContent()
		{
			XmlWriterSettings settings = new XmlWriterSettings();
			settings.CloseOutput = true;
			settings.OmitXmlDeclaration = true;

			StringWriter sw = new StringWriter();
			XmlWriter writer = XmlWriter.Create(sw, settings);

			MockItemContent content = new MockItemContent();
			content.AttributeExtensions.Add(new System.Xml.XmlQualifiedName("test", ""), "value");

			content.WriteTo(writer, "outerElement", "urn:outerNamespace");

			writer.Close();

			string expectedXml = "<outerElement type=\"text\" test=\"value\" xmlns=\"urn:outerNamespace\" />";

			Assert.AreEqual(expectedXml, sw.ToString());

		}

		class MockItemContent : ItemContent
		{
			public MockItemContent() : base()
			{
			}

			protected MockItemContent(ItemContent content)
				: base(content)
			{
			}

			public override ItemContent Clone()
			{
				return new MockItemContent(this);
			}

			protected override void WriteContentsTo(System.Xml.XmlWriter writer)
			{
			}

			public override string Type
			{
				get { return "text"; }
			}
		}

	}
}
