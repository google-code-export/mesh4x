package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncRepository;
import org.mesh4j.sync.adapters.msaccess.MsAccessDialect;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.NullIdentityProvider;

import sun.jdbc.odbc.JdbcOdbcDriver;

import com.healthmarketscience.jackcess.Database;

public class SyncMsAccessTests {

	@Test
	public void testAccessDB() throws IOException{
		String mdbFile = "c:/mesh4x/tests/ms-access/DevDB2.mdb";
		Database db = Database.open(new File(mdbFile));
		System.out.println(db.getTableNames());
	}
	
	@Test
	public void testFileConnection()throws Exception{
		String filename = "c:/mesh4x/tests/ms-access/DevDB2.mdb";
		String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		database+= filename.trim() + ";DriverID=22;READONLY=false}"; // add on to the end 
 
		JdbcOdbcDriver driver = (JdbcOdbcDriver)Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
		Assert.assertNotNull(driver);
		
		Connection conn = DriverManager.getConnection(database,"mesh4j","mesh4j");
//		System.out.println("catalog:" + conn.getCatalog());
//		conn.getMetaData();
//		conn.getClientInfo();
		
		Statement command = conn.createStatement();
		ResultSet rs = command.executeQuery("select user0_.id as id1_, user0_.name as name1_, user0_.pass as pass1_ from User user0_");
		while (rs.next())
		{
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(3));
		}
		
		System.out.println("Connected To Excel");
		
		//String database = "jdbc:odbc:DevDB2";
  		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url", database);
		//builderA.setProperty("hibernate.connection.url","jdbc:odbc:DevDB2");
		builderA.setProperty("hibernate.connection.username","mesh4j");
		builderA.setProperty("hibernate.connection.password","mesh4j");
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addMapping(new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile()));

		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builderA, "user");
		List<IContent> contents = contentAdapter.getAll();
		Assert.assertFalse(contents.isEmpty());		
		
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(builderA, syncInfoParser);
		List<SyncInfo> syncInfos = syncRepository.getAll(contentAdapter.getType());
		Assert.assertFalse(syncInfos.isEmpty());
		
		SplitAdapter splitAdapter = new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);
		List<Item> items = splitAdapter.getAll();
		Assert.assertFalse(items.isEmpty());
		
	}
	
	@Test
	public void testConnection(){
		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url","jdbc:odbc:DevDB");
		//builderA.setProperty("hibernate.connection.url","jdbc:odbc:DevDB2");
		builderA.setProperty("hibernate.connection.username","mesh4j");
		builderA.setProperty("hibernate.connection.password","mesh4j");
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addMapping(new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile()));

		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builderA, "user");
		List<IContent> contents = contentAdapter.getAll();
		Assert.assertFalse(contents.isEmpty());		
		
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(builderA, syncInfoParser);
		List<SyncInfo> syncInfos = syncRepository.getAll(contentAdapter.getType());
		Assert.assertFalse(syncInfos.isEmpty());
		
		SplitAdapter splitAdapter = new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);
		List<Item> items = splitAdapter.getAll();
		Assert.assertFalse(items.isEmpty());
		
	}
	
	@Test
	public void executeSync(){

		String filenameA = "c:/mesh4x/tests/ms-access/DevDB.mdb";
		String databaseA = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		databaseA+= filenameA.trim() + ";DriverID=22;READONLY=false}"; // add on to the end 

		String filenameB = "c:/mesh4x/tests/ms-access/DevDB2.mdb";
		String databaseB = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		databaseB+= filenameB.trim() + ";DriverID=22;READONLY=false}"; // add on to the end
		
		// CREATE SPLIT A
		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url",databaseA);
		//builderA.setProperty("hibernate.connection.url","jdbc:odbc:DevDB");
		builderA.setProperty("hibernate.connection.username","mesh4j");
		builderA.setProperty("hibernate.connection.password","mesh4j");
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addMapping(new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile()));

		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		HibernateSyncRepository syncRepositoryA = new HibernateSyncRepository(builderA, syncInfoParser);
		HibernateContentAdapter contentAdapterA = new HibernateContentAdapter(builderA, "user");
		SplitAdapter splitAdapterA = new SplitAdapter(syncRepositoryA, contentAdapterA, NullIdentityProvider.INSTANCE);
		
		// CREATE SPLIT B		
		HibernateSessionFactoryBuilder builderB = new HibernateSessionFactoryBuilder();
		builderB.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderB.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderB.setProperty("hibernate.connection.url",databaseB);
		//builderB.setProperty("hibernate.connection.url","jdbc:odbc:DevDB2");
		builderB.setProperty("hibernate.connection.username","mesh4j");
		builderB.setProperty("hibernate.connection.password","mesh4j");
		builderB.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderB.addMapping(new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile()));
		
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

	
}
