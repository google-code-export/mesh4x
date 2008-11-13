package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.NullIdentityProvider;

public class SyncMsAccessTests {

	@Test
	public void testConnection(){
		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect","org.mesh4j.sync.adapters.hibernate.MSAccessDialect");
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url","jdbc:odbc:DevDB");
		//builderA.setProperty("hibernate.connection.url","jdbc:odbc:DevDB2");
		builderA.setProperty("hibernate.connection.username","mesh4j");
		builderA.setProperty("hibernate.connection.password","mesh4j");
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addSyncInfoMapping();


		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builderA, "user");
		List<IContent> contents = contentAdapter.getAll();
		
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(syncInfoParser, builderA);
		List<SyncInfo> syncInfos = syncRepository.getAll(contentAdapter.getType());		
		
		SplitAdapter splitAdapter = new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);
		List<Item> items = splitAdapter.getAll();
		
	}
	
	@Test
	public void executeSync(){

		// CREATE SPLIT A
		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect","org.mesh4j.sync.adapters.hibernate.MSAccessDialect");
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url","jdbc:odbc:DevDB");
		builderA.setProperty("hibernate.connection.username","mesh4j");
		builderA.setProperty("hibernate.connection.password","mesh4j");
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addSyncInfoMapping();

		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HibernateSyncRepository syncRepositoryA = new HibernateSyncRepository(syncInfoParser, builderA);
		HibernateContentAdapter contentAdapterA = new HibernateContentAdapter(builderA, "user");
		SplitAdapter splitAdapterA = new SplitAdapter(syncRepositoryA, contentAdapterA, NullIdentityProvider.INSTANCE);
		
		// CREATE SPLIT B		
		HibernateSessionFactoryBuilder builderB = new HibernateSessionFactoryBuilder();
		builderB.setProperty("hibernate.dialect","org.mesh4j.sync.adapters.hibernate.MSAccessDialect");
		builderB.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderB.setProperty("hibernate.connection.url","jdbc:odbc:DevDB2");
		builderB.setProperty("hibernate.connection.username","mesh4j");
		builderB.setProperty("hibernate.connection.password","mesh4j");
		builderB.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderB.addSyncInfoMapping();
		
		HibernateSyncRepository syncRepositoryB = new HibernateSyncRepository(syncInfoParser, builderB);
		HibernateContentAdapter contentAdapterB = new HibernateContentAdapter(builderB, "user");
		SplitAdapter splitAdapterB = new SplitAdapter(syncRepositoryB, contentAdapterB, NullIdentityProvider.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterA, splitAdapterB);
		
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());

	}

	
}
