package org.mesh4j.sync.adapters.msaccess;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;

public class MsAccessHelperTests {

	//@Test
	public void shouldCreateMapping() throws Exception{
		MsAccessHibernateMappingGenerator.createMapping("C:\\mesh4x\\tests\\ms-access\\epiinfo\\test1\\epiinfo.mdb", "Oswego", "C:\\mesh4x\\tests\\ms-access\\epiinfo\\test1\\Oswego.hbm.xml");
	}
	
	@Test
	public void executeSync() throws Exception{

		ISyncAdapter syncAdapterA = MsAccessSyncAdapterFactory.createSyncAdapterFromFile(
			"C:\\mesh4x\\tests\\ms-access\\epiinfo\\test1\\epiinfo.mdb", 
			"Oswego", 
			"C:\\mesh4x\\tests\\ms-access\\epiinfo\\test1");
			
		ISyncAdapter syncAdapterB = MsAccessSyncAdapterFactory.createSyncAdapterFromFile(
			"C:\\mesh4x\\tests\\ms-access\\epiinfo\\test2\\epiinfo.mdb", 
			"Oswego", 
			"C:\\mesh4x\\tests\\ms-access\\epiinfo\\test2");
		
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
