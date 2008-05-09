using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace Mesh4n
{
	[XmlRoot("rss", Namespace="")]
	public class RssFeedFormatter : FeedFormatter
	{
		public RssFeedFormatter()
			: base()
		{
		}

		public RssFeedFormatter(Feed feed, IEnumerable<Item> items)
			: base(feed, items)
		{
		}

		protected override FeedWriter CreateFeedWriter(System.Xml.XmlWriter writer)
		{
			return new RssFeedWriter(writer, false);
		}

		protected override FeedReader CreateFeedReader(System.Xml.XmlReader reader)
		{
			return new RssFeedReader(reader);
		}
	}
}
