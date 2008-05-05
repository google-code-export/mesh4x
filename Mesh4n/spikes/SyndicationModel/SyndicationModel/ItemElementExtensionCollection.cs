using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using System.Xml;
using System.Xml.Serialization;
using System.IO;

namespace SyndicationModel
{
	public sealed class ItemElementExtensionCollection : Collection<ItemElementExtension>
	{
		private string xmlContent;

		internal ItemElementExtensionCollection()
		{
		}

		internal ItemElementExtensionCollection(ItemElementExtensionCollection source)
		{
			this.xmlContent = source.xmlContent;
			for (int i = 0; i < source.Items.Count; i++)
			{
				base.Add(source.Items[i]);
			}
		}

		internal ItemElementExtensionCollection(string xmlContent)
		{
			this.xmlContent = xmlContent;
			if (this.xmlContent != null)
			{
				this.PopulateElements();
			}
		}

		public void Add(XmlReader xmlReader)
		{
			Guard.ArgumentNotNull(xmlReader, "xmlReader");
			base.Add(new ItemElementExtension(xmlReader));
		}

		public void Add(object extensionData, XmlSerializer serializer)
		{
			Guard.ArgumentNotNull(extensionData, "extensionData");

			if (serializer == null)
			{
				serializer = new XmlSerializer(extensionData.GetType());
			}

			base.Add(new ItemElementExtension(extensionData, serializer));
		}

		protected override void ClearItems()
		{
			base.ClearItems();
			this.xmlContent = null;
		}

		public XmlReader GetReaderAtElementExtensions()
		{
			if (this.xmlContent == null)
			{
				throw new InvalidOperationException("The xml content was not set for this collection");
			}

			XmlReader reader = XmlReader.Create(new StringReader(this.xmlContent));
			reader.ReadStartElement();
			return reader;
		}

		protected override void InsertItem(int index, ItemElementExtension item)
		{
			Guard.ArgumentNotNull(item, "item");
			base.InsertItem(index, item);

			this.xmlContent = null;
		}

		private void PopulateElements()
		{
			using (XmlReader reader = GetReaderAtElementExtensions())
			{
				for (int i = 0; reader.IsStartElement(); i++)
				{
					base.Add(new ItemElementExtension(reader));
				}
			}
		}

		public Collection<TExtension> ReadElementExtensions<TExtension>(string extensionName, string extensionNamespace, XmlSerializer serializer)
		{
			Guard.ArgumentNotNullOrEmptyString(extensionName, "extensionName");
			Guard.ArgumentNotNull(serializer, "serializer");

			if (extensionNamespace == null)
			{
				extensionNamespace = string.Empty;
			}

			Collection<TExtension> collection = new Collection<TExtension>();
			for (int i = 0; i < base.Count; i++)
			{
				if ((extensionName == base[i].OuterName) && (extensionNamespace == base[i].OuterNamespace))
				{
					collection.Add(base[i].GetObject<TExtension>(serializer));
				}
			}

			return collection;
		}

		protected override void RemoveItem(int index)
		{
			base.RemoveItem(index);
			this.xmlContent = null;
		}

		protected override void SetItem(int index, ItemElementExtension item)
		{
			Guard.ArgumentNotNull(item, "item");
			base.SetItem(index, item);
			this.xmlContent = null;
		}

		internal void WriteTo(XmlWriter writer)
		{
			Guard.ArgumentNotNull(writer, "writer");

			if (this.xmlContent != null)
			{
				writer.WriteRaw(this.xmlContent);
			}

			for (int i = 0; i < base.Items.Count; i++)
			{
				base.Items[i].WriteTo(writer);
			}
		}
	}
}
