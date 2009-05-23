package org.mesh4j.sync.payload.schema;

import java.text.Format;

import org.mesh4j.sync.validations.Guard;

public class SchemaTypeFormat implements ISchemaTypeFormat {

	// MODEL VARIABLEs
	private Format format;
	
	// BUsINESS METHODS
	public SchemaTypeFormat(Format format) {
		Guard.argumentNotNull(format, "format");		
		this.format = format;
	}

	@Override
	public Object format(Object fieldValue) {
		return this.format.format(fieldValue);
	}

	@Override
	public Object parseObject(String fieldValue) throws Exception {
		return this.format.parseObject(fieldValue);
	}

}
