package org.mesh4j.sync.adapters.msaccess;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.test.utils.TestHelper;

public class MsAccessHelperTests {

	//@Test
	public void shouldCreateMapping() throws Exception{
		MsAccessHibernateMappingGenerator.createMapping(TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1\\epiinfo.mdb", "Oswego", TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1\\Oswego.hbm.xml");
	}
	
	@Test
	public void executeSync() throws Exception{

		ISyncAdapter syncAdapterA = MsAccessSyncAdapterFactory.createSyncAdapterFromFile(
			TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1\\epiinfo.mdb", 
			"Oswego", 
			TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test1");
			
		ISyncAdapter syncAdapterB = MsAccessSyncAdapterFactory.createSyncAdapterFromFile(
				TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test2\\epiinfo.mdb", 
			"Oswego", 
			TestHelper.baseDirectoryRootForTest() + "ms-access\\epiinfo\\test2");
		
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
}
