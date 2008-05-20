package com.mesh4j.sync.adapters.kml;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.MeshException;

public class MeshKMLParserTest {

	private static final String xml = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		"<name>dummy</name>"+
	   	"<ExtendedData>"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+
      	"</ExtendedData>"+
		"<Placemark mesh4x:id=\"1\">"+
		"<name>B</name>"+
		"</Placemark>"+
		"</Document>"+
		"</kml>";
	
	private static final String xmlWithoutMesh = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		"<name>dummy</name>"+
		"<Placemark>"+
		"<name>B</name>"+
		"</Placemark>"+
		"</Document>"+
		"</kml>";
	
	private static final String xmlWithHierarchy = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		"<name>dummy</name>"+
	   	"<ExtendedData>"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+	   	
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
      	"<sx:sync id=\"3\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
      	"<sx:sync id=\"4\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+	
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
      	"<sx:sync id=\"5\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+	
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
      	"<sx:sync id=\"6\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+	
      	"</ExtendedData>"+      	
		"<StyleMap id=\"msn_ylw-pushpin_4\" mesh4x:id=\"4\">"+
		"	<Pair>"+
		"		<key>normal</key>"+
		"		<styleUrl>#sn_ylw-pushpin</styleUrl>"+
		"	</Pair>"+
		"	<Pair>"+
		"		<key>highlight</key>"+
		"		<styleUrl>#sh_ylw-pushpin</styleUrl>"+
		"	</Pair>"+
		"</StyleMap>"+
		"<Style id=\"sn_ylw-pushpin_5\" mesh4x:id=\"5\">"+
		"	<IconStyle>"+
		"		<color>ff00ff55</color>"+
		"		<scale>1.1</scale>"+
		"		<Icon>"+
		"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
		"		</Icon>"+
		"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
		"	</IconStyle>"+
		"	<LabelStyle>"+
		"		<color>ff00ff55</color>"+
		"	</LabelStyle>"+
		"</Style>"+	
		"<Style id=\"sn_ylw-pushpin_6\" mesh4x:id=\"6\">"+
		"	<IconStyle>"+
		"		<color>ff00ff55</color>"+
		"		<scale>1.1</scale>"+
		"		<Icon>"+
		"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
		"		</Icon>"+
		"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
		"	</IconStyle>"+
		"	<LabelStyle>"+
		"		<color>ff00ff55</color>"+
		"	</LabelStyle>"+
		"</Style>"+	
      	"<Folder mesh4x:id=\"1\">"+
      	"	<name>Folder1</name>"+
      	"	<Folder mesh4x:id=\"2\">"+
      	"		<name>Folder2</name>"+
		"		<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"2\">"+
		"			<name>B</name>"+
		"		</Placemark>"+
		"	</Folder>"+
		"</Folder>"+
		"</Document>"+
		"</kml>";
	
	private static final String kmlAsXML = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>dummy</name>"+
		"</Document>"+
		"</kml>";
	
	//prepateSyncRepository(Element)
	@Test
	public void shouldInitializeSyncRepository() throws DocumentException{
		
		Document kmlDocument = DocumentHelper.parseText(kmlAsXML);
		Element documentElement = kmlDocument.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Assert.assertNotNull(documentElement);
		Element extendedData = documentElement.element(KmlNames.KML_EXTENDED_DATA_ELEMENT);
		Assert.assertNull(extendedData);
		
		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		parser.prepateSyncRepository(documentElement);
		
		extendedData = documentElement.element(KmlNames.KML_EXTENDED_DATA_ELEMENT);
		Assert.assertNotNull(extendedData);
		Assert.assertNotNull(extendedData.getNamespaceForPrefix(KmlNames.MESH_PREFIX));
	}
	
	//prepateSyncRepository(Element) + refresh(Element, Element, SyncInfo)
	@SuppressWarnings("unchecked")
	@Test
	public void shouldInitializeMeshData() throws DocumentException{
		
		Document kmlDocument = DocumentHelper.parseText(kmlAsXML);
		Element documentElement = kmlDocument.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);

		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		parser.prepateSyncRepository(documentElement);
		Element extendedData = documentElement.element(KmlNames.KML_EXTENDED_DATA_ELEMENT);
		Assert.assertNotNull(extendedData);
		Assert.assertNotNull(extendedData.getNamespaceForPrefix(KmlNames.MESH_PREFIX));

