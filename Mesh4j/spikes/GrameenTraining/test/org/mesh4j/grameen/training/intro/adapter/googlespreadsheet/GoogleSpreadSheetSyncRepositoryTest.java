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
	private String userName = "mesh4x@gmail.com";
	private String passWord = "g@l@xy24";
	private String GOOGLE_SPREADSHEET_FIELD = "pLUqch-enpf1-GcqnD6qjSA";
	
	
	@Before
	public void setUp(){
		spreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		workSheet = spreadsheet.getGSWorksheet("SYNC_INFO");
	}
	
	@Test
	public void ShouldSaveSyncInfo(){
	    Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(2, syncRepository.getAll("user").size());
	}
	
	@Test
	public void ShouldSaveSyncInfoToSpreadSheetWithEndSyncOperation(){
	    Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(2, syncRepository.getAll("user").size());
		
		syncRepository.beginSync();
		syncRepository.endSync();
		
		//now newly created spreadsheet adapter to test if it can capable to
		//load the saved row from spreadsheet
		IGoogleSpreadSheet newSpreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		GSWorksheet newWorkSheet = newSpreadsheet.getGSWorksheet("SYNC_INFO");
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,newWorkSheet,identityProvider,idGenerator,"SYNC_INFO");
		Assert.assertEquals(2, newSyncRepository.getAll("user").size());
	}
	
	@Test
	public void ShouldNotSaveSyncInfoToPhysicalSpreadSheetWithoutEndSyncOperation(){
	    Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(2, syncRepository.getAll("user").size());
		
		//now newly created spreadsheet adapter to test if it can capable to
		//load the saved row from spreadsheet
		IGoogleSpreadSheet newSpreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		GSWorksheet newWorkSheet = newSpreadsheet.getGSWorksheet("SYNC_INFO");
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,newWorkSheet,identityProvider,idGenerator,"SYNC_INFO");
		Assert.assertEquals(0, newSyncRepository.getAll("user").size());
	}
	
	@Test
	public void ShouldUpdateSyncInfo(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		sync.update("sharif", new Date());
		SyncInfo info = new SyncInfo(sync,"user","2",1);
		syncRepository.save(info);
		
		Assert.assertEquals(2, syncRepository.getAll("user").size());
		
		String updateBy = syncRepository.get(sync.getId()).getSync().getLastUpdate().getBy();
		Assert.assertEquals(updateBy, "sharif");
	}
	
	@Test
	public void ShouldUpdateSyncInfoToSpreadSheetWithEndSyncOperation(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		sync.update("sharif", new Date());
		SyncInfo info = new SyncInfo(sync,"user","2",1);
		syncRepository.save(info);
		
		Assert.assertEquals(2, syncRepository.getAll("user").size());
		
		syncRepository.beginSync();
		syncRepository.endSync();
		
		IGoogleSpreadSheet newSpreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		GSWorksheet newWorkSheet = newSpreadsheet.getGSWorksheet("SYNC_INFO");
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,newWorkSheet,identityProvider,idGenerator,"SYNC_INFO");
		
		String updateBy = newSyncRepository.get(sync.getId()).getSync().getLastUpdate().getBy();
		Assert.assertEquals(updateBy, "sharif");
		
	}
	
	@Test
	public void ShouldGetSyncInfo(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		//compare the entity the id
		Assert.assertEquals(syncRepository.get(syncInfo.getSyncId()).getId(),"2");
		//compare the sync id
		Assert.assertEquals(syncRepository.get(syncInfo.getSyncId()).getSync().getId(),sync.getId());
	}
	
	@Test
	public void ShouldGetSyncInfoFromSpreadSheetWithEndSyncOperation(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		//compare the entity the id
		Assert.assertEquals(syncRepository.get(syncInfo.getSyncId()).getId(),"2");
		//compare the sync id
		Assert.assertEquals(syncRepository.get(syncInfo.getSyncId()).getSync().getId(),sync.getId());
		
		syncRepository.beginSync();
		syncRepository.endSync();
		
		IGoogleSpreadSheet newSpreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		GSWorksheet newWorkSheet = newSpreadsheet.getGSWorksheet("SYNC_INFO");
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,newWorkSheet,identityProvider,idGenerator,"SYNC_INFO");
		
		//compare the entity the id
		Assert.assertEquals(newSyncRepository.get(syncInfo.getSyncId()).getId(),"2");
		//compare the sync id
		Assert.assertEquals(newSyncRepository.get(syncInfo.getSyncId()).getSync().getId(),sync.getId());
	}
	
	@Test
	public void ShouldGetAll(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(syncRepository.getAll("user").size(),2);
		
		for(SyncInfo info : syncRepository.getAll("user")){
			if(info.getId() == "2"){
				Assert.assertEquals(info.getId(),"2");
				Assert.assertEquals(info.getSync().getId(),sync.getId());
			}
		}
		
	}
	
	@Test
	public void ShouldGetAllFromSpreadSheetWithEndSyncOperaton(){
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,workSheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(syncRepository.getAll("user").size(),2);
		
		for(SyncInfo info : syncRepository.getAll("user")){
			if(info.getId() == "2"){
				Assert.assertEquals(info.getId(),"2");
				Assert.assertEquals(info.getSync().getId(),sync.getId());
			}
		}
		
		syncRepository.beginSync();
		syncRepository.endSync();
		
		IGoogleSpreadSheet newSpreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		GSWorksheet newWorkSheet = newSpreadsheet.getGSWorksheet("SYNC_INFO");
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,newWorkSheet,identityProvider,idGenerator,"SYNC_INFO");
		
		Assert.assertEquals(newSyncRepository.getAll("user").size(),2);
		
		for(SyncInfo info : newSyncRepository.getAll("user")){
			if(info.getId() == "2"){
				Assert.assertEquals(info.getId(),"2");
				Assert.assertEquals(info.getSync().getId(),sync.getId());
			}
		}
	}
	
}
