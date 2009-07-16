package org.mesh4j.sync.payload.mappings;

import org.dom4j.Element;

public interface IMapping {

	public final static String ELEMENT_MAPPING = "mappings";
	
	String getValue(Element element, String mappingName);

	String getMapping(String mappingName);

	String asXML();
	
	public String asXMLText();

}
