package org.mesh4j.sync.payload.mappings;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

public class MappingTests {

	@Test
	public void shouldCreateEmptyMappingWhenMappingElementIsNull(){
		Mapping mapping = new Mapping(null);
		Assert.assertEquals("<mappings></mappings>", mapping.asXML());
	}
	
	@Test
	public void shouldGetValue() throws DocumentException{
		
		Element mappings = DocumentHelper.parseText("<mappings><name>Name of patient: {patient/name} {patient/lastName}.</name></mappings>").getRootElement();
		Mapping mapping = new Mapping(mappings);
		
		String propertyName = "name";
		Element element = DocumentHelper.parseText("<patient><name>Juan</name><lastName>Tondato</lastName></patient>").getRootElement();
		String value = mapping.getValue(element, propertyName);
		
		Assert.assertEquals("Name of patient: Juan Tondato.", value);

		Element elementPayload = DocumentHelper.parseText("<payload><patient><name>Juan</name><lastName>Tondato</lastName></patient></payload>").getRootElement();
		value = mapping.getValue(elementPayload, propertyName);
		
		Assert.assertEquals("Name of patient: Juan Tondato.", value);

	}
	
	@Test
	public void shouldGetValueFromPropertyResolver() throws DocumentException{
		IPropertyResolver prop1 = new IPropertyResolver(){
			@Override public boolean accepts(String variableTemplate) {return variableTemplate.startsWith("prop1(");}
			@Override public String getPropertyValue(Element element, String variableTemplate) {return "P1";}
		};
		
		IPropertyResolver prop2 = new IPropertyResolver(){
			@Override public boolean accepts(String variableTemplate) {return variableTemplate.startsWith("prop2(");}
			@Override public String getPropertyValue(Element element, String variableTemplate) {return "P2";}
		};

		
		Element mappings = DocumentHelper.parseText("<mappings><name>{prop1(patient/name)} {prop2(patient/lastName)}.</name></mappings>").getRootElement();
		Mapping mapping = new Mapping(mappings, prop1, prop2);
		
		Element element = DocumentHelper.parseText("<patient><name>Juan</name><lastName>Tondato</lastName></patient>").getRootElement();
		String value = mapping.getValue(element, "name");
		Assert.assertEquals("P1 P2.", value);
	}
	
	@Test
	public void shouldGetMapping() throws DocumentException{
		Element mappings = DocumentHelper.parseText("<mappings><name>Name of patient: {patient/name} {patient/lastName}.</name></mappings>").getRootElement();
		Mapping mapping = new Mapping(mappings);
		
		Assert.assertEquals("Name of patient: {patient/name} {patient/lastName}.", mapping.getMapping("name"));
	}
	
	@Test
	public void shouldGetAttribute() throws DocumentException{
		Element mappings = DocumentHelper.parseText("<mappings><name>{patient/name}</name></mappings>").getRootElement();
		Mapping mapping = new Mapping(mappings);
		
		Assert.assertEquals("{patient/name}", mapping.getMapping("name"));
	}
	
	@Test
	public void shouldAsXml() throws DocumentException{
		Element mappings = DocumentHelper.parseText("<mappings><name>{patient/name}</name></mappings>").getRootElement();
		Mapping mapping = new Mapping(mappings);
		Assert.assertEquals(mappings.asXML(), mapping.asXML());
	}
	
	@Test
	public void shouldAsXmlText() throws DocumentException{
		Element mappings = DocumentHelper.parseText("<mappings><name>{patient/name}</name></mappings>").getRootElement();
		Mapping mapping = new Mapping(mappings);
		Assert.assertEquals(mappings.asXML(), mapping.asXML());
	}
}
