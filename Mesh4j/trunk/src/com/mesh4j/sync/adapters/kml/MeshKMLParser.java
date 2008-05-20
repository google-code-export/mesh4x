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
import com.mesh4j.sync.parsers.SyncInfoParser;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

/**
 * 
 *  <ExtendedData xmlns:mesh4x="http://mesh4x.org/kml" >
 *		<mesh4x:sync parentId="3" xmlns:sx="http://feedsync.org/2007/feedsync">
 *      	<sx:sync id="2" updates="3">
 *      		<sx:history sequence="3" when="2005-05-21T11:43:33Z" by="JEO2000"/>
 *      		<sx:history sequence="2" when="2005-05-21T10:43:33Z" by="REO1750"/>
 *      		<sx:history sequence="1" when="2005-05-21T09:43:33Z" by="REO1750"/>
 *      		<sx:conflicts>
 *					<Placemark>
 *          		...
 *					</Placemark
 *      		</sx:conflicts>
 *     		</sx:sync>
 *		<mesh4x:sync>
 * *		<mesh4x:sync parentId="root" xmlns:sx="http://feedsync.org/2007/feedsync">
 *      	<sx:sync id="3" updates="1">
 *      		<sx:history sequence="1" when="2005-05-21T09:43:33Z" by="REO1750"/>
 *     		</sx:sync>
 *		<mesh4x:sync>
 *  </ExtendedData>
 *   
 * @author jtondato
 */

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
	
	// BUSINESS METHODS

	public MeshKMLParser(ISyndicationFormat syndicationFormat, ISecurity security) {
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(security, "security");
		
		this.syncParser = new SyncInfoParser(syndicationFormat, security);
	}

	public void addElement(Element rootElement, Element newElement, SyncInfo syncInfo) {
		String parentID = getMeshParentId(newElement);
		
		Element parent = getElementByMeshId(rootElement, parentID);
		if(parent == null){
			parent = rootElement;
		}
		
//		if(isFolder(newElement)){
//			Element folder = normalizeFolder(newElement);
//			parent.add(folder);
//			refresh(rootElement, folder, syncInfo);
//		} else {
			parent.add(newElement);
			refresh(rootElement, newElement, syncInfo);
//		}
	}
	
