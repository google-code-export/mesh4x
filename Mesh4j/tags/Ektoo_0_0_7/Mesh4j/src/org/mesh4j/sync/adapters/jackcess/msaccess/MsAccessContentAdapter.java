package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Table;

public class MsAccessContentAdapter implements IIdentifiableContentAdapter, ISyncAware {

	// MODEL VARIABLES
	private IMsAccess msaccess;
	private IMsAccessToXMLMapping mapping;
	
	// BUSINESS METHODS
	public MsAccessContentAdapter(IMsAccess msaccess, IMsAccessToXMLMapping mapping){
		super();
		Guard.argumentNotNull(msaccess, "msaccess");
		Guard.argumentNotNull(mapping, "mapping");

		this.msaccess = msaccess;
		this.mapping = mapping;
		
		Table table = this.msaccess.getTable(mapping.getType());
		if(table == null){
			Guard.throwsArgumentException("tableName", mapping.getType());
		}
	}

	// IContentAdapter methods
	
	@Override
	public String getType() {
		return mapping.getType();
	}

	@Override
	public void save(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		try{
			Map<String, Object> rowMap = this.mapping.translateAsRow(entityContent.getPayload());
			
			Table table = getTable();
			Cursor c = Cursor.createCursor(table);
			
			if(this.mapping.findRow(c, entityContent.getId())){
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
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		try{
			Table table = getTable();
			Cursor c = Cursor.createCursor(table);
			if(this.mapping.findRow(c, entityContent.getId())){
				c.deleteCurrentRow();
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}	
	}

	@Override
	public IContent get(String entityId) {
		try{
			Table table = getTable();
			Cursor c = Cursor.createCursor(table);
			if(this.mapping.findRow(c, entityId)){
				Map<String, Object> row = c.getCurrentRow();
				Element payload = this.mapping.translateAsElement(row);
				return new IdentifiableContent(payload, this.mapping, entityId);
			} else {
				return null;
			}
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private Table getTable() {
		return msaccess.getTable(this.getType());
	}

	@Override
	public List<IContent> getAll(Date since) {
		try{
			Element payload;
			IContent entityContent;
			
			ArrayList<IContent> result = new ArrayList<IContent>();
			Table table = getTable();
			Cursor c = Cursor.createCursor(table);
			
			Map<String, Object> row = c.getNextRow();
			while(row != null){
				if(this.hasChanged(row, since)){
					String entityID = this.mapping.getId(row);
					if(entityID != null){
						payload = this.mapping.translateAsElement(row);
						entityContent = new IdentifiableContent(payload, this.mapping, entityID);
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
		Date lastUpdate = this.mapping.getLastUpdate(row);
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
	public String getID(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		if(entityContent == null){
			return null;
		} else {
			return entityContent.getId();
		}
	}

}
