package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.test.utils.TestHelper;

public class HibernateContentAdapterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfSessionBuilderIsNull(){
		new HibernateContentAdapter(null, "user");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfEntityNameIsNull(){
		new HibernateContentAdapter(getBuilder(), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateAdapterFailsIfEntityNameIsEmpty(){
		new HibernateContentAdapter(getBuilder(), "");
	}
	
	@Test
	public void shouldGetType(){
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(), "user");
		Assert.assertEquals("user", adapter.getType());
	}
	
	@Test
	public void shouldAdd() throws DocumentException{
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(), "user");
		
		String id = TestHelper.newID();
		IContent content = makeContent(id, "juan", "123", adapter);
		adapter.save(content);
	}
	
	@Test
	public void shouldGetItem() throws DocumentException{
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(), "user");

		String id = TestHelper.newID();
		IContent content = makeContent(id, "juan", "123", adapter);

		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
				
		Assert.assertTrue(content.equals(contentLoaded));		
	}
	
	@Test
	public void shouldDeleteItem() throws DocumentException{
		
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(), "user");
		
		String id = TestHelper.newID();
		IContent content = makeContent(id, "juan", "123", adapter);
		
		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);		
		
		adapter.delete(contentLoaded);
		
		contentLoaded = adapter.get(id);
		Assert.assertNull(contentLoaded);			
	}
	
	@Test
	public void shouldUpdateItem() throws DocumentException{
		
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(), "user");
		
		String id = TestHelper.newID();
		IContent content = makeContent(id, "juan", "123", adapter);
		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);		
		
		IContent contentUpdated = makeContent(id, "jose", "456", adapter);
		adapter.save(contentUpdated);
		
		contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		Assert.assertFalse(content.equals(contentLoaded));		
		Assert.assertTrue(contentUpdated.equals(contentLoaded));
	}
		
	@Test
	public void shouldGetAll() throws DocumentException{
	
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(), "user");
		
		Date sinceDate = TestHelper.nowSubtractDays(1);
		
		String id0 = TestHelper.newID();
		IContent content0 = makeContent(id0, "juan", "123", adapter);
		adapter.save(content0);
		
		String id1 = TestHelper.newID();
		IContent content1 = makeContent(id1, "marcelo", "456", adapter);
		adapter.save(content1);
		
		List<IContent> results = adapter.getAll(sinceDate);
		Assert.assertNotNull(results);
		
		Assert.assertTrue(containsContent(results, content0));
		Assert.assertTrue(containsContent(results, content1));		
	}

	private boolean containsContent(List<IContent> results, IContent content) {
		for (IContent aContent : results) {
			if(aContent.equals(content)){
				return true;
			}
		}
		return false;
	}
	
	protected IHibernateSessionFactoryBuilder getBuilder() {
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.addMapping(new File(HibernateAdapterTests.class.getResource("User.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		builder.setPropertiesFile(new File(this.getClass().getResource("xx_hibernate.properties").getFile()));
		return builder;
	}
	
	protected IContent makeContent(String id, String name, String pass, HibernateContentAdapter adapter) throws DocumentException {
		Element element = TestHelper.makeElement("<user><id>"+id+"</id><name>"+name+"</name><pass>"+pass+"</pass></user>");
		IContent user = new IdentifiableContent(element, adapter.getMapping(), id);
		return user;
	}
	

}
