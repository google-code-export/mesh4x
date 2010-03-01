package org.mesh4j.sync.adapters.csv;

import java.util.ArrayList;
import java.util.List;

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

public class CSVSyncRepository implements ISyncRepository, ISyncAware, IIdentifiableCSV {

	// CONSTANTS
	public final static String CSV_HEADER = "syncId,entityName,entityId,version,sync";

	// MODEL VARIABLES
	private IIdentityProvider identityProvider;
	private IIdGenerator idGenerator;
	private CSVFile csvFile;
	
	// BUSINESS METHODS
	public CSVSyncRepository(String fileName, IIdentityProvider identityProvider, IIdGenerator idGenerator) {
		super();
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		this.csvFile = new CSVFile(fileName);
	}
	
	@Override
	public SyncInfo get(String syncId) {
		CSVRow row = this.csvFile.getRow(syncId);
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
		
		SyncInfo syncInfo;
		for (CSVRow row : this.csvFile.getRows()) {
			syncInfo = this.translate(row);
			if(syncInfo.getType().equals(type)){
				result.add(syncInfo);
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
			CSVRow row = this.csvFile.getRow(syncInfo.getSyncId());
			if(row == null){
				this.addRow(syncInfo);
			} else {
				this.updateRow(syncInfo, row);
			}
			this.csvFile.setDirty();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public void beginSync() {
		this.csvFile.createFileIfAbsent(CSV_HEADER);
		this.csvFile.read(this);
	}

	@Override
	public void endSync() {
		this.csvFile.flush();
	}

	private SyncInfo translate(CSVRow row) {
		try{
			//String syncId = row.getCell(0);
			String entityName = row.getCellValue(1);
			String entityId = row.getCellValue(2);
			int version = Integer.valueOf(row.getCellValue(3));
			
			String xml = row.getCellValue(4);
			Document doc = DocumentHelper.parseText(xml);
			
			Sync sync = SyncInfoParser.convertSyncElement2Sync(doc.getRootElement(), RssSyndicationFormat.INSTANCE, this.identityProvider, this.idGenerator);
			return new SyncInfo(sync, entityName, entityId, version);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private void updateRow(SyncInfo syncInfo, CSVRow row) throws Exception {
	   	row.setCellValue(0, syncInfo.getSyncId());
	   	row.setCellValue(1, syncInfo.getType());
	   	row.setCellValue(2, syncInfo.getId());
	   	row.setCellValue(3, String.valueOf(syncInfo.getVersion()));
		
		Element syncElement = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
		String xml = syncElement.asXML();
		row.setCellValue(4, xml);
	}
	
	private void addRow(SyncInfo syncInfo) throws Exception {
		CSVRow row = this.csvFile.newRow(syncInfo.getSyncId());
		this.updateRow(syncInfo, row);		
	}

	@Override
	public String getId(CSVFile csvFile, CSVRow row) {
		return row.getCellValue(0);
	}
}
