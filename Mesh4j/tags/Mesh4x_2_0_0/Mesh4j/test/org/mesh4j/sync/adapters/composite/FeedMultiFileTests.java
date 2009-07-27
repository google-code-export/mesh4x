package org.mesh4j.sync.adapters.composite;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import junit.framework.Assert;

import org.dom4j.DocumentHelper;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.zip.ZipFeedsSyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccess;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessJackcessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.Schema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

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
				
		Map<String, String[]> sheets = new HashMap<String, String[]>();
		sheets.put("sheet1", new String[]{"Code"});
		sheets.put("sheet2", new String[]{"Code"});
		sheets.put("sheet3", new String[]{"Code"});
		
		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(excelFileName, NullIdentityProvider.INSTANCE, sheets, adapterOpaqueTarget, "http://localhost:8080/mesh4x/feeds");
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
		
		TestHelper.assertSync(syncEngine);
		
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
	}
	
	@Test
	public void shouldSyncAllFeedFilesAsZipVsMsExcel() throws Exception{
		
		// zip adapter
		File zipFile = TestHelper.makeFileAndDeleteIfExists("exampleMSExcel.zip");
		
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFile.getCanonicalPath(), NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		
		// excel
		String excelFileName = MsExcelMultiSheetsTests.createMsExcelFile("composite_Feed_MsExcel.xlsx");
		
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		Map<String, String[]> sheets = new HashMap<String, String[]>();
		sheets.put("sheet1", new String[]{"Code"});
		sheets.put("sheet2", new String[]{"Code"});
		sheets.put("sheet3", new String[]{"Code"});
		
		ISyncAdapter target = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(excelFileName, NullIdentityProvider.INSTANCE, sheets, adapterOpaqueTarget, "http://localhost:8080/mesh4x/feeds");
				
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
		
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
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
			targetAdapterOpaque,
			null);
			//new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));
						
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());

		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());
	}
	
	@Test
	public void shouldSyncAllFeedFilesAsZipVsMsAccessWithJackcess() throws Exception{
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("Oswego");

		// zip adapter
		String zipFileName = TestHelper.fileName("exampleMSAccess.zip");
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		//ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest(), tables);
		
		// excel
		InMemorySyncAdapter targetAdapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
				
		String mdbFileName = getMsAccessFileNameToTest();

		ISyncAdapter target = MsAccessJackcessSyncAdapterFactory.createSyncAdapterForMultiTables(new MsAccess(mdbFileName), tables, NullIdentityProvider.INSTANCE, targetAdapterOpaque, "http://mesh4x/test");
						
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());

		TestHelper.assertSync(syncEngine);
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());
	}
	
	@Test
	public void shouldSyncAllFeedFilesAsZipVsMsAccessWithHibernate() throws Exception{
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("Oswego");

		// zip adapter
		String zipFileName = TestHelper.fileName("exampleMSAccess.zip");
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		//ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest(), tables);
		
		// excel
		InMemorySyncAdapter targetAdapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
				
		String mdbFileName = getMsAccessFileNameToTest();

		ISyncAdapter target = MsAccessHibernateSyncAdapterFactory.createSyncAdapterForMultiTables(mdbFileName, tables, "http://mesh4x/test", TestHelper.baseDirectoryRootForTest(), NullIdentityProvider.INSTANCE, targetAdapterOpaque);
						
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());

		TestHelper.assertSync(syncEngine);
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());
	}
	
	@Test
	public void shouldSyncAllFeedFilesAsZipVsCloud() throws Exception{
		
		// zip adapter
		String zipFileName = TestHelper.fileName("exampleMSExcel.zip");
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		source.beginSync();
		
		// Cloud
		String baseURL = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "compositeHTTPZipFeedFile";
		
		// extract schemas
		List<ISchema> schemas = new ArrayList<ISchema>();
		for (IIdentifiableSyncAdapter identifiableAdapter : source.getCompositeAdapter().getAdapters()) {
			IdentifiableSyncAdapter adapter = (IdentifiableSyncAdapter)identifiableAdapter;
			
			String feedName = adapter.getType();
			schemas.add(new Schema(DocumentHelper.createElement(feedName)));
		}
				
		// create http sync adapter
		HttpSyncAdapter target = HttpSyncAdapterFactory.createSyncAdapterForMultiDataset(baseURL, meshGroup, NullIdentityProvider.INSTANCE, schemas);
			
		// sync
		SyncEngine syncEngine = new SyncEngine(target, source);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());

		TestHelper.assertSync(syncEngine);
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
	}
	
	private String getMsAccessFileNameToTest() {
		try{
			String localFileName = this.getClass().getResource("DevDB2003.mdb").getFile();
			String fileName = TestHelper.fileName("msAccess"+IdGenerator.INSTANCE.newID()+".mdb");
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
