package com.mesh4j.sync.adapters.compound;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mesh4j.sync.AbstractRepositoryAdapter;
import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.adapters.IIdentifiableContent;
import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.filter.SinceLastUpdateFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.validations.Guard;

public class CompoundRepositoryAdapter extends AbstractRepositoryAdapter {

	// MODEL VARIABLES
	private ISyncRepository syncRepository;
	private IContentAdapter contentAdapter;
	private ISecurity security;
	
	// BUSINESS METHODS
	public CompoundRepositoryAdapter(ISyncRepository syncRepository,
			IContentAdapter contentRepository, ISecurity security) {
		
		Guard.argumentNotNull(contentRepository, "contentRepo");
		Guard.argumentNotNull(syncRepository, "syncRepo");
		Guard.argumentNotNull(security, "security");
		
		this.syncRepository = syncRepository;
		this.contentAdapter = contentRepository;
		this.security = security;
	}
	
	@Override
	public void add(Item item) {
		
		Guard.argumentNotNull(item, "item");

		SyncInfo syncInfo = null;
		if (!item.isDeleted())
		{
			IIdentifiableContent content = contentAdapter.normalizeContent(item.getContent());
			contentAdapter.save(content);
			syncInfo = new SyncInfo(item.getSync(), content);
		} else {
			syncInfo = new SyncInfo(item.getSync(), contentAdapter.getType(), item.getContent().getId(), item.getContent().getPayload().asXML().hashCode());	
		}		
		syncRepository.save(syncInfo);
	}

	@Override
	public void delete(String syncId) {
		
		Guard.argumentNotNullOrEmptyString(syncId, "id");

		SyncInfo syncInfo = syncRepository.get(syncId);
		
		if (syncInfo != null)
		{
			syncInfo.getSync().delete(this.getAuthenticatedUser(), new Date());
			syncRepository.save(syncInfo);
			
			IIdentifiableContent content = contentAdapter.get(syncInfo.getId());
			if(content != null){
				contentAdapter.delete(content);
			}
		}
	}
	
	@Override
	public void update(Item item) {
		
		Guard.argumentNotNull(item, "item");
		
		if (item.isDeleted())
		{
			SyncInfo syncInfo = syncRepository.get(item.getSyncId());
			if(syncInfo != null){
				syncInfo.updateSync(item.getSync());
				IIdentifiableContent content = contentAdapter.get(syncInfo.getId());

				if(content != null){
					contentAdapter.delete(content);
				}
				syncRepository.save(syncInfo);
			}
		}
		else
		{
			IIdentifiableContent content = contentAdapter.normalizeContent(item.getContent());
			contentAdapter.save(content);
			SyncInfo syncInfo = new SyncInfo(item.getSync(), content);
			syncRepository.save(syncInfo);	
		}
	}

	@Override
	public Item get(String syncId) {
		
		Guard.argumentNotNullOrEmptyString(syncId, "id");

		SyncInfo syncInfo = syncRepository.get(syncId);
		
		if(syncInfo == null){
			return null;
		}
		
		IIdentifiableContent content = contentAdapter.get(syncInfo.getId());
		
		this.updateSync(content, syncInfo);
		
		if(syncInfo.isDeleted()){
			NullContent nullContent = new NullContent(syncInfo.getSyncId());
			return new Item(nullContent, syncInfo.getSync());
		} else {
			return new Item(content, syncInfo.getSync());			
		}
	}

	private void updateSync(IIdentifiableContent content, SyncInfo syncInfo){
	
		Sync sync = syncInfo.getSync();
		if (content != null && sync == null)
		{
			// Add sync on-the-fly.
			sync = new Sync(syncInfo.getSyncId(), this.getAuthenticatedUser(), new Date(), false);
			syncInfo.updateSync(sync);
			syncRepository.save(syncInfo);
		}
		else if (content == null && sync != null)
		{
			if (!sync.isDeleted())
			{
				sync.delete(this.getAuthenticatedUser(), new Date());
				syncRepository.save(syncInfo);
			}
		}
		else
		{
			/// Ensures the Sync information is current WRT the 
			/// item actual data. If it's not, a new 
			/// update will be added. Used when exporting/retrieving 
			/// items from the local stores.
			if (!syncInfo.isDeleted() && syncInfo.contentHasChanged(content))
			{
				sync.update(this.getAuthenticatedUser(), new Date(), sync.isDeleted());
				syncInfo.setVersion(content.getVersion());
				syncRepository.save(syncInfo);
			}
		}
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
	
		ArrayList<Item> result = new ArrayList<Item>();
		
		List<IIdentifiableContent> contents = contentAdapter.getAll();
		List<SyncInfo> syncInfos = syncRepository.getAll(contentAdapter.getType());
		
		Map<String, SyncInfo> syncInfoAsMapByEntity = this.makeSyncMapByEntity(syncInfos);
 
		for (IIdentifiableContent content : contents) {
			
			SyncInfo syncInfo = syncInfoAsMapByEntity.get(content.getId());			

			Sync sync;
			if(syncInfo == null){
				sync = new Sync(syncRepository.newSyncID(content), this.getAuthenticatedUser(), new Date(), false);
				
				SyncInfo newSyncInfo = new SyncInfo(sync, content);
				
				syncRepository.save(newSyncInfo);
				
			} else {
				sync = syncInfo.getSync();
				updateSync(content, syncInfo);
				syncInfos.remove(syncInfo);
			}
			Item item = new Item(content, sync);
			
			if(appliesFilter(item, since, filter)){
				result.add(item);
			}

		}

		for (SyncInfo syncInfo : syncInfos) {
			updateSync(null, syncInfo);
			Item item = new Item(
				new NullContent(syncInfo.getSync().getId()),
				syncInfo.getSync());
			
			if(appliesFilter(item, since, filter)){
				result.add(item);
			}
		}
		return result;
	}

	private Map<String, SyncInfo> makeSyncMapByEntity(List<SyncInfo> syncInfos) {
		HashMap<String, SyncInfo> syncInfoMap = new HashMap<String, SyncInfo>();
		for (SyncInfo syncInfo : syncInfos) {
			syncInfoMap.put(syncInfo.getId(), syncInfo);
		}
		return syncInfoMap;
	}

	private boolean appliesFilter(Item item, Date since, IFilter<Item> filter) {
		boolean dateOk = SinceLastUpdateFilter.applies(item, since);
		return filter.applies(item) && dateOk;
	}

	@Override
	public String getFriendlyName() {		
		return MessageTranslator.translate(CompoundRepositoryAdapter.class.getName());
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

}
