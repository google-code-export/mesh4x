package org.mesh4j.sync.adapters.msaccess;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsAccessHelperTests {

	//@Test
	public void shouldCreateMapping() throws Exception{
		MsAccessHibernateMappingGenerator.createMapping(TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1\\epiinfo.mdb", "Oswego", TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1\\Oswego.hbm.xml");
	}
	
	@Test
	public void executeSync() throws Exception{

		MsAccessSyncAdapterFactory factory = new MsAccessSyncAdapterFactory(TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1", null);
		
		ISyncAdapter syncAdapterA = factory.createSyncAdapterFromFile(
			"Oswego",
			TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1\\epiinfo.mdb", 
			"Oswego",
			NullIdentityProvider.INSTANCE);
			
		ISyncAdapter syncAdapterB = factory.createSyncAdapterFromFile(
			"Oswego",
			TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test2\\epiinfo.mdb", 
			"Oswego",
			NullIdentityProvider.INSTANCE);
	
		SyncEngine syncEngine = new SyncEngine(syncAdapterA, syncAdapterB);
		
		List<Item> conflicts = syncEngine.synchronize();
		
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());

		List<Item> itemsA = syncAdapterA.getAll();
		Assert.assertFalse(itemsA.isEmpty());
		
		List<Item> itemsB = syncAdapterB.getAll();
		Assert.assertFalse(itemsB.isEmpty());

		Assert.assertEquals(itemsA.size(), itemsB.size());
	}
	
	@Test
	public void shouldCreateSyncTableIfAbsent() throws Exception{
		String mdbFileName = TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1\\epiinfo.mdb";
		String syncTableName = MsAccessHibernateMappingGenerator.getSyncTableName(IdGenerator.INSTANCE.newID().substring(0, 5));
		
		Assert.assertFalse(MsAccessHelper.existTable(mdbFileName, syncTableName));
		MsAccessHelper.createSyncTableIfAbsent(mdbFileName, syncTableName);
		Assert.assertTrue(MsAccessHelper.existTable(mdbFileName, syncTableName));
		
		Set<String> columnNames = MsAccessHelper.getTableColumnNames(mdbFileName, syncTableName);
		
		Assert.assertEquals(5, columnNames.size());
		Assert.assertTrue(columnNames.contains("sync_id"));
		Assert.assertTrue(columnNames.contains("entity_name"));
		Assert.assertTrue(columnNames.contains("entity_id"));
		Assert.assertTrue(columnNames.contains("entity_version"));
		Assert.assertTrue(columnNames.contains("sync_data"));
	}

	@Test
	public void shouldNotCreateSyncTableIfNotAbsent() throws Exception{
		String mdbFileName = TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1\\epiinfo.mdb";
		String syncTableName = MsAccessHibernateMappingGenerator.getSyncTableName("Oswego");
		
		Assert.assertTrue(MsAccessHelper.existTable(mdbFileName, syncTableName));
		MsAccessHelper.createSyncTableIfAbsent(mdbFileName, syncTableName);
		Assert.assertTrue(MsAccessHelper.existTable(mdbFileName, syncTableName));
		
		Set<String> columnNames = MsAccessHelper.getTableColumnNames(mdbFileName, syncTableName);
		
		Assert.assertEquals(5, columnNames.size());
		Assert.assertTrue(columnNames.contains("sync_id"));
		Assert.assertTrue(columnNames.contains("entity_name"));
		Assert.assertTrue(columnNames.contains("entity_id"));
		Assert.assertTrue(columnNames.contains("entity_version"));
		Assert.assertTrue(columnNames.contains("sync_data"));
	}
}
