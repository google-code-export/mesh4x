package org.mesh4j.sync.adapters.msexcel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
	public final static String SHEET_NAME = "SYNC_INFO";
	
	public final static String COLUMN_NAME_SYNC_ID = "syncId";
	public final static String COLUMN_NAME_ENTITY_NAME = "entityName";
	public final static String COLUMN_NAME_ENTITY_ID = "entityId";
	public final static String COLUMN_NAME_VERSION = "version";
	public final static String COLUMN_NAME_SYNC = "sync";

	// MODEL VARIABLES
	private IIdentityProvider identityProvider;
	private IIdGenerator idGenerator;
	
	private IMsExcel excel;
	
	// BUSINESS METHODS
	public MsExcelSyncRepository(IMsExcel excel, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		super();
		Guard.argumentNotNull(excel, "excel");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		this.excel = excel;
		this.initialize();
	}

	private void initialize() {
		try{
			
			HSSFWorkbook workbook = this.excel.getWorkbook();
			
			HSSFSheet sheet = MsExcelUtils.getOrCreateSheetIfAbsent(workbook, SHEET_NAME);			
			
			HSSFRow row = MsExcelUtils.getOrCreateRowHeaderIfAbsent(sheet);
			
			MsExcelUtils.getOrCreateCellStringIfAbsent(row, COLUMN_NAME_SYNC_ID);
			MsExcelUtils.getOrCreateCellStringIfAbsent(row, COLUMN_NAME_ENTITY_NAME);
			MsExcelUtils.getOrCreateCellStringIfAbsent(row, COLUMN_NAME_ENTITY_ID);
			MsExcelUtils.getOrCreateCellStringIfAbsent(row, COLUMN_NAME_VERSION);
			MsExcelUtils.getOrCreateCellStringIfAbsent(row, COLUMN_NAME_SYNC);
			
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private SyncInfo translate(HSSFRow row) {
		try{
			//String syncId = row.getCell(0).getRichStringCellValue().getString();
			String entityName = row.getCell(1).getRichStringCellValue().getString();
			String entityId = row.getCell(2).getRichStringCellValue().getString();
			int version = Integer.valueOf(row.getCell(3).getRichStringCellValue().getString());
			
			String syncXml = row.getCell(4).getRichStringCellValue().getString();
			Document doc = DocumentHelper.parseText(syncXml);
			Sync sync = SyncInfoParser.convertSyncElement2Sync(doc.getRootElement(), RssSyndicationFormat.INSTANCE, this.identityProvider, this.idGenerator);
			return new SyncInfo(sync, entityName, entityId, version);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private void updateRow(SyncInfo syncInfo, HSSFRow row) {
		MsExcelUtils.updateOrCreateCellStringIfAbsent(row, 0, syncInfo.getSyncId());
		MsExcelUtils.updateOrCreateCellStringIfAbsent(row, 1, syncInfo.getType());
		MsExcelUtils.updateOrCreateCellStringIfAbsent(row, 2, syncInfo.getId());
		MsExcelUtils.updateOrCreateCellStringIfAbsent(row, 3, String.valueOf(syncInfo.getVersion()));
		
		Element syncElement = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
		MsExcelUtils.updateOrCreateCellStringIfAbsent(row, 4, syncElement.asXML());
	}
	
	private void addRow(SyncInfo syncInfo) {
		HSSFRow row = getSheet().createRow(getSheet().getPhysicalNumberOfRows());
		this.updateRow(syncInfo, row);		
	}

	private HSSFSheet getSheet(){
		return this.excel.getWorkbook().getSheet(SHEET_NAME);
	}
	
	public HSSFWorkbook getWorkbook() {
		return this.excel.getWorkbook();
	}

	// ISyncRepository methods
	
	@Override
	public SyncInfo get(String syncId) {
		HSSFRow row = MsExcelUtils.getRow(getSheet(), 0, syncId);
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
		
		HSSFRow row;
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
		HSSFRow row = MsExcelUtils.getRow(getSheet(), 0, syncInfo.getSyncId());
		if(row == null){
			this.addRow(syncInfo);
		} else {
			this.updateRow(syncInfo, row);
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
