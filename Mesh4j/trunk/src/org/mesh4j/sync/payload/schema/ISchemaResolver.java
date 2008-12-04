package org.mesh4j.sync.payload.schema;

import org.dom4j.Element;

public interface ISchemaResolver {

	public final static String ELEMENT_SCHEMA = "schema";
	public final static String ELEMENT_PAYLOAD = "payload";
	
	String getValue(Element element, String propertyName);

	Element getSchema();

}
