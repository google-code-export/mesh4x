using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.IO;

namespace SyndicationModel
{
	public class XmlItemContent : ItemContent
	{
	    private string xmlContent;
	    private ItemElementExtension extension;
	    private string type;

	    protected XmlItemContent(XmlItemContent source)
	        : base(source)
	    {
	        Guard.ArgumentNotNull(source, "source");

	        this.xmlContent = source.xmlContent;
	        this.extension = source.extension;
	        this.type = source.type;
	    }

	    public XmlItemContent(XmlReader reader)
	    {
	        Guard.ArgumentNotNull(reader, "reader");

	        if(!reader.IsStartElement())
			{
				throw new InvalidOperationException("The current reader position is not an starting element");
			}

	        if (reader.HasAttributes)
	        {
	            while (reader.MoveToNextAttribute())
	            {
	                string localName = reader.LocalName;
	                string namespaceURI = reader.NamespaceURI;
	                if ((localName == "type") && (namespaceURI == string.Empty))
	                {
	                    this.type = reader.Value;
	                }
	                else if (!(localName == "xmlns" || namespaceURI =="http://www.w3.org/2000/xmlns/"))
	                {
	                    base.AttributeExtensions.Add(new XmlQualifiedName(localName, namespaceURI), reader.Value);
	                }
	            }
	            reader.MoveToElement();
	        }
	        
			this.xmlContent = reader.ReadOuterXml();
			if (this.type == null)
			{
				this.type = "text/xml";
			}
	    }

		public XmlItemContent(ItemElementExtension extension) 
			: this(extension, null)
		{
		}

		public XmlItemContent(ItemElementExtension extension, string type)
		{
			Guard.ArgumentNotNull(extension, "extension");

			this.type = string.IsNullOrEmpty(type) ? "text/xml" : type;
			this.extension = extension;
		}

		public XmlItemContent(object extensionData, XmlSerializer serializer) 
			: this(extensionData, serializer, null)
		{
		}

		public XmlItemContent(object extensionData, XmlSerializer serializer, string type)
	    {
	        this.type = string.IsNullOrEmpty(type) ? "text/xml" : type;
	        this.extension = new ItemElementExtension(extensionData, serializer);
	    }

	    public override ItemContent Clone()
	    {
	        return new XmlItemContent(this);
	    }

	    public XmlReader GetReaderAtContent()
	    {
			if (this.xmlContent == null)
			{
				throw new InvalidOperationException("The xml content was not set for this extension");
			}

			XmlReader reader = XmlReader.Create(new StringReader(this.xmlContent));
			reader.MoveToContent();

			return reader;
	    }

	    public TContent ReadContent<TContent>()
	    {
	        return this.ReadContent<TContent>(null);
	    }

	    public TContent ReadContent<TContent>(XmlSerializer serializer)
	    {
	        if (serializer == null)
	        {
	            serializer = new XmlSerializer(typeof(TContent));
	        }
	        if (this.extension != null)
	        {
	            return this.extension.GetObject<TContent>(serializer);
	        }
	        using (XmlReader reader = GetReaderAtContent())
	        {
	            return (TContent)serializer.Deserialize(reader);
	        }
	    }

	    protected override void WriteContentsTo(XmlWriter writer)
	    {
			Guard.ArgumentNotNull(writer, "writer");

	        if (this.extension != null)
	        {
	            this.extension.WriteTo(writer);
	        }
	        else if (this.xmlContent != null)
	        {
				writer.WriteRaw(this.xmlContent);
	        }
	    }

	    public ItemElementExtension Extension
	    {
	        get
	        {
	            return this.extension;
	        }
	    }

	    public override string Type
	    {
	        get
	        {
	            return this.type;
	        }
	    }
	}
}
