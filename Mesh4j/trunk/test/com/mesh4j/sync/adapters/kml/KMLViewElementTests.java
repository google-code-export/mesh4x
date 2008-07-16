package com.mesh4j.sync.adapters.kml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.dom.parsers.FileManager;
import com.mesh4j.sync.adapters.dom.parsers.HierarchyXMLViewElement;
import com.mesh4j.sync.security.NullIdentityProvider;

public class KMLViewElementTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotCreateBecauseHierarchyViewIsNull(){
		new KMLViewElement(KmlNames.KML_QNAME_FOLDER, null, false);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotCreateBecauseQNameIsNull(){
		new KMLViewElement(null, new HierarchyXMLViewElement(), false);
	}
	
	@Test
	public void shouldRefreshUpdateStyleReferences() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<name>syncDocB.kml</name>"
				+"<ExtendedData>"
				+"</ExtendedData>"
				+"<StyleMap id=\"msn_ylw-pushpin\">"
				+"	<Pair>"
				+"		<key>normal</key>"
				+"		<styleUrl>#sn_ylw-pushpin</styleUrl>"
				+"	</Pair>"
				+"	<Pair>"
				+"		<key>highlight</key>"
				+"		<styleUrl>#sh_ylw-pushpin</styleUrl>"
				+"	</Pair>"
				+"</StyleMap>"
				+"<Style id=\"sn_ylw-pushpin\" xml:id=\"1\">"
				+"	<IconStyle>"
				+"		<color>ff00ffff</color>"
				+"		<scale>1.1</scale>"
				+"		<Icon>"
				+"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+"		</Icon>"
				+"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"	</IconStyle>"
				+"	<LabelStyle>"
				+"		<color>ff00ffff</color>"
				+"	</LabelStyle>"
				+"</Style>"
				+"<Style id=\"sh_ylw-pushpin\">"
				+"	<IconStyle>"
				+"		<color>ff00ffff</color>"
				+"		<scale>1.3</scale>"
				+"		<Icon>"
				+"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+"		</Icon>"
				+"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"	</IconStyle>"
				+"	<LabelStyle>"
				+"		<color>ff00ffff</color>"
				+"	</LabelStyle>"
				+"</Style>"
				+"<Placemark>"
				+"	<name>b</name>"
				+"	<visibility>0</visibility>"
				+"	<LookAt>"
				+"		<longitude>-95.26548319412231</longitude>"
				+"		<latitude>38.95938957105113</latitude>"
				+"		<altitude>0</altitude>"
				+"		<range>11001000</range>"
				+"		<tilt>0</tilt>"
				+"		<heading>2.981770013872047e-014</heading>"
				+"	</LookAt>"
				+"	<styleUrl>#msn_ylw-pushpin</styleUrl>"
				+"	<Point>"
				+"		<coordinates>-95.26548319412231,38.95938957105113,0</coordinates>"
				+"	</Point>"
				+"</Placemark>"
				+"</Document>"
				+"</kml>";

		Document document = DocumentHelper.parseText(xml);
		Element element = document.getRootElement().element("Document").element("Style");
		Element styleMap = document.getRootElement().element("Document").element("StyleMap");
		Assert.assertNotNull(styleMap);
		Assert.assertEquals("#sn_ylw-pushpin", styleMap.element("Pair").element("styleUrl").getText());
		
		String oldXML = document.asXML();
		
