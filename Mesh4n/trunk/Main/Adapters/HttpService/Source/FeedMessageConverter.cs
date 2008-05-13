using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Mesh4n.Adapters.HttpService
{
	public class FeedMessageConverter : IMessageFormatter
	{
		#region IMessageFormatter Members

		public System.ServiceModel.Channels.Message Format(Feed feed, IEnumerable<Item> items, System.ServiceModel.Web.IWebOperationContext context)
		{
			throw new NotImplementedException();
		}

		#endregion
	}
}
