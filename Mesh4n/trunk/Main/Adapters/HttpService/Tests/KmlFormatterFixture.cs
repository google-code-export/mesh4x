using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using System.ServiceModel.Channels;
using System.ServiceModel.Web;
using System.Xml;
using System.IO;

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
				.Callback(ct => Assert.AreEqual(KmlFormatter.KmlNames.ContentType, ct))
				.Verifiable();

			var message = formatter.Format(
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
				.Callback(ct => Assert.AreEqual(KmlFormatter.KmlNames.ContentType, ct))
				.Verifiable();

			var message = formatter.Format(
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

	public class KmlFormatter : IMessageFormatter
	{
		public Message Format(Feed feed, IEnumerable<Item> items, IWebOperationContext context)
		{
			context.OutgoingResponse.ContentType = KmlNames.ContentType;

			return Message.CreateMessage(MessageVersion.None, "", new KmlWriter(feed, items));
		}

		public class KmlNames
		{
			public const string NamespaceURI = "http://earth.google.com/kml/2.2";
			public const string ContentType = "application/vnd.google-earth.kml+xml";

			public class ElementNames
			{
				public const string Document = "Document";
				public const string Name = "name";
				public const string Description = "description";
			}
		}

		class KmlWriter : BodyWriter
		{
			Feed feed;
			IEnumerable<Item> items;

			public KmlWriter(Feed feed, IEnumerable<Item> items)
				: base(false)
			{
				this.feed = feed;
				this.items = items;
			}

			protected override void OnWriteBodyContents(XmlDictionaryWriter writer)
			{
				writer.WriteStartDocument();
				writer.WriteStartElement(KmlNames.ElementNames.Document, KmlNames.NamespaceURI);
				writer.WriteElementString(KmlNames.ElementNames.Name, KmlNames.NamespaceURI, feed.Title);
				writer.WriteElementString(KmlNames.ElementNames.Description, KmlNames.NamespaceURI, feed.Description);

				foreach (var item in items)
				{
					var reader = new XmlNodeReader(item.XmlItem.Payload);
					while (reader.Read())
					{
						if (reader.NamespaceURI == KmlNames.NamespaceURI 
							&& reader.NodeType == XmlNodeType.Element)
						{
							var content = reader.ReadSubtree();
							content.MoveToContent();
							writer.WriteNode(content, true);
						}
					}
				}

				writer.WriteEndElement();
				writer.WriteEndDocument();
			}
		}
	}
}
