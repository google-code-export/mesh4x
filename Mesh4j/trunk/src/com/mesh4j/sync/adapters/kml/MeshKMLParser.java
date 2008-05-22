package com.mesh4j.sync.adapters.kml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.parsers.IXMLView;
import com.mesh4j.sync.parsers.SyncInfoParser;
import com.mesh4j.sync.parsers.XMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class MeshKMLParser {
	
	// CONSTANTS
	public static Map<String, String> SEARCH_NAMESPACES = new HashMap<String, String>();
	static{
		SEARCH_NAMESPACES.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		SEARCH_NAMESPACES.put(KmlNames.MESH_PREFIX, KmlNames.MESH_URI);
		SEARCH_NAMESPACES.put(ISyndicationFormat.SX_PREFIX, ISyndicationFormat.NAMESPACE);
	}
	
	// MODEL VARIABLES
	private SyncInfoParser syncParser;
	private Map<String, IXMLView> xmlViews = new HashMap<String, IXMLView>();
	
	// BUSINESS METHODS

	public MeshKMLParser(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider) {
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.syncParser = new SyncInfoParser(syndicationFormat, identityProvider);

		XMLView folderParser = new XMLView();
		folderParser.addAttribute(KmlNames.MESH_QNAME_SYNC_ID);
		folderParser.addAttribute(KmlNames.MESH_QNAME_PARENT_ID);
		folderParser.addAttribute(KmlNames.KML_ATTRIBUTE_ID_QNAME);
		folderParser.addElement(KmlNames.KML_ELEMENT_NAME);
		folderParser.addElement(KmlNames.KML_ELEMENT_DESCRIPTION);
		folderParser.addElement(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		
		XMLView dummyParser = new XMLView();
		
		xmlViews.put(KmlNames.KML_ELEMENT_FOLDER, folderParser);
		xmlViews.put(KmlNames.KML_ELEMENT_STYLE_MAP, dummyParser);
		xmlViews.put(KmlNames.KML_ELEMENT_STYLE, dummyParser);
		xmlViews.put(KmlNames.KML_ELEMENT_PLACEMARK, dummyParser);
	}

	public void addElement(Element rootElement, Element newElement, SyncInfo syncInfo) {
		Element normalizedElement = this.normalize(newElement);		

		String parentID = getMeshParentId(normalizedElement);		
		Element parent = getElementByMeshId(rootElement, parentID);
		if(parent == null){
			parent = rootElement;
		}
		parent.add(normalizedElement);
		
		this.refresh(rootElement, normalizedElement, syncInfo);
	}
	
	public void updateElement(Element rootElement, Element newElement, SyncInfo syncInfo) {
		Element currentElement = this.getElementByMeshId(rootElement, syncInfo.getSyncId());
				
		String parentID = getMeshParentId(newElement);
		String actualParentID = getMeshSyncId(currentElement.getParent());
		boolean parentHasChanged = 
			(parentID == null && actualParentID != null) || 
			(parentID != null && actualParentID == null) || 
			(parentID != null && actualParentID != null && !parentID.equals(actualParentID));
		
		if(parentHasChanged){
			currentElement.getParent().remove(currentElement);
			
			Element parent = getElementByMeshId(rootElement, parentID);
			if(parent == null){
				parent = rootElement;
			}
			parent.add(currentElement);
		}
		
		this.updateFrom(currentElement, newElement);
		this.refresh(rootElement, currentElement, syncInfo);
	}
	
	public void refresh(Element rootElement, Element element, SyncInfo syncInfo) {
		Element syncRepository = getExtendedData(rootElement);
		
		String syncID = syncInfo.getSyncId();
		refreshMeshSyncID(rootElement, element, syncID);
		
		Element meshElement = getMeshElement(syncRepository, syncID);
		if(meshElement == null){
			meshElement = syncRepository.addElement(KmlNames.MESH_QNAME_SYNC);
		}		
		
		String parentID = getMeshSyncId(element.getParent());
		refreshParentID(element, parentID);

		refreshVersion(meshElement, String.valueOf(syncInfo.getVersion()));
		refreshSync(meshElement, syncInfo.getSync());
	}
	
	@SuppressWarnings("unchecked")
	private Element getMeshElement(Element syncRepository, String syncID){
		List<Element> elements = syncRepository.elements();
		for (Element element : elements) {
			if(KmlNames.MESH_QNAME_SYNC.equals(element.getQName())){
				Element syncElement = getSyncElement(element);
				String id = syncElement.attributeValue(ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID);
				if(syncID.equals(id)){
					return element;
				}
			}
		}
		return null;
	}
	
	private void refreshMeshSyncID(Element rootElement, Element element, String syncID) {
		String kmlID = element.attributeValue(KmlNames.KML_ATTRIBUTE_ID);
		Attribute atributeSyncID = element.attribute(KmlNames.MESH_QNAME_SYNC_ID);
		if(atributeSyncID == null){
			element.addAttribute(KmlNames.MESH_QNAME_SYNC_ID, syncID);			
			if(kmlID != null){
				String kmlIDRef = "#"+kmlID;				
				String newKmlID = kmlID+"_"+syncID;			
				String newKmlIDRef = "#"+newKmlID;
				
				element.addAttribute("id", kmlID+"_"+syncID);
				refreshReferences(rootElement, "//kml:*[text()='"+ kmlIDRef +"']", newKmlIDRef);
			}
		}
	}

	private void refreshReferences(Element rootElement, String xpathExpression, String newKmlIDRef) {
		List<Element> references = XMLHelper.selectElements(xpathExpression, rootElement, SEARCH_NAMESPACES);
		for (Element refElement : references) {
			refElement.setText(newKmlIDRef);
		}
	}
	
	private void refreshSync(Element meshElement, Sync sync) {
		Element syncElement = getSyncElement(meshElement);
		if(syncElement != null){
			meshElement.remove(syncElement);
		}
		syncElement = this.syncParser.convertSync2Element(sync);
		meshElement.add(syncElement);
	}
	
	private void refreshParentID(Element element, String parentID) {		
		Attribute parentIDAttr = element.attribute(KmlNames.MESH_QNAME_PARENT_ID);					
		if(parentID == null){
			if(parentIDAttr != null){
				element.remove(parentIDAttr);
			}
		}else{
			if(parentIDAttr == null){
				element.addAttribute(KmlNames.MESH_QNAME_PARENT_ID, parentID);
			} else if(!parentIDAttr.getValue().equals(parentID)){
				parentIDAttr.setValue(parentID);
			}
		}
	}	
	
	private void refreshVersion(Element meshElement, String version) {		
		Attribute versionAttr = meshElement.attribute(KmlNames.MESH_VERSION);					
		if(versionAttr == null){
			meshElement.addAttribute(KmlNames.MESH_VERSION, version);
		} else if(!versionAttr.getValue().equals(version)){
			versionAttr.setValue(version);
		}
	}	

	public String getMeshSyncId(Element element) {
		return element.attributeValue(KmlNames.MESH_QNAME_SYNC_ID);
	}
	
	public String getMeshParentId(Element element) {
		return element.attributeValue(KmlNames.MESH_QNAME_PARENT_ID);
	}		
	
	private Element getSyncElement(Element meshElement) {
		return meshElement.element(ISyndicationFormat.SX_QNAME_SYNC);
	}

	public boolean isValid(Element syncRepositoryRoot, Element element){
		String syncID = getMeshSyncId(element);
		if(syncID == null ){
			return false;
		}		
		
		Element syncRepository = this.getExtendedData(syncRepositoryRoot);
		if(syncRepository == null){
			return false;
		}
		
		Element meshElement = getMeshElement(syncRepository, syncID);
		if(meshElement == null){
			return false;
		}
		
		try{
			parseSyncInfo(meshElement);
		} catch (MeshException e) {
			return false;
		}
		
		String parentID = getMeshSyncId(element.getParent());
		String myParentID = element.attributeValue(KmlNames.MESH_QNAME_PARENT_ID);
		if((parentID == null && myParentID != null) || (parentID != null && myParentID == null)){
			return false;
		}else if (parentID == null && myParentID == null){
			return true;
		} else {
			return parentID.equals(myParentID);
		}
	}

	public boolean prepateSyncRepository(Element syncRepositoryRoot) {
		boolean dirty = false;
		
		Element extendedDataElement = getExtendedData(syncRepositoryRoot);
		if(extendedDataElement == null){
			extendedDataElement = syncRepositoryRoot.addElement(KmlNames.KML_ELEMENT_EXTENDED_DATA);
			dirty = true;
		}
		
		Namespace meshNS = extendedDataElement.getNamespaceForPrefix(KmlNames.MESH_PREFIX);
		if(meshNS == null){
			extendedDataElement.add(KmlNames.MESH_NS);
			dirty = true;
		}
		return dirty;
	}
	
	private Element getExtendedData(Element element){
		return element.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
	}
	
	public Element getElement(Element rootElement, String id)  {
		Element element = this.getElementByMeshId(rootElement, id);
		Element result = this.normalize(element);
		return result;
	}
	
	protected Element getElementByMeshId(Element rootElement, String id)  {
		return XMLHelper.selectSingleNode("//kml:*[@mesh4x:id='"+id+"']", rootElement, SEARCH_NAMESPACES);
	}
	
	public SyncInfo getSyncInfo(Element syncRepositoryRoot, String syncID){
		Element syncRepository = getExtendedData(syncRepositoryRoot);
		
		Element meshElement = this.getMeshElement(syncRepository, syncID);
		SyncInfo syncInfo = parseSyncInfo(meshElement);
		return syncInfo;
	}

	private Sync parseSync(Element syncElement) {
		try {
			return this.syncParser.convertSyncElement2Sync(syncElement);
		} catch (Throwable e) {
			throw new MeshException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SyncInfo> getAllSyncs(Element syncRepositoryRoot){
		Element syncRepository = this.getExtendedData(syncRepositoryRoot);
		ArrayList<SyncInfo> result = new ArrayList<SyncInfo>();
		if(syncRepository == null){
			return result;
		}
		
		List<Element> elements = syncRepository.elements();
		for (Element element : elements) {
			if(KmlNames.MESH_QNAME_SYNC.equals(element.getQName())){
				SyncInfo syncInfo = parseSyncInfo(element);
				result.add(syncInfo);
			}
		}
		return result;
	}

	private SyncInfo parseSyncInfo(Element meshElement) {
		if(meshElement == null){
			return null;
		}
		Element syncElement = getSyncElement(meshElement);
		Sync sync = parseSync(syncElement);
		int version = Integer.valueOf(meshElement.attributeValue(KmlNames.MESH_VERSION));
		SyncInfo syncInfo = new SyncInfo(sync, this.getType(), sync.getId(), version);
		return syncInfo;
	}

	public void refreshSyncInfo(Element syncRepositoryRoot, SyncInfo syncInfo) {
		Element syncRepository = getExtendedData(syncRepositoryRoot);
		
		String syncID = syncInfo.getSyncId();
		
		Element meshElement = getMeshElement(syncRepository, syncID);
		if(meshElement == null){
			meshElement = syncRepository.addElement(KmlNames.MESH_QNAME_SYNC);
		}		
		
		refreshVersion(meshElement, String.valueOf(syncInfo.getVersion()));
		refreshSync(meshElement, syncInfo.getSync());
	}

	public String getType() {
		return KmlNames.KML_PREFIX;
	}
	
	public void removeElement(Element rootElement, String syncID){
		Element element = this.getElementByMeshId(rootElement, syncID);
		if(element != null){
			element.getParent().remove(element);
		}
	}
	
	public List<Element> getElementsToSync(Document document) {
		List<Element> elements = XMLHelper.selectElements("//kml:StyleMap", document.getRootElement(), SEARCH_NAMESPACES);
		elements.addAll(XMLHelper.selectElements("//kml:Style", document.getRootElement(), SEARCH_NAMESPACES));
		elements.addAll(XMLHelper.selectElements("//kml:Folder", document.getRootElement(), SEARCH_NAMESPACES));
		elements.addAll(XMLHelper.selectElements("//kml:Placemark", document.getRootElement(), SEARCH_NAMESPACES));
		return elements;
	}

	
	private void updateFrom(Element currentElement, Element newElement) {
		IXMLView xmlView = this.xmlViews.get(currentElement.getName());
		xmlView.update(currentElement, newElement);		
	}
	
	public Element normalize(Element element) {
		if(element == null){
			return null;
		} else {
			IXMLView parser = this.xmlViews.get(element.getName());
			if(parser == null){
				return element;
			} else {
				return parser.normalize(element);
			}
		}
	}
}
