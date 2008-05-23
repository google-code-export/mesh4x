package com.mesh4j.sync.adapters.kml;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;


public class KMLMeshDocumentTest {

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
	
	
	// Constructor
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullDocument() {
		Document doc = null;
		new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullName() {
		String name = null;
		new KMLMeshDocument(name, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptEmptyName() {
		String name = "";
		new KMLMeshDocument(name, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullIdentityProvider() {
		Document doc = DocumentHelper.createDocument();
		new KMLMeshDocument(doc, null, KMLMeshDOMLoaderFactory.getDefaultXMLView());		
		new KMLMeshDocument("myName", null, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullXMLView() {
		Document doc = DocumentHelper.createDocument();
		new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, null);		
		new KMLMeshDocument("myName", NullIdentityProvider.INSTANCE, null);
	}
	
	// getElement
	@Test
	public void shouldgetElementReturnNullWhenIDDoesNotExists() throws DocumentException{
		String id = IdGenerator.newID();
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());

		Element result = meshParser.getElement(id);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldgetElementReturnElement() throws DocumentException{
		String id = "3";
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Element result = meshParser.getElement(id);
		Assert.assertNotNull(result);
		Assert.assertEquals("Placemark", result.getName());
	}
	
	// getMeshSyncId
	@Test
	public void shouldGetMeshSyncIdReturnsNullWhenAttributeDoesNotExist() throws DocumentException{
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
		Element element = DocumentHelper.createElement("payload");
		String result = meshParser.getMeshSyncId(element);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldGetMeshSyncId() throws DocumentException{

		Element placemark = DocumentHelper.parseText(xml).getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT).element(KmlNames.KML_ELEMENT_PLACEMARK);
		Document doc = DocumentHelper.parseText(xml);		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
		String result = meshParser.getMeshSyncId(placemark);
		Assert.assertNotNull(result);
		Assert.assertEquals("1", result);
	}
	
	// getMeshParentId
	@Test
	public void shouldGetMeshParentIdReturnsNullWhenAttributeDoesNotExist() throws DocumentException{
		Element element = DocumentHelper.createElement("payload");
		
		Document doc = DocumentHelper.parseText(xml);		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
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
		
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
		String result = meshParser.getMeshParentId(placemark);
		Assert.assertNotNull(result);
		Assert.assertEquals("2", result);
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
				
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		SyncInfo syncInfo = meshParser.getSync("1");
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
				
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
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
						
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
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
				
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
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
				
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
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
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
		
		Sync sync = new Sync("3", "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", "3", 1);
		meshParser.refreshSync(syncInfo);
		
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
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
		Assert.assertNotNull(syncInfos);
		Assert.assertEquals(2, syncInfos.size());
		
		Sync sync = new Sync("2", "jmt", TestHelper.now(), true);
		SyncInfo syncInfo = new SyncInfo(sync, "kml", "2", 3);
		meshParser.refreshSync(syncInfo);
		
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
			   	"<ExtendedData>"+
		      	"</ExtendedData>"+
				"<name>example</name>"+
				"</Document>"+
				"</kml>";
		
		Document doc = DocumentHelper.parseText(localXML);
				
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
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
		
		meshParser.addElement(newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals(null, meshParser.getMeshParentId(element));
		
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
			
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
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
		
		meshParser.addElement(newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals(null, meshParser.getMeshParentId(element));
		
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
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		
		List<SyncInfo> syncInfos = meshParser.getAllSyncs();
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
		
		meshParser.addElement(newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals("1", meshParser.getMeshParentId(element));
		Assert.assertEquals("Folder", element.getParent().getName());
		
		syncInfo = meshParser.getSync(syncID);
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
			
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());

		String syncID = "2";
		SyncInfo syncInfo = meshParser.getSync(syncID);
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
		
		meshParser.updateElement(newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals("1", meshParser.getMeshParentId(element));
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

		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());

		String syncID = "2";
		SyncInfo syncInfo = meshParser.getSync(syncID);
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
		
		meshParser.updateElement(newElement, syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals(null, meshParser.getMeshParentId(element));
		Assert.assertEquals("Document", element.getParent().getName());
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
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());

		String syncID = "3";
		SyncInfo syncInfo = meshParser.getSync(syncID);
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
		
		meshParser.updateElement(newElement.createCopy(), syncInfo);
		
		Element element = meshParser.getElement(syncID);
		Assert.assertNotNull(element);
		Assert.assertEquals("2", meshParser.getMeshParentId(element));
		Assert.assertEquals("Folder", element.getParent().getName());
		Assert.assertEquals("FolderTwo", element.getParent().element("name").getText());
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
	
	// getElementsToSync
	@Test
	public void shouldGetElementsToSync() throws DocumentException{
		Document doc = DocumentHelper.parseText(xmlWithHierarchy);
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		List<Element> elements = meshParser.getElementsToSync();
		
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
		Document doc = DocumentHelper.createDocument();
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Assert.assertEquals(KmlNames.KML_PREFIX, meshParser.getType());
	}
	
	// normalize
	@Test
	public void shouldNormalizeStyleMap() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			"<name>dummy</name>"+
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
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_STYLE_MAP);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);		
	}
	
	@Test
	public void shouldNormalizeStyle() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			"<name>dummy</name>"+
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
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_STYLE);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);		
	}
	
	@Test
	public void shouldNormalizeFolderLevelRoot() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			"<name>dummy</name>"+
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
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertNotSame(element, normalizedElement);
		Assert.assertEquals(1, normalizedElement.elements().size());
		Assert.assertEquals("Folder1", normalizedElement.element("name").getText());
		Assert.assertEquals("1", meshParser.getMeshSyncId(normalizedElement));
		Assert.assertEquals(null, meshParser.getMeshParentId(normalizedElement));
	}
	
	@Test
	public void shouldNormalizeFolderLevel1() throws DocumentException{
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			"<name>dummy</name>"+
	      	"<Folder mesh4x:id=\"1\">"+
	      	"	<name>Folder1</name>"+
	      	"	<Folder mesh4x:id=\"2\" mesh4x:parentId=\"1\">"+
	      	"		<name>Folder2</name>"+
			"		<Placemark mesh4x:id=\"3\" mesh4x:parentId=\"2\">"+
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
			.element(KmlNames.KML_ELEMENT_FOLDER)
			.element(KmlNames.KML_ELEMENT_FOLDER);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertNotSame(element, normalizedElement);
		Assert.assertEquals(1, normalizedElement.elements().size());
		Assert.assertEquals("Folder2", normalizedElement.element("name").getText());
		Assert.assertEquals("2", meshParser.getMeshSyncId(normalizedElement));
		Assert.assertEquals("1", meshParser.getMeshParentId(normalizedElement));
		
	}
	
	@Test
	public void shouldNormalizePlacemark() throws DocumentException{
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
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element element = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Element normalizedElement = meshParser.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertSame(element, normalizedElement);	
	}
	
	@Test
	public void shouldNormalizeReturnNull(){
		Document doc = DocumentHelper.createDocument();
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Assert.assertNull(meshParser.normalize(null));
	}
	
	@Test
	public void shouldNormalizeReturnsSameElementBecauseNoXMLViewIsDefinedForElement(){
		Document doc = DocumentHelper.createDocument();
		Element element = DocumentHelper.createElement("FOO");
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		Assert.assertSame(element, meshParser.normalize(element));
	}

	// removeElemet
	@Test
	public void shouldRemoveElement() throws DocumentException{
		
		String elementXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			"<name>dummy</name>"+
			"<Placemark mesh4x:id=\"3\">"+
			"<name>C</name>"+
			"</Placemark>"+
			"</Document>"+
			"</kml>";
	
		Document doc = DocumentHelper.parseText(elementXML);
		Element rootElement = doc
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT);
		
		String syncID = "3";
			
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	
		Assert.assertNotNull(rootElement.element("Placemark"));
		Assert.assertNotNull(syncID, meshParser.getMeshSyncId(rootElement.element("Placemark")));
		meshParser.removeElement(syncID);
		Assert.assertNull(rootElement.element("Placemark"));
	}
	
	@Test
	public void shouldRemoveElementNoEffectBecauseItemDoesNotExist() throws DocumentException{
		Document doc = DocumentHelper.parseText(xml);
		
		KMLMeshDocument meshParser = new KMLMeshDocument(doc, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		meshParser.removeElement(IdGenerator.newID());
	}
	
	
	// verify refresh references
	@Test
	public void shouldRefreshReferences(){
		// TODO (JMT) test: refresh references
	}
	
	//isValid(Element, Element)
	@Test
	public void shouldIsValidReturnsFalseBecauseElementHasNotMeshID() throws DocumentException{
		Document doc = DocumentHelper.parseText(xmlWithoutMesh);
		Element syncRoot = doc.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Element element = syncRoot.element(KmlNames.KML_ELEMENT_PLACEMARK);
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);

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
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);
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
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);
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
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);
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
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);
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
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);
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
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);
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
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);
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
		
		boolean isValid = KMLMeshDocument.isValid(doc, element, NullIdentityProvider.INSTANCE);
		Assert.assertTrue(isValid);
	}
	
	
}