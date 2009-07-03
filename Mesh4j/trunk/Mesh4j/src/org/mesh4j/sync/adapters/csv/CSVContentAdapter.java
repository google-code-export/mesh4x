package org.mesh4j.sync.adapters.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;

public class CSVContentAdapter implements IIdentifiableContentAdapter, ISyncAware{
 
	// MODEL VARIABLES
	private ICSVToXMLMapping mapping;
	private CSVFile csvFile;
	
	// BUSINESS METHODS
	
	public CSVContentAdapter(String fileName, ICSVToXMLMapping mapping) {
		super();
		Guard.argumentNotNull(mapping, "mapping");
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		
		this.mapping = mapping;
		this.csvFile = new CSVFile(fileName);
	}

	@Override
	public IContent get(String entityId) {
		CSVRow row = this.csvFile.getRow(entityId);
		if(row == null){
			return null;
		} else {
			Element payload = this.translate(row);
			return new IdentifiableContent(payload, this.mapping, entityId);
		}
	}

	@Override
	public List<IContent> getAll(Date since) {
		ArrayList<IContent> result = new ArrayList<IContent>();
		
		Element payload;
		IContent entityContent;
		for (CSVRow row : this.csvFile.getRows()) {
			if(row != null && this.hasChanged(row, since)){
				String entityID = this.mapping.getId(this.csvFile, row);
				if(entityID != null){
					payload = this.translate(row);
					entityContent = new IdentifiableContent(payload, this.mapping, entityID);
					result.add(entityContent);
				}
			}
		}
		return result;
	}

	@Override
	public void save(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		CSVRow row = this.csvFile.getRow(entityContent.getId());
		if(row == null){
			this.addRow(entityContent);
		} else {
			this.updateRow(row, entityContent);
		}
		this.csvFile.setDirty();
	}
	
	@Override
	public void delete(IContent content) {
		String id = getID(content);
		this.csvFile.remove(id);
		this.csvFile.setDirty();
	}

	@Override
	public void beginSync() {
		this.csvFile.createFileIfAbsent(this.mapping.getHeader(this.csvFile));
		this.csvFile.read(this.mapping);		
	}

	@Override
	public void endSync() {
		this.csvFile.flush();		
	}

	@Override
	public String getType() {
		return this.mapping.getType();
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

	@Override
	public ISchema getSchema() {
		return mapping.getSchema();
	}

	private boolean hasChanged(CSVRow row, Date since) {
		Date lastUpdate = this.mapping.getLastUpdate(this.csvFile, row);
		if(lastUpdate == null){
			return true;
		} else {
			return since.compareTo(lastUpdate) <= 0;
		}
	}
	
	protected Element translate(CSVRow row) {
		return this.mapping.convertRowToXML(this.csvFile, row);
	}
	
	protected void updateRow(CSVRow row, IdentifiableContent entityContent) {
		this.mapping.appliesXMLToRow(this.csvFile, row, entityContent.getPayload());
	}

	private void addRow(IdentifiableContent entityContent) {
		CSVRow row = this.csvFile.newRow(entityContent.getId());
		this.updateRow(row, entityContent);		
	}

	public CSVFile getCSVFile() {
		return this.csvFile;
	}

	public ICSVToXMLMapping getMapping() {
		return this.mapping;
	}
}
