package com.mesh4j.sync.adapters.dom.parsers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.dom.IMeshDOM;
import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.parsers.IXMLViewElement;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;

public class HierarchyXMLViewElement implements IXMLViewElement, IDOMRequied {

	// MODEL VARIABLES
	private IMeshDOM dom;
	
	// BUSINESS METHODS
	
	public HierarchyXMLViewElement() {
		super();
	}

	@Override
	public Element add(Document document, Element newElement) {
		
		if(document == null || newElement == null){
			return null;
		}
		
		Element normalizedElement = this.normalize(newElement);
		if(normalizedElement == null){
			return null;
		}
		
		String parentId = normalizedElement.attributeValue(MeshNames.MESH_QNAME_PARENT_ID);
		String childId = normalizedElement.attributeValue(MeshNames.MESH_QNAME_CHILD_ID);
		
		Element defaultRoot = getContentRepoElement(document);
		updateParentChildRelationship(defaultRoot, parentId, childId);

		Element syncRepo = getSyncRepoElement(document);
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

	protected Element getContentRepoElement(Document document) {
		Guard.argumentNotNull(this.dom, "dom");
		return this.dom.getContentRepository(document);
	}
	
	protected Element getSyncRepoElement(Document document) {
		Guard.argumentNotNull(this.dom, "dom");
		return this.dom.getSyncRepository(document);
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
		Element defaultRoot = getContentRepoElement(document);
		
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
			Element element = XMLHelper.selectSingleNode("//*[@mesh4x:childId='"+id+"']", document.getRootElement(), getNamespacesToSearch(document.getRootElement()));
			return element;
		}
	}
	
	private Element getElementByMeshId(Element rootElement, String id) {
		Element element = XMLHelper.selectSingleNode("//*[@xml:id='"+id+"']", rootElement, getNamespacesToSearch(rootElement));
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
		return XMLHelper.selectElements("//mesh4x:hierarchy", document.getRootElement(), getNamespacesToSearch(document.getRootElement()));
	}

	private static Map<String, String> getNamespacesToSearch(Element elementRoot) {
		Map<String, String> ns = new HashMap<String, String>();
		ns.put(MeshNames.XML_PREFIX, MeshNames.XML_URI);
		ns.put(MeshNames.MESH_PREFIX, MeshNames.MESH_URI);
		
		if(elementRoot.getNamespacePrefix() != null){
			ns.put(elementRoot.getNamespacePrefix(), elementRoot.getNamespaceURI());
		}		
		return ns;
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
	
		
		Element rootElement = getContentRepoElement(document);
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
		Guard.argumentNotNull(this.dom, "dom");
		
		String hierarchySyncID = element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
		IContent content = dom.createContent(element, hierarchySyncID);
		SyncInfo hierarchySyncInfo = this.dom.getSync(hierarchySyncID);
		if(hierarchySyncInfo == null){
			hierarchySyncInfo = new SyncInfo(
					new Sync(hierarchySyncID, this.dom.getIdentityProvider().getAuthenticatedUser(), new Date(), false), 
					this.dom.getType(), 
					hierarchySyncID, 
					content.getVersion());
		} else { 
			hierarchySyncInfo.updateSyncIfChanged(content, this.dom.getIdentityProvider());
		}
		this.dom.updateSync(hierarchySyncInfo);
	}
	
	@Override
	public boolean isValid(Document document, Element hierarchy) {
		if(document == null || hierarchy == null){
			return false;
		}
		
		String hierarchyParentID = hierarchy.attributeValue(MeshNames.MESH_QNAME_PARENT_ID);
		String childID = hierarchy.attributeValue(MeshNames.MESH_QNAME_CHILD_ID);
		Element child = getElementByMeshId(getContentRepoElement(document), childID);
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
		Guard.argumentNotNull(this.dom, "dom");
		if(document == null || elementChild == null){
			return;
		}
		
		String childID = elementChild.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
		if(childID == null){
			return;
		}
		
		Element hierarchyElement = getHierarchyElementByChild(document, childID);
		if(hierarchyElement == null){
			Element rootElement = getSyncRepoElement(document);
			hierarchyElement = rootElement.addElement(MeshNames.MESH_QNAME_HIERARCHY);
			hierarchyElement.addAttribute(MeshNames.MESH_QNAME_SYNC_ID, this.dom.newID());
			String parentID = elementChild.getParent().attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
			
			refreshHierarchyAttributes(hierarchyElement, parentID, childID);
			updateSync(hierarchyElement);
		}		
	}

	public void setDOM(IMeshDOM dom) {
		this.dom = dom;
	}
	
	public IMeshDOM getDOM() {
		return this.dom;
	}
}


