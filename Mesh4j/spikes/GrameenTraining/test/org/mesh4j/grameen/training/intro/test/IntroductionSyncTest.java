package org.mesh4j.grameen.training.intro.test;

import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;

public class IntroductionSyncTest {

	@Test
	public void shouldSync(){
		
		// CREATE SOURCE ENDPOINT
		String syncId = IdGenerator.INSTANCE.newID();
		Item item = makeNewItem(syncId);
		
		InMemorySyncAdapter source = new InMemorySyncAdapter("source", NullIdentityProvider.INSTANCE);
		source.add(item);
			Assert.assertNotNull(source.getAll());
			Assert.assertEquals(1, source.getAll().size());
			
		Item sourceItem = source.get(syncId);
			Assert.assertNotNull(sourceItem);
			Assert.assertEquals(item, sourceItem);

			
		// CREATE TARGET ENDPOINT
		InMemorySyncAdapter target = new InMemorySyncAdapter("target", NullIdentityProvider.INSTANCE);
		
			Assert.assertNotNull(target.getAll());
			Assert.assertEquals(0, target.getAll().size());
		
		// SYNCHRONIZE 
		SyncEngine syncEngine = new SyncEngine(source, target);
		List<Item> conflicts = syncEngine.synchronize();
		
		// ASSERTS
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		Assert.assertNotNull(source.getAll());
		Assert.assertEquals(1, source.getAll().size());
		
		Assert.assertNotNull(target.getAll());
		Assert.assertEquals(1, target.getAll().size());

		sourceItem = source.get(syncId);
		Item targetItem = target.get(syncId);
		
		Assert.assertNotNull(sourceItem);
		Assert.assertNotNull(targetItem);
		Assert.assertEquals(sourceItem, targetItem);
	}

	private Item makeNewItem(String syncId) {
		String xml = "<user><id>"+syncId+"</id><name>"+syncId+"</name></user>";
		Element payload = XMLHelper.parseElement(xml);
		System.out.println(payload.asXML());
		
		String title = "my raw data";
		String description = "example of feed xml content";
		IContent content = new XMLContent(syncId, title, description, payload);
		
		String by = LoggedInIdentityProvider.getUserName();
		Date when = new Date();
		boolean isDeleted = false;
	
		Sync sync = new Sync(syncId, by, when, isDeleted);
	
		Item item = new Item(content, sync);
		return item;
	}
}
