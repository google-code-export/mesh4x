package com.mesh4j.sync.adapters.dom;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.kml.DOMLoaderFactory;
import com.mesh4j.sync.adapters.kml.KMLContent;
import com.mesh4j.sync.adapters.kml.KmlNames;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.XMLHelper;

public class DOMAdapterHierarchyMeshTests {

	// TODO (jmt) tests
//	public void shouldUpdateNonExistingItem(){
//	public void shouldAddExistingItem(){
//	public void update(){
//	public void remove(){
//	public void refreshDeletedElement(){
	
	
	@Test
	public void shouldAddHierarchyOverRoot() throws DocumentException{
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
						+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
							+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
						+ "</sx:sync>"
					+ "</mesh4x:sync>"
					+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
						+ "<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
							+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
						+ "</sx:sync>"
					+ "</mesh4x:sync>"
				+ "</ExtendedData>"
				+ "<Folder xml:id=\"1\">"
					+ "	<name>Folder1</name>"
					+ "  <Placemark xml:id=\"2\">"
						+ "	  <name>B</name>"
					+ "  </Placemark>"
				+ "</Folder>"			
			+ "</Document>"
			+ "</kml>";
		
		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		
		Document doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		Element hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNull(hierarchy);
		
		Element child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);

		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNull(child);
		
		
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();
		
		String hierarchyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+ "<mesh4x:hierarchy xml:id=\"33\" mesh4x:childId=\"2\" />"
				+ "</ExtendedData>"
			+ "</Document>"
			+ "</kml>";	
		
		Element payload = DocumentHelper.parseText(hierarchyXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		
		String syncID = "33";
		IContent content = new KMLContent(payload, syncID);
		Item item = new Item(content, new Sync(syncID, "jmt", TestHelper.now(), false));
		domAdapter.add(item);
		
		Item loadedItem = domAdapter.get(syncID);
		Assert.assertNotNull(loadedItem);
		
		domAdapter.endSync();
		
		doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNotNull(hierarchy);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNull(child);
	}
	
	@Test
	public void shouldAddHierarchyOneLevel() throws DocumentException{
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
						+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
							+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
						+ "</sx:sync>"
					+ "</mesh4x:sync>"
					+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
						+ "<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
							+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
						+ "</sx:sync>"
					+ "</mesh4x:sync>"
				+ "</ExtendedData>"
				+ "<Folder xml:id=\"1\">"
					+ "	<name>Folder1</name>"
				+ "</Folder>"			
				+ "<Placemark xml:id=\"2\">"
					+ "	<name>B</name>"
				+ "</Placemark>"
			+ "</Document>" +
			"</kml>";
		
		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		
		Document doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		Element hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNull(hierarchy);
		
		Element child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNull(child);

		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);
		
		
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();
		
		String hierarchyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>dummy</name>"
			+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<mesh4x:hierarchy xml:id=\"33\" mesh4x:parentId=\"1\" mesh4x:childId=\"2\" />"
			+ "</ExtendedData>"
			+ "</Document>" +
			"</kml>";	
		