		HierarchyXMLViewElement hierarchyView = new HierarchyXMLViewElement();
		hierarchyView.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(new FileManager())));
		KMLViewElement style = new KMLViewElement(KmlNames.KML_QNAME_STYLE, hierarchyView, true);
		
		Assert.assertSame(element, style.refresh(document, element));
		Assert.assertFalse(oldXML.equals(document.asXML()));
		Assert.assertEquals("sn_ylw-pushpin", element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID));
		Assert.assertEquals("#sn_ylw-pushpin_1", styleMap.element("Pair").element("styleUrl").getText());
	}
	
	@Test
	public void shouldRefreshUpdateStyleReferencesEmulateGoogleEarth() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<name>syncDocB.kml</name>"
				+"<ExtendedData>"
				+"</ExtendedData>"
				+"<StyleMap id=\"msn_ylw-pushpin\">"
				+"	<Pair>"
				+"		<key>normal</key>"
				+"		<styleUrl>#sn_ylw-pushpin0</styleUrl>"
				+"	</Pair>"
				+"	<Pair>"
				+"		<key>highlight</key>"
				+"		<styleUrl>#sh_ylw-pushpin</styleUrl>"
				+"	</Pair>"
				+"</StyleMap>"
				+"<Style id=\"sn_ylw-pushpin0\" xml:id=\"1\" mesh4x:originalId=\"sn_ylw-pushpin\">"
				+"	<IconStyle>"
				+"		<color>ff00ffff</color>"
				+"		<scale>1.1</scale>"
				+"		<Icon>"
				+"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+"		</Icon>"
				+"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"	</IconStyle>"
				+"	<LabelStyle>"
				+"		<color>ff00ffff</color>"
				+"	</LabelStyle>"
				+"</Style>"
				+"<Style id=\"sh_ylw-pushpin\">"
				+"	<IconStyle>"
				+"		<color>ff00ffff</color>"
				+"		<scale>1.3</scale>"
				+"		<Icon>"
				+"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+"		</Icon>"
				+"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"	</IconStyle>"
				+"	<LabelStyle>"
				+"		<color>ff00ffff</color>"
				+"	</LabelStyle>"
				+"</Style>"
				+"<Placemark>"
				+"	<name>b</name>"
				+"	<visibility>0</visibility>"
				+"	<LookAt>"
				+"		<longitude>-95.26548319412231</longitude>"
				+"		<latitude>38.95938957105113</latitude>"
				+"		<altitude>0</altitude>"
				+"		<range>11001000</range>"
				+"		<tilt>0</tilt>"
				+"		<heading>2.981770013872047e-014</heading>"
				+"	</LookAt>"
				+"	<styleUrl>#msn_ylw-pushpin</styleUrl>"
				+"	<Point>"
				+"		<coordinates>-95.26548319412231,38.95938957105113,0</coordinates>"
				+"	</Point>"
				+"</Placemark>"
				+"</Document>"
				+"</kml>";

		Document document = DocumentHelper.parseText(xml);
		Element element = document.getRootElement().element("Document").element("Style");
		Element styleMap = document.getRootElement().element("Document").element("StyleMap");
		Assert.assertNotNull(styleMap);
		Assert.assertEquals("#sn_ylw-pushpin0", styleMap.element("Pair").element("styleUrl").getText());
		
		String oldXML = document.asXML();
		
		HierarchyXMLViewElement hierarchyView = new HierarchyXMLViewElement();
		hierarchyView.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(new FileManager())));
		KMLViewElement style = new KMLViewElement(KmlNames.KML_QNAME_STYLE, hierarchyView, true);
		
		Assert.assertSame(element, style.refresh(document, element));
		Assert.assertFalse(oldXML.equals(document.asXML()));
		
		Assert.assertNotNull(element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
		Assert.assertNotNull(element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID));
		Assert.assertEquals("sn_ylw-pushpin0", element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID));
		Assert.assertEquals("#"+element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID)+"_"+element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID), styleMap.element("Pair").element("styleUrl").getText());
	}
	
	@Test
	public void shouldRefreshUpdateStyleMapReferences() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
				+"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<name>syncDocB.kml</name>"
				+"<ExtendedData>"
				+"</ExtendedData>"
				+"<StyleMap id=\"msn_ylw-pushpin\" xml:id=\"1\">"
				+"	<Pair>"
				+"		<key>normal</key>"
				+"		<styleUrl>#sn_ylw-pushpin</styleUrl>"
				+"	</Pair>"
				+"	<Pair>"
				+"		<key>highlight</key>"
				+"		<styleUrl>#sh_ylw-pushpin</styleUrl>"
				+"	</Pair>"
				+"</StyleMap>"
				+"<Style id=\"sn_ylw-pushpin\">"
				+"	<IconStyle>"
				+"		<color>ff00ffff</color>"
				+"		<scale>1.1</scale>"
				+"		<Icon>"
				+"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+"		</Icon>"
				+"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"	</IconStyle>"
				+"	<LabelStyle>"
				+"		<color>ff00ffff</color>"
				+"	</LabelStyle>"
				+"</Style>"
				+"<Style id=\"sh_ylw-pushpin\">"
				+"	<IconStyle>"
				+"		<color>ff00ffff</color>"
				+"		<scale>1.3</scale>"
				+"		<Icon>"
				+"			<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"
				+"		</Icon>"
				+"		<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
				+"	</IconStyle>"
				+"	<LabelStyle>"
				+"		<color>ff00ffff</color>"
				+"	</LabelStyle>"
				+"</Style>"
				+"<Placemark>"
				+"	<name>b</name>"
				+"	<visibility>0</visibility>"
				+"	<LookAt>"
				+"		<longitude>-95.26548319412231</longitude>"
				+"		<latitude>38.95938957105113</latitude>"
				+"		<altitude>0</altitude>"
				+"		<range>11001000</range>"
				+"		<tilt>0</tilt>"
				+"		<heading>2.981770013872047e-014</heading>"
				+"	</LookAt>"
				+"	<styleUrl>#msn_ylw-pushpin</styleUrl>"
				+"	<Point>"
				+"		<coordinates>-95.26548319412231,38.95938957105113,0</coordinates>"
				+"	</Point>"
				+"</Placemark>"
				+"</Document>"
				+"</kml>";

		Document document = DocumentHelper.parseText(xml);
		Element element = document.getRootElement().element("Document").element("StyleMap");
		Element placemark = document.getRootElement().element("Document").element("Placemark");
		Assert.assertNotNull(placemark);
		Assert.assertEquals("#msn_ylw-pushpin", placemark.element("styleUrl").getText());
		
		String oldXML = document.asXML();
		
		HierarchyXMLViewElement hierarchyView = new HierarchyXMLViewElement();
		hierarchyView.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(new FileManager())));
		KMLViewElement styleMap = new KMLViewElement(KmlNames.KML_QNAME_STYLE_MAP, hierarchyView, true);
		
		Assert.assertSame(element, styleMap.refresh(document, element));
		Assert.assertFalse(oldXML.equals(document.asXML()));
		Assert.assertEquals("msn_ylw-pushpin", element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID));
		Assert.assertEquals("#msn_ylw-pushpin_1", placemark.element("styleUrl").getText());
	}

	@Test
	public void shouldRefreshNoUpdateReferences() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData>"
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
		Element element = document.getRootElement()
			.element("Document")
			.element("Folder")
			.element("Placemark");
		String oldXML = document.asXML();
		
		HierarchyXMLViewElement hierarchyView = new HierarchyXMLViewElement();
		hierarchyView.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(new FileManager())));
		
		KMLViewElement placemark = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, hierarchyView, false);
		Assert.assertSame(element, placemark.refresh(document, element));
		Assert.assertEquals(oldXML, document.asXML());
	}

	
	@Test
	public void shouldRefreshCreateHierarchy() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData>"
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
		Element element = document.getRootElement()
			.element("Document")
			.element("Folder")
			.element("Placemark");
		String oldXML = document.asXML();
		
		HierarchyXMLViewElement hierarchyView = new HierarchyXMLViewElement();
		hierarchyView.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(new FileManager())));
		
		KMLViewElement placemark = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, hierarchyView, false);
		Assert.assertSame(element, placemark.refresh(document, element));
		Assert.assertFalse(oldXML.equals(document.asXML()));
		
		Assert.assertNotNull(HierarchyXMLViewElement.getHierarchyElementByChild(document, "0213a27a-8262-493d-bd1e-5268c6fe401e"));
	}

	@Test
	public void shouldRefreshNoCreateHierarchy() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<kml xmlns=\"http://earth.google.com/kml/2.2\">"
			+"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"
				+"<name>a1.kml</name>"
				+"<visibility>0</visibility>"
				+"<open>1</open>"
				+"<ExtendedData>"
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
		Element element = document.getRootElement()
			.element("Document")
			.element("Folder")
			.element("Placemark");
		String oldXML = document.asXML();
		
		HierarchyXMLViewElement hierarchyView = new HierarchyXMLViewElement();
		hierarchyView.setDOM(new KMLDOM(document, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(new FileManager())));
		
		KMLViewElement placemark = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, hierarchyView, false);
		Assert.assertSame(element, placemark.refresh(document, element));
		Assert.assertEquals(oldXML, document.asXML());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementIsNull(){
		KMLViewElement viewElement = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, new HierarchyXMLViewElement(), false);
		viewElement.clean(DocumentHelper.createDocument(), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementParentIsNull(){
		Element element = DocumentHelper.createElement(KmlNames.KML_QNAME_PLACEMARK);
		
		KMLViewElement viewElement = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, new HierarchyXMLViewElement(), false);
		viewElement.clean(null, element);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementInvalidElementType() throws DocumentException{
		String xml = "<foo><bar></bar></foo>";
		Document document = DocumentHelper.parseText(xml);
		
		Element element = document.getRootElement().element("bar");
		Assert.assertNotNull(element);
				
		KMLViewElement viewElement = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, new HierarchyXMLViewElement(), false);
		viewElement.clean(document, element);
	}
	
	@Test
	public void shouldClean() throws DocumentException{
		String xml = "<foo xmlns= \"http://earth.google.com/kml/2.2\"><Placemark xmlns:mesh4x=\"http://mesh4x.org/kml\" xml:id = \"1\" mesh4x:originalId=\"1\" ></Placemark></foo>";
		Document document = DocumentHelper.parseText(xml);

		Element element = document.getRootElement().element(KmlNames.KML_QNAME_PLACEMARK);
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
		Assert.assertNotNull(element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID));
		Assert.assertNotNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
		
		KMLViewElement viewElement = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, new HierarchyXMLViewElement(), false);
		viewElement.clean(document, element);
		
		element = document.getRootElement().element(KmlNames.KML_QNAME_PLACEMARK);
		Assert.assertNotNull(element);
		Assert.assertNull(element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
		Assert.assertNull(element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID));
		Assert.assertNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
	}
}
