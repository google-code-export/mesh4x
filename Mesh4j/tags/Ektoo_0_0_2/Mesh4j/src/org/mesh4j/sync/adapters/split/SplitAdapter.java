package org.mesh4j.sync.adapters.split;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.mesh4j.sync.translator.MessageTranslator;
import org.mesh4j.sync.validations.Guard;


public class SplitAdapter extends AbstractSyncAdapter implements ISyncAware{

	// MODEL VARIABLES
	private ISyncRepository syncRepository;
	private IContentAdapter contentAdapter;
	private IIdentityProvider identityProvider;
	
	// BUSINESS METHODS
	
	public SplitAdapter(ISyncRepository syncRepository,
			IContentAdapter contentRepository, IIdentityProvider identityProvider) {
		
		Guard.argumentNotNull(contentRepository, "contentRepo");
		Guard.argumentNotNull(syncRepository, "syncRepo");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.syncRepository = syncRepository;
		this.contentAdapter = contentRepository;
		this.identityProvider = identityProvider;
	}
	
	@Override
	public void add(Item item) {
		
		Guard.argumentNotNull(item, "item");
		
		IContent content = item.getContent();
		if (!item.isDeleted()){
			if(contentAdapter instanceof ISyncEntityRelationListener){
				((ISyncEntityRelationListener) contentAdapter).notifyNewSyncForContent(item.getSyncId(), content);
			}
			contentAdapter.save(content);
		}
		
		String id;
		if(contentAdapter instanceof IIdentifiableContentAdapter){
			id = ((IIdentifiableContentAdapter)contentAdapter).getID(content);
			if(id == null){
				id = content.getId();
			}
		} else {
			id = content.getId();
		}
		
		SyncInfo syncInfo = new SyncInfo(item.getSync(), contentAdapter.getType(), id, content.getVersion());
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
			
			IContent content = contentAdapter.get(syncInfo.getId());
			if(content != null){
				contentAdapter.delete(content);
			}
		}
	}
	
	@Override
	public void update(Item item) {
		
		Guard.argumentNotNull(item, "item");
		
		if (item.isDeleted()){
			SyncInfo syncInfo = syncRepository.get(item.getSyncId());
			if(syncInfo != null){
				syncInfo.updateSync(item.getSync());
				IContent content = contentAdapter.get(syncInfo.getId());

				if(content != null){
					contentAdapter.delete(content);
				}
				syncRepository.save(syncInfo);
			}
		}else{
			IContent content = item.getContent();
			contentAdapter.save(content);
			
			String id;
			if(contentAdapter instanceof IIdentifiableContentAdapter){
				id = ((IIdentifiableContentAdapter)contentAdapter).getID(content);
				if(id == null){
					id = content.getId();
				}
			} else {
				id = content.getId();
			}
			
			SyncInfo syncInfo = new SyncInfo(item.getSync(), contentAdapter.getType(), id, content.getVersion());
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
		
		IContent content = contentAdapter.get(syncInfo.getId());
		
		this.updateSyncIfChanged(content, syncInfo);
		
		if(syncInfo.isDeleted()){
			NullContent nullContent = new NullContent(syncInfo.getSyncId());
			return new Item(nullContent, syncInfo.getSync());
		} else {
			return new Item(content, syncInfo.getSync());			
		}
	}

	private void updateSyncIfChanged(IContent content, SyncInfo syncInfo){
	
		Sync sync = syncInfo.getSync();
		if (content != null && sync == null)
		{
			// Add sync on-the-fly.
			sync = new Sync(syncInfo.getSyncId(), this.getAuthenticatedUser(), new Date(), false);
			
			if(contentAdapter instanceof ISyncEntityRelationListener){
				((ISyncEntityRelationListener) contentAdapter).notifyNewSyncForContent(syncInfo.getSyncId(), content);
			}
			
			syncInfo.updateSync(sync);
			syncRepository.save(syncInfo);
		}
		else if (content == null && sync != null)
		{
			if (!sync.isDeleted())
			{
				sync.delete(this.getAuthenticatedUser(), new Date());
				syncRepository.save(syncInfo);
				
				if(contentAdapter instanceof ISyncEntityRelationListener){
					((ISyncEntityRelationListener) contentAdapter).notifyRemoveSync(syncInfo.getSyncId());
				}
			}
		}
		else
		{
			/// Ensures the Sync information is current WRT the 
			/// item actual data. If it's not, a new 
			/// update will be added. Used when exporting/retrieving 
			/// items from the local stores.
			//if (!syncInfo.isDeleted() && syncInfo.contentHasChanged(content))
			if ((!syncInfo.isDeleted() && syncInfo.contentHasChanged(content)) || syncInfo.isDeleted())
			{
				sync.update(this.getAuthenticatedUser(), new Date(), false);
				syncInfo.setVersion(content.getVersion());
				syncRepository.save(syncInfo);
			}
		}
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
	
		ArrayList<Item> result = new ArrayList<Item>();
		
		List<IContent> contents = contentAdapter.getAll(since);
		List<SyncInfo> syncInfos = syncRepository.getAll(contentAdapter.getType());
		
		Map<String, SyncInfo> syncInfoAsMapByEntity = this.makeSyncMapByEntity(syncInfos);
 
		for (IContent content : contents) {
			
			SyncInfo syncInfo = syncInfoAsMapByEntity.get(content.getId());			

			Sync sync;
			if(syncInfo == null){
				sync = new Sync(syncRepository.newSyncID(content), this.getAuthenticatedUser(), new Date(), false);
				
				if(contentAdapter instanceof ISyncEntityRelationListener){
					((ISyncEntityRelationListener)contentAdapter).notifyNewSyncForContent(sync.getId(), content);
				}
				
				SyncInfo newSyncInfo = new SyncInfo(sync, contentAdapter.getType(), content.getId(), content.getVersion());
				
				syncRepository.save(newSyncInfo);
				
			} else {
				sync = syncInfo.getSync();
				updateSyncIfChanged(content, syncInfo);
				syncInfos.remove(syncInfo);
			}
			Item item = new Item(content, sync);
			
			if(appliesFilter(item, since, filter)){
				result.add(item);
			}

		}

		Item item;
		IContent content;
		for (SyncInfo syncInfo : syncInfos) {
			content = contentAdapter.get(syncInfo.getId());
			updateSyncIfChanged(content, syncInfo);
			if(syncInfo.isDeleted()){
				item = new Item(
						new NullContent(syncInfo.getSyncId()),
						syncInfo.getSync());
			} else {
				item = new Item(content, syncInfo.getSync());
			}
			
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
		return MessageTranslator.translate(SplitAdapter.class.getName());
	}

	@Override
	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}
	
	@Override
	public void beginSync(){
		if(this.contentAdapter instanceof ISyncAware){
			((ISyncAware)this.contentAdapter).beginSync();
		}
		
		if(this.syncRepository instanceof ISyncAware){
			((ISyncAware)this.syncRepository).beginSync();
		}
	}

	@Override
	public void endSync(){
		if(this.contentAdapter instanceof ISyncAware){
			((ISyncAware)this.contentAdapter).endSync();
		}
		
		if(this.syncRepository instanceof ISyncAware){
			((ISyncAware)this.syncRepository).endSync();
		}		
	}

	public IContentAdapter getContentAdapter() {
		return this.contentAdapter;
	}
	
	public ISyncRepository getSyncRepository(){
		return this.syncRepository;
	}
}
