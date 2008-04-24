using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.IO;
using System.Globalization;

namespace SimpleSharing
{
	/// <summary>
	/// Base class for feed readers.
	/// </summary>
	public abstract class FeedReader
	{
		XmlReader reader;
		public event EventHandler ItemRead;

		protected FeedReader(XmlReader reader)
		{
			this.reader = reader;
		}

		/// <summary>
		/// Creates a new feed reader using the given <paramref name="xmlReader"/>.
		/// </summary>
		/// <param name="xmlReader">The <see cref="XmlReader"/> containing the actual feed payload.</param>
		/// <returns></returns>
		public static FeedReader Create(XmlReader xmlReader)
		{
			// TODO: will have a format auto-detecting 
			// reader in the future.
#pragma warning disable 618
			return new RssFeedReader(xmlReader);
#pragma warning restore 618
		}

		public void Read(out Feed feed, out IEnumerable<Item> items)
		{
			feed = ReadFeedImpl();
			items = ReadItemsImpl(feed);
		}

		protected abstract XmlQualifiedName ItemName { get; }

		protected abstract Feed ReadFeed(XmlReader reader);

		protected abstract IXmlItem ReadItem(XmlReader reader);

		private Feed ReadFeedImpl()
		{
			SharingXmlReader sharingReader = new SharingXmlReader(reader);

			Feed feed = ReadFeed(sharingReader);
			if (feed != null)
			{
				feed.Sharing = sharingReader.Sharing;
			}

			return feed;
		}

		private IEnumerable<Item> ReadItemsImpl(Feed feed)
		{
			XmlQualifiedName itemName = this.ItemName;

			do
			{
				if (IsItemElement(reader, itemName, XmlNodeType.Element))
				{
					yield return ReadItemImpl(reader);

					if (ItemRead != null)
						ItemRead(this, EventArgs.Empty);
				}
				else if (reader.NodeType == XmlNodeType.Element && feed != null && feed.Payload != null)
				{
					XmlElement el = feed.Payload.OwnerDocument.CreateElement(reader.Prefix, reader.LocalName, reader.NamespaceURI);
					el.InnerXml = reader.ReadInnerXml();
					feed.Payload.AppendChild(el);
				}
			}
			while (reader.Read());
		}

		private Item ReadItemImpl(XmlReader reader)
		{
			SyncXmlReader syncReader = new SyncXmlReader(reader, this);

			IXmlItem item = ReadItem(syncReader);
			Sync sync = syncReader.Sync;

			item.Id = sync.Id;

			if (!sync.Deleted && item.Title == null)
				throw new ArgumentNullException("title");

			return new Item(item, sync);
		}

		private static bool IsSseElement(XmlReader reader, string elementName, XmlNodeType nodeType)
		{
			return reader.LocalName == elementName &&
				reader.NamespaceURI == Schema.Namespace &&
				reader.NodeType == nodeType;
		}

		private static bool IsItemElement(XmlReader reader, XmlQualifiedName itemName, XmlNodeType nodeType)
		{
			return reader.LocalName == itemName.Name &&
				reader.NamespaceURI == itemName.Namespace &&
				reader.NodeType == nodeType;
		}

		protected bool IsItemElement(XmlReader reader, XmlNodeType nodeType)
		{
			return IsItemElement(reader, this.ItemName, nodeType);
		}

		protected string ReadElementValue(XmlReader reader)
		{
			if (reader.NodeType == XmlNodeType.Element)
			{
				if (reader.IsEmptyElement)
					return null;
				else
					reader.Read();
			}

			return reader.Value;
		}

		class SharingXmlReader : XmlWrappingReader
		{
			Sharing sharing = new Sharing();

			public SharingXmlReader(XmlReader baseReader)
				: base(baseReader)
			{
			}

			public override bool Read()
			{
				bool read = base.Read();

				if (IsSseElement(this, Schema.ElementNames.Sharing, XmlNodeType.Element))
				{
					ReadSharing();
				}

				if (IsSseElement(this, Schema.ElementNames.Sharing, XmlNodeType.EndElement))
				{
					read = base.Read();
				}

				return read;
			}

