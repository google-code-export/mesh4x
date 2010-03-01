package org.mesh4j.sync.payload.schema.rdf;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

public class RDFInstanceTestWithSchemaMapper {

	private static Date DATE = TestHelper.makeDate(2009, 4, 1, 1, 1, 1, 1);
	private static SchemaMappedRDFSchema RDF_SCHEMA;
	private static RDFInstance RDF_INSTANCE;
	private static String RDF_XML;
	private static Element RDF_XML_ELEMENT;
	private static String RDF_XML_2;
	private static Element RDF_XML_ELEMENT_2;
	private static String RDF_XML_LESS_FIELD;
	private static Element RDF_XML_ELEMENT_LESS_FIELD;
	private static String PLAIN_XML;
	private static Element PLAIN_XML_ELEMENT;
	
	private static Map<String, Resource> SYNC_SCHEMA = new HashMap<String, Resource>();
	private static Map<String, String> SCHEMA_CONVERT_MAP = new HashMap<String, String>();
	{
		SYNC_SCHEMA.put("string_x", XSD.xstring);
		SYNC_SCHEMA.put("integer_x", XSD.integer);
		SYNC_SCHEMA.put("double_x", XSD.xdouble);
		SYNC_SCHEMA.put("example_x", XSD.ENTITY);
		
		SCHEMA_CONVERT_MAP.put("example","example_x");
		SCHEMA_CONVERT_MAP.put("string","string_x");
		SCHEMA_CONVERT_MAP.put("integer","integer_x");
		SCHEMA_CONVERT_MAP.put("double","double_x");
		
		RDF_SCHEMA = new SchemaMappedRDFSchema("example", "http://mesh4x/example#", "example", SYNC_SCHEMA, SCHEMA_CONVERT_MAP);
		RDF_SCHEMA.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.setIdentifiablePropertyName("string");
        
        RDF_INSTANCE = new RDFInstance(RDF_SCHEMA, "uri:urn:1");
        RDF_INSTANCE.setProperty("string", "abc");
        RDF_INSTANCE.setProperty("integer", Integer.MAX_VALUE);
        RDF_INSTANCE.setProperty("double", Double.MAX_VALUE);
        
        RDF_XML = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example_x=\"http://mesh4x/example_x#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example_x:example_x rdf:about=\"uri:urn:1\"><example_x:string_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example_x:string_x><example_x:integer_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example_x:integer_x><example_x:double_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example_x:double_x></example_x:example_x></rdf:RDF>";
        RDF_XML_ELEMENT = XMLHelper.parseElement(RDF_XML);
        
        RDF_XML_2 = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example_x=\"http://mesh4x/example_x#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example_x:example_x rdf:about=\"uri:urn:1\"><example_x:string_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example_x:string_x><example_x:integer_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example_x:integer_x><example_x:double_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1.7976931348623157E308</example_x:double_x></example_x:example_x></rdf:RDF>";
        RDF_XML_ELEMENT_2 = XMLHelper.parseElement(RDF_XML_2);
        
        RDF_XML_LESS_FIELD = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example_x=\"http://mesh4x/example_x#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example_x:example_x rdf:about=\"uri:urn:1\"><example_x:string_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example_x:string_x><example_x:integer_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example_x:integer_x></example_x:example_x></rdf:RDF>";
        RDF_XML_ELEMENT_LESS_FIELD = XMLHelper.parseElement(RDF_XML_LESS_FIELD);
        
        PLAIN_XML = "<example><integer>2147483647</integer><string>abc</string><double>1.7976931348623157E308</double></example>";
        PLAIN_XML_ELEMENT = XMLHelper.parseElement(PLAIN_XML);
    }

	
	final String rdfXml_full_revert			 = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example:example rdf:about=\"uri:urn:abc\"><example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string><example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer><example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double></example:example></rdf:RDF>";
	final String rdfXml_less_field_revert 	 = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example:example rdf:about=\"uri:urn:abc\"><example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string><example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer></example:example></rdf:RDF>";
	
	
	@Test
	//revert-save
	public void shouldBuildInstanceFromRDFXml(){
		//String rdfXml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example:example rdf:about=\"uri:urn:abc\"><example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string><example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer><example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double></example:example></rdf:RDF>";
		String rdfXml = XMLHelper.canonicalizeXML(XMLHelper.parseElement(rdfXml_full_revert));
		Assert.assertEquals(rdfXml, RDFInstance.buildFromRDFXml(RDF_SCHEMA, RDF_XML_ELEMENT).asRDFXML());
	}
	
