using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace Mesh4n.Adapters.HttpService.ExceptionHandling
{
	public class ServiceException : Exception
	{
		HttpStatusCode statusCode;

		public ServiceException()
			: base()
		{
		}

		public ServiceException(string message)
			: base(message)
		{
		}

		public ServiceException(string message, HttpStatusCode statusCode)
			: base(message)
		{
			this.statusCode = statusCode;
		}

		public HttpStatusCode StatusCode
		{
			get { return statusCode; }
		}

	}
}
