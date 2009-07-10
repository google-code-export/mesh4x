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
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

import com.healthmarketscience.jackcess.Database;

public class SyncMsAccessTests {

	@Test
	public void testAccessDB() throws IOException{
		String mdbFile = getMsAccessFileNameToTest("DevDB.mdb");
		Database db = Database.open(new File(mdbFile));
		System.out.println(db.getTableNames());
	}
	
	@Test
	public void testFileConnection()throws Exception{
		String filename = getMsAccessFileNameToTest("DevDB.mdb");
		String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		database+= filename.trim() + ";DriverID=22;READONLY=false}"; // add on to the end 
 
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				
		Connection conn = null;
		Statement command = null;
		ResultSet rs = null;
		try{
			conn = DriverManager.getConnection(database,"mesh4j","mesh4j");
		
			command = conn.createStatement();
			rs = command.executeQuery("select user0_.id as id1_, user0_.name as name1_, user0_.pass as pass1_ from User user0_");
			while (rs.next())
			{
				System.out.println(rs.getString(1));
				System.out.println(rs.getString(2));
				System.out.println(rs.getString(3));
			}
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(command != null){
				try{
					command.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(conn != null){
				conn.close();
			}
		}
		
		System.out.println("Connected To Excel");
		
  		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url", database);
		builderA.setProperty("hibernate.connection.username","");
		builderA.setProperty("hibernate.connection.password","");
		builderA.setProperty("hibernate.connection.pool_size", "1");	
		
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));

		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builderA, "user");
		List<IContent> contents = contentAdapter.getAll();
		Assert.assertFalse(contents.isEmpty());		
		
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, "user_sync");
		
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(builderA, syncInfoParser);
		List<SyncInfo> syncInfos = syncRepository.getAll(contentAdapter.getType());
		Assert.assertTrue(syncInfos.isEmpty());
		
		SplitAdapter splitAdapter = new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);
		List<Item> items = splitAdapter.getAll();
		Assert.assertFalse(items.isEmpty());
		
		splitAdapter.endSync();
		
	}
	
	@Test
	public void testConnection(){
		String filename = getMsAccessFileNameToTest("DevDB.mdb");
		String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		database+= filename.trim() + ";DriverID=22;READONLY=false}";
		
		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url", database);
		builderA.setProperty("hibernate.connection.username","");
		builderA.setProperty("hibernate.connection.password","");
		builderA.setProperty("hibernate.connection.pool_size", "1");	
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));

		HibernateContentAdapter contentAdapter = new HibernateContentAdapter(builderA, "user");
		List<IContent> contents = contentAdapter.getAll();
		Assert.assertFalse(contents.isEmpty());		
		
		SyncInfoParser syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, "user_sync");
		
		HibernateSyncRepository syncRepository = new HibernateSyncRepository(builderA, syncInfoParser);
		List<SyncInfo> syncInfos = syncRepository.getAll(contentAdapter.getType());
		Assert.assertTrue(syncInfos.isEmpty());
		
		SplitAdapter splitAdapter = new SplitAdapter(syncRepository, contentAdapter, NullIdentityProvider.INSTANCE);
		List<Item> items = splitAdapter.getAll();
		Assert.assertFalse(items.isEmpty());
		splitAdapter.endSync();
	}
	
	@Test
	public void executeSync(){

		String filenameA = getMsAccessFileNameToTest("DevDB.mdb");
		String databaseA = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		databaseA+= filenameA.trim() + ";DriverID=22;READONLY=false}"; // add on to the end 

		String filenameB = getMsAccessFileNameToTest("DevDB2.mdb");
		String databaseB = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		databaseB+= filenameB.trim() + ";DriverID=22;READONLY=false}"; // add on to the end
		
		// CREATE SPLIT A
		HibernateSessionFactoryBuilder builderA = new HibernateSessionFactoryBuilder();
		builderA.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
		builderA.setProperty("hibernate.connection.driver_class","sun.jdbc.odbc.JdbcOdbcDriver");
		builderA.setProperty("hibernate.connection.url",databaseA);
		builderA.setProperty("hibernate.connection.username","");
		builderA.setProperty("hibernate.connection.password","");
		builderA.setProperty("hibernate.connection.pool_size", "1");	
		builderA.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderA.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));

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
		builderB.setProperty("hibernate.connection.pool_size", "1");	
		builderB.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builderB.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		
		HibernateSyncRepository syncRepositoryB = new HibernateSyncRepository(builderB, syncInfoParser);
		HibernateContentAdapter contentAdapterB = new HibernateContentAdapter(builderB, "user");
		SplitAdapter splitAdapterB = new SplitAdapter(syncRepositoryB, contentAdapterB, NullIdentityProvider.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterA, splitAdapterB);
		
		TestHelper.assertSync(syncEngine);
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
