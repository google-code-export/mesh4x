package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.List;

import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.split.ISyncRepository;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
/**
 * Sync information repository, responsible for storing sync information
 * in google spreadsheet,basically CRUD operation
 * 
 * @author Raju
 * @version 1.0,29/4/2009
 */
public class GoogleSpreadSheetSyncRepository implements ISyncRepository,ISyncAware{

	//This attributes are usually used to represent the sync information
	//in a spreadsheet,where every sync row will have following column to
	//hold the items/contents necessary sync information.
	public final static String COLUMN_NAME_SYNC_ID = "syncId";
	public final static String COLUMN_NAME_ENTITY_NAME = "entityName";
	public final static String COLUMN_NAME_ENTITY_ID = "entityId";
	public final static String COLUMN_NAME_VERSION = "version";
	public final static String COLUMN_NAME_SYNC = "sync";
	
	private IGoogleSpreadSheet spreadSheet = null;
	private IIdentityProvider identityProvider = null;
	private IIdGenerator idGenerator = null;
	
	public GoogleSpreadSheetSyncRepository(IGoogleSpreadSheet spreadSheet,IIdentityProvider identityProvider,
			IIdGenerator idGenerator,String sheetName){
		
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		
		this.spreadSheet = spreadSheet;
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
	}
	
	@Override
	public SyncInfo get(String syncId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SyncInfo> getAll(String entityName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String newSyncID(IContent content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(SyncInfo syncInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginSync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endSync() {
		// TODO Auto-generated method stub
		
	}

}
