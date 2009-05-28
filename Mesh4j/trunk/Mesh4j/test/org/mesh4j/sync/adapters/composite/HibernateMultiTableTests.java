package org.mesh4j.sync.adapters.composite;

import java.io.File;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class HibernateMultiTableTests {

	@Test
	public void shouldSyncMultiTables(){
		
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		HashMap<String, String> tables = new HashMap<String, String>();
		tables.put("mesh_sync_example", "mesh_sync_example_sync");
		tables.put("mesh_example_1", "mesh_example_1_sync");
		
		ISyncAdapter adapterSource = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tables, 
			"http://mesh4x/test", 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			adapterOpaque);
		
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
		
		// TODO (JMT) improve this structure
		HashMap<String, String> tables = new HashMap<String, String>();
		tables.put("mesh_sync_example", "mesh_sync_example_sync");
		tables.put("mesh_example_1", "mesh_example_1_sync");
		
		CompositeSyncAdapter adapterSource = HibernateSyncAdapterFactory.createSyncAdapterForMultiTables(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			tables, 
			"http://mesh4x/test", 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			adapterOpaqueSource);


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
		File file = TestHelper.makeFileAndDeleteIfExists("composite_Hibernate_MsExcel.xlsx");
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/feeds");

		ISyncAdapter adapterTarget = factory.createSyncAdapterForMultiSheets(file.getCanonicalPath(), NullIdentityProvider.INSTANCE, adapterOpaqueTarget, sheets);
		
		// sync
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaqueSource.getAll().size());
		Assert.assertEquals(21, adapterOpaqueTarget.getAll().size());
	}
}
