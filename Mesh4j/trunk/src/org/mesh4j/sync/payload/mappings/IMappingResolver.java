package org.mesh4j.sync.payload.mappings;

import org.dom4j.Element;

public interface IMappingResolver {

	public final static String ELEMENT_MAPPING = "mappings";
	
	String getValue(Element element, String mappingName);

	Element getMappings();
}