	@Test
	//revert-save
	public void shouldBuildInstanceFromRDFXmlWithLessField(){
		SYNC_SCHEMA.clear();
		SYNC_SCHEMA.put("string_x", XSD.xstring);
		SYNC_SCHEMA.put("integer_x", XSD.integer);
		SYNC_SCHEMA.put("example_x", XSD.ENTITY);
		
		SCHEMA_CONVERT_MAP.clear();
		SCHEMA_CONVERT_MAP.put("example","example_x");
		SCHEMA_CONVERT_MAP.put("string","string_x");
		SCHEMA_CONVERT_MAP.put("integer","integer_x");
		
		SchemaMappedRDFSchema RDF_SCHEMA = new SchemaMappedRDFSchema("example", "http://mesh4x/example#", "example", SYNC_SCHEMA, SCHEMA_CONVERT_MAP);
		RDF_SCHEMA.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.setIdentifiablePropertyName("string");
		
		String rdfXml = XMLHelper.canonicalizeXML(XMLHelper.parseElement(rdfXml_less_field_revert));
		Assert.assertEquals(rdfXml, RDFInstance.buildFromRDFXml(RDF_SCHEMA, RDF_XML_ELEMENT).asRDFXML());
		Assert.assertEquals(rdfXml, RDFInstance.buildFromRDFXml(RDF_SCHEMA, RDF_XML_ELEMENT_LESS_FIELD).asRDFXML());
	}
	
	@Test
	//revert-save
	public void shouldBuildInstanceFromRDFXmlWithDifferentTypeField(){
		SYNC_SCHEMA.clear();
		SYNC_SCHEMA.put("string_x", XSD.xstring);
		SYNC_SCHEMA.put("integer_x", XSD.xlong);	//source schema integer, sync schema long
		SYNC_SCHEMA.put("double_x", XSD.xstring);	//source schema double, sync schema string
		SYNC_SCHEMA.put("example_x", XSD.ENTITY);
		
		SCHEMA_CONVERT_MAP.clear();
		SCHEMA_CONVERT_MAP.put("example","example_x");
		SCHEMA_CONVERT_MAP.put("string","string_x");
		SCHEMA_CONVERT_MAP.put("integer","integer_x");
		SCHEMA_CONVERT_MAP.put("double","double_x");
		
		SchemaMappedRDFSchema RDF_SCHEMA = new SchemaMappedRDFSchema("example", "http://mesh4x/example#", "example", SYNC_SCHEMA, SCHEMA_CONVERT_MAP);
		RDF_SCHEMA.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.setIdentifiablePropertyName("string");
		
		String rdfXml = XMLHelper.canonicalizeXML(XMLHelper.parseElement(rdfXml_full_revert));
		Assert.assertEquals(rdfXml, RDFInstance.buildFromRDFXml(RDF_SCHEMA, RDF_XML_ELEMENT_2).asRDFXML());
	}

