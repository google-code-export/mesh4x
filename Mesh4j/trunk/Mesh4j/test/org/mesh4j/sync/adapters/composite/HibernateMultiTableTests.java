package org.mesh4j.sync.adapters.composite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.SqlDBUtils;

import com.mysql.jdbc.Driver;

public class HibernateMultiTableTests {

	@BeforeClass
	public static void setUpDB(){
		//create database/tables for source
		String sqlFileName = FileUtils.getResourceFileURL("mesh4j_table_mysql.sql").getFile();
		SqlDBUtils.executeSqlScript(Driver.class, "jdbc:mysql://localhost", "mesh4xdb", "root", "", sqlFileName);	
		
		//create database/tables for target
		sqlFileName = FileUtils.getResourceFileURL("mesh4j_table_mysql_target.sql").getFile();
		SqlDBUtils.executeSqlScript(Driver.class, "jdbc:mysql://localhost", "mesh4xdbtarget", "root", "", sqlFileName);	
		
		//cleanup existing hbm.xml files
		FileUtils.cleanupDirectory(TestHelper.baseDirectoryRootForTest());
	}
	
	@Test
	public void shouldSyncMultiTablesRDF(){
		
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("mesh_example");
		tables.add("mesh_example_1");
		
		ISyncAdapter adapterSource = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", //provide your password here 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tables, 
			"http://mesh4x/test", 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			adapterOpaque,
			null);
			//new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));
		
		// Sync example
		InMemorySyncAdapter adapterTarget = new InMemorySyncAdapter("target", NullIdentityProvider.INSTANCE);
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
	}
	
	@Test
	public void shouldSyncMultiTablesPlainXML(){

		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("mesh_example");
		tables.add("mesh_example_1");
		
		ISyncAdapter adapterSource = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", //provide your password here 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tables, 
			null, 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			adapterOpaque,
			null);
			//new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));
		
		// Sync example
		InMemorySyncAdapter adapterTarget = new InMemorySyncAdapter("target", NullIdentityProvider.INSTANCE);
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());

	}
	
	@Test
	public void shouldSyncMultiTablesVsMsExcelSheets() throws Exception{
		
		// hibernate 
		InMemorySyncAdapter adapterOpaqueSource = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("mesh_example");
		tables.add("mesh_example_1");
		
		String rdfURL = "http://localhost:8080/mesh4x/feeds";
		
		CompositeSyncAdapter adapterSource = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", //provide your password here 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tables, 
			rdfURL, 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			adapterOpaqueSource,
			null);  //new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));


		// create sheets
		
		List<IRDFSchema> sheets = new ArrayList<IRDFSchema>();
		
		for (IIdentifiableSyncAdapter identifiableAdapter : adapterSource.getAdapters()) {
			SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
			HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)splitAdapter.getContentAdapter();
			
			IRDFSchema rdfSchema = (IRDFSchema)hibernateContentAdapter.getMapping().getSchema();
			
			sheets.add(rdfSchema);
		}
		
		// msExcel
		File file = TestHelper.makeFileAndDeleteIfExists("composite_Hibernate_MsExcel.xlsx");
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(file.getCanonicalPath(), NullIdentityProvider.INSTANCE, adapterOpaqueTarget, sheets);
		
		// sync
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaqueSource.getAll().size());
		
		List<Item> items = adapterOpaqueTarget.getAll();
		for (Item item : items) {
			Assert.assertTrue(item.isDeleted());
		}
	}

	@Test
	public void ShouldSyncAllTablesOfTwoDatabaseByRDF(){
		//cleanup existing hbm.xml files
		FileUtils.cleanupDirectory(TestHelper.baseDirectoryRootForTest() + "source");
		FileUtils.cleanupDirectory(TestHelper.baseDirectoryRootForTest() + "target");
		
		//To run this test you have to have two database
		//and this test operates on three existing tables.
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("person");
		tables.add("country");
		
		
		String rdfURL = "http://localhost:8080/mesh4x/feeds";
		
		InMemorySyncAdapter adapterOpaqueSource = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		CompositeSyncAdapter adapterSource = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"",//provide your password here 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tables, 
			rdfURL, 
			TestHelper.baseDirectoryRootForTest() + "source",//directory name for source meta information
			NullIdentityProvider.INSTANCE,
			adapterOpaqueSource,
			null);  //new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));
		
		
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		CompositeSyncAdapter adapterTarget = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
				"jdbc:mysql:///mesh4xdbtarget", 
				"root", 
				"",//provide your password here 
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class,
				tables, 
				rdfURL, 
				TestHelper.baseDirectoryRootForTest() +"target",//directory name for target meta information
				NullIdentityProvider.INSTANCE,
				adapterOpaqueTarget,
				null);  //new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
	}
	
	
	@Test
	public void ShouldSyncAllTablesOfTwoDatabaseByPlainXML(){
		//cleanup existing hbm.xml files
		FileUtils.cleanupDirectory(TestHelper.baseDirectoryRootForTest() + "source");
		FileUtils.cleanupDirectory(TestHelper.baseDirectoryRootForTest() + "target");
		
		//To run this test you have to have two database
		//and this test operates on three existing tables.
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("person");
		tables.add("country");
	
		InMemorySyncAdapter adapterOpaqueSource = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		CompositeSyncAdapter adapterSource = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"",//provide your password here 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tables,
			null,
			TestHelper.baseDirectoryRootForTest() + "source",//directory name for source meta information
			NullIdentityProvider.INSTANCE,
			adapterOpaqueSource,
			null);  //new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));
		

		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		CompositeSyncAdapter adapterTarget = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
				"jdbc:mysql:///mesh4xdbtarget", 
				"root", 
				"",//provide you password here 
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class,
				tables, 
				null, 
				TestHelper.baseDirectoryRootForTest() +"target",//directory name for target meta information
				NullIdentityProvider.INSTANCE,
				adapterOpaqueTarget,
				null);  //new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
	}
}
