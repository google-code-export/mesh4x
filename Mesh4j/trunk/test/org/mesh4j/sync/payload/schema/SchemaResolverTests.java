package org.mesh4j.sync.payload.schema;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

public class SchemaResolverTests {

	@Test
	public void shouldResolveSchema() throws DocumentException{
		Element schema = DocumentHelper.parseText("<schema><name>Name of patient: {patient/name} {patient/lastName}.</name></schema>").getRootElement();
		SchemaResolver schemaResolver = new SchemaResolver(schema);
		Assert.assertEquals(schema.asXML(), schemaResolver.getSchema().asXML());
	}
	
}
