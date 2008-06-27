package com.mesh4j.sync.adapters.feed;

import java.util.Date;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;

public class FeedWriterTests {

	@Test
	public void shouldWriteContent(){
		
		String syncID = IdGenerator.newID();
		
		Element element = DocumentHelper.createElement("payload");
		Element fooElement = element.addElement("foo");
		fooElement.addElement("bar");
		
		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1);
		XMLContent content = new XMLContent(syncID, "myTitle", "myDesc", element);
		Sync sync = new Sync(syncID, "jmt", date, false);
		Item item = new Item(content, sync);
		
		Element root = DocumentHelper.createElement("items");
		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		writer.write(root, item);
		
		Assert.assertEquals("<items><item><foo><bar></bar></foo><title>myTitle</title><description>myDesc</description><author><name>jmt</name></author><sx:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" deleted=\"false\" id=\""+syncID+"\" noconflicts=\"false\" updates=\"1\"><sx:history by=\"jmt\" sequence=\"1\" when=\"Fri, 01 Feb 2008 03:01:01 GMT\"></sx:history></sx:sync></item></items>", XMLHelper.canonicalizeXML(root));		
		
	}
	
	@Test
	public void shouldWriteContentUpdateTitleAndDescription(){
		
		String syncID = IdGenerator.newID();
		
		Element element = DocumentHelper.createElement("payload");
		Element fooElement = element.addElement("foo");
		fooElement.addElement("bar");
		
		Element elementTitle = element.addElement(ISyndicationFormat.SX_ELEMENT_ITEM_TITLE);
		elementTitle.setText("abc");
		
		Element elementDescription = element.addElement(ISyndicationFormat.SX_ELEMENT_ITEM_DESCRIPTION);
		elementDescription.setText("abc");
		
		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1);
		XMLContent content = new XMLContent(syncID, "myTitle", "myDesc", element);
		Sync sync = new Sync(syncID, "jmt", date, false);
		Item item = new Item(content, sync);
		
		Element root = DocumentHelper.createElement("items");
		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		writer.write(root, item);
		
		Assert.assertEquals("<items><item><foo><bar></bar></foo><title>myTitle</title><description>myDesc</description><author><name>jmt</name></author><sx:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" deleted=\"false\" id=\""+syncID+"\" noconflicts=\"false\" updates=\"1\"><sx:history by=\"jmt\" sequence=\"1\" when=\"Fri, 01 Feb 2008 03:01:01 GMT\"></sx:history></sx:sync></item></items>", XMLHelper.canonicalizeXML(root));		
		
	}
	
	@Test
	public void shouldWriteContentDeleteTitleAndDescription(){
		
		String syncID = IdGenerator.newID();
		
		Element element = DocumentHelper.createElement("payload");
		Element fooElement = element.addElement("foo");
		fooElement.addElement("bar");
		
		Element elementTitle = element.addElement(ISyndicationFormat.SX_ELEMENT_ITEM_TITLE);
		elementTitle.setText("abc");
		
		Element elementDescription = element.addElement(ISyndicationFormat.SX_ELEMENT_ITEM_DESCRIPTION);
		elementDescription.setText("abc");
		
		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1);
		XMLContent content = new XMLContent(syncID, null, null, element);
		Sync sync = new Sync(syncID, "jmt", date, false);
		Item item = new Item(content, sync);
		
		Element root = DocumentHelper.createElement("items");
		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		writer.write(root, item);
		
		Assert.assertEquals("<items><item><foo><bar></bar></foo><author><name>jmt</name></author><sx:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" deleted=\"false\" id=\""+syncID+"\" noconflicts=\"false\" updates=\"1\"><sx:history by=\"jmt\" sequence=\"1\" when=\"Fri, 01 Feb 2008 03:01:01 GMT\"></sx:history></sx:sync></item></items>", XMLHelper.canonicalizeXML(root));		
		
	}
}
