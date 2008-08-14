package org.mesh4j.sync.adapters.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.dom.parsers.IDOMRequied;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.IXMLView;
import org.mesh4j.sync.parsers.IXMLViewElement;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;


public abstract class MeshDOM implements IMeshDOM {

	// CONSTANTS
	public static Map<String, String> SEARCH_NAMESPACES = new HashMap<String, String>();
	static{
		SEARCH_NAMESPACES.put(MeshNames.XML_PREFIX, MeshNames.XML_URI);
		SEARCH_NAMESPACES.put(MeshNames.MESH_PREFIX, MeshNames.MESH_URI);
	}
	
	// MODEL VARIABLES
	private IXMLView xmlView;
	private Document document;
	private IIdentityProvider identityProvider;
	private IIdGenerator idGenerator;
	
	// BUSINESS METHODS

	public MeshDOM(Document document, IIdentityProvider identityProvider, IIdGenerator idGenerator, IXMLView xmlView) {
		Guard.argumentNotNull(document, "document");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNull(xmlView, "xmlView");
		
		this.document = document;
		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		this.xmlView = xmlView;
		
		this.initialize();
	}

	@Override
	public List<Element> getAllElements() {
		return this.xmlView.getAllElements(this.getDocument());
	}
	
	public Element getElement(String id){
		Element element = this.getElementByMeshId(id);
		Element result = this.normalize(element);
		return result;
	}

	public Element addElement(Element element){
		return this.xmlView.add(this.getDocument(), element);
	}
	
	public Element updateElement(Element element){
		String syncID = this.getMeshSyncId(element);
		Element currentElement = this.getElementByMeshId(syncID);
		return this.xmlView.update(this.getDocument(), currentElement, element);
	}
	
	public void deleteElement(String syncID){
		Element element = this.getElementByMeshId(syncID);
		this.xmlView.delete(this.getDocument(), element);
	}

	public String getMeshSyncId(Element element){
		return element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
	}
	
	public Element normalize(Element element){
		return this.xmlView.normalize(element);
	}

	// SYNC
	@SuppressWarnings("unchecked")
	public List<SyncInfo> getAllSyncs(){
		Element syncRepository = this.getSyncRepository(this.document);
		if(syncRepository == null){
			return new ArrayList<SyncInfo>();
		}

		ArrayList<SyncInfo> result = new ArrayList<SyncInfo>();
		List<Element> elements = syncRepository.elements();
		for (Element element : elements) {
			if(MeshNames.MESH_QNAME_SYNC.equals(element.getQName())){
				SyncInfo syncInfo = parseSyncInfo(element, this.identityProvider);
				result.add(syncInfo);
			}
		}
		return result;
	}
	
	public SyncInfo getSync(String syncId){
		Element syncRepository = getSyncRepository(this.document);
		
		Element meshElement = getMeshElement(syncRepository, syncId);
		SyncInfo syncInfo = parseSyncInfo(meshElement, this.identityProvider);
		return syncInfo;
	}
	

	public void updateSync(SyncInfo syncInfo){
		Element syncRepository = getSyncRepository(this.document);
		
		String syncID = syncInfo.getSyncId();
		
		Element meshElement = getMeshElement(syncRepository, syncID);
		if(meshElement == null){
			meshElement = syncRepository.addElement(MeshNames.MESH_QNAME_SYNC);
		}		
		
		this.refreshVersionAttribute(meshElement, String.valueOf(syncInfo.getVersion()));
		this.refreshSyncElement(meshElement, syncInfo.getSync());
	}
	
