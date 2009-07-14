package org.mesh4j.sync.adapters.multi.repositories;

import java.io.File;
import java.util.Date;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;


public class InterRepositoriesTests {

	@Test
	public void shouldSyncFeed2Hibernate() throws DocumentException{
		
		File rssFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));

		String id1 = TestHelper.newID();
		String id2 = TestHelper.newID();
		
		Element e1 = TestHelper.makeElement("<payload><user><id>"+id1+"</id><name>"+id1+"</name><pass>123</pass></user></payload>");
		Element e2 = TestHelper.makeElement("<payload><user><id>"+id2+"</id><name>"+id2+"</name><pass>123</pass></user></payload>");
		
		Feed feed = new Feed()
			.addItem(new Item(new XMLContent(id1, id1, id1, e1), new Sync(id1).update(NullIdentityProvider.INSTANCE.getAuthenticatedUser(), new Date())))
			.addItem(new Item(new XMLContent(id2, id2, id2, e2), new Sync(id2).update(NullIdentityProvider.INSTANCE.getAuthenticatedUser(), new Date())));
		
		FeedAdapter feedRepo = new FeedAdapter(rssFile, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, feed);
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.setPropertiesFile(new File(InterRepositoriesTests.class.getResource("xx_hibernate.properties").getFile()));
		builder.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		
		HibernateAdapter hibernateRepo = new HibernateAdapter(builder, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(feedRepo, hibernateRepo);
		TestHelper.assertSync(syncEngine);
		
		
	}
	
	@Test
	public void shouldSyncHibernate2Feed() throws Exception{
		
		File rssFile = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID()+".xml"));

		FeedAdapter feedRepo = new FeedAdapter(rssFile, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, new Feed());
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		builder.setPropertiesFile(new File(InterRepositoriesTests.class.getResource("xx_hibernate.properties").getFile()));
		HibernateAdapter hibernateRepo = new HibernateAdapter(builder, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(feedRepo, hibernateRepo);
		TestHelper.assertSync(syncEngine);
	}
	
	@Test
	public void shouldSyncRssFeed2Hibernate() throws DocumentException{

		File rssFile = new File(this.getClass().getResource("rssUserFeed.xml").getFile());
		FeedAdapter feedRepo = new FeedAdapter(rssFile, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
				
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		builder.setPropertiesFile(new File(this.getClass().getResource("xx_hibernate.properties").getFile()));
		HibernateAdapter hibernateRepo = new HibernateAdapter(builder, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		hibernateRepo.deleteAll();
		Assert.assertEquals(0, hibernateRepo.getAll().size());		
		Assert.assertEquals(1, feedRepo.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(feedRepo, hibernateRepo);
		TestHelper.assertSync(syncEngine);
			
	}
	
	@Test
	public void shouldSyncAtomFeed2Hibernate() throws DocumentException{

		File rssFile = new File(this.getClass().getResource("atomUserFeed.xml").getFile());
		FeedAdapter feedRepo = new FeedAdapter(rssFile, AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
				
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		builder.setPropertiesFile(new File(InterRepositoriesTests.class.getResource("xx_hibernate.properties").getFile()));
		HibernateAdapter hibernateRepo = new HibernateAdapter(builder, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		hibernateRepo.deleteAll();
		
		SyncEngine syncEngine = new SyncEngine(feedRepo, hibernateRepo);
		TestHelper.assertSync(syncEngine);
		
	}

	
}
