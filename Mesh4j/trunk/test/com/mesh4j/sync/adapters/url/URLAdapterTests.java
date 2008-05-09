package com.mesh4j.sync.adapters.url;

import java.net.MalformedURLException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.TestHelper;


public class URLAdapterTests {

	// MODEL VARIABLES
	private URLAdapter urlAdapter;
	
	// BUSINESS METHODS
	
	@Before
	public void setUp() throws MalformedURLException{
		String path = "http://pci-mobile:7777/feeds/MockFeed";
		this.urlAdapter = new URLAdapter(path, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
	}
		
	//@Test
	public void shouldExecuteGetAll(){
		List<Item> items = this.urlAdapter.getAll();
		Assert.assertEquals(1, items.size());
	}
	
	@Test
	public void shouldExecuteMerge(){
		List<Item> items = this.urlAdapter.getAll();
		Assert.assertEquals(1, items.size());
		
		Item item = items.get(0);
		item.getContent().getPayload().setText("CIBRAXXXX");
		item.getSync().update("jmt", TestHelper.now());
		
		List<Item> result = this.urlAdapter.merge(items);
		Assert.assertEquals(0, items.size());
	}
	
	// TODO (JMT) test
	
}
