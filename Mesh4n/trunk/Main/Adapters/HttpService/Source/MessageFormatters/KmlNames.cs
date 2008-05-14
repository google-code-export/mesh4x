using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Mesh4n.Adapters.HttpService.MessageFormatters
{
	public class KmlNames
	{
		public const string NamespaceURI = "http://earth.google.com/kml/2.2";
		public const string ContentType = "application/vnd.google-earth.kml+xml";

		public class AttributeNames
		{
			public const string Id = "ID";
		}

		public class ElementNames
		{
			public const string Kml = "kml";
			public const string Document = "Document";
			public const string Name = "name";
			public const string Description = "description";
			public const string NetworkLink = "NetworkLink";
			public const string Url = "Url";
			public const string Href = "href";
			public const string RefreshMode = "refreshMode";
			public const string RefreshInterval = "refreshInterval";
		}
	}
}
