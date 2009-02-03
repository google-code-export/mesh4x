package org.mesh4j.sync.adapters.dom;

import java.io.File;
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
import org.mesh4j.sync.adapters.dom.parsers.FileManager;
import org.mesh4j.sync.adapters.dom.parsers.HierarchyXMLViewElement;
import org.mesh4j.sync.adapters.kml.KMLContent;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.adapters.kml.KmlNames;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;


public class DOMAdapterTests {

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
			+ "</ExtendedData>"
			+ "<StyleMap xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"msn_ylw-pushpin_4\" xml:id=\"4\" mesh4x:originalId=\"msn_ylw-pushpin\">"
			+ "	<Pair>"
			+ "		<key>normal</key>"
			+ "		<styleUrl>#sn_ylw-pushpin</styleUrl>"
			+ "	</Pair>"
			+ "	<Pair>"
			+ "		<key>highlight</key>"
			+ "		<styleUrl>#sh_ylw-pushpin</styleUrl>"
			+ "	</Pair>"
			+ "</StyleMap>"
			+ "<Style xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"sn_ylw-pushpin_5\" xml:id=\"5\" mesh4x:originalId=\"sn_ylw-pushpin\">"
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
			+ "<Style xmlns:mesh4x=\"http://mesh4x.org/kml\" id=\"sn_ylw-pushpin_6\" xml:id=\"6\" mesh4x:originalId=\"sn_ylw-pushpin\">"
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
			+ "</Folder>" + "</Document>" + "</kml>";

	@Test
	public void shouldCreateFileIfDoesNotExist() throws DocumentException {
		String fileName = TestHelper.fileName(IdGenerator.INSTANCE.newID() + ".kml");
		DOMAdapter DOMAdapter = makeNewDOMAdapter(fileName);
		DOMAdapter.beginSync();
		DOMAdapter.endSync();

		File file = new File(fileName);
		Assert.assertTrue(file.exists());

		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Assert.assertNotNull(document);

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>" + "<name>" + file.getName() + "</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\"></ExtendedData>" + "</Document>" + "</kml>";

		Document expectedDocument = DocumentHelper.parseText(xml);
		Assert.assertEquals(XMLHelper.canonicalizeXML(expectedDocument), XMLHelper.canonicalizeXML(document));
	}

	private DOMAdapter makeNewDOMAdapter(String fileName) {
		IDOMLoader domLoader = KMLDOMLoaderFactory.createDOMLoader(fileName,
				NullIdentityProvider.INSTANCE);
		return new DOMAdapter(domLoader);
	}

	@Test
	public void shoulPrepareDOMToSync() throws DocumentException,
			JaxenException {

		File file = TestHelper.makeNewXMLFile(xml, ".kml");

		IDOMLoader domLoader = KMLDOMLoaderFactory.createDOMLoader(file
				.getAbsolutePath(), NullIdentityProvider.INSTANCE);
		
		DOMAdapter domAdapter = new DOMAdapter(domLoader);
		domAdapter.prepareDOMToSync();

		Document document = XMLHelper.readDocument(file);
		document.normalize();
		
		List<Element> folders = getElements("//kml:Folder", document);
		for (Element folder : folders) {
			Assert.assertTrue(domAdapter.isValid(folder));
		}

		List<Element> placemarks = getElements("//kml:Placemark", document);
		for (Element placemark : placemarks) {
			Assert.assertTrue(domAdapter.isValid(placemark));
		}

		List<Element> styles = getElements("//kml:Style", document);
		for (Element style : styles) {
			Assert.assertTrue(domAdapter.isValid(style));
		}

		List<Element> styleMaps = getElements("//kml:StyleMap", document);
		for (Element styleMap : styleMaps) {
			Assert.assertTrue(domAdapter.isValid(styleMap));
		}
	}

