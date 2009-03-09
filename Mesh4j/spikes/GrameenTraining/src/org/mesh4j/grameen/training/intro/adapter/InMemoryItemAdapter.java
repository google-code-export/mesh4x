package org.mesh4j.grameen.training.intro.adapter;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
/**
 * 
 * @author Raju
 * @version 1.0,4/3/2009
 */
public class InMemoryItemAdapter extends AbstractSyncAdapter{

	//for storing item in memory
	private Map<String,Item> itemsMap = new LinkedHashMap<String, Item>();
	IIdentityProvider identityProvider = null;
	private String name;

	
	public InMemoryItemAdapter(String name ,IIdentityProvider identityProvider,Map<String,Item> itemsMap){
		Guard.argumentNotNullOrEmptyString(name,"name");
		Guard.argumentNotNull(itemsMap, "itemsMap");
		this.name = name;
		this.identityProvider = identityProvider;
		this.itemsMap  = itemsMap;
	}
	
	public InMemoryItemAdapter(String name,IIdentityProvider identityProvider,Item... items ){
		Guard.argumentNotNullOrEmptyString(name,"name");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(items, "items");
		this.name = name;
		this.identityProvider = identityProvider;
		for(Item item :items){
			itemsMap.put(item.getSyncId(), item);
		}
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
		return this.identityProvider.getAuthenticatedUser();
	}

	@Override
	public String getFriendlyName() {
		return name;
	}

	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");
		if(itemsMap.containsKey(item.getSyncId())){
			itemsMap.put(item.getSyncId(), item);
		}
	}

}
