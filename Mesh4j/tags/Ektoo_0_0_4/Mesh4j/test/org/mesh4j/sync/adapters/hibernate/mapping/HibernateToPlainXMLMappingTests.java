package org.mesh4j.sync.adapters.hibernate.mapping;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.utils.XMLHelper;

public class HibernateToPlainXMLMappingTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfIDNodeIsNull(){
		new HibernateToPlainXMLMapping("user", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfIDNodeIsEmpty(){
		new HibernateToPlainXMLMapping("user", "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfEntityNodeIsNull(){
		new HibernateToPlainXMLMapping(null, "id");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsIfEntityNodeIsEmpty(){
		new HibernateToPlainXMLMapping("", "id");
	}
	
	@Test
	public void shouldCreateMapping(){
		HibernateToPlainXMLMapping mapping = new HibernateToPlainXMLMapping("user", "id");
		
		Assert.assertEquals("user", mapping.getEntityNode());
		Assert.assertEquals("id", mapping.getIDNode());
	}
	
	@Test 
	public void shouldConvertRowToXML() throws Exception{
		HibernateToPlainXMLMapping mapping = new HibernateToPlainXMLMapping("user", "id");
		
		String xmlRow = "<user><id>1</id><name>juan</name><pass>123</pass></user>";
		Element row = XMLHelper.parseElement(xmlRow);
		Element rowPlainXML = mapping.convertRowToXML("1", row);
		Assert.assertNotNull(rowPlainXML);
		Assert.assertEquals(XMLHelper.canonicalizeXML(row), XMLHelper.canonicalizeXML(rowPlainXML));
	}
	
	
	@Test 
	public void shouldConvertXMLToRow() throws Exception{
		HibernateToPlainXMLMapping mapping = new HibernateToPlainXMLMapping("User", "id");
		
		String xml = "<User><pass>123</pass><name>juan</name><id>1</id></User>";
		Element rowPlainXml = XMLHelper.parseElement(xml);
		Element row = mapping.convertXMLToRow(rowPlainXml);
		
		Assert.assertNotNull(row);
		Assert.assertEquals(XMLHelper.canonicalizeXML(rowPlainXml), XMLHelper.canonicalizeXML(row));
	}
}
