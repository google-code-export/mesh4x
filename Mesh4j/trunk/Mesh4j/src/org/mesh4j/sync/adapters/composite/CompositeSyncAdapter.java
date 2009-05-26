package org.mesh4j.sync.adapters.composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
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
		EntityContent entityContent = normalize(item);
		if(entityContent == null){
			this.opaqueAdapter.add(item);
		} else {
			IIdentifiableSyncAdapter syncAdapter = this.getAdapter(entityContent);
			syncAdapter.add(item);
		}
	}
	
	@Override
	public void update(Item item) {
		EntityContent entityContent = normalize(item);
		if(entityContent == null){
			this.opaqueAdapter.update(item);
		} else {
			IIdentifiableSyncAdapter syncAdapter = this.getAdapter(entityContent);
			syncAdapter.update(item);
		}
	}

	@Override
	public void delete(String id) {
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			// TODO improve it
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

	private EntityContent normalize(Item item) {
		EntityContent entityContent;
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			entityContent = EntityContent.normalizeContent(item.getContent(), syncAdapter.getType(), syncAdapter.getIdName());
			if(entityContent != null){
				return entityContent;
			}
		}		
		return null;
	}

	private IIdentifiableSyncAdapter getAdapter(EntityContent entityContent) {
		for (IIdentifiableSyncAdapter syncAdapter : this.adapters) {
			if(syncAdapter.getType().equals(entityContent.getType())){
				return syncAdapter;
			}
		}		
		return null;
	}
	
}
