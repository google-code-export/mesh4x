package org.mesh4j.sync.adapters;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

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
	public InMemorySyncAdapter(String name, IIdentityProvider identityProvider, Vector<Item> allItems){
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

	protected Vector<Item> getAll(Date since, IFilter<Item> filter)
	{
		Guard.argumentNotNull(filter, "filter");

		Vector<Item> allItems = new Vector<Item>();

		Enumeration keys = this.items.keys();
		while(keys.hasMoreElements()) {
			Item item = this.items.get(keys.nextElement());

			if (SinceLastUpdateFilter.applies(item, since) && filter.applies(item)){
				allItems.addElement(item.clone());
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

	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}

}
