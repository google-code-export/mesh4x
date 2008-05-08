using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace Mesh4n
{
	public abstract class FeedWriter
	{
		XmlWriter writer;
		bool shouldWriteStartElement = true;

		public event EventHandler ItemWritten;

		public FeedWriter(XmlWriter writer)
			: this (writer, true)
		{
		}

		public FeedWriter(XmlWriter writer, bool shouldWriteStartElement)
		{
			Guard.ArgumentNotNull(writer, "writer");

			this.shouldWriteStartElement = shouldWriteStartElement;
			this.writer = new XmlSharingWriter(writer);
		}

		/// <summary>
		/// Writes the items as an xml fragment of items.
		/// </summary>
		/// <param name="items"></param>
		public void Write(params Item[] items)
		{
			Write((IEnumerable<Item>)items);
		}

		/// <summary>
		/// Writes the items as an xml fragment of items.
		/// </summary>
		/// <param name="items"></param>
		public void Write(IEnumerable<Item> items)
		{
			Write(null, items);
		}

		/// <summary>
		/// Writes the items with the optional feed information.
		/// </summary>
		/// <param name="feed">Feed information, can be null.</param>
		/// <param name="items">Items to write.</param>
		public void Write(Feed feed, params Item[] items)
		{
			Write(feed, (IEnumerable<Item>)items);
		}

		/// <summary>
		/// Writes the items with the optional feed information.
		/// </summary>
		/// <param name="feed">Feed information, can be null.</param>
		/// <param name="items">Items to write.</param>
		public void Write(Feed feed, IEnumerable<Item> items)
		{
			if (feed != null)
			{
				// write feed root element: rss | atom
				WriteStartFeed(feed, writer, shouldWriteStartElement);
				WriteSharing(feed.Sharing);
			}

			if (items != null)
			{

				foreach (Item item in items)
				{
					Write(item);
					if (ItemWritten != null)
						ItemWritten(this, EventArgs.Empty);
				}
			}

			if (feed != null)
			{
				// close feed root
				WriteEndFeed(feed, writer, shouldWriteStartElement);
			}
		}

		private void Write(Item item)
		{
			// <item>
			WriteStartItem(item, writer);
			
			if(item.Sync != null)
				WriteSync(item.Sync);
			
			// </item>
			WriteEndItem(item, writer);
		}

		public void WriteSync(Sync sync)
		{
			// <sx:sync>
			writer.WriteStartElement(Schema.DefaultPrefix, Schema.ElementNames.Sync, Schema.Namespace);
			writer.WriteAttributeString(Schema.AttributeNames.Id, sync.Id);
			writer.WriteAttributeString(Schema.AttributeNames.Updates, XmlConvert.ToString(sync.Updates));
			writer.WriteAttributeString(Schema.AttributeNames.Deleted, XmlConvert.ToString(sync.Deleted));
			writer.WriteAttributeString(Schema.AttributeNames.NoConflicts, XmlConvert.ToString(sync.NoConflicts));

			WriteHistory(sync.UpdatesHistory);

			if (sync.Conflicts.Count > 0)
			{
				// <sx:conflicts>
				writer.WriteStartElement(Schema.DefaultPrefix, Schema.ElementNames.Conflicts, Schema.Namespace);

				foreach (Item conflict in sync.Conflicts)
				{
					Write(conflict);
				}

				// </sx:conflicts>
				writer.WriteEndElement();
			}

			// </sx:sync>
			writer.WriteEndElement();
		}

		private void WriteSharing(Sharing sharing)
		{
			if (sharing.Expires == null &&
				sharing.Since == null &&
				sharing.Until == null &&
				sharing.Related.Count == 0)
				return;

			// <sx:sharing>
			writer.WriteStartElement(Schema.DefaultPrefix, Schema.ElementNames.Sharing, Schema.Namespace);
			if (sharing.Since != null)
				writer.WriteAttributeString(Schema.AttributeNames.Since, sharing.Since);
			if (sharing.Until != null)
				writer.WriteAttributeString(Schema.AttributeNames.Until, sharing.Until);
			if (sharing.Expires != null)
				writer.WriteAttributeString(Schema.AttributeNames.Expires, Timestamp.ToString(sharing.Expires.Value));

			WriteRelated(sharing.Related);

			// </sx:sharing>
			writer.WriteEndElement();
		}

		private void WriteRelated(List<Related> related)
		{
			if (related.Count > 0)
			{
				foreach (Related rel in related)
				{
					// <sx:related>
					writer.WriteStartElement(Schema.DefaultPrefix, Schema.ElementNames.Related, Schema.Namespace);
					writer.WriteAttributeString(Schema.AttributeNames.Link, rel.Link);
					if (rel.Title != null)
						writer.WriteAttributeString(Schema.AttributeNames.Title, rel.Title);
					writer.WriteAttributeString(Schema.AttributeNames.Type, rel.Type.ToString().ToLower());
					writer.WriteEndElement();
				}
			}
		}

		private void WriteHistory(IEnumerable<History> updatesHistory)
		{
			foreach (History history in updatesHistory)
			{
				// <sx:history>
				writer.WriteStartElement(Schema.DefaultPrefix, Schema.ElementNames.History, Schema.Namespace);
				writer.WriteAttributeString(Schema.AttributeNames.Sequence, XmlConvert.ToString(history.Sequence));
				if (history.When.HasValue)
					writer.WriteAttributeString(Schema.AttributeNames.When, Timestamp.ToString(history.When.Value));
				writer.WriteAttributeString(Schema.AttributeNames.By, history.By);
				// </sx:history>
				writer.WriteEndElement();
			}
		}

		protected abstract void WriteStartFeed(Feed feed, XmlWriter writer, bool shouldWriteStartElement);
		protected abstract void WriteEndFeed(Feed feed, XmlWriter writer, bool shouldWriteStartElement);
		protected abstract void WriteStartItem(Item item, XmlWriter writer);
		protected abstract void WriteEndItem(Item item, XmlWriter writer);
	}
}
