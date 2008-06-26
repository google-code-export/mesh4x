package com.mesh4j.sync.message.core.file.repository;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.mesh4j.sync.message.protocol.MockSyncSession;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class FileSyncSessionRepositoryTest {

	@Test
	public void shouldFlush(){
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		repo.flush(syncSession);
	}

	@Test
	public void shouldSnapshot(){
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		repo.snapshot(syncSession);
	}
	
	@Test
	public void readSnapshot(){
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item);
		syncSession.addToSnapshot(item);
		
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		repo.snapshot(syncSession);
		
		List<Item> items = repo.readSnapshot(syncSession.getSessionId());
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(item.equals(items.get(0)));		
	}
	
}
