package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.split.ISyncRepository;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;
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
	private final static int SYNC_ID_INDEX = 1;
	
	private IGoogleSpreadSheet spreadSheet = null;
	private IIdentityProvider identityProvider = null;
	private IIdGenerator idGenerator = null;
	//represents a specific sheet of a google spreadsheet
	private GSWorksheet<GSRow<GSCell>> workSheet;
	private ISpreadSheetToXMLMapper mapper;
	
	public GoogleSpreadSheetSyncRepository(IGoogleSpreadSheet spreadSheet,GSWorksheet workSheet,ISpreadSheetToXMLMapper mapper,
											IIdentityProvider identityProvider,IIdGenerator idGenerator,String sheetName){
		
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNull(mapper, "mapper");
		
		this.spreadSheet = spreadSheet;
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		this.workSheet = workSheet;
		this.mapper = mapper;
	}
	
	@Override
	public SyncInfo get(String syncId) {
		Guard.argumentNotNullOrEmptyString(syncId, "syncId");
		
		GSRow row ;
		row = GoogleSpreadsheetUtils.getRow(workSheet,SYNC_ID_INDEX,syncId);
		if(row == null){
			return null;
		} else {
			SyncInfo syncInfo = convertRowToSyncInfo(row);
			return syncInfo;
		}
	}

	
	
	@Override
	public List<SyncInfo> getAll(String entityName) {
		Guard.argumentNotNullOrEmptyString(entityName, "entityName");
		
		List<SyncInfo> listOfAll = new LinkedList<SyncInfo>();
		
		for(Map.Entry<String, GSRow<GSCell>> mapRow : workSheet.getGSRows().entrySet()){
			GSRow row = mapRow.getValue();
			if(row != null){
				SyncInfo syncInfo = convertRowToSyncInfo(row);
				if(syncInfo.getType().equals(entityName)){
					listOfAll.add(syncInfo);
				}
			}
		}
		return listOfAll;
	}

	@Override
	public String newSyncID(IContent content) {
		return this.idGenerator.newID();
	}

	@Override
	public void save(SyncInfo syncInfo) {
		Guard.argumentNotNull(syncInfo, "syncInfo");
		
		GSRow row = GoogleSpreadsheetUtils.getRow(this.workSheet, SYNC_ID_INDEX, syncInfo.getSyncId());
		if(row == null){
			addRow(syncInfo);
		} else {
			updateRow(row,syncInfo);
		}
	}
	private void addRow(SyncInfo syncInfo){
		Element payLoad = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
		GSRow row = mapper.convertXMLElementToRow(this.workSheet,payLoad);
		this.workSheet.addChildElement(row.getElementId(),row);
		GoogleSpreadsheetUtils.flush(spreadSheet.getService(), spreadSheet.getGSSpreadsheet());
		row.refreshMe();
	}
	private void updateRow(GSRow rowTobeUPdated  ,SyncInfo syncInfo){
		Element payLoad = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
		GSRow updatedRow = mapper.normalizeRow(workSheet, payLoad, rowTobeUPdated);
		this.workSheet.addChildElement(String.valueOf(updatedRow.getRowIndex()), updatedRow);
		
		GoogleSpreadsheetUtils.flush(spreadSheet.getService(), spreadSheet.getGSSpreadsheet());
		updatedRow.refreshMe();
	}
	
	@Override
	public void beginSync() {
		this.spreadSheet.setDirty();
	}

	@Override
	public void endSync() {
		this.spreadSheet.flush();
	}
	
	private SyncInfo convertRowToSyncInfo(GSRow<GSCell> row){

		String entityName = row.getGSCell(2).getCellEntry().getCell().getValue();
		String entityId = row.getGSCell(3).getCellEntry().getCell().getValue();
		String version = row.getGSCell(4).getCellEntry().getCell().getValue();
		String syncXml = row.getGSCell(5).getCellEntry().getCell().getValue();
		try {
			Document doc = DocumentHelper.parseText(syncXml);
			Sync sync = SyncInfoParser.convertSyncElement2Sync(doc.getRootElement(), RssSyndicationFormat.INSTANCE, identityProvider, idGenerator);
			return new SyncInfo(sync,entityName,entityId,Integer.parseInt(version));
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

}
