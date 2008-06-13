package com.mesh4j.sync.message.dataset;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IDataSet;
import com.mesh4j.sync.model.Item;

public class InMemoryDataSet implements IDataSet {

	// MODEL VARIABLES
	private String dataSetId;
	private ArrayList<Item> items = new ArrayList<Item>();
	
	// METHODS
	public InMemoryDataSet(String dataSetId, ArrayList<Item> items) {
		this.dataSetId = dataSetId;
		this.items = items;
	}

	@Override
	public void add(Item item) {
		items.add(item);			
	}

	@Override
	public String getDataSetId() {
		return dataSetId;
	}

	@Override
	public List<Item> getItems() {
		return items;
	}

	@Override
	public void notifyConflict(Item item) {
		// TODO (JMT) MeshSMS: conflicts?
		
	}

	@Override
	public void update(Item item) {
		Item itemToUpdate = getItem(item.getSyncId());
		if(itemToUpdate != null){
			this.items.remove(itemToUpdate);
			if(!item.isDeleted()){
				this.items.add(item);
			}
		}
	}

	private Item getItem(String syncId) {
		for (Item localItem : this.items) {
			if(localItem.getSyncId().equals(syncId)){
				return localItem;
			}
		}
		return null;
	}

}
