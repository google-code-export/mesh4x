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
	/// Summary description for XHtmlItemContentFixture
	/// </summary>
	[TestClass]
	public class XHtmlItemContentFixture
	{
		public XHtmlItemContentFixture()
		{
		}

		[TestMethod]
		public void ShouldWriteContent()
		{
			XmlWriterSettings settings = new XmlWriterSettings();
			settings.CloseOutput = true;
			settings.OmitXmlDeclaration = true;

			StringWriter sw = new StringWriter();
			XmlWriter writer = XmlWriter.Create(sw, settings);

			ItemContent content = new XHtmlItemContent("<payload></payload>");
			content.WriteTo(writer, "textElement", null);

			writer.Close();

			string expectedXml = "<textElement type=\"xhtml\"><payload></payload></textElement>";

			Assert.AreEqual(expectedXml, sw.ToString());
		}

		[TestMethod]
		public void ShouldCloneContent()
		{
			XHtmlItemContent content = new XHtmlItemContent("<payload></payload>");
			XHtmlItemContent clonedContent = (XHtmlItemContent)content.Clone();

			Assert.AreEqual(content.XHtml, clonedContent.XHtml);
		}
	}
}
