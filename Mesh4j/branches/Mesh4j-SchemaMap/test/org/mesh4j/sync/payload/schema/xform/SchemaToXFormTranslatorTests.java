package org.mesh4j.sync.payload.schema.xform;

import java.io.File;
import java.io.IOException;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.payload.schema.Schema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.XMLHelper;

public class SchemaToXFormTranslatorTests {

	@Test
	public void shouldNotTranslateNullSchema(){
		Assert.assertEquals("", SchemaToXFormTranslator.translate(null));
	}

	@Test
	public void shouldNotTranslateNoRDFSchema(){
		Assert.assertEquals("", SchemaToXFormTranslator.translate(new Schema(DocumentHelper.createElement("foo"))));
	}
	
	@Test
	public void shouldTranslateRDFSchema() throws IOException{
		String xml = new String(FileUtils.read(new File(getClass().getResource("xform.txt").getFile())));
		Element element = XMLHelper.parseElement(xml);
		
		String xmlXForm = SchemaToXFormTranslator.translate(getDefaultRDFSchema());
		Element elementXForm = XMLHelper.parseElement(xmlXForm);
		
		Assert.assertEquals(XMLHelper.canonicalizeXML(element), XMLHelper.canonicalizeXML(elementXForm));
		
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
