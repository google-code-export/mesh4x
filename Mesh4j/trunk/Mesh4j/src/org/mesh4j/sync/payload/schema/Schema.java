package org.mesh4j.sync.payload.schema;

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
	public Element getInstanceFromPlainXML(String id, Element element, Map<String, ISchemaTypeFormat> typeFormats) {
		return element;
	}

	@Override
	public Element getInstanceFromXML(Element element) {
		return element;
	}

	@Override
	public String asXMLText() {
		return asXML();
	}
	
}
