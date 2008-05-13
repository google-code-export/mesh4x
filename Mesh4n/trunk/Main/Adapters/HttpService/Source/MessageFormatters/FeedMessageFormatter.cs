using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Channels;
using System.ServiceModel.Web;

namespace Mesh4n.Adapters.HttpService.MessageFormatters
{
	public class FeedMessageFormatter : IMessageFormatter
	{
		public Message Format(string feedName, Feed feed, IEnumerable<Item> items, IWebOperationContext context)
		{
			return Message.CreateMessage(MessageVersion.None, "", new RssFeedFormatter(feed, items));
		}


	}
}
