package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;

public class GoogleSpreadSheetSyncRepositoryTest {
	
	private IGoogleSpreadSheet spreadsheet;
	private IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
	private IIdGenerator idGenerator = IdGenerator.INSTANCE;
	private GSWorksheet workSheet;
	private String userName = "saiful.raju@gmail.com";
	private String passWord = "";
	GoogleSpreadSheetSyncRepository syncRepository ;
	
	
	@Before
	public void setUp(){
		spreadsheet = new GoogleSpreadsheet("pTOwHlskRe06LOcTpClQ-Bw",userName,passWord);
		workSheet = spreadsheet.getGSWorksheet("SYNC_INFO");
		syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
	}
	
	@Test
	public void ShouldSaveSyncInfo(){
	    Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user"));
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(2, syncRepository.getAll("user"));
	}
	
	public void ShouldUpdateSyncInfo(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user"));
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		sync.update("sharif", new Date());
		SyncInfo info = new SyncInfo(sync,"user","2",1);
		syncRepository.save(info);
		
		Assert.assertEquals(2, syncRepository.getAll("user"));
		
	}
	public void ShouldGetSyncInfo(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user"));
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		syncRepository.get(syncInfo.getSyncId());
		
		
	}
	public void ShouldGetAll(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user"));
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(syncRepository.getAll("user").size(),2);
		
	}
	
	
}
