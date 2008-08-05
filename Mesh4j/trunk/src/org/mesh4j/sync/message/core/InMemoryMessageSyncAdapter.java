package org.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class InMemoryMessageSyncAdapter implements IMessageSyncAdapter {
	
	// MODEL VARIABLES
	private String sourceId;
	private List<Item> items = new ArrayList<Item>();
	
	// BUSINESS METHODS
	
	public InMemoryMessageSyncAdapter(String sourceId) {
		this(sourceId, new ArrayList<Item>());
	}
	
	public InMemoryMessageSyncAdapter(String sourceId, List<Item> items) {
		
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNull(items, "items");
		
		this.sourceId = sourceId;
		this.items = items;
	}

	@Override
	public List<Item> getAll() {
		return new ArrayList<Item>(this.items);
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public List<Item> synchronizeSnapshot(ISyncSession syncSession) {
		ArrayList<Item> result = new ArrayList<Item>();
		ArrayList<Item> conflicts = new ArrayList<Item>();
		for (Item snapshotItem : syncSession.getSnapshot()) {
			Item item = snapshotItem.clone();
			if(item.hasSyncConflicts()){
				conflicts.add(item);
			}
			result.add(item);
		}
		this.items = result;
		return conflicts;
	}
	
	public void add(Item item) {
		this.items.add(item.clone());
	}

	public void update(Item item) {
		Item itemToUpdate = get(item.getSyncId());
		if(itemToUpdate != null){
			this.items.remove(itemToUpdate);
			if(!item.isDeleted()){
				this.items.add(item.clone());
			}
		}
	}

	public Item get(String id) {
		for (Item localItem : this.items) {
			if(localItem.getSyncId().equals(id)){
				return localItem;
			}
		}
		return null;
	}
}
