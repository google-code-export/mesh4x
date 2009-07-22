package org.mesh4j.sync.payload.schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.validations.Guard;

public class Schema implements ISchema {

	// MODEL VARIABLES
	private Element schema;

	// BUSINESS METHODS
	
	public Schema(Element schema) {
		Guard.argumentNotNull(schema, "schema");
		this.schema = schema;	
	}

	@Override
	public String asXML() {
		return this.schema.asXML();
	}
	
	@Override
	public Element asInstancePlainXML(Element element, Map<String, ISchemaTypeFormat> typeFormats) {
		return element;
	}
	
	@Override
	public Element asInstanceXML(Element element, HashMap<String, ISchemaTypeFormat> typeFormats) {
		return element;
	}

	@Override
	public Element getInstanceFromPlainXML(String id, Element element, Map<String, ISchemaTypeFormat> typeFormats) {
		return element;
	}

	@Override
	public Element getInstanceFromXML(Element element) {
		return element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getPropertiesAsLexicalFormMap(Element element){
		HashMap<String, String> result = new HashMap<String, String>();
		
		List<Element> elements = element.elements();
		for (Element ele : elements) {
			String propertyName = ele.getName();
			String propertyValue = ele.getText();
			result.put(propertyName, propertyValue);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getPropertiesAsMap(Element element){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		List<Element> elements = element.elements();
		for (Element ele : elements) {
			String propertyName = ele.getName();
			String propertyValue = ele.getText();
			result.put(propertyName, propertyValue);
		}
		return result;
	}
	
	@Override
	public boolean isCompatible(ISchema schema){		
		if (this == schema) return true;		
		if (schema == null || !(schema instanceof Schema)) return false;
		if (this.asXML().equalsIgnoreCase(schema.asXML())) return true;
		return false;
	}

	@Override
	public String getName() {
		return this.schema.getName();
	}

}
