using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace SyndicationModel
{
	public class TextItemContent : ItemContent
	{
		private string text;
		private string type;

		protected TextItemContent(TextItemContent source)
			: base(source)
		{
			Guard.ArgumentNotNull(source, "source");

			this.text = source.text;
		}

		/// <summary>Initializes a new instance of the content</summary>
		/// <param name="text">The text of the content.</param>
		public TextItemContent(string text)
		{
			this.text = text;
			this.type = "text";
		}

		public TextItemContent(string text, string type)
		{
			Guard.ArgumentNotNullOrEmptyString(type, "type");

			this.text = text;
			this.type = type;
		}


		public override ItemContent Clone()
		{
			return new TextItemContent(this);
		}

		protected override void WriteContentsTo(XmlWriter writer)
		{
			string data = this.text ?? string.Empty;
			writer.WriteString(data);
		}

		public string Text
		{
			get
			{
				return this.text;
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
