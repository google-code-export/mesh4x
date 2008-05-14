using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Channels;
using System.Xml;
using System.ServiceModel.Web;

namespace Mesh4n.Adapters.HttpService.MessageFormatters
{
	public class KmlNetworkFormatter : IMessageFormatter
	{
		public Message Format(string feedName, Feed feed, IEnumerable<Item> items, IWebOperationContext context)
		{
			context.OutgoingResponse.ContentType = KmlNames.ContentType;
			context.OutgoingResponse.Headers.Add("Content-Disposition",
				"attachment; filename=" + feedName + "-network.kml");

			Uri href = new Uri(context.IncomingRequest.UriTemplateMatch.BaseUri + "/feeds/" + feedName + "?format=kml");

			return Message.CreateMessage(MessageVersion.None, "", new KmlWriter(feedName, href, feed, items));
		}

		class KmlWriter : BodyWriter
		{
			string feedName;
			Feed feed;
			Uri href;
			IEnumerable<Item> items;

			public KmlWriter(string feedName, Uri href, Feed feed, IEnumerable<Item> items)
				: base(false)
			{
				this.feedName = feedName;
				this.feed = feed;
				this.items = items;
				this.href = href;
			}

			protected override void OnWriteBodyContents(XmlDictionaryWriter writer)
			{
				writer.WriteStartDocument();
				//<kml>
				writer.WriteStartElement(KmlNames.ElementNames.Kml, KmlNames.NamespaceURI);
				//<NetworkLink>
				writer.WriteStartElement(KmlNames.ElementNames.NetworkLink, KmlNames.NamespaceURI);
				writer.WriteElementString(KmlNames.ElementNames.Name, KmlNames.NamespaceURI, feed.Title);
				//<Url>
				writer.WriteStartElement(KmlNames.ElementNames.Url, KmlNames.NamespaceURI);

				writer.WriteElementString(KmlNames.ElementNames.Href, KmlNames.NamespaceURI, href.AbsoluteUri);
				writer.WriteElementString(KmlNames.ElementNames.RefreshMode, KmlNames.NamespaceURI, "onInterval");
				writer.WriteElementString(KmlNames.ElementNames.RefreshInterval, KmlNames.NamespaceURI, "5");

				//</Url>
				writer.WriteEndElement();
				//</NetworkLink>
				writer.WriteEndElement();
				//</kml>
				writer.WriteEndElement();
				writer.WriteEndDocument();
			}
		}
	}
}
