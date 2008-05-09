using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Mesh4n.Adapters.HttpService.Configuration;
using System.ServiceModel.Channels;
using System.ServiceModel;
using Moq;
using Mesh4n.Adapters.HttpService.WebContext;
using Mesh4n.Tests;
using System.Net;
using System.Globalization;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestClass]
	public class SyncServiceTests : TestFixtureBase
	{
		public SyncServiceTests()
		{
		}

		[TestMethod]
		public void ShouldGetRssFeeds()
		{
			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", new MockSyncAdapter());
			List<FeedConfigurationEntry> entries = new List<FeedConfigurationEntry>();
			entries.Add(entry);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.LoadAll()).Returns(entries);

			SyncService syncService = new SyncService (managerMock.Object, null);
			FeedFormatter feed = syncService.GetRssFeeds();

			Assert.IsNotNull(feed);
			Assert.IsInstanceOfType(feed, typeof(RssFeedFormatter));
		}

		[TestMethod]
		public void ShouldGetFeedsWithSpecifiedFormat()
		{
			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", new MockSyncAdapter());
			List<FeedConfigurationEntry> entries = new List<FeedConfigurationEntry>();
			entries.Add(entry);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.LoadAll()).Returns(entries);

			SyncService syncService = new SyncService(managerMock.Object, null);
			FeedFormatter feed = syncService.GetFeeds(SupportedFormats.Rss20);

			Assert.IsNotNull(feed);
			Assert.IsInstanceOfType(feed, typeof(RssFeedFormatter));
		}

		[TestMethod]
		public void ShouldGetEmptyFeed()
		{
			List<FeedConfigurationEntry> entries = new List<FeedConfigurationEntry>();

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.LoadAll()).Returns(entries);

			SyncService syncService = new SyncService(managerMock.Object, null);
			FeedFormatter feed = syncService.GetFeeds(SupportedFormats.Rss20);

			Assert.IsNotNull(feed);
		}

		[TestMethod]
		public void ShouldReturnBadRequestIfInvalidFormat()
		{
			WebContextMock webContextMock = new WebContextMock();
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.BadRequest, sc));

			SyncService syncService = new SyncService(null, webContextMock.Object);
			FeedFormatter feed = syncService.GetFeeds("FooFormat");

			webContextMock.OutgoingWebResponseContext.VerifyAll();
			Assert.IsNull(feed);
		}

		[TestMethod]
		public void ShouldGetCompleteFeed()
		{
			List<Item> items = new List<Item>();

			items.Add(new Item(new NullXmlItem(Guid.NewGuid().ToString()), null));
			items.Add(new Item(new NullXmlItem(Guid.NewGuid().ToString()), null));
			items.Add(new Item(new NullXmlItem(Guid.NewGuid().ToString()), null));

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.GetAll()).Returns(items).Verifiable();

			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load("Foo")).Returns(entry);

			WebContextMock webContextMock = new WebContextMock();
			webContextMock.IncomingWebRequestContext.ExpectGet(requestContext => requestContext.Headers).Returns(new WebHeaderCollection()).Verifiable();

			SyncService syncService = new SyncService(managerMock.Object, webContextMock.Object);
			FeedFormatter feed = syncService.GetFeed("Foo", SupportedFormats.Rss20);

			Assert.IsNotNull(feed);
			Assert.IsNotNull(feed.Feed);
			Assert.IsNotNull(feed.Items);
			Assert.AreEqual(3, Count(feed.Items));

			mockAdapter.Verify();
			webContextMock.IncomingWebRequestContext.Verify();
			
		}

		[TestMethod]
		public void ShouldGetPartialFeedWhenIfModifiedSinceHeaderExists()
		{
			List<Item> items = new List<Item>();

			items.Add(new Item(new NullXmlItem(Guid.NewGuid().ToString()), null));
			items.Add(new Item(new NullXmlItem(Guid.NewGuid().ToString()), null));
			items.Add(new Item(new NullXmlItem(Guid.NewGuid().ToString()), null));

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.GetAllSince(It.IsAny<DateTime>())).Returns(items).Verifiable();

			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load("Foo")).Returns(entry);

			WebContextMock webContextMock = new WebContextMock();
			WebHeaderCollection webHeaderCollection = new WebHeaderCollection();
			webHeaderCollection.Add(HttpRequestHeader.IfModifiedSince, DateTime.Now.ToUniversalTime().ToString("R", CultureInfo.InvariantCulture));
						
			webContextMock.IncomingWebRequestContext.ExpectGet(requestContext => requestContext.Headers).Returns(webHeaderCollection).Verifiable();

			SyncService syncService = new SyncService(managerMock.Object, webContextMock.Object);
			FeedFormatter feed = syncService.GetFeed("Foo", SupportedFormats.Rss20);

			webContextMock.IncomingWebRequestContext.Verify();
			Assert.IsNotNull(feed);
			Assert.IsNotNull(feed.Feed);
			Assert.IsNotNull(feed.Items);
			Assert.AreEqual(3, Count(feed.Items));
			
		}

		[TestMethod]
		public void ShouldReturnNullAndSetHeadersIfNoItemsExistWhenGetPartialFeed()
		{
			List<Item> items = new List<Item>();

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.GetAllSince(It.IsAny<DateTime>())).Returns(items).Verifiable();

			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load("Foo")).Returns(entry);

			WebContextMock webContextMock = new WebContextMock();

			WebHeaderCollection webHeaderCollection = new WebHeaderCollection();
			webHeaderCollection.Add(HttpRequestHeader.IfModifiedSince, DateTime.Now.ToUniversalTime().ToString("R", CultureInfo.InvariantCulture));

			webContextMock.IncomingWebRequestContext.ExpectGet(requestContext => requestContext.Headers).Returns(webHeaderCollection);
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.NotModified, sc));
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.SuppressEntityBody).Callback(seb => Assert.AreEqual(true, seb));
			
			SyncService syncService = new SyncService(managerMock.Object, webContextMock.Object);
			FeedFormatter feed = syncService.GetFeed("Foo", SupportedFormats.Rss20);

			mockAdapter.Verify();
			webContextMock.OutgoingWebResponseContext.VerifyAll();
			Assert.IsNull(feed);
			
		}

		[TestMethod]
		public void ShouldSetETagAndLastModifiedHeadersWhenGetFeed()
		{
			List<Item> items = new List<Item>();

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.GetAll()).Returns(items);

			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load("Foo")).Returns(entry);

			WebContextMock webContextMock = new WebContextMock();
			webContextMock.IncomingWebRequestContext.ExpectGet(requestContext => requestContext.Headers).Returns(new WebHeaderCollection());
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.LastModified);
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.ETag);

			SyncService syncService = new SyncService(managerMock.Object, webContextMock.Object);
			FeedFormatter feed = syncService.GetFeed("Foo", SupportedFormats.Rss20);

			webContextMock.OutgoingWebResponseContext.VerifyAll();
		}

		[TestMethod]
		public void ShouldGetItem()
		{
			string id = Guid.NewGuid().ToString();

			Item item = new Item(new NullXmlItem(id), null);

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.Get(id)).Returns(item);

			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load("Foo")).Returns(entry);

			SyncService syncService = new SyncService(managerMock.Object, null);
			FeedFormatter feed = syncService.GetItem("Foo", id, SupportedFormats.Rss20);

			Assert.IsNotNull(feed);
			Assert.IsNotNull(feed.Feed);
			Assert.IsNotNull(feed.Items);
			Assert.AreEqual(1, Count(feed.Items));
		}

		[TestMethod]
		public void ShouldReturnNullAndSetHeadersIfItemNotFound()
		{
			string id = Guid.NewGuid().ToString();

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.Get(It.IsAny<string>()));

			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load("Foo")).Returns(entry);

			WebContextMock webContextMock = new WebContextMock();
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.NotFound, sc));

			SyncService syncService = new SyncService(managerMock.Object, webContextMock.Object);
			FeedFormatter feed = syncService.GetItem("Foo", id, SupportedFormats.Rss20);

			webContextMock.OutgoingWebResponseContext.VerifyAll();
			Assert.IsNull(feed);
		}

		[TestMethod]
		public void ShouldPostFeed()
		{
			List<Item> items = new List<Item>();

			string id = Guid.NewGuid().ToString();
			items.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "pci", DateTime.Now, false)));
			
			id = Guid.NewGuid().ToString();
			items.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "pci", DateTime.Now, false)));
			
			id = Guid.NewGuid().ToString();
			items.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "pci", DateTime.Now, false)));

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.Add(items[0])).Verifiable();
			mockAdapter.Expect(adapter => adapter.Add(items[1])).Verifiable();
			mockAdapter.Expect(adapter => adapter.Add(items[2])).Verifiable();

			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load("Foo")).Returns(entry);

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), items);

			SyncService syncService = new SyncService(managerMock.Object, null);
			FeedFormatter conflictsFormatter = syncService.PostFeed("Foo", SupportedFormats.Rss20, feedFormatter);

			Assert.IsNotNull(conflictsFormatter);
			Assert.IsNotNull(conflictsFormatter.Feed);
			Assert.IsNotNull(conflictsFormatter.Items);
			Assert.AreEqual(0, Count(conflictsFormatter.Items));

			mockAdapter.Verify();
		}

		[TestMethod]
		public void ShouldPostItem()
		{
			string id = Guid.NewGuid().ToString();
			Item item = new Item(new NullXmlItem(id), Behaviors.Create(id, "pci", DateTime.Now, false));

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.Add(item)).Verifiable();

			FeedConfigurationEntry entry = new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object);

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load("Foo")).Returns(entry);

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), new Item[] { item });

			SyncService syncService = new SyncService(managerMock.Object, null);
			FeedFormatter conflictsFormatter = syncService.PostItem("Foo", id, SupportedFormats.Rss20, feedFormatter);

			Assert.IsNotNull(conflictsFormatter);
			Assert.IsNotNull(conflictsFormatter.Feed);
			Assert.IsNotNull(conflictsFormatter.Items);
			Assert.AreEqual(0, Count(conflictsFormatter.Items));

			mockAdapter.Verify();
		}

		[TestMethod]
		public void ShouldBadRequestIfDifferentIdWhenPostItem()
		{
			WebContextMock webContextMock = new WebContextMock();
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.BadRequest, sc));

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), new Item[] {});

			SyncService syncService = new SyncService(null, webContextMock.Object);
			FeedFormatter conflictsFormatter = syncService.PostItem("entry", Guid.NewGuid().ToString(), SupportedFormats.Rss20, feedFormatter);

			webContextMock.OutgoingWebResponseContext.VerifyAll();
			Assert.IsNull(conflictsFormatter);
		}

		[TestMethod]
		public void ShouldThrowIfInvalidFormatWhenPostItem()
		{
			WebContextMock webContextMock = new WebContextMock();
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.BadRequest, sc));

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), new Item[] {});

			SyncService syncService = new SyncService(null, webContextMock.Object);
			FeedFormatter conflictsFormatter = syncService.PostItem("entry", Guid.NewGuid().ToString(), "FooFormat", feedFormatter);

			webContextMock.OutgoingWebResponseContext.VerifyAll();
			Assert.IsNull(conflictsFormatter);
		}

		[TestMethod]
		public void ShouldThrowIfInvalidFormatWhenPostFeed()
		{
			WebContextMock webContextMock = new WebContextMock();
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.BadRequest, sc));

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), new Item[] {});

			SyncService syncService = new SyncService(null, webContextMock.Object);
			FeedFormatter conflictsFormatter = syncService.PostFeed("entry", "FooFormat", feedFormatter);

			webContextMock.OutgoingWebResponseContext.VerifyAll();
			Assert.IsNull(conflictsFormatter);
		}

		[TestMethod]
		public void ShouldThrowIfInvalidFeedName()
		{
			WebContextMock webContextMock = new WebContextMock();
			webContextMock.OutgoingWebResponseContext.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.NotFound, sc));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load(It.IsAny<string>()));

			SyncService syncService = new SyncService(managerMock.Object, webContextMock.Object);
			FeedFormatter feed = syncService.GetFeed("MyNewFeed", SupportedFormats.Rss20);

			webContextMock.OutgoingWebResponseContext.VerifyAll();
			Assert.IsNull(feed);
		}

		public class WebContextMock : Mock<IWebOperationContext>
		{
			Mock<IIncomingWebRequestContext> requestContextMock = new Mock<IIncomingWebRequestContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			public WebContextMock()
				: base()
			{
				this.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
				this.ExpectGet(webContext => webContext.IncomingRequest).Returns(requestContextMock.Object);
			}

			public Mock<IIncomingWebRequestContext> IncomingWebRequestContext
			{
				get { return requestContextMock; }
			}

			public Mock<IOutgoingWebResponseContext> OutgoingWebResponseContext
			{
				get { return responseContextMock; }
			}
		}

	}
}
