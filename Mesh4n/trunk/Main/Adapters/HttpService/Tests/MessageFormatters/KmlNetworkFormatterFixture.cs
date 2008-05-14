using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using System.ServiceModel.Channels;
using System.ServiceModel.Web;
using System.Xml;
using System.IO;
using Mesh4n.Adapters.HttpService.MessageFormatters;
using System.Net;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestFixture]
	public class KmlNetworkFormatterFixture
	{
		[Test]
		public void ShouldSetContentTypeKml()
		{
			var context = new MockWebContext();
			var formatter = new KmlNetworkFormatter();

			context.OutgoingResponse.ExpectSet(ctx => ctx.ContentType)
				.Callback(ct => Assert.AreEqual(KmlNames.ContentType, ct))
				.Verifiable();

			context.IncomingRequest.Object.UriTemplateMatch.BaseUri = new Uri("http://localhost");

			var message = formatter.Format(
				"foo", 
				new Feed("foo", "http://foo", "bar"),
				new Item[0],
				context.Object);

			Assert.IsNotNull(message);
			Assert.AreEqual("attachment; filename=foo-network.kml", context.OutgoingResponse.Object.Headers["Content-Disposition"]);

			context.Verify();
		}

		[Test]
		public void ShouldRenderNetworkLink()
		{
			var context = new MockWebContext();
			var formatter = new KmlNetworkFormatter();

			context.OutgoingResponse.ExpectSet(ctx => ctx.ContentType)
				.Callback(ct => Assert.AreEqual(KmlNames.ContentType, ct))
				.Verifiable();

			context.IncomingRequest.Object.UriTemplateMatch.BaseUri = new Uri("http://localhost");

			var message = formatter.Format(
				"foo",
				new Feed("foo", "http://foo", "bar"),
				new Item[] {},
				context.Object);

			Assert.IsNotNull(message);

			Console.WriteLine(message.GetReaderAtBodyContents().ReadOuterXml());

			context.Verify();
		}

		private XmlElement GetElement(string xml)
		{
			XmlDocument doc = new XmlDocument();
			doc.Load(GetReader(xml));

			return doc.DocumentElement;
		}

		private XmlReader GetReader(string xml)
		{
			XmlReaderSettings settings = new XmlReaderSettings();
			settings.IgnoreWhitespace = true;
			settings.CheckCharacters = true;
			settings.ConformanceLevel = ConformanceLevel.Auto;

			return XmlReader.Create(new StringReader(xml), settings);
		}
	}
}
