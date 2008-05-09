package com.mesh4j.sync.adapters.feed;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.validations.Guard;

public class Feed {

	// MODEL VARIABLES
	private List<Item> feedItems = new ArrayList<Item>();
	private Element payload;
		
	// BUSINES METHODS
	public Feed(){
		super();
	}
	
	public Feed(List<Item> items){
		super();
		Guard.argumentNotNull(items, "items");
		this.feedItems = items;
	}
	public Feed(Item ...items){
		super();
		for (Item item : items) {
			this.addItem(item);
		}
	}
	
	public Feed addItem(Item item) {
		this.feedItems.add(item);
		return this;
		
	}

	public Feed deleteItem(Item item) {
		this.feedItems.remove(item);
		return this;
	}

	public List<Item> getItems() {
		return this.feedItems;
	}

	public Element getPayload() {
		return payload;
	}
	public void setPayload(Element payload) {
		this.payload = payload;		
	}

	public Item getItemBySyncId(String syncId) {
		for (Item item : this.feedItems) {
			if(item.getSyncId().equals(syncId)){
				return item;
			}
		}
		return null;
	}

}
