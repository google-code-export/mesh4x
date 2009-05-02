package org.mesh4j.sync.adapters.multi.repositories;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsAccessVsMySqlSyncTests {
	
	@Test
	public void shouldSync(){
		
		String ontologyBaseUri = "http://localhost:8080/mesh4x/myExample/";
			
		SplitAdapter adapterSource = HibernateSyncAdapterFactory.createHibernateAdapter(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			"mesh_sync_example", 
			"mesh_sync_info", 
			ontologyBaseUri, 
			TestHelper.baseDirectoryRootForTest());

		IRDFSchema rdfSchemaSource = (IRDFSchema)((HibernateContentAdapter)adapterSource.getContentAdapter()).getMapping().getSchema();
		
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory(ontologyBaseUri);
		String newFileName = TestHelper.fileName("MsExcel_hibernate_RDF_"+IdGenerator.INSTANCE.newID()+".xls");
		SplitAdapter adapterTarget = factory.createSyncAdapter(newFileName, "mesh_sync_example", "uid", NullIdentityProvider.INSTANCE, rdfSchemaSource);
		
		IRDFSchema rdfSchemaTarget = (IRDFSchema)((MsExcelContentAdapter)adapterTarget.getContentAdapter()).getSchema();
		
		Assert.assertNotNull(adapterTarget);
		Assert.assertEquals(rdfSchemaSource.asXML(), rdfSchemaTarget.asXML());
		Assert.assertEquals(0, adapterTarget.getAll().size());
		Assert.assertTrue(adapterSource.getAll().size() > 0);
		
		SyncEngine syncEngine = new SyncEngine(adapterTarget, adapterSource);
		List<Item> conflicts = syncEngine.synchronize();
	
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		Assert.assertEquals(adapterSource.getAll().size(), adapterTarget.getAll().size());

	}

}
