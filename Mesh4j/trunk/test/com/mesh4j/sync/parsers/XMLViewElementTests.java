package com.mesh4j.sync.parsers;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.dom.parsers.HierarchyXMLViewElement;
import com.mesh4j.sync.adapters.kml.KMLViewElement;
import com.mesh4j.sync.adapters.kml.KmlNames;

public class XMLViewElementTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotSupportNullType(){
		new XMLViewElement(null, true);
	}
	
	@Test
	public void shouldReturnName(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		Assert.assertEquals("Folder", view.getName());
	}
	
	@Test
	public void shouldReturnQName(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		Assert.assertEquals(KmlNames.KML_QNAME_FOLDER, view.getQName());
	}

	@Test
	public void shouldNormalizeReturnNullBecauseElementIsNull(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		Assert.assertNull(view.normalize(null));
	}
	
	@Test
	public void shouldNormalizeReturnNullBecauseElementHasInvalidType(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		Element element = DocumentHelper.createElement("FOO");
		Assert.assertNull(view.normalize(element));
	}
	
	@Test
	public void shouldNormalizeReturnsSameElementBecauseNotConfigurationWasDefined(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		Element element = DocumentHelper.createElement("Folder");
		Assert.assertSame(element, view.normalize(element));
	}

	@Test
	public void shoudNormalizeReturnEmptyElement() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		view.addAttribute(DocumentHelper.createQName("fooAttribute", DocumentHelper.createNamespace("foo", "http:\\foo.org")));
		view.addAttribute("myAttribute");
		view.addElement(DocumentHelper.createQName("barAttribute", DocumentHelper.createNamespace("bar", "http:\\bar.org")));
		view.addElement("Bar");
		view.addElement("FooBar", "http:\\foobar.org");
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"<name>dummy</name>"+
	   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"2096103467\">"+
      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+
      	"</ExtendedData>"+
		"<Folder xml:id=\"1\">"+
		"<name>B</name>"+
		"</Folder>"+
		"</Document>"+
		"</kml>";
		
		Element element = DocumentHelper.parseText(xml).getRootElement().element("Document").element("Folder");
		Element normalizedElement = view.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertFalse(normalizedElement.attributeIterator().hasNext());
		Assert.assertFalse(normalizedElement.elementIterator().hasNext());
	}
	
	@Test
	public void shoudNormalize() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		QName attr1 = DocumentHelper.createQName("fooAttribute", DocumentHelper.createNamespace("foo", "http:\\foo.org"));
		view.addAttribute(attr1);
		
		String attr2 = "myAttribute";
		view.addAttribute(attr2);
		
		QName ele1 = DocumentHelper.createQName("BAR", DocumentHelper.createNamespace("bar", "http:\\bar.org"));
		view.addElement(ele1);
		
		String ele2 = "BarFoo";
		view.addElement(ele2);
		
		//view.addElement("FooBar", "http:\\foobar.org");
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<Document>"+
		"<name>dummy</name>"+
	   	"<ExtendedData>"+
		"<MESH xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"2096103467\">"+
      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</MESH>"+
      	"</ExtendedData>"+
		"<Folder xmlns:foo=\"http:\\foo.org\" xmlns:bar=\"http:\\bar.org\" xmlns:fooBar=\"http:\\foobar.org\" myAttribute=\"2\" foo:fooAttribute=\"1\">"+
		"	<name>B</name>"+
      	"	<Placemark foo:id=\"1\">"+
		"		<name>B</name>"+
		"	</Placemark>"+
		"	<bar:BAR>"+
		"		<name>B</name>"+
		"	</bar:BAR>"+
		"	<BarFoo>"+
		"		<name>C</name>"+
		"	</BarFoo>"+
		"</Folder>"+
		"</Document>";
		
		Element element = DocumentHelper.parseText(xml).getRootElement().element("Folder");
		Element normalizedElement = view.normalize(element);
		
		Assert.assertNotNull(normalizedElement);
		Assert.assertFalse(element == normalizedElement);
		
		Assert.assertNotNull(normalizedElement.attributeValue(attr1));
		Assert.assertEquals("1", normalizedElement.attributeValue(attr1));
		
		Assert.assertNotNull(normalizedElement.attributeValue(attr2));
		Assert.assertEquals("2", normalizedElement.attributeValue(attr2));
		
		Assert.assertNotNull(normalizedElement.element(ele1));
		Assert.assertNotNull(normalizedElement.element(ele1).element("name"));
		Assert.assertEquals("B", normalizedElement.element(ele1).element("name").getText());
		
		Assert.assertNotNull(normalizedElement.element(ele2));
		Assert.assertNotNull(normalizedElement.element(ele2).element("name"));
		Assert.assertEquals("C", normalizedElement.element(ele2).element("name").getText());

	}
	
	@Test
	public void shouldUpdateNotEffectBecauseElementIsNull(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		view.addElement("Bar");
		
		Element element = DocumentHelper.createElement("Folder");
		view.update(null, null, element);
	}
	
	@Test
	public void shouldUpdateNotEffectBecauseElementSourceIsNull(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		view.addElement("Bar");
		
		Element element = DocumentHelper.createElement("Folder");
		view.update(null, element, null);
		
		Assert.assertFalse(element.attributeIterator().hasNext());
		Assert.assertFalse(element.elementIterator().hasNext());
	}
	
	@Test
	public void shouldUpdateNotEffectBecauseElementHasInvalidType(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		Element element = DocumentHelper.createElement("Foo");
		Element elementSource = DocumentHelper.createElement("Folder");
		view.update(null, element, elementSource);
		
		Assert.assertFalse(element.attributeIterator().hasNext());
		Assert.assertFalse(element.elementIterator().hasNext());

	}
	
	@Test
	public void shouldUpdateNotEffectBecauseElementSourceHasInvalidType(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		Element element = DocumentHelper.createElement("Folder");
		Element elementSource = DocumentHelper.createElement("Foo");
		view.update(null, element, elementSource);
		
		Assert.assertFalse(element.attributeIterator().hasNext());
		Assert.assertFalse(element.elementIterator().hasNext());

	}
	
	@Test
	public void shouldUpdateAllBecauseNotConfigurationWasDefined() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		String xml = "<kml><Folder><name>1</name></Folder></kml>";
		Element root =  DocumentHelper.parseText(xml).getRootElement();
		Element element = root.element("Folder");
		element.normalize();

		String xmlSource = "<Folder xmlns:foo=\"http://foo.org\" myAttribute=\"2\" foo:fooAttribute=\"1\"><name>dummy</name><ExtendedData><MESH xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"2096103467\"><sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\"><sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/><sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/><sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/></sx:sync></MESH></ExtendedData><Placemark foo:id=\"1\"><name>B</name></Placemark><bar:BAR xmlns:bar=\"http://bar.org\"><name>B</name></bar:BAR><BarFoo><name>C</name></BarFoo></Folder>";
		Element elementSource = DocumentHelper.parseText(xmlSource).getRootElement();
		elementSource.normalize();
		
		Assert.assertFalse(elementSource.asXML().equals(element.asXML()));

		Element result = view.update(null, element, elementSource);		

		result.normalize();
		elementSource.normalize();
		Assert.assertTrue(elementSource == result);
		Assert.assertFalse(element == result);
		
		Assert.assertNotNull(root.element("Folder"));
		Assert.assertEquals(elementSource.asXML(), root.element("Folder").asXML());
	}

	@Test
	public void shoudUpdateNoEffectBecauseNoElementsMatches() throws DocumentException{
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		view.addAttribute(DocumentHelper.createQName("fooAttribute", DocumentHelper.createNamespace("foo", "http:\\foo.org")));
		view.addAttribute("myAttribute");
		view.addElement(DocumentHelper.createQName("barAttribute", DocumentHelper.createNamespace("bar", "http:\\bar.org")));
		view.addElement("Bar");
		view.addElement("FooBar", "http:\\foobar.org");
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Folder xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		"<name>dummy</name>"+
	   	"<ExtendedData>"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"2096103467\">"+
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
		"</Folder>"+
		"</kml>";
		
		Element element = DocumentHelper.createElement("Folder");
		Element elementSource = DocumentHelper.parseText(xml).getRootElement().element("Folder");
		view.update(null, element, elementSource);
		
		Assert.assertFalse(element.attributeIterator().hasNext());
		Assert.assertFalse(element.elementIterator().hasNext());
	}
	
	@Test
	public void shoudUpdate() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		QName attr1 = DocumentHelper.createQName("fooAttribute", DocumentHelper.createNamespace("foo", "http:\\foo.org"));
		view.addAttribute(attr1);
		
		String attr2 = "myAttribute";
		view.addAttribute(attr2);
		
		String attr3 = "myAttribute3";
		view.addAttribute(attr3);
		
		QName ele1 = DocumentHelper.createQName("BAR", DocumentHelper.createNamespace("bar", "http:\\bar.org"));
		view.addElement(ele1);
		
		String ele2 = "BarFoo";
		view.addElement(ele2);
		
		String ele3 = "BarFoo3";
		view.addElement(ele3);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<Folder myAttribute3=\"old\">"+
		"<BarFoo3>"+
		"<name>old</name>"+
		"<desc>old</desc>"+
		"</BarFoo3>"+
		"</Folder>";
		
		Element element = DocumentHelper.parseText(xml).getRootElement();
		
		String xmlSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<Folder xmlns:foo=\"http:\\foo.org\" xmlns:bar=\"http:\\bar.org\" xmlns:fooBar=\"http:\\foobar.org\" myAttribute=\"2\" myAttribute3=\"3\" foo:fooAttribute=\"1\">"+
		"<name>dummy</name>"+
	   	"<ExtendedData>"+
		"<MESH xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"2096103467\">"+
      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</MESH>"+
      	"</ExtendedData>"+
		"<Placemark foo:id=\"1\">"+
		"<name>B</name>"+
		"</Placemark>"+
		"<bar:BAR>"+
		"<name>B</name>"+
		"</bar:BAR>"+
		"<BarFoo>"+
		"<name>C</name>"+
		"</BarFoo>"+
		"<BarFoo3>"+
		"<name>3</name>"+
		"</BarFoo3>"+
		"</Folder>";
		
		Element elementSource = DocumentHelper.parseText(xmlSource).getRootElement();
		view.update(null, element, elementSource);
		
		Assert.assertNotNull(element.attributeValue(attr1));
		Assert.assertEquals("1", element.attributeValue(attr1));
		
		Assert.assertNotNull(element.attributeValue(attr2));
		Assert.assertEquals("2", element.attributeValue(attr2));
		
		Assert.assertNotNull(element.attributeValue(attr3));
		Assert.assertEquals("3", element.attributeValue(attr3));
		
		Assert.assertNotNull(element.element(ele1));
		Assert.assertNotNull(element.element(ele1).element("name"));
		Assert.assertEquals("B", element.element(ele1).element("name").getText());
		
		Assert.assertNotNull(element.element(ele2));
		Assert.assertNotNull(element.element(ele2).element("name"));
		Assert.assertEquals("C", element.element(ele2).element("name").getText());

		Assert.assertNotNull(element.element(ele3));
		Assert.assertNotNull(element.element(ele3).element("name"));
		Assert.assertEquals("3", element.element(ele3).element("name").getText());
		
	}
	
	@Test 
	public void shouldRefresh(){
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		Element element = DocumentHelper.createElement("Folder");
		Element result = view.refresh(null, element);		
		Assert.assertNotNull(result);
		Assert.assertTrue(element == result);
		
		result = view.refresh(null, null);		
		Assert.assertNull(result);
		
	}
	
	@Test
	public void shoulGetAllReturnsEmptyList() throws DocumentException{
		String xml = "<Document><name>1</name></Document>";
		
		Document doc = DocumentHelper.parseText(xml);
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		List<Element> elements = view.getAllElements(doc);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(0, elements.size());
	}
	
	@Test
	public void shouldGetAllSearchInDocumentRoot() throws DocumentException{
		String xml = "<root><foo>1</foo><foo>2</foo><bar><foo>3</foo></bar></root>";
		
		Document doc = DocumentHelper.parseText(xml);
		
		QName qname = DocumentHelper.createQName("foo");
		XMLViewElement view = new XMLViewElement(qname, true);
		List<Element> elements = view.getAllElements(doc);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(2, elements.size());
	}

	@Test
	public void shouldGetAllSearchInAllDocument() throws DocumentException{
		String xml = "<root><foo>1</foo><foo>2</foo><bar><foo>3</foo></bar></root>";
		
		Document doc = DocumentHelper.parseText(xml);
		
		QName qname = DocumentHelper.createQName("foo");
		XMLViewElement view = new XMLViewElement(qname, false);
		List<Element> elements = view.getAllElements(doc);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(3, elements.size());
	}
	
	@Test
	public void shoulGetAll() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.2\"><Document><name>1</name><Folder><Folder><Placemark><name>0</name></Placemark></Folder></Folder><Folder><Placemark><name>1</name></Placemark></Folder><Placemark><name>2</name></Placemark></Document></kml>";
		
		Document doc = DocumentHelper.parseText(xml);
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		List<Element> elements = view.getAllElements(doc);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(3, elements.size());
	}
	
	@Test
	public void shoulGetAllReturnsEmptyListBecauseInvalidNS() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Document><name>1</name><Folder><Folder><Placemark><name>0</name></Placemark></Folder></Folder><Folder><Placemark><name>1</name></Placemark></Folder><Placemark><name>2</name></Placemark></Document>";
		
		Document doc = DocumentHelper.parseText(xml);
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		List<Element> elements = view.getAllElements(doc);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(0, elements.size());
	}
	
	@Test
	public void shouldDeleteNoEffectBecauseElementIsNull() throws DocumentException{

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document><name>1</name></Document>";
		Document doc = DocumentHelper.parseText(xml);
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		view.delete(doc, null);
		
		Assert.assertNotNull(doc.getRootElement());
		Assert.assertEquals(xml, doc.asXML());
		
	}
	
	@Test
	public void shouldDeleteRootElement() throws DocumentException{

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Document><name>1</name></Document>";
		Document doc = DocumentHelper.parseText(xml);
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		view.delete(doc, doc.getRootElement());
		
		Assert.assertNull(doc.getRootElement());
		
	}
	
	@Test
	public void shouldDeleteNoRootElement() throws DocumentException{

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Document><name>1</name></Document>";
		Document doc = DocumentHelper.parseText(xml);
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		view.delete(doc, doc.getRootElement().element("name"));
		
		Assert.assertNotNull(doc.getRootElement());
		Assert.assertNull(doc.getRootElement().element("name"));
		
	}
	
	@Test
	public void shouldIsValid(){
		
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("KML"));
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		
		Assert.assertFalse(view.isValid(null, doc.getRootElement()));
		Assert.assertFalse(view.isValid(doc, null));
		Assert.assertTrue(view.isValid(doc, doc.getRootElement()));
	}
	
	@Test
	public void shouldDelete(){
		
		Document doc = DocumentHelper.createDocument(DocumentHelper.createElement("KML"));
		
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		
		view.delete(doc, null);
		Assert.assertNotNull(doc.getRootElement());

		view.delete(null, doc.getRootElement());
		Assert.assertNotNull(doc.getRootElement());
		
		view.delete(doc, doc.getRootElement());
		Assert.assertNull(doc.getRootElement());
	}
	
	@Test
	public void shouldAddFailsBecauseDocumentIsNull() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		
		Assert.assertNull(view.add(null, DocumentHelper.createElement("Placemark")));
	}
	
	@Test
	public void shouldAddFailsBecauseElementIsNull() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document><name>1</name></Document>";
		Document doc = DocumentHelper.parseText(xml);
		
		Assert.assertNull(view.add(doc, null));
		Assert.assertEquals(xml, doc.asXML());
	}
	
	@Test
	public void shouldAddFailsBecauseElementIsNotNormalized() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document><name>1</name></Document>";
		Document doc = DocumentHelper.parseText(xml);
		
		Element elementToAdd = DocumentHelper.createElement("Folder");
		
		Assert.assertNull(view.add(doc, elementToAdd));
		Assert.assertEquals(xml, doc.asXML());
	}
	
	@Test
	public void shouldAdd() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document><name>1</name></Document>";
		Document doc = DocumentHelper.parseText(xml);

		String xmlToAdd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Placemark><name>2</name></Placemark>";
		Element elementToAdd = DocumentHelper.parseText(xmlToAdd).getRootElement();
		
		Element elementAdded = view.add(doc, elementToAdd);
		Assert.assertNotNull(elementAdded);
		Assert.assertTrue(elementToAdd == elementAdded);
		
		Assert.assertTrue(elementAdded == doc.getRootElement().element("Placemark"));
	}
	
	@Test
	public void shouldAddNormalizedElement() throws DocumentException{
		XMLViewElement view = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		view.addElement("name");
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document><name>1</name></Document>";
		Document doc = DocumentHelper.parseText(xml);

		String xmlToAdd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Placemark><name>2</name><description>des</description></Placemark>";
		Element elementToAdd = DocumentHelper.parseText(xmlToAdd).getRootElement();
		
		Element elementAdded = view.add(doc, elementToAdd);
		Assert.assertNotNull(elementAdded);
		Assert.assertFalse(elementToAdd == elementAdded);
		
		Assert.assertTrue(elementAdded == doc.getRootElement().element("Placemark"));
		Assert.assertEquals("2", doc.getRootElement().element("Placemark").element("name").getText());
		Assert.assertNull(doc.getRootElement().element("Placemark").element("description"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementIsNull(){
		KMLViewElement viewElement = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, new HierarchyXMLViewElement(), false);
		viewElement.clean(DocumentHelper.createDocument(), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementParentIsNull(){
		Element element = DocumentHelper.createElement(KmlNames.KML_QNAME_PLACEMARK);
		
		XMLViewElement viewElement = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		viewElement.clean(null, element);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCleanFailsBecauseElementInvalidElementType() throws DocumentException{
		String xml = "<foo><bar></bar></foo>";
		Document document = DocumentHelper.parseText(xml);
		
		Element element = document.getRootElement().element("bar");
		Assert.assertNotNull(element);
				
		XMLViewElement viewElement = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
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
		
		XMLViewElement viewElement = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		viewElement.clean(document, element);
		
		element = document.getRootElement().element(KmlNames.KML_QNAME_PLACEMARK);
		Assert.assertNotNull(element);
		Assert.assertNotNull(element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID));
		Assert.assertNotNull(element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID));
		Assert.assertNotNull(element.getNamespaceForPrefix(MeshNames.MESH_PREFIX));
	}
}
