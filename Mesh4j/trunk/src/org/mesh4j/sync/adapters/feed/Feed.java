package org.mesh4j.sync.adapters.feed;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class Feed {

	// MODEL VARIABLES
	private List<Item> feedItems = new ArrayList<Item>();
	private Element payload;
		
	// BUSINES METHODS
	public Feed(){
		super();
		this.payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
	}
	
	public Feed(String title, String description, String link, ISyndicationFormat syndicationFormat){
		super();
		this.payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		syndicationFormat.addFeedInformation(this.payload, title, description, link);
	}
	
	public Feed(List<Item> items){
		this();
		Guard.argumentNotNull(items, "items");
		this.feedItems = items;
	}
	
	public Feed(Item ...items){
		this();

		for (Item item : items) {
			this.addItem(item);
		}
	}
	
	public Feed addItems(List<Item> items) {
		for (Item item : items) {
			this.addItem(item);
		}
		return this;
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

	public boolean isEmpty() {
		return this.feedItems.isEmpty();
	}

	public Feed deleteAllItems() {
		this.feedItems = new ArrayList<Item>();
		return this;		
	}

}