	@Test
	public void shoulRefreshNewContent() throws DocumentException,
			JaxenException {

		File file = TestHelper.makeNewXMLFile(xml, ".kml");

		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();
		List<Item> items = domAdapter.getAll();
		domAdapter.endSync();

		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		Assert.assertEquals(18, items.size());

		for (Item item : items) {
			String syncID = domAdapter.getMeshSyncId(item.getContent().getPayload());
			Assert.assertNotNull(syncID);
			Assert.assertEquals(syncID, item.getSyncId());
		}

		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Assert.assertNotNull(document);
		
		List<Element> elements = this.getElementsToSync(document);
		for (Element element : elements) {
			String syncID = domAdapter.getMeshSyncId(element);
			Assert.assertNotNull(syncID);

			String myParentID = domAdapter.getMeshSyncId(element.getParent());
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

	@Test
	public void shoulRefreshOldContent() throws DocumentException,
			JaxenException {

		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");

		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		assertGetItemPayload(domAdapter, "4", null, "StyleMap",
				"msn_ylw-pushpin_4", null);
		assertGetItemPayload(domAdapter, "5", null, "Style",
				"sn_ylw-pushpin_5", null);
		assertGetItemPayload(domAdapter, "6", null, "Style",
				"sn_ylw-pushpin_6", null);
		assertGetItemPayload(domAdapter, "1", null, "Folder", null, "Folder1");
		assertGetItemPayload(domAdapter, "2", "1", "Folder", null, "Folder2");
		assertGetItemPayload(domAdapter, "3", "2", "Placemark", null, "B");
	}

	private void assertGetItemPayload(DOMAdapter domAdapter, String syncID,
			String parentID, String elementType, String kmlID, String name) {
		Item item = domAdapter.get(syncID);
		Assert.assertNotNull(item);
		Element payload = item.getContent().getPayload();
		Assert.assertNotNull(payload);
		Assert.assertEquals(elementType, payload.getName());
		Assert.assertEquals(syncID, domAdapter.getMeshSyncId(payload));
		Assert.assertEquals(parentID, HierarchyXMLViewElement.getMeshParentId(domAdapter.getDOM().toDocument(), payload));
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
				+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"4\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"5\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"6\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "</ExtendedData>"
				+ "<StyleMap id=\"msn_ylw-pushpin_4\" xml:id=\"4\">"
				+ "	<Pair>"
				+ "		<key>normal</key>"
				+ "		<styleUrl>#sn_ylw-pushpin</styleUrl>"
				+ "	</Pair>"
				+ "	<Pair>"
				+ "		<key>highlight</key>"
				+ "		<styleUrl>#sh_ylw-pushpin</styleUrl>"
				+ "	</Pair>"
				+ "</StyleMap>"
				+ "<Style id=\"sn_ylw-pushpin_5\" xml:id=\"5\">"
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
				+ "<Style id=\"sn_ylw-pushpin_6\" xml:id=\"6\">"
				+ "	<IconStyle>"
				+ "		<color>ff00ff55</color>"
				+ "		<scale>1.1</scale>"
				+ "		<Icon>"
				+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+ "		</Icon>"
				+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+ "	</IconStyle>" + "	<LabelStyle>"
				+ "		<color>ff00ff55</color>" + "	</LabelStyle>" + "</Style>"
				+ "<Folder xml:id=\"1\">"
				+ "	<name>Folder1</name>" + "	<Folder xml:id=\"2\">"
				+ "		<name>Folder2</name>" + "	</Folder>" + "</Folder>"
				+ "<Placemark xml:id=\"3\">"
				+ "	<name>B</name>" + "</Placemark>" + "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");

		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();
		domAdapter.endSync();
		
		Document doc = XMLHelper.readDocument(file);
	
		Item item = domAdapter.get("3");
		Assert.assertNotNull(item);
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(doc, item.getContent()
				.getPayload()));

		item = domAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(doc, item.getContent()
				.getPayload()));

