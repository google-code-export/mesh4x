package org.mesh4j.sync.payload.schema.rdf;

import org.mesh4j.sync.payload.schema.ISchemaResolver;

import com.hp.hpl.jena.vocabulary.XSD;

public interface IRDFSchema extends ISchemaResolver {

	public final static String XLS_STRING = XSD.xstring.getURI();
	public final static String XLS_INTEGER = XSD.integer.getURI();
	public final static String XLS_BOOLEAN = XSD.xboolean.getURI();
	public final static String XLS_DATETIME = XSD.dateTime.getURI();

	public String asXML();

	public RDFInstance createNewInstance(String id);

	public RDFInstance createNewInstance(String id, String rdfXml);

	public RDFInstance createNewInstance(String id, String plainXML,
			String idColumnName) throws Exception;

	public Object cannonicaliseValue(String propertyName, Object value);
	
	public int getPropertyCount();
	
	public String getPropertyName(int index);

	public String getOntologyNameSpace();
}