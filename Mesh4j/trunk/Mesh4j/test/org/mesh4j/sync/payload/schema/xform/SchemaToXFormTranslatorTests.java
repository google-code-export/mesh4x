package org.mesh4j.sync.payload.schema.xform;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.XMLHelper;

public class SchemaToXFormTranslatorTests {

	@Test
	public void shouldGenerateXForm() throws IOException{
		
		RDFSchema schema = new RDFSchema("example", "http://mesh4x/example#", "example");
		schema.addStringProperty("string", "string", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addIntegerProperty("integer", "int", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addBooleanProperty("boolean", "boolean", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addDateTimeProperty("datetime", "datetime", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addDoubleProperty("double", "double", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addLongProperty("long", "long", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);
		
		String xml = XMLHelper.canonicalizeXML(SchemaToXFormTranslator.translate(schema));

		String xmlForm = XMLHelper.canonicalizeXML(new String(FileUtils.read(this.getClass().getResource("XFormSchemaForTests.txt").getFile())));
		
		Assert.assertEquals(xmlForm, xml);
	}
}
