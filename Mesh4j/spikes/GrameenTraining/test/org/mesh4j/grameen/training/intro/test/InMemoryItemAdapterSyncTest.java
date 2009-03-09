package org.mesh4j.grameen.training.intro.test;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.grameen.training.intro.adapter.InMemoryItemAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;


public class InMemoryItemAdapterSyncTest {

	@Test
	public void sync(){
		
		//creating content
		String id = "1";
		String title = "test";
		String description = "this is test xml raw data";
		String rawDataAsXML = "<user><id>1</id><name>saiful</name></user>";
		Element payLoad = XMLHelper.parseElement(rawDataAsXML);
		IContent content = new XMLContent(id,title,description,payLoad);
		
		//create sync object
		String syncId = IdGenerator.INSTANCE.newID();
		String by  ="raju";
		Date when = new Date();
		boolean isDeleted = false;
		Sync sync = new Sync(syncId,by,when,isDeleted);
	
		//create new Item object with help of the content and sync instance
		Item item  = new Item(content,sync);
		
		InMemoryItemAdapter sourceAdapter = new InMemoryItemAdapter("source",NullIdentityProvider.INSTANCE);
		sourceAdapter.add(item);
		
		//test cases
		Assert.assertNotNull(sourceAdapter.getAll());
		//end test cases
		
		
		InMemoryItemAdapter targetAdapter = new InMemoryItemAdapter("target",NullIdentityProvider.INSTANCE);
		//sourceAdapter.add(item);
		
		//now sync sourceAdapter and targetAdapter
		SyncEngine syncEngine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> confilicts = syncEngine.synchronize();
		System.out.println("size is:" + confilicts.size());
		
	}
}
