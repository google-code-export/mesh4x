package org.mesh4j.sync.adapters.composite;

import java.io.File;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsAccessMultiTableTests {

	@Test
	public void shouldSyncMultiTables() {
		
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		String mdbFileName = TestHelper.baseDirectoryRootForTest() + "ms-access/DevDB.mdb";
		String[] tables = new String[]{"user", "Oswego"};

		MsAccessSyncAdapterFactory factory = new MsAccessSyncAdapterFactory(TestHelper.baseDirectoryRootForTest(), "http://mesh4x/test");
		ISyncAdapter adapterSource = factory.createSyncAdapterForMultiTables(mdbFileName, tables, NullIdentityProvider.INSTANCE, adapterOpaque);
		
		// Sync example
		InMemorySyncAdapter adapterTarget = new InMemorySyncAdapter("target", NullIdentityProvider.INSTANCE);
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());

	}
	
	@Test
	public void shouldSyncMultiTablesVsMsExcelSheets() throws Exception{
		
		// hibernate 
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		String mdbFileName = TestHelper.baseDirectoryRootForTest() + "ms-access/DevDB.mdb";
		String[] tables = new String[]{"user", "Oswego"};

		MsAccessSyncAdapterFactory factory = new MsAccessSyncAdapterFactory(TestHelper.baseDirectoryRootForTest(), "http://mesh4x/test");
		CompositeSyncAdapter adapterSource = factory.createSyncAdapterForMultiTables(mdbFileName, tables, NullIdentityProvider.INSTANCE, adapterOpaque);
		

		// create sheets
		
		// TODO (JMT) improve this structure
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
	
}
