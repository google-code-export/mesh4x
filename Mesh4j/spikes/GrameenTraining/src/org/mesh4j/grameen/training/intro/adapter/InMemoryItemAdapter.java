package org.mesh4j.grameen.training.intro.adapter;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class InMemoryItemAdapter extends AbstractSyncAdapter{

	//for storing item in memory
	private Map<String,Item> itemsMap = new LinkedHashMap<String, Item>();

	
	public InMemoryItemAdapter(Map<String,Item> itemsMap){
		Guard.argumentNotNull(itemsMap, "itemsMap");
		this.itemsMap  = itemsMap;
		//TODO print the item from the collection
	}
	
	@Override
	public void add(Item item) {
		Guard.argumentNotNull(item, "item");
		if(itemsMap.containsKey(item.getSyncId())){
			throw new IllegalArgumentException();
		}
		itemsMap.put(item.getSync().getId(), item);
	}

	@Override
	public void delete(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");
		itemsMap.remove(id);
	}

	@Override
	public Item get(String id) {
		Guard.argumentNotNullOrEmptyString(id, "id");
		return  itemsMap.get(id);
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthenticatedUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFriendlyName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");
		if(itemsMap.containsKey(item.getSyncId())){
			itemsMap.put(item.getSyncId(), item);
		}
	}

}
