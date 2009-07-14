package org.mesh4j.sync.payload.schema;

import java.util.Map;

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
	
	@Test
	public void shouldIsCompatibleReturnsFalseWhenSchemaParameterIsNull(){
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Assert.assertFalse(schema.isCompatible(null));
	}

	@Test
	public void shouldIsCompatibleReturnsFalseWhenSchemaParameterIsNotSchemaInstance(){
		ISchema mockSchema = new ISchema(){
			@Override public Element asInstancePlainXML(Element element, Map<String, ISchemaTypeFormat> typeFormats) {return null;}
			@Override public String asXML() {return null;}
			@Override public Element getInstanceFromPlainXML(String id, Element element, Map<String, ISchemaTypeFormat> typeFormats) {return null;}
			@Override public Element getInstanceFromXML(Element element) {return null;}
			@Override public Map<String, String> getPropertiesAsLexicalFormMap(Element element) {return null;}
			@Override public Map<String, Object> getPropertiesAsMap(Element element) {return null;}
			@Override public boolean isCompatible(ISchema schema) {return false;}
			@Override public String getName() {return null;}			
		};
		
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Assert.assertFalse(schema.isCompatible(mockSchema));
	}
	
	@Test
	public void shouldIsCompatibleReturnsFalseWhenSchemaHasNotSameXml(){
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Schema schemaOther = new Schema(XMLHelper.parseElement("<name1>Name of patient: {patient/name} {patient/lastName}.</name1>"));
		Assert.assertFalse(schema.isCompatible(schemaOther));
	}
	
	@Test
	public void shouldIsCompatibleReturnsTrueWhenSchemaHasSameXml(){
		Schema schema = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Schema schemaOther = new Schema(XMLHelper.parseElement("<name>Name of patient: {patient/name} {patient/lastName}.</name>"));
		Assert.assertTrue(schema.isCompatible(schemaOther));
	}
}