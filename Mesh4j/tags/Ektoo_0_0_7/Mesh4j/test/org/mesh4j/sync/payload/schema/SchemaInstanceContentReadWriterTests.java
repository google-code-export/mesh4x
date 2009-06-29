package org.mesh4j.sync.payload.schema;

import java.util.Date;

import junit.framework.Assert;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;

public class SchemaInstanceContentReadWriterTests {

	private final RDFSchema RDF_SCHEMA;
	
	{
		RDF_SCHEMA = new RDFSchema("User", "http://localhost:8080/mesh4x/User#", "User");
		RDF_SCHEMA.addStringProperty("id", "id", "en");
		RDF_SCHEMA.addStringProperty("name", "name", "en");
		RDF_SCHEMA.addStringProperty("pass", "pass", "en");
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfSchemaIsNull(){
		new SchemaInstanceContentReadWriter(null, new Mapping(null), true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfMappingIsNull(){
		new SchemaInstanceContentReadWriter(RDF_SCHEMA, null, true);
	}
	
	@Test
	public void shouldCreateWithWriteSync(){
		RDFSchema schema = RDF_SCHEMA;
		Mapping mapping = new Mapping(null);
		SchemaInstanceContentReadWriter schemaRW = new SchemaInstanceContentReadWriter(schema, mapping, true);
		Assert.assertNotNull(schemaRW);
		Assert.assertEquals(schema, schemaRW.getSchema());
		Assert.assertEquals(mapping, schemaRW.getMapping());
		Assert.assertTrue(schemaRW.mustWriteSync(null));
	}
	
	@Test
	public void shouldCreateWithoutWriteSync(){
		RDFSchema schema = RDF_SCHEMA;
		Mapping mapping = new Mapping(null);
		SchemaInstanceContentReadWriter schemaRW = new SchemaInstanceContentReadWriter(schema, mapping, false);
		Assert.assertNotNull(schemaRW);
		Assert.assertEquals(schema, schemaRW.getSchema());
		Assert.assertEquals(mapping, schemaRW.getMapping());
		Assert.assertFalse(schemaRW.mustWriteSync(null));
	}
	
	
	@Test
	public void shouldWriteDeletedItem(){

		SchemaInstanceContentReadWriter schemaRW = new SchemaInstanceContentReadWriter(RDF_SCHEMA, makeMapping(), false);

		Item item = new Item(new NullContent("2"), new Sync("2").delete("jmt", new Date()));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		schemaRW.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>Element was DELETED, content id = 2, sync Id = 2</title><description>---DELETED---</description></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		schemaRW.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">Element was DELETED, content id = 2, sync Id = 2</title><summary xmlns=\"http://www.w3.org/2005/Atom\">---DELETED---</summary></payload>", itemElementAtom.asXML());
	}	
	
	// MAPPINGS
	private IMapping makeMapping() {
		IMapping mapping = new IMapping(){
			@Override public String asXML() {return null;}
			@Override public String getMapping(String mappingName) {return null;}
			@Override public String getValue(Element element, String mappingName) {
				if(ISyndicationFormat.MAPPING_NAME_ITEM_TITLE.equals(mappingName)){
					return "mapTitle";
				}
				if(ISyndicationFormat.MAPPING_NAME_ITEM_DESCRIPTION.equals(mappingName)){
					return "mapDesc";
				}
				return null;
			}			
		};
		return mapping;
	}	
	
	private IMapping makeNullMapping() {
		IMapping mapping = new IMapping(){
			@Override public String asXML() {return null;}
			@Override public String getMapping(String mappingName) {return null;}
			@Override public String getValue(Element element, String mappingName) {return null;}			
		};
		return mapping;
	}	
	
	
	@Test
	public void shouldWriteItemUsingMappings(){
		
		SchemaInstanceContentReadWriter schemaRW = new SchemaInstanceContentReadWriter(RDF_SCHEMA, makeMapping(), false);
		
		IContent content = new IContent(){
			@Override public String getId() {return "1";}
			@Override public Element getPayload() {
				String xmlRow = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
				"<User:User rdf:about=\"uri:urn:1\">"+
				"<User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id>"+
				"<User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name>"+
				"<User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass>"+
				"</User:User>"+
				"</rdf:RDF>";
				Element row = XMLHelper.parseElement(xmlRow);
				return row;
			}
			@Override public int getVersion() {return 0;}
			@Override public IContent clone() {return this;}
		};
		
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		
		schemaRW.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>mapTitle</title><description>mapDesc</description><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<rdf:RDF xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><User:User rdf:about=\"uri:urn:1\"><User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id><User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name><User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass></User:User></rdf:RDF>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		schemaRW.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">mapTitle</title><summary xmlns=\"http://www.w3.org/2005/Atom\">mapDesc</summary><content type=\"text\"><![CDATA[<rdf:RDF xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><User:User rdf:about=\"uri:urn:1\"><User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id><User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name><User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass></User:User></rdf:RDF>]]></content></payload>", itemElementAtom.asXML());
	}		
	
	@Test
	public void shouldWriteItemWhenMappingDoesNotExists(){
	
		SchemaInstanceContentReadWriter schemaRW = new SchemaInstanceContentReadWriter(RDF_SCHEMA, makeNullMapping(), false);
		
		IContent content = new IContent(){
			@Override public String getId() {return "1";}
			@Override public Element getPayload() {
				String xmlRow = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
				"<User:User rdf:about=\"uri:urn:1\">"+
				"<User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id>"+
				"<User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name>"+
				"<User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass>"+
				"</User:User>"+
				"</rdf:RDF>";
				Element row = XMLHelper.parseElement(xmlRow);
				return row;
			}
			@Override public int getVersion() {return 0;}
			@Override public IContent clone() {return this;}
		};
		
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		
		schemaRW.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>2</title><description>Id: 1 Version: 0</description><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<rdf:RDF xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><User:User rdf:about=\"uri:urn:1\"><User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id><User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name><User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass></User:User></rdf:RDF>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		schemaRW.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">2</title><summary xmlns=\"http://www.w3.org/2005/Atom\">Id: 1 Version: 0</summary><content type=\"text\"><![CDATA[<rdf:RDF xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"><User:User rdf:about=\"uri:urn:1\"><User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id><User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name><User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass></User:User></rdf:RDF>]]></content></payload>", itemElementAtom.asXML());
	}
	
