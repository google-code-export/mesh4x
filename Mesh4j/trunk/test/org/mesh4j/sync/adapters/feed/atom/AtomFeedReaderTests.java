package org.mesh4j.sync.adapters.feed.atom;

import java.io.File;

import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;


public class AtomFeedReaderTests {

	@Test
	public void shouldReadFeed() throws DocumentException{
		
		File file = new File(this.getClass().getResource("atom.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = new FeedReader(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		Feed feed = reader.read(file);
		Assert.assertNotNull(feed); // TODO (JMT) test
		
		Assert.assertEquals(3, feed.getItems().get(0).getLastUpdate().getSequence());
	}
}
