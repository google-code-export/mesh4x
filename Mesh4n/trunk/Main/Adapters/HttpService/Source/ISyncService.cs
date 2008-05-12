
using System.ServiceModel;
using System.ServiceModel.Web;

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
		RssFeedFormatter GetRssFeeds();

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}?format={format}")]
		FeedFormatter GetFeed(string name, string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}")]
		RssFeedFormatter GetRssFeed(string name);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}/{itemId}?format={format}")]
		FeedFormatter GetItem(string name, string itemId, string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}/{itemId}")]
		RssFeedFormatter GetRssItem(string name, string itemId);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}?format={format}")]
		FeedFormatter GetConflicts(string name, string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}")]
		RssFeedFormatter GetRssConflicts(string name);
		
		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}?format={format}")]
		FeedFormatter PostFeed(string name, string format, FeedFormatter formatter);

		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}")]
		RssFeedFormatter PostRssFeed(string name, RssFeedFormatter formatter);

		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}/{itemId}?format={format}")]
		FeedFormatter PostItem(string name, string itemId, string format, FeedFormatter formatter);

		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}/{itemId}")]
		RssFeedFormatter PostRssItem(string name, string itemId, RssFeedFormatter formatter);
	}
}
