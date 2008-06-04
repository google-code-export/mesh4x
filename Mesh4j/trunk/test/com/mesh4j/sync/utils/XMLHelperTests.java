package com.mesh4j.sync.utils;

import java.io.IOException;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.test.utils.TestHelper;

public class XMLHelperTests {

	@Test
	public void shouldCanonicalizeXML(){
		Date date = new Date();
		
		Element element = DocumentHelper.createElement(ISyndicationFormat.SX_QNAME_HISTORY);
		element.addAttribute(ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY, "jmt");
		element.addAttribute(ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN, DateHelper.formatW3CDateTime(date));
		
		String xml = XMLHelper.canonicalizeXML(element);
		
		Element element2 = DocumentHelper.createElement(ISyndicationFormat.SX_QNAME_HISTORY);
		element2.addAttribute(ISyndicationFormat.SX_ATTRIBUTE_HISTORY_WHEN, DateHelper.formatW3CDateTime(date));
		element2.addAttribute(ISyndicationFormat.SX_ATTRIBUTE_HISTORY_BY, "jmt");
		
		String xml2 = XMLHelper.canonicalizeXML(element);
		
		Assert.assertEquals(xml, xml2);
		
	}
	
	@Test
	public void shouldEncodeFileBytesInXML() throws IOException{
		byte[] originalBytes = TestHelper.readFileBytes(this.getClass().getResource("kmzExample_star.jpg").getFile());
		String encoded = Base64Helper.encode(originalBytes);
		Assert.assertNotNull(encoded);

		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("foo"));
		Element element = doc.getRootElement().addElement(MeshNames.MESH_QNAME_FILE);
		element.addAttribute(MeshNames.MESH_QNAME_FILE_ID, "kmzExample_star.jpg");
		Element fileContentElement = element.addElement(MeshNames.MESH_QNAME_FILE_CONTENT);
		fileContentElement.setText(encoded);
		
		String xml = XMLHelper.canonicalizeXML(element);
		Assert.assertNotNull(xml);
		Assert.assertEquals(element.asXML(), xml);
		
		encoded = doc.getRootElement().element(MeshNames.MESH_QNAME_FILE).element(MeshNames.MESH_QNAME_FILE_CONTENT).getText();
		byte[] decodedBytes = Base64Helper.decode(encoded);
		Assert.assertNotNull(decodedBytes);
		Assert.assertArrayEquals(originalBytes, decodedBytes);
		
	}
}
