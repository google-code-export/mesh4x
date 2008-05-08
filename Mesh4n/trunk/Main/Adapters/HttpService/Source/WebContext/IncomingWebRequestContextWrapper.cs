using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Web;

namespace Mesh4n.Adapters.HttpService.WebContext
{
	public class IncomingWebRequestContextWrapper : IIncomingWebRequestContext
	{
		private IncomingWebRequestContext context;

		public IncomingWebRequestContextWrapper(IncomingWebRequestContext context)
		{
			this.context = context;
		}
		
		#region IIncomingWebRequestContext Members

		public string Accept
		{
			get { return context.Accept; }
		}

		public long ContentLength
		{
			get { return context.ContentLength; }
		}

		public string ContentType
		{
			get { return context.ContentType; }
		}

		public System.Net.WebHeaderCollection Headers
		{
			get { return context.Headers; }
		}

		public string Method
		{
			get { return context.Method; }
		}

		public UriTemplateMatch UriTemplateMatch
		{
			get
			{
				return context.UriTemplateMatch;
			}
			set
			{
				context.UriTemplateMatch = value;
			}
		}

		public string UserAgent
		{
			get { return context.UserAgent; }
		}

		#endregion
	}
}
