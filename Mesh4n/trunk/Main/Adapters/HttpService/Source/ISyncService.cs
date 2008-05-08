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
	[ServiceKnownType(typeof(RssFeedFormatter))]
	public interface ISyncService
	{
		[OperationContract]
		[WebGet(UriTemplate = "/feeds?format={format}")]
		FeedFormatter GetFeeds(string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds")]
		FeedFormatter GetRssFeeds();

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}?format={format}")]
		FeedFormatter GetFeed(string name, string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}")]
		FeedFormatter GetRssFeed(string name);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}/{itemId}?format={format}")]
		FeedFormatter GetItem(string name, string itemId, string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}/{itemId}")]
		FeedFormatter GetRssItem(string name, string itemId);
		
		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}?format={format}")]
		FeedFormatter PostFeed(string name, string format, FeedFormatter formatter);

		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}")]
		FeedFormatter PostRssFeed(string name, FeedFormatter formatter);

		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}/{itemId}?format={format}")]
		FeedFormatter PostItem(string name, string itemId, string format, FeedFormatter formatter);

		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}/{itemId}")]
		FeedFormatter PostRssItem(string name, string itemId, FeedFormatter formatter);
	}
}
