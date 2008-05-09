using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.ServiceModel.Web;
using System.ServiceModel.Dispatcher;
using Mesh4n.Adapters.HttpService.WebContext;

namespace Mesh4n.Adapters.HttpService.ExceptionHandling
{
    public class ErrorHandler: IErrorHandler
    {
        const string FaultNs = "http://HttpSyncService/Errors";
		IWebOperationContext operationContext;

		public ErrorHandler()
		{
		}

		public IWebOperationContext Context
		{
			get 
			{
				if (this.operationContext == null && WebOperationContext.Current != null)
				{
					this.operationContext = new WebOperationContextWrapper(WebOperationContext.Current);
				}

				return this.operationContext;
			}
			set 
			{
				this.operationContext = value;
			}
		}

        public bool HandleError(Exception error)
        {
			return true;
        }

        public void ProvideFault(Exception error, MessageVersion version, ref Message fault)
        {
			FaultCode code = FaultCode.CreateSenderFaultCode(error.GetType().Name, FaultNs);
            fault = Message.CreateMessage(version, code, error.Message, null);

            if (this.Context != null)
            {
				this.Context.OutgoingResponse.StatusCode = ((ServiceException)error).StatusCode;
				this.Context.OutgoingResponse.StatusDescription = error.Message;
				this.Context.OutgoingResponse.SuppressEntityBody = false;
            }
        }
    }


}
