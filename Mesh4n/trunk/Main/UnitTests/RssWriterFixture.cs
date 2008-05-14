#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;
using System.Xml;
using System.IO;
using System.Xml.XPath;
using System.Text.RegularExpressions;

namespace Mesh4n.Tests
{
	[TestClass]
	public class RssWriterFixture : TestFixtureBase
	{
		[TestMethod]
		public void ShouldWriteEmptyItemPayload()
		{
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");
			feed.Sharing.Expires = new DateTime(2007, 6, 7);
			feed.Sharing.Related.Add(new Related("http://kzu/full", RelatedType.Complete, "Complete feed"));

			FeedWriter writer = new RssFeedWriter(xw);

			writer.Write(feed, new Item(new NullXmlItem("1"), new Sync("1")));

			xw.Flush();
		}

		[TestMethod]
		public void ShouldWriteEmptyFeed()
		{
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed();

			FeedWriter writer = new RssFeedWriter(xw);

			writer.Write(feed, new Item(new NullXmlItem("1"), new Sync("1")));

			xw.Flush();

			Assert.AreNotEqual(0, sw.GetStringBuilder().ToString().Length);
		}

		[TestMethod]
		public void ShouldWriteCompleteFeed()
		{
			MockSyncRepository syncRepo = new MockSyncRepository();
			MockXmlRepository xmlRepo = new MockXmlRepository();
			ISyncAdapter repo = new SplitSyncAdapter(xmlRepo, syncRepo);

			string id = Guid.NewGuid().ToString();
			repo.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(3)), false)));

			id = Guid.NewGuid().ToString();
			repo.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(2)), false)));

			id = Guid.NewGuid().ToString();
			repo.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1)), false)));
			
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");
			feed.Sharing.Expires = new DateTime(2007, 6, 7);
			feed.Sharing.Related.Add(new Related("http://kzu/full", RelatedType.Complete, "Complete feed"));

			FeedWriter writer = new RssFeedWriter(xw);
			writer.Write(feed, repo.GetAll());

			xw.Flush();

			XmlElement output = GetElement(sw.ToString());

			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel"));
			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel/sx:sharing"));
			Assert.AreEqual(Timestamp.ToString(feed.Sharing.Expires.Value), EvaluateString(output, "/rss/channel/sx:sharing/@expires"));
			Assert.AreEqual("http://kzu/full", EvaluateString(output, "/rss/channel/sx:sharing/sx:related/@link"));
			Assert.AreEqual("Complete feed", EvaluateString(output, "/rss/channel/sx:sharing/sx:related/@title"));
			Assert.AreEqual(3, EvaluateCount(output, "/rss/channel/item"));
			Assert.AreEqual(3, EvaluateCount(output, "/rss/channel/item/sx:sync"));
			Assert.AreEqual(3, EvaluateCount(output, "/rss/channel/item/sx:sync/sx:history"));
			Assert.AreEqual(3, EvaluateCount(output, "/rss/channel/item/sx:sync/sx:history[@sequence=1]"));
		}

		[TestMethod]
		public void ShouldWriteIfNullSharing()
		{
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");

			FeedWriter writer = new RssFeedWriter(xw);
			writer.Write(feed);

			xw.Flush();
			Console.WriteLine(sw.ToString());
		}

		[Ignore]
		[TestMethod]
		public void ShouldPublishLast1Day()
		{
			MockSyncRepository syncRepo = new MockSyncRepository();
			MockXmlRepository xmlRepo = new MockXmlRepository();
			ISyncAdapter repo = new SplitSyncAdapter(xmlRepo, syncRepo);

			string id = Guid.NewGuid().ToString();
			repo.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(3)), false)));

			id = Guid.NewGuid().ToString();
			repo.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(2)), false)));

			id = Guid.NewGuid().ToString();
			repo.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1)), false)));

			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");
			feed.Sharing.Related.Add(new Related("http://kzu/full", RelatedType.Complete));

			FeedWriter writer = new RssFeedWriter(xw);
			writer.Write(feed, repo.GetAllSince(DateTime.Now.Subtract(TimeSpan.FromDays(1))));

			xw.Flush();

			XmlNode output = GetElement(sw.ToString());

			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel"));
			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel/sx:sharing"));
			Assert.AreEqual("http://kzu/full", EvaluateString(output, "/rss/channel/sx:sharing/sx:related/@link"));
			// We get two items, as the one which was created for yesterday, 
			// would be in the middle of the day, while the "last N days" logic 
			// is since the starting of yesterday.
			Assert.AreEqual(2, EvaluateCount(output, "/rss/channel/item"));
			Assert.AreEqual(2, EvaluateCount(output, "/rss/channel/item/sx:sync"));
			Assert.AreEqual(2, EvaluateCount(output, "/rss/channel/item/sx:sync/sx:history"));
			Assert.AreEqual(2, EvaluateCount(output, "/rss/channel/item/sx:sync/sx:history[@sequence=1]"));
		}

		[TestMethod]
		public void ShouldWriteFeedWithDeletedItem()
		{
			IContentAdapter xmlRepo = new MockXmlRepository().AddOneItem();
			ISyncAdapter repo = new SplitSyncAdapter(xmlRepo, new MockSyncRepository());

			Item item = GetFirst<Item>(repo.GetAll());
			xmlRepo.Remove(item.XmlItem.Id);

			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");
			feed.Sharing.Related.Add(new Related("http://kzu/full", RelatedType.Complete));

			FeedWriter writer = new RssFeedWriter(xw);
			writer.Write(feed, repo.GetAll());

			xw.Flush();

			XmlNode output = GetElement(sw.ToString());

			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel"));
			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel/sx:sharing"));
			Assert.AreEqual("http://kzu/full", EvaluateString(output, "/rss/channel/sx:sharing/sx:related/@link"));
			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel/item"));
			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel/item/sx:sync"));
			Assert.AreEqual(true, XmlConvert.ToBoolean(EvaluateString(output, "/rss/channel/item/sx:sync/@deleted")));
		}

		[TestMethod]
		public void ShouldWriteHistoryWithNullWhen()
		{
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");
			feed.Sharing.Related.Add(new Related("http://kzu/full", RelatedType.Complete));

			XmlElement payload = new XmlDocument().CreateElement("payload");
			payload.InnerXml = "<geo:point xmlns:geo='http://geo'>25</geo:point>";

			Item item = new Item(
				new XmlItem("foo", "bar", payload, DateTime.Now),
				Behaviors.Create("1", "kzu", DateTime.Now, false));

			FeedWriter writer = new RssFeedWriter(xw);
			writer.Write(feed, item);

			xw.Flush();
		}

		[TestMethod]
		public void ShouldNotWrapPayload()
		{
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");
			feed.Sharing.Related.Add(new Related("http://kzu/full", RelatedType.Complete));

			XmlElement payload = new XmlDocument().CreateElement("payload");
			payload.InnerXml = "<geo:point xmlns:geo='http://geo'>25</geo:point>";

			Item item = new Item(
				new XmlItem("foo", "bar", payload, DateTime.Now),
				Behaviors.Create("1", "kzu", DateTime.Now, false));

			FeedWriter writer = new RssFeedWriter(xw);
			writer.Write(feed, item);

			xw.Flush();

			Assert.IsFalse(sw.ToString().IndexOf("<payload>") != -1);
		}

		[TestMethod]
		public void ShouldNotDuplicateTitleDescription()
		{
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			XmlElement payload = new XmlDocument().CreateElement("payload");
			payload.InnerXml = "<title>title</title><description>description</description><geo:point xmlns:geo='http://geo'>25</geo:point>";

			Item item = new Item(
				new XmlItem("foo", "bar", payload, DateTime.Now),
				Behaviors.Create("1", "kzu", null, false));

			FeedWriter writer = new RssFeedWriter(xw);
			writer.Write(item);

			xw.Flush();

			string xml = ReadToEnd(GetReader(sw.ToString()));
			string expected = @"<item xmlns:sx=""http://feedsync.org/2007/feedsync"">
  <title>foo</title>
  <description>bar</description>
  <geo:point xmlns:geo=""http://geo"">25</geo:point>
  <author>kzu@example.com</author>
  <sx:sync id=""1"" updates=""1"" deleted=""false"" noconflicts=""false"">
    <sx:history sequence=""1"" by=""kzu"" />
  </sx:sync>
</item>";

			Assert.AreEqual(expected, xml);
		}

		[TestMethod]
		public void ShouldAddAuthorEmailFromLastUpdateIfValidEmail()
		{
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			using (XmlWriter xw = XmlWriter.Create(sw, set))
			{
				Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");

				XmlElement payload = new XmlDocument().CreateElement("payload");
				payload.InnerXml = "<geo:point xmlns:geo='http://geo'>25</geo:point>";

				Item item = new Item(
					new XmlItem("foo", "bar", payload, DateTime.Now),
					Behaviors.Create("1", "nospam@clariusconsulting.net", DateTime.Now, false));

				FeedWriter writer = new RssFeedWriter(xw);
				writer.Write(feed, item);
			}

			using (XmlReader reader = GetReader(sw.ToString()))
			{
				Assert.IsTrue(reader.ReadToDescendant("author"));
				Assert.AreEqual("nospam@clariusconsulting.net", reader.ReadElementContentAsString());				
			}
		}

		[TestMethod]
		public void ShouldGenerateAuthorEmailFromLastUpdateIfInvalidEmail()
		{
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			using (XmlWriter xw = XmlWriter.Create(sw, set))
			{
				Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");

				XmlElement payload = new XmlDocument().CreateElement("payload");
				payload.InnerXml = "<geo:point xmlns:geo='http://geo'>25</geo:point>";

				Item item = new Item(
					new XmlItem("foo", "bar", payload, DateTime.Now),
					Behaviors.Create("1", "MACHINE\\UserName", DateTime.Now, false));

				FeedWriter writer = new RssFeedWriter(xw);
				writer.Write(feed, item);
			}

			using (XmlReader reader = GetReader(sw.ToString()))
			{
				Assert.IsTrue(reader.ReadToDescendant("author"));
				Assert.AreEqual("UserName@MACHINE.com", reader.ReadElementContentAsString());
			}
		}

		[TestMethod]
		public void ShouldGenerateAuthorEmailFromLastUpdateIfDeviceGuid()
		{
			string deviceId = Guid.NewGuid().ToString();
			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			using (XmlWriter xw = XmlWriter.Create(sw, set))
			{
				Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");

				XmlElement payload = new XmlDocument().CreateElement("payload");
				payload.InnerXml = "<geo:point xmlns:geo='http://geo'>25</geo:point>";

				Item item = new Item(
					new XmlItem("foo", "bar", payload, DateTime.Now),
					Behaviors.Create("1", deviceId, DateTime.Now, false));

				FeedWriter writer = new RssFeedWriter(xw);
				writer.Write(feed, item);
			}

			using (XmlReader reader = GetReader(sw.ToString()))
			{
				Assert.IsTrue(reader.ReadToDescendant("author"));
				Assert.AreEqual(deviceId + "@example.com", reader.ReadElementContentAsString());
			}
		}

		[TestMethod]
		public void ShouldGenerateAuthorEmailFromDeviceAuthorIfNoBy()
		{
			Regex emailExp = new Regex(@"[a-z0-9+_-]+@[a-z0-9][a-z0-9-]*(\.[a-z0-9-]+)+", RegexOptions.Compiled | RegexOptions.ExplicitCapture | RegexOptions.IgnoreCase);

			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			using (XmlWriter xw = XmlWriter.Create(sw, set))
			{
				Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");

				XmlElement payload = new XmlDocument().CreateElement("payload");
				payload.InnerXml = "<geo:point xmlns:geo='http://geo'>25</geo:point>";

				Item item = new Item(
					new XmlItem("foo", "bar", payload, DateTime.Now),
					Behaviors.Create("1", null, DateTime.Now, false));

				FeedWriter writer = new RssFeedWriter(xw);
				writer.Write(feed, item);
			}

			using (XmlReader reader = GetReader(sw.ToString()))
			{
				Assert.IsTrue(reader.ReadToDescendant("author"));

				Assert.IsTrue(emailExp.IsMatch(reader.ReadElementContentAsString()));
			}
		}

		[TestMethod]
		public void ShouldWriteFeedPayload()
		{
			XmlElement payload = new XmlDocument().CreateElement("payload");
			payload.InnerXml = "<somedata>fizz</somedata><id d='buz'/>";

			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed", payload);

			FeedWriter writer = new RssFeedWriter(xw);
			writer.Write(feed);

			xw.Flush();

			string xml = ReadToEnd(GetReader(sw.ToString()));
			string expected = @"<rss xmlns:sx=""http://feedsync.org/2007/feedsync"" version=""2.0"">
  <channel>
    <title>Hello World</title>
    <description>this is my feed</description>
    <link>http://kzu</link>
    <somedata>fizz</somedata>
    <id d=""buz"" />
  </channel>
</rss>";

			Assert.AreEqual(expected, xml);
		}
	}
}