	@Test
	public void shouldReadSimpleElementWithEmptyPayload(){
		Element elementPayload = DocumentHelper.createElement("payload");
		
		String xmlRow = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<User:User rdf:about=\"uri:urn:1\">"+
		"<User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id>"+
		"<User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name>"+
		"<User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass>"+
		"</User:User>"+
		"</rdf:RDF>";
		Element element = XMLHelper.parseElement(xmlRow);
				
		SchemaInstanceContentReadWriter schemaRW = new SchemaInstanceContentReadWriter(RDF_SCHEMA, makeNullMapping(), false);
		schemaRW.readContent("1", elementPayload, element);
		
		Assert.assertEquals(xmlRow, element.asXML());
		Assert.assertEquals("<payload>"+xmlRow+"</payload>", elementPayload.asXML());
	}

	@Test
	public void shouldReadMultiElementWithEmptyPayload(){
		Assert.fail("pending task");
	}
	
	@Test
	public void shouldReadSimpleElement(){
		Element elementPayload = DocumentHelper.createElement("payload");
		Element foo = elementPayload.addElement("foo");
		foo.setText("bar");

		String xmlRow = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:User=\"http://localhost:8080/mesh4x/User#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
		"<User:User rdf:about=\"uri:urn:1\">"+
		"<User:id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">1</User:id>"+
		"<User:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</User:name>"+
		"<User:pass rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">123</User:pass>"+
		"</User:User>"+
		"</rdf:RDF>";
		Element element = XMLHelper.parseElement(xmlRow);

		SchemaInstanceContentReadWriter schemaRW = new SchemaInstanceContentReadWriter(RDF_SCHEMA, makeNullMapping(), false);
		schemaRW.readContent("1", elementPayload, element);
		
		Assert.assertEquals(xmlRow, element.asXML());
		Assert.assertEquals("<payload><foo>bar</foo>"+xmlRow+"</payload>", elementPayload.asXML());
	}

	@Test
	public void shouldReadMultiElement(){
		Element elementPayload = DocumentHelper.createElement("payload");
		Element elementFoo1 = elementPayload.addElement("foo1");
		elementFoo1.setText("jmt1");
		
		Assert.fail("pending task");
	}
}
