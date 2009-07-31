package org.mesh4j.sync.adapters.kml.split.content.adapter;

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
import org.mesh4j.sync.adapters.kml.KMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.test.utils.TestHelper;


public class KMLStyleMapTests {

	@Test
	public void shouldUpdatePacemark() throws DocumentException{
		
		String id = IdGenerator.INSTANCE.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"	<StyleMap id=\"msn_ylw-pushpin_"+id+"\" xml:id=\""+id+"\">"+
					"		<Pair>"+
					"			<key>normal</key>"+
					"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
					"		</Pair>"+
					"		<Pair>"+
					"			<key>highlight</key>"+
					"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
					"		</Pair>"+
					"	</StyleMap>"+					
					"<Folder xml:id=\""+ IdGenerator.INSTANCE.newID() +"\">"+
					"	<name>Folder1</name>"+
					"	<Placemark xml:id=\""+IdGenerator.INSTANCE.newID()+"\">"+
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
		String xmlID1 = content.getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		Assert.assertEquals("msn_ylw-pushpin_"+xmlID1, content.getPayload().attributeValue("id"));
		
		List<IContent> items = kmlAdapter.getAll();
		Assert.assertEquals(5, items.size());
		
		String newFolderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
							"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
							"<Document>"+
							"<name>a.kml</name>"+
							"	<StyleMap id=\"msn_NEW-pushpin_"+id+"\" xml:id=\""+id+"\">"+
							"		<Pair>"+
							"			<key>normal</key>"+
							"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
							"		</Pair>"+
							"		<Pair>"+
							"			<key>highlight</key>"+
							"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
							"		</Pair>"+
							"	</StyleMap>"+
							"</Document>"+
							"</kml>";
		
		Element newElement = DocumentHelper.parseText(newFolderXML).getRootElement().element("Document").element("StyleMap");				
		content = new KMLContent(newElement, id);
		kmlAdapter.save(content);
		
		content = kmlAdapter.get(id);
		Assert.assertNotNull(content);
		xmlID1 = content.getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		Assert.assertEquals("msn_NEW-pushpin_"+xmlID1, content.getPayload().attributeValue("id"));
		
		items = kmlAdapter.getAll();
		Assert.assertEquals(5, items.size());
	}
		
	@Test
	public void shouldGetStyleMapAsItem(){
		String id = IdGenerator.INSTANCE.newID();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>a.kml</name>"+
					"	<StyleMap id=\"msn_ylw-pushpin_"+id+"\" xml:id=\""+id+"\">"+
					"		<Pair>"+
					"			<key>normal</key>"+
					"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
					"		</Pair>"+
					"		<Pair>"+
					"			<key>highlight</key>"+
					"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
					"		</Pair>"+
					"	</StyleMap>"+
					"<Folder xml:id=\""+ IdGenerator.INSTANCE.newID() +"\">"+
					"	<name>Folder1</name>"+
					"	<Placemark xml:id=\""+IdGenerator.INSTANCE.newID()+"\">"+
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
		String xmlID = content.getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		Assert.assertEquals("msn_ylw-pushpin_"+xmlID, content.getPayload().attributeValue("id"));

	}
	
	@Test
	public void shouldReturnsStyleMapsAsItems(){
		
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"	<name>a.kml</name>"+
					"	<StyleMap id=\"msn_ylw-pushpin\">"+
					"		<Pair>"+
					"			<key>normal</key>"+
					"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
					"		</Pair>"+
					"		<Pair>"+
					"			<key>highlight</key>"+
					"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
					"		</Pair>"+
					"	</StyleMap>"+
					"	<StyleMap id=\"msh_ylw-pushpin\">"+
					"		<Pair>"+
					"			<key>normal</key>"+
					"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
					"		</Pair>"+
					"		<Pair>"+
					"			<key>highlight</key>"+
					"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
					"		</Pair>"+
					"	</StyleMap>"+
					"</Document>"+
					"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
		List<IContent> items = kmlAdapter.getAll();
		
		Assert.assertEquals(2, items.size());
		String xmlID1 = items.get(0).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		String xmlID2 = items.get(1).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		
		Assert.assertEquals("msn_ylw-pushpin_"+xmlID1, items.get(0).getPayload().attributeValue("id"));
		Assert.assertEquals("msh_ylw-pushpin_"+xmlID2, items.get(1).getPayload().attributeValue("id"));
	}
	
