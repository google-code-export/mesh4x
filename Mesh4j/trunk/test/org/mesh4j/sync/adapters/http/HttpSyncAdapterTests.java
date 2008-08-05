package org.mesh4j.sync.adapters.http;

import java.net.MalformedURLException;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jaxen.JaxenException;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.IdGenerator;


public class HttpSyncAdapterTests {

	// MODEL VARIABLES
	private HttpSyncAdapter httpAdapter;
	
	// BUSINESS METHODS
	
	@Before
	public void setUp() throws MalformedURLException{
		String path = "http://localhost:7777/feeds/KML";
		this.httpAdapter = new HttpSyncAdapter(path, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
	}
		
	@Test
	public void shouldExecuteGetAll(){
		List<Item> items = this.httpAdapter.getAll();
		Assert.assertEquals(0, items.size());
	}
	
	@Test
	public void shouldExecuteGetAllSince(){
		List<Item> items = this.httpAdapter.getAllSince(TestHelper.now());
		Assert.assertEquals(0, items.size());
	}

	
	@Test
	public void shouldExecuteMerge() throws DocumentException, JaxenException{
		
		String newID = IdGenerator.newID();
		
		String xml ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
		"<rss version=\"2.0\" xmlns:sx=\"http://feedsync.org/2007/feedsync\">"+
		 "<channel> "+			
			"<item>"+
		   "<title>Buy groceries</title>"+
		   "<user><name>jose</name></user>"+
		   "<description>Get milk, eggs, butter and bread</description>"+
		   "<sx:sync id=\""+ newID +"\" updates=\"3\">"+
		    "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		    "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		    "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		   "</sx:sync>"+
		  "</item>"+
		  "</channel>"+
		 "</rss>";
		
		FeedReader reader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = reader.read(DocumentHelper.parseText(xml));
				
		List<Item> result = this.httpAdapter.merge(feed.getItems());
		Assert.assertEquals(0, result.size());
	}
	
	// TODO (JMT) test
	
}
