package com.mesh4j.sync.adapters.kml;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.dom.parsers.FileManager;
import com.mesh4j.sync.adapters.dom.parsers.HierarchyXMLViewElement;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;


public class KMLDOMTest {

	private static final String xml = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"<name>dummy</name>"+
	   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+
      	"</ExtendedData>"+
		"<Placemark xml:id=\"1\">"+
		"<name>B</name>"+
		"</Placemark>"+
		"</Document>"+
		"</kml>";
	
	private static final String xmlWithoutMesh = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"<name>dummy</name>"+
		"<Placemark>"+
		"<name>B</name>"+
		"</Placemark>"+
		"</Document>"+
		"</kml>";
	
	private static final String xmlWithHierarchy = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"<name>dummy</name>"+
	   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
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
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"+
      	"<sx:sync id=\"7\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+
		"<mesh4x:hierrachy xml:id=\"7\" mesh4x:parentId=\"2\" mesh4x:childId=\"3\"/>"+
      	"</ExtendedData>"+      	
		"<StyleMap id=\"msn_ylw-pushpin_4\" xml:id=\"4\">"+
		"	<Pair>"+
		"		<key>normal</key>"+
		"		<styleUrl>#sn_ylw-pushpin</styleUrl>"+
		"	</Pair>"+
		"	<Pair>"+
		"		<key>highlight</key>"+
		"		<styleUrl>#sh_ylw-pushpin</styleUrl>"+
		"	</Pair>"+
		"</StyleMap>"+
		"<Style id=\"sn_ylw-pushpin_5\" xml:id=\"5\">"+
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
		"<Style id=\"sn_ylw-pushpin_6\" xml:id=\"6\">"+
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
      	"<Folder xml:id=\"1\">"+
      	"	<name>Folder1</name>"+
      	"	<Folder xml:id=\"2\">"+
      	"		<name>Folder2</name>"+
		"		<Placemark xml:id=\"3\">"+
		"			<name>B</name>"+
		"		</Placemark>"+
		"	</Folder>"+
		"</Folder>"+
		"</Document>"+
		"</kml>";
	
	
	// Constructor
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullDocument() {
		Document doc = null;
		new KMLDOM(doc, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createView(new FileManager()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullName() {
		String name = null;
		new KMLDOM(name, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createView(new FileManager()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptEmptyName() {
		String name = "";
		new KMLDOM(name, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createView(new FileManager()));
	}

	private KMLDOM createDOM(Document document) {
		FileManager fileManager = new FileManager();
		return new KMLDOM(document, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createView(fileManager));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullIdentityProvider() {
		Document doc = DocumentHelper.createDocument();
		new KMLDOM(doc, null, DOMLoaderFactory.createView(new FileManager()));		
		new KMLDOM("myName", null, DOMLoaderFactory.createView(new FileManager()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullXMLView() {
		Document doc = DocumentHelper.createDocument();
		new KMLDOM(doc, NullIdentityProvider.INSTANCE, null);		
		new KMLDOM("myName", NullIdentityProvider.INSTANCE, null);
	}
	
	// getElement
	@Test
	public void shouldgetElementReturnNullWhenIDDoesNotExists() throws DocumentException{
		String id = IdGenerator.newID();
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		
		KMLDOM meshParser = createDOM(doc);

		Element result = meshParser.getElement(id);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldgetElementReturnElement() throws DocumentException{
		String id = "3";
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		
		KMLDOM meshParser = createDOM(doc);
		Element result = meshParser.getElement(id);
		Assert.assertNotNull(result);
		Assert.assertEquals("Placemark", result.getName());
	}
	
	// getMeshSyncId
	@Test
	public void shouldGetMeshSyncIdReturnsNullWhenAttributeDoesNotExist() throws DocumentException{
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		
		KMLDOM meshParser = createDOM(doc);
		
		Element element = DocumentHelper.createElement("payload");
		String result = meshParser.getMeshSyncId(element);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldGetMeshSyncId() throws DocumentException{

		Element placemark = DocumentHelper.parseText(xml).getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT).element(KmlNames.KML_ELEMENT_PLACEMARK);
		Document doc = DocumentHelper.parseText(xml);		
		KMLDOM meshParser = createDOM(doc);
		
		String result = meshParser.getMeshSyncId(placemark);
		Assert.assertNotNull(result);
		Assert.assertEquals("1", result);
	}
	
	// getMeshParentId
	@Test
	public void shouldGetMeshParentIdReturnsNullWhenAttributeDoesNotExist() throws DocumentException{
		Element element = DocumentHelper.createElement("payload");
		
		Document doc = DocumentHelper.parseText(xml);		
	
		String result = HierarchyXMLViewElement.getMeshParentId(doc, element);
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
		
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);		
		
		String result = HierarchyXMLViewElement.getMeshParentId(doc, placemark);

		Assert.assertNotNull(result);
		Assert.assertEquals("2", result);
	}
	
	//getSyncInfo(Element, String)
	@Test
	public void shouldGetSyncInfoReturnsNullBecauseSyncDoesNotExist() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
				
		KMLDOM meshParser = createDOM(doc);
		SyncInfo syncInfo = meshParser.getSync("1");
		Assert.assertNull(syncInfo);
	}
	
	@Test
	public void shouldGetSyncInfo() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
				
		KMLDOM meshParser = createDOM(doc);
		SyncInfo syncInfo = meshParser.getSync("2");
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
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		      	"</ExtendedData>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
						
		KMLDOM meshParser = createDOM(doc);
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
	}
	
	@Test
	public void shouldGetAllSyncsReturnsEmptyListBecauseSyncRepoDoesNotExist() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
				
		KMLDOM meshParser = createDOM(doc);
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
	}
	
	@Test
	public void shouldGetAllSyncs() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
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
				"<Placemark xml:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
				
		KMLDOM meshParser = createDOM(doc);
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
	}
	
	//refreshSyncInfo(Element, SyncInfo)
	
	@Test
	public void shouldRefreshSyncAddMesh() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
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
				"<Placemark xml:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		KMLDOM meshParser = createDOM(doc);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
		
		Sync sync = new Sync("3", "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", "3", 1);
		meshParser.updateSync(syncInfo);
		
		syncInfos = meshParser.getAllSyncs();
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
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
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
				"<Placemark xml:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		KMLDOM meshParser = createDOM(doc);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
		
		Sync sync = new Sync("2", "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", "2", 3);
		meshParser.updateSync(syncInfo);
		
		syncInfos = meshParser.getAllSyncs();
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
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		      	"</ExtendedData>"+
				"<name>example</name>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
				
		KMLDOM meshParser = createDOM(doc);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(0, syncInfos.size());
		
		String syncID = "1";
		Sync sync = new Sync(syncID, "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", syncID, 1);

		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
				"<Placemark xml:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		meshParser.addElement(newElement.createCopy());
		meshParser.updateSync(syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(doc, element));
		
		syncInfo = meshParser.getSync(syncID);
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
				"<Document>"+
				"<name>example</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder xml:id=\"1\" >"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
			
		KMLDOM meshParser = createDOM(doc);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(1, syncInfos.size());
		
		String syncID = "3";
		Sync sync = new Sync(syncID, "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", syncID, 1);

		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
				"<Placemark xml:id=\"3\">"+
				"<name>C</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		meshParser.addElement(newElement.createCopy());
		meshParser.updateSync(syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(doc, element));
		
		syncInfo = meshParser.getSync(syncID);
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
				"<Document>"+
				"<name>example</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder xml:id=\"1\" >"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		
		KMLDOM meshParser = createDOM(doc);
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(1, syncInfos.size());
		
		String syncID = "3";
		Sync sync = new Sync(syncID, "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", syncID, 1);
		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
				"<Placemark xml:id=\"3\">"+
				"<name>C</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		meshParser.addElement(newElement.createCopy());
		meshParser.updateSync(syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		
		Assert.assertEquals(null,HierarchyXMLViewElement.getMeshParentId(doc, element));
		Assert.assertEquals("Document", element.getParent().getName());
		
		String hierrachyXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
			"<mesh4x:hierarchy xmlns:mesh4x=\"http://mesh4x.org/kml\" xml:id=\"33\" mesh4x:parentId=\"1\" mesh4x:childId=\"3\"/>"+
			"</Document>"+
			"</kml>";
	
		Element hierrachyElement = DocumentHelper.parseText(hierrachyXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
	
		Sync hierrachySync = new Sync("33", "jmt", TestHelper.now(), true);
		SyncInfo hierrachySyncInfo = new SyncInfo(hierrachySync, "kml", "33", 1);

		meshParser.addElement(hierrachyElement.createCopy());
		meshParser.updateSync(hierrachySyncInfo);
		
		element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals("1", HierarchyXMLViewElement.getMeshParentId(doc, element));
		Assert.assertEquals("Folder", element.getParent().getName());
		
		syncInfo = meshParser.getSync(syncID);
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals(syncID, syncInfo.getId());
		Assert.assertEquals(syncID, syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(1, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(sync.equals(syncInfo.getSync()));
		
		syncInfo = meshParser.getSync("33");
		Assert.assertNotNull(syncInfo);
		Assert.assertEquals("33", syncInfo.getId());
		Assert.assertEquals("33", syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(1, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(hierrachySync.equals(syncInfo.getSync()));
		
	}
	
	//updateElement(Element rootElement, Element newElement, SyncInfo syncInfo)
	
	@Test
	public void shouldUpdateElement() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>example</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
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
		      	"<Folder xml:id=\"1\" >"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
			
		KMLDOM meshParser = createDOM(doc);
		meshParser.updateMeshStatus();
		
		String syncID = "2";
		SyncInfo syncInfo = meshParser.getSync(syncID);
		Assert.assertNotNull(syncInfo);

		Sync sync = syncInfo.getSync();
		sync.update("jmt",  TestHelper.now());
		
		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
				"<Placemark xml:id=\"2\">"+
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
		
		meshParser.updateElement(newElement.createCopy());
		meshParser.updateSync(syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		
		Assert.assertEquals("1", HierarchyXMLViewElement.getMeshParentId(doc, element));
		Assert.assertEquals("Folder", element.getParent().getName());
		Assert.assertEquals("C", element.element("name").getText());
		
		syncInfo = meshParser.getSync(syncID);
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
				"<Document>"+
				"<name>example</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
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
				"<mesh4x:hierarchy xml:id=\"3\" mesh4x:parentId=\"1\" mesh4x:childId=\"2\" />"+
		      	"</ExtendedData>"+
		      	"<Folder xml:id=\"1\" >"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);

		KMLDOM meshParser = createDOM(doc);
		meshParser.updateMeshStatus();
		
		String syncID = "2";
		SyncInfo syncInfo = meshParser.getSync(syncID);
		Assert.assertNotNull(syncInfo);

		String elementXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
				"<mesh4x:hierarchy xmlns:mesh4x=\"http://mesh4x.org/kml\" xml:id=\"3\" mesh4x:childId=\"2\" />"+
				"</Document>"+
				"</kml>";
		
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(MeshNames.MESH_QNAME_HIERARCHY);

		String hierarchySyncID = "3";
		SyncInfo hierarchySyncInfo = meshParser.getSync(hierarchySyncID);
		
		Sync hierarchySync = syncInfo.getSync();
		hierarchySync.update("jmt",  TestHelper.now());

		int version = newElement.asXML().hashCode();
		syncInfo = new SyncInfo(hierarchySync, "kml", hierarchySyncID, version);
		
		SyncInfo originalSyncInfo = meshParser.getSync(syncID);
		Assert.assertNotNull(originalSyncInfo);
		
		meshParser.updateElement(newElement);
		meshParser.updateSync(hierarchySyncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(doc, element));
		Assert.assertEquals("Document", element.getParent().getName());
		
		syncInfo = meshParser.getSync(syncID);
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals(originalSyncInfo.getId(), syncInfo.getId());
		Assert.assertEquals(originalSyncInfo.getSyncId(), syncInfo.getSyncId());
		Assert.assertEquals(originalSyncInfo.getType(), syncInfo.getType());
		Assert.assertEquals(originalSyncInfo.getVersion(), syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(originalSyncInfo.getSync().equals(syncInfo.getSync()));		
	}
	
	@Test
	public void shouldUpdateElementMoveFromFolderOneToFolderTwo() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>example</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
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
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"4\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
				"<mesh4x:hierarchy xml:id=\"4\" mesh4x:parentId=\"1\" mesh4x:childId=\"3\" />"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"5\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
				"<mesh4x:hierarchy xml:id=\"5\" mesh4x:childId=\"2\" />"+
		      	"</ExtendedData>"+
		      	"<Folder xml:id=\"1\" >"+
		      	"<name>FolderONE</name>"+
				"<Placemark xml:id=\"3\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"<Folder xml:id=\"2\" >"+
				"<name>FolderTwo</name>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		
		KMLDOM meshParser = createDOM(doc);

		SyncInfo syncInfo = meshParser.getSync("4");
		Assert.assertNotNull(syncInfo);

		Sync sync = syncInfo.getSync();
		sync.update("jmt",  TestHelper.now());
		
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
			"<mesh4x:hierarchy xmlns:mesh4x=\"http://mesh4x.org/kml\" xml:id=\"4\" mesh4x:parentId=\"2\"  mesh4x:childId=\"3\" />"+
			"</Document>"+
			"</kml>";
	
		Element newElement = DocumentHelper.parseText(elementXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		
	
		int version = newElement.asXML().hashCode();
		syncInfo = new SyncInfo(sync, "kml", "4", version);
		
		meshParser.updateElement(newElement.createCopy());
		meshParser.updateSync(syncInfo);
		
		Element element = meshParser.getElement("3");
		Assert.assertNotNull(element);
		
		Assert.assertEquals("2", HierarchyXMLViewElement.getMeshParentId(doc, element));
		Assert.assertEquals("Folder", element.getParent().getName());
		Assert.assertEquals("FolderTwo", element.getParent().element("name").getText());
		
		syncInfo = meshParser.getSync("4");
		Assert.assertNotNull(syncInfo);		
		Assert.assertEquals("4", syncInfo.getId());
		Assert.assertEquals("4", syncInfo.getSyncId());
		Assert.assertEquals("kml", syncInfo.getType());
		Assert.assertEquals(version, syncInfo.getVersion());
		Assert.assertNotNull(syncInfo.getSync());
		Assert.assertTrue(sync.equals(syncInfo.getSync()));		
	}
	
	// getElementsToSync
	@Test
	public void shouldGetElementsToSync() throws DocumentException{
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		KMLDOM meshParser = createDOM(doc);
		List<Element> elements = meshParser.getAllElements();
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(6, elements.size());

		Assert.assertEquals("Style", elements.get(0).getName());
		Assert.assertEquals("5", meshParser.getMeshSyncId(elements.get(0)));
		Assert.assertEquals("Style", elements.get(1).getName());
		Assert.assertEquals("6", meshParser.getMeshSyncId(elements.get(1)));
		Assert.assertEquals("StyleMap", elements.get(2).getName());
		Assert.assertEquals("4", meshParser.getMeshSyncId(elements.get(2)));
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
		Document doc = DocumentHelper.createDocument();
		KMLDOM meshParser = createDOM(doc);
		Assert.assertEquals(KmlNames.KML_PREFIX, meshParser.getType());
	}
	
	// normalize
	@Test
	public void shouldNormalizeStyleMap() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
			"<StyleMap id=\"msn_ylw-pushpin_4\" xml:id=\"4\">"+
			"	<Pair>"+
			"		<key>normal</key>"+
			"		<styleUrl>#sn_ylw-pushpin</styleUrl>"+
			"	</Pair>"+
			"	<Pair>"+
			"		<key>highlight</key>"+
			"		<styleUrl>#sh_ylw-pushpin</styleUrl>"+
			"	</Pair>"+
			"</StyleMap>"+
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_STYLE_MAP);
		
		KMLDOM meshParser = createDOM(doc);
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);		
	}
	
	@Test
	public void shouldNormalizeStyle() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
			"<Style id=\"sn_ylw-pushpin_5\" xml:id=\"5\">"+
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
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_STYLE);
		
		KMLDOM meshParser = createDOM(doc);
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);		
	}
	
	@Test
	public void shouldNormalizePhotoOverlay() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
			"<PhotoOverlay>"+
			"<name>MyPhoto</name>"+
			"<visibility>0</visibility>"+
			"<description>Mi foto</description>"+
			"<Camera>"+
				"<longitude>-95.26548319399994</longitude>"+
				"<latitude>38.95938957100002</latitude>"+
				"<altitude>705.5462440393095</altitude>"+
				"<heading>-2.385416011097637e-015</heading>"+
				"<tilt>0</tilt>"+
				"<roll>-3.975693351829396e-016</roll>"+
			"</Camera>"+
			"<Style>"+
				"<IconStyle>"+
					"<Icon>"+
						"<href>files/camera_mode.png</href>"+
					"</Icon>"+
				"</IconStyle>"+
				"<ListStyle>"+
					"<listItemType>check</listItemType>"+
					"<ItemIcon>"+
						"<state>open closed error fetching0 fetching1 fetching2</state>"+
						"<href>http://maps.google.com/mapfiles/kml/shapes/camera-lv.png</href>"+
					"</ItemIcon>"+
					"<bgColor>00ffffff</bgColor>"+
				"</ListStyle>"+
			"</Style>"+
			"<Icon>"+
				"<href>files/star.jpg</href>"+
			"</Icon>"+
			"<ViewVolume>"+
				"<leftFov>-25.005</leftFov>"+
				"<rightFov>25.005</rightFov>"+
				"<bottomFov>-19.23</bottomFov>"+
				"<topFov>19.23</topFov>"+
				"<near>57.908</near>"+
			"</ViewVolume>"+
			"<Point>"+
				"<altitudeMode>relativeToGround</altitudeMode>"+
				"<coordinates>-95.26548319399993,38.95938957100002,705.5462440393095</coordinates>"+
				"</Point>"+
			"</PhotoOverlay>"+
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PHOTO_OVERLAY);
		
		KMLDOM meshParser = createDOM(doc);
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);		
	}
	
	@Test
	public void shouldNormalizeGroundOverlay() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
			+"<name>dummy</name>"
			+"<GroundOverlay>"
				+"<name>Untitled Image Overlay</name>"
				+"<visibility>0</visibility>"
				+"<Icon>"
					+"<href>KmlWeb/star.jpg</href>"
					+"<viewBoundScale>0.75</viewBoundScale>"
				+"</Icon>"
				+"<LatLonBox>"
					+"<north>55.54920981373157</north>"
					+"<south>2.039978829726834</south>"
					+"<east>-54.44883367420367</east>"
					+"<west>-136.0821327137962</west>"
				+"</LatLonBox>"
			+"</GroundOverlay>"
			+"</Document>"
			+"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_GROUND_OVERLAY);
		
		KMLDOM meshParser = createDOM(doc);
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);		
	}
	
