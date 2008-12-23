package org.mesh4j.sync.adapters.feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class Feed {

	// MODEL VARIABLES
	private List<Item> feedItems;
	private Element payload;
	private String title;
	private String description;
	private String link;
		
	// BUSINES METHODS
	public Feed(){
		super();
		this.payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		this.title = "";
		this.description = "";
		this.link = "";
		this.feedItems = new ArrayList<Item>();
	}
	
	public Feed(String title, String description, String link){
		this();
		this.title = title;
		this.description = description;
		this.link = link;
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
					if(date.before(item.getLastUpdate().getWhen())){
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
}
