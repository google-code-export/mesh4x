package org.mesh4j.sync.adapters.feed;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

public class ContentReaderTests {

	@Test
	public void shouldNotFailsIfPayloadIsNull(){
		Element element = DocumentHelper.createElement("foo");
		String xml = element.asXML();
		ContentReader.INSTANCE.readContent("1", null, element);
		Assert.assertEquals(xml, element.asXML());
	}
	
	@Test
	public void shouldNotFailsIfElementIsNull(){
		Element element = DocumentHelper.createElement("payload");
		String xml = element.asXML();
		ContentReader.INSTANCE.readContent("1", element, null);
		Assert.assertEquals(xml, element.asXML());
	}
	
	@Test
	public void shouldReadSimpleElementWithEmptyPayload(){
		Element elementPayload = DocumentHelper.createElement("payload");
		
		Element element = DocumentHelper.createElement("foo");
		Element elementBar = element.addElement("bar");
		elementBar.setText("jmt");
		
		Assert.assertEquals("<foo><bar>jmt</bar></foo>", element.asXML());
		Assert.assertEquals("<payload/>", elementPayload.asXML());
		
		ContentReader.INSTANCE.readContent("1", elementPayload, element);
		
		Assert.assertEquals("<foo><bar>jmt</bar></foo>", element.asXML());
		Assert.assertEquals("<payload><foo><bar>jmt</bar></foo></payload>", elementPayload.asXML());
	}

	@Test
	public void shouldReadMultiElementWithEmptyPayload(){
		Element elementPayload = DocumentHelper.createElement("payload");
				
		Element element = DocumentHelper.createElement("payload");
		Element elementFoo = element.addElement("foo");
		elementFoo.setText("jmt2");		
		Element elementBar = element.addElement("bar");
		elementBar.setText("jmt3");
		
		Assert.assertEquals("<payload><foo>jmt2</foo><bar>jmt3</bar></payload>", element.asXML());
		Assert.assertEquals("<payload/>", elementPayload.asXML());
		
		ContentReader.INSTANCE.readContent("1", elementPayload, element);
		
		Assert.assertEquals("<payload><foo>jmt2</foo><bar>jmt3</bar></payload>", element.asXML());
		Assert.assertEquals("<payload><foo>jmt2</foo><bar>jmt3</bar></payload>", elementPayload.asXML());
	}
	
	@Test
	public void shouldReadSimpleElement(){
		Element elementPayload = DocumentHelper.createElement("payload");
		Element element = DocumentHelper.createElement("foo");
		Element elementBar = element.addElement("bar");
		elementBar.setText("jmt");
		
		Assert.assertEquals("<foo><bar>jmt</bar></foo>", element.asXML());
		Assert.assertEquals("<payload/>", elementPayload.asXML());
		
		ContentReader.INSTANCE.readContent("1", elementPayload, element);
		
		Assert.assertEquals("<foo><bar>jmt</bar></foo>", element.asXML());
		Assert.assertEquals("<payload><foo><bar>jmt</bar></foo></payload>", elementPayload.asXML());
	}

	@Test
	public void shouldReadMultiElement(){
		Element elementPayload = DocumentHelper.createElement("payload");
		Element elementFoo1 = elementPayload.addElement("foo1");
		elementFoo1.setText("jmt1");
		
		Element element = DocumentHelper.createElement("payload");
		Element elementFoo = element.addElement("foo");
		elementFoo.setText("jmt2");		
		Element elementBar = element.addElement("bar");
		elementBar.setText("jmt3");
		
		Assert.assertEquals("<payload><foo>jmt2</foo><bar>jmt3</bar></payload>", element.asXML());
		Assert.assertEquals("<payload><foo1>jmt1</foo1></payload>", elementPayload.asXML());
		
		ContentReader.INSTANCE.readContent("1", elementPayload, element);
		
		Assert.assertEquals("<payload><foo>jmt2</foo><bar>jmt3</bar></payload>", element.asXML());
		Assert.assertEquals("<payload><foo1>jmt1</foo1><foo>jmt2</foo><bar>jmt3</bar></payload>", elementPayload.asXML());
	}
}
