package org.mesh4j.sync.payload.schema.xform;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.XMLHelper;

public class SchemaToXFormTranslatorTests {

	@Test
	public void shouldGenerateXForm() throws IOException{
		
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", "en");
		schema.addIntegerProperty("integer", "int", "en");
		schema.addBooleanProperty("boolean", "boolean", "en");
		schema.addDateTimeProperty("datetime", "datetime", "en");
		schema.addDoubleProperty("double", "double", "en");
		schema.addLongProperty("long", "long", "en");
		schema.addDecimalProperty("decimal", "decimal", "en");
		
		String xml = XMLHelper.canonicalizeXML(SchemaToXFormTranslator.translate(schema));

		String xmlForm = XMLHelper.canonicalizeXML(new String(FileUtils.read(this.getClass().getResource("XFormSchemaForTests.txt").getFile())));
		
		Assert.assertEquals(xmlForm, xml);
	}
}
