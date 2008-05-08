using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.IO;
using System.Xml;
using Mesh4n.Tests;
using System.Xml.Serialization;

namespace Mesh4n.Adapters.HttpService.Tests
{
	[TestClass]
	public class RssFeedFormatterTests : TestFixtureBase
	{
		public RssFeedFormatterTests()
		{
		}

		[TestMethod]
		public void ShouldWriteCompleteFeed()
		{
			List<Item> items = new List<Item>();

			string id = Guid.NewGuid().ToString();
			items.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(3)), false)));

			id = Guid.NewGuid().ToString();
			items.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(2)), false)));

			id = Guid.NewGuid().ToString();
			items.Add(new Item(new NullXmlItem(id), Behaviors.Create(id, "kzu", DateTime.Now.Subtract(TimeSpan.FromDays(1)), false)));

			StringWriter sw = new StringWriter();
			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			XmlWriter xw = XmlWriter.Create(sw, set);

			Feed feed = new Feed("Hello World", "http://kzu", "this is my feed");

			RssFeedFormatter formatter = new RssFeedFormatter(feed, items);
			XmlSerializer serializer = new XmlSerializer(typeof(RssFeedFormatter));
			serializer.Serialize(xw, formatter);

			xw.Close();
			
			XmlElement output = GetElement(sw.ToString());

			Assert.AreEqual(1, EvaluateCount(output, "/rss/channel"));
			Assert.AreEqual(3, EvaluateCount(output, "/rss/channel/item"));
			Assert.AreEqual(3, EvaluateCount(output, "/rss/channel/item/sx:sync"));
			Assert.AreEqual(3, EvaluateCount(output, "/rss/channel/item/sx:sync/sx:history"));
			Assert.AreEqual(3, EvaluateCount(output, "/rss/channel/item/sx:sync/sx:history[@sequence=1]"));
		}

		[TestMethod]
		public void ShouldCompleteFeed()
		{
			string xml = @"
			<rss version='2.0' xmlns:sx='http://feedsync.org/2007/feedsync'>
			 <channel>
			  <title>To Do List</title>
			  <description>A list of items to do</description>
			  <link>http://somefakeurl.com/partial.xml</link>
			  <sx:sharing version='0.93' since='2005-02-13T18:30:02Z'
				until='2005-05-23T18:30:02Z' >
			   <sx:related link='http://x.com/all.xml' type='complete' />
			   <sx:related link='http://y.net/B.xml' type='aggregated' 
				title='To Do List (Jacks Copy)' />
			  </sx:sharing>
			  <item>
			   <title>Buy groceries</title>
			   <description>Get milk, eggs, butter and bread</description>
			   <pubDate>Sun, 19 May 02 15:21:36 GMT</pubDate>
			   <customer id='1' />
			   <sx:sync id='0a7903db47fb0fff' updates='3'>
				<sx:history sequence='3' by='JEO2000'/>
				<sx:history sequence='2' by='REO1750'/>
				<sx:history sequence='1' by='REO1750'/>
				<sx:conflicts>
				  <item>
				   <title>Buy icecream</title>
				   <description>Get hagen daaz</description>
				   <pubDate>Sun, 19 May 02 12:21:36 GMT</pubDate>
				   <customer id='1' />
				   <sx:sync id='0a7903db47fb0fff' updates='1'>
					<sx:history sequence='1' by='REO1750'/>
				   </sx:sync>
				  </item>
				</sx:conflicts>
			   </sx:sync>
			  </item>
			 </channel>
			</rss>";

			XmlReader reader = GetReader(xml);

			XmlSerializer serializer = new XmlSerializer(typeof(RssFeedFormatter));

			RssFeedFormatter formatter = (RssFeedFormatter)serializer.Deserialize(reader);

			Feed feed = formatter.Feed;
			IEnumerable<Item> i = formatter.Items;

			Assert.AreEqual("To Do List", feed.Title);
			Assert.AreEqual("A list of items to do", feed.Description);
			Assert.AreEqual("http://somefakeurl.com/partial.xml", feed.Link);
			Assert.AreEqual(2, feed.Sharing.Related.Count);
			List<Item> items = new List<Item>(i);
			Assert.AreEqual(1, items.Count);
			Assert.AreEqual("Buy groceries", items[0].XmlItem.Title);
			Assert.AreEqual("Get milk, eggs, butter and bread", items[0].XmlItem.Description);
			Assert.AreEqual(3, items[0].Sync.Updates);
			Assert.AreEqual("JEO2000", items[0].Sync.LastUpdate.By);
			Assert.AreEqual("0a7903db47fb0fff", items[0].Sync.Id);
			Assert.AreEqual("0a7903db47fb0fff", items[0].XmlItem.Id);
			Assert.AreEqual(1, items[0].Sync.Conflicts.Count);
			Assert.AreEqual("Buy icecream", items[0].Sync.Conflicts[0].XmlItem.Title);
			Assert.AreEqual(@"<payload>
  <pubDate>Sun, 19 May 02 15:21:36 GMT</pubDate>
  <customer id=""1"" />
</payload>", ReadToEnd(new XmlNodeReader(items[0].XmlItem.Payload)));
		}
	}
}
