package com.mesh4j.sync.adapters.feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.AbstractRepositoryAdapter;
import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.filter.SinceLastUpdateFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.validations.Guard;

// TODO (JMT) incremental loading for feed items, xml pull parser?
public class FeedAdapter extends AbstractRepositoryAdapter{

	// MODEL VARIABLES
	private Feed feed;
	private ISecurity security;
	
	// BUSINESS METHODS
	public FeedAdapter(ISecurity security){
		this(new Feed(), security);
	}
	
	public FeedAdapter(Feed feed, ISecurity security){
		
		Guard.argumentNotNull(feed, "feed");
		Guard.argumentNotNull(security, "security");
		
		this.feed = feed;
		this.security = security;
	}
	
	@Override
	public void add(Item item) {
		this.feed.addItem(item);		
	}

	@Override
	public void delete(String id) {
		Item item = this.get(id);
		if(item != null){
			this.feed.deleteItem(item);
		}		
	}

	@Override
	public Item get(String id) {
		for (Item item : this.feed.getItems()) {
			if(item.getSyncId().equals(id)){
				return item;
			}
		}
		return null;
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (Item item : this.feed.getItems()) {
			boolean dateOk = SinceLastUpdateFilter.applies(item, since);
			if(filter.applies(item) && dateOk){
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public String getFriendlyName() {
		return MessageTranslator.translate(this.getClass().getName());
	}
	
	@Override
	public boolean supportsMerge() {
		return false;
	}

	@Override
	public List<Item> merge(List<Item> items) {
		// Nothing to do, see FeedAdapter.supportsMerge()
		return items;
	}

	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");

		Item originalItem = get(item.getSyncId());
		if(originalItem != null){
			this.feed.deleteItem(originalItem);
		
			Item newItem;
			if (item.getSync().isDeleted()){
				newItem = new Item(new NullContent(item.getSyncId()), item.getSync().clone());
			}else{
				newItem = item.clone();
			}
			
			this.feed.addItem(newItem);
		}
	}
	
	public Feed getFeed(){
		return this.feed;
	}

	@Override
	public String getAuthenticatedUser() {
		return this.security.getAuthenticatedUser();
	}

}
