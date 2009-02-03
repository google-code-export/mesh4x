package org.mesh4j.sync.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;


public class InMemorySyncAdapter extends AbstractSyncAdapter {

	// MODEL VARIABLES
	private String name;
	private Hashtable<String, Item> items = new Hashtable<String, Item>();
	private IIdentityProvider identityProvider;

	// BUSINESS METHODS
	
	public InMemorySyncAdapter(String name, IIdentityProvider identityProvider, Item... allItems){
		super();
		Guard.argumentNotNullOrEmptyString(name, "name");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(allItems, "allItems");
		
		this.name = name;
		this.identityProvider = identityProvider;
		
		for (Item item : allItems)
		{
			items.put(item.getSyncId(), item);
		}
	}
	
	public InMemorySyncAdapter(String name, IIdentityProvider identityProvider, List<Item> allItems){
		super();
		Guard.argumentNotNullOrEmptyString(name, "name");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(allItems, "allItems");
		
		this.name = name;
		this.identityProvider = identityProvider;
		
		for (Item item : allItems)
		{
			items.put(item.getSyncId(), item);
		}
	}

	public String getFriendlyName()
	{
		return name;
	}

	public void add(Item item)
	{
		Guard.argumentNotNull(item, "item");

		if (items.containsKey(item.getSyncId())){
			throw new IllegalArgumentException();
		}

		items.put(item.getSyncId(), item.clone());
	}

	public Item get(String id)
	{
		Guard.argumentNotNullOrEmptyString(id, "id");

		if (items.containsKey(id)){
			return items.get(id).clone();
		}else{
			return null;
		}
	}

	protected List<Item> getAll(Date since, IFilter<Item> filter)
	{
		Guard.argumentNotNull(filter, "filter");

		ArrayList<Item> allItems = new ArrayList<Item>();
		for(Item item : items.values()){
			if (SinceLastUpdateFilter.applies(item, since) && filter.applies(item)){
				allItems.add(item.clone());
			}
		}
		return allItems;
	}

	public void delete(String id){
		Guard.argumentNotNullOrEmptyString(id, "id");

		items.remove(id);
	}

	public void update(Item item){
		Guard.argumentNotNull(item, "item");

		Item i;
		if (item.getSync().isDeleted()){
			i = new Item(new NullContent(item.getSyncId()), item.getSync().clone());
		}else{
			i = item.clone();
		}

		items.put(item.getSyncId(), i);
	}

	public Hashtable<String, Item> getItems() {
		return items;
	}

	@Override
	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}

}