	private void refreshVersionAttribute(Element meshElement, String version) {		
		Attribute versionAttr = meshElement.attribute(MeshNames.MESH_VERSION);					
		if(versionAttr == null){
			meshElement.addAttribute(MeshNames.MESH_VERSION, version);
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
	
	public void updateMeshStatus(){

		List<Element> elements = this.getAllElements();		
		Map<String, SyncInfo> syncByID = getAllSyncsGroupBySyncID();
		
		for (Element element : elements) {
			Element normalizedElement = updateMeshStatus(element);
			if(normalizedElement != null){
				boolean syncHasChanged = false;
				String syncID = this.getMeshSyncId(element);
				SyncInfo syncInfo = this.getSync(syncID);
				
				if(syncInfo == null){
					IContent content = this.createContent(normalizedElement, syncID);
					Sync sync = new Sync(syncID, identityProvider.getAuthenticatedUser(), new Date(), false);
					syncInfo = new SyncInfo(sync, this.getType(), content.getId(), content.getVersion());
					syncHasChanged = true;
				} else {
					IContent content = this.createContent(normalizedElement, syncID);
					syncHasChanged = syncInfo.updateSyncIfChanged(content, identityProvider);
					syncByID.remove(syncID);
				}
				
				if(syncHasChanged){
					this.updateSync(syncInfo);
				}
			}
		}
		
		for (SyncInfo syncInfo : syncByID.values()) {
			updateMeshStatusMarkDeleted(syncInfo);
			this.updateSync(syncInfo);
		}
	}
	
	protected Element updateMeshStatus(Element element) {
		String syncID = this.getMeshSyncId(element);
		if(syncID == null){
			syncID = newID();
			element.addAttribute(MeshNames.MESH_QNAME_SYNC_ID, syncID);
		}
		return this.xmlView.refreshAndNormalize(this.document, element);
	}

	protected void updateMeshStatusMarkDeleted(SyncInfo syncInfo) {
		syncInfo.updateSyncIfChanged(null, identityProvider);
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
	
	// INTERNALS
	@SuppressWarnings("unchecked")
	private Element getMeshElement(Element syncRepository, String syncID){
		List<Element> elements = syncRepository.elements();
		for (Element element : elements) {
			if(MeshNames.MESH_QNAME_SYNC.equals(element.getQName())){
				Element syncElement = element.element(ISyndicationFormat.SX_QNAME_SYNC);
				String id = syncElement.attributeValue(ISyndicationFormat.SX_ATTRIBUTE_SYNC_ID);
				if(syncID.equals(id)){
					return element;
				}
			}
		}
		return null;
	}
	
	private Element getSyncElement(Element meshElement) {
		return meshElement.element(ISyndicationFormat.SX_QNAME_SYNC);
	}

	public String newID() {
		return this.idGenerator.newID();
	}
	
	private Map<String, SyncInfo> getAllSyncsGroupBySyncID() {		
		List<SyncInfo> syncs = this.getAllSyncs();
		HashMap<String, SyncInfo> syncMap = new HashMap<String, SyncInfo>();
		for (SyncInfo sync : syncs) {
			syncMap.put(sync.getId(), sync);
		}
		return syncMap;
	}

	private SyncInfo parseSyncInfo(Element meshElement, IIdentityProvider identityProvider) {
		if(meshElement == null){
			return null;
		}
		Element syncElement = getSyncElement(meshElement);
		Sync sync = SyncInfoParser.convertSyncElement2Sync(syncElement, AtomSyndicationFormat.INSTANCE, identityProvider, this.idGenerator);
		int version = Integer.valueOf(meshElement.attributeValue(MeshNames.MESH_VERSION));
		SyncInfo syncInfo = new SyncInfo(sync, this.getType(), sync.getId(), version);
		return syncInfo;
	}
	
	public Document getDocument(){
		return this.document;
	}
	
	public Element getElementByMeshId(String id)  {
		Element rootElement = getContentRepository(this.document);
		Element element = XMLHelper.selectSingleNode("//*[@xml:id='"+id+"']", rootElement, this.getSearchNamespaces());
		return element;
	}
	
	protected Map<String, String> getSearchNamespaces() {
		return this.xmlView.getNameSpaces();
	}

	public IIdentityProvider getIdentityProvider(){
		return this.identityProvider;
	}
	
	public boolean isValid(Element element){
		String syncID = element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
		if(syncID == null ){
			return false;
		}		
		
		Element syncRepository = this.getSyncRepository(this.document);
		 
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
		
		return this.xmlView.isValid(this.getDocument(), element);

	}
	
	public IXMLView getXMLView(){
		return this.xmlView;
	}
	
	private void initialize() {
		Collection<IXMLViewElement> elements = this.getXMLView().getXMLViewElements();
		for (IXMLViewElement viewElement : elements) {
			if(viewElement instanceof IDOMRequied){
				((IDOMRequied)viewElement).setDOM(this);
			}
		}		
	}
	
	public void clean(){
		Element syncRepository = getSyncRepository(this.document);
		
		List<SyncInfo> syncs = getAllSyncs();
		for (SyncInfo syncInfo : syncs) {
			Element meshElement = getMeshElement(syncRepository, syncInfo.getSyncId());
			meshElement.getParent().remove(meshElement);
		}
		
		syncRepository.remove(MeshNames.MESH_NS);
		
		List<Element> elements = getAllElements();
		for (Element element : elements) {
			this.xmlView.clean(this.document, element);
		}
	}
	
	public void purgue(){
		Element syncRepository = getSyncRepository(this.document);
		
		List<SyncInfo> syncs = getAllSyncs();
		for (SyncInfo syncInfo : syncs) {
			if(syncInfo.isDeleted()){
				Element meshElement = getMeshElement(syncRepository, syncInfo.getSyncId());
				meshElement.getParent().remove(meshElement);
			} else {
				if(syncInfo.purgue()){
					updateSync(syncInfo);
				}
			}
		}
	}

	
	// SUBCLASS RESPONSIBILITY
	public abstract String getType();
	public abstract Element getSyncRepository(Document document);
	public abstract Element getContentRepository(Document document);
	public abstract IContent createContent(Element element, String syncID);
	public abstract IContent normalizeContent(IContent content);

}
