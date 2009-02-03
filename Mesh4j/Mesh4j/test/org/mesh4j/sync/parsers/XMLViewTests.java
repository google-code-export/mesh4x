package org.mesh4j.sync.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.dom.MeshNames;
import org.mesh4j.sync.adapters.kml.KmlNames;


public class XMLViewTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateXMLViewFailsIfNoElementViewAreGiving(){
		new XMLView();
	}
	
	@Test
	public void shouldGetViewElements(){
		IXMLViewElement view1 = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		IXMLViewElement view2 = new XMLViewElement(KmlNames.KML_QNAME_FOLDER, false);
		
		XMLView view = new XMLView(view1, view2);
		List<IXMLViewElement> viewElements = view.getXMLViewElements();
		
		Assert.assertNotNull(viewElements);
		Assert.assertEquals(2, viewElements.size());
		Assert.assertSame(view1, viewElements.get(0));
		Assert.assertSame(view2, viewElements.get(1));
	}
	
	@Test
	public void shouldGetNamespaces(){
		IXMLViewElement view1 = new XMLViewElement(KmlNames.KML_QNAME_PLACEMARK, false);
		IXMLViewElement view2 = new XMLViewElement(MeshNames.MESH_QNAME_HIERARCHY, false);
		
		XMLView view = new XMLView(view1, view2);
		Map<String, String> ns = view.getNameSpaces();
		Assert.assertNotNull(ns);
		Assert.assertEquals(2, ns.size());
		Assert.assertEquals(KmlNames.KML_QNAME_PLACEMARK.getNamespaceURI(), ns.get(KmlNames.KML_QNAME_PLACEMARK.getNamespacePrefix()));
		Assert.assertEquals(MeshNames.MESH_QNAME_HIERARCHY.getNamespaceURI(), ns.get(MeshNames.MESH_QNAME_HIERARCHY.getNamespacePrefix()));
	}
	
	@Test
	public void shouldGetAllElements(){
		MockXMLViewElement view1 = new MockXMLViewElement("Folder");
		MockXMLViewElement view2 = new MockXMLViewElement("Placemark");
		
		XMLView view = new XMLView(view1, view2);
		List<Element> elements = view.getAllElements(DocumentHelper.createDocument());
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(2, elements.size());
		Assert.assertEquals(view1.getElement(), elements.get(0));
		Assert.assertEquals(view2.getElement(), elements.get(1));
	}

	@Test
	public void shouldNormalize(){
		MockXMLViewElement view1 = new MockXMLViewElement("Folder");
		XMLView view = new XMLView(view1);
		
		Assert.assertSame(view1.getElement(), view.normalize(view1.getElement()));		
		Assert.assertNull(view.normalize(null));		
		Assert.assertNull(view.normalize(DocumentHelper.createElement("Placemark")));
	}
	
	@Test
	public void shouldRefreshAndNormalize(){
		MockXMLViewElement view1 = new MockXMLViewElement("Folder");
		XMLView view = new XMLView(view1);
		
		Assert.assertSame(view1.getElement(), view.refreshAndNormalize(null, view1.getElement()));		
		Assert.assertNull(view.refreshAndNormalize(null, null));		
		Assert.assertNull(view.refreshAndNormalize(null, DocumentHelper.createElement("Placemark")));
	}
	
	@Test
	public void shouldAdd(){
		MockXMLViewElement view1 = new MockXMLViewElement("Folder");
		XMLView view = new XMLView(view1);
		
		Document document = DocumentHelper.createDocument(DocumentHelper.createElement("kml"));
		Assert.assertSame(view1.getElement(), view.add(document, view1.getElement()));
		Assert.assertSame(view1.getElement(), document.getRootElement().element(view1.getElement().getName()));
		Assert.assertNull(view.add(null, null));		
		Assert.assertNull(view.add(null, DocumentHelper.createElement("Placemark")));
	}
	
	@Test
	public void shouldUpdate(){
		MockXMLViewElement view1 = new MockXMLViewElement("Folder");
		XMLView view = new XMLView(view1);
		
		Element newElement = DocumentHelper.createElement("Folder");
		Assert.assertSame(newElement, view.update(null, view1.getElement(), newElement));
		Assert.assertNull(view.update(null, null, null));		
		Assert.assertNull(view.update(null, DocumentHelper.createElement("Placemark"), null));
	}
	
	@Test
	public void shouldDelete(){
		MockXMLViewElement view1 = new MockXMLViewElement("Folder");
		XMLView view = new XMLView(view1);
		
		Document document = DocumentHelper.createDocument(DocumentHelper.createElement("kml"));
		document.getRootElement().add(view1.getElement());
		Assert.assertNotNull(document.getRootElement().element(view1.getElement().getName()));
		
		view.delete(document, view1.getElement());
		Assert.assertNull(document.getRootElement().element(view1.getElement().getName()));

		document.getRootElement().add(view1.getElement());
		Assert.assertNotNull(document.getRootElement().element(view1.getElement().getName()));
		view.delete(document, null);		
		Assert.assertNotNull(document.getRootElement().element(view1.getElement().getName()));
		
		Assert.assertNotNull(document.getRootElement().element(view1.getElement().getName()));
		view.delete(document, DocumentHelper.createElement("Placemark"));		
		Assert.assertNotNull(document.getRootElement().element(view1.getElement().getName()));

	}
	
	@Test
	public void shouldIsValid(){
		MockXMLViewElement view1 = new MockXMLViewElement("Folder");
		XMLView view = new XMLView(view1);
		
		Assert.assertTrue(view.isValid(null, view1.getElement()));
		Assert.assertFalse(view.isValid(null, null));		
		Assert.assertFalse(view.isValid(null, DocumentHelper.createElement("Placemark")));
	}
	
	@Test
	public void shouldClean(){
		Document document = DocumentHelper.createDocument();
		Element folderElement = DocumentHelper.createElement("Folder");
		
		MockXMLViewElement view1 = new MockXMLViewElement("Folder");
		XMLView view = new XMLView(view1);
		
		Assert.assertFalse(view1.cleanWasCalled());
		view.clean(document, folderElement);
		Assert.assertTrue(view1.cleanWasCalled());
	}
	
	@Test
	public void shouldNoClean(){
		Document document = DocumentHelper.createDocument();
		Element folderElement = DocumentHelper.createElement("Folder");
		
		MockXMLViewElement view1 = new MockXMLViewElement("FOO");
		XMLView view = new XMLView(view1);
		
		Assert.assertFalse(view1.cleanWasCalled());
		view.clean(document, folderElement);
		Assert.assertFalse(view1.cleanWasCalled());
	}
	
	
	private class MockXMLViewElement implements IXMLViewElement{
		
		private Element element;
		private boolean cleanWasCalled = false;

		private MockXMLViewElement(String elementName){
			super();
			this.element = DocumentHelper.createElement(elementName);
		}
		
		public boolean cleanWasCalled() {
			return cleanWasCalled;
		}

		public Element getElement() {
			return this.element;
		}

		@Override
		public Element add(Document document, Element element) {
			document.getRootElement().add(element);
			return element;
		}

		@Override
		public void delete(Document document, Element element) {
			document.getRootElement().remove(element);
		}

		@Override
		public List<Element> getAllElements(Document document) {
			ArrayList<Element> result = new ArrayList<Element>();
			result.add(this.element);
			return result;
		}

		@Override
		public boolean isValid(Document document, Element element) {
			return true;
		}

		@Override
		public Element normalize(Element element) {
			return element;
		}

		@Override
		public Element refresh(Document document, Element element) {
			return element;
		}

		@Override
		public Element update(Document document, Element element,
				Element newElement) {
			return newElement;
		}

		@Override
		public void clean(Document document, Element element) {
			cleanWasCalled = true;
		}

		@Override
		public Map<String, String> getNameSpaces() {
			return null;
		}

		@Override
		public boolean manage(Element element) {
			return this.element.getName().equals(element.getName());
		}
	}
}
