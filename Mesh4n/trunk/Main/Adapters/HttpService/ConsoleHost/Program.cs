using System;
using System.Collections.Generic;
using System.Text;
using Mesh4n.Adapters.HttpService.Configuration;
using Mesh4n.Adapters.HttpService;
using System.ServiceModel.Web;
using System.ServiceModel.Dispatcher;

namespace ConsoleHost
{
	class Program
	{
		static void Main(string[] args)
		{
			WebServiceHost host = new WebServiceHost(typeof(SyncService));
			host.Open();

			foreach (ChannelDispatcher cd in host.ChannelDispatchers)
				foreach (EndpointDispatcher ed in cd.Endpoints)
					Console.WriteLine("Service listening at {0}", ed.EndpointAddress.Uri);

			// The service can now be accessed.
			Console.WriteLine("The service is ready.");
			Console.WriteLine("Press <ENTER> to terminate service.");
			Console.WriteLine();
			Console.ReadLine();
		}
	}
}
