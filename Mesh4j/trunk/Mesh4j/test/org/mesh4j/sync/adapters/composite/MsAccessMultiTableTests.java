package org.mesh4j.sync.adapters.composite;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccess;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessContentAdapter;
import org.mesh4j.sync.adapters.jackcess.msaccess.MsAccessJackcessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class MsAccessMultiTableTests {

	@Test
	public void shouldSyncMultiTablesWithHibernate() {
		
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		String mdbFileName = getMsAccessFileNameToTest();
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("Oswego");

		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryRootForTest(), "http://mesh4x/test");
		ISyncAdapter adapterSource = factory.createSyncAdapterForMultiTables(mdbFileName, tables, NullIdentityProvider.INSTANCE, adapterOpaque);
		
		// Sync example
		InMemorySyncAdapter adapterTarget = new InMemorySyncAdapter("target", NullIdentityProvider.INSTANCE);
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());

	}
	
	@Test
	public void shouldSyncMultiTablesVsMsExcelSheetsWithHibernate() throws Exception{
		
		// hibernate 
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		String mdbFileName = getMsAccessFileNameToTest();

		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("Oswego");

		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryRootForTest(), "http://mesh4x/test");
		CompositeSyncAdapter adapterSource = factory.createSyncAdapterForMultiTables(mdbFileName, tables, NullIdentityProvider.INSTANCE, adapterOpaque);
		

		// create sheets
		
		HashMap<IRDFSchema, String> sheets = new HashMap<IRDFSchema, String>();
		
		for (IIdentifiableSyncAdapter identifiableAdapter : adapterSource.getAdapters()) {
			SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
			HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)splitAdapter.getContentAdapter();
			
			String id = hibernateContentAdapter.getMapping().getIDNode();
			IRDFSchema rdfSchema = (IRDFSchema)hibernateContentAdapter.getMapping().getSchema();
			
			sheets.put(rdfSchema, id);
		}
		
		// msExcel
		File file = TestHelper.makeFileAndDeleteIfExists("composite_MSAccess_MSExcel.xlsx");
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		MsExcelRDFSyncAdapterFactory excelFactory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/feeds");

		ISyncAdapter adapterTarget = excelFactory.createSyncAdapterForMultiSheets(file.getCanonicalPath(), NullIdentityProvider.INSTANCE, adapterOpaqueTarget, sheets);
		
		// sync
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
	}
	
	
	@Test
	public void shouldSyncMultiTablesWithJackcess() {
		
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		String mdbFileName = getMsAccessFileNameToTest();
		
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("Oswego");

		ISyncAdapter adapterSource = MsAccessJackcessSyncAdapterFactory.createSyncAdapterForMultiTables(new MsAccess(mdbFileName), tables, NullIdentityProvider.INSTANCE, adapterOpaque, "http://mesh4x/test");
		
		// Sync example
		InMemorySyncAdapter adapterTarget = new InMemorySyncAdapter("target", NullIdentityProvider.INSTANCE);
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
		
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());

	}
	
	@Test
	public void shouldSyncMultiTablesVsMsExcelSheetswithJackcess() throws Exception{
		
		// hibernate 
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		String mdbFileName = getMsAccessFileNameToTest();

		TreeSet<String> tables = new TreeSet<String>();
		tables.add("user");
		tables.add("Oswego");

		CompositeSyncAdapter adapterSource = MsAccessJackcessSyncAdapterFactory.createSyncAdapterForMultiTables(new MsAccess(mdbFileName), tables, NullIdentityProvider.INSTANCE, adapterOpaque, "http://mesh4x/test");
		
		// create sheets
		
		HashMap<IRDFSchema, String> sheets = new HashMap<IRDFSchema, String>();
		
		for (IIdentifiableSyncAdapter identifiableAdapter : adapterSource.getAdapters()) {
			SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
			MsAccessContentAdapter contentAdapter = (MsAccessContentAdapter)splitAdapter.getContentAdapter();
			
			String id = contentAdapter.getMapping().getIdColumnName();
			IRDFSchema rdfSchema = (IRDFSchema)contentAdapter.getMapping().getSchema();
			
			sheets.put(rdfSchema, id);
		}
		
		// msExcel
		File file = TestHelper.makeFileAndDeleteIfExists("composite_MSAccess_MSExcel.xlsx");
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		MsExcelRDFSyncAdapterFactory excelFactory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/feeds");

		ISyncAdapter adapterTarget = excelFactory.createSyncAdapterForMultiSheets(file.getCanonicalPath(), NullIdentityProvider.INSTANCE, adapterOpaqueTarget, sheets);
		
		// sync
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());

		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
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
