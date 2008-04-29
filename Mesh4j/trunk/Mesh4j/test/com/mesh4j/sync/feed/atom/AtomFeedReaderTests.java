package com.mesh4j.sync.feed.atom;

import java.io.File;

import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.feed.Feed;
import com.mesh4j.sync.feed.FeedReader;

public class AtomFeedReaderTests {

	@Test
	public void shouldReadRssFeed() throws DocumentException{
		
		File file = new File(this.getClass().getResource("atom.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = new FeedReader(AtomSyndicationFormat.INSTANCE);
		Feed feed = reader.read(file);
		Assert.assertNotNull(feed); // TODO (JMT) test
	}
}
