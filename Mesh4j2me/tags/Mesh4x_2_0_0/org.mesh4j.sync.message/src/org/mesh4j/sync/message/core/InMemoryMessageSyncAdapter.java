package org.mesh4j.sync.message.core;

import java.util.Vector;

import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class InMemoryMessageSyncAdapter implements IMessageSyncAdapter {
	
	// MODEL VARIABLES
	private String sourceId;
	private Vector<Item> items = new Vector<Item>();
	
	// BUSINESS METHODS
	
	public InMemoryMessageSyncAdapter(String sourceId) {
		this(sourceId, new Vector<Item>());
	}
	
	public InMemoryMessageSyncAdapter(String sourceId, Vector<Item> items) {
		
		Guard.argumentNotNullOrEmptyString(sourceId, "sourceId");
		Guard.argumentNotNull(items, "items");
		
		this.sourceId = sourceId;
		this.items = items;
	}

	public Vector<Item> getAll() {
		Vector<Item> result = new Vector<Item>();
		for (Item item : this.items) {
			result.addElement(item);
		}
		return result;
	}

	public String getSourceId() {
		return sourceId;
	}

	public Vector<Item> synchronizeSnapshot(ISyncSession syncSession) {
		Vector<Item> result = new Vector<Item>();
		Vector<Item> conflicts = new Vector<Item>();
		for (Item snapshotItem : syncSession.getSnapshot()) {
			Item item = snapshotItem.clone();
			if(item.hasSyncConflicts()){
				conflicts.addElement(item);
			}
			result.addElement(item);
		}
		this.items = result;
		return conflicts;
	}
	
	public void add(Item item) {
		this.items.addElement(item.clone());
	}

	public void update(Item item) {
		Item itemToUpdate = get(item.getSyncId());
		if(itemToUpdate != null){
			this.items.removeElement(itemToUpdate);
			if(!item.isDeleted()){
				this.items.addElement(item.clone());
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
