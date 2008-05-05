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
	/// Summary description for TextItemContentFixture
	/// </summary>
	[TestClass]
	public class TextItemContentFixture
	{
		public TextItemContentFixture()
		{
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowExceptionWhenNullType()
		{
			new TextItemContent("", null);
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentException))]
		public void ShouldThrowExceptionWhenEmptyType()
		{
			new TextItemContent("", "");
		}

		[TestMethod]
		public void ShouldWriteContent()
		{
			XmlWriterSettings settings = new XmlWriterSettings();
			settings.CloseOutput = true;
			settings.OmitXmlDeclaration = true;

			StringWriter sw = new StringWriter();
			XmlWriter writer = XmlWriter.Create(sw, settings);

			ItemContent content = new TextItemContent("myText", "text");
			content.WriteTo(writer, "textElement", null);

			writer.Close();

			string expectedXml = "<textElement type=\"text\">myText</textElement>";

			Assert.AreEqual(expectedXml, sw.ToString());
		}

		[TestMethod]
		public void ShouldCloneContent()
		{
			TextItemContent content = new TextItemContent("myText", "text");
			TextItemContent clonedContent = (TextItemContent)content.Clone();

			Assert.AreEqual(content.Text, clonedContent.Text);
		}
	}
}
