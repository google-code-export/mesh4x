package com.mesh4j.sync.adapters.kml;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.dom.IMeshDOM;
import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.dom.parsers.HierarchyXMLViewElement;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.security.NullIdentityProvider;

public class HierarchyXMLViewElementTests {

	@Test
	public void shouldNormalize(){
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		
		Assert.assertNull(hierarchy.normalize(null));
		
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertSame(element, hierarchy.normalize(element));
		
		element = DocumentHelper.createElement("hierarchy");
		Assert.assertNull(hierarchy.normalize(element));
	}
	
	@Test
	public void shouldUpdateFails(){
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		
		Document document = DocumentHelper.createDocument();
		Element hierarchyElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY);
		
		Assert.assertNull(hierarchy.update(null, hierarchyElement, hierarchyElement));
		Assert.assertNull(hierarchy.update(document, null, hierarchyElement));
		Assert.assertNull(hierarchy.update(document, hierarchyElement, null));
		
		Element folderElement = DocumentHelper.createElement(KmlNames.KML_QNAME_FOLDER);
		Assert.assertNull(hierarchy.update(document, hierarchyElement, folderElement));
		Assert.assertNull(hierarchy.update(document, folderElement, hierarchyElement));
	}
	
	@Test
	public void shouldUpdateNoEffectSameParentRoot() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
		+"<Document>"
			+"<name>a1.kml</name>"
			+"<visibility>0</visibility>"
			+"<open>1</open>"
			+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"<mesh4x:sync version=\"1282223342\">"
					+"<sx:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
						+"<sx:history sequence=\"1\" when=\"2008-05-29T04:00:29Z\" by=\"vcc-PC_jtondato\"></sx:history>"
					+"</sx:sync>"
				+"</mesh4x:sync>"
				+"<mesh4x:sync version=\"-952789581\">"
					+"<sx:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"
						+"<sx:history sequence=\"1\" when=\"2008-05-29T04:00:29Z\" by=\"vcc-PC_jtondato\"></sx:history>"
					+"</sx:sync>"
				+"</mesh4x:sync>"
			+"</ExtendedData>"
			+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
				+"<name>c1</name>"
				+"<visibility>0</visibility>"
				+"<LookAt>"
					+"<longitude>-95.25772959590469</longitude>"
					+"<latitude>38.95773961787486</latitude>"
					+"<altitude>0</altitude>"
					+"<range>11031061.48259681</range>"
					+"<tilt>0</tilt>"
					+"<heading>0.001110632503188744</heading>"
				+"</LookAt>"
				+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
				+"<Point>"
					+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
				+"</Point>"
			+"</Placemark>"
		+"</Document>"
		+"</kml>";

		Document document = DocumentHelper.parseText(xml);
		Element hierarchyElement = document
			.getRootElement()
			.element("Document")
			.element("ExtendedData")
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		hierarchy.setDOM(new MockDOM());
		
		String oldXML = hierarchyElement.asXML();
		Assert.assertSame(hierarchyElement, hierarchy.update(document, hierarchyElement, hierarchyElement.createCopy()));
		Assert.assertEquals(oldXML, hierarchyElement.asXML());
	}
	
	@Test
	public void shouldUpdateNoEffectSameParent() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
		+"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
			+"<name>a1.kml</name>"
			+"<visibility>0</visibility>"
			+"<open>1</open>"
			+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"1\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
			+"</ExtendedData>"
			+"<Folder xml:id=\"1\">"
				+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
					+"<name>c1</name>"
					+"<visibility>0</visibility>"
					+"<LookAt>"
						+"<longitude>-95.25772959590469</longitude>"
						+"<latitude>38.95773961787486</latitude>"
						+"<altitude>0</altitude>"
						+"<range>11031061.48259681</range>"
						+"<tilt>0</tilt>"
						+"<heading>0.001110632503188744</heading>"
					+"</LookAt>"
					+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
					+"<Point>"
						+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
					+"</Point>"
				+"</Placemark>"
			+"</Folder>"
		+"</Document>"
		+"</kml>";

		Document document = DocumentHelper.parseText(xml);
		Element hierarchyElement = document
			.getRootElement()
			.element("Document")
			.element("ExtendedData")
			.element(MeshNames.MESH_QNAME_HIERARCHY);
		
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		hierarchy.setDOM(new MockDOM());
		
		String oldXML = hierarchyElement.asXML();
		Assert.assertSame(hierarchyElement, hierarchy.update(document, hierarchyElement, hierarchyElement.createCopy()));
		Assert.assertEquals(oldXML, hierarchyElement.asXML());
	}

	@Test
	public void shouldMoveElementFromRootToFolder() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<name>1</name>"
				+"</Folder>"
				+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
				+"<name>c1</name>"
				+"<visibility>0</visibility>"
				+"<LookAt>"
					+"<longitude>-95.25772959590469</longitude>"
					+"<latitude>38.95773961787486</latitude>"
					+"<altitude>0</altitude>"
					+"<range>11031061.48259681</range>"
					+"<tilt>0</tilt>"
					+"<heading>0.001110632503188744</heading>"
				+"</LookAt>"
				+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
				+"<Point>"
					+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
				+"</Point>"
			+"</Placemark>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Element hierarchyElement = document
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY);
			
			Assert.assertNull(document.getRootElement().element("Document").element("Folder").element("Placemark"));

			Element placemark = document.getRootElement().element("Document").element("Placemark");
			Assert.assertNotNull(placemark);

			
			String xmlUpdate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+"<Document>"
					+"<name>a1.kml</name>"
					+"<visibility>0</visibility>"
					+"<open>1</open>"
					+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
						+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"1\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
					+"</ExtendedData>"
				+"</Document>"
				+"</kml>";

			Element hierarchyElementUpdated = DocumentHelper.parseText(xmlUpdate)
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY);

			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new MockDOM());
			
			String oldXML = hierarchyElement.asXML();
			Assert.assertSame(hierarchyElement, hierarchy.update(document, hierarchyElement, hierarchyElementUpdated));
			Assert.assertFalse(oldXML.equals(hierarchyElement.asXML()));
						
			Assert.assertSame(hierarchyElement, document.getRootElement().element("Document").element("ExtendedData").element(MeshNames.MESH_QNAME_HIERARCHY));
			Assert.assertEquals("da05747a-1bbf-4fce-8415-9095df0743ac", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
			Assert.assertEquals("1", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID));
			Assert.assertEquals("0213a27a-8262-493d-bd1e-5268c6fe401e", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID));
			
			Assert.assertNotNull(document.getRootElement().element("Document").element("Folder").element("Placemark"));
			Assert.assertSame(placemark, document.getRootElement().element("Document").element("Folder").element("Placemark"));
	}
	
	@Test
	public void shouldMoveElementFromFolderToRoot() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"1\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<name>1</name>"
					+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Element hierarchyElement = document
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY);
			
			Assert.assertNull(document.getRootElement().element("Document").element("Placemark"));

			Element placemark = document.getRootElement().element("Document").element("Folder").element("Placemark");
			Assert.assertNotNull(placemark);

			
			String xmlUpdate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+"<Document>"
					+"<name>a1.kml</name>"
					+"<visibility>0</visibility>"
					+"<open>1</open>"
					+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
						+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
					+"</ExtendedData>"
				+"</Document>"
				+"</kml>";

			Element hierarchyElementUpdated = DocumentHelper.parseText(xmlUpdate)
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY);

			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new MockDOM());
			
			String oldXML = hierarchyElement.asXML();
			Assert.assertSame(hierarchyElement, hierarchy.update(document, hierarchyElement, hierarchyElementUpdated));
			Assert.assertFalse(oldXML.equals(hierarchyElement.asXML()));
						
			Assert.assertSame(hierarchyElement, document.getRootElement().element("Document").element("ExtendedData").element(MeshNames.MESH_QNAME_HIERARCHY));
			Assert.assertEquals("da05747a-1bbf-4fce-8415-9095df0743ac", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
			Assert.assertEquals(null, hierarchyElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID));
			Assert.assertEquals("0213a27a-8262-493d-bd1e-5268c6fe401e", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID));
			
			Assert.assertNull(document.getRootElement().element("Document").element("Folder").element("Placemark"));
			Assert.assertNotNull(document.getRootElement().element("Document").element("Placemark"));
			Assert.assertSame(placemark, document.getRootElement().element("Document").element("Placemark"));
	}
	
	@Test
	public void shouldMoveElementFromFolder1ToFolder2() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"1\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<name>1</name>"
					+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
				+"<Folder xml:id=\"2\">"
					+"<name>2</name>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Element hierarchyElement = document
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY);
			
			Element folder1 = (Element)document.getRootElement().element("Document").elements("Folder").get(0);
			Element folder2 = (Element)document.getRootElement().element("Document").elements("Folder").get(1);
			
			Element placemark = folder1.element("Placemark");
			Assert.assertNotNull(placemark);
			
			String xmlUpdate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+"<Document>"
					+"<name>a1.kml</name>"
					+"<visibility>0</visibility>"
					+"<open>1</open>"
					+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
						+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"2\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
					+"</ExtendedData>"
				+"</Document>"
				+"</kml>";

			Element hierarchyElementUpdated = DocumentHelper.parseText(xmlUpdate)
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY);

			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new MockDOM());
			
			String oldXML = hierarchyElement.asXML();
			Assert.assertSame(hierarchyElement, hierarchy.update(document, hierarchyElement, hierarchyElementUpdated));
			Assert.assertFalse(oldXML.equals(hierarchyElement.asXML()));
						
			Assert.assertSame(hierarchyElement, document.getRootElement().element("Document").element("ExtendedData").element(MeshNames.MESH_QNAME_HIERARCHY));
			Assert.assertEquals("da05747a-1bbf-4fce-8415-9095df0743ac", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
			Assert.assertEquals("2", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID));
			Assert.assertEquals("0213a27a-8262-493d-bd1e-5268c6fe401e", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID));
			
			Element placemarkUpdated = ((Element)document.getRootElement().element("Document").elements("Folder").get(1)).element("Placemark");
			Assert.assertNotNull(placemarkUpdated);
			Assert.assertSame(placemark, placemarkUpdated);
			Assert.assertNull(folder1.element("Placemark"));
			Assert.assertSame(folder2, placemark.getParent());

	}

	@Test 
	public void shouldAddFails(){
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		
		Document document = DocumentHelper.createDocument();
		Element hierarchyElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY);
		
		Assert.assertNull(hierarchy.add(null, hierarchyElement));
		Assert.assertNull(hierarchy.add(document, null));
		
		Element folderElement = DocumentHelper.createElement(KmlNames.KML_QNAME_FOLDER);
		Assert.assertNull(hierarchy.add(document, folderElement));
	}
	
	@Test
	public void shouldAddToRoot() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"</ExtendedData>"
				+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
					+"<name>c1</name>"
					+"<visibility>0</visibility>"
					+"<LookAt>"
						+"<longitude>-95.25772959590469</longitude>"
						+"<latitude>38.95773961787486</latitude>"
						+"<altitude>0</altitude>"
						+"<range>11031061.48259681</range>"
						+"<tilt>0</tilt>"
						+"<heading>0.001110632503188744</heading>"
					+"</LookAt>"
					+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
					+"<Point>"
						+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
					+"</Point>"
				+"</Placemark>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);

			Element placemark = document.getRootElement().element("Document").element("Placemark");
			Assert.assertNotNull(placemark);
			
			String xmlAdd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+"<Document>"
					+"<name>a1.kml</name>"
					+"<visibility>0</visibility>"
					+"<open>1</open>"
					+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
						+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
					+"</ExtendedData>"
				+"</Document>"
				+"</kml>";

			Element hierarchyElementToAdd = DocumentHelper.parseText(xmlAdd)
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY)
				.createCopy();

			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new MockDOM());
			
			Assert.assertSame(hierarchyElementToAdd, hierarchy.add(document, hierarchyElementToAdd));
						
			Assert.assertSame(hierarchyElementToAdd, document.getRootElement().element("Document").element("ExtendedData").element(MeshNames.MESH_QNAME_HIERARCHY));
			Assert.assertEquals("da05747a-1bbf-4fce-8415-9095df0743ac", hierarchyElementToAdd.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
			Assert.assertEquals(null, hierarchyElementToAdd.attributeValue(MeshNames.MESH_QNAME_PARENT_ID));
			Assert.assertEquals("0213a27a-8262-493d-bd1e-5268c6fe401e", hierarchyElementToAdd.attributeValue(MeshNames.MESH_QNAME_CHILD_ID));
			
			Assert.assertNotNull(document.getRootElement().element("Document").element("Placemark"));
			Assert.assertSame(placemark, document.getRootElement().element("Document").element("Placemark"));
	}
	
	@Test
	public void shouldAddToFolder() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
				+"</Folder>"
				+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
					+"<name>c1</name>"
					+"<visibility>0</visibility>"
					+"<LookAt>"
						+"<longitude>-95.25772959590469</longitude>"
						+"<latitude>38.95773961787486</latitude>"
						+"<altitude>0</altitude>"
						+"<range>11031061.48259681</range>"
						+"<tilt>0</tilt>"
						+"<heading>0.001110632503188744</heading>"
					+"</LookAt>"
					+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
					+"<Point>"
						+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
					+"</Point>"
				+"</Placemark>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);

			Element placemark = document.getRootElement().element("Document").element("Placemark");
			Assert.assertNotNull(placemark);
			
			String xmlAdd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+"<Document>"
					+"<name>a1.kml</name>"
					+"<visibility>0</visibility>"
					+"<open>1</open>"
					+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
						+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"1\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
					+"</ExtendedData>"
				+"</Document>"
				+"</kml>";

			Element hierarchyElementToAdd = DocumentHelper.parseText(xmlAdd)
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY)
				.createCopy();

			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new MockDOM());
			
			Assert.assertSame(hierarchyElementToAdd, hierarchy.add(document, hierarchyElementToAdd));
						
			Assert.assertSame(hierarchyElementToAdd, document.getRootElement().element("Document").element("ExtendedData").element(MeshNames.MESH_QNAME_HIERARCHY));
			Assert.assertEquals("da05747a-1bbf-4fce-8415-9095df0743ac", hierarchyElementToAdd.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
			Assert.assertEquals("1", hierarchyElementToAdd.attributeValue(MeshNames.MESH_QNAME_PARENT_ID));
			Assert.assertEquals("0213a27a-8262-493d-bd1e-5268c6fe401e", hierarchyElementToAdd.attributeValue(MeshNames.MESH_QNAME_CHILD_ID));
			
			Assert.assertNull(document.getRootElement().element("Document").element("Placemark"));
			Assert.assertNotNull(document.getRootElement().element("Document").element("Folder").element("Placemark"));
			Assert.assertSame(placemark, document.getRootElement().element("Document").element("Folder").element("Placemark"));
	}

	@Test
	public void shouldGetParentID() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"5\" mesh4x:childId=\"1\"></mesh4x:hierarchy>"
					+"<mesh4x:hierarchy xml:id=\"6\" mesh4x:parentId=\"1\" mesh4x:childId=\"2\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<Placemark xml:id=\"2\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			
			Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(document, null));
			Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(null, document.getRootElement().element("Document").element("Folder")));
			Assert.assertEquals(null, HierarchyXMLViewElement.getMeshParentId(document, document.getRootElement().element("Document").element("Folder")));
			Assert.assertEquals("1", HierarchyXMLViewElement.getMeshParentId(document, document.getRootElement().element("Document").element("Folder").element("Placemark")));
		}

	@Test
	public void shouldDeleteFails(){
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		
		Document document = DocumentHelper.createDocument(DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY));
		String xml = document.asXML();
		Element hierarchyElement = document.getRootElement();
		
		hierarchy.delete(null, hierarchyElement);
		Assert.assertEquals(xml, document.asXML());
		
		hierarchy.delete(document, null);
		Assert.assertEquals(xml, document.asXML());
		
		Element folderElement = DocumentHelper.createElement(KmlNames.KML_QNAME_FOLDER);
		hierarchy.delete(document, folderElement);
		Assert.assertEquals(xml, document.asXML());
		
		hierarchy.delete(document, hierarchyElement);
		Assert.assertEquals(xml, document.asXML());

	}
	
	@Test
	public void shouldDelete() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
			+"</Document>"
			+"</kml>";
		
		Document document = DocumentHelper.parseText(xml);
		Element hierarchyElement = document.getRootElement().element("Document").element("ExtendedData").element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNotNull(hierarchyElement.getParent());
		
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		hierarchy.delete(document, hierarchyElement);
		
		Assert.assertNull(hierarchyElement.getParent());
		Assert.assertNull(document.getRootElement().element("Document").element("ExtendedData").element(MeshNames.MESH_QNAME_HIERARCHY));

	}
	
	@Test
	public void shouldGetAll() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"1\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
					+"<mesh4x:hierarchy xml:id=\"2\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
					+"<mesh4x:hierarchy xml:id=\"3\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
					+"<mesh4x:hierarchy xml:id=\"4\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
			+"</Document>"
			+"</kml>";
		
		Document document = DocumentHelper.parseText(xml);
		
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		
		List<Element> elements = hierarchy.getAllElements(document);
		Assert.assertEquals(4, elements.size());
		Assert.assertEquals("1", elements.get(0).attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
		Assert.assertEquals("2", elements.get(1).attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
		Assert.assertEquals("3", elements.get(2).attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
		Assert.assertEquals("4", elements.get(3).attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
	}

	@Test
	public void shouldGetAllReturnsEmptyList() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"</ExtendedData>"
			+"</Document>"
			+"</kml>";
		
		Document document = DocumentHelper.parseText(xml);
		
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		
		List<Element> elements = hierarchy.getAllElements(document);
		Assert.assertEquals(0, elements.size());
	}
	
	@Test
	public void shouldCreateHierarchyIfAbsentFails(){
		
		Document document = DocumentHelper.createDocument(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PLACEMARK));
		String xml = document.asXML();
		Element elementChild = document.getRootElement();
				
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		hierarchy.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createKMLView()));
		
		hierarchy.createHierarchyIfAbsent(null, elementChild);
		Assert.assertEquals(xml, document.asXML());
		
		hierarchy.createHierarchyIfAbsent(document, null);
		Assert.assertEquals(xml, document.asXML());
		
		hierarchy.createHierarchyIfAbsent(document, elementChild);
		Assert.assertEquals(xml, document.asXML());
	}

	@Test
	public void shouldCreateHierarchyIfAbsentNoEffectBecauseHierarchyExists() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-\" mesh4x:childId=\"1\"></mesh4x:hierarchy>"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"1\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			String oldXML = document.asXML();
			
			Element elementChild = document
				.getRootElement()
				.element("Document")
				.element("Folder");
			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createKMLView()));

			hierarchy.createHierarchyIfAbsent(document, elementChild);
			
			Assert.assertEquals(oldXML, document.asXML());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateHierarchyIfAbsentFailsBecauseViewElementDoesNotContainKMLDomReference() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Element elementChild = document
				.getRootElement()
				.element("Document")
				.element("Folder");
			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.createHierarchyIfAbsent(document, elementChild);
	}
	
	@Test
	public void shouldCreateHierarchyIfAbsent() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<Placemark xml:id=\"2\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Element childElement = document
				.getRootElement()
				.element("Document")
				.element("Folder")
				.element("Placemark");
			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createKMLView()));
			hierarchy.createHierarchyIfAbsent(document, childElement);
			
			Assert.assertEquals("1", HierarchyXMLViewElement.getMeshParentId(document, childElement));
			
			Element hierarchyElement = HierarchyXMLViewElement.getHierarchyElementByChild(document, "2");
			Assert.assertNotNull(hierarchyElement);
			Assert.assertNotNull(hierarchyElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
			Assert.assertEquals("1", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID));
			Assert.assertEquals("2", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID));
			
			Assert.assertNotNull(hierarchy.getDOM().getSync(hierarchyElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID)));
	}

	@Test
	public void shouldGetHierarchyElementByChildFails() throws DocumentException{
		Assert.assertNull(HierarchyXMLViewElement.getHierarchyElementByChild(DocumentHelper.createDocument(), null));
		Assert.assertNull(HierarchyXMLViewElement.getHierarchyElementByChild(null, "2"));
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Assert.assertNull(HierarchyXMLViewElement.getHierarchyElementByChild(document, "1"));
	}
	
	@Test
	public void shouldGetHierarchyElementByChild() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-\" mesh4x:childId=\"1\"></mesh4x:hierarchy>"
				+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"1\" mesh4x:childId=\"2\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<Placemark xml:id=\"2\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Assert.assertNotNull(HierarchyXMLViewElement.getHierarchyElementByChild(document, "2"));
			Assert.assertEquals("da05747a-1bbf-4fce-8415-9095df0743ac", HierarchyXMLViewElement.getHierarchyElementByChild(document, "2").attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
	}

	@Test
	public void shouldRefreshFails(){
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		
		Document document = DocumentHelper.createDocument();
		Element hierarchyElement = DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY);
		
		Assert.assertNull(hierarchy.refresh(null, hierarchyElement));
		Assert.assertNull(hierarchy.refresh(document, null));
		
		Element folderElement = DocumentHelper.createElement(KmlNames.KML_QNAME_FOLDER);
		Assert.assertNull(hierarchy.refresh(document, folderElement));

	}
	
	@Test
	public void shouldRefreshNoChange() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:parentId=\"1\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

		Document document = DocumentHelper.parseText(xml);
		Element hierarchyElement = document
			.getRootElement()
			.element("Document")
			.element("ExtendedData")
			.element(MeshNames.MESH_QNAME_HIERARCHY);
	
		HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
		hierarchy.setDOM(new MockDOM());
			
		String oldXML = document.asXML();
		Assert.assertSame(hierarchyElement, hierarchy.refresh(document, hierarchyElement));
		Assert.assertEquals(oldXML, document.asXML());
	}
	