			private Sharing ReadSharing()
			{
				if (base.MoveToAttribute(Schema.AttributeNames.Since))
					sharing.Since = base.Value;
				if (base.MoveToAttribute(Schema.AttributeNames.Until))
					sharing.Until = base.Value;
				if (base.MoveToAttribute(Schema.AttributeNames.Expires))
					sharing.Expires = DateTime.Parse(base.Value);

				if (base.NodeType == XmlNodeType.Attribute)
					base.MoveToElement();

				if (!base.IsEmptyElement)
				{
					while (base.Read() &&
						!IsSseElement(this, Schema.ElementNames.Sharing, XmlNodeType.EndElement))
					{
						if (IsSseElement(this, Schema.ElementNames.Related, XmlNodeType.Element))
						{
							sharing.Related.Add(new Related(
								base.GetAttribute(Schema.AttributeNames.Link),
								(RelatedType)Enum.Parse(
									typeof(RelatedType),
									CultureInfo.CurrentCulture.TextInfo.ToTitleCase(
										base.GetAttribute(Schema.AttributeNames.Type)),
										false),
								base.GetAttribute(Schema.AttributeNames.Title)));
						}
					}
				}

				return sharing;
			}

			public Sharing Sharing
			{
				get { return sharing; }
			}
		}

		public class SyncXmlReader : XmlWrappingReader
		{
			FeedReader feedReader;
			Sync sync;

			public SyncXmlReader(XmlReader baseReader, FeedReader feedReader)
				: base(baseReader)
			{
				this.feedReader = feedReader;
			}

			public Sync Sync { get { return sync; } }

			public override bool Read()
			{
				bool read = base.Read();

				if (IsSseElement(this, Schema.ElementNames.Sync, XmlNodeType.Element))
				{
					sync = ReadSync();
				}

				if (IsSseElement(this, Schema.ElementNames.Sync, XmlNodeType.EndElement))
				{
					read = base.Read();
				}

				return read;
			}

			public Sync ReadSync()
			{
				XmlQualifiedName itemName = feedReader.ItemName;
				Sync newSync = null;

				base.MoveToAttribute(Schema.AttributeNames.Id);
				string id = base.Value;
				base.MoveToAttribute(Schema.AttributeNames.Updates);
				int updates = XmlConvert.ToInt32(base.Value);

				newSync = new Sync(id, updates);

				if (base.MoveToAttribute(Schema.AttributeNames.Deleted))
				{
					newSync.Deleted = XmlConvert.ToBoolean(base.Value);
				}
				if (base.MoveToAttribute(Schema.AttributeNames.NoConflicts))
				{
					newSync.NoConflicts = XmlConvert.ToBoolean(base.Value);
				}

				base.MoveToElement();

				List<History> historyUpdates = new List<History>();

				if (!base.IsEmptyElement)
				{
					while (base.Read() && !IsEndSync())
					{
						if (IsSseElement(this, Schema.ElementNames.History, XmlNodeType.Element))
						{
							base.MoveToAttribute(Schema.AttributeNames.Sequence);
							int sequence = XmlConvert.ToInt32(base.Value);
							string by = null;
							DateTime? when = DateTime.Now;

							if (base.MoveToAttribute(Schema.AttributeNames.When))
								when = DateTime.Parse(base.Value);
							if (base.MoveToAttribute(Schema.AttributeNames.By))
								by = base.Value;

							historyUpdates.Add(new History(by, when, sequence));
						}
						else if (IsSseElement(this, Schema.ElementNames.Conflicts, XmlNodeType.Element))
						{
							while (base.Read() &&
								!IsSseElement(this, Schema.ElementNames.Conflicts, XmlNodeType.EndElement))
							{
								if (IsItemElement(this, itemName, XmlNodeType.Element))
								{
									newSync.Conflicts.Add(feedReader.ReadItemImpl(base.BaseReader.ReadSubtree()));
								}
							}
						}
					}
				}

				if (historyUpdates.Count != 0)
				{
					historyUpdates.Reverse();
					foreach (History history in historyUpdates)
					{
						newSync.AddHistory(history);
					}
				}

				return newSync;
			}

			private bool IsEndSync()
			{
				return IsSseElement(this, Schema.ElementNames.Sync, XmlNodeType.EndElement);
			}
		}
	}
}
