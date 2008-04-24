#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Net;

namespace SimpleSharing.Tests
{
	[TestClass]
	public class MSLiveLabsFixture : TestFixtureBase
	{
		[Ignore]
		[TestMethod]
		public void ShouldUploadEmptyItems()
		{
			ISyncRepository syncRepo = new MockSyncRepository();
			IXmlRepository xmlRepo = new MockXmlRepository();
			SyncEngine engine = new SyncEngine(xmlRepo, syncRepo);

			WebRequest req = WebRequest.Create("http://sse.mslivelabs.com/feed.sse?i=293578659bbf40bfb8aa0b9102c36766&c=1&alt=RSS");
			req.Timeout = -1;
			req.Method = "PUT";

			XmlWriterSettings set = new XmlWriterSettings();
			set.CloseOutput = true;
			Feed feed = new Feed("Client feed", "http://client/feed", "Client feed description");
			using (XmlWriter w = XmlWriter.Create(req.GetRequestStream(), set))
			{
				engine.Publish(feed, new RssFeedWriter(w));
			}

			req.GetResponse();
		}

		[Ignore]
		[TestMethod]
		public void ShouldSyncItems()
		{
			ISyncRepository syncRepo = new MockSyncRepository();
			IXmlRepository xmlRepo = new MockXmlRepository();
			SyncEngine engine = new SyncEngine(xmlRepo, syncRepo);

			using (XmlReader xr = XmlReader.Create("http://sse.mslivelabs.com/feed.sse?i=293578659bbf40bfb8aa0b9102c36766&c=1&alt=Rss"))
			{
				IList<Item> conflicts = engine.Subscribe(new RssFeedReader(xr));

				Assert.AreEqual(0, conflicts.Count);
			}

			Assert.AreEqual(2, Count(xmlRepo.GetAll()));
		}

		[Ignore]
		[TestMethod]
		public void ShouldUpdateItems()
		{
			ISyncRepository syncRepo = new MockSyncRepository();
			IXmlRepository xmlRepo = new MockXmlRepository();
			SyncEngine engine = new SyncEngine(xmlRepo, syncRepo);

			using (XmlReader xr = XmlReader.Create("http://sse.mslivelabs.com/feed.sse?i=293578659bbf40bfb8aa0b9102c36766&c=1&alt=Rss"))
			{
				IList<Item> conflicts = engine.Subscribe(new RssFeedReader(xr));

				Assert.AreEqual(0, conflicts.Count);
			}

			IXmlItem first = GetFirst<IXmlItem>(xmlRepo.GetAll());
			first.Title = "Baz";
			xmlRepo.Update(first);

			WebRequest req = WebRequest.Create("http://sse.mslivelabs.com/feed.sse?i=293578659bbf40bfb8aa0b9102c36766&c=1&alt=RSS");
			req.Timeout = -1;
			req.Method = "PUT";

			XmlWriterSettings set = new XmlWriterSettings();
			set.CloseOutput = true;
			Feed feed = new Feed("Client feed", "http://client/feed", "Client feed description");
			using (XmlWriter w = XmlWriter.Create(req.GetRequestStream(), set))
			{
				engine.Publish(feed, new RssFeedWriter(w));
			}

			req.GetResponse();

			syncRepo = new MockSyncRepository();
			xmlRepo = new MockXmlRepository();
			engine = new SyncEngine(xmlRepo, syncRepo);

			using (XmlReader xr = XmlReader.Create("http://sse.mslivelabs.com/feed.sse?i=293578659bbf40bfb8aa0b9102c36766&c=1&alt=RSS"))
			{
				IList<Item> conflicts = engine.Subscribe(new RssFeedReader(xr));

				Assert.AreEqual(0, conflicts.Count);
			}

			Assert.AreEqual(2, Count(xmlRepo.GetAll()));

			first = GetFirst<IXmlItem>(xmlRepo.GetAll());
			Assert.AreEqual("Baz", first.Title);
		}

		[Ignore]
		[TestMethod]
		public void ShouldPutFeed()
		{
			string xml = @"<rss version='2.0' xmlns:nid='http://www.microsoft.com/schemas/nid' xmlns:csacd_contact='http://www.microsoft.com/schemas/csacd/contact' xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'>
  <channel>
	 <title>Contacts</title>
	 <description>Sample list of contacts</description>
	 <nid:bindings version='0.8889' defaultbindingid='urn:microsoft.com/schemas/nid/contactwithgeo'>
		<nid:binding id='urn:microsoft.com/schemas/nid/contactwithgeo'
            name='Contact With Geo' titlefieldid='01' descriptionfieldid='03'>
		  <nid:namespace localprefix='csacd_contact'
            uri='http://www.microsoft.com/schemas/csacd/contact'/>
		  <nid:namespace localprefix='geo'
            uri='http://www.w3.org/2003/01/geo/wgs84_pos#'/>
		  <nid:field id='01' element='csacd_contact:fn' required='true'
            contenttype='text' label='Full Name'/>
		  <nid:field id='02' element='csacd_contact:Created' label='Created'
            deleted='true'/>
		  <nid:field id='03' element='csacd_contact:info' label='Comments'/>
		  <nid:field id='04' element='geo:lat' label='Latitude' type='floatingpoint'
            defaultvalue='0.0'/>
		  <nid:field id='05' element='geo:long' label='Longitude' type='floatingpoint'
            defaultvalue='0.0'/>
		  <nid:field id='06' element='csacd_contact:Addresses/csacd_contact:Address'
            label='Addresses' array='true'/>
		  <nid:field id='07' element='csacd_contact:Created2' label='Created'
            required='true' type='datetime'/>
		  <nid:field id='08' element='csacd_contact:Gender' label='Gender'
            required='true' picklist='gender'/>

		</nid:binding>
		<nid:picklist id='gender'>
		  <nid:listitem id='101'>Male</nid:listitem>
		  <nid:listitem id='102'>Female</nid:listitem>
		</nid:picklist>
	 </nid:bindings>
	 <item>
		<title>Ray Ozzie</title>
		<description>Chief Software Architect</description>
		<nid:binding id='urn:microsoft.com/schemas/nid/contactwithgeo'/>
		<csacd_contact:fn>Ray Ozzie</csacd_contact:fn>
		<csacd_contact:Created>2006-09-22T21:27:24Z</csacd_contact:Created>
		<csacd_contact:info>Chief Software Architect</csacd_contact:info>
		<csacd_contact:Gender>Male</csacd_contact:Gender>
		<geo:lat>42.322</geo:lat>
		<geo:long>22.11</geo:long>
		<csacd_contact:Addresses>
		  <csacd_contact:Address>1 Redmond Way, Redmond, WA 98052</csacd_contact:Address>
		  <csacd_contact:Address>1 Main Street, Seattle, WA 98104</csacd_contact:Address>

		</csacd_contact:Addresses>
	 </item>
  </channel>
</rss>";

			string url = "http://sse.mslivelabs.com/feed.sse?i=7df0b449e30645139d5ea366461fda8d&c=1?alt=rss";

			WebRequest req = WebRequest.Create(url);
			req.Timeout = -1;
			req.Method = "PUT";

			XmlWriterSettings set = new XmlWriterSettings();
			set.CloseOutput = true;
			using (XmlWriter w = XmlWriter.Create(req.GetRequestStream(), set))
			{
				w.WriteNode(GetReader(xml), false);
			}
			Console.WriteLine(DateTime.Now);
			req.GetResponse();
		}
	}
}
