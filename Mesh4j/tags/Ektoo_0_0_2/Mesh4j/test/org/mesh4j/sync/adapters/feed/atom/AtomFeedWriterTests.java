package org.mesh4j.sync.adapters.feed.atom;

import java.io.File;
import java.io.FileWriter;

import org.dom4j.io.XMLWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;


public class AtomFeedWriterTests {

	@Test
	public void shouldWriteRssFeed() throws Exception{
		
		File file = new File(this.getClass().getResource("atom.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = makeFeedReader();
		Feed feed = reader.read(file);
		
		XMLWriter xmlWriter = new XMLWriter(new FileWriter(TestHelper.fileName("atom1.xml")));
		
		FeedWriter writer = makeFeedWriter();
		writer.write(xmlWriter, feed);
		
		File file2 =  new File(TestHelper.fileName("atom1.xml"));
		Assert.assertTrue(file2.exists());
		
		Feed feed2 = reader.read(file2);
		Assert.assertNotNull(feed2);
		// TODO (JMT) test
		
	}

	private FeedWriter makeFeedWriter() {
		return new FeedWriter(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, ContentWriter.INSTANCE);
	}

	private FeedReader makeFeedReader() {
		return new FeedReader(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, ContentReader.INSTANCE);
	}
	
}
