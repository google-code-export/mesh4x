package org.mesh4j.sync.adapters.composite;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;

public class IdentifiableSyncAdapter implements IIdentifiableSyncAdapter, ISyncAware{

	// MODEL VARIABLES
	private String type;
	private ISyncAdapter syncAdapter;
	
	// BUSINESS METHODS

	public IdentifiableSyncAdapter(String type, ISyncAdapter syncAdapter) {
		Guard.argumentNotNullOrEmptyString(type, "type");
		Guard.argumentNotNull(syncAdapter, "syncAdapter");
		
		this.syncAdapter = syncAdapter;
		this.type = type;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void add(Item item) {
		this.syncAdapter.add(item);		
	}

	@Override
	public void delete(String id) {
		this.syncAdapter.delete(id);
	}

	@Override
	public Item get(String id) {
		return this.syncAdapter.get(id);
	}

	@Override
	public List<Item> getAll() {
		return this.syncAdapter.getAll();
	}

	@Override
	public List<Item> getAll(IFilter<Item> filter) {
		return this.syncAdapter.getAll(filter);
	}

	@Override
	public List<Item> getAllSince(Date since) {
		return this.syncAdapter.getAllSince(since);
	}

	@Override
	public List<Item> getAllSince(Date since, IFilter<Item> filter) {
		return this.syncAdapter.getAllSince(since, filter);
	}

	@Override
	public List<Item> getConflicts() {
		return this.syncAdapter.getConflicts();
	}

	@Override
	public String getFriendlyName() {
		return this.syncAdapter.getFriendlyName();
	}

	@Override
	public void update(Item item) {
		this.syncAdapter.update(item);
	}

	@Override
	public void update(Item item, boolean resolveConflicts) {
		this.syncAdapter.update(item, resolveConflicts);
	}

	@Override
	public void beginSync() {
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware) this.syncAdapter).beginSync();
		}
	}

	@Override
	public void endSync() {
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware) this.syncAdapter).endSync();
		}		
	}

	public ISyncAdapter getSyncAdapter() {
		return this.syncAdapter;
	}

	@Override
	public ISchema getSchema() {
		return syncAdapter.getSchema();
	}
}