	final String rdfXml_full 				 = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example_x=\"http://mesh4x/example_x#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example_x:example_x rdf:about=\"uri:urn:abc\"><example_x:string_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example_x:string_x><example_x:integer_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example_x:integer_x><example_x:double_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example_x:double_x></example_x:example_x></rdf:RDF>";
	final String rdfXml_less_field 			 = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example_x=\"http://mesh4x/example_x#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example_x:example_x rdf:about=\"uri:urn:abc\"><example_x:string_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example_x:string_x><example_x:integer_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example_x:integer_x></example_x:example_x></rdf:RDF>";
	//double field as string
	final String rdfXml_different_type_field = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example_x=\"http://mesh4x/example_x#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example_x:example_x rdf:about=\"uri:urn:abc\"><example_x:string_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example_x:string_x><example_x:integer_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">2147483647</example_x:integer_x><example_x:double_x rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1.7976931348623157E308</example_x:double_x></example_x:example_x></rdf:RDF>";

	@Test
	//load-convert
	public void shouldBuildInstanceFromPlainXml(){
		String rdfXml = XMLHelper.canonicalizeXML(XMLHelper.parseElement(rdfXml_full));
		Assert.assertEquals(rdfXml, RDFInstance.buildFromPlainXML(RDF_SCHEMA, "abc", PLAIN_XML_ELEMENT, ISchema.EMPTY_FORMATS, null).asRDFXML());
	}
	
	@Test
	//load-convert
	public void shouldBuildInstanceFromPlainXmlWithLessField(){
		SYNC_SCHEMA.clear();
		SYNC_SCHEMA.put("string_x", XSD.xstring);
		SYNC_SCHEMA.put("integer_x", XSD.integer);
		SYNC_SCHEMA.put("example_x", XSD.ENTITY);
		
		SCHEMA_CONVERT_MAP.clear();
		SCHEMA_CONVERT_MAP.put("example","example_x");
		SCHEMA_CONVERT_MAP.put("string","string_x");
		SCHEMA_CONVERT_MAP.put("integer","integer_x");
		
		SchemaMappedRDFSchema RDF_SCHEMA = new SchemaMappedRDFSchema("example", "http://mesh4x/example#", "example", SYNC_SCHEMA, SCHEMA_CONVERT_MAP);
		RDF_SCHEMA.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.setIdentifiablePropertyName("string");
        
		String rdfXml = XMLHelper.canonicalizeXML(XMLHelper.parseElement(rdfXml_less_field));
		Assert.assertEquals(rdfXml, RDFInstance.buildFromPlainXML(RDF_SCHEMA, "abc", PLAIN_XML_ELEMENT, ISchema.EMPTY_FORMATS, null).asRDFXML());
	}
	
	@Test
	//load-convert
	public void shouldBuildInstanceFromPlainXmlWithDifferentTypeField(){
		SYNC_SCHEMA.clear();
		SYNC_SCHEMA.put("string_x", XSD.xstring);
		SYNC_SCHEMA.put("integer_x", XSD.xlong);	//source schema integer, sync schema long
		SYNC_SCHEMA.put("double_x", XSD.xstring);	//source schema double, sync schema string
		SYNC_SCHEMA.put("example_x", XSD.ENTITY);
		
		SCHEMA_CONVERT_MAP.clear();
		SCHEMA_CONVERT_MAP.put("example","example_x");
		SCHEMA_CONVERT_MAP.put("string","string_x");
		SCHEMA_CONVERT_MAP.put("integer","integer_x");
		SCHEMA_CONVERT_MAP.put("double","double_x");
		
		SchemaMappedRDFSchema RDF_SCHEMA = new SchemaMappedRDFSchema("example", "http://mesh4x/example#", "example", SYNC_SCHEMA, SCHEMA_CONVERT_MAP);
		RDF_SCHEMA.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.setIdentifiablePropertyName("string");
		
		String rdfXml = XMLHelper.canonicalizeXML(XMLHelper.parseElement(rdfXml_different_type_field));
		Assert.assertEquals(rdfXml, RDFInstance.buildFromPlainXML(RDF_SCHEMA, "abc", PLAIN_XML_ELEMENT, ISchema.EMPTY_FORMATS, null).asRDFXML());
	}
}
