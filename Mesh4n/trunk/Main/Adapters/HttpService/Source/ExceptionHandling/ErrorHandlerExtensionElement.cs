using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.ServiceModel.Dispatcher;
using System.ServiceModel.Configuration;

namespace Mesh4n.Adapters.HttpService.ExceptionHandling
{
    public class ErrorHandlerExtensionElement: BehaviorExtensionElement
    {
        public override Type BehaviorType 
        {
            get { return typeof(ErrorHanderBehavior);}
        }

        protected override object CreateBehavior()
        {
            return new ErrorHanderBehavior(new ErrorHandler());
        }
    }
}
