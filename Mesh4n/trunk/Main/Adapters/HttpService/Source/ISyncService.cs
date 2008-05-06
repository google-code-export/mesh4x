using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.ServiceModel;
using System.ServiceModel.Web;
using System.ServiceModel.Channels;

namespace Mesh4n.Adapters.HttpService
{
	[ServiceContract]
	public interface ISyncService
	{
		[OperationContract]
		[WebGet(UriTemplate = "/feeds")]
		Message GetFeeds();

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}")]
		Message GetFeed(string name);
	}
}
