using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace SyndicationModel
{
	/// <summary>Represents syndication content that consists of a URL to another resource.</summary>
	public class UrlItemContent : ItemContent
	{
		private string mediaType;
		private Uri url;

		protected UrlItemContent(UrlItemContent source)
			: base(source)
		{
			Guard.ArgumentNotNull(source, "source");

			this.url = source.url;
			this.mediaType = source.mediaType;
		}

		public UrlItemContent(Uri url, string mediaType)
		{
			Guard.ArgumentNotNull(url, "url");

			this.url = url;
			this.mediaType = mediaType;
		}

		public override ItemContent Clone()
		{
			return new UrlItemContent(this);
		}

		protected override void WriteContentsTo(XmlWriter writer)
		{
			string completeUrl = (this.url.IsAbsoluteUri) ? this.url.AbsoluteUri : this.url.ToString();
			writer.WriteAttributeString("src", string.Empty, completeUrl);
		}

		public override string Type
		{
			get
			{
				return this.mediaType;
			}
		}

		public Uri Url
		{
			get
			{
				return this.url;
			}
		}
	}
}
