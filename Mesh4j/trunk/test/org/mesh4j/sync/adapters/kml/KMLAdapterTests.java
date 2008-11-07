package org.mesh4j.sync.adapters.kml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.ISupportMerge;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.dom.DOMLoader;
import org.mesh4j.sync.adapters.dom.IDOMLoader;
import org.mesh4j.sync.adapters.dom.MeshDOM;
import org.mesh4j.sync.adapters.dom.MeshNames;
import org.mesh4j.sync.adapters.dom.parsers.FileManager;
import org.mesh4j.sync.adapters.dom.parsers.HierarchyXMLViewElement;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;


public class KMLAdapterTests {

	private static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>example</name>"
			+ "<StyleMap id=\"msn_ylw-pushpin\">"
			+ "	<Pair>"
			+ "		<key>normal</key>"
			+ "		<styleUrl>#sn_ylw-pushpin</styleUrl>"
			+ "	</Pair>"
			+ "	<Pair>"
			+ "		<key>highlight</key>"
			+ "		<styleUrl>#sh_ylw-pushpin</styleUrl>"
			+ "	</Pair>"
			+ "</StyleMap>"
			+ "<Style id=\"sn_ylw-pushpin\">"
			+ "	<IconStyle>"
			+ "		<scale>1.1</scale>"
			+ "		<Icon>"
			+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
			+ "		</Icon>"
			+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
			+ "	</IconStyle>"
			+ "</Style>"
			+ "<Style id=\"sh_ylw-pushpin\">"
			+ "	<IconStyle>"
			+ "		<scale>1.3</scale>"
			+ "		<Icon>"
			+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
			+ "		</Icon>"
			+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
			+ "	</IconStyle>"
			+ "</Style>"
			+ "<Folder>"
			+ "	<name>Folder1</name>"
			+ "	<Folder>"
			+ "		<name>Folder3</name>"
			+ "		<Placemark>"
			+ "			<name>Placemark_C</name>"
			+ "			<visibility>0</visibility>"
			+ "			<LookAt>"
			+ "				<longitude>-86.29603105340947</longitude>"
			+ "				<latitude>36.94722720798701</latitude>"
			+ "				<altitude>0</altitude>"
			+ "				<range>15133106.44824071</range>"
			+ "				<tilt>0</tilt>"
			+ "				<heading>1.090894378010394</heading>"
			+ "			</LookAt>"
			+ "			<styleUrl>#msn_ylw-pushpin</styleUrl>"
			+ "			<Point>"
			+ "				<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"
			+ "			</Point>"
			+ "		</Placemark>"
			+ "	</Folder>"
			+ "	<Placemark>"
			+ "		<name>Placemark_A</name>"
			+ "		<visibility>0</visibility>"
			+ "		<LookAt>"
			+ "			<longitude>-86.29603105340947</longitude>"
			+ "			<latitude>36.94722720798701</latitude>"
			+ "			<altitude>0</altitude>"
			+ "			<range>15133106.44824071</range>"
			+ "			<tilt>0</tilt>"
			+ "			<heading>1.090894378010394</heading>"
			+ "		</LookAt>"
			+ "		<styleUrl>#msn_ylw-pushpin</styleUrl>"
			+ "		<Point>"
			+ "			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"
			+ "		</Point>"
			+ "	</Placemark>"
			+ "</Folder>"
			+ "<Placemark>"
			+ "	<name>Placemark_B</name>"
			+ "	<visibility>0</visibility>"
			+ "	<LookAt>"
			+ "		<longitude>-86.29603105340947</longitude>"
			+ "		<latitude>36.94722720798701</latitude>"
			+ "		<altitude>0</altitude>"
			+ "		<range>15133106.44824071</range>"
			+ "		<tilt>0</tilt>"
			+ "		<heading>1.090894378010394</heading>"
			+ "	</LookAt>"
			+ "	<styleUrl>#msn_ylw-pushpin</styleUrl>"
			+ "	<Point>"
			+ "		<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"
			+ "	</Point>" + "</Placemark>" + "<Folder>"
			+ "	<name>Folder2</name>" + "</Folder>" + "</Document>" + "</kml>";

