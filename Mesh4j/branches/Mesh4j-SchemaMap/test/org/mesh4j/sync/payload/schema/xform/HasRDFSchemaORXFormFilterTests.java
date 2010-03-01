package org.mesh4j.sync.payload.schema.xform;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.XMLHelper;

public class HasRDFSchemaORXFormFilterTests {

	@Test
	public void shouldAcceptItemWithoutRDFSchemaWithXForm() throws IOException{
		
		String xmlFile = new String(FileUtils.read(new File(getClass().getResource("xform.txt").getFile())));
					
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);		
		Element xformElement = payload.addElement(XFormRDFSchemaContentWriter.ELEMENT_XFORM);
		xformElement.setText(xmlFile);
				
		IContent content = new XMLContent(IdGenerator.INSTANCE.newID(), "", "", payload);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);		
		Item item = new Item(content, sync);
		
		Assert.assertTrue(HasRDFSchemaORXFormFilter.INSTANCE.applies(item));
	}
	
	@Test
	public void shouldAcceptItemWithRDFSchemaWithXForm() throws IOException{
		
		String xmlFile = new String(FileUtils.read(new File(getClass().getResource("xform.txt").getFile())));
					
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);		
		Element schemaElement = payload.addElement(ISchema.ELEMENT_SCHEMA);		
		schemaElement.setText(getDefaultRDFSchema().asXML());
		Element xformElement = payload.addElement(XFormRDFSchemaContentWriter.ELEMENT_XFORM);
		xformElement.setText(xmlFile);
				
		IContent content = new XMLContent(IdGenerator.INSTANCE.newID(), "", "", payload);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);		
		Item item = new Item(content, sync);
		
		Assert.assertTrue(HasRDFSchemaORXFormFilter.INSTANCE.applies(item));
	}
	
	@Test
	public void shouldWriteItemWithRDFSchemaWithoutXForm() throws IOException{
		
		Element payload = XMLHelper.parseElement(getDefaultRDFSchema().asXML());
		
		IContent content = new XMLContent(IdGenerator.INSTANCE.newID(), "", "", payload);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);		
		Item item = new Item(content, sync);
		
		Assert.assertTrue(HasRDFSchemaORXFormFilter.INSTANCE.applies(item));
	}
		
	@Test
	public void shouldWriteItemWithoutRDFSchemaWithoutXForm() throws IOException{
		
		Element payload = DocumentHelper.createElement("foo");
		
		IContent content = new XMLContent(IdGenerator.INSTANCE.newID(), "", "", payload);
		Sync sync = new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false);		
		Item item = new Item(content, sync);
		
		Assert.assertFalse(HasRDFSchemaORXFormFilter.INSTANCE.applies(item));
	}

	private IRDFSchema getDefaultRDFSchema() {
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Oswego");
		rdfSchema.addBooleanProperty("ill", "ill", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("dateOnset", "dateOnSet", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("AgeDouble", "ageDouble", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("AgeInt", "ageInt", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("AgeLong", "ageLong", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
	
		return rdfSchema;
	
	}

}
