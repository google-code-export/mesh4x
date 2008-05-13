
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
		RssFeedFormatter GetRssFeeds();

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}?format={format}")]
		Message GetFeed(string name, string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}")]
		Message GetRssFeed(string name);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}/{itemId}?format={format}")]
		FeedFormatter GetItem(string name, string itemId, string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}/{itemId}")]
		RssFeedFormatter GetRssItem(string name, string itemId);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}/conflicts?format={format}")]
		FeedFormatter GetConflicts(string name, string format);

		[OperationContract]
		[WebGet(UriTemplate = "/feeds/{name}/conflicts")]
		RssFeedFormatter GetRssConflicts(string name);
		
		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}")]
		FeedFormatter PostFeed(string name, FeedFormatter formatter);

		[OperationContract]
		[WebInvoke(Method = "POST", UriTemplate = "/feeds/{name}/{itemId}")]
		FeedFormatter PostItem(string name, string itemId, FeedFormatter formatter);
	}
}
