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
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class InMemoryItemAdapterTest {
	
	@Test
	public void createItem(){
		String rawDataAsXml = "<user><id>1</id><name>saiful</name></user>";
		//creating the element from the raw xml data
		Element element = XMLHelper.parseElement(rawDataAsXml);
		
		//create content object
		IContent content = new XMLContent("1","test","sample xml for test",element);
		
		//create sync object
		String syncId = IdGenerator.INSTANCE.newID();
		boolean isDeleted = false;
		String updateBy = "raju";
		Date when = new Date();
		Sync sync = new Sync(syncId,updateBy,when,true);
		
		Item item = new Item(content, sync);
		
		//now we are going to test our item object with junit assert
		Assert.assertEquals(content,item.getContent());
		Assert.assertEquals(element, item.getContent().getPayload());
		Assert.assertEquals(syncId, item.getSync().getId());
		Assert.assertEquals(rawDataAsXml, item.getContent().getPayload().asXML());
		Assert.assertEquals(sync,item.getSync());
		
		Assert.assertFalse(isDeleted);
		Assert.assertFalse(item.hasSyncConflicts());
		
		//history checking
		Assert.assertEquals(item.getSync().getLastUpdate().getBy(), updateBy);
		Assert.assertEquals(item.getSync().getLastUpdate().getWhen(), DateHelper.normalize(when));
		//not understand what is sequence
		Assert.assertEquals(1, item.getSync().getLastUpdate().getSequence());
		
		
	}
	
	@Test
	public void deleteItem(){
		
		String rawDataAsXml = "<user><id>1</id><name>saiful</name></user>";
		//creating the element from the raw xml data
		Element element = XMLHelper.parseElement(rawDataAsXml);
		
		//create content object
		String itemId = "1";
		IContent content = new XMLContent(itemId,"test","sample xml for test",element);
		
		//create sync object
		String syncId = IdGenerator.INSTANCE.newID();
		boolean isDeleted = false;
		String updateBy = "raju";
		Date when = new Date();
		Sync sync = new Sync(syncId,updateBy,when,isDeleted);
		
		Item item = new Item(content, sync);
		
		//now delte item. delte item is actually creating a null item and populate
		//it with sync information.
		String deletedBy = "javed";
		Date deletedWhen = new Date();
		isDeleted = true;
		
		Sync deleteSync = sync.clone();
		deleteSync.delete(deletedBy, deletedWhen);
		
		//we create a NullContent object to put delete command to the Item object
		NullContent nullContent = new NullContent(itemId);
		Item deletedItem = new Item(nullContent,deleteSync);
		
		//Junit test
		Assert.assertFalse(item.equals(deletedItem));
		Assert.assertEquals(nullContent,deletedItem.getContent());
		Assert.assertNotSame(item, deletedItem.getContent());
		Assert.assertEquals(deletedItem.getSync(), deleteSync);
		Assert.assertEquals(syncId, deletedItem.getSyncId());
		Assert.assertTrue(deletedItem.isDeleted());
		
		Assert.assertFalse(deletedItem.hasSyncConflicts());
		
		//history
		History deleteHistory = deletedItem.getLastUpdate();
		Assert.assertEquals(deletedBy, deleteHistory.getBy());
		Assert.assertEquals(DateHelper.normalize(deletedWhen), deleteHistory.getWhen());
		Assert.assertEquals(2,deleteHistory.getSequence());
	}
	
	@Test
	public void updateItem(){
		String rawDataAsXml = "<user><id>1</id><name>saiful</name></user>";
		//creating the element from the raw xml data
		Element element = XMLHelper.parseElement(rawDataAsXml);
		
		//create content object
		String itemId = "1";
		String title = "test";
		String description = "sample xml for test";
		IContent content = new XMLContent(itemId,title,description,element);
		
		//create sync object
		String syncId = IdGenerator.INSTANCE.newID();
		boolean isDeleted = false;
		String updateBy = "raju";
		Date when = new Date();
		Sync sync = new Sync(syncId,updateBy,when,isDeleted);
		
		Item item = new Item(content, sync);
		
		//create update item
		String updatedRawDataAsXml = "<user><id>1</id><name>javed</name></user>";
		//creating the element from the raw xml data
		Element updatedElement = XMLHelper.parseElement(updatedRawDataAsXml);
		
		//create content object
		IContent updatedContent = new XMLContent(itemId,title,description,updatedElement);
		
		//create sync object
		isDeleted = false;
		updateBy = "javed";
		when = new Date();
		Sync updatedSync = sync.clone();
		updatedSync.update(updateBy , when);
		
		Item updatedItem = new Item(updatedContent, updatedSync);
		
		Assert.assertFalse(item.equals(updatedItem));
		Assert.assertEquals(syncId, updatedItem.getSyncId());
		Assert.assertEquals(updatedSync, updatedItem.getSync());
		
		Assert.assertEquals(updatedContent, updatedItem.getContent());
		Assert.assertEquals(updatedElement, updatedItem.getContent().getPayload());
		Assert.assertEquals(updatedRawDataAsXml, updatedItem.getContent().getPayload().asXML());
		Assert.assertFalse(updatedItem.hasSyncConflicts());
		
		//History
		History history = updatedItem.getLastUpdate();
		Assert.assertEquals(updateBy, history.getBy());
		Assert.assertEquals(DateHelper.normalize(when), history.getWhen());
		Assert.assertEquals(2, history.getSequence());
		
	}
	
}