		// make new element
		Element newElement = documentElement.addElement(KmlNames.KML_ELEMENT_PLACEMARK);
		String uid = IdGenerator.newID();
		Sync sync = new Sync(uid, "jmt", TestHelper.now(), false);
		int version =  newElement.asXML().hashCode();
		SyncInfo syncInfo = new SyncInfo(sync, "kml", uid, version);
		parser.refresh(documentElement, newElement, syncInfo);
		
		// verify newElement
		String syncID = newElement.attributeValue(KmlNames.MESH_QNAME_SYNC_ID);
		Assert.assertNotNull(syncID);
		Assert.assertEquals(uid, syncID);
		
		// verify sync repository mesh4x:sync
		List<Element> meshElements = extendedData.elements(KmlNames.MESH_QNAME_SYNC);
		Assert.assertNotNull(meshElements);
		Assert.assertEquals(1, meshElements.size());

		Element meshElement = meshElements.get(0);
		
		Attribute parentIDAttr = meshElement.attribute(KmlNames.MESH_QNAME_PARENT_ID);
		Assert.assertNull(parentIDAttr);	

		Attribute versionAttr = meshElement.attribute(KmlNames.MESH_VERSION);
		Assert.assertNotNull(versionAttr);
		Assert.assertEquals(version, Integer.valueOf(versionAttr.getValue()));
		
		// verify sync repository sx:sync
		Element syncElemet = meshElement.element(ISyndicationFormat.SX_QNAME_SYNC);
		Assert.assertNotNull(syncElemet);
		
		String id = syncElemet.attributeValue(ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID);
		Assert.assertNotNull(id);
		Assert.assertEquals(uid, syncID);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldReadMeshData() throws DocumentException, JaxenException{
		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		Document document = DocumentHelper.parseText(xml);
		Element documentElement = document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		List<Element> elements = XMLHelper.selectElements("//kml:Placemark", documentElement, MeshKMLParser.SEARCH_NAMESPACES);
		
		parser.prepateSyncRepository(documentElement);
		
		for (Element element : elements) {
			String syncID = parser.getMeshSyncId(element);
			SyncInfo syncInfo = parser.getSyncInfo(documentElement, syncID);
			parser.refresh(documentElement, element, syncInfo);	
		}
					
		Assert.assertEquals(xml, document.asXML());
		
	}
	
	@Test
	public void shoudRefreshHierrachy() throws DocumentException{

		String localXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
			"<StyleMap id=\"msn_ylw-pushpin\">"+
			"	<Pair>"+
			"		<key>normal</key>"+
			"		<styleUrl>#sn_ylw-pushpin</styleUrl>"+
			"	</Pair>"+
			"	<Pair>"+
			"		<key>highlight</key>"+
			"		<styleUrl>#sh_ylw-pushpin</styleUrl>"+
			"	</Pair>"+
			"</StyleMap>"+
			"<Style id=\"sn_ylw-pushpin\">"+
			"	<IconStyle>"+
			"		<color>ff00ff55</color>"+
			"		<scale>1.1</scale>"+
			"		<Icon>"+
			"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
			"		</Icon>"+
			"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
			"	</IconStyle>"+
			"	<LabelStyle>"+
			"		<color>ff00ff55</color>"+
			"	</LabelStyle>"+
			"</Style>"+	
			"<Style id=\"sn_ylw-pushpin\">"+
			"	<IconStyle>"+
			"		<color>ff00ff55</color>"+
			"		<scale>1.1</scale>"+
			"		<Icon>"+
			"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
			"		</Icon>"+
			"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
			"	</IconStyle>"+
			"	<LabelStyle>"+
			"		<color>ff00ff55</color>"+
			"	</LabelStyle>"+
			"</Style>"+	
	      	"<Folder>"+
	      	"	<name>Folder1</name>"+
	      	"	<Folder>"+
	      	"		<name>Folder2</name>"+
			"		<Placemark>"+
			"			<name>B</name>"+
			"		</Placemark>"+
			"	</Folder>"+
			"</Folder>"+
			"<Placemark>"+
			"	<name>C</name>"+
			"</Placemark>"+
			"</Document>"+
			"</kml>";
		
		Document document = DocumentHelper.parseText(localXML);
		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		List<Element> elements = parser.getElementsToSync(document);
		
		Element syncRoot = document.getRootElement().element("Document");
		parser.prepateSyncRepository(syncRoot);
		
		for (Element element : elements) {
			String syncID = IdGenerator.newID();
			Sync sync = new Sync(syncID, "jmt", TestHelper.now(), false);
			SyncInfo syncInfo = new SyncInfo(sync, parser.getType(), syncID, element.asXML().hashCode());
			parser.refresh(syncRoot, element, syncInfo);	
		}
					
		List<SyncInfo> syncs = parser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncs);
		Assert.assertEquals(7, syncs.size());
		
