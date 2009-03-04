package org.mesh4j.grameen.training.intro.test;

import java.util.Date;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class IntroductionItemTest {

	@Test
	public void shouldCreateItem(){
		
		// Create content data (raw data)
		String xml = "<user><id>1</id><name>jmt</name></user>";
		Element payload = XMLHelper.parseElement(xml);
		System.out.println(payload.asXML());
		
		String id = "1";
		String title = "my raw data";
		String description = "example of feed xml content";
		IContent content = new XMLContent(id, title, description, payload);
		
		// Create sync data
		String syncId = IdGenerator.INSTANCE.newID();
		String by = LoggedInIdentityProvider.getUserName();
		Date when = new Date();
		boolean isDeleted = false;
	
		Sync sync = new Sync(syncId, by, when, isDeleted);
	
		// Create Item
		Item item = new Item(content, sync);
		
		// ASSERTS
		Assert.assertEquals(content, item.getContent());
		Assert.assertEquals(payload, item.getContent().getPayload());
		Assert.assertEquals(xml, item.getContent().getPayload().asXML());
		
		Assert.assertEquals(syncId, item.getSyncId());
		Assert.assertEquals(sync, item.getSync());
		Assert.assertFalse(item.isDeleted());
		Assert.assertFalse(item.hasSyncConflicts());
		
		History history = item.getLastUpdate();
		Assert.assertEquals(by, history.getBy());
		Assert.assertEquals(DateHelper.normalize(when), history.getWhen());
		Assert.assertEquals(1, history.getSequence());		
		
	}
	
	@Test
	public void shouldUpdateItem(){
		
		// Original Item 
		String xml = "<user><id>1</id><name>jmt</name></user>";
		Element payload = XMLHelper.parseElement(xml);
		System.out.println(payload.asXML());
		
		String id = "1";
		String title = "my raw data";
		String description = "example of feed xml content";
		IContent content = new XMLContent(id, title, description, payload);
		
		String syncId = IdGenerator.INSTANCE.newID();
		String by = LoggedInIdentityProvider.getUserName();
		Date when = new Date();
		boolean isDeleted = false;
	
		Sync sync = new Sync(syncId, by, when, isDeleted);
	
		Item item = new Item(content, sync);

		// Update Item (imagine user change the data in the database)
		xml = "<user><id>1</id><name>raju</name></user>";
		payload = XMLHelper.parseElement(xml);
		System.out.println(payload.asXML());
		
		IContent updatedContent = new XMLContent(id, title, description, payload);
		
		by = "raju";
		when = new Date();
		isDeleted = false;
		
		Sync updatedSync = sync.clone();
		updatedSync.update(by, when, isDeleted);
		
		Item updatedItem = new Item(updatedContent, updatedSync);
		
		// ASSERTS
		Assert.assertFalse(item.equals(updatedItem));
		
		Assert.assertEquals(updatedContent, updatedItem.getContent());
		Assert.assertEquals(payload, updatedItem.getContent().getPayload());
		Assert.assertEquals(xml, updatedItem.getContent().getPayload().asXML());
		
		Assert.assertEquals(syncId, updatedItem.getSyncId());
		Assert.assertEquals(updatedSync, updatedItem.getSync());
		Assert.assertFalse(updatedItem.isDeleted());
		Assert.assertFalse(updatedItem.hasSyncConflicts());
		
		History history = updatedItem.getLastUpdate();
		Assert.assertEquals(by, history.getBy());
		Assert.assertEquals(DateHelper.normalize(when), history.getWhen());
		Assert.assertEquals(2, history.getSequence());		
		
	}
	
	@Test
	public void shouldDeleteItem(){
		
		// Original Item 
		String xml = "<user><id>1</id><name>jmt</name></user>";
		Element payload = XMLHelper.parseElement(xml);
		System.out.println(payload.asXML());
		
		String id = "1";
		String title = "my raw data";
		String description = "example of feed xml content";
		IContent content = new XMLContent(id, title, description, payload);
		
		String syncId = IdGenerator.INSTANCE.newID();
		String by = LoggedInIdentityProvider.getUserName();
		Date when = new Date();
		boolean isDeleted = false;
	
		Sync sync = new Sync(syncId, by, when, isDeleted);
	
		Item item = new Item(content, sync);

		// Delete Item (imagine user delete row in the database)
		
		by = "raju";
		when = new Date();
		isDeleted = true;
		
		Sync deletedSync = sync.clone();
		deletedSync.delete(by, when);
		
		NullContent deletedContent = new NullContent(id);         // NullContent because the row was deleted
		Item deletedItem = new Item(deletedContent, deletedSync);
		
		// ASSERTS
		Assert.assertFalse(item.equals(deletedItem));
		
		Assert.assertEquals(deletedContent, deletedItem.getContent());
		
		Assert.assertEquals(syncId, deletedItem.getSyncId());
		Assert.assertEquals(deletedSync, deletedItem.getSync());
		Assert.assertTrue(deletedItem.isDeleted());
		Assert.assertFalse(deletedItem.hasSyncConflicts());
		
		History history = deletedItem.getLastUpdate();
		Assert.assertEquals(by, history.getBy());
		Assert.assertEquals(DateHelper.normalize(when), history.getWhen());
		Assert.assertEquals(2, history.getSequence());		
		
	}
}
