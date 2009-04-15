package org.mesh4j.sync.adapters.hibernate.mapping;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;

public class HibernateToRDFMappingTests {

	private final RDFSchema RDF_SCHEMA;
	
	{
		RDF_SCHEMA = new RDFSchema("User", "http://localhost:8080/mesh4x/User#", "User");
		RDF_SCHEMA.addStringProperty("id", "id", "en");
		RDF_SCHEMA.addStringProperty("name", "name", "en");
		RDF_SCHEMA.addStringProperty("pass", "pass", "en");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfSchemaIsNull(){
		new HibernateToRDFMapping(null, "user", "id");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfIDNodeIsNull(){
		new HibernateToRDFMapping(RDF_SCHEMA, "user", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfIDNodeIsEmpty(){
		new HibernateToRDFMapping(RDF_SCHEMA, "user", "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfEntityNodeIsNull(){
		new HibernateToRDFMapping(RDF_SCHEMA, null, "id");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfEntityNodeIsEmpty(){
		new HibernateToRDFMapping(RDF_SCHEMA, "", "id");
	}
	
	@Test
	public void shouldCreateMapping(){
		HibernateToRDFMapping mapping = new HibernateToRDFMapping(RDF_SCHEMA, "user", "id");
		
		Assert.assertEquals("user", mapping.getEntityNode());
		Assert.assertEquals("id", mapping.getIDNode());
		Assert.assertEquals(RDF_SCHEMA, mapping.getSchema());
	}
	
	@Test 
	public void shouldConvertRowToXML() throws Exception{
		HibernateToRDFMapping mapping = new HibernateToRDFMapping(RDF_SCHEMA, "user", "id");
		
		String xml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<User:User rdf:about=\"uri:urn:1\">"+
		"<User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id>"+
		"<User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name>"+
		"<User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass>"+
		"</User:User>"+
		"</rdf:RDF>";
		  
		String xmlRow = "<user><id>1</id><name>juan</name><pass>123</pass></user>";
		Element row = XMLHelper.parseElement(xmlRow);
		Element rowAsRDF = mapping.convertRowToXML("1", row);
		Assert.assertNotNull(rowAsRDF);
		Assert.assertEquals(XMLHelper.canonicalizeXML(xml), XMLHelper.canonicalizeXML(rowAsRDF));
	}
	
	
	@Test 
	public void shouldConvertXMLToRow() throws Exception{
		HibernateToRDFMapping mapping = new HibernateToRDFMapping(RDF_SCHEMA, "User", "id");
		
		String xml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<User:User rdf:about=\"uri:urn:1\">"+
		"<User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id>"+
		"<User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name>"+
		"<User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass>"+
		"</User:User>"+
		"</rdf:RDF>";
		  
		String xmlRow = "<User><pass>123</pass><name>juan</name><id>1</id></User>";
		Element rowAsRDF = XMLHelper.parseElement(xml);
		Element row = mapping.convertXMLToRow(rowAsRDF);
		
		Assert.assertNotNull(row);
		Assert.assertEquals(XMLHelper.canonicalizeXML(xmlRow), XMLHelper.canonicalizeXML(row));
	}
	
	
	@Test 
	public void shouldConvertXMLToRowMultiPlayload() throws Exception{
		HibernateToRDFMapping mapping = new HibernateToRDFMapping(RDF_SCHEMA, "User", "id");
		
		String xml = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<User:User rdf:about=\"uri:urn:1\">"+
		"<User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id>"+
		"<User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name>"+
		"<User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass>"+
		"</User:User>"+
		"</rdf:RDF>";
		  
		String xmlRow = "<User><pass>123</pass><name>juan</name><id>1</id></User>";
		Element rowAsRDF = XMLHelper.parseElement("<payload><foo1>bar</foo1>"+xml+"<foo>bar</foo></payload>");
		Element row = mapping.convertXMLToRow(rowAsRDF);
		
		Assert.assertNotNull(row);
		Assert.assertEquals(XMLHelper.canonicalizeXML(xmlRow), XMLHelper.canonicalizeXML(row));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldConvertXMLToRowMultiPlayloadFailsIfRDFElementDoesNotExists() throws Exception{
		HibernateToRDFMapping mapping = new HibernateToRDFMapping(RDF_SCHEMA, "User", "id");
		
		Element rowAsRDF = XMLHelper.parseElement("<payload><foo1>bar</foo1><foo>bar</foo></payload>");
		mapping.convertXMLToRow(rowAsRDF);
	}
	
}
