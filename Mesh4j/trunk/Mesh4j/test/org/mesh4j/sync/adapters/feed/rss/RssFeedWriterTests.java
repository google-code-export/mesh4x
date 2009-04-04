package org.mesh4j.sync.adapters.feed.rss;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;


public class RssFeedWriterTests {

	@Test
	public void shouldWriteRssFeed() throws Exception{
		
		File file = new File(this.getClass().getResource("rss.xml").getFile());
		Assert.assertTrue(file.exists());
		
		FeedReader reader = makeFeedReader();
		Feed feed = reader.read(file);
		
		XMLWriter xmlWriter = new XMLWriter(new FileWriter(TestHelper.fileName("rss1.xml")));

		FeedWriter writer = makeFeedWriter();
		writer.write(xmlWriter, feed);
		
		File file2 =  new File(TestHelper.fileName("rss1.xml"));
		Assert.assertTrue(file2.exists());
		
		Feed feed2 = reader.read(file2);
		Assert.assertNotNull(feed2);
		// TODO (JMT) test
		xmlWriter = new XMLWriter(new FileWriter(TestHelper.fileName("rss2.xml")));
		Feed newFeed = new Feed(feed2.getItems());
		writer.write(xmlWriter, newFeed);
		
	}

	private FeedWriter makeFeedWriter() {
		return new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, ContentWriter.INSTANCE);
	}

	private FeedReader makeFeedReader() {
		return new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, ContentReader.INSTANCE);
	}
	
	@Test
	public void shouldAddLastUpdateAuthor() throws Exception{
		Document document = DocumentHelper.createDocument();
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		Element author = payload.addElement(ISyndicationFormat.SX_ELEMENT_AUTHOR);
		Element name = author.addElement(ISyndicationFormat.SX_ELEMENT_NAME);
		name.setText("otherAuthor");
		
		String by = "jmt";
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), by, new Date(), false);
		XMLContent content = new XMLContent(sync.getId(), "titleA", "descA", payload);
		Item item = new Item(content, sync);
		Feed feed = new Feed(item);

		FeedWriter writer = makeFeedWriter();
		writer.write(document, feed);
		
		assertFeedAuthor(document, by);
		
	}

	@Test
	public void shouldAddLastUpdateAuthorWhenAuthorDoesNotExist() throws Exception{
		
		Document document = DocumentHelper.createDocument();
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		payload.addElement("Foo");
		
		String by = "jmt";
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), by, new Date(), false);
		XMLContent content = new XMLContent(sync.getId(), "titleA", "descA", payload);
		Item item = new Item(content, sync);
		Feed feed = new Feed(item);

		FeedWriter writer = makeFeedWriter();
		writer.write(document, feed);
		
		assertFeedAuthor(document, by);
	}
	
	@Test
	public void shouldAddLastUpdateAuthorWhenNameDoesNotExist() throws Exception{
		Document document = DocumentHelper.createDocument();
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		payload.addElement(ISyndicationFormat.SX_ELEMENT_AUTHOR);
		
		String by = "jmt";
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), by, new Date(), false);
		XMLContent content = new XMLContent(sync.getId(), "titleA", "descA", payload);
		Item item = new Item(content, sync);
		Feed feed = new Feed(item);

		FeedWriter writer = makeFeedWriter();
		writer.write(document, feed);
		
		assertFeedAuthor(document, by);
	}
	
	@Test
	public void shouldNoAddLastUpdateAuthorWhenAreTheSameElementAuthor() throws Exception{
		Document document = DocumentHelper.createDocument();
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		Element author = payload.addElement(ISyndicationFormat.SX_ELEMENT_AUTHOR);
		Element name = author.addElement(ISyndicationFormat.SX_ELEMENT_NAME);
		name.setText("jmt");
		
		String by = "jmt";
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), by, new Date(), false);
		XMLContent content = new XMLContent(sync.getId(), "titleA", "descA", payload);
		Item item = new Item(content, sync);
		Feed feed = new Feed(item);

		FeedWriter writer = makeFeedWriter();
		writer.write(document, feed);
		
		assertFeedAuthor(document, by);
	}
	
	@Test
	public void shouldAddLoggedAuthor() throws Exception{
		Document document = DocumentHelper.createDocument();
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		Element author = payload.addElement(ISyndicationFormat.SX_ELEMENT_AUTHOR);
		Element name = author.addElement(ISyndicationFormat.SX_ELEMENT_NAME);
		name.setText("otherAuthor");
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), null, new Date(), false);
		XMLContent content = new XMLContent(sync.getId(), "titleA", "descA", payload);
		Item item = new Item(content, sync);
		Feed feed = new Feed(item);

		FeedWriter writer = makeFeedWriter();
		writer.write(document, feed);
		
		assertFeedAuthor(document, NullIdentityProvider.INSTANCE.getAuthenticatedUser());
	}

	@Test
	public void shouldAddLoggedAuthorWhenAuthorDoesNotExist() throws Exception{
		Document document = DocumentHelper.createDocument();
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), null, new Date(), false);
		XMLContent content = new XMLContent(sync.getId(), "titleA", "descA", payload);
		Item item = new Item(content, sync);
		Feed feed = new Feed(item);

		FeedWriter writer = makeFeedWriter();
		writer.write(document, feed);
		
		assertFeedAuthor(document, NullIdentityProvider.INSTANCE.getAuthenticatedUser());
	}
	
	@Test
	public void shouldAddLoggedAuthorWhenNameDoesNotExist() throws Exception{
		Document document = DocumentHelper.createDocument();
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		payload.addElement(ISyndicationFormat.SX_ELEMENT_AUTHOR);
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), null, new Date(), false);
		XMLContent content = new XMLContent(sync.getId(), "titleA", "descA", payload);
		Item item = new Item(content, sync);
		Feed feed = new Feed(item);

		FeedWriter writer = makeFeedWriter();
		writer.write(document, feed);
		
		assertFeedAuthor(document, NullIdentityProvider.INSTANCE.getAuthenticatedUser());
	}
	
	@Test
	public void shouldNoAddLoggedAuthorWhenAreTheSameElementAuthor() throws Exception{
		Document document = DocumentHelper.createDocument();
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		Element author = payload.addElement(ISyndicationFormat.SX_ELEMENT_AUTHOR);
		Element name = author.addElement(ISyndicationFormat.SX_ELEMENT_NAME);
		name.setText(NullIdentityProvider.INSTANCE.getAuthenticatedUser());
		
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), null, new Date(), false);
		XMLContent content = new XMLContent(sync.getId(), "titleA", "descA", payload);
		Item item = new Item(content, sync);
		Feed feed = new Feed(item);

		FeedWriter writer = makeFeedWriter();
		writer.write(document, feed);
		
		assertFeedAuthor(document, NullIdentityProvider.INSTANCE.getAuthenticatedUser());
	}
	
	private void assertFeedAuthor(Document document, String by) {
		Element rssElement = document.getRootElement();
		Assert.assertNotNull(rssElement);		
		Assert.assertEquals(RssSyndicationFormat.RSS_ELEMENT_ROOT, rssElement.getName());
		
		Element channelElement = rssElement.element(RssSyndicationFormat.RSS_ELEMENT_CHANNEL);
		Assert.assertNotNull(channelElement);
		
		Element itemElement = channelElement.element(RssSyndicationFormat.RSS_ELEMENT_ITEM);
		Assert.assertNotNull(itemElement);
		
		Element authorElement = itemElement.element(RssSyndicationFormat.SX_ELEMENT_AUTHOR);
		Assert.assertNotNull(authorElement);
		
		Assert.assertEquals(by+"@mesh4x.example", authorElement.getTextTrim());
	}

}