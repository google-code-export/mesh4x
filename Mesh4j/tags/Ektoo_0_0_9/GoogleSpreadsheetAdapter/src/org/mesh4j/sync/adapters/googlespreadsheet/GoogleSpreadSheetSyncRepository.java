package org.mesh4j.sync.adapters.googlespreadsheet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.split.ISyncRepository;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.util.ServiceException;
/**
 * Sync information repository, responsible for storing sync information
 * in google spreadsheet.
 * 
 * @author Raju
 * @version 1.0,29/4/2009
 */
public class GoogleSpreadSheetSyncRepository implements ISyncRepository,ISyncAware{

	// MODEL VARIABLES
	
	//This attributes are usually used to represent the sync information
	//in a spreadsheet,where every sync row will have following column to
	//hold the items/contents necessary sync information.
	private final static int SYNC_ID_INDEX = 1;

	private IGoogleSpreadSheet spreadSheet = null;
	private IIdentityProvider identityProvider = null;
	private IIdGenerator idGenerator = null;
	private GSWorksheet<GSRow<GSCell>> workSheet; //represents a specific sheet of a google spreadsheet
	
	// BUSINESS METHODS	
	
	public GoogleSpreadSheetSyncRepository(IGoogleSpreadSheet spreadSheet, IIdentityProvider identityProvider, IIdGenerator idGenerator, String syncWorksheetName){
		
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNull(spreadSheet.getGSSpreadsheet(), "spreadSheet.gssSpreadsheet");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNullOrEmptyString(syncWorksheetName, "syncWorksheetName");
		
		this.spreadSheet = spreadSheet;
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		this.workSheet = GoogleSpreadsheetUtils.getOrCreateSyncSheetIfAbsent(spreadSheet.getGSSpreadsheet(), syncWorksheetName);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SyncInfo get(String syncId) {
		Guard.argumentNotNullOrEmptyString(syncId, "syncid");
		
		GSRow row = GoogleSpreadsheetUtils.getRow(workSheet,SYNC_ID_INDEX, syncId);
		if(row == null){
			return null;
		} else {
			SyncInfo syncInfo = convertRowToSyncInfo(row);
			return syncInfo;
		}
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SyncInfo> getAll(String entityName) {
		Guard.argumentNotNullOrEmptyString(entityName, "entityName");
		
		List<SyncInfo> listOfAll = new LinkedList<SyncInfo>();
		
		for(Map.Entry<String, GSRow<GSCell>> mapRow : workSheet.getGSRows().entrySet()){
			GSRow row = mapRow.getValue();
			if(row != null && row.getElementListIndex() >1){
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

	@SuppressWarnings("unchecked")
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
		createSyncRow(syncInfo);
	}
	
	@SuppressWarnings("unchecked")
	private void updateRow(GSRow rowTobeUPdated  ,SyncInfo syncInfo){
		GSRow updatedRow = convertSyncInfoToRow(rowTobeUPdated, syncInfo);
		this.workSheet.updateChildElement(updatedRow.getElementId(), updatedRow);
	}
	
	
	/**
	 * reflect the update syncInfo into the provided GSRow
	 * @param rowTobeUPdated
	 * @param syncInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private GSRow convertSyncInfoToRow(GSRow rowTobeUPdated,SyncInfo syncInfo){
		
		Element syncPayLoad = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
	
		rowTobeUPdated.updateCellValue( syncInfo.getSyncId() , SyncColumn.sync_id.name());
		rowTobeUPdated.updateCellValue( syncInfo.getType() ,SyncColumn.entity_name.name());
		rowTobeUPdated.updateCellValue( syncInfo.getId() ,SyncColumn.entity_id.name());
		rowTobeUPdated.updateCellValue( String.valueOf(syncInfo.getVersion()) ,SyncColumn.entity_version.name());
		rowTobeUPdated.updateCellValue(syncPayLoad.asXML()  ,SyncColumn.sync_data.name());
		
		return rowTobeUPdated;
	}
	
	private SyncInfo convertRowToSyncInfo(GSRow<GSCell> row){

		String entityName = row.getGSCell(2).getCellValue();
		String entityId = row.getGSCell(3).getCellValue();
		String version = row.getGSCell(4).getCellValue();
		String syncXml = row.getGSCell(5).getCellValue();
		try {
			Document doc = DocumentHelper.parseText(syncXml);
			Sync sync = SyncInfoParser.convertSyncElement2Sync(doc.getRootElement(), RssSyndicationFormat.INSTANCE, identityProvider, idGenerator);
			return new SyncInfo(sync,entityName,entityId,Integer.parseInt(version));
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	
	private GSRow<GSCell> createSyncRow(SyncInfo syncInfo){
		GSRow<GSCell>  gsRow = null;
		Element payLoad = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
		
		LinkedHashMap<String, String> listMap = new LinkedHashMap<String, String>();
		
		listMap.put(SyncColumn.sync_id.name(), syncInfo.getSyncId());
		listMap.put(SyncColumn.entity_name.name(), syncInfo.getType());
		listMap.put(SyncColumn.entity_id.name(), syncInfo.getId());
		listMap.put(SyncColumn.entity_version.name(), String.valueOf(syncInfo.getVersion()));
		listMap.put(SyncColumn.sync_data.name(), payLoad.asXML());
		
		try {
			gsRow = this.workSheet.createNewRow(listMap);
		} catch (IOException e) {
			throw new MeshException(e);
		} catch (ServiceException e) {
			throw new MeshException(e);
		}
		return gsRow;
	}
	
	@Override
	public void beginSync() {
		this.spreadSheet.setDirty();
	}

	@Override
	public void endSync() {
		this.spreadSheet.flush();
	}
}