	@Test
	public void shouldAddNewStyleMap() throws DocumentException{
		
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"	<name>a.kml</name>"+
			"	<StyleMap id=\"msn_ylw-pushpin\">"+
			"		<Pair>"+
			"			<key>normal</key>"+
			"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"		<Pair>"+
			"			<key>highlight</key>"+
			"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"	</StyleMap>"+
			"	<StyleMap id=\"msh_ylw-pushpin\">"+
			"		<Pair>"+
			"			<key>normal</key>"+
			"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"		<Pair>"+
			"			<key>highlight</key>"+
			"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"	</StyleMap>"+
			"</Document>"+
			"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
		
		String id = IdGenerator.INSTANCE.newID();
		
		String newXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"	<name>a.kml</name>"+
			"	<StyleMap id=\"new_"+id+"\" xml:id=\""+id+"\">"+
			"		<Pair>"+
			"			<key>normal</key>"+
			"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"		<Pair>"+
			"			<key>highlight</key>"+
			"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"	</StyleMap>"+
			"</Document>"+
			"</kml>";
			
		Element newElement = DocumentHelper.parseText(newXML).getRootElement().element("Document").element("StyleMap");				
		KMLContent content = new KMLContent(newElement, id);
		kmlAdapter.save(content);
		
		KMLContent addedContent = kmlAdapter.get(id);
		Assert.assertNotNull(addedContent);
		
		Element addedStyleMap = addedContent.getPayload();
		String xmlID = addedStyleMap.attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		String kmlID = addedStyleMap.attributeValue("id");
		Assert.assertEquals("new_"+id, kmlID);
		Assert.assertEquals(id, xmlID);
		
		List<IContent> items = kmlAdapter.getAll();
		Assert.assertEquals(3, items.size());
		
		addedStyleMap = items.get(2).getPayload();
		xmlID = addedStyleMap.attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		kmlID = addedStyleMap.attributeValue("id");
		Assert.assertEquals("new_"+id, kmlID);
		Assert.assertEquals(id, xmlID);
				
		String xmlID1 = items.get(0).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		String xmlID2 = items.get(1).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);		
		Assert.assertEquals("msn_ylw-pushpin_"+xmlID1, items.get(0).getPayload().attributeValue("id"));
		Assert.assertEquals("msh_ylw-pushpin_"+xmlID2, items.get(1).getPayload().attributeValue("id"));

	}
	
	@Test
	public void shouldDeleteStyleMap() throws DocumentException{
		
		String id = IdGenerator.INSTANCE.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"	<name>a.kml</name>"+
			"	<StyleMap id=\"msn_ylw-pushpin_"+id+"\" xml:id=\""+id+"\">"+
			"		<Pair>"+
			"			<key>normal</key>"+
			"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"		<Pair>"+
			"			<key>highlight</key>"+
			"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"	</StyleMap>"+
			"	<StyleMap id=\"msh_ylw-pushpin\">"+
			"		<Pair>"+
			"			<key>normal</key>"+
			"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"		<Pair>"+
			"			<key>highlight</key>"+
			"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"	</StyleMap>"+
			"</Document>"+
			"</kml>";
		File file = TestHelper.makeNewXMLFile(xml);
		
		KMLContentAdapter kmlAdapter = new KMLContentAdapter(file);
				
		List<IContent> items = kmlAdapter.getAll();
		KMLContent content = (KMLContent)items.get(0);
		
		Assert.assertEquals(2, items.size());
		String xmlID1 = items.get(0).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		String xmlID2 = items.get(1).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals("msn_ylw-pushpin_"+xmlID1, items.get(0).getPayload().attributeValue("id"));
		Assert.assertEquals("msh_ylw-pushpin_"+xmlID2, items.get(1).getPayload().attributeValue("id"));
		
		kmlAdapter.delete(content);
		
		items = kmlAdapter.getAll();
		Assert.assertEquals(1, items.size());
		Assert.assertFalse(id.equals(xmlID2));
		xmlID2 = items.get(0).getPayload().attributeValue(KMLContentAdapterNames.XML_ID_QNAME);
		Assert.assertEquals("msh_ylw-pushpin_"+xmlID2, items.get(0).getPayload().attributeValue("id"));

	}
	
	@SuppressWarnings({ "unchecked" })
	@Test
	public void shouldPrepareKML() throws DocumentException, JaxenException{
		
		String id = IdGenerator.INSTANCE.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<StyleMap id=\"msn_ylw-pushpin_"+id+"\" xml:id=\""+id+"\">"+
		"		<Pair>"+
		"			<key>normal</key>"+
		"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
		"		</Pair>"+
		"		<Pair>"+
		"			<key>highlight</key>"+
		"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
		"		</Pair>"+
		"	</StyleMap>"+
		"	<StyleMap id=\"msh_ylw-pushpin\">"+
		"		<Pair>"+
		"			<key>normal</key>"+
		"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
		"		</Pair>"+
		"		<Pair>"+
		"			<key>highlight</key>"+
		"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
		"		</Pair>"+
		"	</StyleMap>"+
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
		Assert.assertEquals("msn_ylw-pushpin_"+xmlID1, elements.get(0).attributeValue("id"));
		Assert.assertEquals("msh_ylw-pushpin_"+xmlID2, elements.get(1).attributeValue("id"));

		HashSet<String> ids = new HashSet<String>();		
		ids.add(xmlID1);
		ids.add(xmlID2);
		Assert.assertEquals(2, ids.size());
	}
	
	@Test
	public void shouldNormalizeContent() throws DocumentException{
		
		String id = IdGenerator.INSTANCE.newID();
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"	<name>a.kml</name>"+
			"	<StyleMap id=\"msn_ylw-pushpin_"+id+"\" xml:id=\""+id+"\">"+
			"		<Pair>"+
			"			<key>normal</key>"+
			"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"		<Pair>"+
			"			<key>highlight</key>"+
			"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
			"		</Pair>"+
			"	</StyleMap>"+
			"</Document>"+
			"</kml>";
		
		Element newElement = DocumentHelper.parseText(xml).getRootElement().element("Document").element("StyleMap");				
		KMLContent content = new KMLContent(newElement, id);
		
		KMLContent normalizedContent = KMLContent.normalizeContent(content, KMLContentAdapter.XML_VIEW);
		Assert.assertEquals(content, normalizedContent);
	}
}
