package org.mesh4j.sync.message.protocol;

import java.util.Vector;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;

public class NoContainsItemFilter implements IFilter<Item> {

	// MODEL VARIABLES
	private Vector<String> syncIds;
	
	// BUSINESS METHODS
	public NoContainsItemFilter(Vector<String> syncIds) {
		super();
		this.syncIds = syncIds;
	}

	public boolean applies(Item item) {
		return !syncIds.contains(item.getSyncId());
	}

}
