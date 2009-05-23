package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Sync;

/**
 * @author Raju
 * @version 1.0 
 * @since 19/3/2009
 */
public class InMemorySyncRepositoryTest {

	@Test
	public void ShouldSaveSyncInfo(){
		
		String syncId = IdGenerator.INSTANCE.newID();
		String by = "raju";
		Date when = new Date();
		Sync sync = new Sync(syncId,by,when,false);
		SyncInfo syncInfo = new SyncInfo(sync,"syncrepository","1",100);
		InMemorySyncRepository repository = new InMemorySyncRepository();
		repository.save(syncInfo);
		
		Assert.assertEquals(repository.get(syncId),syncInfo);
		Assert.assertEquals(repository.getAll("syncrepository").size(),1);
	}
	
	@Test
	public void ShouldUpdateSyncInfo(){
		
		String syncId = IdGenerator.INSTANCE.newID();
		String by = "raju";
		Date when = new Date();
		Sync sync = new Sync(syncId,by,when,false);
		InMemorySyncRepository repository = new InMemorySyncRepository();
		repository.save(getSyncRow(sync, "Student", "1", 100));
		
		Assert.assertEquals(repository.getAll("Student").size(),1);
		
		//update the previous sync
		sync.update("javed", new Date());
		repository.save(getSyncRow(sync, "Student", "1", 100));
		Assert.assertEquals(repository.getAll("Student").size(),1);
		
		//add a new sync
		syncId = IdGenerator.INSTANCE.newID();
		Sync sync_1 = newSync(syncId,"raju",new Date(),false);
		repository.save(getSyncRow(sync_1, "Student", "1", 100));
		
		Assert.assertEquals(repository.getAll("Student").size(),2);	
	}
	
	@Test
	public void ShouldGetAll(){
		String syncId = IdGenerator.INSTANCE.newID();
		String by = "raju";
		Date when = new Date();
		Sync sync_1 = new Sync(syncId,by,when,false);
		InMemorySyncRepository repository = new InMemorySyncRepository();
		SyncInfo syncInfo_1 = getSyncRow(sync_1,"Student","1",100);
		repository.save(syncInfo_1);
		
		Assert.assertEquals(repository.getAll("Student").size(),1);
		
		//update the previous sync
		sync_1.update("javed", new Date());
		repository.save(getSyncRow(sync_1, "Student", "1", 100));
		Assert.assertEquals(repository.getAll("Student").size(),1);
		
		//add a new sync
		syncId = IdGenerator.INSTANCE.newID();
		Sync sync_2 = newSync(syncId,"raju",new Date(),false);
		SyncInfo syncInfo_2 = getSyncRow(sync_2,"Student","1",100);
		repository.save(syncInfo_2);
		
		Assert.assertEquals(repository.getAll("Student").size(),2);
		
		Assert.assertEquals(syncInfo_2.getSync(),sync_2);
		Assert.assertEquals(syncInfo_1.getSync(),sync_1);
		
		
	}
	private Sync newSync(String syncId,String by,Date when,boolean isDeleted){
		Sync sync = new Sync(syncId,by,when,isDeleted);
		return sync;
	}
	private SyncInfo getSyncRow(Sync sync,String entityName,String entityId,int version){
		SyncInfo syncInfo = new SyncInfo(sync,entityName,entityId,version);
		return syncInfo;
	}
}
