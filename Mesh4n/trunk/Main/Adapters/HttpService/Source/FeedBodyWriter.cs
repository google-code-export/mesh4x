using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Channels;
using System.Xml;

namespace Mesh4n.Adapters.HttpService
{
	public class FeedBodyWriter : BodyWriter
	{
		Feed feed;
		IEnumerable<Item> items;

		public FeedBodyWriter(Feed feed)
			: this(feed, null)
		{
		}

		public FeedBodyWriter(Feed feed, IEnumerable<Item> items)
			: base(false)
		{
			Guard.ArgumentNotNull(feed, "feed");

			this.feed = feed;
			this.items = items;
		}

		protected override void OnWriteBodyContents(XmlDictionaryWriter writer)
		{
			RssFeedWriter feedWriter = new RssFeedWriter(writer);
			feedWriter.Write(feed, items);
		}
	}
}
