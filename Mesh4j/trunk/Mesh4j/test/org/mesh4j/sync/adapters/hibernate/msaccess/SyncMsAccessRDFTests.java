package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

import sun.jdbc.odbc.JdbcOdbcDriver;

public class SyncMsAccessRDFTests {

	@Test
	public void shouldSync2MSAccessTablesWithRDFMapping(){

		String filenameA = getMsAccessFileNameToTest("DevDB.mdb");
		String databaseA = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		databaseA+= filenameA.trim() + ";DriverID=22;READONLY=false}"; // add on to the end 

		String filenameB = getMsAccessFileNameToTest("DevDB2.mdb");
		String databaseB = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		databaseB+= filenameB.trim() + ";DriverID=22;READONLY=false}"; // add on to the end
		
		// CREATE SPLIT A
		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderA.setProperty("hibernate.connection.driver_class", JdbcOdbcDriver.class.getName());
		builderA.setProperty("hibernate.connection.url",databaseA);
		builderA.setProperty("hibernate.connection.username","");
		builderA.setProperty("hibernate.connection.password","");
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		
		RDFSchema schemaA = new RDFSchema("user", "http://mesh4x/user#", "user");
		schemaA.addStringProperty("id", "id", "en");
		schemaA.addStringProperty("pass", "password", "en");
		schemaA.addStringProperty("name", "name", "en");
		builderA.addRDFSchema("user", schemaA);
		
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, "user_sync");
		
		HibernateSyncRepository syncRepositoryA = new HibernateSyncRepository(builderA, syncInfoParser);
		HibernateContentAdapter contentAdapterA = new HibernateContentAdapter(builderA, "user");
		SplitAdapter splitAdapterA = new SplitAdapter(syncRepositoryA, contentAdapterA, NullIdentityProvider.INSTANCE);
		
		// CREATE SPLIT B		
		HibernateSessionFactoryBuilder builderB = new HibernateSessionFactoryBuilder();
		builderB.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderB.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderB.setProperty("hibernate.connection.url",databaseB);
		builderB.setProperty("hibernate.connection.username","");
		builderB.setProperty("hibernate.connection.password","");
		builderB.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderB.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));

		RDFSchema schemaB = new RDFSchema("user", "http://mesh4x/user#", "user");
		schemaB.addStringProperty("id", "id", "en");
		schemaB.addStringProperty("pass", "password", "en");
		schemaB.addStringProperty("name", "name", "en");
		builderB.addRDFSchema("user", schemaB);
		
		HibernateSyncRepository syncRepositoryB = new HibernateSyncRepository(builderB, syncInfoParser);
		HibernateContentAdapter contentAdapterB = new HibernateContentAdapter(builderB, "user");
		SplitAdapter splitAdapterB = new SplitAdapter(syncRepositoryB, contentAdapterB, NullIdentityProvider.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterA, splitAdapterB);
		
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());

		List<Item> itemsA = splitAdapterA.getAll();
		Assert.assertFalse(itemsA.isEmpty());
		
		List<Item> itemsB = splitAdapterB.getAll();
		Assert.assertFalse(itemsB.isEmpty());

		Assert.assertEquals(itemsA.size(), itemsB.size());
	}
	
	@Test
	public void shouldSyncMSAccessToFeedWithRDFMapping() throws Exception{

		String filenameA = getMsAccessFileNameToTest("DevDB.mdb");
		String databaseA = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		databaseA+= filenameA.trim() + ";DriverID=22;READONLY=false}"; // add on to the end 
		
		// CREATE SPLIT A
		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url",databaseA);
		builderA.setProperty("hibernate.connection.username","");
		builderA.setProperty("hibernate.connection.password","");
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		
		RDFSchema schemaA = new RDFSchema("user", "http://mesh4x/user#", "user");
		schemaA.addStringProperty("id", "id", "en");
		schemaA.addStringProperty("pass", "password", "en");
		schemaA.addStringProperty("name", "name", "en");
		builderA.addRDFSchema("user", schemaA);
		
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, "user_sync");
		
		HibernateSyncRepository syncRepositoryA = new HibernateSyncRepository(builderA, syncInfoParser);
		HibernateContentAdapter contentAdapterA = new HibernateContentAdapter(builderA, "user");
		SplitAdapter splitAdapterA = new SplitAdapter(syncRepositoryA, contentAdapterA, NullIdentityProvider.INSTANCE);
		
		// CREATE Feed adapter
		String fileName= TestHelper.fileName("rdfSync_" + IdGenerator.INSTANCE.newID());
		Feed feed = new Feed("user", "example", "");
		FeedAdapter feedAdapter = new FeedAdapter(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
		
		// Sync		
		SyncEngine syncEngine = new SyncEngine(splitAdapterA, feedAdapter);
		
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());

		List<Item> itemsA = splitAdapterA.getAll();
		Assert.assertFalse(itemsA.isEmpty());
		
		List<Item> itemsB = feedAdapter.getAll();
		Assert.assertFalse(itemsB.isEmpty());

		Assert.assertEquals(itemsA.size(), itemsB.size());
	}

	private String getMsAccessFileNameToTest(String name) {
		try{
			String localFileName = this.getClass().getResource("DevDB2003.mdb").getFile();
			String fileName = TestHelper.fileName(name);
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