		Element payload = DocumentHelper.parseText(hierarchyXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		
		String syncID = "33";
		IContent content = new KMLContent(payload, syncID);
		Item item = new Item(content, new Sync(syncID, "jmt", TestHelper.now(), false));
		domAdapter.add(item);
		
		Item loadedItem = domAdapter.get(syncID);
		Assert.assertNotNull(loadedItem);
		
		domAdapter.endSync();
		
		doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNotNull(hierarchy);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNull(child);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);
	}
	
	@Test
	public void shouldAddHierarchyTwoLevels() throws DocumentException{
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
				+ "<name>dummy</name>"
				+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
						+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
							+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
						+ "</sx:sync>"
					+ "</mesh4x:sync>"
					+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
						+ "<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
							+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
						+ "</sx:sync>"
					+ "</mesh4x:sync>"
					+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
						+ "<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
							+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
						+ "</sx:sync>"
					+ "</mesh4x:sync>"
				+ "</ExtendedData>"
				+ "<Folder xml:id=\"1\">"
					+ "	<name>Folder1</name>"
					+ " <Folder xml:id=\"2\">"
						+ "	 <name>Folder2</name>"
					+ " </Folder>"
				+ "</Folder>"			
				+ "<Placemark xml:id=\"3\">"
					+ "	<name>B</name>"
				+ "</Placemark>"
			+ "</Document>"
			+"</kml>";
		
		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		
		Document doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		Element hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNull(hierarchy);
		
		Element child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNull(child);

		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);
		
		
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();
		
		String hierarchyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>dummy</name>"
			+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+ "<mesh4x:hierarchy xml:id=\"33\" mesh4x:parentId=\"2\" mesh4x:childId=\"3\" />"
			+ "</ExtendedData>"
			+ "</Document>" +
			"</kml>";	
		
		Element payload = DocumentHelper.parseText(hierarchyXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		
		String syncID = "33";
		IContent content = new KMLContent(payload, syncID);
		Item item = new Item(content, new Sync(syncID, "jmt", TestHelper.now(), false));
		domAdapter.add(item);
		
		Item loadedItem = domAdapter.get(syncID);
		Assert.assertNotNull(loadedItem);
		
		domAdapter.endSync();
		
		doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNotNull(hierarchy);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNull(child);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);
	}
	
	@Test
	public void shouldAddHierarchyNotEffectBecauseSameTree() throws DocumentException{
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>dummy</name>"
			+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
			+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"3\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "</ExtendedData>"
			+ "<Folder xml:id=\"1\">"
			+ "	<name>Folder1</name>"
			+ " <Folder xml:id=\"2\">"
			+ "	 <name>Folder2</name>"
			+ "		<Placemark xml:id=\"3\">"
			+ "			<name>B</name>"
			+ "		</Placemark>"
			+ " </Folder>"
			+ "</Folder>"			
			+ "</Document>" +
			"</kml>";
		
		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		
		Document doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		Element hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNull(hierarchy);
		
		Element child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);

		
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();
		
		String hierarchyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>dummy</name>"
			+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
			+ "<mesh4x:hierarchy xml:id=\"33\" mesh4x:parentId=\"2\" mesh4x:childId=\"3\" />"
			+ "</ExtendedData>"
			+ "</Document>" +
			"</kml>";	
		
		Element payload = DocumentHelper.parseText(hierarchyXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		
		String syncID = "33";
		IContent content = new KMLContent(payload, syncID);
		Item item = new Item(content, new Sync(syncID, "jmt", TestHelper.now(), false));
		domAdapter.add(item);
		
		Item loadedItem = domAdapter.get(syncID);
		Assert.assertNotNull(loadedItem);
		
		domAdapter.endSync();
		
		doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNotNull(hierarchy);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);
	}
	
	@Test
	public void shouldAddHierarchyOverRootNoEffectSameTree() throws DocumentException{
		String localXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>dummy</name>"
			+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"
			+ "<sx:sync id=\"1\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"3\">"
			+ "<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
			+ "<sx:history sequence=\"1\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
			+ "</sx:sync>"
			+ "</mesh4x:sync>"
			+ "</ExtendedData>"
			+ "<Folder xml:id=\"1\">"
			+ "	<name>Folder1</name>"
			+ "</Folder>"
			+ "<Placemark xml:id=\"2\">"
			+ "	  <name>B</name>"
			+ "</Placemark>"
			+ "</Document>" +
			"</kml>";
		
		File file = TestHelper.makeNewXMLFile(localXML, ".kml");
		
		Document doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		Element hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNull(hierarchy);
		
		Element child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNull(child);

		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);
		
		
		DOMAdapter domAdapter = makeNewDOMAdapter(file.getAbsolutePath());
		domAdapter.beginSync();
		
		String hierarchyXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+ "<Document>"
			+ "<name>dummy</name>"
			+ "<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
			+ "<mesh4x:hierarchy xml:id=\"33\" mesh4x:childId=\"2\" />"
			+ "</ExtendedData>"
			+ "</Document>" +
			"</kml>";	
		
		Element payload = DocumentHelper.parseText(hierarchyXML)
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		
		String syncID = "33";
		IContent content = new KMLContent(payload, syncID);
		Item item = new Item(content, new Sync(syncID, "jmt", TestHelper.now(), false));
		domAdapter.add(item);
		
		Item loadedItem = domAdapter.get(syncID);
		Assert.assertNotNull(loadedItem);
		
		domAdapter.endSync();
		
		doc = XMLHelper.readDocument(file);
		Assert.assertNotNull(doc);
		
		hierarchy = doc.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA)
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNotNull(hierarchy);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNotNull(child);
		
		child = doc.getRootElement()
		.element(KmlNames.KML_ELEMENT_DOCUMENT)
		.element(KmlNames.KML_ELEMENT_FOLDER)
		.element(KmlNames.KML_ELEMENT_PLACEMARK);
		Assert.assertNull(child);
	}
	
	private DOMAdapter makeNewDOMAdapter(String fileName) {
		IDOMLoader domLoader = DOMLoaderFactory.createDOMLoader(fileName,
				NullIdentityProvider.INSTANCE);
		return new DOMAdapter(domLoader);
	}
}
