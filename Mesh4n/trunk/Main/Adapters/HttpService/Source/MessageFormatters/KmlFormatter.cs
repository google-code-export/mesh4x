using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Channels;
using System.Xml;
using System.ServiceModel.Web;

namespace Mesh4n.Adapters.HttpService.MessageFormatters
{
	public class KmlFormatter : IMessageFormatter
	{
		public Message Format(string feedName, Feed feed, IEnumerable<Item> items, IWebOperationContext context)
		{
			context.OutgoingResponse.ContentType = KmlNames.ContentType;
			context.OutgoingResponse.Headers.Add("Content-Disposition",
				"attachment; filename=" + feedName + ".kml");

			return Message.CreateMessage(MessageVersion.None, "", new KmlWriter(feedName, feed, items));
		}

		class KmlWriter : BodyWriter
		{
			string feedName;
			Feed feed;
			IEnumerable<Item> items;

			public KmlWriter(string feedName, Feed feed, IEnumerable<Item> items)
				: base(false)
			{
				this.feedName = feedName;
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
