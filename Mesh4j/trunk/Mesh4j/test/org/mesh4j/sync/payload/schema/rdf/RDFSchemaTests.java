package org.mesh4j.sync.payload.schema.rdf;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;


public class RDFSchemaTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSchemaFromReaderFailsIfReaderIsNull(){
		new RDFSchema(null);
	}
	
	@Test(expected=MeshException.class)
	public void shouldCreateSchemaFromReaderFailsIfXMLIsNotValidXML(){
		Reader reader = new StringReader("abc");
		new RDFSchema(reader);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSchemaFromReaderFailsIfXMLIsInvalidRDF(){
		Reader reader = new StringReader("<foo>bar</foo>");
		new RDFSchema(reader);
	}
	
	@Test
	public void shouldCreateSchemaFromReader() throws FileNotFoundException{
		Reader reader = new FileReader(this.getClass().getResource("oswego.owl").getFile());
		RDFSchema schema = new RDFSchema(reader);
		Assert.assertNotNull(schema);
		
		Assert.assertEquals(6, schema.getPropertyCount());
		
		Assert.assertEquals(IRDFSchema.XLS_STRING, schema.getPropertyType("Code"));
		Assert.assertEquals("Code", schema.getPropertyLabel("Code", "en"));

		Assert.assertEquals(IRDFSchema.XLS_STRING, schema.getPropertyType("Name"));
		Assert.assertEquals("Name", schema.getPropertyLabel("Name", "en"));

		Assert.assertEquals(IRDFSchema.XLS_DATETIME, schema.getPropertyType("DateOnset"));
		Assert.assertEquals("DateOnset", schema.getPropertyLabel("DateOnset", "en"));

		Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, schema.getPropertyType("ILL"));
		Assert.assertEquals("ILL", schema.getPropertyLabel("ILL", "en"));
		
		Assert.assertEquals(IRDFSchema.XLS_DOUBLE, schema.getPropertyType("AGE"));
		Assert.assertEquals("AGE", schema.getPropertyLabel("AGE", "en"));

		Assert.assertEquals(IRDFSchema.XLS_INTEGER, schema.getPropertyType("RecStatus"));
		Assert.assertEquals("RecStatus", schema.getPropertyLabel("RecStatus", "en"));

		Assert.assertEquals("Oswego", schema.getOntologyNameSpace());
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", schema.getOntologyBaseUri());
		Assert.assertEquals("Oswego", schema.getOntologyClassName());
	}
		
	@Test
	public void shouldCreateSchemaFailsIfAnyParameterIsEmptyOrNull(){
		try{
			new RDFSchema(null, "http://localhost:8080/mesh4x#className", "className");
			Assert.fail();
		} catch(IllegalArgumentException e){
			// ok test
		}
		try{
			new RDFSchema("", "http://localhost:8080/mesh4x#className", "className");
			Assert.fail();
		} catch(IllegalArgumentException e){
			// ok test
		}
		try{
			new RDFSchema("ns", null, "className");
			Assert.fail();
		} catch(IllegalArgumentException e){
			// ok test
		}
		try{
			new RDFSchema("ns", "", "className");
			Assert.fail();
		} catch(IllegalArgumentException e){
			// ok test
		}
		try{
			new RDFSchema("ns", "http://localhost:8080/mesh4x#className", null);
			Assert.fail();
		} catch(IllegalArgumentException e){
			// ok test
		}
		try{
			new RDFSchema("ns", "http://localhost:8080/mesh4x#className", "");
			Assert.fail();
		} catch(IllegalArgumentException e){
			// ok test
		}
		
	}
	
	@Test
	public void shouldCreateSchema(){
		RDFSchema schema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", "Oswego");
		Assert.assertNotNull(schema);
		Assert.assertEquals(0, schema.getPropertyCount());
		Assert.assertEquals("Oswego", schema.getOntologyNameSpace());
		Assert.assertEquals("http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", schema.getOntologyBaseUri());
		Assert.assertEquals("Oswego", schema.getOntologyClassName());
	}

	@Test
	public void shouldGetPropertyTypeReturnsNullIfPropertyDoesNotExists() throws FileNotFoundException{
		Reader reader = new FileReader(this.getClass().getResource("oswego.owl").getFile());
		RDFSchema schema = new RDFSchema(reader);;
		Assert.assertNotNull(schema);
		Assert.assertNull(schema.getPropertyType("undefined"));
	}
	
	@Test
	public void shouldGetPropertyType() throws FileNotFoundException{
		Reader reader = new FileReader(this.getClass().getResource("oswego.owl").getFile());
		RDFSchema schema = new RDFSchema(reader);
		Assert.assertNotNull(schema);
		Assert.assertEquals(IRDFSchema.XLS_STRING, schema.getPropertyType("Name"));
	}
	
	@Test
	public void shouldGetPropertyLabelReturnsNullIfPropertyDoesNotExists() throws FileNotFoundException{
		Reader reader = new FileReader(this.getClass().getResource("oswego.owl").getFile());
		RDFSchema schema = new RDFSchema(reader);
		Assert.assertNotNull(schema);
		Assert.assertNull(schema.getPropertyLabel("undefined", "en"));
	}
	
	@Test
	public void shouldGetPropertyLabelReturnsNullIfLangDoesNotExists() throws FileNotFoundException{
		Reader reader = new FileReader(this.getClass().getResource("oswego.owl").getFile());
		RDFSchema schema = new RDFSchema(reader);
		Assert.assertNotNull(schema);
		Assert.assertNull(schema.getPropertyLabel("Name", "sp"));
	}
	
	@Test
	public void shouldGetPropertyLabel() throws FileNotFoundException{
		Reader reader = new FileReader(this.getClass().getResource("oswego.owl").getFile());
		RDFSchema schema = new RDFSchema(reader);
		Assert.assertNotNull(schema);
		Assert.assertEquals("Name", schema.getPropertyLabel("Name", "en"));
	}

	@Test
	public void shouldCannonicaliseValueReturnsNullIfPropertyDoesNotExists() throws FileNotFoundException{
		Reader reader = new FileReader(this.getClass().getResource("oswego.owl").getFile());
		RDFSchema schema = new RDFSchema(reader);
		Assert.assertNotNull(schema);
		Assert.assertNull(schema.cannonicaliseValue("undefined", "123"));
	}
	
	@Test
	public void shouldCannonicaliseValue() throws FileNotFoundException{
		Reader reader = new FileReader(this.getClass().getResource("oswego.owl").getFile());
		RDFSchema schema = new RDFSchema(reader);
		Assert.assertNotNull(schema);
		Assert.assertEquals(new Long(Integer.MIN_VALUE -1), schema.cannonicaliseValue("RecStatus", Integer.MIN_VALUE -1));
	}
	
	@Test
	public void shouldGetPropertyNameReturnsNullIfSchemaHasNotProperties(){
		RDFSchema schema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", "Oswego");
		Assert.assertNotNull(schema);
		Assert.assertEquals(0, schema.getPropertyCount());
		Assert.assertNull(schema.getPropertyName(3));
	}
	
	@Test
	public void shouldGetPropertyNameReturnsNullIfIndexIsOutOfRange(){
		RDFSchema schema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", "Oswego");
		Assert.assertNotNull(schema);
		schema.addStringProperty("name", "name", "en");
		Assert.assertEquals(1, schema.getPropertyCount());
		
		Assert.assertNull(schema.getPropertyName(1));
	}
	
	@Test
	public void shouldGetPropertyName(){
		RDFSchema schema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", "Oswego");
		Assert.assertNotNull(schema);
		schema.addStringProperty("name", "name", "en");
		schema.addStringProperty("code", "code", "en");
		Assert.assertEquals(2, schema.getPropertyCount());
		
		Assert.assertEquals("name", schema.getPropertyName(1));
		Assert.assertEquals("code", schema.getPropertyName(0));
	}
	
	@Test
	public void shouldGetInstanceFromRDFXMLReturnsNullIfElementHasNotRDFNode(){
		Element element = DocumentHelper.createElement("foo");
		RDFSchema schema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", "Oswego");
		Assert.assertNull(schema.getInstanceFromXML(element));
	}
	
	@Test
	public void shouldGetInstanceFromRDFXMLRootElement(){
		Element element = DocumentHelper.createElement(IRDFSchema.QNAME_RDF);
		RDFSchema schema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", "Oswego");
		Element rdfElement = schema.getInstanceFromXML(element);
		Assert.assertEquals(element.asXML(), rdfElement.asXML());
	}
	
	@Test
	public void shouldGetInstanceFromRDFXMLSubElement(){
		Element element = DocumentHelper.createElement("foo");
		Element elementSub = element.addElement(IRDFSchema.QNAME_RDF);
		RDFSchema schema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", "Oswego");
		Element rdfElement = schema.getInstanceFromXML(element);
		Assert.assertEquals(elementSub.asXML(), rdfElement.asXML());
	}
	
	@Test
	public void shouldAsInstancePlainXMLReturnsNullIfElementHasNotRDFNode(){
		Element element = DocumentHelper.createElement("foo");
		RDFSchema schema = new RDFSchema("Oswego", "http://localhost:8080/mesh4x/feeds/Epiinfo/Oswego#", "Oswego");
		Assert.assertNull(schema.asInstancePlainXML(element, ISchema.EMPTY_FORMATS));
	}
	
	@Test
	public void shouldAsInstancePlainXMLRootElement(){
        String rdfXml = "<rdf:RDF xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<example:example rdf:about=\"uri:urn:1\">"+
		"<example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string>"+
		"<example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer>"+
		"<example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean>"+
		"<example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-06-01T05:31:01.001Z</example:datetime>"+
		"<example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double>"+
		"<example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long>"+
		"<example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10</example:decimal>"+
		"</example:example>"+
		"</rdf:RDF>";
        
        Element element = XMLHelper.parseElement(rdfXml);
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
        
		Element plainElement = schema.asInstancePlainXML(element, ISchema.EMPTY_FORMATS);
		Assert.assertEquals("<example><decimal>10</decimal><long>9223372036854775807</long><double>1.7976931348623157E308</double><datetime>2009-06-01T05:31:01.001Z</datetime><boolean>true</boolean><integer>2147483647</integer><string>abc</string></example>", plainElement.asXML());
	}
	
	@Test
	public void shouldAsInstancePlainXMLSubElement(){
		Element element = DocumentHelper.createElement("foo");
		String rdfXml = "<rdf:RDF xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
			"<example:example rdf:about=\"uri:urn:1\">"+
			"<example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string>"+
			"<example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer>"+
			"<example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean>"+
			"<example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-06-01T05:31:01.001Z</example:datetime>"+
			"<example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double>"+
			"<example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long>"+
			"<example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10</example:decimal>"+
			"</example:example>"+
			"</rdf:RDF>";
	        
	    Element elementSub = XMLHelper.parseElement(rdfXml);
	    element.add(elementSub);
	    
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
	        
		Element plainElement = schema.asInstancePlainXML(element, ISchema.EMPTY_FORMATS);
		Assert.assertEquals("<example><decimal>10</decimal><long>9223372036854775807</long><double>1.7976931348623157E308</double><datetime>2009-06-01T05:31:01.001Z</datetime><boolean>true</boolean><integer>2147483647</integer><string>abc</string></example>", plainElement.asXML());

	}
	

	@Test
	public void shouldGetInstanceFromPlainXML(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
	       
		String xml = "<example><decimal>10.8</decimal><long>9223372036854775807</long><double>1.7976931348623157E308</double><datetime>2009-06-01T05:31:01.001Z</datetime><boolean>true</boolean><integer>2147483647</integer><string>abc</string></example>";
		Element element = XMLHelper.parseElement(xml);
		Element rdfElement = schema.getInstanceFromPlainXML("1", element, ISchema.EMPTY_FORMATS);
		
		String rdfXml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<example:example rdf:about=\"uri:urn:1\">"+
		"<example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string>"+
		"<example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer>"+
		"<example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean>"+
		"<example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-06-01T05:31:01.001Z</example:datetime>"+
		"<example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double>"+
		"<example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long>"+
		"<example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10.8</example:decimal>"+
		"</example:example>"+
		"</rdf:RDF>";
		
		Assert.assertEquals(XMLHelper.canonicalizeXML(rdfXml), XMLHelper.canonicalizeXML(rdfElement));
	}
	
	@Test
	public void shouldGetInstanceFromProperties(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		propertyValues.put("decimal", 10d);
		propertyValues.put("long", 9223372036854775807l);
		propertyValues.put("double", 1.7976931348623157E308d);
		propertyValues.put("datetime", DateHelper.parseW3CDateTime("2009-06-01T05:31:01.001Z"));
		propertyValues.put("boolean", true);
		propertyValues.put("integer", 2147483647);
		propertyValues.put("string", "abc");
		
		Element rdfElement = XMLHelper.parseElement(schema.createNewInstanceFromProperties("1", propertyValues).asXML());
		
		String rdfXml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<example:example rdf:about=\"uri:urn:1\">"+
		"<example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string>"+
		"<example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer>"+
		"<example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean>"+
		"<example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-06-01T05:31:01.001Z</example:datetime>"+
		"<example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double>"+
		"<example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long>"+
		"<example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10</example:decimal>"+
		"</example:example>"+
		"</rdf:RDF>";
		
		Assert.assertEquals(XMLHelper.canonicalizeXML(rdfXml), XMLHelper.canonicalizeXML(rdfElement));
	}
	
	@Test
	public void shouldGetPropertiesAsMap(){
		
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		String rdfXml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<example:example rdf:about=\"uri:urn:1\">"+
		"<example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string>"+
		"<example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer>"+
		"<example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean>"+
		"<example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-06-01T05:31:01.001Z</example:datetime>"+
		"<example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double>"+
		"<example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long>"+
		"<example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10</example:decimal>"+
		"</example:example>"+
		"</rdf:RDF>";
		
		Element rdfElement = XMLHelper.parseElement(rdfXml);
		
		Map<String, String> context = schema.getPropertiesAsLexicalFormMap(rdfElement);
		
		Assert.assertEquals("10", context.get("decimal"));
		Assert.assertEquals("9223372036854775807", context.get("long"));
		Assert.assertEquals("1.7976931348623157E308", context.get("double"));
		Assert.assertEquals("2009-06-01T05:31:01.001Z", context.get("datetime"));
		Assert.assertEquals("true", context.get("boolean"));
		Assert.assertEquals("2147483647", context.get("integer"));
		Assert.assertEquals("abc", context.get("string"));
	}

	@Test
	public void shouldIsCompatibleReturnsTrueIfParameterSchemaIsSameInstance(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		Assert.assertTrue(schema.isCompatible(schema));
	}
	
	@Test
	public void shouldIsCompatibleReturnsFalseIfParameterSchemaIsNull(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		Assert.assertFalse(schema.isCompatible(null));
	}

	@Test
	public void shouldIsCompatibleReturnsFalseIfParameterSchemaIsNotRDFSchemaInstance(){
		ISchema mockSchema = new ISchema(){
			@Override public Element asInstancePlainXML(Element element, Map<String, ISchemaTypeFormat> typeFormats) {return null;}
			@Override public String asXML() {return null;}
			@Override public Element getInstanceFromPlainXML(String id, Element element, Map<String, ISchemaTypeFormat> typeFormats) {return null;}
			@Override public Element getInstanceFromXML(Element element) {return null;}
			@Override public Map<String, String> getPropertiesAsLexicalFormMap(Element element) {return null;}
			@Override public Map<String, Object> getPropertiesAsMap(Element element) {return null;}
			@Override public boolean isCompatible(ISchema schema) {return false;}
			@Override public String getName() {return null;}			
		};
		
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		Assert.assertFalse(schema.isCompatible(mockSchema));
	}
	
	@Test
	public void shouldIsCompatibleReturnsTrueIfParameterSchemaHasSameRDFXml(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		RDFSchema otherSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		otherSchema.addStringProperty("string", "string", "en");
		otherSchema.addIntegerProperty("integer", "int", "en");
		otherSchema.addBooleanProperty("boolean", "boolean", "en");
		otherSchema.addDateTimeProperty("datetime", "datetime", "en");
		otherSchema.addDoubleProperty("double", "double", "en");
		otherSchema.addLongProperty("long", "long", "en");
		otherSchema.addDecimalProperty("decimal", "decimal", "en");  
		
		Assert.assertEquals(schema.asXML(), otherSchema.asXML());
		Assert.assertTrue(schema.isCompatible(otherSchema));
	}
	
	@Test
	public void shouldIsCompatibleReturnsTrueIfParameterSchemaHasSameProperties(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		RDFSchema otherSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		otherSchema.addIntegerProperty("integer", "int", "en");
		otherSchema.addBooleanProperty("boolean", "boolean", "en");
		otherSchema.addStringProperty("string", "string", "en");
		otherSchema.addDateTimeProperty("datetime", "datetime", "en");
		otherSchema.addLongProperty("long", "long", "en");
		otherSchema.addDecimalProperty("decimal", "decimal", "en");
		otherSchema.addDoubleProperty("double", "double", "en");
		
		Assert.assertEquals(schema.asXML(), otherSchema.asXML());
		Assert.assertTrue(schema.isCompatible(otherSchema));
	}
	
	@Test
	public void shouldIsCompatibleReturnsFalseIfParameterSchemaHasNotSamePropertiesCount(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		RDFSchema otherSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		otherSchema.addIntegerProperty("integer", "int", "en");
		otherSchema.addBooleanProperty("boolean", "boolean", "en");
		otherSchema.addStringProperty("string", "string", "en");
		
		Assert.assertFalse(schema.isCompatible(otherSchema));
	}
	
	@Test
	public void shouldIsCompatibleReturnsFalseIfParameterSchemaHasNotSamePropertyNames(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		RDFSchema otherSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		otherSchema.addIntegerProperty("integer", "int", "en");
		otherSchema.addBooleanProperty("boolean", "boolean", "en");
		otherSchema.addStringProperty("string", "string", "en");
		otherSchema.addDateTimeProperty("datetime", "datetime", "en");
		otherSchema.addLongProperty("long", "long", "en");
		otherSchema.addDecimalProperty("decimal", "decimal", "en");
		otherSchema.addDoubleProperty("double2", "double", "en");
		
		Assert.assertFalse(schema.isCompatible(otherSchema));
	}
	
	@Test
	public void shouldIsCompatibleReturnsFalseIfParameterSchemaHasNotSamePropertyTypes(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		RDFSchema otherSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		otherSchema.addIntegerProperty("integer", "int", "en");
		otherSchema.addBooleanProperty("boolean", "boolean", "en");
		otherSchema.addStringProperty("string", "string", "en");
		otherSchema.addDateTimeProperty("datetime", "datetime", "en");
		otherSchema.addLongProperty("long", "long", "en");
		otherSchema.addDecimalProperty("decimal", "decimal", "en");
		otherSchema.addStringProperty("double", "double", "en");
		
		Assert.assertFalse(schema.isCompatible(otherSchema));
	}
	
	@Test
	public void shouldIsCompatibleReturnsTrueIfParameterSchemaHasCompatiblePropertyTypes(){
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");  
		
		RDFSchema otherSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		otherSchema.addStringProperty("string", "string", "en");
		otherSchema.addDoubleProperty("integer", "int", "en");
		otherSchema.addBooleanProperty("boolean", "boolean", "en");
		otherSchema.addDateTimeProperty("datetime", "datetime", "en");
		otherSchema.addLongProperty("double", "double", "en");
		otherSchema.addDecimalProperty("long", "long", "en");
		otherSchema.addIntegerProperty("decimal", "decimal", "en");  
		
		Assert.assertFalse(schema.asXML().equals(otherSchema.asXML()));
		Assert.assertTrue(schema.isCompatible(otherSchema));
	}
}