//	public boolean isFolder(Element newElement) {
//		return KmlNames.KML_ELEMENT_FOLDER.equals(newElement.getName());
//	}

	public void updateElement(Element rootElement, Element newElement, SyncInfo syncInfo) {
//		if(isFolder(newElement)){
//			updateFolder(rootElement, newElement, syncInfo);
//		} else {
			Element currentElement = this.getElementByMeshId(rootElement, syncInfo.getSyncId());
			currentElement.getParent().remove(currentElement);
			addElement(rootElement, newElement, syncInfo);
//		}
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
		refreshParent(element, parentID);

		refreshVersion(meshElement, String.valueOf(syncInfo.getVersion()));
		refreshSync(meshElement, syncInfo.getSync());
	}
	
	@SuppressWarnings("unchecked")
	private Element getMeshElement(Element syncRepository, String syncID){
		// TODO (JMT) use xPath
//			Element syncElement = XMLHelper.selectSingleNode("//sx:sync[id='"+syncID+"']", syncRepository, SEARCH_NAMESPACES);
//			return syncElement == null ? null : syncElement.getParent();
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
	
	private void refreshParent(Element element, String parentID) {		
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
			extendedDataElement = syncRepositoryRoot.addElement(KmlNames.KML_EXTENDED_DATA_ELEMENT);
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
		return element.element(KmlNames.KML_EXTENDED_DATA_ELEMENT);
	}
	
	public Element getElementByMeshId(Element rootElement, String id)  {
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
		
		// TODO (JMT) use xPath
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
	
	protected List<Element> getElementsToSync(Document document) {
		List<Element> elements = XMLHelper.selectElements("//kml:StyleMap", document.getRootElement(), SEARCH_NAMESPACES);
		elements.addAll(XMLHelper.selectElements("//kml:Style", document.getRootElement(), SEARCH_NAMESPACES));
		elements.addAll(XMLHelper.selectElements("//kml:Folder", document.getRootElement(), SEARCH_NAMESPACES));
		elements.addAll(XMLHelper.selectElements("//kml:Placemark", document.getRootElement(), SEARCH_NAMESPACES));
		return elements;
	}

//	protected Element normalizeFolder(Element element) {
//		String syncID = this.getMeshSyncId(element);
//		String parentID = this.getMeshSyncId(element);
//			
//		Element newFolder = DocumentHelper.createDocument().addElement(KmlNames.KML_ELEMENT_FOLDER, KmlNames.KML_URI);
//		newFolder.addNamespace(KmlNames.MESH_PREFIX, KmlNames.MESH_URI);
//			
//		if(syncID != null){
//			newFolder.addAttribute(KmlNames.MESH_QNAME_SYNC_ID, syncID);
//		}
//			
//		if(parentID != null){
//			newFolder.addAttribute(KmlNames.MESH_QNAME_PARENT_ID, parentID);
//		}
//	
//		Element folderNameElement = element.element(KmlNames.KML_ELEMENT_NAME);
//		if(folderNameElement != null){
//			Element newFolderNameElement = newFolder.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
//			newFolderNameElement.addText(folderNameElement.getText());
//		}
//			
//		Element folderDescriptionElement = element.element(KmlNames.KML_ELEMENT_DESCRIPTION);
//		if(folderDescriptionElement != null){
//			Element newFolderDescriptionElement = newFolder.addElement(KmlNames.KML_ELEMENT_DESCRIPTION, KmlNames.KML_URI);
//			newFolderDescriptionElement.addText(folderDescriptionElement.getText());
//		}
//		return newFolder;
//
//	}
//	
//	protected void updateFolder(Element rootElement, Element newElement, SyncInfo syncInfo) {
//		Element folder = getElementByMeshId(rootElement, syncInfo.getSyncId());
//		
//		Element updatedName = newElement.element(KmlNames.KML_ELEMENT_NAME);		
//		Element folderNameElement = folder.element(KmlNames.KML_ELEMENT_NAME);
//		if(folderNameElement == null){
//			if(updatedName != null){
//				folderNameElement = folder.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
//				folderNameElement.addText(updatedName.getText());
//			}
//		} else{
//			if(updatedName != null){
//				folderNameElement.setText(updatedName.getText());
//			} else {
//				folder.remove(folderNameElement);
//			}
//		}
//		
//		Element updatedDesc = newElement.element(KmlNames.KML_ELEMENT_DESCRIPTION);
//		Element folderDescElement = folder.element(KmlNames.KML_ELEMENT_DESCRIPTION);
//		if(folderDescElement == null){
//			if(updatedDesc != null){
//				folderDescElement = folder.addElement(KmlNames.KML_ELEMENT_DESCRIPTION, KmlNames.KML_URI);
//				folderDescElement.addText(updatedDesc.getText());
//			}
//		} else{
//			if(updatedDesc != null){
//				folderDescElement.setText(updatedDesc.getText());
//			} else {
//				folder.remove(folderDescElement);
//			}
//		}
//
//		String myParentID = getMeshParentId(folder);
//		String parentId = getMeshParentId(newElement);
//		
//		boolean equalsParentID = (myParentID == null && parentId == null) || (myParentID != null && myParentID.equals(parentId));
//		if(!equalsParentID){
//			Element newParent = getElementByMeshId(rootElement, parentId);
//			if(newParent != null){
//				Element newFolder = folder.createCopy();
//				folder.getParent().remove(folder);
//				newParent.add(newFolder);
//				refresh(rootElement, newFolder, syncInfo);
//				return;
//			}
//		}
//		refresh(rootElement, folder, syncInfo);
//
//	}
}
