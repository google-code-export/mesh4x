package com.mesh4j.sync.adapters.kml.spli.content.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.kml.KMLContent;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class KMLPlacemarkTests {

	@Test
	public void shouldUpdatePacemark() throws DocumentException{
		
		String id = IdGenerator.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ IdGenerator.newID() +"\">"+
					"	<name>Folder1</name>"+
					"	<Placemark xml:id=\""+id+"\">"+
					"		<name>a</name>"+
					"		<visibility>0</visibility>"+
					"		<LookAt>"+
					"			<longitude>-86.29603105340947</longitude>"+
					"			<latitude>36.94722720798701</latitude>"+
					"			<altitude>0</altitude>"+
					"			<range>15133106.44824071</range>"+
					"			<tilt>0</tilt>"+
					"			<heading>1.090894378010394</heading>"+
					"		</LookAt>"+
					"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
					"		<Point>"+
					"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
					"		</Point>"+
					"	</Placemark>"+					
					"</Folder>"+
					"<Folder>"+
					"	<name>Folder2</name>"+
					"</Folder>"+
					"<Folder>"+
					"	<name>Folder3</name>"+
					"</Folder>"+
					"</Document>"+
					"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
		
		KMLContent content = kmlAdapter.get(id);
		Assert.assertNotNull(content);
		Assert.assertEquals("a", content.getPayload().element("name").getText());
		Assert.assertEquals(id, content.getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME));
		
		List<IContent> items = kmlAdapter.getAll();
		Assert.assertEquals(4, items.size());
		
		String newXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
							"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
							"<Document>"+
							"<name>a.kml</name>"+
							"	<Placemark xml:id=\""+id+"\">"+
							"		<name>zz</name>"+
							"		<visibility>0</visibility>"+
							"		<LookAt>"+
							"			<longitude>-86.29603105340947</longitude>"+
							"			<latitude>36.94722720798701</latitude>"+
							"			<altitude>0</altitude>"+
							"			<range>15133106.44824071</range>"+
							"			<tilt>0</tilt>"+
							"			<heading>1.090894378010394</heading>"+
							"		</LookAt>"+
							"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
							"		<Point>"+
							"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
							"		</Point>"+
							"	</Placemark>"+
							"</Document>"+
							"</kml>";
		
		Element newElement = DocumentHelper.parseText(newXML).getRootElement().element("Document").element("Placemark");				
		content = new KMLContent(newElement, id);
		kmlAdapter.save(content);
		
		content = kmlAdapter.get(id);
		Assert.assertNotNull(content);
		Assert.assertEquals("zz", content.getPayload().element("name").getText());
		Assert.assertEquals(id, content.getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME));
		
		items = kmlAdapter.getAll();
		Assert.assertEquals(4, items.size());
	}
	
	@Test
	public void shouldDeleteNotEfectWhenItemDoesNotExist(){
		String id = IdGenerator.newID();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ IdGenerator.newID() +"\">"+
					"	<name>Folder1</name>"+
					"	<Placemark xml:id=\""+id+"\">"+
					"		<name>zz</name>"+
					"		<visibility>0</visibility>"+
					"		<LookAt>"+
					"			<longitude>-86.29603105340947</longitude>"+
					"			<latitude>36.94722720798701</latitude>"+
					"			<altitude>0</altitude>"+
					"			<range>15133106.44824071</range>"+
					"			<tilt>0</tilt>"+
					"			<heading>1.090894378010394</heading>"+
					"		</LookAt>"+
					"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
					"		<Point>"+
					"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
					"		</Point>"+
					"	</Placemark>"+
					"</Folder>"+
					"<Folder>"+
					"	<name>Folder2</name>"+
					"</Folder>"+
					"<Folder>"+
					"	<name>Folder3</name>"+
					"</Folder>"+
					"</Document>"+
					"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
		
		List<IContent> items = kmlAdapter.getAll();
		Assert.assertEquals(4, items.size());
		
		kmlAdapter.delete(new KMLContent(DocumentHelper.createElement("payload"), IdGenerator.newID()));
		
		items = kmlAdapter.getAll();
		Assert.assertEquals(4, items.size());
	}
	
	@Test
	public void shouldGetReturnNullWhenItemDoesNotExist(){
		String id = IdGenerator.newID();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ IdGenerator.newID() +"\">"+
					"	<name>Folder1</name>"+
					"	<Placemark xml:id=\""+id+"\">"+
					"		<name>zz</name>"+
					"		<visibility>0</visibility>"+
					"		<LookAt>"+
					"			<longitude>-86.29603105340947</longitude>"+
					"			<latitude>36.94722720798701</latitude>"+
					"			<altitude>0</altitude>"+
					"			<range>15133106.44824071</range>"+
					"			<tilt>0</tilt>"+
					"			<heading>1.090894378010394</heading>"+
					"		</LookAt>"+
					"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
					"		<Point>"+
					"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
					"		</Point>"+
					"	</Placemark>"+
					"</Folder>"+
					"<Folder>"+
					"	<name>Folder2</name>"+
					"</Folder>"+
					"<Folder>"+
					"	<name>Folder3</name>"+
					"</Folder>"+
					"</Document>"+
					"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
		KMLContent content = kmlAdapter.get(IdGenerator.newID());
		
		Assert.assertNull(content);
	}
	
	@Test
	public void shouldGetPlacemarkAsItem(){
		
		String id = IdGenerator.newID();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ IdGenerator.newID() +"\">"+
					"	<name>Folder1</name>"+
					"	<Placemark xml:id=\""+id+"\">"+
					"		<name>a</name>"+
					"		<visibility>0</visibility>"+
					"		<LookAt>"+
					"			<longitude>-86.29603105340947</longitude>"+
					"			<latitude>36.94722720798701</latitude>"+
					"			<altitude>0</altitude>"+
					"			<range>15133106.44824071</range>"+
					"			<tilt>0</tilt>"+
					"			<heading>1.090894378010394</heading>"+
					"		</LookAt>"+
					"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
					"		<Point>"+
					"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
					"		</Point>"+
					"	</Placemark>"+
					"</Folder>"+
					"	<Placemark>"+
					"		<name>One</name>"+
					"		<visibility>0</visibility>"+
					"		<LookAt>"+
					"			<longitude>-86.29603105340947</longitude>"+
					"			<latitude>36.94722720798701</latitude>"+
					"			<altitude>0</altitude>"+
					"			<range>15133106.44824071</range>"+
					"			<tilt>0</tilt>"+
					"			<heading>1.090894378010394</heading>"+
					"		</LookAt>"+
					"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
					"		<Point>"+
					"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
					"		</Point>"+
					"	</Placemark>"+					
					"<Folder>"+
					"	<name>Folder2</name>"+
					"</Folder>"+
					"<Folder>"+
					"	<name>Folder3</name>"+
					"</Folder>"+
					"</Document>"+
					"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
		KMLContent content = kmlAdapter.get(id);
		
		Assert.assertNotNull(content);
		Assert.assertEquals("a", content.getPayload().element("name").getText());
	}
	
	@Test
	public void shouldReturnsPlacemarksAsItems(){
		
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"	<name>a.kml</name>"+
					"	<Placemark>"+
					"		<name>a</name>"+
					"		<visibility>0</visibility>"+
					"		<LookAt>"+
					"			<longitude>-86.29603105340947</longitude>"+
					"			<latitude>36.94722720798701</latitude>"+
					"			<altitude>0</altitude>"+
					"			<range>15133106.44824071</range>"+
					"			<tilt>0</tilt>"+
					"			<heading>1.090894378010394</heading>"+
					"		</LookAt>"+
					"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
					"		<Point>"+
					"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
					"		</Point>"+
					"	</Placemark>"+
					"	<Placemark>"+
					"		<name>b</name>"+
					"		<visibility>0</visibility>"+
					"		<LookAt>"+
					"			<longitude>-86.29603105340947</longitude>"+
					"			<latitude>36.94722720798701</latitude>"+
					"			<altitude>0</altitude>"+
					"			<range>15133106.44824071</range>"+
					"			<tilt>0</tilt>"+
					"			<heading>1.090894378010394</heading>"+
					"		</LookAt>"+
					"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
					"		<Point>"+
					"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
					"		</Point>"+
					"	</Placemark>"+
					"</Document>"+
					"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
		List<IContent> items = kmlAdapter.getAll();
		
		Assert.assertEquals(2, items.size());
		String xmlID1 = items.get(0).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		String xmlID2 = items.get(1).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		
		Assert.assertNotNull(xmlID1);
		Assert.assertNotNull(xmlID2);
		Assert.assertFalse(xmlID1.equals(xmlID2));
		Assert.assertEquals("a", items.get(0).getPayload().element("name").getText());
		Assert.assertEquals("b", items.get(1).getPayload().element("name").getText());
	}
	
	@Test
	public void shouldAddNewPlacemark() throws DocumentException{
		
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Placemark>"+
		"		<name>a</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"	<Placemark>"+
		"		<name>b</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"</Document>"+
		"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
		
		String id = IdGenerator.newID();
		
		String newXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"	<name>a.kml</name>"+
			"	<Placemark xml:id=\""+id+"\">"+
			"		<name>new</name>"+
			"		<visibility>0</visibility>"+
			"		<LookAt>"+
			"			<longitude>-86.29603105340947</longitude>"+
			"			<latitude>36.94722720798701</latitude>"+
			"			<altitude>0</altitude>"+
			"			<range>15133106.44824071</range>"+
			"			<tilt>0</tilt>"+
			"			<heading>1.090894378010394</heading>"+
			"		</LookAt>"+
			"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
			"		<Point>"+
			"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
			"		</Point>"+
			"	</Placemark>"+
			"</Document>"+
			"</kml>";
			
		Element newElement = DocumentHelper.parseText(newXML).getRootElement().element("Document").element("Placemark");				
		KMLContent content = new KMLContent(newElement, id);
		kmlAdapter.save(content);
		
		KMLContent addedContent = kmlAdapter.get(id);
		Assert.assertNotNull(addedContent);
		
		Element addedPlacemark = addedContent.getPayload();
		String xmlID = addedPlacemark.attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		Assert.assertEquals(id, xmlID);
		Assert.assertEquals("new", addedPlacemark.element("name").getText());
				
		List<IContent> items = kmlAdapter.getAll();
		Assert.assertEquals(3, items.size());
		
		addedPlacemark = items.get(2).getPayload();
		xmlID = addedPlacemark.attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		Assert.assertEquals(id, xmlID);
		Assert.assertNotNull(xmlID);
		Assert.assertEquals("new", addedPlacemark.element("name").getText());
				
		String xmlID1 = items.get(0).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		String xmlID2 = items.get(1).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);		
		Assert.assertNotNull(xmlID1);
		Assert.assertEquals("a", items.get(0).getPayload().element("name").getText());
		Assert.assertNotNull(xmlID2);
		Assert.assertEquals("b", items.get(1).getPayload().element("name").getText());

	}
	
	@Test
	public void shouldDeletePlacemark() throws DocumentException{
		
		String id = IdGenerator.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Placemark xml:id=\""+id+"\">"+
		"		<name>a</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"	<Placemark>"+
		"		<name>b</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"</Document>"+
		"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
				
		List<IContent> items = kmlAdapter.getAll();
		KMLContent content = (KMLContent)items.get(0);
		
		Assert.assertEquals(2, items.size());
		String xmlID1 = items.get(0).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		String xmlID2 = items.get(1).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		
		Assert.assertNotNull(xmlID1);
		Assert.assertNotNull(xmlID2);
		Assert.assertTrue(id.equals(xmlID1));
		Assert.assertFalse(xmlID1.equals(xmlID2));
		Assert.assertEquals("a", items.get(0).getPayload().element("name").getText());
		Assert.assertEquals("b", items.get(1).getPayload().element("name").getText());
		
		kmlAdapter.delete(content);
		
		items = kmlAdapter.getAll();
		Assert.assertEquals(1, items.size());
		xmlID1 = items.get(0).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);		
		Assert.assertNotNull(xmlID1);
		Assert.assertFalse(id.equals(xmlID1));
		Assert.assertEquals("b", items.get(0).getPayload().element("name").getText());

	}
	
	@SuppressWarnings({ "unchecked" })
	@Test
	public void shouldPrepareKML() throws DocumentException, JaxenException{
		
		String id = IdGenerator.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Placemark xml:id=\""+id+"\">"+
		"		<name>a</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"	<Placemark>"+
		"		<name>b</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"</Document>"+
		"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		KMLContentAdapter.prepareKMLToSync(file.getAbsolutePath());
		
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(KMLContentAdapterNames.XML_PREFIX, KMLContentAdapterNames.XML_URI);
		map.put(KMLContentAdapterNames.KML_PREFIX, KMLContentAdapterNames.KML_URI);

		Dom4jXPath xpath = new Dom4jXPath("//kml:*[@xml:id]");
		xpath.setNamespaceContext(new SimpleNamespaceContext(map));
		List<Element> elements = xpath.selectNodes(document);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(2, elements.size());
		String xmlID1 = elements.get(0).attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		String xmlID2 = elements.get(1).attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		
		Assert.assertNotNull(xmlID1);
		Assert.assertNotNull(xmlID2);
		Assert.assertTrue(id.equals(xmlID1));
		Assert.assertFalse(xmlID1.equals(xmlID2));
		Assert.assertEquals("a", elements.get(0).element("name").getText());
		Assert.assertEquals("b", elements.get(1).element("name").getText());
		
		HashSet<String> ids = new HashSet<String>();		
		ids.add(xmlID1);
		ids.add(xmlID2);
		Assert.assertEquals(2, ids.size());
	}
	
	@Test
	public void shouldNormalizeContent() throws DocumentException{
		
		String id = IdGenerator.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Placemark xml:id=\""+id+"\">"+
		"		<name>a</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"</Document>"+
		"</kml>";
		
		Element newElement = DocumentHelper.parseText(xml).getRootElement().element("Document").element("Placemark");				
		KMLContent content = new KMLContent(newElement, id);
		
		KMLContent normalizedContent = KMLContent.normalizeContent(content);
		Assert.assertEquals(content, normalizedContent);
		Assert.assertEquals(content.getId(), normalizedContent.getId());
	}
}
