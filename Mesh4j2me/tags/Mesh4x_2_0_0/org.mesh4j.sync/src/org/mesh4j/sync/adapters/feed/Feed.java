package org.mesh4j.sync.adapters.feed;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class Feed {

	// MODEL VARIABLES
	private Vector<Item> feedItems = new Vector<Item>();
	private String payload;
	private String title;
	private String description;
	private String link;
	
	// BUSINES METHODS
	public Feed(){
		super();
		this.payload = "";
		this.title = "";
		this.description = "";
		this.link = "";
	}
	
	public Feed(String title, String description, String link, Vector<Item> items){
		this();
		
		Guard.argumentNotNull(items, "items");
		
		this.title = title;
		this.description = description;
		this.link = link;
		this.feedItems = items;
	}

	public Feed addItem(Item item) {
		this.feedItems.addElement(item);
		return this;
		
	}

	public Feed deleteItem(Item item) {
		this.feedItems.removeElement(item);
		return this;
	}

	public Vector<Item> getItems() {
		return this.feedItems;
	}

	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getLastUpdate() {
		Date date = null;
		for (Item item : this.feedItems) {
			if(item.getLastUpdate() != null && item.getLastUpdate().getWhen() != null){
				if(date == null){
					date = item.getLastUpdate().getWhen();
				} else {
					if(date.getTime() < item.getLastUpdate().getWhen().getTime()){
						date = item.getLastUpdate().getWhen();
					}
				}
			}
		}
		
		if(date ==  null){
			date = new Date();
		}
		return date;
	}

	public void addItems(Vector<Item> items) {
		this.feedItems = items;		
	}

}
