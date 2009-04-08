package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.io.IOException;
import java.util.LinkedHashMap;
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

import com.google.gdata.util.ServiceException;
/**
 * Sync information repository, responsible for storing sync information
 * in google spreadsheet.
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
	
	
	public GoogleSpreadSheetSyncRepository(IGoogleSpreadSheet spreadSheet,GSWorksheet workSheet,
											IIdentityProvider identityProvider,IIdGenerator idGenerator,String sheetName){
		
		Guard.argumentNotNull(spreadSheet, "spreadSheet");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		
		
		this.spreadSheet = spreadSheet;
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		this.workSheet = workSheet;
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
		GSRow<GSCell> row = createSyncRow(syncInfo);
		this.workSheet.addChildElement(row.getElementId(),row);
		//GoogleSpreadsheetUtils.flush(spreadSheet.getService(), spreadSheet.getGSSpreadsheet());
		//row.refreshMe();
	}
	
	@SuppressWarnings("unused")
	private void printTest(GSRow<GSCell> row){
		for(Map.Entry<String, GSCell> celMap :row.getGSCells().entrySet()){
			System.out.println(celMap.getKey());
			GSCell cell = celMap.getValue();
			if(cell != null){
				String value = cell.getCellEntry().getCell().getValue();
				System.out.println("cell value:" + value);
			}
		}
	}
	
	
	private void updateRow(GSRow rowTobeUPdated  ,SyncInfo syncInfo){
		GSRow updatedRow = convertSyncInfoToRow(rowTobeUPdated, syncInfo);
		this.workSheet.updateChildElement(String.valueOf(updatedRow.getRowIndex()), updatedRow);
		//GoogleSpreadsheetUtils.flush(spreadSheet.getService(), spreadSheet.getGSSpreadsheet());
		//updatedRow.refreshMe();
	}
	
	
	/**
	 * reflect the update syncInfo into the provided GSRow
	 * @param rowTobeUPdated
	 * @param syncInfo
	 * @return
	 */
	private GSRow convertSyncInfoToRow(GSRow rowTobeUPdated,SyncInfo syncInfo){
		
		Element syncPayLoad = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
		
		rowTobeUPdated.updateCellValue( syncInfo.getSyncId() ,COLUMN_NAME_SYNC_ID);
		rowTobeUPdated.updateCellValue( syncInfo.getType() ,COLUMN_NAME_ENTITY_NAME);
		rowTobeUPdated.updateCellValue( syncInfo.getId() ,COLUMN_NAME_ENTITY_ID);
		rowTobeUPdated.updateCellValue( String.valueOf(syncInfo.getVersion()) ,COLUMN_NAME_VERSION);
		rowTobeUPdated.updateCellValue(syncPayLoad.asXML()  ,COLUMN_NAME_SYNC);
		
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
		
		listMap.put(COLUMN_NAME_SYNC_ID, syncInfo.getSyncId());
		listMap.put(COLUMN_NAME_ENTITY_NAME, syncInfo.getType());
		listMap.put(COLUMN_NAME_ENTITY_ID, syncInfo.getId());
		listMap.put(COLUMN_NAME_VERSION, String.valueOf(syncInfo.getVersion()));
		listMap.put(COLUMN_NAME_SYNC, payLoad.asXML());
		
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
