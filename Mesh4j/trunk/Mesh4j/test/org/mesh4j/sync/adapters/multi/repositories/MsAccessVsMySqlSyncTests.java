package org.mesh4j.sync.adapters.multi.repositories;

import junit.framework.Assert;

import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsAccessVsMySqlSyncTests {
	
	@Test
	public void shouldSync(){
		
		String ontologyBaseUri = "http://localhost:8080/mesh4x/myExample";
			
		SplitAdapter adapterSource = makeHibernateAdapter(ontologyBaseUri);
		IRDFSchema rdfSchemaSource = (IRDFSchema)((HibernateContentAdapter)adapterSource.getContentAdapter()).getMapping().getSchema();
		
		String fileName = TestHelper.fileName("MsExcel_hibernate_RDF_"+IdGenerator.INSTANCE.newID()+".xls");
		SplitAdapter adapterTarget = makeExcelAdapter(ontologyBaseUri, rdfSchemaSource, fileName);
		MsExcelContentAdapter excelContent = ((MsExcelContentAdapter)adapterTarget.getContentAdapter());
		
		Assert.assertNotNull(adapterTarget);
		Assert.assertEquals(0, adapterTarget.getAll().size());

		int size = adapterSource.getAll().size();
	
		SyncEngine syncEngine = syncAndAssert(adapterSource, adapterTarget);
		
		// sync to create excel
		TestHelper.assertSync(syncEngine);
	
		// no changes - create again the adapters emulating other sync
		TestHelper.assertSync(syncEngine);
	
		// add
		excelContent.beginSync();
		String value = IdGenerator.INSTANCE.newID();
		int rowNum = excelContent.getSheet().getPhysicalNumberOfRows();
		Row row = excelContent.getSheet().createRow(rowNum);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(excelContent.getWorkbook(), row, 0, value);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(excelContent.getWorkbook(), row, 1, value);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(excelContent.getWorkbook(), row, 2, value);
		excelContent.endSync();
		
		Assert.assertEquals(size, adapterSource.getAll().size());
		Assert.assertEquals(size + 1, adapterTarget.getAll().size());
		TestHelper.assertSync(syncEngine);
	
		// update - create again the adapters emulating other sync
		excelContent.beginSync();
		row = excelContent.getSheet().getRow(rowNum);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(excelContent.getWorkbook(), row, 2, IdGenerator.INSTANCE.newID());
		excelContent.endSync();
		
		Assert.assertEquals(size + 1, adapterSource.getAll().size());
		Assert.assertEquals(size + 1, adapterTarget.getAll().size());
		TestHelper.assertSync(syncEngine);
			
		// delete - create again the adapters emulating other sync
		excelContent.beginSync();
		row = excelContent.getSheet().getRow(rowNum);
		excelContent.getSheet().removeRow(row);
		excelContent.endSync();
		
		Assert.assertEquals(size + 1, adapterSource.getAll().size());
		Assert.assertEquals(size + 1, adapterTarget.getAll().size());
		TestHelper.assertSync(syncEngine);
		
	}
	
	@Test
	public void shouldSyncEmulatingDifferentsSyncSessions(){
		
		String ontologyBaseUri = "http://localhost:8080/mesh4x/myExample";
		
		SplitAdapter adapterSource = makeHibernateAdapter(ontologyBaseUri);
		IRDFSchema rdfSchemaSource = (IRDFSchema)((HibernateContentAdapter)adapterSource.getContentAdapter()).getMapping().getSchema();
		
		String fileName = TestHelper.fileName("MsExcel_hibernate_RDF_"+IdGenerator.INSTANCE.newID()+".xls");
		SplitAdapter adapterTarget = makeExcelAdapter(ontologyBaseUri, rdfSchemaSource, fileName);
				
		Assert.assertNotNull(adapterTarget);
		Assert.assertEquals(0, adapterTarget.getAll().size());

		int size = adapterSource.getAll().size();
	
		// sync to create excel
		adapterSource = makeHibernateAdapter(ontologyBaseUri);
		adapterTarget = makeExcelAdapter(ontologyBaseUri, rdfSchemaSource, fileName);
		syncAndAssert(adapterSource, adapterTarget);
	
		// no changes - create again the adapters emulating other sync
		adapterSource = makeHibernateAdapter(ontologyBaseUri);
		adapterTarget = makeExcelAdapter(ontologyBaseUri, rdfSchemaSource, fileName);
		syncAndAssert(adapterSource, adapterTarget);
	
		// add
		adapterSource = makeHibernateAdapter(ontologyBaseUri);
		adapterTarget = makeExcelAdapter(ontologyBaseUri, rdfSchemaSource, fileName);
		
		MsExcelContentAdapter excelContent = ((MsExcelContentAdapter)adapterTarget.getContentAdapter());
		excelContent.beginSync();
		String value = IdGenerator.INSTANCE.newID();
		int rowNum = excelContent.getSheet().getPhysicalNumberOfRows();
		Row row = excelContent.getSheet().createRow(rowNum);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(excelContent.getWorkbook(), row, 0, value);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(excelContent.getWorkbook(), row, 1, value);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(excelContent.getWorkbook(), row, 2, value);
		excelContent.endSync();
		
		Assert.assertEquals(size, adapterSource.getAll().size());
		Assert.assertEquals(size + 1, adapterTarget.getAll().size());
		syncAndAssert(adapterSource, adapterTarget);
	
		// update - create again the adapters emulating other sync
		adapterSource = makeHibernateAdapter(ontologyBaseUri);
		adapterTarget = makeExcelAdapter(ontologyBaseUri, rdfSchemaSource, fileName);
		excelContent = ((MsExcelContentAdapter)adapterTarget.getContentAdapter());
		
		excelContent.beginSync();
		row = excelContent.getSheet().getRow(rowNum);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(excelContent.getWorkbook(), row, 2, IdGenerator.INSTANCE.newID());
		excelContent.endSync();
		
		Assert.assertEquals(size + 1, adapterSource.getAll().size());
		Assert.assertEquals(size + 1, adapterTarget.getAll().size());
		syncAndAssert(adapterSource, adapterTarget);
			
		// delete - create again the adapters emulating other sync
		adapterSource = makeHibernateAdapter(ontologyBaseUri);
		adapterTarget = makeExcelAdapter(ontologyBaseUri, rdfSchemaSource, fileName);
		excelContent = ((MsExcelContentAdapter)adapterTarget.getContentAdapter());
		
		excelContent.beginSync();
		row = excelContent.getSheet().getRow(rowNum);
		excelContent.getSheet().removeRow(row);
		excelContent.endSync();
		
		Assert.assertEquals(size + 1, adapterSource.getAll().size());
		Assert.assertEquals(size + 1, adapterTarget.getAll().size());
		syncAndAssert(adapterSource, adapterTarget);
		
	}
	
	private SyncEngine syncAndAssert(SplitAdapter adapterSource, SplitAdapter adapterTarget) {
		IRDFSchema rdfSchemaSource = (IRDFSchema)((HibernateContentAdapter)adapterSource.getContentAdapter()).getMapping().getSchema();
		IRDFSchema  rdfSchemaTarget = (IRDFSchema)((MsExcelContentAdapter)adapterTarget.getContentAdapter()).getSchema();
		
		Assert.assertEquals(rdfSchemaSource.asXML(), rdfSchemaTarget.asXML());
		
		SyncEngine syncEngine = new SyncEngine(adapterTarget, adapterSource);
		TestHelper.assertSync(syncEngine);
		return syncEngine;
	}

	private SplitAdapter makeExcelAdapter(String ontologyBaseUri, IRDFSchema rdfSchema, String fileName) {
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory(ontologyBaseUri);
		return factory.createSyncAdapter(fileName, "mesh_sync_example", "uid", NullIdentityProvider.INSTANCE, rdfSchema);
	}

	private SplitAdapter makeHibernateAdapter(String ontologyBaseUri) {
		return HibernateSyncAdapterFactory.createHibernateAdapter(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			"mesh_sync_example", 
			"mesh_sync_info", 
			ontologyBaseUri, 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE);
	}
	
	//@Test
	public void shouldSyncSahana(){
		
		String ontologyBaseUri = "http://localhost:8080/mesh4x/sahana";
			
		SplitAdapter adapterSource = HibernateSyncAdapterFactory.createHibernateAdapter(
			"jdbc:mysql:///sahana", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			"users", 
			"mesh4x_users_syncinfo", 
			ontologyBaseUri, 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE);

		IRDFSchema rdfSchemaSource = (IRDFSchema)((HibernateContentAdapter)adapterSource.getContentAdapter()).getMapping().getSchema();
		
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory(ontologyBaseUri);
		//String newFileName = TestHelper.fileName("sahana_users_"+IdGenerator.INSTANCE.newID()+".xls");
		String newFileName = TestHelper.fileName("sahana_users.xls");
		SplitAdapter adapterTarget = factory.createSyncAdapter(newFileName, "users", "p_uuid", NullIdentityProvider.INSTANCE, rdfSchemaSource);
		
		IRDFSchema rdfSchemaTarget = (IRDFSchema)((MsExcelContentAdapter)adapterTarget.getContentAdapter()).getSchema();
		
		Assert.assertNotNull(adapterTarget);
		Assert.assertEquals(rdfSchemaSource.asXML(), rdfSchemaTarget.asXML());
		Assert.assertEquals(3, adapterTarget.getAll().size());
		//Assert.assertEquals(0, adapterTarget.getAll().size());
		Assert.assertTrue(adapterSource.getAll().size() > 0);
		
		syncAndAssert(adapterSource, adapterTarget);

	}
}
