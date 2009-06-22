package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Table;

public class MsAccessContentAdapter implements IIdentifiableContentAdapter, ISyncAware {

	// MODEL VARIABLES
	private IMsAccess msaccess;
	private IMsAccessToXMLMapping mapping;
	
	private String tableName;
	
	// BUSINESS METHODS
	public MsAccessContentAdapter(IMsAccess msaccess, IMsAccessToXMLMapping mapping, String tableName){
		super();
		Guard.argumentNotNull(msaccess, "msaccess");
		Guard.argumentNotNull(mapping, "mapping");
		Guard.argumentNotNullOrEmptyString(tableName, "tableName");
		
		this.tableName = tableName;
		this.msaccess = msaccess;
		this.mapping = mapping;
		
		Table table = this.msaccess.getTable(tableName);
		if(table == null){
			Guard.throwsArgumentException("tableName", tableName);
		}
	}

	// IContentAdapter methods
	
	@Override
	public String getType() {
		return this.tableName;
	}

	@Override
	public void save(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.getType(), this.getIdNode());
		Map<String, Object> rowMap = this.mapping.translateAsRow(entityContent.getPayload());
		
		try{
			Table table = msaccess.getTable(tableName);
			Cursor c = Cursor.createCursor(table);
			Column column = table.getColumn(this.getIdNode());
			
			if(c.findRow(column, rowMap.get(this.getIdNode()))){
				c.deleteCurrentRow();
				table.addRow(table.asRow(rowMap));
			} else {
				table.addRow(table.asRow(rowMap));
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	@Override
	public void delete(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.getType(), this.getIdNode());
		Map<String, Object> rowMap = this.mapping.translateAsRow(entityContent.getPayload());
		try{
			Table table = msaccess.getTable(tableName);
			Cursor c = Cursor.createCursor(table);
			Column column = table.getColumn(this.getIdNode());
			
			if(c.findRow(column, rowMap.get(this.getIdNode()))){
				c.deleteCurrentRow();
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}	
	}

	@Override
	public IContent get(String entityId) {
		try{
			Table table = msaccess.getTable(tableName);
			Map<String, Object> rowPattern = new HashMap<String, Object>();
			rowPattern.put(this.getIdNode(), this.mapping.normalizeAsMsAccessID(entityId));
						
			Map<String, Object> row = Cursor.findRow(table, rowPattern);
			if(row != null){
				Element payload = this.mapping.translateAsElement(row);
				return new EntityContent(payload, this.getType(), this.getIdNode(), entityId);
			} else {
				return null;
			}
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public List<IContent> getAll(Date since) {
		try{
			Element payload;
			IContent entityContent;
			
			ArrayList<IContent> result = new ArrayList<IContent>();
			Table table = msaccess.getTable(tableName);
			Cursor c = Cursor.createCursor(table);
			
			Map<String, Object> row = c.getNextRow();
			while(row != null){
				if(this.hasChanged(row, since)){
					String entityID = this.mapping.getMeshIdValue(row);
					if(entityID != null){
						payload = this.mapping.translateAsElement(row);
						entityContent = new EntityContent(payload, this.getType(), this.getIdNode(), entityID);
						result.add(entityContent);
					}
				}
				row = c.getNextRow();
			}
			return result;
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private boolean hasChanged(Map<String, Object> row, Date since) {
		Date lastUpdate = this.mapping.getLastUpdateColumnValue(row);
		if(lastUpdate == null){
			return true;
		} else {
			return since.compareTo(lastUpdate) <= 0;
		}
	}

	// ISyncAware methods

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

	// IIdentifiableContentAdapter
	@Override
	public ISchema getSchema() {
		return mapping.getSchema();
	}

	public IMsAccessToXMLMapping getMapping(){
		return this.mapping;
	}

	@Override
	public String getIdNode() {
		return this.mapping.getIdColumnName();
	}
	
	@Override
	public String getID(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.getType(), this.getIdNode());
		if(entityContent == null){
			return null;
		} else {
			return entityContent.getId();
		}
	}

}
