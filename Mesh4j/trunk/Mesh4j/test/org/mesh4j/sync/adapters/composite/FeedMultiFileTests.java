package org.mesh4j.sync.adapters.composite;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.feed.zip.ZipFeedsSyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class FeedMultiFileTests {

	@Test
	public void shouldSyncAllFeedFiles() throws Exception{
		
		// Composite adapter
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		Map<String, String> feeds = new HashMap<String, String>();
		feeds.put("sheet1", TestHelper.makeFileAndDeleteIfExists("composite_Feed_MsExcel_sheet1.xml").getCanonicalPath());
		feeds.put("sheet2", TestHelper.makeFileAndDeleteIfExists("composite_Feed_MsExcel_sheet2.xml").getCanonicalPath());
		feeds.put("sheet3", TestHelper.makeFileAndDeleteIfExists("composite_Feed_MsExcel_sheet3.xml").getCanonicalPath());
		
		ISyncAdapter adapterSource = FeedSyncAdapterFactory.createSyncAdapterForMultiFiles(feeds, NullIdentityProvider.INSTANCE, adapterOpaque);
		
		// Sync example
		String excelFileName = MsExcelMultiSheetsTests.createMsExcelFile("composite_Feed_MsExcel.xlsx");
		
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/feeds");
		
		Map<String, String> sheets = new HashMap<String, String>();
		sheets.put("sheet1", "Code");
		sheets.put("sheet2", "Code");
		sheets.put("sheet3", "Code");
		
		ISyncAdapter adapterTarget = factory.createSyncAdapterForMultiSheets(excelFileName, NullIdentityProvider.INSTANCE, sheets, adapterOpaqueTarget);
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
		
		TestHelper.assertSync(syncEngine);
	}
	
	@Test
	public void shouldSyncAllFeedFilesAsZipVsMsExcel() throws Exception{
		
		// zip adapter
		String zipFileName = TestHelper.fileName("exampleMSExcel.zip");
		
		//TreeSet<String> requiredEntries = new TreeSet<String>();
		//requiredEntries.add("sheet1");
		//requiredEntries.add("sheet2");
		//requiredEntries.add("sheet3");
		//
		//ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest(), requiredEntries);
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		
		// excel
		String excelFileName = MsExcelMultiSheetsTests.createMsExcelFile("composite_Feed_MsExcel.xlsx");
		
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/feeds");
		
		Map<String, String> sheets = new HashMap<String, String>();
		sheets.put("sheet1", "Code");
		sheets.put("sheet2", "Code");
		sheets.put("sheet3", "Code");
		
		ISyncAdapter target = factory.createSyncAdapterForMultiSheets(excelFileName, NullIdentityProvider.INSTANCE, sheets, adapterOpaqueTarget);
				
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
		
		TestHelper.assertSync(syncEngine);
	}
	
	@Test
	public void shouldSyncAllFeedFilesAsZipVsMySql() throws Exception{
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("mesh_example");
		tables.add("mesh_example_1");

		// zip adapter
		String zipFileName = TestHelper.fileName("exampleMySql.zip");
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		//ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest(), tables);
		
		// excel
		InMemorySyncAdapter targetAdapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
				
		ISyncAdapter target = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tables, 
			"http://mesh4x/test", 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			targetAdapterOpaque);
						
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());

		TestHelper.assertSync(syncEngine);
	}
	
	@Test
	public void shouldSyncAllFeedFilesAsZipVsMsAccess() throws Exception{
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("Oswego");

		// zip adapter
		String zipFileName = TestHelper.fileName("exampleMSAccess.zip");
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		//ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest(), tables);
		
		// excel
		InMemorySyncAdapter targetAdapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
				
		String mdbFileName = TestHelper.baseDirectoryRootForTest() + "ms-access/DevDB.mdb";

		MsAccessSyncAdapterFactory factory = new MsAccessSyncAdapterFactory(TestHelper.baseDirectoryRootForTest(), "http://mesh4x/test");
		ISyncAdapter target = factory.createSyncAdapterForMultiTables(mdbFileName, tables, NullIdentityProvider.INSTANCE, targetAdapterOpaque);
						
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());

		TestHelper.assertSync(syncEngine);
	}
	
	@Test
	public void shouldSyncAllFeedFilesAsZipVsCloud() throws Exception{
		
		// zip adapter
		String zipFileName = TestHelper.fileName("exampleMSExcel.zip");
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		
		// Cloud
		String baseURL = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "compositeHTTPZipFeedFile";
		
		// create mesh group
		HttpSyncAdapter.uploadMeshDefinition(baseURL, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// create mesh data sets
		for (IIdentifiableSyncAdapter identifiableAdapter : source.getCompositeAdapter().getAdapters()) {
			IdentifiableSyncAdapter adapter = (IdentifiableSyncAdapter)identifiableAdapter;
			
			String feedName = adapter.getType();
			
			HttpSyncAdapter.uploadMeshDefinition(baseURL, meshGroup + "/" + feedName, RssSyndicationFormat.NAME, "my description", null, null, "jmt");	
		}
				
		// create http sync adapter
		String url = HttpSyncAdapter.makeMeshGroupURLToSync(baseURL + "/" + meshGroup);
		HttpSyncAdapter target = HttpSyncAdapterFactory.createSyncAdapter(url, NullIdentityProvider.INSTANCE);
	
		// sync
		SyncEngine syncEngine = new SyncEngine(target, source);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());

		TestHelper.assertSync(syncEngine);
	}
}
