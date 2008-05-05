package com.mesh4j.sync.adapters.multi.repositories;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.feed.Feed;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.FeedAdapter;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.adapters.hibernate.HibernateAdapter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.Security;
import com.mesh4j.sync.test.utils.TestHelper;

public class InterRepositoriesTests {

	@Test
	public void shouldSyncFeed2Hibernate() throws DocumentException{
		String id1 = TestHelper.newID();
		String id2 = TestHelper.newID();
		
		Element e1 = TestHelper.makeElement("<payload><user><id>"+id1+"</id><name>"+id1+"</name><pass>123</pass></user></payload>");
		Element e2 = TestHelper.makeElement("<payload><user><id>"+id2+"</id><name>"+id2+"</name><pass>123</pass></user></payload>");
		
		Feed feed = new Feed()
			.addItem(new Item(new XMLContent(id1, id1, id1, e1), new Sync(id1).update(Security.getAuthenticatedUser(), new Date())))
			.addItem(new Item(new XMLContent(id2, id2, id2, e2), new Sync(id2).update(Security.getAuthenticatedUser(), new Date())));
		
		FeedAdapter feedRepo = new FeedAdapter(feed);
				
		HibernateAdapter	hibernateRepo = new HibernateAdapter(InterRepositoriesTests.class.getResource("User.hbm.xml").getFile());
		
		List<Item> allItems = hibernateRepo.getAll();
		
		SyncEngine engine = new SyncEngine(feedRepo, hibernateRepo);
		List<Item> conflicts = engine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		Assert.assertTrue(hibernateRepo.getAll().size() == allItems.size() + 2);
		Assert.assertTrue(feed.getItems().size() == allItems.size() + 2);
		
		
	}
	
	@Test
	public void shouldSyncHibernate2Feed() throws IOException, DocumentException{
		
		FeedAdapter feedRepo = new FeedAdapter();
		
		HibernateAdapter	hibernateRepo = new HibernateAdapter(InterRepositoriesTests.class.getResource("User.hbm.xml").getFile());
		
		List<Item> allItems = hibernateRepo.getAll();
		
		SyncEngine engine = new SyncEngine(feedRepo, hibernateRepo);
		List<Item> conflicts = engine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		Feed feed = feedRepo.getFeed();
		Assert.assertTrue(feed.getItems().size() == allItems.size());
		
		Assert.assertTrue(hibernateRepo.getAll().size() == allItems.size());
		
		XMLWriter xmlWriter = new XMLWriter(new FileWriter("c:\\atomUserFeed1.xml"));
		FeedWriter feedWriter = new FeedWriter(AtomSyndicationFormat.INSTANCE);
		feedWriter.write(xmlWriter, feed);
		
		XMLWriter xmlWriterRss = new XMLWriter(new FileWriter("c:\\rssUserFeed1.xml"));
		FeedWriter rssFeedWriter = new FeedWriter(RssSyndicationFormat.INSTANCE);
		rssFeedWriter.write(xmlWriterRss, feed);
	}
	
	@Test
	public void shouldSyncRssFeed2Hibernate() throws DocumentException{

		File rssFile = new File(this.getClass().getResource("rssUserFeed.xml").getFile());
		FeedReader rssFeedReader = new FeedReader(RssSyndicationFormat.INSTANCE);

		Feed feed = rssFeedReader.read(rssFile);
		FeedAdapter feedRepo = new FeedAdapter(feed);
				
		File fileMapping = new File(this.getClass().getResource("User.hbm.xml").getFile());
		HibernateAdapter	hibernateRepo = new HibernateAdapter(fileMapping);
		
		hibernateRepo.deleteAll();
		
		SyncEngine engine = new SyncEngine(feedRepo, hibernateRepo);
		List<Item> conflicts = engine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		Assert.assertTrue(hibernateRepo.getAll().size() == 1);
		Assert.assertTrue(feed.getItems().size() == 1);
		
		
	}
	
	@Test
	public void shouldSyncAtomFeed2Hibernate() throws DocumentException{

		File rssFile = new File(this.getClass().getResource("atomUserFeed.xml").getFile());
		FeedReader rssFeedReader = new FeedReader(AtomSyndicationFormat.INSTANCE);

		Feed feed = rssFeedReader.read(rssFile);
		FeedAdapter feedRepo = new FeedAdapter(feed);
				
		File fileMapping = new File(this.getClass().getResource("User.hbm.xml").getFile());
		HibernateAdapter	hibernateRepo = new HibernateAdapter(fileMapping);
		
		hibernateRepo.deleteAll();
		
		SyncEngine engine = new SyncEngine(feedRepo, hibernateRepo);
		List<Item> conflicts = engine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		Assert.assertTrue(hibernateRepo.getAll().size() == 1);
		Assert.assertTrue(feed.getItems().size() == 1);
		
		
	}

	
}
