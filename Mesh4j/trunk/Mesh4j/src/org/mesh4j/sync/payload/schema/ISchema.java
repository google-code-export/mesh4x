package org.mesh4j.sync.payload.schema;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

public interface ISchema {

	public final static String ELEMENT_SCHEMA = "schema";
	public final static HashMap<String, ISchemaTypeFormat> EMPTY_FORMATS = new HashMap<String, ISchemaTypeFormat>();

	public String asXML();

	public Element getInstanceFromXML(Element elementXMLRDF);
	public Element getInstanceFromPlainXML(String id, Element elementPlainXML, Map<String, ISchemaTypeFormat> typeFormats);
	public Element asInstancePlainXML(Element elementXMLRDF, Map<String, ISchemaTypeFormat> typeFormats);

	public String asXMLText();

}
