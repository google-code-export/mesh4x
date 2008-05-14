using System;
using System.Collections.Generic;
using System.Globalization;
using System.Net;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.ServiceModel.Web;
using Mesh4n.Adapters.HttpService.Configuration;
using Mesh4n.Adapters.HttpService.Properties;
using System.ServiceModel.Channels;
using Mesh4n.Adapters.HttpService.MessageFormatters;
using System.Xml;
using System.IO;

namespace Mesh4n.Adapters.HttpService
{
	[AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
	public class SyncService : ISyncService
	{
		IFeedConfigurationManager configurationManager;
		IWebOperationContext operationContext;

		public SyncService()
		{
			this.configurationManager = SyncServiceConfigurationSection.GetConfigurationManager();
			this.operationContext = new WebOperationContextWrapper(WebOperationContext.Current);
		}

		public SyncService(IFeedConfigurationManager configurationManager, IWebOperationContext context)
		{
			this.configurationManager = configurationManager;
			this.operationContext = context;
		}

		public FeedFormatter GetFeeds(string format)
		{
			ValidateFormat(format);
			
			List<Item> items = new List<Item>();
			foreach (FeedConfigurationEntry entry in configurationManager.LoadAll())
			{
				Item item = new Item(new XmlItem(entry.Title, entry.Description,
					 GetElement(String.Format(
						"<payload><link>{0}</link></payload>", 
						"/feeds/" + entry.Name))), null);
				items.Add(item);
			}

			Feed feed = CreateFeed(Resources.MainFeedTitle, Resources.MainFeedDescription, "/feeds");

			return CreateFeedFormatter(feed, items, format);
		}

		public RssFeedFormatter GetRssFeeds()
		{
			return (RssFeedFormatter)GetFeeds(SupportedFormats.Rss20);
		}

		public Message GetFeed(string name, string format)
		{
			ValidateFormat(format);

			FeedConfigurationEntry entry = configurationManager.Load(name);
			if (entry == null)
			{
				throw new ServiceException(string.Format(CultureInfo.InvariantCulture,
					Resources.FeedNotFound, name), HttpStatusCode.NotFound);
			}

			DateTime? since = GetSinceDate(this.operationContext);

			IEnumerable<Item> items;
			if (since.HasValue)
			{
				items = entry.SyncAdapter.GetAllSince(since.Value);
				if (items == null || !items.GetEnumerator().MoveNext())
				{
					throw new ServiceException(Resources.NoAvailableItems, HttpStatusCode.NotModified);
				}
			}
			else
			{
				items = entry.SyncAdapter.GetAll();
			}

			SetSinceDate(this.operationContext, DateTime.Now);
			
			Feed feed = CreateFeed(entry.Title, entry.Description, "/feeds/" + name);

			IMessageFormatter formatter = GetFormatter(format);

			return formatter.Format(entry.Name, feed, items, this.operationContext);
		}

		public Message GetRssFeed(string name)
		{
			return GetFeed(name, SupportedFormats.Rss20);
		}

		public FeedFormatter GetItem(string name, string itemId, string format)
		{
			ValidateFormat(format);

			FeedConfigurationEntry entry = configurationManager.Load(name);
			if (entry == null)
			{
				throw new ServiceException(string.Format(CultureInfo.InvariantCulture,
					Resources.FeedNotFound, name), HttpStatusCode.NotFound);
			}
			
			Item item = entry.SyncAdapter.Get(itemId);

			if (item == null)
			{
				throw new ServiceException(string.Format(CultureInfo.InvariantCulture,
					Resources.ItemNotFound, itemId), HttpStatusCode.NotFound);
			}
			else
			{
				Feed feed = CreateFeed(entry.Title, entry.Description, string.Format("/feeds/{0}/{1}", name, itemId));
				return CreateFeedFormatter(feed, new Item[] { item }, format);
			}
		}

		public RssFeedFormatter GetRssItem(string name, string itemId)
		{
			return (RssFeedFormatter)GetItem(name, itemId, SupportedFormats.Rss20);
		}

		public FeedFormatter GetConflicts(string name, string format)
		{
			ValidateFormat(format);

			FeedConfigurationEntry entry = configurationManager.Load(name);
			if (entry == null)
			{
				throw new ServiceException(string.Format(CultureInfo.InvariantCulture,
					Resources.FeedNotFound, name), HttpStatusCode.NotFound);
			}

			IEnumerable<Item> conflicts = entry.SyncAdapter.GetConflicts();

			Feed feed = CreateFeed(entry.Title, entry.Description, string.Format("/feeds/{0}/conflicts", name));
			
			return CreateFeedFormatter(feed, conflicts, format);
		}

		public RssFeedFormatter GetRssConflicts(string name)
		{
			return (RssFeedFormatter)GetConflicts(name, SupportedFormats.Rss20);
		}
		
		public FeedFormatter PostFeed(string name, FeedFormatter formatter)
		{
			FeedConfigurationEntry entry = configurationManager.Load(name);
			if (entry == null)
			{
				throw new ServiceException(string.Format(CultureInfo.InvariantCulture,
					Resources.FeedNotFound, name), HttpStatusCode.NotFound);
			}

			SyncEngine syncEngine = new SyncEngine(entry.SyncAdapter, formatter.Items);
			IEnumerable<Item> conflicts = syncEngine.Synchronize();

			Feed feed = CreateFeed(entry.Title, entry.Description, "/feeds/" + name);
			
			return CreateFeedFormatter(feed, conflicts, formatter.GetType());
		}

		public FeedFormatter PostItem(string name, string itemId, FeedFormatter formatter)
		{
			IEnumerator<Item> enumerator = formatter.Items.GetEnumerator();
			if (!enumerator.MoveNext())
			{
				throw new ServiceException(Resources.EmptyRequest, HttpStatusCode.BadRequest);
			}

			Item item = enumerator.Current;
			if (item.XmlItem.Id != itemId)
			{
				throw new ServiceException(string.Format(CultureInfo.InvariantCulture,
					Resources.DifferentIds, itemId, item.XmlItem.Id), HttpStatusCode.BadRequest);
			}

			return PostFeed(name, formatter);
		}

		protected virtual IMessageFormatter GetFormatter(string format)
		{
			switch (format.ToLowerInvariant())
			{
				case SupportedFormats.Rss20:
					return new FeedMessageFormatter();
				case SupportedFormats.Kml:
					return new KmlFormatter();
				case SupportedFormats.KmlNetwork:
					return new KmlNetworkFormatter();
			}
			
			return null;
		}

		protected virtual FeedFormatter CreateFeedFormatter(Feed feed, IEnumerable<Item> items, string format)
		{
			if(format == SupportedFormats.Rss20)
				return new RssFeedFormatter(feed, items);

			return null;
		}

		protected virtual FeedFormatter CreateFeedFormatter(Feed feed, IEnumerable<Item> items, Type feedFormatterType)
		{
			if (feedFormatterType == typeof(RssFeedFormatter))
				return new RssFeedFormatter(feed, items);

			return null;
		}

		protected virtual Feed CreateFeed(string title, string description, string link)
		{
			return new Feed(title, link, description);
		}

		protected virtual void ValidateFormat(string format)
		{
			if (format != SupportedFormats.Rss20 &&
				format != SupportedFormats.Kml &&
				format != SupportedFormats.KmlNetwork)
			{
				throw new ServiceException(string.Format(CultureInfo.InvariantCulture,
					Resources.NotSupportedFormat, format), HttpStatusCode.BadRequest);
			}
		}

		private DateTime? GetSinceDate(IWebOperationContext context)
		{
			if (context.IncomingRequest.Headers[HttpRequestHeader.IfNoneMatch] != null)
			{
				return DateTime.Parse(context.IncomingRequest.Headers[HttpRequestHeader.IfNoneMatch]);
			}
			else if (context.IncomingRequest.Headers[HttpRequestHeader.IfModifiedSince] != null)
			{
				return DateTime.Parse(context.IncomingRequest.Headers[HttpRequestHeader.IfModifiedSince]);
			}

			return null;
		}

		private void SetSinceDate(IWebOperationContext context, DateTime time)
		{
			context.OutgoingResponse.LastModified = time;
			context.OutgoingResponse.ETag = (time.Kind == DateTimeKind.Utc) ? time.ToString("R", CultureInfo.InvariantCulture) 
				: time.ToUniversalTime().ToString("R", CultureInfo.InvariantCulture);

		}

		private XmlElement GetElement(string xml)
		{
			XmlDocument doc = new XmlDocument();
			doc.Load(XmlReader.Create(new StringReader(xml), readerSettings));

			return doc.DocumentElement;
		}

		static readonly XmlReaderSettings readerSettings = new XmlReaderSettings
		{ 
			IgnoreWhitespace = true,
			CheckCharacters = true,
			ConformanceLevel = ConformanceLevel.Auto,
		};
	}
}