//	@Test(expected=IllegalArgumentException.class)
//	public void shouldRefreshFailsBecauseKMLDom() throws DocumentException{
//		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
//			+"<Document>"
//				+"<name>a1.kml</name>"
//				+"<visibility>0</visibility>"
//				+"<open>1</open>"
//				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
//					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
//				+"</ExtendedData>"
//				+"<Folder xml:id=\"1\">"
//					+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
//						+"<name>c1</name>"
//						+"<visibility>0</visibility>"
//						+"<LookAt>"
//							+"<longitude>-95.25772959590469</longitude>"
//							+"<latitude>38.95773961787486</latitude>"
//							+"<altitude>0</altitude>"
//							+"<range>11031061.48259681</range>"
//							+"<tilt>0</tilt>"
//							+"<heading>0.001110632503188744</heading>"
//						+"</LookAt>"
//						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
//						+"<Point>"
//							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
//						+"</Point>"
//					+"</Placemark>"
//				+"</Folder>"
//			+"</Document>"
//			+"</kml>";
//
//			Document document = DocumentHelper.parseText(xml);
//			Element hierarchyElement = document
//				.getRootElement()
//				.element("Document")
//				.element("ExtendedData")
//				.element(MeshNames.MESH_QNAME_HIERARCHY);
//			
//			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
//			hierarchy.refresh(document, hierarchyElement);
//	}

	@Test
	public void shouldRefreshChangeHierarchyBecauseChildParentWasChanged() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
					+"<Placemark xml:id=\"0213a27a-8262-493d-bd1e-5268c6fe401e\">"
						+"<name>c1</name>"
						+"<visibility>0</visibility>"
						+"<LookAt>"
							+"<longitude>-95.25772959590469</longitude>"
							+"<latitude>38.95773961787486</latitude>"
							+"<altitude>0</altitude>"
							+"<range>11031061.48259681</range>"
							+"<tilt>0</tilt>"
							+"<heading>0.001110632503188744</heading>"
						+"</LookAt>"
						+"<styleUrl>#msn_ylw-pushpin_75013293-0e6e-44e1-aac4-1a206f10f113</styleUrl>"
						+"<Point>"
							+"<coordinates>-95.25772959590469,38.95773961787486,0</coordinates>"
						+"</Point>"
					+"</Placemark>"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Element hierarchyElement = document
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY);
			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createKMLView()));
			
			Assert.assertSame(hierarchyElement, hierarchy.refresh(document, hierarchyElement));

			hierarchyElement = HierarchyXMLViewElement.getHierarchyElementByChild(document, "0213a27a-8262-493d-bd1e-5268c6fe401e");
			Assert.assertNotNull(hierarchyElement);
			Assert.assertNotNull(hierarchyElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
			Assert.assertEquals("1", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID));
			Assert.assertEquals("0213a27a-8262-493d-bd1e-5268c6fe401e", hierarchyElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID));
			
			//Assert.assertNotNull(hierarchy.getKmlDOM().getSync(hierarchyElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID)));
			
	}

	@Test
	public void shouldRefreshDeleteHierarchyBecauseChildWasDeleted() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document>"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"
					+"<mesh4x:hierarchy xml:id=\"da05747a-1bbf-4fce-8415-9095df0743ac\" mesh4x:childId=\"0213a27a-8262-493d-bd1e-5268c6fe401e\"></mesh4x:hierarchy>"
				+"</ExtendedData>"
				+"<Folder xml:id=\"1\">"
				+"</Folder>"
			+"</Document>"
			+"</kml>";

			Document document = DocumentHelper.parseText(xml);
			Element hierarchyElement = document
				.getRootElement()
				.element("Document")
				.element("ExtendedData")
				.element(MeshNames.MESH_QNAME_HIERARCHY);
			
			HierarchyXMLViewElement hierarchy = new HierarchyXMLViewElement();
			hierarchy.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, DOMLoaderFactory.createKMLView()));
			
			Assert.assertSame(null, hierarchy.refresh(document, hierarchyElement));

			hierarchyElement = HierarchyXMLViewElement.getHierarchyElementByChild(document, "0213a27a-8262-493d-bd1e-5268c6fe401e");
			Assert.assertNull(hierarchyElement);
			
			//Assert.assertNotNull(hierarchy.getKmlDOM().getSync("da05747a-1bbf-4fce-8415-9095df0743ac"));
			//Assert.assertTrue(hierarchy.getKmlDOM().getSync("da05747a-1bbf-4fce-8415-9095df0743ac").isDeleted());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementIsNull(){
		HierarchyXMLViewElement viewElement = new HierarchyXMLViewElement();
		viewElement.clean(DocumentHelper.createDocument(), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementParentIsNull(){
		Element element = DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY);
		
		HierarchyXMLViewElement viewElement = new HierarchyXMLViewElement();
		viewElement.clean(null, element);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementInvalidElementType() throws DocumentException{
		String xml = "<foo><bar></bar></foo>";
		Document document = DocumentHelper.parseText(xml);
		
		Element element = document.getRootElement().element("bar");
		Assert.assertNotNull(element);
				
		HierarchyXMLViewElement viewElement = new HierarchyXMLViewElement();
		viewElement.clean(document, element);
	}
	
	@Test
	public void shouldClean() throws DocumentException{
		String xml = "<foo><mesh4x:hierarchy xmlns:mesh4x=\"http://mesh4x.org/kml\"></mesh4x:hierarchy></foo>";
		Document document = DocumentHelper.parseText(xml);

		Element element = document.getRootElement().element(MeshNames.MESH_QNAME_HIERARCHY);
		Assert.assertNotNull(element);
		
		HierarchyXMLViewElement viewElement = new HierarchyXMLViewElement();
		viewElement.clean(document, element);
		
		Assert.assertNull(document.getRootElement().element(MeshNames.MESH_QNAME_HIERARCHY));
	}
	
	private class MockDOM implements IMeshDOM {

		@Override
		public Element addElement(Element element) {
			return null;
		}

		@Override
		public String asXML() {
			return null;
		}

		@Override
		public IContent createContent(Element element, String syncID) {
			return null;
		}

		@Override
		public void deleteElement(String id) {
		}

		@Override
		public List<Element> getAllElements() {
			return null;
		}

		@Override
		public List<SyncInfo> getAllSyncs() {
			return null;
		}

		@Override
		public Element getContentRepository(Document document) {
			return document.getRootElement().element("Document");
		}

		@Override
		public Element getElement(String id) {
			return null;
		}

		@Override
		public IIdentityProvider getIdentityProvider() {
			return null;
		}

		@Override
		public String getMeshSyncId(Element element) {
			return null;
		}

		@Override
		public SyncInfo getSync(String syncId) {
			return null;
		}

		@Override
		public Element getSyncRepository(Document document) {
			return document.getRootElement().element("Document").element("ExtendedData");
		}

		@Override
		public String getType() {
			return null;
		}

		@Override
		public boolean isValid(Element element) {
			return false;
		}

		@Override
		public String newID() {
			return null;
		}

		@Override
		public Element normalize(Element element) {
			return null;
		}

		@Override
		public void normalize() {
		}

		@Override
		public IContent normalizeContent(IContent content) {
			return null;
		}

		@Override
		public Document toDocument() {
			return null;
		}

		@Override
		public Element updateElement(Element element) {
			return null;
		}

		@Override
		public void updateMeshStatus() {
		}

		@Override
		public void updateSync(SyncInfo syncInfo) {
		}

		@Override
		public void clean() {
		}

		@Override
		public void purgue() {			
		}
	}
}
