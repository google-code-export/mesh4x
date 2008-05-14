using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.Xml;

namespace Mesh4n
{
	public abstract class FeedFormatter : IXmlSerializable
	{
		Feed feed;
		IEnumerable<Item> items; 

		public FeedFormatter()
		{
		}

		public FeedFormatter(Feed feed, IEnumerable<Item> items)
		{
			this.feed = feed;
			this.items = new List<Item>(items);
		}

		public Feed Feed
		{
			get { return feed; }
			set { feed = value; }
		}

		public IEnumerable<Item> Items
		{
			get { return items; }
			set { items = value; }
		}

		protected abstract FeedWriter CreateFeedWriter(XmlWriter writer);
		protected abstract FeedReader CreateFeedReader(XmlReader reader);

		public System.Xml.Schema.XmlSchema GetSchema()
		{
			return null; 
		}

		public void ReadXml(System.Xml.XmlReader reader)
		{
			Guard.ArgumentNotNull(reader, "reader");

			FeedReader feedReader = CreateFeedReader(reader);
			feedReader.Read(out feed, out items);

			// HACK: It must be a List, otherwise the XmlSerializer will close the reader before the items
			// are read from the enumeration.
			items = new List<Item>(items);
		}

		public void WriteXml(System.Xml.XmlWriter writer)
		{
			Guard.ArgumentNotNull(writer, "writer");
			
			FeedWriter feedWriter = CreateFeedWriter(writer);
			feedWriter.Write(this.feed, new List<Item>(this.items));
		}

	}
}
