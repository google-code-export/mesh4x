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

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestFixture]
	public class KmlFormatterFixture
	{
		[Test]
		public void ShouldSetContentTypeKml()
		{
			var context = new MockWebContext();
			var formatter = new KmlFormatter();

			context.OutgoingResponse.ExpectSet(ctx => ctx.ContentType)
				.Callback(ct => Assert.AreEqual(KmlNames.ContentType, ct))
				.Verifiable();

			var message = formatter.Format(
				"foo",
				new Feed("foo", "http://foo", "bar"),
				new Item[0],
				context.Object);

			Assert.IsNotNull(message);

			context.Verify();
		}

		[Test]
		public void ShouldIgnoreNonKmlPayload()
		{
			var context = new MockWebContext();
			var formatter = new KmlFormatter();

			context.OutgoingResponse.ExpectSet(ctx => ctx.ContentType)
				.Callback(ct => Assert.AreEqual(KmlNames.ContentType, ct))
				.Verifiable();

			var message = formatter.Format(
				"foo", 
				new Feed("foo", "http://foo", "bar"),
				new []
				{
					new Item(new XmlItem("1", "itemTitle", "itemDescription", 
						GetElement(@"
<payload>
	<title>itemTitle</title>
	<Placemark xmlns='http://earth.google.com/kml/2.2'>
		<name>Simple placemark</name>
		<description>Simple description</description>
	</Placemark>
</payload>
")),
						Behaviors.Create("1", "kzu", DateTime.Now, false))
				},
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
