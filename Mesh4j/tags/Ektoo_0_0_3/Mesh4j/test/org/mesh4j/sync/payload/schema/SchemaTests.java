package org.mesh4j.sync.payload.schema;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.utils.XMLHelper;

public class SchemaTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSchemaFailsIfSchemaElementIsNull(){
		new Schema(null);
	}
	
	@Test
	public void shouldAsXml() throws DocumentException{
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Assert.assertEquals("<name>Name of patient: {patient/name} {patient/lastName}.</name>", schema.asXML());
	}
	
	@Test
	public void shouldAsXmlText() throws DocumentException{
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Assert.assertEquals("<name>Name of patient: {patient/name} {patient/lastName}.</name>", schema.asXML());
	}
	
	@Test
	public void shouldAsInstancePlainXML() {
		Element element = DocumentHelper.createElement("foo");
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Assert.assertEquals(element, schema.asInstancePlainXML(element, ISchema.EMPTY_FORMATS));
	}

	@Test
	public void shouldGetInstanceFromPlainXML() {
		Element element = DocumentHelper.createElement("foo");
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Assert.assertEquals(element, schema.getInstanceFromPlainXML("1", element, ISchema.EMPTY_FORMATS));
	}
	
	@Test
	public void shouldGetInstanceFromXML() {
		Element element = DocumentHelper.createElement("foo");
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Assert.assertEquals(element, schema.getInstanceFromXML(element));
	}
}
