using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace Mesh4n.Adapters.HttpService.WebContext
{
	public interface IOutgoingWebResponseContext
	{
		void SetStatusAsCreated(Uri locationUri);
		void SetStatusAsNotFound();
		void SetStatusAsNotFound(string description);

		long ContentLength { get; set; }
		string ContentType { get; set; }
		string ETag { get; set; }
		WebHeaderCollection Headers { get; }
		DateTime LastModified { get; set; }
		string Location { get; set; }
		HttpStatusCode StatusCode { get; set; }
		string StatusDescription { get; set; }
		bool SuppressEntityBody { get; set; }
	}
}
