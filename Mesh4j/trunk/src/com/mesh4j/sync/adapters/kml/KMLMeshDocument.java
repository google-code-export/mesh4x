package com.mesh4j.sync.adapters.kml;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.parsers.SyncInfoParser;
import com.mesh4j.sync.parsers.XMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class KMLMeshDocument implements IKMLMeshDocument{
	
	// CONSTANTS
	public static Map<String, String> SEARCH_NAMESPACES = new HashMap<String, String>();
	static{
		SEARCH_NAMESPACES.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		SEARCH_NAMESPACES.put(KmlNames.MESH_PREFIX, KmlNames.MESH_URI);
		SEARCH_NAMESPACES.put(ISyndicationFormat.SX_PREFIX, ISyndicationFormat.NAMESPACE);
	}
	
	// MODEL VARIABLES
	private XMLView xmlView;
	private Document document;
	private IIdentityProvider identityProvider;
	
	// BUSINESS METHODS

	public KMLMeshDocument(Document document, IIdentityProvider identityProvider, XMLView xmlView) {
		Guard.argumentNotNull(document, "document");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(xmlView, "xmlView");
		
		this.document = document;
		this.identityProvider = identityProvider;
		this.xmlView = xmlView;
	}

	public KMLMeshDocument(String name, IIdentityProvider identityProvider, XMLView xmlView) {
		Guard.argumentNotNullOrEmptyString(name, "name");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(xmlView, "xmlView");
		
		this.document = createDocument(name);
		this.identityProvider = identityProvider;
		this.xmlView = xmlView;
	}
	
	// ELEMENTS
	public List<Element> getElementsToSync(){
		List<Element> elements = XMLHelper.selectElements("//kml:StyleMap", document.getRootElement(), SEARCH_NAMESPACES);
		elements.addAll(XMLHelper.selectElements("//kml:Style", document.getRootElement(), SEARCH_NAMESPACES));
		elements.addAll(XMLHelper.selectElements("//kml:Folder", document.getRootElement(), SEARCH_NAMESPACES));
		elements.addAll(XMLHelper.selectElements("//kml:Placemark", document.getRootElement(), SEARCH_NAMESPACES));
		return elements;
	}
	
	public Element getElement(String id){
		Element element = this.getElementByMeshId(id);
		Element result = this.normalize(element);
		return result;
	}
	
	public String getMeshSyncId(Element element){
		return element.attributeValue(KmlNames.MESH_QNAME_SYNC_ID);
	}

	public void addElement(Element newElement, SyncInfo syncInfo){
		
		Element normalizedElement = this.normalize(newElement);		

		String parentID = getMeshParentId(normalizedElement);		
		
		Element parent = getElementByMeshId(parentID);
		if(parent == null){
			parent = this.getContentRepository();
		}
		parent.add(normalizedElement);
		
		this.refresh(normalizedElement, syncInfo);
	}
	
	private void refresh(Element element, SyncInfo syncInfo) {
		Element syncRepository = getSyncRepository();
		
		String syncID = syncInfo.getSyncId();
		refreshMeshSyncIDAttributeAndReferences(element, syncID);
		
		Element meshElement = getMeshElement(syncRepository, syncID);
		if(meshElement == null){
			meshElement = syncRepository.addElement(KmlNames.MESH_QNAME_SYNC);
		}		
		
		String parentID = getMeshSyncId(element.getParent());
		refreshParentIDAttribute(element, parentID);

		refreshVersionAttribute(meshElement, String.valueOf(syncInfo.getVersion()));
		refreshSyncElement(meshElement, syncInfo.getSync());
	}
	
	private void refreshMeshSyncIDAttributeAndReferences(Element element, String syncID) {
		String kmlID = element.attributeValue(KmlNames.KML_ATTRIBUTE_ID);
		Attribute atributeSyncID = element.attribute(KmlNames.MESH_QNAME_SYNC_ID);
		if(atributeSyncID == null){
			element.addAttribute(KmlNames.MESH_QNAME_SYNC_ID, syncID);			
			if(kmlID != null){
				String kmlIDRef = "#"+kmlID;				
				String newKmlID = kmlID+"_"+syncID;			
				String newKmlIDRef = "#"+newKmlID;
				
				element.addAttribute("id", kmlID+"_"+syncID);
				refreshReferences("//kml:*[text()='"+ kmlIDRef +"']", newKmlIDRef);
			}
		}
	}

	private void refreshReferences(String xpathExpression, String newKmlIDRef) {
		Element rootElement = getContentRepository();
		List<Element> references = XMLHelper.selectElements(xpathExpression, rootElement, SEARCH_NAMESPACES);
		for (Element refElement : references) {
			refElement.setText(newKmlIDRef);
		}
	}
	
	public void updateElement(Element newElement, SyncInfo syncInfo){
		Element currentElement = this.getElementByMeshId(syncInfo.getSyncId());
		
		String parentID = getMeshParentId(newElement);
		String actualParentID = getMeshSyncId(currentElement.getParent());
		boolean parentHasChanged = 
			(parentID == null && actualParentID != null) || 
			(parentID != null && actualParentID == null) || 
			(parentID != null && actualParentID != null && !parentID.equals(actualParentID));
		
		if(parentHasChanged){
			currentElement.getParent().remove(currentElement);
			
			Element parent = getElementByMeshId(parentID);
			if(parent == null){
				parent = this.getContentRepository();
			}
			parent.add(currentElement);
		}
		
		this.xmlView.update(currentElement, newElement);
		
		this.refresh(currentElement, syncInfo);	
	}
	
	public void removeElement(String syncID){
		Element element = this.getElementByMeshId(syncID);
		if(element != null){
			element.getParent().remove(element);
		}
	}
	
	public Element normalize(Element element){
		return this.xmlView.normalize(element);
	}

	// SYNC
	@SuppressWarnings("unchecked")
	public List<SyncInfo> getAllSyncs(){
		Element syncRepository = this.getSyncRepository();
		if(syncRepository == null){
			return new ArrayList<SyncInfo>();
		}

		ArrayList<SyncInfo> result = new ArrayList<SyncInfo>();
		List<Element> elements = syncRepository.elements();
		for (Element element : elements) {
			if(KmlNames.MESH_QNAME_SYNC.equals(element.getQName())){
				SyncInfo syncInfo = parseSyncInfo(element, this.identityProvider);
				result.add(syncInfo);
			}
		}
		return result;
	}
	
	public SyncInfo getSync(String syncId){
		Element syncRepository = getSyncRepository();
		
		Element meshElement = getMeshElement(syncRepository, syncId);
		SyncInfo syncInfo = parseSyncInfo(meshElement, this.identityProvider);
		return syncInfo;
	}
	
	public void refreshSync(SyncInfo syncInfo){
		Element syncRepository = getSyncRepository();
		
		String syncID = syncInfo.getSyncId();
		
		Element meshElement = getMeshElement(syncRepository, syncID);
		if(meshElement == null){
			meshElement = syncRepository.addElement(KmlNames.MESH_QNAME_SYNC);
		}		
		
		this.refreshVersionAttribute(meshElement, String.valueOf(syncInfo.getVersion()));
		this.refreshSyncElement(meshElement, syncInfo.getSync());
	}
	
	private void refreshVersionAttribute(Element meshElement, String version) {		
		Attribute versionAttr = meshElement.attribute(KmlNames.MESH_VERSION);					
		if(versionAttr == null){
			meshElement.addAttribute(KmlNames.MESH_VERSION, version);
		} else if(!versionAttr.getValue().equals(version)){
			versionAttr.setValue(version);
		}
	}

	private void refreshSyncElement(Element meshElement, Sync sync) {
		Element syncElement = getSyncElement(meshElement);
		if(syncElement != null){
			meshElement.remove(syncElement);
		}
		syncElement = SyncInfoParser.convertSync2Element(sync, AtomSyndicationFormat.INSTANCE, identityProvider);
		meshElement.add(syncElement);
	}
	
	private void refreshParentIDAttribute(Element element, String parentID) {		
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
	
	public void updateMeshStatus(){
		validateDocument(this.document);
		prepateSyncRepository(this.document);

		List<Element> elements = this.getElementsToSync();
		
		Map<String, SyncInfo> syncByID = getAllSyncsGroupBySyncID();
		
		for (Element element : elements) {
			String syncID = this.getMeshSyncId(element);
			SyncInfo syncInfo = syncByID.get(syncID);
			KMLContent content = null;
			if(syncInfo == null){
				if(syncID == null){
					syncID = newID();
				}
				content = new KMLContent(this.normalize(element), syncID);
				Sync sync = new Sync(newID(), identityProvider.getAuthenticatedUser(), new Date(), false);
				syncInfo = new SyncInfo(sync, KmlNames.KML_PREFIX, content.getId(), content.getVersion());
			} else {
				content = new KMLContent(this.normalize(element), syncID);
				syncInfo.updateSyncIfChanged(content, identityProvider);
				syncByID.remove(syncID);
			}
			this.refresh(element, syncInfo);
		}
		
		for (SyncInfo syncInfo : syncByID.values()) {
			syncInfo.updateSyncIfChanged(null, identityProvider);
			this.refreshSync(syncInfo);			
		}
		this.normalize();
	}
	
	// DOM
	public Document toDocument(){
		return this.document;
	}
	
	public String asXML(){
		return this.document.asXML();
	}
	
	public void normalize(){
		this.document.normalize();
	}
	
	public String getType(){
		return KmlNames.KML_PREFIX;
	}
	
	// INTERNALS
	private Element getElementByMeshId(String id)  {
		Element rootElement = getContentRepository();
		return XMLHelper.selectSingleNode("//kml:*[@mesh4x:id='"+id+"']", rootElement, SEARCH_NAMESPACES);
	}
	
	private Element getContentRepository(){
		return this.document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
	}
	
	private Element getSyncRepository(){
		Element element = getContentRepository();
		return element.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
	}
	
	@SuppressWarnings("unchecked")
	private static Element getMeshElement(Element syncRepository, String syncID){
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
	
	private static Element getSyncElement(Element meshElement) {
		return meshElement.element(ISyndicationFormat.SX_QNAME_SYNC);
	}

	public String getMeshParentId(Element element) {
		return element.attributeValue(KmlNames.MESH_QNAME_PARENT_ID);
	}	

	private String newID() {
		return IdGenerator.newID();
	}
	
	private void prepateSyncRepository(Document kmlDocument) {
		
		Element syncRepositoryRoot = kmlDocument.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		
		Element extendedDataElement = syncRepositoryRoot.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);;
		if(extendedDataElement == null){
			extendedDataElement = syncRepositoryRoot.addElement(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		}
		
		Namespace meshNS = extendedDataElement.getNamespaceForPrefix(KmlNames.MESH_PREFIX);
		if(meshNS == null){
			extendedDataElement.add(KmlNames.MESH_NS);
		}
	}

	private Document createDocument(String name) {
		Document kmlDocument = DocumentHelper.createDocument();
		Element kmlElement = kmlDocument.addElement(KmlNames.KML_ELEMENT, KmlNames.KML_URI);
		Element documentElement = kmlElement.addElement(KmlNames.KML_ELEMENT_DOCUMENT, KmlNames.KML_URI);
		Element elementName = documentElement.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
		elementName.addText(name);
		
		prepateSyncRepository(kmlDocument);
		kmlDocument.normalize();
		return kmlDocument;
	}

	private Map<String, SyncInfo> getAllSyncsGroupBySyncID() {		
		List<SyncInfo> syncs = this.getAllSyncs();
		HashMap<String, SyncInfo> syncMap = new HashMap<String, SyncInfo>();
		for (SyncInfo sync : syncs) {
			syncMap.put(sync.getId(), sync);
		}
		return syncMap;
	}

	private static SyncInfo parseSyncInfo(Element meshElement, IIdentityProvider identityProvider) {
		if(meshElement == null){
			return null;
		}
		Element syncElement = getSyncElement(meshElement);
		Sync sync = SyncInfoParser.convertSyncElement2Sync(syncElement, AtomSyndicationFormat.INSTANCE, identityProvider);
		int version = Integer.valueOf(meshElement.attributeValue(KmlNames.MESH_VERSION));
		SyncInfo syncInfo = new SyncInfo(sync, KmlNames.KML_PREFIX, sync.getId(), version);
		return syncInfo;
	}

	public static boolean isValid(Document document, Element element, IIdentityProvider identityProvider){
		String syncID = element.attributeValue(KmlNames.MESH_QNAME_SYNC_ID);
		if(syncID == null ){
			return false;
		}		
		
		Element syncRepository = document
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		 
		if(syncRepository == null){
			return false;
		}
		
		Element meshElement = getMeshElement(syncRepository, syncID);
		if(meshElement == null){
			return false;
		}
		
		try{
			parseSyncInfo(meshElement, identityProvider);
		} catch (RuntimeException e) {
			return false;
		}
		
		String parentID = element.getParent().attributeValue(KmlNames.MESH_QNAME_SYNC_ID);
		String myParentID = element.attributeValue(KmlNames.MESH_QNAME_PARENT_ID);
		if((parentID == null && myParentID != null) || (parentID != null && myParentID == null)){
			return false;
		}else if (parentID == null && myParentID == null){
			return true;
		} else {
			return parentID.equals(myParentID);
		}
	}
	
	private void validateDocument(Document kmlDocument) {
		Element kmlElement = kmlDocument.getRootElement();
		if(kmlElement == null || !KmlNames.KML_ELEMENT.equals(kmlElement.getName())){
			throw new MeshException("invalid kml file, root element should be a kml element.");
		}
		
		Element documentElement = kmlElement.element(KmlNames.KML_ELEMENT_DOCUMENT);
		if(documentElement == null){
			throw new MeshException("invalid kml file, kml element has not contains a document element.");
		}
	}
}
