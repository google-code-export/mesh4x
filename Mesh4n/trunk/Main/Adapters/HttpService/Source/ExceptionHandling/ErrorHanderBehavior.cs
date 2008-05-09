using System;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.ServiceModel.Dispatcher;
using System.ServiceModel.Description;
using System.ServiceModel.Web;

namespace Mesh4n.Adapters.HttpService.ExceptionHandling
{
    public class ErrorHanderBehavior: WebHttpBehavior
    {
		IErrorHandler errorHandler;

		public ErrorHanderBehavior(IErrorHandler errorHandler) 
        { 
            this.errorHandler = errorHandler; 
        }

        protected override void AddServerErrorHandlers(ServiceEndpoint endpoint, EndpointDispatcher endpointDispatcher)  
        {
            endpointDispatcher.ChannelDispatcher.ErrorHandlers.Clear();  
            endpointDispatcher.ChannelDispatcher.ErrorHandlers.Add(errorHandler);  
        }
    }
}
