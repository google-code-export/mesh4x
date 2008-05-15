package com.mesh4j.sync.adapters.kml;

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

import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class KMLFolderTests {

	@Test
	public void shouldUpdateFolder() throws DocumentException{
		
		String id = IdGenerator.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ id +"\">"+
					"	<name>Folder1</name>"+
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
		Assert.assertEquals("Folder1", content.getPayload().element("name").getText());
		Assert.assertEquals(id, content.getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		
		List<IContent> items = kmlAdapter.getAll();
		Assert.assertEquals(3, items.size());
		
		String newFolderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
							"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
							"<Document>"+
							"<name>a.kml</name>"+
							"<Folder xml:id=\""+ id +"\">"+
							"	<name>New Folder</name>"+
							"</Folder>"+
							"</Document>"+
							"</kml>";
		
		Element newFolderElement = DocumentHelper.parseText(newFolderXML).getRootElement().element("Document").element("Folder");				
		content = new KMLContent(newFolderElement, id);
		kmlAdapter.save(content);
		
		content = kmlAdapter.get(id);
		Assert.assertNotNull(content);
		Assert.assertEquals("New Folder", content.getPayload().element("name").getText());
		Assert.assertEquals(id, content.getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		
		items = kmlAdapter.getAll();
		Assert.assertEquals(3, items.size());
	}
	
	@Test
	public void shouldDeleteNotEfectWhenItemDoesNotExist(){
		String id = IdGenerator.newID();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ id +"\">"+
					"	<name>Folder1</name>"+
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
					"<Folder xml:id=\""+ id +"\">"+
					"	<name>Folder1</name>"+
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
	public void shouldGetFolderAsItem(){
		
		String id = IdGenerator.newID();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ id +"\">"+
					"	<name>Folder1</name>"+
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
		Assert.assertEquals("Folder1", content.getPayload().element("name").getText());
		Assert.assertEquals(1, content.getPayload().elements().size());
	}
	
	@Test
	public void shouldReturnsFolderAsItems(){
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder>"+
					"	<name>Folder1</name>"+
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
		Assert.assertEquals("Folder1", items.get(0).getPayload().element("name").getText());
		Assert.assertEquals(1, items.get(0).getPayload().elements().size());
		Assert.assertEquals("Folder2", items.get(1).getPayload().element("name").getText());
		Assert.assertEquals("Folder3", items.get(2).getPayload().element("name").getText());
		Assert.assertEquals("Placemark", items.get(3).getPayload().getName());
	}
	
	@Test
	public void shouldAddNewFolder() throws DocumentException{
		
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder>"+
					"	<name>Folder1</name>"+
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
		
		String id = IdGenerator.newID();
		
		String newFolderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
							"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
							"<Document>"+
							"<name>a.kml</name>"+
							"<Folder xml:id=\""+ id +"\">"+
							"	<name>New Folder</name>"+
							"</Folder>"+
							"</Document>"+
							"</kml>";
		
		Element newFolderElement = DocumentHelper.parseText(newFolderXML).getRootElement().element("Document").element("Folder");				
		KMLContent content = new KMLContent(newFolderElement, id);
		kmlAdapter.save(content);
		
		KMLContent addedContent = kmlAdapter.get(id);
		Assert.assertNotNull(addedContent);
		
		Element addedFolder = addedContent.getPayload();
		Assert.assertEquals("New Folder", addedFolder.element("name").getText());
		Assert.assertEquals(id, addedFolder.attributeValue(KmlNames.XML_ID_QNAME));
		
		List<IContent> items = kmlAdapter.getAll();
		Assert.assertEquals(4, items.size());
		
		addedFolder = items.get(3).getPayload();
		
		Assert.assertEquals("New Folder", addedFolder.element("name").getText());
		Assert.assertEquals(id, addedFolder.attributeValue(KmlNames.XML_ID_QNAME));
				
		Assert.assertEquals("Folder1", items.get(0).getPayload().element("name").getText());
		Assert.assertEquals("Folder2", items.get(1).getPayload().element("name").getText());
		Assert.assertEquals("Folder3", items.get(2).getPayload().element("name").getText());
	}
	
	@Test
	public void shouldDeleteFolder() throws DocumentException{
		
		String id = IdGenerator.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ id +"\">"+
					"	<name>Folder1</name>"+
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
		KMLContent content = (KMLContent)items.get(0);
		
		Assert.assertEquals(3, items.size());
		Assert.assertEquals("Folder1", content.getPayload().element("name").getText());
		Assert.assertEquals(id, content.getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(id, content.getId());
		Assert.assertEquals("Folder2", items.get(1).getPayload().element("name").getText());
		Assert.assertEquals("Folder3", items.get(2).getPayload().element("name").getText());
		
		kmlAdapter.delete(content);
		items = kmlAdapter.getAll();
		Assert.assertEquals(2, items.size());
		Assert.assertEquals("Folder2", items.get(0).getPayload().element("name").getText());
		Assert.assertEquals("Folder3", items.get(1).getPayload().element("name").getText());		
	}
	
	@SuppressWarnings({ "unchecked" })
	@Test
	public void shouldPrepareKML() throws DocumentException, JaxenException{
		
		String id = IdGenerator.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"<Folder xml:id=\""+ id +"\">"+
					"	<name>Folder1</name>"+
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
		KMLContentAdapter.prepareKMLToSync(file.getAbsolutePath());
		
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(KmlNames.XML_PREFIX, KmlNames.XML_URI);
		map.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);

		Dom4jXPath xpath = new Dom4jXPath("//kml:*[@xml:id]");
		xpath.setNamespaceContext(new SimpleNamespaceContext(map));
		List<Element> elements = xpath.selectNodes(document);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(3, elements.size());
		Assert.assertEquals("Folder", elements.get(0).getName());
		Assert.assertEquals("Folder1", elements.get(0).element("name").getText());
		Assert.assertEquals("Folder", elements.get(1).getName());
		Assert.assertEquals("Folder2", elements.get(1).element("name").getText());
		Assert.assertEquals("Folder", elements.get(2).getName());
		Assert.assertEquals("Folder3", elements.get(2).element("name").getText());

		HashSet<String> ids = new HashSet<String>();
		
		ids.add(elements.get(0).attributeValue(KmlNames.XML_ID_QNAME));
		ids.add(elements.get(1).attributeValue(KmlNames.XML_ID_QNAME));
		ids.add(elements.get(2).attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(3, ids.size());
	}
	
	@Test
	public void shouldNormalizeContent() throws DocumentException{
		
		String id = IdGenerator.newID();
		
		String newFolderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
							"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
							"<Document>"+
							"<name>a.kml</name>"+
							"<Folder xml:id=\""+ id +"\">"+
							"	<name>New Folder</name>"+
							"</Folder>"+
							"</Document>"+
							"</kml>";
		
		Element newFolderElement = DocumentHelper.parseText(newFolderXML).getRootElement().element("Document").element("Folder");				
		KMLContent content = new KMLContent(newFolderElement, id);
		
		KMLContent normalizedContent = KMLContent.normalizeContent(content);
		Assert.assertEquals(content, normalizedContent);
	}
}
