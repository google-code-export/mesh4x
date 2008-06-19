package com.mesh4j.sync.adapters.kml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.parsers.IXMLViewElement;
import com.mesh4j.sync.validations.Guard;

public class KMLDocumentExtendedDataViewElement implements IXMLViewElement {

	// MODEL VARIABLES
	private HashMap<String, String> namespaces = new HashMap<String, String>();
	
	// BUSINESS METHODS
	public KMLDocumentExtendedDataViewElement(){
		super();
		this.namespaces.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
	}
		
	@Override
	public Map<String, String> getNameSpaces() {
		return namespaces;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Element> getAllElements(Document document) {
		Element rootElement = getContentRepository(document);
		List<Element> elements = rootElement.elements();
		ArrayList<Element> result = new ArrayList<Element>();
		for (Element element : elements) {
			if(!MeshNames.MESH_PREFIX.equals(element.getNamespacePrefix())){
				result.add(element);
				if(element.getNamespacePrefix() != null && element.getNamespacePrefix().trim().length() > 0){
					this.namespaces.put(element.getNamespacePrefix(), element.getNamespaceURI());
				}
			}
		}
		return result;
	}

	@Override
	public Element add(Document document, Element newElement) {
		Guard.argumentNotNull(newElement, "newElement");
		if(newElement != null && !this.manage(newElement)){
			Guard.throwsArgumentException("element type", newElement);
		}
		
		Element rootElement = getContentRepository(document);
		Namespace ns = newElement.getNamespace();
		if(rootElement.getNamespaceForPrefix(ns.getPrefix()) == null){
			rootElement.add(ns);
		}
		rootElement.add(newElement);
		return newElement;
	}

	@Override
	public void delete(Document document, Element element) {
		Guard.argumentNotNull(document, "document");
		Guard.argumentNotNull(element, "element");
		if(element != null && !this.manage(element)){
			Guard.throwsArgumentException("element type", element);
		}
		Guard.argumentNotNull(element.getParent(), "parent");

		element.getParent().remove(element);
	}

	@Override
	public boolean isValid(Document document, Element element) {
		return this.manage(element)
			&& element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID) != null
			&& element.getParent() != null
			&& KmlNames.KML_ELEMENT_EXTENDED_DATA.equals(element.getParent().getName())
			&& element.getParent().getNamespaceForPrefix(element.getNamespacePrefix()) != null;
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
	public Element update(Document document, Element element, Element newElement) {
		Guard.argumentNotNull(document, "document");
		Guard.argumentNotNull(element, "element");
		Guard.argumentNotNull(newElement, "newElement");
		if(element != null && !this.manage(element)){
			Guard.throwsArgumentException("element type", element);
		}
		if(newElement != null && !this.manage(newElement)){
			Guard.throwsArgumentException("element type", newElement);
		}
		Guard.argumentNotNull(element.getParent(), "parent");
		
		Element parent = element.getParent();
		parent.remove(element);
		parent.add(newElement);
		return newElement;
	}

	@Override
	public boolean manage(Element element) {
		if(element == null){
			return false;
		}
		if(!MeshNames.MESH_PREFIX.equals(element.getNamespacePrefix()) 
				&& !KmlNames.KML_PREFIX.equals(element.getNamespacePrefix())
				&& !Namespace.NO_NAMESPACE.equals(element.getNamespace())
				&& (element.getParent() == null ||
					(element.getParent() != null && KmlNames.KML_ELEMENT_EXTENDED_DATA.equals(element.getParent().getName())) ||
					(element.getParent() != null && ISyndicationFormat.ELEMENT_PAYLOAD.equals(element.getParent().getName())))){
			return true;
		}
		return KmlNames.KML_ELEMENT_DATA.equals(element.getName()) ||
			KmlNames.KML_ELEMENT_SCHEMA_DATA.equals(element.getName());
	}

	@Override
	public void clean(Document document, Element element) {
		Guard.argumentNotNull(element, "element");
		
		Attribute syncIDAttr = element.attribute(MeshNames.MESH_QNAME_SYNC_ID);
		if(syncIDAttr != null){
			element.remove(syncIDAttr);
		}
	}
	
	private Element getContentRepository(Document document) {
		Guard.argumentNotNull(document, "document");
		Guard.argumentNotNull(document.getRootElement(), "document.root");
		
		Element docElement = document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Guard.argumentNotNull(document, "document.root.documentelement");
		
		Element extendedData = docElement.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		Guard.argumentNotNull(extendedData, "document.root.document.extendeddata");
		
		return extendedData;
	}
}
