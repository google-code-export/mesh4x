package org.mesh4j.sync.payload.schema;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SchemaResolver implements ISchemaResolver {

	// MODEL VARIABLES
	private Element schema;

	// BUSINESS METHODS
	
	public SchemaResolver() {
		this(DocumentHelper.createElement(ELEMENT_SCHEMA));
	}

	public SchemaResolver(Element schema) {
		if(schema == null){
			this.schema = DocumentHelper.createElement(ELEMENT_SCHEMA);
		} else {
			this.schema = schema;	
		}
	}

	@Override
	public Element getSchema() {
		return this.schema;
	}
	
}