		elements = parser.getElementsToSync(document);
		for (Element element : elements) {
			Assert.assertTrue(parser.isValid(syncRoot, element));
		}
	}
	
	@Test
	public void shoudRefreshHierrachyMovePlacemark() throws DocumentException{

		String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+	   	
				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
		      	"<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
		      	"<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
		      	"<sx:sync id=\"4\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+	
				"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
		      	"<sx:sync id=\"5\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+					
		      	"</ExtendedData>"+      	
		      	"<Folder mesh4x:id=\"1\">"+
		      	"	<name>Folder1</name>"+
				"	<Placemark mesh4x:id=\"5\"  mesh4x:parentId=\"2\">"+
				"		<name>D</name>"+
				"	</Placemark>"+		      	
		      	"	<Folder mesh4x:id=\"2\">"+
		      	"		<name>Folder2</name>"+
				"		<Placemark mesh4x:id=\"4\">"+
				"			<name>C</name>"+
				"		</Placemark>"+
				"	</Folder>"+
				"</Folder>"+
				"<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"2\">"+
				"	<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document document = DocumentHelper.parseText(localXML);
		MeshKMLParser parser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		List<Element> elements = parser.getElementsToSync(document);		
		Element syncRoot = document.getRootElement().element("Document");
		parser.prepateSyncRepository(syncRoot);

		for (Element element : elements) {
			String syncID = parser.getMeshSyncId(element);
			SyncInfo syncInfo = parser.getSyncInfo(syncRoot, syncID);
			syncInfo.getSync().update("JMT", TestHelper.now());
			SyncInfo newSyncInfo = new SyncInfo(syncInfo.getSync(), parser.getType(), "JMT", element.asXML().hashCode());
			
			parser.refresh(syncRoot, element, newSyncInfo);
			Assert.assertTrue(parser.isValid(syncRoot, element));

		}
				
		Element element = parser.getElementByMeshId(syncRoot, "4");
		Assert.assertEquals("2", parser.getMeshParentId(element));
		
		Element elementParent = parser.getElementByMeshId(syncRoot, "2");
		Assert.assertNotNull(elementParent);
		Assert.assertEquals(elementParent, element.getParent());
		
		element = parser.getElementByMeshId(syncRoot, "5");
		Assert.assertEquals("1", parser.getMeshParentId(element));
		
		elementParent = parser.getElementByMeshId(syncRoot, "1");
		Assert.assertNotNull(elementParent);
		Assert.assertEquals(elementParent, element.getParent());
		
		element = parser.getElementByMeshId(syncRoot, "3");
		Assert.assertEquals(null, parser.getMeshParentId(element));
	}
	
	// Constructor
	@Test(expected=IllegalArgumentException.class)
	public void shouldDoesNotAcceptNullSyndicationFormat() {
		new MeshKMLParser(null, NullSecurity.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldDoesNotAcceptNullSecurity() {
		new MeshKMLParser(AtomSyndicationFormat.INSTANCE, null);
	}
	
	// GetElementByMeshId
	@Test
	public void shouldGetElementByMeshIdReturnNullWhenIDDoesNotExists(){
		String id = "1";
		Element rootElement = DocumentHelper.createElement("payload");
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		Element result = meshParser.getElementByMeshId(rootElement, id);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldGetElementByMeshIdReturnElement() throws DocumentException{
		String id = "3";
		Element rootElement = DocumentHelper.parseText(xmlWithHierarchy).getRootElement();
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		Element result = meshParser.getElementByMeshId(rootElement, id);
		Assert.assertNotNull(result);
		Assert.assertEquals("Placemark", result.getName());
	}
	
	@Test(expected=MeshException.class)
	public void shouldGetElementByMeshIdThrowsMeshException(){
		String id = "\'2";
		Element rootElement = null;
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		meshParser.getElementByMeshId(rootElement, id);
	}
	
	// getMeshSyncId
	@Test
	public void shouldGetMeshSyncIdReturnsNullWhenAttributeDoesNotExist(){
		Element element = DocumentHelper.createElement("payload");
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		String result = meshParser.getMeshSyncId(element);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldGetMeshSyncId() throws DocumentException{
		Element placemark = DocumentHelper.parseText(xml).getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT).element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		String result = meshParser.getMeshSyncId(placemark);
		Assert.assertNotNull(result);
		Assert.assertEquals("1", result);
	}
	
	// getMeshParentId
	@Test
	public void shouldGetMeshParentIdReturnsNullWhenAttributeDoesNotExist(){
		Element element = DocumentHelper.createElement("payload");
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		String result = meshParser.getMeshParentId(element);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldGetMeshParentId() throws DocumentException{
		Element placemark = DocumentHelper.parseText(xmlWithHierarchy)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER)
			.element(KmlNames.KML_ELEMENT_FOLDER)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		String result = meshParser.getMeshParentId(placemark);
		Assert.assertNotNull(result);
		Assert.assertEquals("2", result);
	}
	
	//isValid(Element, Element)
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasNotMeshID() throws DocumentException{
		Document doc = DocumentHelper.parseText(xmlWithoutMesh);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void shouldIsValidReturnsFalseBecauseSyncRepositoryHasNotExist() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
				"<Placemark mesh4x:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasNotMeshInfoInSyncRepository() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertFalse(isValid);
	}
	
	
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasInvalidSyncInfoInSyncRepository() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"eefdewf\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertFalse(isValid);
	}
	
	
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasNullParentIDButParentHasID() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder mesh4x:id=\"1\">"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_FOLDER).element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertFalse(isValid);
	}

	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasParentIDButParentHasNotID() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder>"+
				"<Placemark mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_FOLDER).element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasParentIDButParentHasOtherID() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder mesh4x:id=\"3\" >"+
				"<Placemark mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_FOLDER).element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void shouldIsValidReturnsTrueParentIDAttributeEqualsParentElementMeshIDAttribute() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder mesh4x:id=\"1\" >"+
				"<Placemark mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_FOLDER).element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertTrue(isValid);
	}
	
	
	@Test
	public void shouldIsValidReturnsTrue() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		boolean isValid = meshParser.isValid(syncRoot, element);
		Assert.assertTrue(isValid);
	}
	
	//getSyncInfo(Element, String)
	@Test
	public void shouldGetSyncInfoReturnsNullBecauseSyncDoesNotExist() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		SyncInfo syncInfo = meshParser.getSyncInfo(syncRoot, "1");
		Assert.assertNull(syncInfo);
	}
	
	@Test
	public void shouldGetSyncInfo() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		SyncInfo syncInfo = meshParser.getSyncInfo(syncRoot, "2");
		Assert.assertNotNull(syncInfo);
		
		Assert.assertEquals("2", syncInfo.getId());
		Assert.assertEquals("2", syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(1, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		
		Assert.assertEquals("2", syncInfo.getSync().getId());
		Assert.assertEquals(3, syncInfo.getSync().getUpdates());
	}
	
	//getAllSyncs(Element)
	
	@Test
	public void shouldGetAllSyncsReturnsEmptyList() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		List<SyncInfo> syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
	}
	
	@Test
	public void shouldGetAllSyncsReturnsEmptyListBecauseSyncRepoDoesNotExist() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		List<SyncInfo> syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
	}
	
	@Test
	public void shouldGetAllSyncs() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		List<SyncInfo> syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
	}
	
	//refreshSyncInfo(Element, SyncInfo)
	
	@Test
	public void shouldRefreshSyncAddMesh() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
		
		Sync sync = new Sync("3", "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", "3", 1);
		meshParser.refreshSyncInfo(syncRoot, syncInfo);
		
		syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(3, syncInfos.size());
		
		syncInfo = syncInfos.get(2);
		Assert.assertEquals("3", syncInfo.getId());
		Assert.assertEquals("3", syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(1, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		
		Assert.assertTrue(sync.equals(syncInfo.getSync()));
		
	}
	
	@Test
	public void shouldRefreshSyncUpdateMesh() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark mesh4x:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
		
		Sync sync = new Sync("2", "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", "2", 3);
		meshParser.refreshSyncInfo(syncRoot, syncInfo);
		
		syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
		
		syncInfo = syncInfos.get(1);
		Assert.assertEquals("2", syncInfo.getId());
		Assert.assertEquals("2", syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(3, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		
		Assert.assertTrue(sync.equals(syncInfo.getSync()));
		
	}
		
	//addElement(Element, Element, SyncInfo)
	
	@Test 
	public void shouldAddElementToRoot() throws DocumentException{

		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
			   	"<ExtendedData>"+
		      	"</ExtendedData>"+
				"<name>example</name>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
		
		String syncID = "1";
		Sync sync = new Sync(syncID, "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", syncID, 1);

		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
				"<Placemark mesh4x:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		meshParser.addElement(syncRoot, newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElementByMeshId(syncRoot, syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals(null, meshParser.getMeshParentId(element));
		
		syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals(syncID, syncInfo.getId());
		Assert.assertEquals(syncID, syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(1, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(sync.equals(syncInfo.getSync()));
	}
	
	@Test 
	public void shouldAddElementToRootBecauseParentIDIsInvalid() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>example</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder mesh4x:id=\"1\" >"+
				"<Placemark mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(1, syncInfos.size());
		
		String syncID = "3";
		Sync sync = new Sync(syncID, "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", syncID, 1);

		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
				"<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"33\">"+
				"<name>C</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		meshParser.addElement(syncRoot, newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElementByMeshId(syncRoot, syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals(null, meshParser.getMeshParentId(element));
		
		syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals(syncID, syncInfo.getId());
		Assert.assertEquals(syncID, syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(1, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(sync.equals(syncInfo.getSync()));
	}
	
	@Test 
	public void shouldAddElementToParent() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>example</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder mesh4x:id=\"1\" >"+
				"<Placemark mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs(syncRoot);
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(1, syncInfos.size());
		
		String syncID = "3";
		Sync sync = new Sync(syncID, "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", syncID, 1);

		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
				"<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"1\">"+
				"<name>C</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		meshParser.addElement(syncRoot, newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElementByMeshId(syncRoot, syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals("1", meshParser.getMeshParentId(element));
		Assert.assertEquals("Folder", element.getParent().getName());
		
		syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals(syncID, syncInfo.getId());
		Assert.assertEquals(syncID, syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(1, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(sync.equals(syncInfo.getSync()));
		
	}

	//updateElement(Element rootElement, Element newElement, SyncInfo syncInfo)
	
	@Test
	public void shouldUpdateElement() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>example</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder mesh4x:id=\"1\" >"+
				"<Placemark mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);

		String syncID = "2";
		SyncInfo syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);

		Sync sync = syncInfo.getSync();
		sync.update("jmt",  TestHelper.now());
		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
				"<Placemark mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
				"<name>C</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);

		int version = newElement.asXML().hashCode();
		syncInfo = new SyncInfo(sync, "kml", syncID, version);
		
		meshParser.updateElement(syncRoot, newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElementByMeshId(syncRoot, syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals("1", meshParser.getMeshParentId(element));
		Assert.assertEquals("Folder", element.getParent().getName());
		Assert.assertEquals("C", element.element("name").getText());
		
		syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals(syncID, syncInfo.getId());
		Assert.assertEquals(syncID, syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(version, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(sync.equals(syncInfo.getSync()));		
	}

	@Test
	public void shouldUpdateElementMoveFromFolderToRoot() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>example</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder mesh4x:id=\"1\" >"+
				"<Placemark mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);

		String syncID = "2";
		SyncInfo syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);

		Sync sync = syncInfo.getSync();
		sync.update("jmt",  TestHelper.now());
		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
				"<Placemark mesh4x:id=\"2\">"+
				"<name>C</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);

		int version = newElement.asXML().hashCode();
		syncInfo = new SyncInfo(sync, "kml", syncID, version);
		
		meshParser.updateElement(syncRoot, newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElementByMeshId(syncRoot, syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals(null, meshParser.getMeshParentId(element));
		Assert.assertEquals("Document", element.getParent().getName());
		Assert.assertEquals("C", element.element("name").getText());
		
		syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals(syncID, syncInfo.getId());
		Assert.assertEquals(syncID, syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(version, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(sync.equals(syncInfo.getSync()));		
	}
	
	@Test
	public void shouldUpdateElementMoveFromFolderOneToFolderTwo() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>example</name>"+
			   	"<ExtendedData>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder mesh4x:id=\"1\" >"+
		      	"<name>FolderONE</name>"+
				"<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"<Folder mesh4x:id=\"2\" >"+
				"<name>FolderTwo</name>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);				
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);

		String syncID = "3";
		SyncInfo syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);

		Sync sync = syncInfo.getSync();
		sync.update("jmt",  TestHelper.now());
		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
				"<name>dummy</name>"+
				"<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"2\">"+
				"<name>C</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);

		int version = newElement.asXML().hashCode();
		syncInfo = new SyncInfo(sync, "kml", syncID, version);
		
		meshParser.updateElement(syncRoot, newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElementByMeshId(syncRoot, syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals("2", meshParser.getMeshParentId(element));
		Assert.assertEquals("Folder", element.getParent().getName());
		Assert.assertEquals("FolderTwo", element.getParent().element("name").getText());
		Assert.assertEquals("C", element.element("name").getText());
		
		syncInfo = meshParser.getSyncInfo(syncRoot, syncID);
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals(syncID, syncInfo.getId());
		Assert.assertEquals(syncID, syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(version, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(sync.equals(syncInfo.getSync()));		
	}
		
	// TODO (JMT) refresh references
	
	// getElementsToSync
	@Test
	public void shouldGetElementsToSync() throws DocumentException{
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		List<Element> elements = meshParser.getElementsToSync(doc);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(6, elements.size());
		
		Assert.assertEquals("StyleMap", elements.get(0).getName());
		Assert.assertEquals("4", meshParser.getMeshSyncId(elements.get(0)));
		Assert.assertEquals("Style", elements.get(1).getName());
		Assert.assertEquals("5", meshParser.getMeshSyncId(elements.get(1)));
		Assert.assertEquals("Style", elements.get(2).getName());
		Assert.assertEquals("6", meshParser.getMeshSyncId(elements.get(2)));
		Assert.assertEquals("Folder", elements.get(3).getName());
		Assert.assertEquals("1", meshParser.getMeshSyncId(elements.get(3)));
		Assert.assertEquals("Folder", elements.get(4).getName());
		Assert.assertEquals("2", meshParser.getMeshSyncId(elements.get(4)));
		Assert.assertEquals("Placemark", elements.get(5).getName());
		Assert.assertEquals("3", meshParser.getMeshSyncId(elements.get(5)));
	}

	//getType
	@Test
	public void shouldGetType(){
		MeshKMLParser meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		Assert.assertEquals(KmlNames.KML_PREFIX, meshParser.getType());
	}
}