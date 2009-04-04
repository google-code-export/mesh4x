package org.mesh4j.sync.adapters.feed.rss;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;


public class RssFeedReaderTests {

	@Test
	public void shouldReadRssFeed() throws Exception{
		
		File file = new File(this.getClass().getResource("rss.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, ContentReader.INSTANCE);
		Feed feed = reader.read(file);
		Assert.assertNotNull(feed); // TODO (JMT) test
	}
}
