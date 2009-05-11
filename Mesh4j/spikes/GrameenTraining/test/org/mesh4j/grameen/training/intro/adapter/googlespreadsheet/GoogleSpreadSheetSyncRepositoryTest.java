package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
/**
 * 
 * @author Raju
 */

public class GoogleSpreadSheetSyncRepositoryTest {
	
	private IGoogleSpreadSheet spreadsheet;
	private GSWorksheet<GSRow<GSCell>> workSheet;
	String userName = "gspreadsheet.test@gmail.com";
	String passWord = "java123456";
	String GOOGLE_SPREADSHEET_FIELD = "peo4fu7AitTo8e3v0D8FCew";

	
	
	
	
	@Test
	public void ShouldSaveSyncInfo(){
		
		cleanUP();

		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

	    Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(2, syncRepository.getAll("user").size());
	}
	
	@Test
	public void ShouldSaveSyncInfoToSpreadSheetWithEndSyncOperation(){
		
		cleanUP();
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

	    Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
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
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		Assert.assertEquals(2, newSyncRepository.getAll("user").size());
	}
	
	@Test
	public void ShouldNotSaveSyncInfoToPhysicalSpreadSheetWithoutEndSyncOperation(){
		
		cleanUP();
		
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

	    Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
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
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		Assert.assertEquals(0, newSyncRepository.getAll("user").size());
	}
	
	@Test
	public void ShouldUpdateSyncInfo(){
		
		cleanUP();
		
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
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
		
		cleanUP();
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
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
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		
		String updateBy = newSyncRepository.get(sync.getId()).getSync().getLastUpdate().getBy();
		Assert.assertEquals(updateBy, "sharif");
		
	}
	
	@Test
	public void ShouldGetSyncInfo(){
		
		cleanUP();
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		//compare the entity the id
		Assert.assertEquals("2",syncRepository.get(syncInfo.getSyncId()).getId());
		//compare the sync id
		Assert.assertEquals(syncRepository.get(syncInfo.getSyncId()).getSync().getId(),sync.getId());
	}
	
	@Test
	public void ShouldGetSyncInfoFromSpreadSheetWithEndSyncOperation(){
		
		cleanUP();
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		//compare the entity the id
		Assert.assertEquals("2",syncRepository.get(syncInfo.getSyncId()).getId());
		//compare the sync id
		Assert.assertEquals(syncRepository.get(syncInfo.getSyncId()).getSync().getId(),sync.getId());
		
		syncRepository.beginSync();
		syncRepository.endSync();
		
		IGoogleSpreadSheet newSpreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		GSWorksheet newWorkSheet = newSpreadsheet.getGSWorksheet("SYNC_INFO");
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		
		//compare the entity the id
		Assert.assertEquals("2",newSyncRepository.get(syncInfo.getSyncId()).getId());
		//compare the sync id
		Assert.assertEquals(newSyncRepository.get(syncInfo.getSyncId()).getSync().getId(),sync.getId());
	}
	
	@Test
	public void ShouldGetAll(){
		
		cleanUP();
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(syncRepository.getAll("user").size(),2);
		
		for(SyncInfo info : syncRepository.getAll("user")){
			if(info.getId() == "2"){
				Assert.assertEquals("2",info.getId());
				Assert.assertEquals(info.getSync().getId(),sync.getId());
			}
		}
		
	}
	
	@Test
	public void ShouldGetAllFromSpreadSheetWithEndSyncOperaton(){
		
		cleanUP();
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;

		
		Sync sync = null;
		SyncInfo syncInfo = null;
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "1", 1);
		
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(1, syncRepository.getAll("user").size());
		
		sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		syncInfo = new SyncInfo(sync, "user", "2", 1);
		syncRepository.save(syncInfo);
		
		Assert.assertEquals(2,syncRepository.getAll("user").size());
		
		for(SyncInfo info : syncRepository.getAll("user")){
			if(info.getId() == "2"){
				Assert.assertEquals("2",info.getId());
				Assert.assertEquals(info.getSync().getId(),sync.getId());
			}
		}
		
		syncRepository.beginSync();
		syncRepository.endSync();
		
		IGoogleSpreadSheet newSpreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		GSWorksheet newWorkSheet = newSpreadsheet.getGSWorksheet("SYNC_INFO");
		GoogleSpreadSheetSyncRepository newSyncRepository = new GoogleSpreadSheetSyncRepository(newSpreadsheet,identityProvider,idGenerator,"SYNC_INFO");
		
		Assert.assertEquals(2,newSyncRepository.getAll("user").size());
		
		for(SyncInfo info : newSyncRepository.getAll("user")){
			if(info.getId() == "2"){
				Assert.assertEquals("2",info.getId());
				Assert.assertEquals(info.getSync().getId(),sync.getId());
			}
		}
	}
	

	@Before
	public void setUp(){
		spreadsheet = new GoogleSpreadsheet(GOOGLE_SPREADSHEET_FIELD,userName,passWord);
		workSheet = GoogleSpreadsheetUtils.getOrCreateSyncSheetIfAbsent(spreadsheet.getGSSpreadsheet(),"sync_info");
	}
	
	
	private void cleanUP(){
		
		for(Map.Entry<String, GSRow<GSCell>> mapRows : workSheet.getGSRows().entrySet()){
			//We should not delete the first header row
			if(Integer.parseInt(mapRows.getKey()) > 1){
				workSheet.deleteChildElement(mapRows.getKey());	
			}
		}
		GoogleSpreadsheetUtils.flush(spreadsheet.getSpreadsheetService(), spreadsheet.getGSSpreadsheet());
	}
	
	
}
