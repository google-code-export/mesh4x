package com.mesh4j.sync.adapters.feed.rss;

import java.io.File;

import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.Feed;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.security.NullSecurity;

public class RssFeedReaderTests {

	@Test
	public void shouldReadRssFeed() throws DocumentException{
		
		File file = new File(this.getClass().getResource("rss.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = new FeedReader(RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		Feed feed = reader.read(file);
		Assert.assertNotNull(feed); // TODO (JMT) test
	}
}
