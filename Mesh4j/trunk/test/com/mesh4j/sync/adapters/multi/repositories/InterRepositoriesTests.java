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
import com.mesh4j.sync.adapters.feed.FeedAdapter;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.adapters.hibernate.HibernateAdapter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class InterRepositoriesTests {

	@Test
	public void shouldSyncFeed2Hibernate() throws DocumentException{
		
		File rssFile = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));

		String id1 = TestHelper.newID();
		String id2 = TestHelper.newID();
		
		Element e1 = TestHelper.makeElement("<payload><user><id>"+id1+"</id><name>"+id1+"</name><pass>123</pass></user></payload>");
		Element e2 = TestHelper.makeElement("<payload><user><id>"+id2+"</id><name>"+id2+"</name><pass>123</pass></user></payload>");
		
		Feed feed = new Feed()
			.addItem(new Item(new XMLContent(id1, id1, id1, e1), new Sync(id1).update(NullIdentityProvider.INSTANCE.getAuthenticatedUser(), new Date())))
			.addItem(new Item(new XMLContent(id2, id2, id2, e2), new Sync(id2).update(NullIdentityProvider.INSTANCE.getAuthenticatedUser(), new Date())));
		
		FeedAdapter feedRepo = new FeedAdapter(rssFile, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, feed);
				
		HibernateAdapter hibernateRepo = new HibernateAdapter(InterRepositoriesTests.class.getResource("User.hbm.xml").getFile(), NullIdentityProvider.INSTANCE);
		
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
		
		File rssFile = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));

		FeedAdapter feedRepo = new FeedAdapter(rssFile, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, new Feed());
		
		HibernateAdapter	hibernateRepo = new HibernateAdapter(InterRepositoriesTests.class.getResource("User.hbm.xml").getFile(), NullIdentityProvider.INSTANCE);
		
		List<Item> allItems = hibernateRepo.getAll();
		
		SyncEngine engine = new SyncEngine(feedRepo, hibernateRepo);
		List<Item> conflicts = engine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		Feed feed = feedRepo.getFeed();
		Assert.assertTrue(feed.getItems().size() == allItems.size());
		
		Assert.assertTrue(hibernateRepo.getAll().size() == allItems.size());
		
		XMLWriter xmlWriter = new XMLWriter(new FileWriter(TestHelper.fileName("atomUserFeed1.xml")));
		FeedWriter feedWriter = new FeedWriter(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		feedWriter.write(xmlWriter, feed);
	}
	
	@Test
	public void shouldSyncRssFeed2Hibernate() throws DocumentException{

		File rssFile = new File(this.getClass().getResource("rssUserFeed.xml").getFile());
		FeedAdapter feedRepo = new FeedAdapter(rssFile, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
				
		File fileMapping = new File(this.getClass().getResource("User.hbm.xml").getFile());
		HibernateAdapter hibernateRepo = new HibernateAdapter(fileMapping, NullIdentityProvider.INSTANCE);
		
		hibernateRepo.deleteAll();
		
		SyncEngine engine = new SyncEngine(feedRepo, hibernateRepo);
		List<Item> conflicts = engine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		Assert.assertTrue(hibernateRepo.getAll().size() == 1);
		Assert.assertTrue(feedRepo.getFeed().getItems().size() == 1);
		
		
	}
	
	@Test
	public void shouldSyncAtomFeed2Hibernate() throws DocumentException{

		File rssFile = new File(this.getClass().getResource("atomUserFeed.xml").getFile());
		FeedAdapter feedRepo = new FeedAdapter(rssFile, AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
				
		File fileMapping = new File(this.getClass().getResource("User.hbm.xml").getFile());
		HibernateAdapter	hibernateRepo = new HibernateAdapter(fileMapping, NullIdentityProvider.INSTANCE);
		
		hibernateRepo.deleteAll();
		
		SyncEngine engine = new SyncEngine(feedRepo, hibernateRepo);
		List<Item> conflicts = engine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		Assert.assertTrue(hibernateRepo.getAll().size() == 1);
		Assert.assertTrue(feedRepo.getFeed().getItems().size() == 1);
		
	}

	
}