	private static String xmlWithMeshInfo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>dummy</name>"
			+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
			+ "<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"3\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"4\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"5\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"6\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"7\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"8\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"9\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"10\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"11\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"12\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"			
			+ "<mesh4x:hierarchy xml:id=\"7\" mesh4x:childId=\"1\" />"
			+ "<mesh4x:hierarchy xml:id=\"8\" mesh4x:childId=\"2\" mesh4x:parentId=\"1\" />"
			+ "<mesh4x:hierarchy xml:id=\"9\" mesh4x:childId=\"3\" mesh4x:parentId=\"2\" />"
			+ "<mesh4x:hierarchy xml:id=\"10\" mesh4x:childId=\"4\" />"
			+ "<mesh4x:hierarchy xml:id=\"11\" mesh4x:childId=\"5\" />"
			+ "<mesh4x:hierarchy xml:id=\"12\" mesh4x:childId=\"6\" />"			
			+ "</ExtendedData>"
			+ "<StyleMap xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"msn_ylw-pushpin_4\" xml:id=\"4\" mesh4x:originalId=\"msn_ylw-pushpin\" >"
			+ "	<Pair>"
			+ "		<key>normal</key>"
			+ "		<styleUrl>#sn_ylw-pushpin</styleUrl>"
			+ "	</Pair>"
			+ "	<Pair>"
			+ "		<key>highlight</key>"
			+ "		<styleUrl>#sh_ylw-pushpin</styleUrl>"
			+ "	</Pair>"
			+ "</StyleMap>"
			+ "<Style xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"sn_ylw-pushpin_5\" xml:id=\"5\" mesh4x:originalId=\"sn_ylw-pushpin\" >"
			+ "	<IconStyle>"
			+ "		<color>ff00ff55</color>"
			+ "		<scale>1.1</scale>"
			+ "		<Icon>"
			+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
			+ "		</Icon>"
			+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
			+ "	</IconStyle>"
			+ "	<LabelStyle>"
			+ "		<color>ff00ff55</color>"
			+ "	</LabelStyle>"
			+ "</Style>"
			+ "<Style xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"sn_ylw-pushpin_6\" xml:id=\"6\" mesh4x:originalId=\"sn_ylw-pushpin\" >"
			+ "	<IconStyle>"
			+ "		<color>ff00ff55</color>"
			+ "		<scale>1.1</scale>"
			+ "		<Icon>"
			+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
			+ "		</Icon>"
			+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
			+ "	</IconStyle>"
			+ "	<LabelStyle>"
			+ "		<color>ff00ff55</color>"
			+ "	</LabelStyle>"
			+ "</Style>"
			+ "<Folder xml:id=\"1\">"
			+ "	<name>Folder1</name>"
			+ "	<Folder xml:id=\"2\">"
			+ "		<name>Folder2</name>"
			+ "		<Placemark xml:id=\"3\">"
			+ "			<name>B</name>"
			+ "		</Placemark>"
			+ "	</Folder>"
			+ "</Folder>" 
			+ "</Document>" 
			+ "</kml>";

