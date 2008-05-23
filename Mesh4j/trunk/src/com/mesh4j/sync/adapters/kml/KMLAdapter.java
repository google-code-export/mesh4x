package com.mesh4j.sync.adapters.kml;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.mesh4j.sync.AbstractSyncAdapter;
import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.ISyncAware;
import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.filter.SinceLastUpdateFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.validations.Guard;

public class KMLAdapter extends AbstractSyncAdapter implements ISyncAware {
	
	// TODO (JMT) Purge and clean mesh4x data to kml file.
	// TODO (JMT) XML Canonalization (C14N) for versioning
	
	// MODEL VARIABLES
	private IKMLMeshDomLoader domLoader;
	
	// BUSINESS METHODS
	public KMLAdapter(IKMLMeshDomLoader domLoader){
		Guard.argumentNotNull(domLoader, "domLoader");
		this.domLoader = domLoader;
	}
	
	@Override
	public void add(Item item) {
		Guard.argumentNotNull(item, "item");

		SyncInfo syncInfo = new SyncInfo(item.getSync(), this.getMeshKmlDocument().getType(), item.getContent().getId(), item.getContent().getVersion());
		if (item.isDeleted()){
			this.getMeshKmlDocument().refreshSync(syncInfo);
		}else{
			KMLContent kmlContent = KMLContent.normalizeContent(item.getContent());
			this.getMeshKmlDocument().addElement(kmlContent.getPayload().createCopy(), syncInfo);
		}
	}
	
	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");
		
		if (item.isDeleted()){
			SyncInfo syncInfo = this.getMeshKmlDocument().getSync(item.getSyncId());
			if(syncInfo != null){
				syncInfo.updateSync(item.getSync());
				this.getMeshKmlDocument().refreshSync(syncInfo);				
				this.getMeshKmlDocument().removeElement(syncInfo.getId());
			}
		}else{
			SyncInfo syncInfo = new SyncInfo(item.getSync(), this.getMeshKmlDocument().getType(), item.getContent().getId(), item.getContent().getVersion());
			KMLContent kmlContent = KMLContent.normalizeContent(item.getContent());
			this.getMeshKmlDocument().updateElement(kmlContent.getPayload().createCopy(), syncInfo);
		}
	} 

	@Override
	public void delete(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");

		SyncInfo syncInfo = this.getMeshKmlDocument().getSync(id);
		if (syncInfo != null && !syncInfo.isDeleted()){
			syncInfo.getSync().delete(this.getAuthenticatedUser(), new Date());
			this.getMeshKmlDocument().refreshSync(syncInfo);
		}else if(syncInfo == null){
			Sync sync = new Sync(id, this.getAuthenticatedUser(), new Date(), true);
			syncInfo = new SyncInfo(sync, this.getMeshKmlDocument().getType(), id, new NullContent(id).getVersion());
			this.getMeshKmlDocument().refreshSync(syncInfo);
		}		
		this.getMeshKmlDocument().removeElement(id);
	}

	@Override
	public Item get(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");

		SyncInfo syncInfo = this.getMeshKmlDocument().getSync(id);
		if(syncInfo == null){
			return null;
		}
		
		if(syncInfo.isDeleted()){
			NullContent nullContent = new NullContent(syncInfo.getSyncId());
			return new Item(nullContent, syncInfo.getSync());
		} else {
			Element payload = this.getMeshKmlDocument().getElement(id);
			KMLContent content = new KMLContent(payload, id);
			return new Item(content, syncInfo.getSync());		
		}
	}
	
	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
		
		ArrayList<Item> result = new ArrayList<Item>();
		
		List<Element> elements = this.getMeshKmlDocument().getElementsToSync();
		Map<String, SyncInfo> syncByID = this.convertToMapByID(this.getMeshKmlDocument().getAllSyncs());
		
		for (Element element : elements) {
			String syncID = this.getMeshKmlDocument().getMeshSyncId(element);
			SyncInfo syncInfo = syncByID.get(syncID);
			KMLContent content = new KMLContent(this.getMeshKmlDocument().normalize(element), syncID);
			syncByID.remove(syncID);

			Item item = new Item(content, syncInfo.getSync());
			if(appliesFilter(item, since, filter)){
				result.add(item);
			}			
		}
		
		for (SyncInfo syncInfo : syncByID.values()) {
			Item item = new Item(new NullContent(syncInfo.getSyncId()), syncInfo.getSync());
			if(appliesFilter(item, since, filter)){
				result.add(item);
			}
		}

		return result;
	}
	
	private Map<String, SyncInfo> convertToMapByID(List<SyncInfo> syncs) {
		HashMap<String, SyncInfo> syncMap = new HashMap<String, SyncInfo>();
		for (SyncInfo sync : syncs) {
			syncMap.put(sync.getId(), sync);
		}
		return syncMap;
	}
	
	private boolean appliesFilter(Item item, Date since, IFilter<Item> filter) {
		boolean dateOk = SinceLastUpdateFilter.applies(item, since);
		boolean filterOK = filter.applies(item); 
		return filterOK && dateOk;
	}
	
	public static void prepareKMLToSync(IKMLMeshDomLoader domLoader) {
		KMLAdapter kmlAdapter = new KMLAdapter(domLoader);
		kmlAdapter.beginSync();
		kmlAdapter.endSync();
	}	
		
	// UNSUPPORTED OPERATIONS
	
	@Override
	public String getFriendlyName() {		
		return MessageTranslator.translate(KMLAdapter.class.getName());
	}

	@Override
	public String getAuthenticatedUser() {
		return getIdentityProvider().getAuthenticatedUser();
	}

	private IIdentityProvider getIdentityProvider() {
		return this.domLoader.getIdentityProvider();
	}
	
	@Override
	public void beginSync() {
		this.domLoader.read();
	}

	@Override
	public void endSync() {
		this.domLoader.write();
	}
	
	private IKMLMeshDocument getMeshKmlDocument() {
		return this.domLoader.getDocument();
	}
	
}