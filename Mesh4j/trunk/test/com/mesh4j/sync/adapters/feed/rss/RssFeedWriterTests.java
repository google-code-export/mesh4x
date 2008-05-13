package com.mesh4j.sync.adapters.feed.rss;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.DocumentException;
import org.dom4j.io.XMLWriter;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.Feed;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.TestHelper;

public class RssFeedWriterTests {

	@Test
	public void shouldWriteRssFeed() throws DocumentException, IOException{
		
		File file = new File(this.getClass().getResource("rss.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = new FeedReader(RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		Feed feed = reader.read(file);
		
		XMLWriter xmlWriter = new XMLWriter(new FileWriter(TestHelper.fileName("rss1.xml")));

		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		writer.write(xmlWriter, feed);
		
		File file2 =  new File(TestHelper.fileName("rss1.xml"));
		Assert.assertTrue(file2.exists());
		
		Feed feed2 = reader.read(file2);
		Assert.assertNotNull(feed2);
		// TODO (JMT) test
		// TODO (JMT) MOCK File ?
		xmlWriter = new XMLWriter(new FileWriter(TestHelper.fileName("rss2.xml")));
		Feed newFeed = new Feed(feed2.getItems());
		writer.write(xmlWriter, newFeed);
		
	}
	
}