		item = domAdapter.get("2");
		Assert.assertNotNull(item);
		Assert.assertEquals("1",
				HierarchyXMLViewElement.getMeshParentId(doc, item.getContent().getPayload()));
	}

	@Test
	public void shoulGetAllRefreshContent() throws DocumentException,
			JaxenException {

		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<name>dummy</name>"
				+ "<ExtendedData>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"4\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"5\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"6\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "</ExtendedData>"
				+ "<StyleMap id=\"msn_ylw-pushpin_4\" xml:id=\"4\">"
				+ "	<Pair>"
				+ "		<key>normal</key>"
				+ "		<styleUrl>#sn_ylw-pushpin</styleUrl>"
				+ "	</Pair>"
				+ "	<Pair>"
				+ "		<key>highlight</key>"
				+ "		<styleUrl>#sh_ylw-pushpin</styleUrl>"
				+ "	</Pair>"
				+ "</StyleMap>"
				+ "<Style id=\"sn_ylw-pushpin_5\" xml:id=\"5\">"
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
				+ "<Style id=\"sn_ylw-pushpin_6\" xml:id=\"6\">"
				+ "	<IconStyle>"
				+ "		<color>ff00ff55</color>"
				+ "		<scale>1.1</scale>"
				+ "		<Icon>"
				+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+ "		</Icon>"
				+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+ "	</IconStyle>" + "	<LabelStyle>"
				+ "		<color>ff00ff55</color>" + "	</LabelStyle>" + "</Style>"
				+ "<Folder xml:id=\"2\">" + "	<name>Folder2</name>"
				+ "	<Folder xml:id=\"1\" mesh4x:parentId=\"2\">"
				+ "		<name>Folder1</name>"
				+ "		<Placemark xml:id=\"3\" mesh4x:parentId=\"2\">"
				+ "			<name>B</name>" + "		</Placemark>" + "	</Folder>"
				+ "</Folder>" + "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");

		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		String localXMLUpdated = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<name>dummy</name>"
				+ "<ExtendedData>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"4\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"5\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
				+ "<sx:sync id=\"6\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>"
				+ "</mesh4x:sync>"
				+ "</ExtendedData>"
				+ "<StyleMap id=\"msn_ylw-pushpin_4\" xml:id=\"4\">"
				+ "	<Pair>"
				+ "		<key>normal</key>"
				+ "		<styleUrl>#sn_ylw-pushpin</styleUrl>"
				+ "	</Pair>"
				+ "	<Pair>"
				+ "		<key>highlight</key>"
				+ "		<styleUrl>#sh_ylw-pushpin</styleUrl>"
				+ "	</Pair>"
				+ "</StyleMap>"
				+ "<Style id=\"sn_ylw-pushpin_5\" xml:id=\"5\">"
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
				+ "<Style id=\"sn_ylw-pushpin_6\" xml:id=\"6\">"
				+ "	<IconStyle>"
				+ "		<color>ff00ff55</color>"
				+ "		<scale>1.1</scale>"
				+ "		<Icon>"
				+ "			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+ "		</Icon>"
				+ "		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+ "	</IconStyle>" + "	<LabelStyle>"
				+ "		<color>ff00ff55</color>" + "	</LabelStyle>" + "</Style>"
				+ "<Folder xml:id=\"1\" mesh4x:parentId=\"2\">"
				+ "	<name>Folder1</name>" + "	<Folder xml:id=\"2\">"
				+ "		<name>Folder2</name>" + "	</Folder>" + "</Folder>"
				+ "<Placemark xml:id=\"3\" mesh4x:parentId=\"2\">"
				+ "	<name>B</name>" + "</Placemark>" + "</Document>" + "</kml>";

		XMLHelper.write(localXMLUpdated, file);
		domAdapter.beginSync();
		domAdapter.endSync();
		
		Document doc = XMLHelper.readDocument(file);

		List<Item> items = domAdapter.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(12, items.size());

		Item item = domAdapter.get("3");
		Assert.assertNotNull(item);
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(doc, item.getContent().getPayload()));

		item = domAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(doc, item.getContent().getPayload()));

		item = domAdapter.get("2");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", HierarchyXMLViewElement.getMeshParentId(doc, item.getContent().getPayload()));
	}

	@Test(expected = MeshException.class)
	public void shouldThrowsExceptionBecauseFileHasNotElements() {
		DOMAdapter domAdapter = makeNewDOMAdapter(getFileWithOutElements());
		domAdapter.beginSync();
	}

	@Test(expected = MeshException.class)
	public void shouldThrowsExceptionIfFileExistButHasNotKMLElement() {
		DOMAdapter domAdapter = makeNewDOMAdapter(getFileWithOutKMLElement());
		domAdapter.beginSync();
	}

	@Test(expected = MeshException.class)
	public void shouldThrowsExceptionIfFileExistHasKMLElementButHasNotDocumentElement() {
		DOMAdapter domAdapter = makeNewDOMAdapter(getFileWithOutDocumentElement());
		domAdapter.beginSync();
	}

	@Test
	public void shouldReturnsAuthenticatedUser() {
		DOMAdapter domAdapter = makeNewDOMAdapter(getDummyFileName());
		String user = domAdapter.getAuthenticatedUser();
		Assert.assertNotNull(user);
		Assert.assertEquals(user, NullIdentityProvider.INSTANCE
				.getAuthenticatedUser());
	}

	@Test
	public void shouldNotSupportMerge() {
		DOMAdapter domAdapter = makeNewDOMAdapter(getDummyFileName());
		Assert.assertFalse(domAdapter instanceof ISupportMerge);
	}

	@Test
	public void shouldReturnsFriendlyName() {
		DOMAdapter domAdapter = makeNewDOMAdapter(getDummyFileName());
		String name = domAdapter.getFriendlyName();
		Assert.assertNotNull(name);
		Assert.assertEquals(name, "KML Adapter");
	}

	private List<Element> getElements(String xpathExpression, Document document)
			throws JaxenException {
		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		return XMLHelper.selectElements(xpathExpression, document
				.getRootElement(), namespaces);
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
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		Item item = domAdapter.get(IdGenerator.INSTANCE.newID());
		Assert.assertNull(item);
	}

	@Test
	public void shouldGetReturnsItem() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		assertGetItemPayload(domAdapter, "2", "1", "Folder", null, "Folder2");
	}

	@Test
	public void shouldGetReturnsNullContentBecauseItemWasDeleted() {
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"3\" deleted=\"true\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
				+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>" + "</mesh4x:sync>" + "</ExtendedData>"
				+ "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		Item item = domAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
	}

	@Test
	public void shouldGetReturnsNullContentBecauseItemWasDeletedExternally() {
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<name>dummy</name>"
				+ "<ExtendedData>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>" + "</mesh4x:sync>" + "</ExtendedData>"
				+ "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		Item item = domAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(2, item.getSync().getUpdates());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetTrowsExceptionBecauseParameterIsNull() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		domAdapter.get(null);
	}

	// ADD
	@Test(expected = IllegalArgumentException.class)
	public void shouldAddTrowsExceptionBecauseParameterIsNull() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		domAdapter.add(null);
	}

	@Test
	public void shouldAdd() throws DocumentException {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		String syncID = IdGenerator.INSTANCE.newID();

		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document>"
				+ "<Placemark xml:id=\"" + syncID + "\">" + "	<name>MYHouse</name>" + "</Placemark>"
				+ "</Document>" + "</kml>";

		Element payload = DocumentHelper.parseText(localXML).getRootElement()
				.element("Document").element("Placemark");

		KMLContent kmlContent = new KMLContent(payload, syncID);

		Sync sync = new Sync(syncID, "JMT", TestHelper.now(), false);
		Item item = new Item(kmlContent, sync);
		domAdapter.add(item);

		assertGetItemPayload(domAdapter, syncID, null, "Placemark", null,
				"MYHouse");
	}

	@Test
	public void shouldAddDeletedItem() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		String syncID = IdGenerator.INSTANCE.newID();

		Sync sync = new Sync(syncID, "JMT", TestHelper.now(), true);
		Item item = new Item(new NullContent(syncID), sync);
		domAdapter.add(item);

		item = domAdapter.get(syncID);
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
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		domAdapter.delete(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDeleteTrowsExceptionBecauseParameterIsStringEmpty() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		domAdapter.delete("");
	}

	@Test
	public void shouldDeleteAddSyncDeleteDataBecauseItemDoesNotExist() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		String syncID = IdGenerator.INSTANCE.newID();
		Item item = domAdapter.get(syncID);
		Assert.assertNull(item);

		domAdapter.delete(syncID);

		item = domAdapter.get(syncID);
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
				+ "<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<name>dummy</name>"
				+ "<ExtendedData>"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"3\" deleted=\"true\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
				+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>" + "</mesh4x:sync>" + "</ExtendedData>"
				+ "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		Item item = domAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(3, item.getSync().getUpdates());

		domAdapter.delete("1");

		item = domAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(3, item.getSync().getUpdates());
	}

	@Test
	public void shouldDelete() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		Item item = domAdapter.get("3");
		Assert.assertNotNull(item);
		Assert.assertEquals("3", item.getSyncId());
		Assert.assertFalse(item.getSync().isDeleted());
		Assert.assertEquals(4, item.getSync().getUpdates());

		domAdapter.delete("3");

		item = domAdapter.get("3");
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
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		domAdapter.update(null);
	}

	@Test
	public void shouldUpdateDeletedItem() {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		String syncID = "3";
		Item item = domAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertFalse(item.getSync().isDeleted());

		Sync sync = item.getSync();
		sync.update("JMT", TestHelper.now(), true);

		item = new Item(new NullContent(syncID), sync);
		domAdapter.update(item);

		item = domAdapter.get(syncID);
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
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
				+ "<sx:sync id=\"1\" updates=\"3\" deleted=\"true\" noconflicts=\"false\">"
				+ "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
				+ "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
				+ "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
				+ "</sx:sync>" + "</mesh4x:sync>" + "</ExtendedData>"
				+ "</Document>" + "</kml>";

		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		Item item = domAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(3, item.getSync().getUpdates());

		item.getSync().update("jmt", TestHelper.now(), true);
		domAdapter.update(item);

		item = domAdapter.get("1");
		Assert.assertNotNull(item);
		Assert.assertEquals("1", item.getSyncId());
		Assert.assertTrue(item.getSync().isDeleted());
		Assert.assertTrue(item.getContent() instanceof NullContent);
		Assert.assertEquals(4, item.getSync().getUpdates());
	}

	@Test
	public void shouldUpdateItem() throws DocumentException {
		File file = TestHelper.makeNewXMLFile(xmlWithMeshInfo, ".kml");
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();

		String syncID = "3";
		Item item = domAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertFalse(item.getSync().isDeleted());
		Assert.assertEquals(4, item.getSync().getUpdates());

		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+ "<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "	<Placemark xml:id=\"3\" mesh4x:parentId=\"2\">"
				+ "		<name>MuMu</name>" + "	</Placemark>" + "</Document>"
				+ "</kml>";

		Element payload = DocumentHelper.parseText(localXML).getRootElement()
				.element("Document").element("Placemark");

		KMLContent content = new KMLContent(payload, syncID);
		item = new Item(content, item.getSync().update("jmt", TestHelper.now(),
				false));
		domAdapter.update(item);

		item = domAdapter.get(syncID);
		Assert.assertNotNull(item);
		Assert.assertEquals(syncID, item.getSyncId());
		Assert.assertFalse(item.getSync().isDeleted());
		Assert.assertEquals(5, item.getSync().getUpdates());
		Assert.assertEquals("MuMu", item.getContent().getPayload().element(
				"name").getText());
	}
}