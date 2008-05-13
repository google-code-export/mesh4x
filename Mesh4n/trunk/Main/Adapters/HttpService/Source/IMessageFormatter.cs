using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Channels;
using System.ServiceModel.Web;

namespace Mesh4n.Adapters.HttpService
{
	interface IMessageFormatter
	{
		Message Format(Feed feed, IEnumerable<Item> items, IWebOperationContext context);
	}
}
