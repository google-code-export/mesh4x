package org.mesh4j.grameen.training.intro.adapter;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

/**
 * 
 * @author Sharif
 * @version 1.0,4/3/2009
 */
public class InMemoryListItemAdapter extends AbstractSyncAdapter {

	// for storing item in memory
	private List<Item> itemList = new ArrayList<Item>();
	IIdentityProvider identityProvider = null;
	private String name;

	public InMemoryListItemAdapter(String name,
			IIdentityProvider identityProvider, List<Item> itemList) {
		Guard.argumentNotNullOrEmptyString(name, "name");
		Guard.argumentNotNull(itemList, "itemList");
		this.name = name;
		this.identityProvider = identityProvider;
		this.itemList = itemList;
	}

	public InMemoryListItemAdapter(String name,
			IIdentityProvider identityProvider, Item... items) {
		Guard.argumentNotNullOrEmptyString(name, "name");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(items, "items");
		this.name = name;
		this.identityProvider = identityProvider;
		for (Item item : items) {
			itemList.add(item);
		}
	}

	@Override
	public void add(Item item) {
		Guard.argumentNotNull(item, "item");

		if ((containsKey(itemList, item.getSyncId())) != -1) {
			throw new IllegalArgumentException();
		}
		itemList.add(item);
	}

	@Override
	public void delete(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");

		for (Item item : itemList) {
			if (item.getSyncId().equals(id)) {
				itemList.remove(item);
				break;
			}
		}

	}

	@Override
	public Item get(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");

		for (Item item : itemList) {
			if (item.getSyncId().equals(id))
				return item;
		}
		return null;
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {

		Guard.argumentNotNull(filter, "filter");
		List<Item> alItems = new ArrayList<Item>();
		for (Item item : itemList) {
			if (SinceLastUpdateFilter.applies(item, since)
					&& filter.applies(item)) {
				alItems.add(item.clone());
			}
		}
		return alItems;
	}

	@Override
	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}

	@Override
	public String getFriendlyName() {
		return name;
	}

	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");
		int itemToUpdateIndex;
		if ((itemToUpdateIndex = containsKey(itemList, item.getSyncId())) != -1) {
			itemList.set(itemToUpdateIndex, item);
		}
	}

	private int containsKey(List<Item> itemList, String syncId) {
		for (Item item : itemList) {
			if (item.getSyncId().equals(syncId))
				itemList.indexOf(item);
		}
		return -1; // -1 means item not found;
	}

}
