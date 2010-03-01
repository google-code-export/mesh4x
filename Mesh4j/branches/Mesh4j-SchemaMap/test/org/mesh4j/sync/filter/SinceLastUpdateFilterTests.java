package org.mesh4j.sync.filter;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;


public class SinceLastUpdateFilterTests {
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldValidateNullParameter(){
		new SinceLastUpdateFilter(null); 
	}
	
	@Test
	public void shouldFilterItemWithoutHistory(){
		Item item = new Item(null, new Sync("123"));
		
		SinceLastUpdateFilter filter = new SinceLastUpdateFilter(TestHelper.now());
		Assert.assertTrue(filter.applies(item));
	}
	
	@Test
	public void shouldFilterItemWithoutHistoryWhen(){
		Item item = new Item(null, new Sync("123").update("jmt", null, 1));
		
		SinceLastUpdateFilter filter = new SinceLastUpdateFilter(TestHelper.now());
		Assert.assertTrue(filter.applies(item));
	}
	
	@Test
	public void shouldFilterItem(){
		Item item = new Item(null, new Sync("123").update("jmt", TestHelper.now(), 1));
		
		SinceLastUpdateFilter filter = new SinceLastUpdateFilter(TestHelper.nowSubtractDays(1));
		Assert.assertTrue(filter.applies(item));
	}

	@Test
	public void shouldNotFilterItem(){
		Item item = new Item(null, new Sync("123").update("jmt", TestHelper.nowSubtractDays(2), 1));
		
		SinceLastUpdateFilter filter = new SinceLastUpdateFilter(TestHelper.now());
		Assert.assertFalse(filter.applies(item));
	}
	
}
