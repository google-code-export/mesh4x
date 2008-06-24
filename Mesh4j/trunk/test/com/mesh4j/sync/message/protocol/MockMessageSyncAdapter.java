package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.model.Item;

public class MockMessageSyncAdapter implements IMessageSyncAdapter {

	private String sourceId;
	
	public MockMessageSyncAdapter(String sourceID) {
		super();
		this.sourceId = sourceID;
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override public void add(Item item) {}
	@Override public void delete(String id) {}
	@Override public Item get(String id) { return null;}
	@Override public List<Item> getAll()  {return new ArrayList<Item>();}
	@Override public List<Item> getAll(IFilter<Item> filter) {return new ArrayList<Item>();}
	@Override public List<Item> getAllSince(Date since) {return new ArrayList<Item>();}
	@Override public List<Item> getAllSince(Date since, IFilter<Item> filter) {return new ArrayList<Item>();}
	@Override public List<Item> getConflicts() {return new ArrayList<Item>();}
	@Override public String getFriendlyName() {return "mock message adapter";}
	@Override public void update(Item item) {}
	@Override public void update(Item item, boolean resolveConflicts) {}

}
