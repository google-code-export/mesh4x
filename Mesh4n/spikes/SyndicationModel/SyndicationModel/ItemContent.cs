using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace SyndicationModel
{
	/// <summary>A base class that represents content.</summary>
	public abstract class ItemContent : ICloneable<ItemContent>
	{
		private Dictionary<XmlQualifiedName, string> attributeExtensions = new Dictionary<XmlQualifiedName, string>();

		protected ItemContent()
        {
        }

		protected ItemContent(ItemContent source)
        {
            this.CopyAttributeExtensions(source);
        }

		public abstract ItemContent Clone();

		internal void CopyAttributeExtensions(ItemContent source)
		{
			Guard.ArgumentNotNull(source, "source");

			foreach (XmlQualifiedName name in source.attributeExtensions.Keys)
			{
				this.AttributeExtensions.Add(name, source.attributeExtensions[name]);
			}
		}

		public static TextItemContent CreateHtmlContent(string content)
		{
			return new TextItemContent(content, "html");
		}

		public static TextItemContent CreatePlaintextContent(string content)
		{
			return new TextItemContent(content);
		}

		///// <summary>Creates a new <see cref="T:System.ServiceModel.Syndication.UrlSyndicationContent" /> instance with the specified <see cref="T:System.Uri" /> and media type.</summary>
		///// <returns>A new <see cref="T:System.ServiceModel.Syndication.UrlSyndicationContent" /> instance.</returns>
		///// <param name="url">The <see cref="T:System.Uri" /> of the content.</param>
		///// <param name="mediaType">The media type of the content.</param>
		//public static UrlSyndicationContent CreateUrlContent(Uri url, string mediaType)
		//{
		//    return new UrlSyndicationContent(url, mediaType);
		//}

		public static XHtmlItemContent CreateXhtmlContent(string content)
		{
			return new XHtmlItemContent(content);
		}

		///// <summary>Creates a new <see cref="T:System.ServiceModel.Syndication.XmlSyndicationContent" /> instance with the specified data contract object.</summary>
		///// <returns>A new <see cref="T:System.ServiceModel.Syndication.XmlSyndicationContent" /> instance.</returns>
		///// <param name="dataContractObject">The data contract object.</param>
		//public static XmlSyndicationContent CreateXmlContent(object dataContractObject)
		//{
		//    return new XmlSyndicationContent("text/xml", dataContractObject, null);
		//}

		///// <summary>Creates XML syndication content.</summary>
		///// <returns>An instance of the <see cref="T:System.ServiceModel.Syndication.XmlSyndicationContent" /> class containing the new content.</returns>
		///// <param name="xmlReader">The <see cref="T:System.Xml.XmlReader" /> to read from.</param>
		//public static XmlSyndicationContent CreateXmlContent(XmlReader xmlReader)
		//{
		//    return new XmlSyndicationContent(xmlReader);
		//}

		///// <summary>Creates a new <see cref="T:System.ServiceModel.Syndication.XmlSyndicationContent" /> instance with the specified data contract object and data contract serializer.</summary>
		///// <returns>A new <see cref="T:System.ServiceModel.Syndication.XmlSyndicationContent" /> instance.</returns>
		///// <param name="dataContractObject">The data contract object.</param>
		///// <param name="dataContractSerializer">The data contract serializer.</param>
		//public static XmlSyndicationContent CreateXmlContent(object dataContractObject, XmlObjectSerializer dataContractSerializer)
		//{
		//    return new XmlSyndicationContent("text/xml", dataContractObject, dataContractSerializer);
		//}

		///// <summary>Creates a new <see cref="T:System.ServiceModel.Syndication.XmlSyndicationContent" /> instance with the specified object and XML serializer.</summary>
		///// <returns>A new <see cref="T:System.ServiceModel.Syndication.XmlSyndicationContent" /> instance.</returns>
		///// <param name="xmlSerializerObject">The object.</param>
		///// <param name="serializer">The XML serializer.</param>
		//public static XmlSyndicationContent CreateXmlContent(object xmlSerializerObject, XmlSerializer serializer)
		//{
		//    return new XmlSyndicationContent("text/xml", xmlSerializerObject, serializer);
		//}

		protected abstract void WriteContentsTo(XmlWriter writer);
		
		public void WriteTo(XmlWriter writer, string outerElementName, string outerElementNamespace)
		{
			Guard.ArgumentNotNull(writer, "writer");
			Guard.ArgumentNotNullOrEmptyString(outerElementName, "outerElementName");
						
			writer.WriteStartElement(outerElementName, outerElementNamespace);
			writer.WriteAttributeString("type", string.Empty, this.Type);
			
			if (this.attributeExtensions != null)
			{
				foreach (XmlQualifiedName name in this.attributeExtensions.Keys)
				{
					if (name.Name != "type" || name.Namespace != string.Empty) 
					{
						string extension = null;
						if (this.attributeExtensions.TryGetValue(name, out extension))
						{
							writer.WriteAttributeString(name.Name, name.Namespace, extension);
						}
					}
				}
			}
			this.WriteContentsTo(writer);
			writer.WriteEndElement();
		}

		/// <summary>Gets the attribute extensions for this content.</summary>
		/// <returns>A dictionary that contains the attribute extensions for this object.</returns>
		public Dictionary<XmlQualifiedName, string> AttributeExtensions
		{
			get
			{
				return this.attributeExtensions;
			}
		}

		/// <summary>Gets the syndication content type.</summary>
		/// <returns>The name of the type of syndication content.</returns>
		public abstract string Type { get; }
	}
}
