package org.mesh4j.sync.adapters.dom;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;


public class DOMAdapter  extends AbstractSyncAdapter implements ISyncAware {
	
	// MODEL VARIABLES
	private IDOMLoader domLoader;
	
	// BUSINESS METHODS
	public DOMAdapter(IDOMLoader domLoader){
		Guard.argumentNotNull(domLoader, "domLoader");
		this.domLoader = domLoader;
	}
	
	@Override
	public void add(Item item) {
		Guard.argumentNotNull(item, "item");
		
		if (item.isDeleted()){
			SyncInfo syncInfo = new SyncInfo(item.getSync(), this.getDOM().getType(), item.getContent().getId(), item.getContent().getVersion());
			this.getDOM().updateSync(syncInfo);	
		} else {
			IContent content = this.getDOM().normalizeContent(item.getContent());
			if(content != null){
				Element elementAdded = this.getDOM().addElement(content.getPayload().createCopy());
				if(elementAdded != null){
					IContent contentAdded = this.getDOM().createContent(elementAdded, item.getSyncId());
					SyncInfo syncInfo = new SyncInfo(item.getSync(), this.getDOM().getType(), contentAdded.getId(), contentAdded.getVersion());
					this.getDOM().updateSync(syncInfo);	
				}
			}
		}
		
	}
	
	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");
		
		if (item.isDeleted()){
			SyncInfo syncInfo = this.getDOM().getSync(item.getSyncId());
			if(syncInfo != null){
				syncInfo.updateSync(item.getSync());
				this.getDOM().deleteElement(syncInfo.getId());
				this.getDOM().updateSync(syncInfo);
			}
		}else{
			IContent content = this.getDOM().normalizeContent(item.getContent());
			if(content != null){
				Element elementUpdated = this.getDOM().updateElement(content.getPayload().createCopy());
				IContent contentUpdated = this.getDOM().createContent(elementUpdated, item.getSyncId());
				
				SyncInfo syncInfo = new SyncInfo(item.getSync(), this.getDOM().getType(), contentUpdated.getId(), contentUpdated.getVersion());
				this.getDOM().updateSync(syncInfo);
			}
		}
	} 

	@Override
	public void delete(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");

		SyncInfo syncInfo = this.getDOM().getSync(id);
		if (syncInfo != null && !syncInfo.isDeleted()){
			syncInfo.getSync().delete(this.getAuthenticatedUser(), new Date());
		}else if(syncInfo == null){
			Sync sync = new Sync(id, this.getAuthenticatedUser(), new Date(), true);
			syncInfo = new SyncInfo(sync, this.getDOM().getType(), id, new NullContent(id).getVersion());
		}		
		this.getDOM().deleteElement(id);
		this.getDOM().updateSync(syncInfo);
	}

	@Override
	public Item get(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");

		SyncInfo syncInfo = this.getDOM().getSync(id);
		if(syncInfo == null){
			return null;
		}
		
		if(syncInfo.isDeleted()){
			NullContent nullContent = new NullContent(syncInfo.getSyncId());
			return new Item(nullContent, syncInfo.getSync());
		} else {
			Element payload = this.getDOM().getElement(id);
			IContent content = this.getDOM().createContent(payload, id);
			return new Item(content, syncInfo.getSync());		
		}
	}
	
	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
		
		ArrayList<Item> result = new ArrayList<Item>();
		
		List<Element> elements = this.getDOM().getAllElements();
		Map<String, SyncInfo> syncByID = this.convertToMapByID(this.getDOM().getAllSyncs());
		
		for (Element element : elements) {
			String syncID = this.getDOM().getMeshSyncId(element);
			SyncInfo syncInfo = syncByID.get(syncID);
			IContent content = this.getDOM().createContent(this.getDOM().normalize(element), syncID);
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
	
	public void prepareDOMToSync() {
		this.beginSync();
		this.endSync();
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
	
	public IMeshDOM getDOM() {
		return this.domLoader.getDOM();
	}
	
	@Override
	public String getFriendlyName() {		
		return this.domLoader.getFriendlyName();
	}

	public String getMeshSyncId(Element element) {
		return this.getDOM().getMeshSyncId(element);
	}

	// UTILS
	public boolean isValid(Element element) {
		return this.getDOM().isValid(element);
	}
	
	public void clean(){
		this.beginSync();
		this.getDOM().clean();
		this.endSync();
	}
	
	public void purgue(){
		this.beginSync();
		this.getDOM().purgue();
		this.endSync();
	}
}