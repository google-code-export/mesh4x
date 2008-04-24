#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif

using System;
using System.Collections.Generic;
using System.Xml;
using System.IO;

namespace SimpleSharing.Tests
{
	[TestClass]
	public class FeedReaderFixture : TestFixtureBase
	{
		[TestMethod]
		public void ShouldReadWellFormedXML()
		{
			string xml = @"
        <item xmlns:sx='http://feedsync.org/2007/feedsync'>
            <title>Bar</title>
            <sx:sync id='529a0d9d-d008-4ed2-b2d7-ccfc18da7cab' updates='1'>
                <sx:history sequence='1' by='dcazzulino@hotmail.com' />
            </sx:sync>
        </item>";

			XmlReader xr = GetReader(xml);

			FeedReader.SyncXmlReader reader = new FeedReader.SyncXmlReader(xr, new RssFeedReader(xr));

			XmlWriterSettings set = new XmlWriterSettings();
			set.Indent = true;
			set.ConformanceLevel = ConformanceLevel.Fragment;

			using (XmlWriter w = XmlWriter.Create(Console.Out))
			{
				w.WriteNode(reader, false);
			}

			Assert.IsNotNull(reader.Sync);
		}
	}
}