	public void shouldNormalizeSchema() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
			+"<name>dummy</name>"
			+"<Schema name=\"TrailHeadType\" id=\"TrailHeadTypeId\">"     
			+"<SimpleField type=\"string\" name=\"TrailHeadName\">"
			+"  <displayName><![CDATA[<b>Trail Head Name</b>]]></displayName>"     
			+"</SimpleField>"
			+"<SimpleField type=\"double\" name=\"TrailLength\">"       
			+"  <displayName><![CDATA[<i>Length in miles</i>]]></displayName>"     
			+"</SimpleField>"
			+"<SimpleField type=\"int\" name=\"ElevationGain\">"       
			+"  <displayName><![CDATA[<i>Change in altitude</i>]]></displayName>"     
			+"</SimpleField>"
			+"</Schema>"
			+"</Document>"
			+"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_SCHEMA);
		
		KMLDOM meshParser = createDOM(doc);
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);
		
		XMLHelper.canonicalizeXML(element);
	}
	
	@Test
	public void shouldNormalizeFolderLevelRoot() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
	      	"<Folder xml:id=\"1\">"+
	      	"	<name>Folder1</name>"+
	      	"	<Folder xml:id=\"2\">"+
	      	"		<name>Folder2</name>"+
			"		<Placemark xml:id=\"3\">"+
			"			<name>B</name>"+
			"		</Placemark>"+
			"	</Folder>"+
			"</Folder>"+
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER);
		
		KMLDOM meshParser = createDOM(doc);
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertNotSame(element, normalizedElement);
		Assert.assertEquals(1, normalizedElement.elements().size());
		Assert.assertEquals("Folder1", normalizedElement.element("name").getText());
		Assert.assertEquals("1", meshParser.getMeshSyncId(normalizedElement));
		
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(doc, normalizedElement));
	}
	
	@Test
	public void shouldNormalizeFolderLevel1() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
	      	"<Folder xml:id=\"1\">"+
	      	"	<name>Folder1</name>"+
	      	"	<Folder xml:id=\"2\">"+
	      	"		<name>Folder2</name>"+
			"		<Placemark xml:id=\"3\">"+
			"			<name>B</name>"+
			"		</Placemark>"+
			"	</Folder>"+
			"</Folder>"+
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		
		KMLDOM meshParser = createDOM(doc);
		meshParser.updateMeshStatus();
		
		Element element = doc
		.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_FOLDER);
		
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertNotSame(element, normalizedElement);
		Assert.assertEquals(1, normalizedElement.elements().size());
		Assert.assertEquals("Folder2", normalizedElement.element("name").getText());
		Assert.assertEquals("2", meshParser.getMeshSyncId(normalizedElement));
		
		Assert.assertEquals("1", HierarchyXMLViewElement.getMeshParentId(doc, normalizedElement));
		
	}
	
	@Test
	public void shouldNormalizePlacemark() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
			"<Placemark xml:id=\"3\">"+
			"<name>C</name>"+
			"</Placemark>"+
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		KMLDOM meshParser = createDOM(doc);
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);	
	}
	
	@Test
	public void shouldNormalizeReturnNull(){
		Document doc = DocumentHelper.createDocument();
		KMLDOM meshParser = createDOM(doc);
		Assert.assertNull(meshParser.normalize(null));
	}
	
	@Test
	public void shouldNormalizeReturnsNullBecauseNoXMLViewIsDefinedForElement(){
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("kml"));
		Element element = doc.getRootElement().addElement("NewElement");
		element.add(KmlNames.KML_NS);
		
		KMLDOM meshParser = createDOM(doc);
		Assert.assertNull(meshParser.normalize(element));
	}

	// removeElemet
	@Test
	public void shouldRemoveElement() throws DocumentException{
		
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<name>dummy</name>"+
			"<Placemark xml:id=\"3\">"+
			"<name>C</name>"+
			"</Placemark>"+
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element rootElement = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT);
		
		String syncID = "3";
			
		KMLDOM meshParser = createDOM(doc);
	
		Assert.assertNotNull(rootElement.element("Placemark"));
		Assert.assertNotNull(syncID, meshParser.getMeshSyncId(rootElement.element("Placemark")));
		meshParser.deleteElement(syncID);
		Assert.assertNull(rootElement.element("Placemark"));
	}
	
	@Test
	public void shouldRemoveElementNoEffectBecauseItemDoesNotExist() throws DocumentException{
		Document doc = DocumentHelper.parseText(xml);
		
		KMLDOM meshParser = createDOM(doc);
		meshParser.deleteElement(IdGenerator.newID());
	}
	
	
	//isValid(Element, Element)
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasNotMeshID() throws DocumentException{
		Document doc = DocumentHelper.parseText(xmlWithoutMesh);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		KMLDOM dom = createDOM(doc);
		
		boolean isValid = dom.isValid(element);

		Assert.assertFalse(isValid);
	}
	
	@Test
	public void shouldIsValidReturnsFalseBecauseSyncRepositoryHasNotExist() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
				"<Placemark xml:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		KMLDOM dom = createDOM(doc);

		boolean isValid = dom.isValid(element);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasNotMeshInfoInSyncRepository() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		      	"</ExtendedData>"+
				"<Placemark xml:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		KMLDOM dom = createDOM(doc);

		boolean isValid = dom.isValid(element);
		Assert.assertFalse(isValid);
	}
	
	
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasInvalidSyncInfoInSyncRepository() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"eefdewf\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark xml:id=\"1\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		KMLDOM dom = createDOM(doc);

		boolean isValid = dom.isValid(element);
		Assert.assertFalse(isValid);
	}
	
	
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasNullParentIDButParentHasID() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder xml:id=\"1\">"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_FOLDER).element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		KMLDOM dom = createDOM(doc);

		boolean isValid = dom.isValid(element);
		Assert.assertFalse(isValid);
	}

	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasParentIDButParentHasNotID() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_FOLDER).element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		KMLDOM dom = createDOM(doc);

		boolean isValid = dom.isValid(element);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasParentIDButParentHasOtherID() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder xml:id=\"3\" >"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_FOLDER).element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		KMLDOM dom = createDOM(doc);

		boolean isValid = dom.isValid(element);
		Assert.assertFalse(isValid);
	}
	
	@Test
	public void shouldIsValidReturnsTrueParentIDAttributeEqualsParentElementMeshIDAttribute() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
		      	"<Folder xml:id=\"1\" >"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Folder>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		KMLDOM dom = createDOM(doc);
		dom.updateMeshStatus();
		
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_FOLDER).element(KmlNames.KML_ELEMENT_PLACEMARK);

		boolean isValid = dom.isValid(element);
		Assert.assertTrue(isValid);
	}
	
	
	@Test
	public void shouldIsValidReturnsTrue() throws DocumentException{
		 String localXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
				"<Document>"+
				"<name>dummy</name>"+
			   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			   	"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
		      	"<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
		      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		     	"</sx:sync>"+
				"</mesh4x:sync>"+
		      	"</ExtendedData>"+
				"<Placemark xml:id=\"2\">"+
				"<name>B</name>"+
				"</Placemark>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
		
		KMLDOM dom = createDOM(doc);
		dom.updateMeshStatus();
		
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		boolean isValid = dom.isValid(element);
		Assert.assertTrue(isValid);
	}
	
	
}