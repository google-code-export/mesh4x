package com.mesh4j.sync.filter;

import junit.framework.Assert;

import org.junit.Test;

import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.test.utils.TestHelper;

public class CompoundFilterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldValidateNullParameter(){
		new CompoundFilter(new ConflictsFilter[0]); 
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldNotFilterItem(){
		Item item = new Item(null, new Sync("123"));
		
		SinceLastUpdateFilter filter1 = new SinceLastUpdateFilter(TestHelper.now());
		ConflictsFilter filter2 = new ConflictsFilter();
		CompoundFilter filter = new CompoundFilter(filter1, filter2);
		
		Assert.assertFalse(filter.applies(item));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldFilterItem(){
		
		Sync sync = new Sync("123")
			.update("jmt", TestHelper.now(), 1)
			.addConflict(new Item(null, new Sync("dwsfq")));

		Item item = new Item(null, sync); 
		
		SinceLastUpdateFilter filter1 = new SinceLastUpdateFilter(TestHelper.nowSubtractDays(1));
		ConflictsFilter filter2 = new ConflictsFilter();
		CompoundFilter filter = new CompoundFilter(filter1, filter2);
		Assert.assertTrue(filter.applies(item));
	}
}
