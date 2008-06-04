package com.mesh4j.sync.adapters.kml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Test;

import com.mesh4j.sync.utils.XMLHelper;


public class MeshKMLParserTest {

	@Test
	public void  should() throws DocumentException{
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			"<Style>"+
				"<name>dsds</name>"+
			"</Style>"+
			"<PhotoOverlay>"+
				"<Style>"+
					"<name>vwevjbvc</name>"+
				"</Style>"+
			"</PhotoOverlay>"+
			"</Document>"+
			"</kml>";
		
		Document doc = DocumentHelper.parseText(xml);
		
		System.out.println(XMLHelper.selectElements("kml:Style", doc.getRootElement().element("Document"), KMLViewElement.SEARCH_NAMESPACES).size());
		System.out.println(XMLHelper.selectElements("//kml:Style", doc.getRootElement().element("Document"), KMLViewElement.SEARCH_NAMESPACES).size());
		
	}
	
	
	// TODO (JMT) Migrate to KMLMeshDOMLoaderTests
//	private static final String xml = 
//		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
//		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
//		"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
//		"<name>dummy</name>"+
//	   	"<ExtendedData>"+
//		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
//      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
//      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
//      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
//      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//     	"</sx:sync>"+
//		"</mesh4x:sync>"+
//      	"</ExtendedData>"+
//		"<Placemark mesh4x:id=\"1\">"+
//		"<name>B</name>"+
//		"</Placemark>"+
//		"</Document>"+
//		"</kml>";
//	
//	private static final String xmlWithoutMesh = 
//		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
//		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
//		"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
//		"<name>dummy</name>"+
//		"<Placemark>"+
//		"<name>B</name>"+
//		"</Placemark>"+
//		"</Document>"+
//		"</kml>";
//	
//	private static final String xmlWithHierarchy = 
//		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
//		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
//		"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
//		"<name>dummy</name>"+
//	   	"<ExtendedData>"+
//		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
//      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
//      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
//      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
//      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//     	"</sx:sync>"+
//		"</mesh4x:sync>"+	   	
//		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
//      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
//      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
//      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//     	"</sx:sync>"+
//		"</mesh4x:sync>"+
//		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//      	"<sx:sync id=\"3\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
//      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
//      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
//      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//     	"</sx:sync>"+
//		"</mesh4x:sync>"+
//		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//      	"<sx:sync id=\"4\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
//      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
//      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
//      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//     	"</sx:sync>"+
//		"</mesh4x:sync>"+	
//		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//      	"<sx:sync id=\"5\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
//      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
//      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
//      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//     	"</sx:sync>"+
//		"</mesh4x:sync>"+	
//		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//      	"<sx:sync id=\"6\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
//      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
//      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
//      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//     	"</sx:sync>"+
//		"</mesh4x:sync>"+	
//      	"</ExtendedData>"+      	
//		"<StyleMap id=\"msn_ylw-pushpin_4\" mesh4x:id=\"4\">"+
//		"	<Pair>"+
//		"		<key>normal</key>"+
//		"		<styleUrl>#sn_ylw-pushpin</styleUrl>"+
//		"	</Pair>"+
//		"	<Pair>"+
//		"		<key>highlight</key>"+
//		"		<styleUrl>#sh_ylw-pushpin</styleUrl>"+
//		"	</Pair>"+
//		"</StyleMap>"+
//		"<Style id=\"sn_ylw-pushpin_5\" mesh4x:id=\"5\">"+
//		"	<IconStyle>"+
//		"		<color>ff00ff55</color>"+
//		"		<scale>1.1</scale>"+
//		"		<Icon>"+
//		"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
//		"		</Icon>"+
//		"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
//		"	</IconStyle>"+
//		"	<LabelStyle>"+
//		"		<color>ff00ff55</color>"+
//		"	</LabelStyle>"+
//		"</Style>"+	
//		"<Style id=\"sn_ylw-pushpin_6\" mesh4x:id=\"6\">"+
//		"	<IconStyle>"+
//		"		<color>ff00ff55</color>"+
//		"		<scale>1.1</scale>"+
//		"		<Icon>"+
//		"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
//		"		</Icon>"+
//		"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
//		"	</IconStyle>"+
//		"	<LabelStyle>"+
//		"		<color>ff00ff55</color>"+
//		"	</LabelStyle>"+
//		"</Style>"+	
//      	"<Folder mesh4x:id=\"1\">"+
//      	"	<name>Folder1</name>"+
//      	"	<Folder mesh4x:id=\"2\">"+
//      	"		<name>Folder2</name>"+
//		"		<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"2\">"+
//		"			<name>B</name>"+
//		"		</Placemark>"+
//		"	</Folder>"+
//		"</Folder>"+
//		"</Document>"+
//		"</kml>";
//	
//	private static final String kmlAsXML = 
//		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
//		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
//		"<Document>"+
//		"	<name>dummy</name>"+
//		"</Document>"+
//		"</kml>";
//	
//	//prepateSyncRepository(Element)
//	@Test
//	public void shouldInitializeSyncRepository() throws DocumentException{
//		
//		Document kmlDocument = DocumentHelper.parseText(kmlAsXML);
//		Element documentElement = kmlDocument.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
//		Assert.assertNotNull(documentElement);
//		Element extendedData = documentElement.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
//		Assert.assertNull(extendedData);
//		
//		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
//		parser.prepateSyncRepository(documentElement);
//		
//		extendedData = documentElement.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
//		Assert.assertNotNull(extendedData);
//		Assert.assertNotNull(extendedData.getNamespaceForPrefix(KmlNames.MESH_PREFIX));
//	}
//	
//	//prepateSyncRepository(Element) + refresh(Element, Element, SyncInfo)
//	@SuppressWarnings("unchecked")
//	@Test
//	public void shouldInitializeMeshData() throws DocumentException{
//		
//		Document kmlDocument = DocumentHelper.parseText(kmlAsXML);
//		Element documentElement = kmlDocument.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
//
//		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
//		parser.prepateSyncRepository(documentElement);
//		Element extendedData = documentElement.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
//		Assert.assertNotNull(extendedData);
//		Assert.assertNotNull(extendedData.getNamespaceForPrefix(KmlNames.MESH_PREFIX));
//
//		// make new element
//		Element newElement = documentElement.addElement(KmlNames.KML_ELEMENT_PLACEMARK);
//		String uid = IdGenerator.newID();
//		Sync sync = new Sync(uid, "jmt", TestHelper.now(), false);
//		int version =  newElement.asXML().hashCode();
//		SyncInfo syncInfo = new SyncInfo(sync, "kml", uid, version);
//		parser.refresh(documentElement, newElement, syncInfo);
//		
//		// verify newElement
//		String syncID = newElement.attributeValue(KmlNames.MESH_QNAME_SYNC_ID);
//		Assert.assertNotNull(syncID);
//		Assert.assertEquals(uid, syncID);
//		
//		// verify sync repository mesh4x:sync
//		List<Element> meshElements = extendedData.elements(KmlNames.MESH_QNAME_SYNC);
//		Assert.assertNotNull(meshElements);
//		Assert.assertEquals(1, meshElements.size());
//
//		Element meshElement = meshElements.get(0);
//		
//		Attribute parentIDAttr = meshElement.attribute(KmlNames.MESH_QNAME_PARENT_ID);
//		Assert.assertNull(parentIDAttr);	
//
//		Attribute versionAttr = meshElement.attribute(KmlNames.MESH_VERSION);
//		Assert.assertNotNull(versionAttr);
//		Assert.assertEquals(version, Integer.valueOf(versionAttr.getValue()));
//		
//		// verify sync repository sx:sync
//		Element syncElemet = meshElement.element(ISyndicationFormat.SX_QNAME_SYNC);
//		Assert.assertNotNull(syncElemet);
//		
//		String id = syncElemet.attributeValue(ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID);
//		Assert.assertNotNull(id);
//		Assert.assertEquals(uid, syncID);
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void shouldReadMeshData() throws DocumentException, JaxenException{
//		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
//		
//		Document document = DocumentHelper.parseText(xml);
//		Element documentElement = document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
//		List<Element> elements = XMLHelper.selectElements("//kml:Placemark", documentElement, MeshKMLParser.SEARCH_NAMESPACES);
//		
//		parser.prepateSyncRepository(documentElement);
//		
//		for (Element element : elements) {
//			String syncID = parser.getMeshSyncId(element);
//			SyncInfo syncInfo = parser.getSyncInfo(documentElement, syncID);
//			parser.refresh(documentElement, element, syncInfo);	
//		}
//					
//		Assert.assertEquals(xml, document.asXML());
//		
//	}
//	
//	@Test
//	public void shoudRefreshHierrachy() throws DocumentException{
//
//		String localXML = 
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
//			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
//			"<Document>"+
//			"<name>dummy</name>"+
//			"<StyleMap id=\"msn_ylw-pushpin\">"+
//			"	<Pair>"+
//			"		<key>normal</key>"+
//			"		<styleUrl>#sn_ylw-pushpin</styleUrl>"+
//			"	</Pair>"+
//			"	<Pair>"+
//			"		<key>highlight</key>"+
//			"		<styleUrl>#sh_ylw-pushpin</styleUrl>"+
//			"	</Pair>"+
//			"</StyleMap>"+
//			"<Style id=\"sn_ylw-pushpin\">"+
//			"	<IconStyle>"+
//			"		<color>ff00ff55</color>"+
//			"		<scale>1.1</scale>"+
//			"		<Icon>"+
//			"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
//			"		</Icon>"+
//			"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
//			"	</IconStyle>"+
//			"	<LabelStyle>"+
//			"		<color>ff00ff55</color>"+
//			"	</LabelStyle>"+
//			"</Style>"+	
//			"<Style id=\"sn_ylw-pushpin\">"+
//			"	<IconStyle>"+
//			"		<color>ff00ff55</color>"+
//			"		<scale>1.1</scale>"+
//			"		<Icon>"+
//			"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
//			"		</Icon>"+
//			"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
//			"	</IconStyle>"+
//			"	<LabelStyle>"+
//			"		<color>ff00ff55</color>"+
//			"	</LabelStyle>"+
//			"</Style>"+	
//	      	"<Folder>"+
//	      	"	<name>Folder1</name>"+
//	      	"	<Folder>"+
//	      	"		<name>Folder2</name>"+
//			"		<Placemark>"+
//			"			<name>B</name>"+
//			"		</Placemark>"+
//			"	</Folder>"+
//			"</Folder>"+
//			"<Placemark>"+
//			"	<name>C</name>"+
//			"</Placemark>"+
//			"</Document>"+
//			"</kml>";
//		
//		Document document = DocumentHelper.parseText(localXML);
//		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
//		List<Element> elements = parser.getElementsToSync(document);
//		
//		Element syncRoot = document.getRootElement().element("Document");
//		parser.prepateSyncRepository(syncRoot);
//		
//		for (Element element : elements) {
//			String syncID = IdGenerator.newID();
//			Sync sync = new Sync(syncID, "jmt", TestHelper.now(), false);
//			SyncInfo syncInfo = new SyncInfo(sync, parser.getType(), syncID, element.asXML().hashCode());
//			parser.refresh(syncRoot, element, syncInfo);	
//		}
//					
//		List<SyncInfo> syncs = parser.getAllSyncs(syncRoot);
//		Assert.assertNotNull(syncs);
//		Assert.assertEquals(7, syncs.size());
//		
//		elements = parser.getElementsToSync(document);
//		for (Element element : elements) {
//			Assert.assertTrue(parser.isValid(syncRoot, element));
//		}
//	}
//	
//	@Test
//	public void shoudRefreshHierrachyMovePlacemark() throws DocumentException{
//
//		String localXML = 
//				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
//				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
//				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
//				"<name>dummy</name>"+
//			   	"<ExtendedData>"+
//				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
//		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
//		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//		     	"</sx:sync>"+
//				"</mesh4x:sync>"+	   	
//				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//		      	"<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
//		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//		     	"</sx:sync>"+
//				"</mesh4x:sync>"+
//				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//		      	"<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
//		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//		     	"</sx:sync>"+
//				"</mesh4x:sync>"+
//				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//		      	"<sx:sync id=\"4\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
//		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//		     	"</sx:sync>"+
//				"</mesh4x:sync>"+	
//				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
//		      	"<sx:sync id=\"5\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
//		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
//		     	"</sx:sync>"+
//				"</mesh4x:sync>"+					
//		      	"</ExtendedData>"+      	
//		      	"<Folder mesh4x:id=\"1\">"+
//		      	"	<name>Folder1</name>"+
//				"	<Placemark mesh4x:id=\"5\"  mesh4x:parentId=\"2\">"+
//				"		<name>D</name>"+
//				"	</Placemark>"+		      	
//		      	"	<Folder mesh4x:id=\"2\">"+
//		      	"		<name>Folder2</name>"+
//				"		<Placemark mesh4x:id=\"4\">"+
//				"			<name>C</name>"+
//				"		</Placemark>"+
//				"	</Folder>"+
//				"</Folder>"+
//				"<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"2\">"+
//				"	<name>B</name>"+
//				"</Placemark>"+
//				"</Document>"+
//				"</kml>";
//		
//		Document document = DocumentHelper.parseText(localXML);
//		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
//		List<Element> elements = parser.getElementsToSync(document);		
//		Element syncRoot = document.getRootElement().element("Document");
//		parser.prepateSyncRepository(syncRoot);
//
//		for (Element element : elements) {
//			String syncID = parser.getMeshSyncId(element);
//			SyncInfo syncInfo = parser.getSyncInfo(syncRoot, syncID);
//			syncInfo.getSync().update("JMT", TestHelper.now());
//			SyncInfo newSyncInfo = new SyncInfo(syncInfo.getSync(), parser.getType(), "JMT", element.asXML().hashCode());
//			
//			parser.refresh(syncRoot, element, newSyncInfo);
//			Assert.assertTrue(parser.isValid(syncRoot, element));
//
//		}
//				
//		Element element = parser.getElement(syncRoot, "4");
//		Assert.assertEquals("2", parser.getMeshParentId(element));
//		
//		Element elementParent = parser.getElementByMeshId(syncRoot, "2");
//		Assert.assertNotNull(elementParent);
//		Assert.assertEquals(elementParent, element.getParent());
//		
//		element = parser.getElement(syncRoot, "5");
//		Assert.assertEquals("1", parser.getMeshParentId(element));
//		
//		elementParent = parser.getElementByMeshId(syncRoot, "1");
//		Assert.assertNotNull(elementParent);
//		Assert.assertEquals(elementParent, element.getParent());
//		
//		element = parser.getElement(syncRoot, "3");
//		Assert.assertEquals(null, parser.getMeshParentId(element));
//	}
	
}