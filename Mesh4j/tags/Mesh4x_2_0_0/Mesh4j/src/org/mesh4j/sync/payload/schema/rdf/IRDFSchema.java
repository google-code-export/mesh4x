package org.mesh4j.sync.payload.schema.rdf;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;

import com.hp.hpl.jena.vocabulary.XSD;

public interface IRDFSchema extends ISchema {

	public static final Namespace NS_RDF = DocumentHelper.createNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	public static final String ELEMENT_RDF = "RDF";
	public static final QName QNAME_RDF = DocumentHelper.createQName("RDF", NS_RDF);
	
	public static final String DEFAULT_LANGUAGE = "en";
	
	public static final String XLS_STRING = XSD.xstring.getURI();
	public static final String XLS_INTEGER = XSD.integer.getURI();
	public static final String XLS_BOOLEAN = XSD.xboolean.getURI();
	public static final String XLS_DATETIME = XSD.dateTime.getURI();
	public static final String XLS_DOUBLE = XSD.xdouble.getURI();
	public static final String XLS_LONG = XSD.xlong.getURI();
	public static final String XLS_DECIMAL = XSD.decimal.getURI();
	public static final String XLS_FLOAT = XSD.xfloat.getURI();

	public RDFInstance createNewInstance(String id);

	public RDFInstance createNewInstanceFromRDFXML(Element rdfXml);	
	public RDFInstance createNewInstanceFromPlainXML(String id, Element plainXML, Map<String, ISchemaTypeFormat> formatters) throws ParseException;
	public RDFInstance createNewInstanceFromPlainXML(String id, Element plainXML, Map<String, ISchemaTypeFormat> formatters, String[] splitElements) throws ParseException;

	public RDFInstance createNewInstanceFromProperties(String id, Map<String, Object> propertyValues);

	public Object cannonicaliseValue(String propertyName, Object value);
	
	public int getPropertyCount();
	
	public String getPropertyName(int index);
	public String getPropertyType(String propertyName);
	public String getPropertyLabel(String propertyName);
	public String getPropertyLabel(String propertyName, String lang);
	
	public String getOntologyBaseRDFUrl();
	public String getOntologyNameSpace();
	public String getOntologyBaseClassUri();
	public String getOntologyClassName();


	public boolean hasCompositeId();
	List<String> getIdentifiablePropertyNames();

	String getVersionPropertyName();

	public boolean isGUID(String propertyName);

}