	@Test
	public void shouldCreateFileIfDoesNotExist() throws DocumentException, IOException {
		String fileName = TestHelper.fileName(IdGenerator.INSTANCE.newID() + ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(fileName);
		kmlAdapter.beginSync();
		kmlAdapter.endSync();

		File file = new File(fileName);
		Assert.assertTrue(file.exists());

		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Assert.assertNotNull(document);

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>" + "<name>" + file.getName() + "</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\"></ExtendedData>" + "</Document>" + "</kml>";
		Document doc = DocumentHelper.parseText(xml);
		Assert.assertEquals(
			XMLHelper.canonicalizeXML(doc), 
			XMLHelper.canonicalizeXML(document));
	}

	private DOMAdapter makeNewDOMAdapter(String fileName) {
		IDOMLoader domLoader = KMLDOMLoaderFactory.createDOMLoader(fileName,
				NullIdentityProvider.INSTANCE);
		return new DOMAdapter(domLoader);
	}

	@Test
	public void shoulPrepareKMLToSync() throws DocumentException,
			JaxenException {

		File file = TestHelper.makeNewXMLFile(xml, ".kml");

		IDOMLoader domLoader = KMLDOMLoaderFactory.createDOMLoader(file
				.getAbsolutePath(), NullIdentityProvider.INSTANCE);

		DOMAdapter kmlAdapter = new DOMAdapter(domLoader);
		kmlAdapter.prepareDOMToSync();

		Document document = XMLHelper.readDocument(file);

		List<Element> folders = getElements("//kml:Folder", document);
		for (Element folder : folders) {
			Assert.assertTrue(kmlAdapter.isValid(folder));
		}

		List<Element> placemarks = getElements("//kml:Placemark", document);
		for (Element placemark : placemarks) {
			Assert.assertTrue(kmlAdapter.isValid(placemark));
		}

		List<Element> styles = getElements("//kml:Style", document);
		for (Element style : styles) {
			Assert.assertTrue(kmlAdapter.isValid(style));
		}

		List<Element> styleMaps = getElements("//kml:StyleMap", document);
		for (Element styleMap : styleMaps) {
			Assert.assertTrue(kmlAdapter.isValid(styleMap));
		}
	}

	@Test
	public void shoulRefreshNewContent() throws DocumentException,
			JaxenException {

		File file = TestHelper.makeNewXMLFile(xml, ".kml");

		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();
		List<Item> items = kmlAdapter.getAll();
		kmlAdapter.endSync();

		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		Assert.assertEquals(18, items.size());

		for (Item item : items) {
			String syncID = kmlAdapter.getMeshSyncId(item.getContent().getPayload());
			Assert.assertNotNull(syncID);
			Assert.assertEquals(syncID, item.getSyncId());

			String parentID = kmlAdapter.getMeshSyncId(item.getContent().getPayload());
			Assert.assertNotNull(parentID);
		}

		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Assert.assertNotNull(document);

		List<Element> elements = this.getElementsToSync(document);
		for (Element element : elements) {
			String syncID = kmlAdapter.getMeshSyncId(element);
			Assert.assertNotNull(syncID);

			String myParentID = kmlAdapter.getMeshSyncId(element.getParent());
			String parentID = HierarchyXMLViewElement.getMeshParentId(document, element);
			Assert.assertEquals(parentID, myParentID);

		}
	}
	
	public List<Element> getElementsToSync(Document document) {
		Map<String, String> namespaces = KMLDOMLoaderFactory.createView(new FileManager()).getNameSpaces();
		List<Element> elements = XMLHelper.selectElements("//kml:StyleMap",
				document.getRootElement(), namespaces);
		elements.addAll(XMLHelper.selectElements("//kml:Style", document
				.getRootElement(), namespaces));
		elements.addAll(XMLHelper.selectElements("//kml:Folder", document
				.getRootElement(), namespaces));
		elements.addAll(XMLHelper.selectElements("//kml:Placemark", document
				.getRootElement(), namespaces));
		return elements;
	}

	private List<Element> getElements(String xpathExpression, Document document) throws JaxenException {
		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		
		return XMLHelper.selectElements(xpathExpression, document
		.getRootElement(), namespaces);
	}
	

	@Test
	public void shoulRefreshOldContent() throws DocumentException,
			JaxenException {

		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");

		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();
		
		Document document = XMLHelper.readDocument(file);

		assertGetItemPayload(kmlAdapter, document, "4", null, "StyleMap",
				"msn_ylw-pushpin_4", null);
		assertGetItemPayload(kmlAdapter, document, "5", null, "Style",
				"sn_ylw-pushpin_5", null);
		assertGetItemPayload(kmlAdapter, document, "6", null, "Style",
				"sn_ylw-pushpin_6", null);
		assertGetItemPayload(kmlAdapter, document, "1", null, "Folder", null, "Folder1");
		assertGetItemPayload(kmlAdapter, document, "2", "1", "Folder", null, "Folder2");
		assertGetItemPayload(kmlAdapter, document, "3", "2", "Placemark", null, "B");
	}

	private void assertGetItemPayload(DOMAdapter kmlAdapter, Document document, String syncID,
			String parentID, String elementType, String kmlID, String name) {
		Item item = kmlAdapter.get(syncID);
		Assert.assertNotNull(item);
		Element payload = item.getContent().getPayload();
		Assert.assertNotNull(payload);
		Assert.assertEquals(elementType, payload.getName());
		Assert.assertEquals(syncID, kmlAdapter.getMeshSyncId(payload));
		Assert.assertEquals(parentID, HierarchyXMLViewElement.getMeshParentId(document, payload));
		if (name != null) {
			Assert.assertEquals(name, payload.element("name").getText());
		}
	}

	@Test
	public void shoulRefreshContent() throws DocumentException, JaxenException {

		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "	<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"4\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"5\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"6\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"7\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"8\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"9\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"10\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"11\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "	<sx:sync id=\"12\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "		<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "	</sx:sync>"
				+ "</mesh4x:sync>"			
				+ "<mesh4x:hierarchy xml:id=\"7\" mesh4x:childId=\"1\" mesh4x:parentId=\"2\" />"
				+ "<mesh4x:hierarchy xml:id=\"8\" mesh4x:childId=\"2\" />"
				+ "<mesh4x:hierarchy xml:id=\"9\" mesh4x:childId=\"3\" mesh4x:parentId=\"2\" />"
				+ "<mesh4x:hierarchy xml:id=\"10\" mesh4x:childId=\"4\" />"
				+ "<mesh4x:hierarchy xml:id=\"11\" mesh4x:childId=\"5\" />"
				+ "<mesh4x:hierarchy xml:id=\"12\" mesh4x:childId=\"6\" />"

				+ "</ExtendedData>"
				+ "<StyleMap xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"msn_ylw-pushpin_4\" xml:id=\"4\" mesh4x:originalId=\"msn_ylw-pushpin\" >"
				+ "	<Pair>"
				+ "		<key>normal</key>"
				+ "		<styleUrl>#sn_ylw-pushpin</styleUrl>"
				+ "	</Pair>"
				+ "	<Pair>"
				+ "		<key>highlight</key>"
				+ "		<styleUrl>#sh_ylw-pushpin</styleUrl>"
				+ "	</Pair>"
				+ "</StyleMap>"
				+ "<Style xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"sn_ylw-pushpin_5\" xml:id=\"5\"  mesh4x:originalId=\"sn_ylw-pushpin\" >"
				+ "	<IconStyle>"
				+ "		<color>ff00ff55</color>"
				+ "		<scale>1.1</scale>"
				+ "		<Icon>"
				+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+ "		</Icon>"
				+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+ "	</IconStyle>"
				+ "	<LabelStyle>"
				+ "		<color>ff00ff55</color>"
				+ "	</LabelStyle>"
				+ "</Style>"
				+ "<Style xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"sn_ylw-pushpin_6\" xml:id=\"6\"  mesh4x:originalId=\"sh_ylw-pushpin\" >"
				+ "	<IconStyle>"
				+ "		<color>ff00ff55</color>"
				+ "		<scale>1.1</scale>"
				+ "		<Icon>"
				+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+ "		</Icon>"
				+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+ "	</IconStyle>" 
				+ "	<LabelStyle>"
				+ "		<color>ff00ff55</color>" 
				+ "	</LabelStyle>" 
				+ "</Style>"
				+ "<Folder xml:id=\"1\">"
				+ "	<name>Folder1</name>" 
				+ "	<Folder xml:id=\"2\">"
				+ "		<name>Folder2</name>" 
				+ "	</Folder>" 
				+ "</Folder>"
				+ "<Placemark xml:id=\"3\">"
				+ "	<name>B</name>" 
				+ "</Placemark>" 
				+ "</Document>" 
				+ "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");

		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();
		kmlAdapter.endSync();
		
		Document document = XMLHelper.readDocument(file);
		Assert.assertNotNull(document);

		Item item = kmlAdapter.get("3");
		Assert.assertNotNull(item);
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(document, item.getContent()
				.getPayload()));

