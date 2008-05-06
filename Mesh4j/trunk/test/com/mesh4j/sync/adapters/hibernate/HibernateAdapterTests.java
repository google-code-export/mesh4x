package com.mesh4j.sync.adapters.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.filter.NullFilter;
import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.test.utils.TestHelper;


public class HibernateAdapterTests {

	private HibernateAdapter repo;
	
	@Before
	public void setUp(){
		if(repo == null ){
			repo = new HibernateAdapter(HibernateAdapterTests.class.getResource("User.hbm.xml").getFile());
		}
		
	}
	
	private Content makeNewUser(String id) throws DocumentException {
		Element element = TestHelper.makeElement("<user><id>"+id+"</id><name>"+id+"</name><pass>123</pass></user>");
		Content user = new EntityContent(element, "user", id);
		return user;
	}
	
	@Test
	public void shouldAddItem() throws DocumentException{
		String id = TestHelper.newID();
		Content content = makeNewUser(id);
		Item item = new Item(content, new Sync(id));
		repo.add(item);
	}
	
	@Test
	public void shouldDeleteAll() throws DocumentException{
		String id = TestHelper.newID();
		Content content = makeNewUser(id);
		Item item = new Item(content, new Sync(id));
		repo.add(item);
		
		Assert.assertTrue(repo.getAll().size() > 0);
		
		repo.deleteAll();
		Assert.assertTrue(repo.getAll().size() == 0);
	}
	
	
	@Test
	public void shouldGetItem() throws DocumentException{
		String id = TestHelper.newID();
		Content content = makeNewUser(id);
System.out.println(content.getPayload().asXML());
		Item item = new Item(content, new Sync(id));
		repo.add(item);
		
		Item itemLoaded = repo.get(id);
		Assert.assertNotNull(itemLoaded);
		
System.out.println(item.getContent().getPayload().asXML());
System.out.println(itemLoaded.getContent().getPayload().asXML());
		
		Assert.assertTrue(item.equals(itemLoaded));		
	}
	
	@Test
	public void shouldDeleteItem() throws DocumentException{
		String id = TestHelper.newID();
		Content content = makeNewUser(id);
		Item item = new Item(content, new Sync(id));
		repo.add(item);
		
		Item itemLoaded = repo.get(id);
		Assert.assertNotNull(itemLoaded);		
		
		repo.delete(id);
		
		itemLoaded = repo.get(id);
		Assert.assertTrue(itemLoaded.getSync().isDeleted());			
	}
	
	@Test
	public void shouldUpdateItem() throws DocumentException{
		String id = TestHelper.newID();
		Content content = makeNewUser(id);
		Item item = new Item(content, new Sync(id));
		repo.add(item);
		
		Item itemLoaded = repo.get(id);
		Assert.assertNotNull(itemLoaded);		
		
		Element payload = item.getContent().getPayload();
		payload.element("pass").clearContent();
		payload.element("pass").addText("555");
		
		item.getSync().update("jmt", new Date());		
		repo.update(item);
		
		itemLoaded = repo.get(id);
		Assert.assertNotNull(itemLoaded);
		Assert.assertTrue(item.equals(itemLoaded));	
	}
	
	@Test
	public void shouldNotSupportMerge(){
		Assert.assertFalse(repo.supportsMerge());		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void shouldNotMerge(){
		List<Item> itemsSource = new ArrayList<Item>();
		Item item = new Item(null, new Sync("132"));
		itemsSource.add(item);
		repo.merge(itemsSource);	
	}

	@Test
	public void shouldGetAll() throws DocumentException{
		Date sinceDate = TestHelper.nowSubtractDays(1);
		Date twoDaysAgo = TestHelper.nowSubtractDays(2);
		Date now = TestHelper.now();
		
		String id0 = TestHelper.newID();
		Content content0 = makeNewUser(id0);
		Item item0 = new Item(content0, new Sync(id0).update("jmt", twoDaysAgo));
		repo.add(item0);
		
		String id1 = TestHelper.newID();
		Content content1 = makeNewUser(id1);
		Item item1 = new Item(content1, new Sync(id1).update("jmt", now));
		repo.add(item1);
		
		List<Item> results = repo.getAll(sinceDate, new NullFilter<Item>());
		Assert.assertNotNull(results);
		
		Assert.assertFalse(containsContent(results, item0.getContent()));
		Assert.assertTrue(containsContent(results, item1.getContent()));		
	}

	private boolean containsContent(List<Item> results, Content content) {
		for (Item item : results) {
			if(item.getContent().equals(content)){
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void shouldReturnFriendlyName() {
		Assert.assertFalse(HibernateAdapter.class.getName() == repo.getFriendlyName());
	}
}