package org.mesh4j.sync.adapters.hibernate;

import java.io.File;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.SqlDBUtils;

import com.mysql.jdbc.Driver;

public class HibernateTypesTests {

	@Test
	public void shouldSyncDecimalVsFeed(){
			
		ISyncAdapter adapterSource = HibernateSyncAdapterFactory.createHibernateAdapter(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			"mesh_example_1",  
			"http://mesh4x/test", 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			null);
			//new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));
		
		String fileName = TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_feed_test_hibernate_types.xml");
		Feed feed = new Feed("test", "test", "http://mesh4x/test/testHibernateTypes");
		ISyncAdapter adapterTarget = new FeedAdapter(fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
		
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
	}
	
	@Test
	public void shouldSyncDecimalVsMsExcel(){
		
		String rdfBaseURL = "http://mesh4x/test";
		String tableName = "mesh_example_1";
		
		SplitAdapter adapterSource = HibernateSyncAdapterFactory.createHibernateAdapter(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tableName, 
			rdfBaseURL, 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			null);
			//new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));

		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapterSource.getContentAdapter()).getMapping().getSchema();
		
		String excelFileName = TestHelper.fileName(IdGenerator.INSTANCE.newID() + "_test_hibernate_types.xls");

		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(excelFileName), NullIdentityProvider.INSTANCE, rdfSchema);
					
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		TestHelper.assertSync(syncEngine);
	}
	
	
	@Test //Issue# 125
	public void ShouldSyncMySQLWithNumericIdProperty(){
		// prepare/update the mysql for this specific test
		String sqlFileName = FileUtils.getResourceFileURL("mesh4j_table_mysql.sql").getFile();
		SqlDBUtils.executeSqlScript(Driver.class, "jdbc:mysql://localhost", "mesh4xdb", "root", "", sqlFileName);	

		String baseDirectory = TestHelper.baseDirectoryForTest();
		File mappingDirectory = new File(baseDirectory + File.separator + "mesh4xdb");
		String connectionUri = "jdbc:mysql://localhost/mesh4xdb";
		
		ISyncAdapter sourceAsMySql = HibernateSyncAdapterFactory.createHibernateAdapter(
				connectionUri,
				"root",
				"",
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class, 
				"user2", 
				"http://localhost:8080/mesh4x/feeds",
				mappingDirectory.getAbsolutePath(),
				NullIdentityProvider.INSTANCE,
				null);
		
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsMySql;
		ISchema sourceSchema = ((HibernateContentAdapter)splitAdapterSource.getContentAdapter()).getMapping().getSchema();
		
		File targetContentFile = new File(TestHelper.baseDirectoryForTest() + "target_"+IdGenerator.INSTANCE.newID()+".xls");
		
		ISyncAdapter targetAsExcel = MsExcelRDFSyncAdapterFactory.createSyncAdapter(new MsExcel(targetContentFile.getAbsolutePath()),NullIdentityProvider.INSTANCE, (IRDFSchema)sourceSchema);

		SyncEngine syncEngine = new SyncEngine(splitAdapterSource, targetAsExcel);
		TestHelper.assertSync(syncEngine);
		
		FileUtils.cleanupDirectory(TestHelper.baseDirectoryForTest());
	}
}
