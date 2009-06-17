package org.mesh4j.sync.adapters.composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class CompositeSyncAdapter extends AbstractSyncAdapter implements ISyncAware {

	// MODEL VARIABLES
	private String name;
	private IIdentityProvider identityProvider;
	private ISyncAdapter opaqueAdapter;
	private IIdentifiableSyncAdapter[] adapters;
	
	// BUSINESS METHODS
	
	public CompositeSyncAdapter(String name, ISyncAdapter opaqueAdapter, IIdentityProvider identityProvider, IIdentifiableSyncAdapter... adapters) {
		Guard.argumentNotNullOrEmptyString(name, "name");
		Guard.argumentNotNull(opaqueAdapter, "opaqueAdapter");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.name = name;
		this.identityProvider = identityProvider;
		this.adapters = adapters;
		this.opaqueAdapter = opaqueAdapter;
	}

	@Override
	public void add(Item item) {
		if(item.isDeleted()){
			this.opaqueAdapter.add(item);
		} else {
			IIdentifiableSyncAdapter syncAdapter = this.getAdapter(item);
			if(syncAdapter == null){
				this.opaqueAdapter.add(item);
			} else {
				syncAdapter.add(item);
			}
		}
	}
	
	@Override
	public void update(Item item) {
		if(item.isDeleted()){
			delete(item.getSyncId());
		} else {
			IIdentifiableSyncAdapter syncAdapter = this.getAdapter(item);
			if(syncAdapter == null){
				this.opaqueAdapter.update(item);
			} else {
				syncAdapter.update(item);
			}
		}
	}

	@Override
	public void delete(String id) {
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			// TODO (JMT) improve delete
			syncAdapter.delete(id);
		}
		this.opaqueAdapter.delete(id);
	}

	@Override
	public Item get(String id) {
		Item item;
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			item = syncAdapter.get(id);
			if(item != null){
				return item;
			}
		}
		return this.opaqueAdapter.get(id);
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			result.addAll(syncAdapter.getAllSince(since, filter));
		}
		result.addAll(this.opaqueAdapter.getAllSince(since, filter));
		return result;
	}

	@Override
	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}

	@Override
	public String getFriendlyName() {
		return this.name;
	}

	@Override
	public void beginSync() {
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			if(syncAdapter instanceof ISyncAware){
				((ISyncAware)syncAdapter).beginSync();
			}
		}
		
		if(this.opaqueAdapter instanceof ISyncAware){
			((ISyncAware)this.opaqueAdapter).beginSync();
		}
	}

	@Override
	public void endSync() {
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			if(syncAdapter instanceof ISyncAware){
				((ISyncAware)syncAdapter).endSync();
			}
		}
		
		if(this.opaqueAdapter instanceof ISyncAware){
			((ISyncAware)this.opaqueAdapter).endSync();
		}
	}

	public IIdentifiableSyncAdapter[] getAdapters() {
		return this.adapters;
	}

	private IIdentifiableSyncAdapter getAdapter(Item item) {
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			if(isAdapterForItem(syncAdapter, item)){
				return syncAdapter;
			}
		}		
		return null;
	}

	private boolean isAdapterForItem(IIdentifiableSyncAdapter syncAdapter, Item item) {
		String entityNode = syncAdapter.getType();
	
		if(entityNode.equals(item.getContent().getPayload().getName())){
			return true;
		}else{
			return item.getContent().getPayload().element(entityNode) != null;
		}
	}
}
