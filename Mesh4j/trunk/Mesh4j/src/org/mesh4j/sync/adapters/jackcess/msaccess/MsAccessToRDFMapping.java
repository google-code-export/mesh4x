package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
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
		String[] idValues = new String[idNames.size()];
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		int size = this.rdfSchema.getPropertyCount();
		int idAdded = 0;
		for (int i = 0; i < size; i++) {
			String propertyName = this.rdfSchema.getPropertyName(i);
			Object propertyValue = getValue(row, propertyName);
			
			if(propertyValue != null){
				if(idNames.contains(propertyName)){
					int idx = idNames.indexOf(propertyName);
					String id = String.valueOf(propertyValue);
					if(this.rdfSchema.isGUID(propertyName)){
						if(id.startsWith("{") && id.endsWith("}")){
							id = id.substring(1, id.length() -1);
						}
						id= id.toLowerCase();
					}
					idValues[idx] = id;	
					properties.put(propertyName, id);
					idAdded++;
				} else {
					properties.put(propertyName, propertyValue);
				}
			}
		}

		if(idNames.size() != idAdded){
			return null;
		}
		
		String id = makeId(Arrays.asList(idValues));	
				
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromProperties(id, properties);
		return rdfInstance.asElementRDFXML();
	}

	@Override
	public Map<String, Object> translateAsRow(Element payload) {
		Map<String, Object> properties = this.rdfSchema.getPropertiesAsMap(payload);
		HashMap<String, Object> row = new HashMap<String, Object>();
		
		List<String> idNames = this.rdfSchema.getIdentifiablePropertyNames();
		
		for (String propertyName : properties.keySet()) {
			String label = this.rdfSchema.getPropertyLabel(propertyName);
			Object propertyValue = properties.get(propertyName);
			
			if(idNames.contains(propertyName)){
				String id = String.valueOf(propertyValue);
				if(this.rdfSchema.isGUID(propertyName)){
					if(!id.startsWith("{")){
						id = "{"+id;
					}
					if(!id.endsWith("}")){
						id = id + "}";
					}
					id=id.toUpperCase();
				}
				row.put(label, id);	
			} else {
				row.put(label, propertyValue);
			}
		}
		
		return row;
	}
	
	@Override
	public Date getLastUpdate(Map<String, Object> row) {
		if(this.rdfSchema.getVersionPropertyName() == null){
			return null;
		} else {
			Object propertyValue = getValue(row, this.rdfSchema.getVersionPropertyName());
			return (Date)propertyValue;
		}
	}

	@Override
	public String getId(Map<String, Object> row) {
		List<String> idNames = this.rdfSchema.getIdentifiablePropertyNames();
		List<String> idValues = new ArrayList<String>();
		
		for (String idName : idNames) {
			Object idValue = getValue(row, idName);
			if(idValue == null){
				return null;
			} else {
				String id = String.valueOf(idValue);
				if(this.rdfSchema.isGUID(idName)){
					if(id.startsWith("{") && id.endsWith("}")){
						id = id.substring(1, id.length() -1);
					}
					id=id.toLowerCase();
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
					idValue = idValue.toUpperCase();
				}
				
				String label = this.rdfSchema.getPropertyLabel(idName);
				rowPattern.put(label, idValue);			
			}
			return cursor.findRow(rowPattern);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private Object getValue(Map<String, Object> row, String propertyName) {
		Object propertyValue = row.get(propertyName);
		if(propertyValue == null){
			String label = this.rdfSchema.getPropertyLabel(propertyName);
			propertyValue = row.get(label);
		}
		return propertyValue;
	}
}
