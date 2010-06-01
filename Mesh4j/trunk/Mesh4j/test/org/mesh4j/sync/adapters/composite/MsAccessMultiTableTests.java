package org.mesh4j.sync.adapters.composite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
		
		List<String> tables = new ArrayList<String>();
		tables.add("user");
		tables.add("Oswego");

		ISyncAdapter adapterSource = MsAccessHibernateSyncAdapterFactory.createSyncAdapterForMultiTables(mdbFileName, tables, "http://mesh4x/test", TestHelper.baseDirectoryRootForTest(), NullIdentityProvider.INSTANCE, adapterOpaque);
		
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

		List<String> tables = new ArrayList<String>();
		tables.add("user");
		tables.add("Oswego");

		CompositeSyncAdapter adapterSource = MsAccessHibernateSyncAdapterFactory.createSyncAdapterForMultiTables(mdbFileName, tables, "http://mesh4x/test", TestHelper.baseDirectoryRootForTest(), NullIdentityProvider.INSTANCE, adapterOpaque);

		// create sheets
		
		List<IRDFSchema> sheets = new ArrayList<IRDFSchema>();
		
		for (IIdentifiableSyncAdapter identifiableAdapter : adapterSource.getAdapters()) {
			SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
			HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)splitAdapter.getContentAdapter();
			
			IRDFSchema rdfSchema = (IRDFSchema)hibernateContentAdapter.getMapping().getSchema();
			
			sheets.add(rdfSchema);
		}
		
		// msExcel
		File file = TestHelper.makeFileAndDeleteIfExists("composite_MSAccess_MSExcel.xlsx");
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(file.getCanonicalPath(), NullIdentityProvider.INSTANCE, adapterOpaqueTarget, sheets);
		
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
		
		List<IRDFSchema> sheets = new ArrayList<IRDFSchema>();
		
		for (IIdentifiableSyncAdapter identifiableAdapter : adapterSource.getAdapters()) {
			SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
			MsAccessContentAdapter contentAdapter = (MsAccessContentAdapter)splitAdapter.getContentAdapter();
			
			IRDFSchema rdfSchema = (IRDFSchema)contentAdapter.getMapping().getSchema();
			
			sheets.add(rdfSchema);
		}
		
		// msExcel
		File file = TestHelper.makeFileAndDeleteIfExists("composite_MSAccess_MSExcel.xlsx");
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterTarget = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(file.getCanonicalPath(), NullIdentityProvider.INSTANCE, adapterOpaqueTarget, sheets);
		
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
