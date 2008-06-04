package com.mesh4j.sync.adapters.kml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;

import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.dom.parsers.HierarchyXMLViewElement;
import com.mesh4j.sync.parsers.XMLViewElement;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;

public class KMLViewElement extends XMLViewElement {

	// CONSTANTS
	public static Map<String, String> SEARCH_NAMESPACES = new HashMap<String, String>();
	static{
		SEARCH_NAMESPACES.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		SEARCH_NAMESPACES.put(MeshNames.XML_PREFIX, MeshNames.XML_URI);
		SEARCH_NAMESPACES.put(MeshNames.MESH_PREFIX, MeshNames.MESH_URI);
	}	
	
	// MODEL VARIABLES
	private HierarchyXMLViewElement hierarchyViewElement;
	
	// BUSINESS METHODS
	public KMLViewElement(QName qname, HierarchyXMLViewElement hierarchyViewElement, boolean searchOnlyRoot) {
		super(qname, searchOnlyRoot);
		
		Guard.argumentNotNull(hierarchyViewElement, "hierarchyViewElement");
		this.hierarchyViewElement = hierarchyViewElement;
	}
	
	@Override
	public Element refresh(Document document, Element element) {
		Element result = super.refresh(document, element);
		
		String originalID = result.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID);
		Attribute attr = result.attribute(KmlNames.KML_ATTRIBUTE_ID);	
		if(attr != null && attr.getNamespacePrefix().length() == 0){
			String kmlID = attr.getValue();
			String syncID = result.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
			if(originalID == null && kmlID != null && syncID != null){
				Element rootElement = this.getRootElement(document);
				refreshReferences(result, kmlID, syncID, rootElement, SEARCH_NAMESPACES);
			}else if (syncID != null && originalID != null && kmlID != null && !kmlID.equals(originalID+"_"+syncID)){
				// Google Earth and probably others applications copy styles for create new styles, in this case is necessary to change syncID and references.
				Element rootElement = this.getRootElement(document);				
				syncID = this.hierarchyViewElement.getDOM().newID();
				result.addAttribute(MeshNames.MESH_QNAME_SYNC_ID, syncID);
				refreshReferences(result, kmlID, syncID, rootElement, SEARCH_NAMESPACES);	
			}
		}
		
		this.hierarchyViewElement.createHierarchyIfAbsent(document, result);
		return result;
	}
	
	private void refreshReferences(Element element, String originalId, String syncID, Element rootElement, Map<String, String> searchNamespaces) {

		String kmlIDRef = "#"+originalId;
		String newKmlID = originalId+"_"+syncID;			
		String newKmlIDRef = "#"+newKmlID;
		
		element.addAttribute(MeshNames.MESH_QNAME_ORIGINAL_ID, originalId);
		element.addAttribute(KmlNames.KML_ATTRIBUTE_ID, originalId+"_"+syncID);

		List<Element> references = XMLHelper.selectElements("//kml:*[text()='"+ kmlIDRef +"']", rootElement, searchNamespaces);
		for (Element refElement : references) {
			refElement.setText(newKmlIDRef);
		}
	}
	
	@Override
	public boolean isValid(Document document, Element element) {
		if(super.isValid(document, element)){
			String originalID = element.attributeValue(MeshNames.MESH_QNAME_ORIGINAL_ID);
			String syncID = element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
			if(originalID != null){
				String kmlID = element.attributeValue(KmlNames.KML_ATTRIBUTE_ID);
				if(kmlID != null && syncID != null && kmlID.equals(originalID+"_"+syncID)){
					Element hierarchy = HierarchyXMLViewElement.getHierarchyElementByChild(document, syncID);
					return this.hierarchyViewElement.isValid(document, hierarchy);
				} else {
					return false;
				}
			} else {
				Element hierarchy = HierarchyXMLViewElement.getHierarchyElementByChild(document, syncID);
				return this.hierarchyViewElement.isValid(document, hierarchy);
			}
		} else {
			return false;
		}
	}
	
	@Override
	protected Element getRootElement(Document document) {
		return document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
	}

	public HierarchyXMLViewElement getHierarchyElementView() {
		return this.hierarchyViewElement;
	}
}
