package org.mesh4j.sync.payload.schema.rdf;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import junit.framework.Assert;

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
	private static String PLAIN_XML;
	{
		RDF_SCHEMA = new RDFSchema("example", "http://mesh4x/example#", "example");
		RDF_SCHEMA.addStringProperty("string", "string", "en");
        RDF_SCHEMA.addIntegerProperty("integer", "int", "en");
        RDF_SCHEMA.addBooleanProperty("boolean", "boolean", "en");
        RDF_SCHEMA.addDateTimeProperty("datetime", "datetime", "en");
        RDF_SCHEMA.addDoubleProperty("double", "double", "en");
        RDF_SCHEMA.addLongProperty("long", "long", "en");
        RDF_SCHEMA.addDecimalProperty("decimal", "decimal", "en");        
    	
        RDF_INSTANCE = new RDFInstance(RDF_SCHEMA, "uri:urn:1");
        RDF_INSTANCE.setProperty("string", "abc");
        RDF_INSTANCE.setProperty("integer", Integer.MAX_VALUE);
        RDF_INSTANCE.setProperty("boolean", true);
        RDF_INSTANCE.setProperty("datetime", DATE);
        RDF_INSTANCE.setProperty("double", Double.MAX_VALUE);
        RDF_INSTANCE.setProperty("long", Long.MAX_VALUE);
        RDF_INSTANCE.setProperty("decimal", BigDecimal.TEN);
        
        RDF_XML = "<rdf:RDF xmlns:example=\"http://mesh4x/example#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
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
        
        PLAIN_XML = "<example>"+
					"<string>abc</string>"+
					"<integer>2147483647</integer>"+
					"<boolean>true</boolean>"+
					"<datetime>2009-06-01T05:31:01.001Z</datetime>"+
					"<double>1.7976931348623157E308</double>"+
					"<long>9223372036854775807</long>"+
					"<decimal>10</decimal>"+
					"</example>";
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
		Assert.assertEquals(RDF_XML, RDFInstance.buildFromRDFXml(RDF_SCHEMA, RDF_XML).asXML());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIsSchemaIsNull(){
		RDFInstance.buildFromPlainXML(null, "uri:urn:1", PLAIN_XML, ISchema.EMPTY_FORMATS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfIdIsNull(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, null, PLAIN_XML, ISchema.EMPTY_FORMATS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfIdIsEmpty(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, "", PLAIN_XML, ISchema.EMPTY_FORMATS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfXMLIsNull(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, "uri:urn:1", null, ISchema.EMPTY_FORMATS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfXMLIsEmpty(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, "uri:urn:1", "", ISchema.EMPTY_FORMATS);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldBuildInstanceFromPlainXmlFailsIfFormatsIsNull(){
		RDFInstance.buildFromPlainXML(RDF_SCHEMA, "uri:urn:1", PLAIN_XML, null);
	}
	
	@Test
	public void shouldBuildInstanceFromPlainXml(){
		Assert.assertEquals(RDF_XML, RDFInstance.buildFromPlainXML(RDF_SCHEMA, "uri:urn:1", PLAIN_XML, ISchema.EMPTY_FORMATS).asXML());
	}

	@Test
	public void shouldSetProperty() {
		RDFInstance instance = new RDFInstance(RDF_SCHEMA, "uri:urn:1");
		instance.setProperty("string", "abc");
		instance.setProperty("integer", Integer.MAX_VALUE);
		instance.setProperty("boolean", true);
		instance.setProperty("datetime", TestHelper.makeDate(2009, 4, 1, 1, 1, 1, 1));
		instance.setProperty("double", Double.MAX_VALUE);
		instance.setProperty("long", Long.MAX_VALUE);
        instance.setProperty("decimal", BigDecimal.TEN);
        
        Assert.assertEquals(RDF_XML, instance.asXML());
	}

	@Test
	public void shouldAsPlainXML() {
		RDFInstance instance = RDF_INSTANCE;
        
        Assert.assertEquals(PLAIN_XML, instance.asPlainXML());
	}
	
	@Test
	public void shouldAsPlainXMLWithTypeFormats() {
		RDFInstance instance = RDF_INSTANCE;
        
		String plainXML = "<example>"+
		"<string>abc</string>"+
		"<integer>2147483647</integer>"+
		"<boolean>yes</boolean>"+
		"<datetime>2009-06-01</datetime>"+
		"<double>1.7976931348623157E308</double>"+
		"<long>9223372036854775807</long>"+
		"<decimal>10</decimal>"+
		"</example>";
		
		HashMap<String, ISchemaTypeFormat> formats = new HashMap<String, ISchemaTypeFormat>();
		formats.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat("yyyy-MM-dd")));
		formats.put(IRDFSchema.XLS_BOOLEAN, XFormBooleanFormat.INSTANCE);
		
        Assert.assertEquals(plainXML, instance.asPlainXML(formats));
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
        Assert.assertEquals(BigDecimal.TEN, instance.getPropertyValue("decimal"));
	}

	@Test
	public void shouldGetPropertyName() {
		RDFInstance instance = RDF_INSTANCE;
		Assert.assertEquals("decimal", instance.getPropertyName(1)); 
		Assert.assertEquals("long", instance.getPropertyName(2));
		Assert.assertEquals("double", instance.getPropertyName(3));
		Assert.assertEquals("datetime", instance.getPropertyName(4));
		Assert.assertEquals("booelan", instance.getPropertyName(5));       
		Assert.assertEquals("integer", instance.getPropertyName(6));
		Assert.assertEquals("string", instance.getPropertyName(7));
    
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
        
        Assert.assertNotNull(instance);
        Assert.assertEquals(XMLHelper.canonicalizeXML(RDF_XML), XMLHelper.canonicalizeXML(XMLHelper.parseElement(instance.asXML())));
	}
}
