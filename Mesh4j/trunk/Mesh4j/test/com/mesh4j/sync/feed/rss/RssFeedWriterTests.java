package com.mesh4j.sync.feed.rss;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.DocumentException;
import org.dom4j.io.XMLWriter;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.feed.Feed;
import com.mesh4j.sync.feed.FeedReader;
import com.mesh4j.sync.feed.FeedWriter;

public class RssFeedWriterTests {

	@Test
	public void shouldWriteRssFeed() throws DocumentException, IOException{
		
		File file = new File(this.getClass().getResource("rss.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = new FeedReader(RssSyndicationFormat.INSTANCE);
		Feed feed = reader.read(file);
		
		XMLWriter xmlWriter = new XMLWriter(new FileWriter("c:\\rss1.xml"));

		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE);
		writer.write(xmlWriter, feed);
		
		File file2 =  new File("c:\\rss1.xml");
		Assert.assertTrue(file2.exists());
		
		Feed feed2 = reader.read(file2);
		Assert.assertNotNull(feed2);
		// TODO (JMT) test
		
	}
	
}