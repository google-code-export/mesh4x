package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;
import org.mesh4j.sync.adapters.split.ISyncRepository;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Table;

public class MsAccessSyncRepository implements ISyncRepository, ISyncAware{

	// CONSTANTS
	public final static String COLUMN_NAME_SYNC_ID = "sync_id";
	public final static String COLUMN_NAME_ENTITY_NAME = "entity_name";
	public final static String COLUMN_NAME_ENTITY_ID = "entity_id";
	public final static String COLUMN_NAME_VERSION = "entity_version";
	public final static String COLUMN_NAME_SYNC = "sync_data";

	// MODEL VARIABLES
	private IIdentityProvider identityProvider;
	private IIdGenerator idGenerator;
	
	private IMsAccess msaccess;
	private String tableName;
	
	// BUSINESS METHODS
	public MsAccessSyncRepository(IMsAccess msaccess, String tableName, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		super();
		Guard.argumentNotNull(msaccess, "msaccess");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNullOrEmptyString(tableName, "tableName");
		
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		this.msaccess = msaccess;
		this.tableName = tableName;
		
		MsAccessHelper.createSyncTableIfAbsent(this.msaccess.getFileName(), tableName);
	}

	@Override
	public SyncInfo get(String syncId) {
		try{
			Table table = msaccess.getTable(tableName);
			Map<String, Object> rowPattern = new HashMap<String, Object>();
			rowPattern.put(COLUMN_NAME_SYNC_ID, syncId);
			
			Map<String, Object> row = Cursor.findRow(table, rowPattern);
			if(row != null){
				SyncInfo syncInfo = this.translate(row);
				return syncInfo;
			} else {
				return null;
			}
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public List<SyncInfo> getAll(String type) {
		try{
			ArrayList<SyncInfo> result = new ArrayList<SyncInfo>();
			Table table = msaccess.getTable(tableName);
			Cursor c = Cursor.createCursor(table);
			
			Map<String, Object> row = c.getNextRow();
			while(row != null && type.equals(row.get(COLUMN_NAME_ENTITY_NAME))){
				SyncInfo syncInfo = this.translate(row);
				result.add(syncInfo);
				
				row = c.getNextRow();
			}
			return result;
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public String newSyncID(IContent content) {
		return this.idGenerator.newID();
	}

	@Override
	public void save(SyncInfo syncInfo) {
		try{
			Map<String, Object> rowMap = translate(syncInfo);
			
			Table table = msaccess.getTable(tableName);
			Cursor c = Cursor.createCursor(table);
			Column column = table.getColumn(COLUMN_NAME_SYNC_ID);
			if(c.findRow(column, syncInfo.getSyncId())){
				c.deleteCurrentRow();
				table.addRow(table.asRow(rowMap));
			} else {
				table.addRow(table.asRow(rowMap));
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	// PRIVATE METHODS
	
	public Map<String, Object> translate(SyncInfo syncInfo) {
		LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
		rowMap.put( COLUMN_NAME_SYNC_ID, syncInfo.getSyncId());
		rowMap.put( COLUMN_NAME_ENTITY_NAME, syncInfo.getType());
		rowMap.put( COLUMN_NAME_ENTITY_ID, syncInfo.getId());
		rowMap.put( COLUMN_NAME_VERSION, String.valueOf(syncInfo.getVersion()));
		
		Element syncElement = SyncInfoParser.convertSync2Element(syncInfo.getSync(), RssSyndicationFormat.INSTANCE, this.identityProvider);
		String xml = syncElement.asXML();
		rowMap.put( COLUMN_NAME_SYNC, xml);
		return rowMap;
	}


	private SyncInfo translate(Map<String, Object> row) {
		try{
			String entityName = (String)row.get(COLUMN_NAME_ENTITY_NAME);
			String entityId = (String)row.get(COLUMN_NAME_ENTITY_ID);
			int version = Integer.valueOf((String) row.get(COLUMN_NAME_VERSION));
			
			String xml = (String)row.get(COLUMN_NAME_SYNC);
			Document doc = DocumentHelper.parseText(xml);
			
			Sync sync = SyncInfoParser.convertSyncElement2Sync(doc.getRootElement(), RssSyndicationFormat.INSTANCE, this.identityProvider, this.idGenerator);
			return new SyncInfo(sync, entityName, entityId, version);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public void beginSync() {
		try {
			this.msaccess.open();
		} catch (Exception e) {
			throw new MeshException(e);
		}			
	}


	@Override
	public void endSync() {
		try{
			this.msaccess.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}	
	}
}
