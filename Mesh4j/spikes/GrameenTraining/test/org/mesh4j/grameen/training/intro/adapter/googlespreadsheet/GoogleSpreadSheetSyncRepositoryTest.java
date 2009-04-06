package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;

public class GoogleSpreadSheetSyncRepositoryTest {
	
	private IGoogleSpreadSheet spreadsheet;
	private IIdentityProvider identityProvider = null;
	private IIdGenerator idGenerator = null;
	private ISpreadSheetToXMLMapper mapper;
	private GSWorksheet workSheet;
	private String userName = "saiful.raju@gmail.com";
	private String passWord = "";
	
	
	
	@Before
	public void setUp(){
		
		spreadsheet = new GoogleSpreadsheet("pTOwHlskRe06LOcTpClQ-Bw",userName,passWord);
		mapper = new SpreadSheetToXMLMapper();
		workSheet = spreadsheet.getGSWorksheet("user");
	}
	
	@Test
	public void ShouldSaveSyncInfo(){
		GoogleSpreadSheetSyncRepository syncRepository = new GoogleSpreadSheetSyncRepository(spreadsheet,
				workSheet,mapper,identityProvider,idGenerator,"SYNC_INFO");
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "raju", new Date(), false);
		SyncInfo syncInfo = new SyncInfo(sync, "user", "1", 1);
		syncRepository.save(syncInfo);
		
		
	}
	public void ShouldUpdateSyncInfo(){
		
	}
	public void ShouldGetSyncInfo(){
		
	}
	public void ShouldGetAll(){
		
	}
	
}
