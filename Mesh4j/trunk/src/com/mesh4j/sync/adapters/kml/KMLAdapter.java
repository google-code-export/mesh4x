package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mesh4j.sync.AbstractRepositoryAdapter;
import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import com.mesh4j.sync.filter.SinceLastUpdateFilter;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class KMLAdapter extends AbstractRepositoryAdapter {
	
	// TODO (JMT) Folder Hierarchy, wrapper for properties per element to import/export
	// TODO (JMT) Supports kmz extension
	// TODO (JMT) Purge and clean mesh4x data to kml file.
	// TODO (JMT) XML Canonalization (C14N) 
	
	// MODEL VARIABLES
	private ISecurity security;
	private Document kmlDocument;
	private Element kmlDocumentElement;
	private File kmlFile;
	private MeshKMLParser meshParser;
	
	// BUSINESS METHODS
	public KMLAdapter(String fileName, ISecurity security){
		super();
		
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(security, "security");
		
		this.security = security;
		this.meshParser = new MeshKMLParser(AtomSyndicationFormat.INSTANCE, security);
		
		this.kmlFile = new File(fileName);
		this.kmlDocument = read(this.kmlFile);
		this.kmlDocumentElement = this.kmlDocument.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		this.prepareKMLToSync();
	}
	
	public static Document read(File kmlFile) {
		try{
			Document document = null;
			if(!kmlFile.exists()){
				document = DocumentHelper.createDocument();
				Element kmlElement = document.addElement(KmlNames.KML_ELEMENT, KmlNames.KML_URI);
				Element documentElement = kmlElement.addElement(KmlNames.KML_ELEMENT_DOCUMENT, KmlNames.KML_URI);
				Element elementName = documentElement.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
				elementName.addText(kmlFile.getName());
			} else {
				SAXReader saxReader = new SAXReader();
				document = saxReader.read(kmlFile);
				
				Element kmlElement = document.getRootElement();
				if(kmlElement == null || !KmlNames.KML_ELEMENT.equals(kmlElement.getName())){
					throw new MeshException("invalid kml file, root element should be a kml element.");
				}
				
				Element documentElement = kmlElement.element(KmlNames.KML_ELEMENT_DOCUMENT);
				if(documentElement == null){
					throw new MeshException("invalid kml file, kml element has not contains a document element.");
				}
			}
			return document;
		} catch(Exception e){
			throw new MeshException(e);
		}
	}	

	@Override
	public void add(Item item) {
		Guard.argumentNotNull(item, "item");

		SyncInfo syncInfo = new SyncInfo(item.getSync(), this.meshParser.getType(), item.getContent().getId(), item.getContent().getVersion());
		if (item.isDeleted()){
			this.meshParser.refreshSyncInfo(this.kmlDocumentElement, syncInfo);
		}else{
			KMLContent kmlContent = KMLContent.normalizeContent(item.getContent());
			this.meshParser.addElement(this.kmlDocumentElement, kmlContent.getPayload().createCopy(), syncInfo);
		}
		this.flush();
	}
	
	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");
		
		if (item.isDeleted()){
			SyncInfo syncInfo = this.meshParser.getSyncInfo(this.kmlDocumentElement, item.getSyncId());
			if(syncInfo != null){
				syncInfo.updateSync(item.getSync());
				this.meshParser.refreshSyncInfo(this.kmlDocumentElement, syncInfo);
				
				Element payload = this.meshParser.getElementByMeshId(this.kmlDocumentElement, syncInfo.getId());
				if(payload != null){
					payload.getParent().remove(payload);
				}
			}
		}else{
			SyncInfo syncInfo = new SyncInfo(item.getSync(), this.meshParser.getType(), item.getContent().getId(), item.getContent().getVersion());
			KMLContent kmlContent = KMLContent.normalizeContent(item.getContent());
			this.meshParser.updateElement(this.kmlDocumentElement, kmlContent.getPayload().createCopy(), syncInfo);
		}
		this.flush();
	} 

	@Override
	public void delete(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");

		SyncInfo syncInfo = this.meshParser.getSyncInfo(this.kmlDocumentElement, id);
		if (syncInfo != null){
			syncInfo.getSync().delete(this.getAuthenticatedUser(), new Date());
			this.meshParser.refreshSyncInfo(this.kmlDocumentElement, syncInfo);
		}
			
		Element element = this.meshParser.getElementByMeshId(this.kmlDocumentElement, id);
		if(element != null){
			element.getParent().remove(element);
		}
		this.flush();
	}

	@Override
	public Item get(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");

		SyncInfo syncInfo = this.meshParser.getSyncInfo(this.kmlDocumentElement, id);
		if(syncInfo == null){
			return null;
		}
		
		if(syncInfo.isDeleted()){
			NullContent nullContent = new NullContent(syncInfo.getSyncId());
			return new Item(nullContent, syncInfo.getSync());
		} else {
			Element payload = this.meshParser.getElementByMeshId(this.kmlDocumentElement, id);
//			if(this.meshParser.isFolder(payload)){
//				payload = this.meshParser.normalizeFolder(payload);
//			}
			KMLContent content = new KMLContent(payload, id);
			return new Item(content, syncInfo.getSync());		
		}
	}
	
	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
		RefreshItemsResult refreshResult = refreshContent();
		if(refreshResult.isDirty()){
			this.flush();
		}
		
		List<Item> result = new ArrayList<Item>();
		for (Item item : refreshResult.getItems()) {
			Item itemToSync = null;
//			if(this.meshParser.isFolder(item.getContent().getPayload())){
//				Element payload = this.meshParser.normalizeFolder(item.getContent().getPayload());
//				KMLContent content = new KMLContent(payload, item.getSyncId());
//				itemToSync = new Item(content, item.getSync());		
//			} else {
				itemToSync = item;
//			}
			
			if(appliesFilter(itemToSync, since, filter)){
				result.add(itemToSync);
			}
		}
		return result;
	}
	
	private boolean appliesFilter(Item item, Date since, IFilter<Item> filter) {
		boolean dateOk = SinceLastUpdateFilter.applies(item, since);
		return filter.applies(item) && dateOk;
	}
	
	public static void prepareKMLToSync(String fileName, ISecurity security) {
		new KMLAdapter(fileName, security);
	}	
	
	public void prepareKMLToSync() {
		RefreshItemsResult refreshResult = this.refreshContent();
		if(refreshResult.isDirty()){
			this.flush();
		}
	}	
	
	private RefreshItemsResult refreshContent() {	
		RefreshItemsResult refreshResult = new RefreshItemsResult();
		
		boolean dirty = this.meshParser.prepateSyncRepository(this.kmlDocumentElement);
		refreshResult.updateDirty(dirty);
		
		List<Element> elements = this.meshParser.getElementsToSync(this.kmlDocument);
		List<SyncInfo> syncs = this.meshParser.getAllSyncs(this.kmlDocumentElement);
		
		Map<String, SyncInfo> syncByID = this.makeSyncByID(syncs);
		
		for (Element element : elements) {
			String syncID = this.meshParser.getMeshSyncId(element);
			SyncInfo syncInfo = syncByID.get(syncID);
			KMLContent content = null;
			if(syncInfo == null){
				if(syncID == null){
					syncID = this.newID();
				}
				content = new KMLContent(element, syncID);
				Sync sync = new Sync(this.newID(), this.getAuthenticatedUser(), new Date(), false);
				syncInfo = new SyncInfo(sync, KmlNames.KML_PREFIX, content.getId(), content.getVersion());
				dirty = true;
			} else {
				content = new KMLContent(element, syncID);
				dirty = updateSync(content, syncInfo);
				syncs.remove(syncInfo);
			}
			
			refreshResult.updateDirty(dirty);
			refreshResult.addItem(new Item(content, syncInfo.getSync()));
			this.meshParser.refresh(this.kmlDocumentElement, element, syncInfo);
		}
		
		for (SyncInfo syncInfo : syncs) {
			dirty = updateSync(null, syncInfo);
			refreshResult.updateDirty(dirty);
			
			this.meshParser.refreshSyncInfo(this.kmlDocumentElement, syncInfo);
			
			refreshResult.addItem(new Item(new NullContent(syncInfo.getSyncId()), syncInfo.getSync()));
		}
		
		return refreshResult;
		
	}
	
	private boolean updateSync(IContent content, SyncInfo syncInfo){		
		Sync sync = syncInfo.getSync();
		if (content == null && sync != null){
			if (!sync.isDeleted()){
				sync.delete(this.getAuthenticatedUser(), new Date());
				return true;
			}
		}else{
			if (!syncInfo.isDeleted() && syncInfo.contentHasChanged(content)){
				sync.update(this.getAuthenticatedUser(), new Date(), sync.isDeleted());
				syncInfo.setVersion(content.getVersion());
				return true;
			}
		}
		return false;
	}

	private Map<String, SyncInfo> makeSyncByID(List<SyncInfo> syncs) {
		HashMap<String, SyncInfo> syncMap = new HashMap<String, SyncInfo>();
		for (SyncInfo sync : syncs) {
			syncMap.put(sync.getId(), sync);
		}
		return syncMap;
	}
	
	private void flush() {
		XMLHelper.write(this.kmlDocument, this.kmlFile);		
	}

	// UNSUPPORTED OPERATIONS
	
	@Override
	public String getFriendlyName() {		
		return MessageTranslator.translate(KMLAdapter.class.getName());
	}

	@Override
	public boolean supportsMerge() {
		return false;
	}
	
	@Override
	public List<Item> merge(List<Item> items) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAuthenticatedUser() {
		return this.security.getAuthenticatedUser();
	}
	
	private class RefreshItemsResult {
		
		private boolean dirty = false;
		private List<Item> items = new ArrayList<Item>();

		protected RefreshItemsResult() {}

		public void updateDirty(boolean dirty) {
			this.dirty = this.dirty || dirty;			
		}

		protected boolean isDirty() {
			return dirty;
		}

		protected List<Item> getItems() {
			return items;
		}
		
		protected void addItem(Item item){
			this.items.add(item);
		}
	}
	
	protected String newID() {
		return IdGenerator.newID();
	}
}