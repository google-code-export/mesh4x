package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

import com.healthmarketscience.jackcess.Cursor;

public class MsAccessToRDFMapping extends AbstractRDFIdentifiableMapping implements IMsAccessToXMLMapping {

	// BUSINESS METHODs
	public MsAccessToRDFMapping(IRDFSchema rdfSchema) {
		super(rdfSchema);
	}

	@Override
	public Element translateAsElement(Map<String, Object> row) {		
		List<String> idNames = this.rdfSchema.getIdentifiablePropertyNames();
		List<String> idValues = new ArrayList<String>();
		String idValue;
		for (String idName : idNames) {
			idValue = String.valueOf(row.get(idName));
			if(idValue == null){
				return null;
			} else {
				String id = idValue;
				if(this.rdfSchema.isGUID(idName)){
					if(id.startsWith("{") && id.endsWith("}")){
						id = id.substring(1, id.length() -1);
					}
				}
				idValues.add(id);
				row.put(idName, id);
			}
		}
		String id = makeId(idValues);	
				
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromProperties(id, row);
		return XMLHelper.parseElement(rdfInstance.asXML());
	}

	@Override
	public Map<String, Object> translateAsRow(Element payload) {
		Map<String, Object> row = this.rdfSchema.getPropertiesAsMap(payload);
		List<String> idNames = this.rdfSchema.getIdentifiablePropertyNames();
		for (String idName : idNames) {
			String idValue = String.valueOf(row.get(idName));
			String id = idValue;
			if(this.rdfSchema.isGUID(idName)){
				if(!id.startsWith("{")){
					id = "{"+id;
				}
				if(!id.endsWith("}")){
					id = id + "}";
				}
			}
			row.put(idName, id);
		}
		return row;
	}
	
	@Override
	public Date getLastUpdate(Map<String, Object> row) {
		if(this.rdfSchema.getVersionPropertyName() == null){
			return null;
		} else {
			return (Date)row.get(this.rdfSchema.getVersionPropertyName());
		}
	}

	@Override
	public String getId(Map<String, Object> row) {
		List<String> idNames = this.rdfSchema.getIdentifiablePropertyNames();
		List<String> idValues = new ArrayList<String>();
		String idValue;
		for (String idName : idNames) {
			idValue = String.valueOf(row.get(idName));
			if(idValue == null){
				return null;
			} else {
				String id = idValue;
				if(this.rdfSchema.isGUID(idName)){
					if(id.startsWith("{") && id.endsWith("}")){
						id = id.substring(1, id.length() -1);
					}
				}
				idValues.add(id);
			}
		}
		return makeId(idValues);	
	}

	@Override
	public boolean findRow(Cursor cursor, String meshid) {
		try{
			HashMap<String, Object> rowPattern = new HashMap<String, Object>();
			
			String[] idValues = this.getIds(meshid);
			List<String> idNames = this.rdfSchema.getIdentifiablePropertyNames();
			for (int i = 0; i < idNames.size(); i++) {
				String idName = idNames.get(i);
				String idValue = idValues[i];
				
				if(this.rdfSchema.isGUID(idName)){
					if(!idValue.startsWith("{")){
						idValue = "{"+idValue;
					}
					if(!idValue.endsWith("}")){
						idValue = idValue + "}";
					}
				}
				rowPattern.put(idName, idValue);			
			}
			return cursor.findRow(rowPattern);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
