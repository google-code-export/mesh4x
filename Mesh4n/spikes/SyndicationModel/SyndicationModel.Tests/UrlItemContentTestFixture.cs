using SyndicationModel;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Xml;
using System.IO;

namespace SyndicationModel.Tests
{
	[TestClass()]
	public class UrlItemContentTestFixture
	{
		public UrlItemContentTestFixture()
		{
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowExceptionWhenNullUrl()
		{
			new UrlItemContent(null, null);
		}

		[TestMethod]
		public void ShouldWriteContent()
		{
			XmlWriterSettings settings = new XmlWriterSettings();
			settings.CloseOutput = true;
			settings.OmitXmlDeclaration = true;

			StringWriter sw = new StringWriter();
			XmlWriter writer = XmlWriter.Create(sw, settings);

			ItemContent content = new UrlItemContent(new Uri("http://test.org"), "link");
			content.WriteTo(writer, "textElement", null);

			writer.Close();

			string expectedXml = "<textElement type=\"link\" src=\"http://test.org/\" />";

			Assert.AreEqual(expectedXml, sw.ToString());
		}

		[TestMethod]
		public void ShouldCloneContent()
		{
			UrlItemContent content = new UrlItemContent(new Uri("http://test.org"), "link");
			UrlItemContent clonedContent = (UrlItemContent)content.Clone();

			Assert.AreEqual(content.Url, clonedContent.Url);
			Assert.AreEqual(content.Type, clonedContent.Type);
		}
	}
}
