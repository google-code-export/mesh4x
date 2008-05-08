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
			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry1",
			    new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", typeof(MockSyncAdapter)));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>(MockBehavior.Strict);
			managerMock.Expect(manager => manager.Load()).Returns(configuration).Verifiable();

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object };
			FeedFormatter feed = syncService.GetRssFeeds();

			Assert.IsNotNull(feed);
			Assert.IsInstanceOfType(feed, typeof(RssFeedFormatter));
			managerMock.Verify();
		}

		[TestMethod]
		public void ShouldGetFeedsWithSpecifiedFormat()
		{
			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry1",
				new XamlFeedConfigurationEntry("Foo", "Foo Title", "Foo Description", typeof(MockSyncAdapter)));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect<FeedConfiguration>(manager => manager.Load()).Returns(configuration).Verifiable();

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object };
			FeedFormatter feed = syncService.GetFeeds(SupportedFormats.Rss20);

			Assert.IsNotNull(feed);
			Assert.IsInstanceOfType(feed, typeof(RssFeedFormatter));
			managerMock.Verify();
		}

		[TestMethod]
		public void ShouldGetEmptyFeed()
		{
			FeedConfiguration configuration = new FeedConfiguration();

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect<FeedConfiguration>(manager => manager.Load()).Returns(configuration).Verifiable();

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object };
			FeedFormatter feed = syncService.GetFeeds(SupportedFormats.Rss20);

			managerMock.Verify();

			Assert.IsNotNull(feed);
		}

		[TestMethod]
		public void ShouldReturnBadRequestIfInvalidFormat()
		{
			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			responseContextMock.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.BadRequest, sc));
			
			SyncService syncService = new SyncService { OperationContext = webContextMock.Object };
			FeedFormatter feed = syncService.GetFeeds("FooFormat");

			responseContextMock.VerifyAll();
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

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry", new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load()).Returns(configuration);

			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IIncomingWebRequestContext> requestContextMock = new Mock<IIncomingWebRequestContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();
			
			webContextMock.ExpectGet(webContext => webContext.IncomingRequest).Returns(requestContextMock.Object);
			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			requestContextMock.ExpectGet(requestContext => requestContext.Headers).Returns(new WebHeaderCollection()).Verifiable();

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object, OperationContext = webContextMock.Object };
			FeedFormatter feed = syncService.GetFeed("entry", SupportedFormats.Rss20);

			Assert.IsNotNull(feed);
			Assert.IsNotNull(feed.Feed);
			Assert.IsNotNull(feed.Items);
			Assert.AreEqual(3, Count(feed.Items));

			mockAdapter.Verify();
			requestContextMock.Verify();
			
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
			
			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry", new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load()).Returns(configuration);

			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IIncomingWebRequestContext> requestContextMock = new Mock<IIncomingWebRequestContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			WebHeaderCollection webHeaderCollection = new WebHeaderCollection();
			webHeaderCollection.Add(HttpRequestHeader.IfModifiedSince, DateTime.Now.ToUniversalTime().ToString("R", CultureInfo.InvariantCulture));
						
			webContextMock.ExpectGet(webContext => webContext.IncomingRequest).Returns(requestContextMock.Object);
			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			requestContextMock.ExpectGet(requestContext => requestContext.Headers).Returns(webHeaderCollection).Verifiable();

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object, OperationContext = webContextMock.Object };
			FeedFormatter feed = syncService.GetFeed("entry", SupportedFormats.Rss20);

			requestContextMock.Verify();

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

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry", new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load()).Returns(configuration);

			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IIncomingWebRequestContext> requestContextMock = new Mock<IIncomingWebRequestContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			WebHeaderCollection webHeaderCollection = new WebHeaderCollection();
			webHeaderCollection.Add(HttpRequestHeader.IfModifiedSince, DateTime.Now.ToUniversalTime().ToString("R", CultureInfo.InvariantCulture));

			webContextMock.ExpectGet(webContext => webContext.IncomingRequest).Returns(requestContextMock.Object);
			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			requestContextMock.ExpectGet(requestContext => requestContext.Headers).Returns(webHeaderCollection);
			responseContextMock.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.NotModified, sc));
			responseContextMock.ExpectSet(responseContext => responseContext.SuppressEntityBody).Callback(seb => Assert.AreEqual(true, seb));
			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object, OperationContext = webContextMock.Object };
			FeedFormatter feed = syncService.GetFeed("entry", SupportedFormats.Rss20);

			mockAdapter.Verify();
			responseContextMock.VerifyAll();
			Assert.IsNull(feed);
			
		}

		[TestMethod]
		public void ShouldSetETagAndLastModifiedHeadersWhenGetFeed()
		{
			List<Item> items = new List<Item>();

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.GetAll()).Returns(items);

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry", new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load()).Returns(configuration);

			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IIncomingWebRequestContext> requestContextMock = new Mock<IIncomingWebRequestContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			webContextMock.ExpectGet(webContext => webContext.IncomingRequest).Returns(requestContextMock.Object);
			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			requestContextMock.ExpectGet(requestContext => requestContext.Headers).Returns(new WebHeaderCollection());
			responseContextMock.ExpectSet(responseContext => responseContext.LastModified);
			responseContextMock.ExpectSet(responseContext => responseContext.ETag);

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object, OperationContext = webContextMock.Object };
			FeedFormatter feed = syncService.GetFeed("entry", SupportedFormats.Rss20);

			responseContextMock.VerifyAll();
		}

		[TestMethod]
		public void ShouldGetItem()
		{
			string id = Guid.NewGuid().ToString();

			Item item = new Item(new NullXmlItem(id), null);

			Mock<ISyncAdapter> mockAdapter = new Mock<ISyncAdapter>();
			mockAdapter.Expect(adapter => adapter.Get(id)).Returns(item);

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry", new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load()).Returns(configuration);

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object };
			FeedFormatter feed = syncService.GetItem("entry", id, SupportedFormats.Rss20);

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

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry", new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load()).Returns(configuration);

			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IIncomingWebRequestContext> requestContextMock = new Mock<IIncomingWebRequestContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			webContextMock.ExpectGet(webContext => webContext.IncomingRequest).Returns(requestContextMock.Object);
			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			responseContextMock.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.NotFound, sc));

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object, OperationContext=webContextMock.Object };
			FeedFormatter feed = syncService.GetItem("entry", id, SupportedFormats.Rss20);

			responseContextMock.VerifyAll();
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

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry", new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load()).Returns(configuration);

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), items);
			
			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object };
			FeedFormatter conflictsFormatter = syncService.PostFeed("entry", SupportedFormats.Rss20, feedFormatter);

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

			FeedConfiguration configuration = new FeedConfiguration();
			configuration.Add("entry", new FeedConfigurationEntry("Foo", "Foo Title", "Foo Description", mockAdapter.Object));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect(manager => manager.Load()).Returns(configuration);

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), new Item[] { item });

			SyncService syncService = new SyncService { ConfigurationManager = managerMock.Object };
			FeedFormatter conflictsFormatter = syncService.PostItem("entry", id, SupportedFormats.Rss20, feedFormatter);

			Assert.IsNotNull(conflictsFormatter);
			Assert.IsNotNull(conflictsFormatter.Feed);
			Assert.IsNotNull(conflictsFormatter.Items);
			Assert.AreEqual(0, Count(conflictsFormatter.Items));

			mockAdapter.Verify();
		}

		[TestMethod]
		public void ShouldBadRequestIfDifferentIdWhenPostItem()
		{
			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			responseContextMock.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.BadRequest, sc));

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), new Item[] {});

			SyncService syncService = new SyncService { OperationContext = webContextMock.Object };
			FeedFormatter conflictsFormatter = syncService.PostItem("entry", Guid.NewGuid().ToString(), SupportedFormats.Rss20, feedFormatter);

			responseContextMock.VerifyAll();
			Assert.IsNull(conflictsFormatter);
		}

		[TestMethod]
		public void ShouldThrowIfInvalidFormatWhenPostItem()
		{
			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			responseContextMock.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.BadRequest, sc));

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), new Item[] {});

			SyncService syncService = new SyncService { OperationContext = webContextMock.Object };
			FeedFormatter conflictsFormatter = syncService.PostItem("entry", Guid.NewGuid().ToString(), "FooFormat", feedFormatter);

			webContextMock.VerifyAll();
			Assert.IsNull(conflictsFormatter);
		}

		[TestMethod]
		public void ShouldThrowIfInvalidFormatWhenPostFeed()
		{
			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			responseContextMock.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.BadRequest, sc));

			FeedFormatter feedFormatter = new RssFeedFormatter(new Feed(), new Item[] {});

			SyncService syncService = new SyncService { OperationContext = webContextMock.Object };
			FeedFormatter conflictsFormatter = syncService.PostFeed("entry", "FooFormat", feedFormatter);

			webContextMock.VerifyAll();
			Assert.IsNull(conflictsFormatter);
		}

		[TestMethod]
		public void ShouldThrowIfInvalidFeedName()
		{
			Mock<IWebOperationContext> webContextMock = new Mock<IWebOperationContext>();
			Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

			webContextMock.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			responseContextMock.ExpectSet(responseContext => responseContext.StatusCode).Callback(sc => Assert.AreEqual(HttpStatusCode.NotFound, sc));

			Mock<IFeedConfigurationManager> managerMock = new Mock<IFeedConfigurationManager>();
			managerMock.Expect<FeedConfiguration>(manager => manager.Load()).Returns(new FeedConfiguration());

			SyncService syncService = new SyncService{ ConfigurationManager = managerMock.Object, OperationContext = webContextMock.Object };
			FeedFormatter feed = syncService.GetFeed("entry1", SupportedFormats.Rss20);

			responseContextMock.VerifyAll();
			Assert.IsNull(feed);


		}
	}
}