		item = kmlAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(document, item.getContent()
				.getPayload()));

		item = kmlAdapter.get("2");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", HierarchyXMLViewElement.getMeshParentId(document, item.getContent().getPayload()));
	}

	@Test
	public void shoulGetAllRefreshContent() throws DocumentException, JaxenException {

		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");

		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		String localXMLUpdated = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>dummy</name>"
			+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
			+ "<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"2\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"3\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"4\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"5\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"6\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"7\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"8\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"9\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"10\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"11\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"12\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"			
			+ "<mesh4x:hierarchy xml:id=\"7\" mesh4x:childId=\"1\" />"
			+ "<mesh4x:hierarchy xml:id=\"8\" mesh4x:childId=\"2\" mesh4x:parentId=\"1\" />"
			+ "<mesh4x:hierarchy xml:id=\"9\" mesh4x:childId=\"3\" mesh4x:parentId=\"2\" />"
			+ "<mesh4x:hierarchy xml:id=\"10\" mesh4x:childId=\"4\" />"
			+ "<mesh4x:hierarchy xml:id=\"11\" mesh4x:childId=\"5\" />"
			+ "<mesh4x:hierarchy xml:id=\"12\" mesh4x:childId=\"6\" />"			
			+ "</ExtendedData>"
			+ "<StyleMap xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"msn_ylw-pushpin_4\" xml:id=\"4\" mesh4x:originalId=\"msn_ylw-pushpin\" >"
			+ "	<Pair>"
			+ "		<key>normal</key>"
			+ "		<styleUrl>#sn_ylw-pushpin</styleUrl>"
			+ "	</Pair>"
			+ "	<Pair>"
			+ "		<key>highlight</key>"
			+ "		<styleUrl>#sh_ylw-pushpin</styleUrl>"
			+ "	</Pair>"
			+ "</StyleMap>"
			+ "<Style xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"sn_ylw-pushpin_5\" xml:id=\"5\" mesh4x:originalId=\"sn_ylw-pushpin\" >"
			+ "	<IconStyle>"
			+ "		<color>ff00ff55</color>"
			+ "		<scale>1.1</scale>"
			+ "		<Icon>"
			+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
			+ "		</Icon>"
			+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
			+ "	</IconStyle>"
			+ "	<LabelStyle>"
			+ "		<color>ff00ff55</color>"
			+ "	</LabelStyle>"
			+ "</Style>"
			+ "<Style xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"sn_ylw-pushpin_6\" xml:id=\"6\" mesh4x:originalId=\"sn_ylw-pushpin\" >"
			+ "	<IconStyle>"
			+ "		<color>ff00ff55</color>"
			+ "		<scale>1.1</scale>"
			+ "		<Icon>"
			+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
			+ "		</Icon>"
			+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
			+ "	</IconStyle>"
			+ "	<LabelStyle>"
			+ "		<color>ff00ff55</color>"
			+ "	</LabelStyle>"
			+ "</Style>"
			+ "<Folder xml:id=\"1\" >"
			+ "	<name>Folder1</name>" 
			+ "	<Folder xml:id=\"2\">"
			+ "		<name>Folder2</name>" 
			+ "	</Folder>" 
			+ "</Folder>"
			+ "<Placemark xml:id=\"3\" >"
			+ "	<name>B</name>" 
			+ "</Placemark>" 
			+ "</Document>" 
			+ "</kml>";

		XMLHelper.write(localXMLUpdated, file);
		kmlAdapter.beginSync();
		kmlAdapter.endSync();

		List<Item> items = kmlAdapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(12, items.size());

		Document document = XMLHelper.readDocument(file);
		Assert.assertNotNull(document);
		
		Item item = kmlAdapter.get("3");
		Assert.assertNotNull(item);
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(document, item.getContent()
				.getPayload()));

		item = kmlAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(document, item.getContent()
				.getPayload()));

		item = kmlAdapter.get("2");
		Assert.assertNotNull(item);
		Assert.assertEquals("1",
				HierarchyXMLViewElement.getMeshParentId(document, item.getContent().getPayload()));
	}

	@Test(expected = MeshException.class)
	public void shouldThrowsExceptionBecauseFileHasNotElements() {
		DOMAdapter kmlAdapter = makeNewDOMAdapter(getFileWithOutElements());
		kmlAdapter.beginSync();
	}

	@Test(expected = MeshException.class)
	public void shouldThrowsExceptionIfFileExistButHasNotKMLElement() {
		DOMAdapter kmlAdapter = makeNewDOMAdapter(getFileWithOutKMLElement());
		kmlAdapter.beginSync();
	}

	@Test(expected = MeshException.class)
	public void shouldThrowsExceptionIfFileExistHasKMLElementButHasNotDocumentElement() {
		DOMAdapter kmlAdapter = makeNewDOMAdapter(getFileWithOutDocumentElement());
		kmlAdapter.beginSync();
	}

	@Test
	public void shouldReturnsAuthenticatedUser() {
		DOMAdapter kmlAdapter = makeNewDOMAdapter(getDummyFileName());
		String user = kmlAdapter.getAuthenticatedUser();
		Assert.assertNotNull(user);
		Assert.assertEquals(user, NullIdentityProvider.INSTANCE
				.getAuthenticatedUser());
	}

	@Test
	public void shouldNotSupportMerge() {
		DOMAdapter kmlAdapter = makeNewDOMAdapter(getDummyFileName());
		Assert.assertFalse(kmlAdapter instanceof ISupportMerge);
	}

	@Test
	public void shouldReturnsFriendlyName() {
		DOMAdapter kmlAdapter = makeNewDOMAdapter(getDummyFileName());
		String name = kmlAdapter.getFriendlyName();
		Assert.assertNotNull(name);
		Assert.assertEquals(name, "KML Adapter");
	}


	private String getDummyFileName() {
		return this.getClass().getResource("dummy.kml").getFile();
	}

	private String getFileWithOutKMLElement() {
		return this.getClass().getResource("templateWithOutKMLElement.kml")
				.getFile();
	}

	private String getFileWithOutDocumentElement() {
		return this.getClass()
				.getResource("templateWithOutDocumentElement.kml").getFile();
	}

	private String getFileWithOutElements() {
		return this.getClass().getResource("templateWithOutElements.kml")
				.getFile();
	}

	// GET
	@Test
	public void shouldGetReturnNullBecauseItemDoesNotExist() {
		File file = TestHelper.makeNewXMLFile(xml, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		Item item = kmlAdapter.get(IdGenerator.INSTANCE.newID());
		Assert.assertNull(item);
	}

	@Test
	public void shouldGetReturnsItem() throws DocumentException {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();
		
		Document document = XMLHelper.readDocument(file);

		assertGetItemPayload(kmlAdapter, document, "2", "1", "Folder", null, "Folder2");
	}

	@Test
	public void shouldGetReturnsNullContentBecauseItemWasDeleted() {
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\" >"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"3\" deleted=\"true\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
				+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>" + "</mesh4x:sync>" + "</ExtendedData>"
				+ "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		Item item = kmlAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
	}

	@Test
	public void shouldGetReturnsNullContentBecauseItemWasDeletedExternally() {
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\" >"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>" + "</mesh4x:sync>" + "</ExtendedData>"
				+ "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		Item item = kmlAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(2, item.getSync().getUpdates());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetTrowsExceptionBecauseParameterIsNull() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		kmlAdapter.get(null);
	}

	// ADD
	@Test(expected = IllegalArgumentException.class)
	public void shouldAddTrowsExceptionBecauseParameterIsNull() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		kmlAdapter.add(null);
	}

	@Test
	public void shouldAdd() throws DocumentException {
		
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		String syncID = IdGenerator.INSTANCE.newID();

		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "<Placemark xml:id=\"" + syncID + "\">" 
				+ "	<name>MYHouse</name>" + "</Placemark>"
				+ "</Document>" + "</kml>";

		Element payload = DocumentHelper.parseText(localXML).getRootElement()
				.element("Document").element("Placemark");

		KMLContent kmlContent = new KMLContent(payload, syncID);

		Sync sync = new Sync(syncID, "JMT", TestHelper.now(), false);
		Item item = new Item(kmlContent, sync);
		kmlAdapter.add(item);

		kmlAdapter.endSync();
		
		Document document = XMLHelper.readDocument(file);
		
		assertGetItemPayload(kmlAdapter, document, syncID, null, "Placemark", null,
				"MYHouse");

		Element hierarchyElement = XMLHelper.selectSingleNode("//mesh4x:*[@mesh4x:childId='"+syncID+"']", document.getRootElement(), MeshDOM.SEARCH_NAMESPACES);
		Assert.assertNull(hierarchyElement);
		
		String hierarchySyncID = IdGenerator.INSTANCE.newID();
	
		localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<mesh4x:hierarchy xmlns:mesh4x=\"http://mesh4x.org/kml\" xml:id=\""+ hierarchySyncID +"\" mesh4x:parentId=\"1\" mesh4x:childId=\""+syncID+"\" />"
			+ "</Document>" + "</kml>";

		payload = DocumentHelper.parseText(localXML).getRootElement()
			.element("Document").element(MeshNames.MESH_QNAME_HIERARCHY);

		kmlContent = new KMLContent(payload, hierarchySyncID);
		item = new Item(kmlContent, new Sync(hierarchySyncID, "jmt", TestHelper.now(), false));
		kmlAdapter.add(item);

		item = kmlAdapter.get(hierarchySyncID);
		Assert.assertNotNull(item);
		
		kmlAdapter.endSync();
		
		document = XMLHelper.readDocument(file);
				
		assertGetItemPayload(kmlAdapter, document, syncID, "1", "Placemark", null, "MYHouse");

		Assert.assertNotNull(kmlAdapter.get(hierarchySyncID));
		
		kmlAdapter.endSync();
		
		document = XMLHelper.readDocument(file);
		hierarchyElement = XMLHelper.selectSingleNode("//mesh4x:*[@mesh4x:childId='"+syncID+"']", document.getRootElement(), MeshDOM.SEARCH_NAMESPACES);
		Assert.assertNotNull(hierarchyElement);
		
		hierarchySyncID = hierarchyElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
		Assert.assertNotNull(hierarchySyncID);
		
		String parentID = hierarchyElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID);
		Assert.assertNotNull(parentID);
		Assert.assertEquals("1", parentID);
		
		String childID = hierarchyElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID);
		Assert.assertNotNull(childID);
		Assert.assertEquals(syncID, childID);
		
	}

	@Test
	public void shouldAddDeletedItem() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		String syncID = IdGenerator.INSTANCE.newID();

		Sync sync = new Sync(syncID, "JMT", TestHelper.now(), true);
		Item item = new Item(new NullContent(syncID), sync);
		kmlAdapter.add(item);

		item = kmlAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(1, item.getSync().getUpdates());

	}

	// DELETE
	@Test(expected = IllegalArgumentException.class)
	public void shouldDeleteTrowsExceptionBecauseParameterIsNull() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		kmlAdapter.delete(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDeleteTrowsExceptionBecauseParameterIsStringEmpty() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		kmlAdapter.delete("");
	}

	@Test
	public void shouldDeleteAddSyncDeleteDataBecauseItemDoesNotExist() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		String syncID = IdGenerator.INSTANCE.newID();
		Item item = kmlAdapter.get(syncID);
		Assert.assertNull(item);

		kmlAdapter.delete(syncID);

		item = kmlAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(1, item.getSync().getUpdates());
	}

	@Test
	public void shouldDeleteWithoutEffectBecauseItemWasDeleted() {

		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\" >"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"3\" deleted=\"true\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
				+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>" + "</mesh4x:sync>" + "</ExtendedData>"
				+ "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		Item item = kmlAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(3, item.getSync().getUpdates());

		kmlAdapter.delete("1");

		item = kmlAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(3, item.getSync().getUpdates());
	}

	@Test
	public void shouldDelete() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		Item item = kmlAdapter.get("3");
		Assert.assertNotNull(item);
		Assert.assertEquals("3", item.getSyncId());
		Assert.assertFalse(item.getSync().isDeleted());
		Assert.assertEquals(4, item.getSync().getUpdates());

		kmlAdapter.delete("3");

		item = kmlAdapter.get("3");
		Assert.assertNotNull(item);
		Assert.assertEquals("3", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(5, item.getSync().getUpdates());

	}

	// UPDATE
	@Test(expected = IllegalArgumentException.class)
	public void shouldUpdateTrowsExceptionBecauseParameterIsNull() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		kmlAdapter.update(null);
	}

	@Test
	public void shouldUpdateDeletedItem() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		String syncID = "3";
		Item item = kmlAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertFalse(item.getSync().isDeleted());

		Sync sync = item.getSync();
		sync.update("JMT", TestHelper.now(), true);

		item = new Item(new NullContent(syncID), sync);
		kmlAdapter.update(item);

		item = kmlAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
	}

	@Test
	public void shouldUpdateDeletedItemRefreshSyncDataBecauseItemWasDeleted() {
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\" >"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"3\" deleted=\"true\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
				+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>" + "</mesh4x:sync>" + "</ExtendedData>"
				+ "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		Item item = kmlAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(3, item.getSync().getUpdates());

		item.getSync().update("jmt", TestHelper.now(), true);
		kmlAdapter.update(item);

		item = kmlAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(4, item.getSync().getUpdates());
	}

	@Test
	public void shouldUpdateItem() throws DocumentException {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter kmlAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		kmlAdapter.beginSync();

		String syncID = "3";
		Item item = kmlAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertFalse(item.getSync().isDeleted());
		Assert.assertEquals(4, item.getSync().getUpdates());

		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "	<Placemark xml:id=\"3\">"
				+ "		<name>MuMu</name>" + "	</Placemark>" + "</Document>"
				+ "</kml>";

		Element payload = DocumentHelper.parseText(localXML).getRootElement()
				.element("Document").element("Placemark");

		KMLContent content = new KMLContent(payload, syncID);
		item = new Item(content, item.getSync().update("jmt", TestHelper.now(),
				false));
		kmlAdapter.update(item);

		item = kmlAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertFalse(item.getSync().isDeleted());
		Assert.assertEquals(5, item.getSync().getUpdates());
		Assert.assertEquals("MuMu", item.getContent().getPayload().element(
				"name").getText());

	}
	
	@Test
	public void shouldClean() throws DocumentException{
		
		String fileName = "kmlWithSyncInfo.kml";
		File externalFile = new File(TestHelper.fileName(fileName));
		File localFile = new File(this.getClass().getResource(fileName).getFile());
		
		Document doc = XMLHelper.readDocument(localFile);
		XMLHelper.write(doc, externalFile);
		
		DOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(externalFile.getAbsolutePath(), NullIdentityProvider.INSTANCE);
		DOMAdapter kml = new DOMAdapter(loader);
		kml.beginSync();
		
		Map<String, String> ns = new HashMap<String, String>();
		ns.put(MeshNames.XML_PREFIX, MeshNames.XML_URI);
		ns.put(MeshNames.MESH_PREFIX, MeshNames.MESH_URI);
		ns.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		
		doc = XMLHelper.readDocument(externalFile);
		List<Element> elements = XMLHelper.selectElements("//mesh4x:*", doc.getRootElement(), ns);
		Assert.assertNotNull(elements);
		Assert.assertEquals(37, elements.size());
		
		elements = XMLHelper.selectElements("//*[@mesh4x:originalId!='']", doc.getRootElement(), ns);
		Assert.assertNotNull(elements);
		Assert.assertEquals(9, elements.size());

		elements = XMLHelper.selectElements("//*[@xml:id!='']", doc.getRootElement(), ns);
		Assert.assertNotNull(elements);
		Assert.assertEquals(24, elements.size());
		
		elements = XMLHelper.selectElements("//kml:Style", doc.getRootElement(), ns); 
		for (Element element : elements) {
			Assert.assertNotNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
		}

		elements = XMLHelper.selectElements("//kml:StyleMap", doc.getRootElement(), ns); 
		for (Element element : elements) {
			Assert.assertNotNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
		}
		
		elements = XMLHelper.selectElements("//kml:ExtendedData", doc.getRootElement(), ns); 
		for (Element element : elements) {
			Assert.assertNotNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
		}
		
		kml.clean();
		
		doc = XMLHelper.readDocument(externalFile);
		elements = XMLHelper.selectElements("//mesh4x:*", doc.getRootElement(), ns);
		Assert.assertNotNull(elements);
		Assert.assertEquals(0, elements.size());
		
		elements = XMLHelper.selectElements("//*[@mesh4x:originalId!='']", doc.getRootElement(), ns);
		Assert.assertNotNull(elements);
		Assert.assertEquals(0, elements.size());

		elements = XMLHelper.selectElements("//*[@xml:is!='']", doc.getRootElement(), ns);
		Assert.assertNotNull(elements);
		Assert.assertEquals(0, elements.size());
		
		
		elements = XMLHelper.selectElements("//kml:Style", doc.getRootElement(), ns); 
		for (Element element : elements) {
			Assert.assertNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
		}

		elements = XMLHelper.selectElements("//kml:StyleMap", doc.getRootElement(), ns); 
		for (Element element : elements) {
			Assert.assertNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
		}
		
		elements = XMLHelper.selectElements("//kml:ExtendedData", doc.getRootElement(), ns); 
		for (Element element : elements) {
			Assert.assertNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
		}
	}
	
	@Test
	public void shouldPurgue() throws DocumentException{
		
		String fileName = "kmlWithSyncInfo.kml";
		File externalFile = new File(TestHelper.fileName(fileName));
		File localFile = new File(this.getClass().getResource(fileName).getFile());
		
		Document doc = XMLHelper.readDocument(localFile);
		XMLHelper.write(doc, externalFile);
		
		DOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(externalFile.getAbsolutePath(), NullIdentityProvider.INSTANCE);
		DOMAdapter kml = new DOMAdapter(loader);
		kml.beginSync();
		
		boolean thereAreMoreOneHistories = false;
		boolean thereAreDeletes = false;
		List<SyncInfo> syncs = kml.getDOM().getAllSyncs();
		for (SyncInfo syncInfo : syncs) {
			Assert.assertEquals(syncInfo.getSync().getUpdates(), syncInfo.getSync().getUpdatesHistory().size());
			if(syncInfo.getSync().getUpdates() > 1){
				thereAreMoreOneHistories  = true;
			}
			if(syncInfo.isDeleted()){
				thereAreDeletes = true;	
			}
		}
		Assert.assertTrue(thereAreMoreOneHistories);
		Assert.assertTrue(thereAreDeletes);
		
		kml.purgue();
		
		syncs = kml.getDOM().getAllSyncs();
		for (SyncInfo syncInfo : syncs) {
			Assert.assertEquals(syncInfo.getSync().getUpdates(), syncInfo.getSync().getLastUpdate().getSequence());
			Assert.assertEquals(1, syncInfo.getSync().getUpdatesHistory().size());
			Assert.assertFalse(syncInfo.isDeleted());
		}
	}
}
