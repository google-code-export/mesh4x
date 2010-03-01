package org.mesh4j.sync.payload.schema.rdf;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.payload.schema.SchemaTypeFormat;
import org.mesh4j.sync.payload.schema.xform.XFormBooleanFormat;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

public class RDFInstanceTest {

	private static Date DATE = TestHelper.makeDate(2009, 4, 1, 1, 1, 1, 1);
	private static RDFSchema RDF_SCHEMA;
	private static RDFInstance RDF_INSTANCE;
	private static String RDF_XML;
	private static Element RDF_XML_ELEMENT;
	private static String PLAIN_XML;
	private static Element PLAIN_XML_ELEMENT;
	{
		RDF_SCHEMA = new RDFSchema("example", "http://mesh4x/example#", "example");
		RDF_SCHEMA.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addBooleanProperty("boolean", "boolean", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addDateTimeProperty("datetime", "datetime", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addLongProperty("long", "long", IRDFSchema.DEFAULT_LANGUAGE);
        RDF_SCHEMA.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);        
    	
        RDF_INSTANCE = new RDFInstance(RDF_SCHEMA, "uri:urn:1");
        RDF_INSTANCE.setProperty("string", "abc");
        RDF_INSTANCE.setProperty("integer", Integer.MAX_VALUE);
        RDF_INSTANCE.setProperty("boolean", true);
        RDF_INSTANCE.setProperty("datetime", DATE);
        RDF_INSTANCE.setProperty("double", Double.MAX_VALUE);
        RDF_INSTANCE.setProperty("long", Long.MAX_VALUE);
        RDF_INSTANCE.setProperty("decimal", BigDecimal.TEN);
        
        RDF_XML = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example:example rdf:about=\"uri:urn:1\"><example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10</example:decimal><example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long><example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double><example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-06-01T05:31:01.001Z</example:datetime><example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean><example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer><example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string></example:example></rdf:RDF>";
        RDF_XML_ELEMENT = XMLHelper.parseElement(RDF_XML);
        PLAIN_XML = "<example><integer>2147483647</integer><string>abc</string><boolean>true</boolean><datetime>2009-05-01T05:31:01.001Z</datetime><double>1.7976931348623157E308</double><long>9223372036854775807</long><decimal>10</decimal></example>";
        PLAIN_XML_ELEMENT = XMLHelper.parseElement(PLAIN_XML);
      }
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromRDFSchemaFailsIfSchemaIsNull(){
		new RDFInstance(null, "uri:urn:1");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromRDFSchemaFailsIfIdIsNull(){
		new RDFInstance(RDF_SCHEMA, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromRDFSchemaFailsIfIdIsEmpty(){
		new RDFInstance(RDF_SCHEMA, "");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromRDFSchemaFailsIfIdIsNotURLValid(){
		new RDFInstance(RDF_SCHEMA, "2234ded");
	}
	
	@Test
	public void shouldBuildInstanceFromRDFSchema(){
		Assert.assertNotNull(new RDFInstance(RDF_SCHEMA, "uri:urn:3421"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromRDFXmlFailsIfSchemaIsNull(){
		RDFInstance.buildFromRDFXml(null, RDF_XML);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromRDFXmlFailsIfXMLIsNull(){
		RDFInstance.buildFromRDFXml(RDF_SCHEMA, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromRDFXmlFailsIfXMLIsEmpty(){
		RDFInstance.buildFromRDFXml(RDF_SCHEMA, "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromRDFXmlFailsIfXMLIsNotValid(){
		String rdfXml = "<foo>bar</foo>";
		RDFInstance.buildFromRDFXml(RDF_SCHEMA, rdfXml);
	}
	
	@Test(expected=MeshException.class)
	public void shouldBuildInstanceFromRDFXmlFailsIfXMLIsNotValidXML(){
		String rdfXml = "235245";
		RDFInstance.buildFromRDFXml(RDF_SCHEMA, rdfXml);
	}
	
	@Test
	public void shouldBuildInstanceFromRDFXml(){
		String rdfXml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example:example rdf:about=\"uri:urn:1\"><example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string><example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer><example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean><example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-06-01T05:31:01.001Z</example:datetime><example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double><example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long><example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10</example:decimal></example:example></rdf:RDF>";
		rdfXml = XMLHelper.canonicalizeXML(XMLHelper.parseElement(rdfXml));
		Assert.assertEquals(rdfXml, RDFInstance.buildFromRDFXml(RDF_SCHEMA, RDF_XML).asRDFXML());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIsSchemaIsNull(){
		RDFInstance.buildFromPlainXML(null, "uri:urn:1", PLAIN_XML_ELEMENT, ISchema.EMPTY_FORMATS, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfIdIsNull(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, null, PLAIN_XML_ELEMENT, ISchema.EMPTY_FORMATS, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfIdIsEmpty(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, "", PLAIN_XML_ELEMENT, ISchema.EMPTY_FORMATS, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfXMLIsNull(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, "uri:urn:1", null, ISchema.EMPTY_FORMATS, null);
	}
	
//	@Test(expected=IllegalArgumentException.class)
//	public void shouldBuildInstanceFromPlainXmlFailsIfXMLIsEmpty(){
//		RDFInstance.buildFromPlainXML(RDF_SCHEMA, "uri:urn:1", "", ISchema.EMPTY_FORMATS);
//	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfFormatsIsNull(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, "uri:urn:1", PLAIN_XML_ELEMENT, null, null);
	}
	
	@Test
	public void shouldBuildInstanceFromPlainXml(){
		String rdfXml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example:example rdf:about=\"uri:urn:1\"><example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string><example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer><example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean><example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-05-01T05:31:01.001Z</example:datetime><example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double><example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long><example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10</example:decimal></example:example></rdf:RDF>";
		rdfXml = XMLHelper.canonicalizeXML(XMLHelper.parseElement(rdfXml));
		Assert.assertEquals(rdfXml, RDFInstance.buildFromPlainXML(RDF_SCHEMA, "1", PLAIN_XML_ELEMENT, ISchema.EMPTY_FORMATS, null).asRDFXML());
	}

	@Test
	public void shouldBuildInstanceFromPlainXmlWithTypeFormats(){
		
		String xml = "<example><integer>2147483647</integer><string>abc</string><boolean>true</boolean><datetime>2009-05-01</datetime><double>1.7976931348623157E308</double><long>9223372036854775807</long><decimal>10</decimal></example>";
		Element plainXml = XMLHelper.parseElement(xml);
		
		HashMap<String, ISchemaTypeFormat> formats = new HashMap<String, ISchemaTypeFormat>();
		formats.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		formats.put(IRDFSchema.XLS_BOOLEAN, XFormBooleanFormat.INSTANCE);
		
		Assert.assertEquals(
				"<example>" +
				"<boolean>true</boolean>" +
				"<datetime>2009-05-01</datetime>" +
				"<decimal>10</decimal>" +
				"<double>1.7976931348623157E308</double>" +
				"<integer>2147483647</integer>" +
				"<long>9223372036854775807</long>" +
				"<string>abc</string>" +
				"</example>", 
				RDFInstance.buildFromPlainXML(RDF_SCHEMA, "1", plainXml, formats, null).asPlainXML(formats));

	}
	
	@Test
	public void shouldBuildInstanceFromPlainXmlWithPropertyNameTypeFormats(){
		 
		String xml = "<example><integer>2147483647</integer><string>abc</string><boolean>true</boolean><datetime>2009-05-01</datetime><double>1.7976931348623157E308</double><long>9223372036854775807</long><decimal>10</decimal></example>";
		Element plainXml = XMLHelper.parseElement(xml);
		
		HashMap<String, ISchemaTypeFormat> formats = new HashMap<String, ISchemaTypeFormat>();
		formats.put("datetime", new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		formats.put("boolean", XFormBooleanFormat.INSTANCE);
		
		Assert.assertEquals(
			"<example>" +
			"<boolean>true</boolean>" +
			"<datetime>2009-05-01</datetime>" +
			"<decimal>10</decimal>" +
			"<double>1.7976931348623157E308</double>" +
			"<integer>2147483647</integer>" +
			"<long>9223372036854775807</long>" +
			"<string>abc</string>" +
			"</example>", 
			RDFInstance.buildFromPlainXML(RDF_SCHEMA, "1", plainXml, formats, null).asPlainXML(formats));
	}
	
	@Test
	public void shouldSetProperty() {
		RDFInstance instance = new RDFInstance(RDF_SCHEMA, "uri:urn:1");
		instance.setProperty("string", "abc");
		instance.setProperty("integer", Integer.MAX_VALUE);
		instance.setProperty("boolean", true);
		instance.setProperty("datetime", TestHelper.makeDate(2009, 5, 1, 1, 1, 1, 1));
		instance.setProperty("double", Double.MAX_VALUE);
		instance.setProperty("long", Long.MAX_VALUE);
        instance.setProperty("decimal", BigDecimal.TEN);
        
        Assert.assertEquals(XMLHelper.canonicalizeXML(RDF_XML_ELEMENT), instance.asRDFXML());
	}

	@Test
	public void shouldAsPlainXML() {
		RDFInstance instance = RDF_INSTANCE;
        
        Assert.assertEquals(XMLHelper.canonicalizeXML(PLAIN_XML_ELEMENT), instance.asPlainXML());
	}
	
	
	@Test
	public void shouldAsPlainXMLEscapeSpecialXmlCharacters() {
		RDFSchema rdfSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		rdfSchema.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
    	
        RDFInstance rdfInstance = new RDFInstance(rdfSchema, "uri:urn:1");
        rdfInstance.setProperty("string", "hola & chau");
        
        System.out.println(rdfInstance.asRDFXML());
        Assert.assertEquals("<example><string>hola &amp; chau</string></example>", rdfInstance.asPlainXML());
    }
	
	@Test
	public void shouldAsXMLEscapeSpecialXmlCharacters() {
		RDFSchema rdfSchema = new RDFSchema("example", "http://mesh4x/example#", "example");
		rdfSchema.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
    	
        RDFInstance rdfInstance = new RDFInstance(rdfSchema, "uri:urn:1");
        rdfInstance.setProperty("string", "hola & chau");
		Element element = XMLHelper.parseElement("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example:example rdf:about=\"uri:urn:1\"><example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">hola &amp; chau</example:string></example:example></rdf:RDF>");		
	
        Assert.assertEquals(
        	XMLHelper.canonicalizeXML(element), 
       		rdfInstance.asRDFXML());
    }
	
		
	@Test
	public void shouldAsPlainXMLWithTypeFormats() {
		RDFInstance instance = RDF_INSTANCE;
        
		String plainXML = "<example><integer>2147483647</integer><string>abc</string><boolean>true</boolean><datetime>2009-05-01</datetime><double>1.7976931348623157E308</double><long>9223372036854775807</long><decimal>10</decimal></example>";
		Element expectedXml = XMLHelper.parseElement(plainXML);
		
		HashMap<String, ISchemaTypeFormat> formats = new HashMap<String, ISchemaTypeFormat>();
		formats.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		formats.put(IRDFSchema.XLS_BOOLEAN, XFormBooleanFormat.INSTANCE);
			
        Assert.assertEquals(XMLHelper.canonicalizeXML(expectedXml), instance.asPlainXML(formats));
	}
	
	@Test
	public void shouldAsPlainXMLWithTypeFormatsAndCompositeId() {
		RDFInstance instance = RDF_INSTANCE;
        
		String plainXML = "<example><composite2><datetime>2009-05-01</datetime><double>1.7976931348623157E308</double></composite2><long>9223372036854775807</long><composite1><integer>2147483647</integer><string>abc</string><boolean>true</boolean></composite1><decimal>10</decimal></example>";
		Element expectedXML = XMLHelper.parseElement(plainXML);
		
		HashMap<String, ISchemaTypeFormat> formats = new HashMap<String, ISchemaTypeFormat>();
		formats.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		formats.put(IRDFSchema.XLS_BOOLEAN, XFormBooleanFormat.INSTANCE);
		
		CompositeProperty compositeProperty1 = new CompositeProperty("composite1", Arrays.asList(new String[]{"integer", "string", "boolean"}));
		CompositeProperty compositeProperty2 = new CompositeProperty("composite2", Arrays.asList(new String[]{"datetime", "double"}));
		
		CompositeProperty[] compositeProperties = new CompositeProperty[]{compositeProperty1, compositeProperty2};
        Assert.assertEquals(XMLHelper.canonicalizeXML(expectedXML), instance.asPlainXML(formats, compositeProperties));
	}
	
	@Test
	public void shouldAsPlainXMLWithPropertyNameTypeFormats() {
		RDFInstance instance = RDF_INSTANCE;
        
		String plainXML = "<example><integer>2147483647</integer><string>abc</string><boolean>true</boolean><datetime>2009-05-01</datetime><double>1.7976931348623157E308</double><long>9223372036854775807</long><decimal>10</decimal></example>";
		Element expectedXml = XMLHelper.parseElement(plainXML);
		
		HashMap<String, ISchemaTypeFormat> formats = new HashMap<String, ISchemaTypeFormat>();
		formats.put("datetime", new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		formats.put("boolean", XFormBooleanFormat.INSTANCE);
		
		
        Assert.assertEquals(XMLHelper.canonicalizeXML(expectedXml), instance.asPlainXML(formats));
	}
	
	@Test
	public void shouldAsPlainXMLWithPropertyNameTypeFormatsAndCompositeId() {
		RDFInstance instance = RDF_INSTANCE;
        
		String plainXML = "<example><composite2><datetime>2009-05-01</datetime><double>1.7976931348623157E308</double></composite2><long>9223372036854775807</long><composite1><integer>2147483647</integer><string>abc</string><boolean>true</boolean></composite1><decimal>10</decimal></example>";
		Element expectedXml = XMLHelper.parseElement(plainXML);
		
		HashMap<String, ISchemaTypeFormat> formats = new HashMap<String, ISchemaTypeFormat>();
		formats.put("datetime", new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		formats.put("boolean", XFormBooleanFormat.INSTANCE);
		
		CompositeProperty compositeProperty1 = new CompositeProperty("composite1", Arrays.asList(new String[]{"integer", "string", "boolean"}));
		CompositeProperty compositeProperty2 = new CompositeProperty("composite2", Arrays.asList(new String[]{"datetime", "double"}));
		
		CompositeProperty[] compositeProperties = new CompositeProperty[]{compositeProperty1, compositeProperty2};
        Assert.assertEquals(XMLHelper.canonicalizeXML(expectedXml), instance.asPlainXML(formats, compositeProperties));
	}
	
	@Test
	public void shouldGetPropertyCount() {
		RDFInstance instance = RDF_INSTANCE;        
        Assert.assertEquals(7, instance.getPropertyCount());
	}

	@Test
	public void shouldGetPropertyValue() {
		RDFInstance instance = RDF_INSTANCE;
        
        Assert.assertEquals("abc", instance.getPropertyValue("string"));
        Assert.assertEquals(Integer.MAX_VALUE, instance.getPropertyValue("integer"));
        Assert.assertEquals(true, instance.getPropertyValue("boolean"));
        Assert.assertEquals(DATE, instance.getPropertyValue("datetime"));
        Assert.assertEquals(Double.MAX_VALUE, instance.getPropertyValue("double"));
        Assert.assertEquals(Long.MAX_VALUE, instance.getPropertyValue("long"));
        Assert.assertEquals(BigDecimal.TEN, new BigDecimal(instance.getPropertyValue("decimal").toString()));
	}

	@Test
	public void shouldGetPropertyName() {
		RDFInstance instance = RDF_INSTANCE;
		Assert.assertEquals("long", instance.getPropertyName(0));
		Assert.assertEquals("datetime", instance.getPropertyName(1)); 
		Assert.assertEquals("string", instance.getPropertyName(2));
		Assert.assertEquals("double", instance.getPropertyName(3));
		Assert.assertEquals("decimal", instance.getPropertyName(4));
		Assert.assertEquals("integer", instance.getPropertyName(5));       
		Assert.assertEquals("boolean", instance.getPropertyName(6));
    
	}

	@Test
	public void shouldGetPropertyType() {
		RDFInstance instance = RDF_INSTANCE;
        
        Assert.assertEquals(IRDFSchema.XLS_STRING, instance.getPropertyType("string"));
        Assert.assertEquals(IRDFSchema.XLS_INTEGER, instance.getPropertyType("integer"));
        Assert.assertEquals(IRDFSchema.XLS_BOOLEAN, instance.getPropertyType("boolean"));
        Assert.assertEquals(IRDFSchema.XLS_DATETIME, instance.getPropertyType("datetime"));
        Assert.assertEquals(IRDFSchema.XLS_DOUBLE, instance.getPropertyType("double"));
        Assert.assertEquals(IRDFSchema.XLS_LONG, instance.getPropertyType("long"));
        Assert.assertEquals(IRDFSchema.XLS_DECIMAL, instance.getPropertyType("decimal"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPropertiesFailsIfPropertiesIsNull(){
		RDFInstance.buildFromProperties(RDF_SCHEMA, "1", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPropertiesFailsIfPropertiesIsEmpty(){
		RDFInstance.buildFromProperties(RDF_SCHEMA, "1", new HashMap<String, Object>());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPropertiesFailsIfRDFSchemaIsNull(){
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put("string", "abc");
        propertyValues.put("integer", Integer.MAX_VALUE);
        propertyValues.put("boolean", true);
        propertyValues.put("datetime", DATE);
        propertyValues.put("double", Double.MAX_VALUE);
        propertyValues.put("long", Long.MAX_VALUE);
        propertyValues.put("decimal", BigDecimal.TEN);
		RDFInstance.buildFromProperties(null, "1", propertyValues);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPropertiesFailsIfIdIsNull(){
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put("string", "abc");
        propertyValues.put("integer", Integer.MAX_VALUE);
        propertyValues.put("boolean", true);
        propertyValues.put("datetime", DATE);
        propertyValues.put("double", Double.MAX_VALUE);
        propertyValues.put("long", Long.MAX_VALUE);
        propertyValues.put("decimal", BigDecimal.TEN);
		RDFInstance.buildFromProperties(RDF_SCHEMA, null, propertyValues);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPropertiesFailsIfIdIsEmpty(){
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put("string", "abc");
        propertyValues.put("integer", Integer.MAX_VALUE);
        propertyValues.put("boolean", true);
        propertyValues.put("datetime", DATE);
        propertyValues.put("double", Double.MAX_VALUE);
        propertyValues.put("long", Long.MAX_VALUE);
        propertyValues.put("decimal", BigDecimal.TEN);
		RDFInstance.buildFromProperties(RDF_SCHEMA, "", propertyValues);
	}
	
	@Test
	public void shouldBuildInstanceFromProperties(){
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put("string", "abc");
        propertyValues.put("integer", Integer.MAX_VALUE);
        propertyValues.put("boolean", true);
        propertyValues.put("datetime", DATE);
        propertyValues.put("double", Double.MAX_VALUE);
        propertyValues.put("long", Long.MAX_VALUE);
        propertyValues.put("decimal", BigDecimal.TEN);
        RDFInstance instance = RDFInstance.buildFromProperties(RDF_SCHEMA, "1", propertyValues);
        
        String rdfXml = "<rdf:RDF xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><example:example rdf:about=\"uri:urn:1\"><example:string rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">abc</example:string><example:integer rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">2147483647</example:integer><example:boolean rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</example:boolean><example:datetime rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2009-05-01T05:31:01.001Z</example:datetime><example:double rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">1.7976931348623157E308</example:double><example:long rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">9223372036854775807</example:long><example:decimal rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10</example:decimal></example:example></rdf:RDF>";
        Element expectedRDFXml = XMLHelper.parseElement(rdfXml);
        Assert.assertNotNull(instance);
        Assert.assertEquals(XMLHelper.canonicalizeXML(expectedRDFXml), XMLHelper.canonicalizeXML(XMLHelper.parseElement(instance.asRDFXML())));
	}
	
	@Test
	public void shouldGetValueAsLexicalForm(){
        
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.US);
        dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));

        RDFInstance instance = RDF_INSTANCE;
        
        Assert.assertEquals("abc", instance.getPropertyValueAsLexicalForm("string"));
        Assert.assertEquals(String.valueOf(Integer.MAX_VALUE), instance.getPropertyValueAsLexicalForm("integer"));
        Assert.assertEquals(String.valueOf(true), instance.getPropertyValueAsLexicalForm("boolean"));
        Assert.assertEquals(String.valueOf(Double.MAX_VALUE), instance.getPropertyValueAsLexicalForm("double"));
        Assert.assertEquals(String.valueOf(Long.MAX_VALUE), instance.getPropertyValueAsLexicalForm("long"));
        Assert.assertEquals(String.valueOf(BigDecimal.TEN), instance.getPropertyValueAsLexicalForm("decimal"));
        Assert.assertEquals(dateFormater.format(DATE), instance.getPropertyValueAsLexicalForm("datetime"));
	}
}
