package com.mesh4j.sync.adapters.feed.url;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;
import org.junit.Before;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;


public class URLAdapterTests {

	// MODEL VARIABLES
	private URLFeedAdapter urlAdapter;
	
	// BUSINESS METHODS
	
	@Before
	public void setUp() throws MalformedURLException{
		String path = "http://localhost:7777/feeds/KML";
		this.urlAdapter = new URLFeedAdapter(path, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
	}
		
	@Test
	public void shouldExecuteGetAll(){
		List<Item> items = this.urlAdapter.getAll();
		Assert.assertEquals(0, items.size());
	}
	
	@Test
	public void shouldExecuteGetAllSince(){
		List<Item> items = this.urlAdapter.getAllSince(TestHelper.now());
		Assert.assertEquals(0, items.size());
	}

	
	@Test
	public void shouldExecuteMerge() throws DocumentException, JaxenException{
		
		String newID = IdGenerator.newID();
		
		String xml ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
		"<rss version=\"2.0\" xmlns:sx=\"http://feedsync.org/2007/feedsync\">"+
		 "<channel> "+			
			"<item  xmlns=\"http://feedsync.org/2007/feedsync\">"+
		   "<title>Buy groceries</title>"+
		   "<description>Get milk, eggs, butter and bread</description>"+
		   "<sx:sync id=\""+ newID +"\" updates=\"3\">"+
		    "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
		    "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
		    "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
		   "</sx:sync>"+
		  "</item>"+
		  "</channel>"+
		 "</rss>";
		
		Element payload = selectElements("//sx:sync", DocumentHelper.parseText(xml)).get(0);
		IContent content = new XMLContent(newID, "test", "test", payload);
		Item item = new Item(content, new Sync(newID, "jmt", TestHelper.now(), false));
		
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(item);
		
		List<Item> result = this.urlAdapter.merge(items);
		Assert.assertEquals(0, result.size());
	}
	
	// TODO (JMT) test
	
	@SuppressWarnings("unchecked")
	private List<Element> selectElements(String xpathExpression, Document document) throws JaxenException {
		List<Element> elements = new ArrayList<Element>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("sx", "http://feedsync.org/2007/feedsync");
			
		Dom4jXPath xpath = new Dom4jXPath(xpathExpression);
		xpath.setNamespaceContext(new SimpleNamespaceContext(map));
		  
		elements = xpath.selectNodes(document);
		  
		return elements;
	}
	
}
