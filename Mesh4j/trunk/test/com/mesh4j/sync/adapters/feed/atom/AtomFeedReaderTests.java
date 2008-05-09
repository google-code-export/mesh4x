package com.mesh4j.sync.adapters.feed.atom;

import java.io.File;

import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.Feed;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.security.NullSecurity;

public class AtomFeedReaderTests {

	@Test
	public void shouldReadRssFeed() throws DocumentException{
		
		File file = new File(this.getClass().getResource("atom.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = new FeedReader(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		Feed feed = reader.read(file);
		Assert.assertNotNull(feed); // TODO (JMT) test
	}
}
