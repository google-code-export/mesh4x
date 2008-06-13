package com.mesh4j.sync.message;

import java.util.List;

import com.mesh4j.sync.model.Item;

public interface IDataSet {

	String getDataSetId();
	
	List<Item> getItems();
	
	void add(Item item);

	void update(Item item);

	Item get(String syncID);
}
