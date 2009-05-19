package org.mesh4j.sync.filter;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;


public class ConflictsFilterTests {

	@Test
	public void shouldFilterWithoutConflicts(){
		Item item = new Item(null, new Sync("123"));
		
		ConflictsFilter filter = new ConflictsFilter();
		Assert.assertFalse(filter.applies(item));
	}
	
	@Test
	public void shouldFilterItemWithConflicts(){
		Item item = new Item(null, new Sync("123").addConflict(new Item(null, new Sync("jmt"))));
		
		ConflictsFilter filter = new ConflictsFilter();
		Assert.assertTrue(filter.applies(item));
	}
}
