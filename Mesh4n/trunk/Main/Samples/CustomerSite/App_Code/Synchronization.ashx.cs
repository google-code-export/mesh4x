using System;
using System.Data;
using System.Web;
using System.Collections;
using System.Web.Services;
using System.Web.Services.Protocols;
using SimpleSharing;
using System.Xml;
using System.Collections.Generic;
using CustomerLibrary;
using System.IO;
using System.Data.Common;
using System.Configuration;
using Microsoft.Practices.EnterpriseLibrary.Data;

namespace CustomerSite
{
	[WebService(Namespace = Schema.Namespace)]
	[WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
	public class Synchronization : IHttpHandler
	{
		IXmlRepository xmlRepo;
		ISyncRepository syncRepo;

		public Synchronization()
		{
			xmlRepo = new CustomerRepository(DatabaseFactory.CreateDatabase("CustomerDB"));
			syncRepo = new DbSyncRepository(DatabaseFactory.CreateDatabase("SyncDB"), "Customer");
		}

		public void ProcessRequest(HttpContext context)
		{
			context.Response.ContentType = "text/xml";

			SyncEngine engine = new SyncEngine(xmlRepo, syncRepo);

			using (XmlReader r = XmlReader.Create(context.Request.InputStream))
			{
				Feed feed;
				IEnumerable<Item> items;
				new RssFeedReader(r).Read(out feed, out items);

				IList<Item> conflicts = engine.Import(items);
			}

			XmlWriterSettings set = new XmlWriterSettings();
			set.CloseOutput = true;

			using (XmlWriter w = XmlWriter.Create(context.Response.OutputStream, set))
			{
				Feed feed = new Feed(
					ConfigurationManager.AppSettings["FeedTitle"],
					context.Request.Url.GetComponents(UriComponents.SchemeAndServer | UriComponents.Path, UriFormat.SafeUnescaped),
					ConfigurationManager.AppSettings["FeedDescription"]);

				IEnumerable<Item> items = engine.Export();

				new RssFeedWriter(w).Write(feed, items);
			}

			context.Response.End();
		}

		public bool IsReusable
		{
			get { return true; }
		}
	}
}
