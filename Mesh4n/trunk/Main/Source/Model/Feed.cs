using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace Mesh4n
{
	[Serializable]
	public class Feed
	{
		private Sharing sharing = new Sharing();

		private string title;
		private string description;
		private string link;
		private XmlElement payload;

		// TODO: unpublished. 2.6 and 4.1. Add Unpublish(Item) ?

		public Feed() 
		{
		}

		public Feed(string title, string linkUrl, string description) 
			: this(title, linkUrl, description, null)
		{
		}

		public Feed(string title, string linkUrl, string description, XmlElement payload)
		{
			if (payload == null)
			{
				this.payload = new XmlDocument().CreateElement("payload");
			}
			else
			{
				this.payload = payload;
			}

			this.title = title;
			this.link = linkUrl;
			this.description = description;
		}

		public Sharing Sharing
		{
			get { return sharing; }
			set { sharing = value; }
		}

		public string Description
		{
			get { return description; }
		}

		public string Link
		{
			get { return link; }
		}

		public string Title
		{
			get { return title; }
		}

		public XmlElement Payload
		{
			get { return payload; }
			set { payload = value; }
		}
	}
}
