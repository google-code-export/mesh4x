package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.List;

import junit.framework.Assert;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.utils.XMLHelper;



public class InMemorySplitAdapterTest {
	
	private IContent content1,content2,content3;
	
	@Before
	public void setUp() throws Exception {
		
		String id = "1";
		String title = "Student Info";
		String description = "Student Information(id,name,roll,department)";
		String rawDataAsXML = "<Student>" +
								"<id>" +"1" +"</id>" +
								"<name>" +"raju" +"</name>" +
								"<roll>" +"5004" +"</roll>" +
								"<department>" +"CSE" +"</department>" +
								"</Student>";
		Element payload = XMLHelper.parseElement(rawDataAsXML);
		content1 = new XMLContent(id,title,description,payload);
		
		id = "2";
		title = "Student Info";
		description = "Student Information(id,name,roll,department)";
		rawDataAsXML = "<Student>" +
						"<id>" +"2" +"</id>" +
						"<name>" +"marcelo" +"</name>" +
						"<roll>" +"4004" +"</roll>" +
						"<department>" +"CSE" +"</department>" +
						"</Student>";
		payload = XMLHelper.parseElement(rawDataAsXML);
		content2 = new XMLContent(id,title,description,payload);
		
		
		id = "3";
		title = "Student Info";
		description = "Student Information(id,name,roll,department)";
		rawDataAsXML = "<Student>" +
						"<id>" +"3" +"</id>" +
						"<name>" +"sharif" +"</name>" +
						"<roll>" +"3004" +"</roll>" +
						"<department>" +"CSE" +"</department>" +
						"</Student>";
		payload = XMLHelper.parseElement(rawDataAsXML);
		content3 = new XMLContent(id,title,description,payload);
	}
	
	@Test
	public void ShouldSync(){
		
		Storage storageA = new Storage("1",content1);
		SplitAdapter splitAdapterA = createSplitAdapter(storageA);
		
		
		Storage storageB = new Storage("2",content2);
		SplitAdapter splitAdapterB = createSplitAdapter(storageB);
	
		SyncEngine syncEngine = new SyncEngine(splitAdapterA,splitAdapterB);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertEquals(0, conflicts.size());
	}
	
	@Test
	public void ShouldGetAllItems(){
		
		Storage storageA = new Storage("1",content1);
		SplitAdapter splitAdapterA = createSplitAdapter(storageA);
		
		Assert.assertEquals(1,splitAdapterA.getAll().size());
	
		Storage storageB = new Storage("2",content2);
		SplitAdapter splitAdapterB = createSplitAdapter(storageB);
	
		Assert.assertEquals(1,splitAdapterB.getAll().size());
		
		SyncEngine syncEngine = new SyncEngine(splitAdapterA,splitAdapterB);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
		Assert.assertEquals(2,splitAdapterA.getAll().size());
		Assert.assertEquals(2,splitAdapterB.getAll().size());
		
		
		//now add more to content repository
		splitAdapterA.getContentAdapter().save(content3);
		
		Assert.assertEquals(splitAdapterA.getAll().size(), 3);
		Assert.assertEquals(splitAdapterB.getAll().size(), 2);
		
		conflicts = syncEngine.synchronize();
		Assert.assertEquals(splitAdapterA.getAll().size(), 3);
		Assert.assertEquals(splitAdapterB.getAll().size(), 3);
	}
	@Test
	public void ShouldSaveAndIdentifyNewContent(){
		
		Storage storageA = new Storage("1",content1);
		SplitAdapter splitAdapterA = createSplitAdapter(storageA);
		
		
		Storage storageB = new Storage("2",content2);
		SplitAdapter splitAdapterB = createSplitAdapter(storageB);
	
		SyncEngine syncEngine = new SyncEngine(splitAdapterA,splitAdapterB);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
		Assert.assertEquals(splitAdapterA.getContentAdapter().get("1"),content1);
		Assert.assertEquals(splitAdapterB.getContentAdapter().get("1"),content1);
		
		Assert.assertEquals(splitAdapterA.getContentAdapter().get("2"),content2);
		Assert.assertEquals(splitAdapterB.getContentAdapter().get("2"),content2);
		
		splitAdapterA.getContentAdapter().save(content3);
		conflicts = syncEngine.synchronize();
		
		Assert.assertEquals(0, conflicts.size());
		Assert.assertEquals(splitAdapterA.getContentAdapter().get("3"),content3);
		Assert.assertEquals(splitAdapterB.getContentAdapter().get("3"),content3);
		
	}
	
	@Test
	public void ShouldDeleteItem(){
		
		Storage storageA = new Storage("1",content1);
		SplitAdapter splitAdapterA = createSplitAdapter(storageA);
		
		
		Storage storageB = new Storage("2",content2);
		SplitAdapter splitAdapterB = createSplitAdapter(storageB);
	
		SyncEngine syncEngine = new SyncEngine(splitAdapterA,splitAdapterB);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
		Assert.assertEquals(splitAdapterA.getAll().size(), 2);
		
		List<SyncInfo> listOfSyncInfo = splitAdapterA.getSyncRepository().getAll("Student");
		
		for(SyncInfo syncInfo : listOfSyncInfo){
			splitAdapterA.delete(syncInfo.getId());
		}
		
		Assert.assertEquals(splitAdapterA.getAll().size(), 2);
		
		List<Item> items = splitAdapterA.getAll();
		for (Item item : items) {
			Assert.assertTrue(item.isDeleted());
		}
	}
	
	//creating a splitadapter
	private SplitAdapter createSplitAdapter(Storage storage){
		
		InMemoryContentRepository contentRepo = new InMemoryContentRepository(storage,"InmemorySplitAdapter","Student");
		InMemorySyncRepository syncRepo = new InMemorySyncRepository();
		
		SplitAdapter adapterA = new SplitAdapter(syncRepo,contentRepo,NullIdentityProvider.INSTANCE);
		return adapterA;
	}
}
