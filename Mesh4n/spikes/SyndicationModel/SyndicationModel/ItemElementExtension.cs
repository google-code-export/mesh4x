using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.IO;

namespace SyndicationModel
{
	/// <summary>A class that represents a item element extension.</summary>
	public class ItemElementExtension
	{
		private string xmlContent;
		private object extensionData;
		private ExtensionDataWriter extensionDataWriter;
		private string outerName;
		private string outerNamespace;

		public ItemElementExtension(XmlReader xmlReader)
		{
			Guard.ArgumentNotNull(xmlReader, "xmlReader");
			
			if(!xmlReader.IsStartElement())
			{
				throw new InvalidOperationException("The current reader position is not an starting element");
			}

			this.outerName = xmlReader.LocalName;
			this.outerNamespace = xmlReader.NamespaceURI;
			this.xmlContent = xmlReader.ReadOuterXml();
		}

		public ItemElementExtension(object extensionData, XmlSerializer serializer)
		{
			Guard.ArgumentNotNull(extensionData, "extensionData");

			if (serializer == null)
			{
				serializer = new XmlSerializer(extensionData.GetType());
			}
			
			this.extensionData = extensionData;
			this.extensionDataWriter = new ExtensionDataWriter(this.extensionData, serializer);
		}

		private void EnsureOuterNameAndNs()
		{
			this.extensionDataWriter.ComputeOuterNameAndNs(out this.outerName, out this.outerNamespace);
		}

		public TExtension GetObject<TExtension>(XmlSerializer serializer)
		{
			Guard.ArgumentNotNull(serializer, "serializer");
			
			if ((this.extensionData != null) && typeof(TExtension).IsAssignableFrom(this.extensionData.GetType()))
			{
				return (TExtension)this.extensionData;
			}

			using (XmlReader reader = this.GetReader())
			{
				return (TExtension)serializer.Deserialize(reader);
			}
		}

		public XmlReader GetReader()
		{
			if (this.xmlContent == null)
			{
				throw new InvalidOperationException("The xml content was not set for this extension");
			}

			XmlReader reader = XmlReader.Create(new StringReader(this.xmlContent));
			return reader;
		}

		public void WriteTo(XmlWriter writer)
		{
			Guard.ArgumentNotNull(writer, "writer");

			if (this.extensionDataWriter != null)
			{
				this.extensionDataWriter.WriteTo(writer);
			}
			else
			{
				writer.WriteRaw(this.xmlContent);
			}
		}

		public string OuterName
		{
			get
			{
				if (this.outerName == null)
				{
					this.EnsureOuterNameAndNs();
				}
				return this.outerName;
			}
		}

		public string OuterNamespace
		{
			get
			{
				if (this.outerName == null)
				{
					this.EnsureOuterNameAndNs();
				}
				return this.outerNamespace;
			}
		}

		private class ExtensionDataWriter
		{
			private readonly object extensionData;
			private string outerName;
			private string outerNamespace;
			private readonly XmlSerializer xmlSerializer;

			public ExtensionDataWriter(object extensionData, XmlSerializer serializer)
			{
				this.xmlSerializer = serializer;
				this.extensionData = extensionData;
			}

			internal void ComputeOuterNameAndNs(out string name, out string ns)
			{
				if (this.outerName != null)
				{
					name = this.outerName;
					ns = this.outerNamespace;
				}
				else
				{
					XmlTypeMapping mapping = new XmlReflectionImporter().ImportTypeMapping(this.extensionData.GetType());
					if ((mapping != null) && !string.IsNullOrEmpty(mapping.ElementName))
					{
						name = mapping.ElementName;
						ns = mapping.Namespace;

						this.outerName = name;
						this.outerName = ns;
					}
					else
					{
						this.ReadOuterNameAndNs(out name, out ns);

						this.outerName = name;
						this.outerNamespace = ns;
					}
				}
			}

			internal void ReadOuterNameAndNs(out string name, out string ns)
			{
				using (MemoryStream stream = new MemoryStream())
				{
					using (XmlWriter writer = XmlWriter.Create(stream))
					{
						this.WriteTo(writer);
					}
					stream.Seek(0L, SeekOrigin.Begin);
					using (XmlReader reader = XmlReader.Create(stream))
					{
						name = reader.LocalName;
						ns = reader.NamespaceURI;
					}
				}
			}

			public void WriteTo(XmlWriter writer)
			{
				if (this.xmlSerializer != null)
				{
					this.xmlSerializer.Serialize(writer, this.extensionData);
				}
			}
		}
	}
}
