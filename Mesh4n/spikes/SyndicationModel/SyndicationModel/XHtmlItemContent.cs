using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace SyndicationModel
{
	public class XHtmlItemContent : ItemContent
	{
		private string xhtml;

		protected XHtmlItemContent(XHtmlItemContent source)
			: base(source)
		{
			Guard.ArgumentNotNull(source, "source");

			this.xhtml = source.xhtml;
		}

		public XHtmlItemContent(string xhtml)
		{
			this.xhtml = xhtml;
		}

		public override ItemContent Clone()
		{
			return new XHtmlItemContent(this);
		}

		protected override void WriteContentsTo(XmlWriter writer)
		{
			string data = this.xhtml ?? string.Empty;
			writer.WriteRaw(data);
		}

		public string XHtml
		{
			get
			{
				return this.xhtml;
			}
		}

		public override string Type
		{
			get
			{
				return "xhtml";
			}
		}
	}
}
