package com.mesh4j.sync.adapters.compound;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mesh4j.sync.AbstractRepositoryAdapter;
import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.adapters.SyncInfo;
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

		EntityContent entity = contentAdapter.normalizeContent(item.getContent());
		
		if (!item.isDeleted())
		{
			contentAdapter.save(entity);
		}
		SyncInfo syncInfo = new SyncInfo(item.getSync(), entity);
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
			
			EntityContent content = contentAdapter.get(syncInfo.getEntityId());
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
				EntityContent entity = contentAdapter.get(syncInfo.getEntityId());

				if(entity != null){
					contentAdapter.delete(entity);
				}
				syncRepository.save(syncInfo);
			}
		}
		else
		{
			EntityContent entity = contentAdapter.normalizeContent(item.getContent());
			contentAdapter.save(entity);
			SyncInfo syncInfo = new SyncInfo(item.getSync(), entity);
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
		
		EntityContent entity = contentAdapter.get(syncInfo.getEntityId());
		
		this.updateSync(entity, syncInfo);
		
		if(syncInfo.isDeleted()){
			NullContent nullEntity = new NullContent(syncInfo.getSyncId());
			return new Item(nullEntity, syncInfo.getSync());
		} else {
			return new Item(entity, syncInfo.getSync());			
		}
	}

	private void updateSync(EntityContent entity, SyncInfo syncInfo){
	
		Sync sync = syncInfo.getSync();
		if (entity != null && sync == null)
		{
			// Add sync on-the-fly.
			sync = new Sync(syncInfo.getSyncId(), this.getAuthenticatedUser(), new Date(), false);
			syncInfo.updateSync(sync);
			syncRepository.save(syncInfo);
		}
		else if (entity == null && sync != null)
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
			if (!syncInfo.isDeleted() && syncInfo.contentHasChanged(entity))
			{
				sync.update(this.getAuthenticatedUser(), new Date(), sync.isDeleted());
				syncInfo.setEntityVersion(entity.getEntityVersion());
				syncRepository.save(syncInfo);
			}
		}
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
	
		ArrayList<Item> result = new ArrayList<Item>();
		
		List<EntityContent> entities = contentAdapter.getAll();
		List<SyncInfo> syncInfos = syncRepository.getAll(contentAdapter.getEntityName());
		
		Map<String, SyncInfo> syncInfoAsMapByEntity = this.makeSyncMapByEntity(syncInfos);
 
		for (EntityContent entity : entities) {
			
			SyncInfo syncInfo = syncInfoAsMapByEntity.get(entity.getEntityId());			

			Sync sync;
			if(syncInfo == null){
				sync = new Sync(syncRepository.newSyncID(entity), this.getAuthenticatedUser(), new Date(), false);
				
				SyncInfo newSyncInfo = new SyncInfo(sync, entity);
				
				syncRepository.save(newSyncInfo);
				
			} else {
				sync = syncInfo.getSync();
				updateSync(entity, syncInfo);
				syncInfos.remove(syncInfo);
			}
			Item item = new Item(entity, sync);
			
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
			syncInfoMap.put(syncInfo.getEntityId(), syncInfo);
		}
		return syncInfoMap;
	}

	private boolean appliesFilter(Item item, Date since, IFilter<Item> filter) {
		boolean dateOk = since == null || (item.getSync().getLastUpdate() == null || since.compareTo(item.getSync().getLastUpdate().getWhen()) <= 0);
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
