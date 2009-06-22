package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.util.Date;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.utils.XMLHelper;

public class MsAccessToRDFMapping implements IMsAccessToXMLMapping {

	// MODEL VARIABLES
	private IRDFSchema rdfSchema;

	private String idColumnName;
	private String lastUpdateColumnName = null;
	private boolean guid;
	
	// BUSINESS METHODs
	public MsAccessToRDFMapping(IRDFSchema schema, String idColumnName, boolean isGuid) {
		super();
		this.rdfSchema = schema;
		this.idColumnName = idColumnName;
		this.guid = isGuid;
	}

	@Override
	public String getIdColumnName() {
		return this.idColumnName;
	}

	@Override
	public String getLastUpdateColumnName() {
		return this.lastUpdateColumnName;
	}
	
	public void setLastUpdateColumnName(String columnName) {
		this.lastUpdateColumnName = columnName;
	}

	@Override
	public IRDFSchema getSchema() {
		return rdfSchema;
	}

	@Override
	public Element translateAsElement(Map<String, Object> row) {		
		String id = getMeshIdValue(row);
		row.put(this.getIdColumnName(), id);
				
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromProperties(id, row);
		return XMLHelper.parseElement(rdfInstance.asXML());
	}

	@Override
	public Map<String, Object> translateAsRow(Element payload) {
		Map<String, Object> row = this.rdfSchema.getPropertiesAsMap(payload);
		String id = String.valueOf(rdfSchema.cannonicaliseValue(this.getIdColumnName(), row.get(this.getIdColumnName())));
		id = this.normalizeAsMsAccessID(id);
		row.put(this.getIdColumnName(), id);		
		return row;
	}
	
	@Override
	public Date getLastUpdateColumnValue(Map<String, Object> row) {
		if(this.getLastUpdateColumnName() == null){
			return null;
		} else {
			return (Date)row.get(this.getLastUpdateColumnName());
		}
	}

	@Override
	public String normalizeAsMeshID(String msAccessId) {
		String id = msAccessId;
		if(this.guid){
			if(id.startsWith("{") && id.endsWith("}")){
				id = id.substring(1, id.length() -1);
			}
		}
		return id;
	}

	@Override
	public String normalizeAsMsAccessID(String meshID) {
		String id = meshID;
		if(this.guid){
			if(!meshID.startsWith("{")){
				id = "{"+id;
			}
			if(!meshID.endsWith("}")){
				id = id + "}";
			}
		} 
		return id;
	}
	
	@Override
	public String getMeshIdValue(Map<String, Object> row) {
		String id = String.valueOf(rdfSchema.cannonicaliseValue(this.getIdColumnName(), row.get(this.getIdColumnName())));
		return normalizeAsMeshID(id);
	}
}
