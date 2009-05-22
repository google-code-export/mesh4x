package org.mesh4j.sync.adapters.msexcel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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

public class MsExcelSyncRepository implements ISyncRepository, ISyncAware {
	
	// CONSTANTS
	public final static String COLUMN_NAME_SYNC_ID = "syncId";
	public final static String COLUMN_NAME_ENTITY_NAME = "entityName";
	public final static String COLUMN_NAME_ENTITY_ID = "entityId";
	public final static String COLUMN_NAME_VERSION = "version";
	public final static String COLUMN_NAME_SYNC = "sync";

	// MODEL VARIABLES
	private IIdentityProvider identityProvider;
	private IIdGenerator idGenerator;
	
	private IMsExcel excel;
	private String sheetName;
	
	// BUSINESS METHODS
	public MsExcelSyncRepository(IMsExcel excel, String sheetName, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		super();
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		this.excel = excel;
		this.sheetName = sheetName;
		
		this.initialize();
	}

	private void initialize() {
		try{
			
			Workbook workbook = this.excel.getWorkbook();
			
			Sheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, this.sheetName);			
			
			Row row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
			
			MsExcelUtils.getOrCreateCellStringIfAbsent(workbook, row, COLUMN_NAME_SYNC_ID);
			MsExcelUtils.getOrCreateCellStringIfAbsent(workbook, row, COLUMN_NAME_ENTITY_NAME);
			MsExcelUtils.getOrCreateCellStringIfAbsent(workbook, row, COLUMN_NAME_ENTITY_ID);
			MsExcelUtils.getOrCreateCellStringIfAbsent(workbook, row, COLUMN_NAME_VERSION);
			MsExcelUtils.getOrCreateCellStringIfAbsent(workbook, row, COLUMN_NAME_SYNC);
			
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private SyncInfo translate(Row row) {
		try{
			//String syncId = row.getCell(0).getRichStringCellValue().getString();
			String entityName = row.getCell(1).getRichStringCellValue().getString();
			String entityId = row.getCell(2).getRichStringCellValue().getString();
			int version = Integer.valueOf(row.getCell(3).getRichStringCellValue().getString());
			
			String xml = row.getCell(4).getRichStringCellValue().getString();
			Document doc = DocumentHelper.parseText(xml);
			
			Sync sync = SyncInfoParser.convertSyncElement2Sync(doc.getRootElement(), RssSyndicationFormat.INSTANCE, this.identityProvider, this.idGenerator);
			return new SyncInfo(sync, entityName, entityId, version);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private void updateRow(SyncInfo syncInfo, Row row) throws Exception {
	   	Workbook workbook = getWorkbook();
		MsExcelUtils.updateOrCreateCellStringIfAbsent(workbook, row, 0, syncInfo.getSyncId());
		MsExcelUtils.updateOrCreateCellStringIfAbsent(workbook, row, 1, syncInfo.getType());
		MsExcelUtils.updateOrCreateCellStringIfAbsent(workbook, row, 2, syncInfo.getId());
		MsExcelUtils.updateOrCreateCellStringIfAbsent(workbook, row, 3, String.valueOf(syncInfo.getVersion()));
		
		Element syncElement = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
		String xml = syncElement.asXML();
		MsExcelUtils.updateOrCreateCellStringIfAbsent(workbook, row, 4, xml);
	}
	
	private void addRow(SyncInfo syncInfo) throws Exception {
		Row row = getSheet().createRow(getSheet().getPhysicalNumberOfRows());
		this.updateRow(syncInfo, row);		
	}

	private Sheet getSheet(){
		return this.excel.getWorkbook().getSheet(this.sheetName);
	}
	
	public Workbook getWorkbook() {
		return this.excel.getWorkbook();
	}

	// ISyncRepository methods
	
	@Override
	public SyncInfo get(String syncId) {
		Row row = MsExcelUtils.getRow(getSheet(), 0, syncId);
		if(row == null){
			return null;
		} else {
			SyncInfo syncInfo = this.translate(row);
			return syncInfo;
		}
	}
	
	@Override
	public List<SyncInfo> getAll(String type) {
		ArrayList<SyncInfo> result = new ArrayList<SyncInfo>();
		
		Row row;
		SyncInfo syncInfo;
		for (int i = getSheet().getFirstRowNum()+1; i <= getSheet().getLastRowNum(); i++) {
			row = getSheet().getRow(i);
			if(row != null){
				syncInfo = this.translate(row);
				if(syncInfo.getType().equals(type)){
					result.add(syncInfo);
				}
			}
		}
		return result;
	}

	@Override
	public String newSyncID(IContent content) {
		return this.idGenerator.newID();
	}

	@Override
	public void save(SyncInfo syncInfo) {
		try{
			Row row = MsExcelUtils.getRow(getSheet(), 0, syncInfo.getSyncId());
			if(row == null){
				this.addRow(syncInfo);
			} else {
				this.updateRow(syncInfo, row);
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	// ISyncAware methods
	
	@Override
	public void beginSync() {
		this.excel.setDirty();		
	}

	@Override
	public void endSync() {
		this.excel.flush();
	}
}
