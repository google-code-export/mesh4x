package com.mesh4j.sync.feed.rss;

import java.io.File;

import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.feed.Feed;
import com.mesh4j.sync.feed.FeedReader;

public class RssFeedReaderTests {

	@Test
	public void shouldReadRssFeed() throws DocumentException{
		
		File file = new File(this.getClass().getResource("rss.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = new FeedReader(RssSyndicationFormat.INSTANCE);
		Feed feed = reader.read(file);
		Assert.assertNotNull(feed); // TODO (JMT) test
	}
}
