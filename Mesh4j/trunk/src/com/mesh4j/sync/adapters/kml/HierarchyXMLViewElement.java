package com.mesh4j.sync.adapters.kml;

import java.util.Date;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.parsers.IXMLViewElement;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;

public class HierarchyXMLViewElement implements IXMLViewElement {

	// MODEL VARIABLES
	private KMLDOM kmlDOM;
	
	// BUSINESS METHODS
	
	public HierarchyXMLViewElement() {
		super();
	}

	@Override
	public Element add(Document document, Element element) {
		
		if(document == null || element == null){
			return null;
		}
		
		Element normalizedElement = this.normalize(element);
		if(normalizedElement == null){
			return null;
		}
		
		String parentId = normalizedElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID);
		String childId = normalizedElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID);
		
		Element defaultRoot = getDocumentElement(document);
		updateParentChildRelationship(defaultRoot, parentId, childId);

		Element syncRepo = getRootElement(document, element);
		syncRepo.add(normalizedElement);
		return normalizedElement;
	}
	
	private void updateParentChildRelationship(Element defaultRoot, String parentId, String childId) {
		Element child = getElementByMeshId(defaultRoot, childId);
		if(child != null){
			Element parent = getElementByMeshId(defaultRoot, parentId);
			Element actualParent = defaultRoot.equals(child.getParent()) ? null : child.getParent();
			if(actualParent != parent){
				child.getParent().remove(child);
				if(parent == null){
					defaultRoot.add(child);		
				} else {
					parent.add(child);
				}
			}
		}
	}

	protected Element getRootElement(Document document, Element element) {
		return getDocumentElement(document).element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
	}
	
	protected static Element getDocumentElement(Document document) {
		return document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
	}

	@Override
	public String getName() {
		return MeshNames.MESH_QNAME_HIERARCHY.getName();
	}

	@Override
	public Element normalize(Element element) {
		if(element != null && !this.getQName().equals(element.getQName())){
			return null;
		} else {
			return element;
		}
	}

	@Override
	public Element update(Document document, Element currentElement, Element newElement) {
		if(document == null || currentElement == null || newElement == null){
			return null;
		}
		if(!this.getQName().equals(currentElement.getQName()) || !this.getQName().equals(newElement.getQName())){
			return null;
		}
		
		String parentId = newElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID);
		String childId = newElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID);
		Element defaultRoot = getDocumentElement(document);
		
		updateParentChildRelationship(defaultRoot, parentId, childId);
		refreshHierarchyAttributes(currentElement, parentId, childId);
		return currentElement;
	}
	
	private static void refreshHierarchyAttributes(Element hierarchyElement, String parentID, String childID) {
		Attribute parentIDAttr = hierarchyElement.attribute(MeshNames.MESH_QNAME_PARENT_ID);					
		if(parentID == null){
			if(parentIDAttr != null){
				hierarchyElement.remove(parentIDAttr);
			}
		}else{
			if(parentIDAttr == null){
				hierarchyElement.addAttribute(MeshNames.MESH_QNAME_PARENT_ID, parentID);
			} else if(!parentIDAttr.getValue().equals(parentID)){
				parentIDAttr.setValue(parentID);
			}
		}		
		
		Attribute childIDAttr = hierarchyElement.attribute(MeshNames.MESH_QNAME_CHILD_ID);					
		if(childIDAttr == null){
			hierarchyElement.addAttribute(MeshNames.MESH_QNAME_CHILD_ID, childID);
		} else if(!childIDAttr.getValue().equals(childID)){
			childIDAttr.setValue(childID);
		}
	}

	public static String getMeshParentId(Document document, Element childElement) {
		if(childElement == null || document == null){
			return null;
		}
		
		String childID = childElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
		if(childID == null){
			return null;
		}
//		Element rootElement = getDocumentElement(document).element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
//		if(rootElement == null){
//			return null;
//		}
		
		Element hierarchy = getHierarchyElementByChild(document, childID);
		if(hierarchy == null){
			return null;
		}
		return hierarchy.attributeValue(MeshNames.MESH_QNAME_PARENT_ID);		
	}
	
	public static Element getHierarchyElementByChild(Document document, String id) {
		if(document == null || id == null){
			return null;
		} else {
			Element element = XMLHelper.selectSingleNode("//*[@mesh4x:childId='"+id+"']", document.getRootElement(), KMLViewElement.SEARCH_NAMESPACES);
			return element;
		}
	}
	
	private static Element getElementByMeshId(Element rootElement, String id) {
		Element element = XMLHelper.selectSingleNode("//*[@xml:id='"+id+"']", rootElement, KMLViewElement.SEARCH_NAMESPACES);
		return element;
	}
	
	@Override
	public void delete(Document document, Element element) {
		if(document == null || element == null){
			return;
		}
		if(!this.getQName().equals(element.getQName())){
			return;
		}
		
		Element parent = element.getParent();
		if(parent == null){
			return;
		} else {
			parent.remove(element);
		}
	}

	@Override
	public List<Element> getAllElements(Document document) {
		return XMLHelper.selectElements("//mesh4x:hierarchy", document.getRootElement(), KMLViewElement.SEARCH_NAMESPACES);
	}

	@Override
	public QName getQName() {
		return MeshNames.MESH_QNAME_HIERARCHY;
	}
	
	@Override
	public Element refresh(Document document, Element element) {
		if(document == null || element == null){
			return null;
		}
		
		if(!this.getQName().equals(element.getQName())){
			return null;
		}
	
		
		Element rootElement = getDocumentElement(document);
		String childID = element.attributeValue(MeshNames.MESH_QNAME_CHILD_ID);
		Element child = getElementByMeshId(rootElement, childID);
		if(child == null){
			// DELETE Hierarchy
			element.getParent().remove(element);
			return null;
		}else{
			// UPDATE PARENT ID
			String parentID = child.getParent().attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
			String hierarchyParentID = element.attributeValue(MeshNames.MESH_QNAME_PARENT_ID);
			
			boolean parentHasChanged = 
				(parentID == null && hierarchyParentID != null) || 
				(parentID != null && hierarchyParentID == null) || 
				(parentID != null && hierarchyParentID != null && !parentID.equals(hierarchyParentID));
			
			if(parentHasChanged){
				refreshHierarchyAttributes(element, parentID, childID);
				//updateSync(element);
			}
			return element;
		}
	}

	private void updateSync(Element element) {
		Guard.argumentNotNull(this.kmlDOM, "KMLDOM");
		
		String hierarchySyncID = element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
		IContent content = new KMLContent(element, hierarchySyncID);
		SyncInfo hierarchySyncInfo = this.kmlDOM.getSync(hierarchySyncID);
		if(hierarchySyncInfo == null){
			hierarchySyncInfo = new SyncInfo(
					new Sync(hierarchySyncID, this.kmlDOM.getIdentityProvider().getAuthenticatedUser(), new Date(), false), 
					this.kmlDOM.getType(), 
					hierarchySyncID, 
					content.getVersion());
		} else { 
			hierarchySyncInfo.updateSyncIfChanged(content, this.kmlDOM.getIdentityProvider());
		}
		this.kmlDOM.updateSync(hierarchySyncInfo);
	}
	
	@Override
	public boolean isValid(Document document, Element hierarchy) {
		if(document == null || hierarchy == null){
			return false;
		}
		
//		String syncID = element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
//		String parentID = element.getParent().attributeValue(MeshNames.MESH_QNAME_SYNC_ID);		
	//	Element syncRepository = getRootElement(document, element);
	//	Element hierarchy = getHierarchyElementByChild(document, syncID);
		
		String hierarchyParentID = hierarchy.attributeValue(MeshNames.MESH_QNAME_PARENT_ID);
		String childID = hierarchy.attributeValue(MeshNames.MESH_QNAME_CHILD_ID);
		Element child = getElementByMeshId(getRootElement(document, hierarchy), childID);
		if(child == null){
			return false;
		}
		String parentID = child.getParent() == null ? null : child.getParent().attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
		
		boolean parentHasChanged = 
			(parentID == null && hierarchyParentID != null) || 
			(parentID != null && hierarchyParentID == null) || 
			(parentID != null && hierarchyParentID != null && !parentID.equals(hierarchyParentID));
		return !parentHasChanged;
	}
 
	public void createHierarchyIfAbsent(Document document, Element elementChild) {
		Guard.argumentNotNull(this.kmlDOM, "KMLDOM");
		if(document == null || elementChild == null){
			return;
		}
		
		String childID = elementChild.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
		if(childID == null){
			return;
		}
		
		Element rootElement = getRootElement(document, elementChild);
		Element hierarchyElement = getHierarchyElementByChild(document, childID);
		if(hierarchyElement == null){
			hierarchyElement = rootElement.addElement(MeshNames.MESH_QNAME_HIERARCHY);
			hierarchyElement.addAttribute(MeshNames.MESH_QNAME_SYNC_ID, this.kmlDOM.newID());
			String parentID = elementChild.getParent().attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
			
			refreshHierarchyAttributes(hierarchyElement, parentID, childID);
			updateSync(hierarchyElement);
		}		
	}

	public void setKmlDOM(KMLDOM kmlDOM) {
		this.kmlDOM = kmlDOM;
	}
	
	public KMLDOM getKmlDOM() {
		return this.kmlDOM;
	}	
}


