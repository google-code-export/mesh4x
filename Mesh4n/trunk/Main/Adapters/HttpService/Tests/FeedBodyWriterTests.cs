using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Xml;
using Mesh4n.Tests;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestClass]
	public class FeedBodyWriterTests : TestFixtureBase
	{
		public FeedBodyWriterTests()
		{
		}

		[TestMethod]
		[ExpectedException(typeof(ArgumentNullException))]
		public void ShouldThrowIfNullFeed()
		{
			new FeedBodyWriter(null);
		}

		[TestMethod]
		public void ShouldWriteFeed()
		{
			StringBuilder sb = new StringBuilder();
			
			Feed feed = new Feed("Title", "/Feeds", "Description");
			FeedBodyWriter bodyWriter = new FeedBodyWriter(feed);

			XmlWriterSettings settings = new XmlWriterSettings();
			settings.OmitXmlDeclaration = true;
			
			XmlWriter writer = XmlWriter.Create(sb, settings);
			bodyWriter.WriteBodyContents(
				XmlDictionaryWriter.CreateDictionaryWriter(writer));
			writer.Close();

			XmlElement output = GetElement(sb.ToString());

			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel"));
			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel/title"));
			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel/description"));
			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel/link"));

			Assert.AreEqual("Title", EvaluateInnerString(output, "/rss/channel/title"));
			Assert.AreEqual("/Feeds", EvaluateInnerString(output, "/rss/channel/link"));
			Assert.AreEqual("Description", EvaluateInnerString(output, "/rss/channel/description"));
		}
	}
}
