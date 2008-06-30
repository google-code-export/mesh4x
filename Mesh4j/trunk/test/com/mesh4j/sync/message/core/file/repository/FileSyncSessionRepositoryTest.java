package com.mesh4j.sync.message.core.file.repository;

import java.io.File;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.Feed;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.ISyncSessionFactory;
import com.mesh4j.sync.message.core.SyncSessionFactory;
import com.mesh4j.sync.message.protocol.MockMessageSyncAdapter;
import com.mesh4j.sync.message.protocol.MockSyncSession;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.MeshException;

public class FileSyncSessionRepositoryTest {

	// FLUSH
	@Test
	public void shouldGetFlushFile(){
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		File file = repo.getCurrentSessionFile("myFile");
		Assert.assertEquals(TestHelper.baseDirectoryForTest() + "myFile_current.xml", file.getAbsolutePath());
	}
	
	@Test
	public void shouldFlushOpenSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("true", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_OPEN));
	}
	
	@Test(expected=MeshException.class)
	public void shouldFlushCloseSessionFails() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("false", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_OPEN));
	
	}

	@Test
	public void shouldFlushFullProtocolSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		syncSession.setFullProtocol(true);
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("true", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_FULL));	
	}
	
	@Test
	public void shouldFlushLightProtocolSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setFullProtocol(false);
		syncSession.setOpen();
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("false", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_FULL));
	}
	
	@Test
	public void shouldFlushLastSyncDateSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1);
		MockSyncSession syncSession = new MockSyncSession(date, null, sessionId);
		syncSession.setOpen();
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(DateHelper.formatRFC822(date), syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_LAST_SYNC_DATE));
	}
	
	@Test
	public void shouldFlushNonLastSyncDateSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_LAST_SYNC_DATE));
	}
	
	@Test
	public void shouldFlushNonEmptySession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(null, item, sessionId);
		syncSession.setOpen();
		Assert.assertFalse(syncSession.getCurrentSnapshot().isEmpty());
		
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Assert.assertNotNull(feed.getItems());
		Assert.assertEquals(1, feed.getItems().size());
		Assert.assertTrue(item.equals(feed.getItems().get(0)));	
	}
	
	@Test
	public void shouldFlushEmptySession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		Assert.assertTrue(syncSession.getCurrentSnapshot().isEmpty());
		
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Assert.assertNotNull(feed.getItems());
		Assert.assertEquals(0, feed.getItems().size());
	}

	@Test
	public void shouldFlushACKSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		String syncID = IdGenerator.newID();
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		syncSession.waitForAck(syncID);
		
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(1, syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_ACK).size());
		Assert.assertEquals(syncID, ((Element)syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_ACK).get(0)).getText());
	}
	
	@Test
	public void shouldFlushNonACKSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(0, syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_ACK).size());
	}
	
	@Test
	public void shouldFlushConflictSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		String syncID = IdGenerator.newID();
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		syncSession.addConflict(syncID);
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(1, syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_CONFLICT).size());
		Assert.assertEquals(syncID, ((Element)syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_CONFLICT).get(0)).getText());
	}
	
	@Test
	public void shouldFlushNonConflictSession() throws DocumentException{
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		repo.flush(syncSession);
				
		file = repo.getCurrentSessionFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(0, syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_CONFLICT).size());
	}
	
	// SNAPSHOT
	@Test
	public void shouldGetSnapshotFile(){
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		File file = repo.getSnapshotFile("myFile");
		Assert.assertEquals(TestHelper.baseDirectoryForTest() + "myFile_snapshot.xml", file.getAbsolutePath());
	}
	
	@Test(expected=MeshException.class)
	public void shouldSnapshotOpenSessionFails() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		syncSession.setOpen();
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("true", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_OPEN));
	}
	
	@Test
	public void shouldSnapshotCloseSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("false", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_OPEN));
	
	}

	@Test
	public void shouldSnapshotFullProtocolSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.setFullProtocol(true);
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("true", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_FULL));	
	}
	
	@Test
	public void shouldSnapshotLightProtocolSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.setFullProtocol(false);
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("false", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_FULL));
	}
	
	@Test
	public void shouldSnapshotLastSyncDateSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1);
		MockSyncSession syncSession = new MockSyncSession(date, null, sessionId);
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(DateHelper.formatRFC822(date), syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_LAST_SYNC_DATE));
	}
	
	@Test(expected=MeshException.class)
	public void shouldSnapshotNonLastSyncDateSessionFails() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(null, null, sessionId);
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals("", syncSessionElement.attributeValue(FileSyncSessionRepository.ATTRIBUTE_LAST_SYNC_DATE));
	}
	
	@Test
	public void shouldSnapshotNonEmptySession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.addToSnapshot(item);
		
		Assert.assertFalse(syncSession.getSnapshot().isEmpty());
		
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Assert.assertNotNull(feed.getItems());
		Assert.assertEquals(1, feed.getItems().size());
		Assert.assertTrue(item.equals(feed.getItems().get(0)));	
	}
	
	@Test
	public void shouldSnapshotEmptySession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		Assert.assertTrue(syncSession.getCurrentSnapshot().isEmpty());
		
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Assert.assertNotNull(feed.getItems());
		Assert.assertEquals(0, feed.getItems().size());
	}

	@Test
	public void shouldSnapshotACKSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.addToSnapshot(item);
		syncSession.waitForAck(syncID);
		
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(0, syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_ACK).size());
	}
	
	@Test
	public void shouldSnapshotNonACKSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.addToSnapshot(item);

		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(0, syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_ACK).size());
	}
	
	@Test
	public void shouldSnapshotConflictSession() throws DocumentException{

		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.addToSnapshot(item);
		syncSession.addConflict(syncID);
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(1, syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_CONFLICT).size());
		Assert.assertEquals(syncID, ((Element)syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_CONFLICT).get(0)).getText());
	}
	
	@Test
	public void shouldSnapshotNonConflictSession() throws DocumentException{
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());
		
		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.addToSnapshot(item);
		repo.snapshot(syncSession);
				
		file = repo.getSnapshotFile(sessionId);
		Assert.assertTrue(file.exists());
		
		FeedReader feedReader = new FeedReader(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		Feed feed = feedReader.read(file);

		Element syncSessionElement = feed.getPayload().element(FileSyncSessionRepository.ELEMENT_SYNC_SESSION);
		Assert.assertNotNull(syncSessionElement);
		Assert.assertEquals(0, syncSessionElement.elements(FileSyncSessionRepository.ELEMENT_CONFLICT).size());
	}
	
	@Test
	public void shouldSnapshotDeleteCurrentFile(){
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getSnapshotFile(sessionId);
		Assert.assertFalse(file.exists());

		File fileCurrent = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(fileCurrent.exists());

		String syncID = IdGenerator.newID();
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.addToSnapshot(item);
		syncSession.setOpen();
		repo.flush(syncSession);
		
		Assert.assertFalse(file.exists());
		Assert.assertTrue(fileCurrent.exists());
		
		syncSession.setClose();
		repo.snapshot(syncSession);
		
		Assert.assertTrue(file.exists());
		Assert.assertFalse(fileCurrent.exists());
		
		repo.snapshot(syncSession);
		
		Assert.assertTrue(file.exists());
		Assert.assertFalse(fileCurrent.exists());
	}
	
	// DELETE CURRENT SESSION
	@Test
	public void shouldDeleteCurrentSessionFile(){
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		MockSyncSession syncSession = new MockSyncSession(new Date(), null, sessionId);
		syncSession.setOpen();
		repo.flush(syncSession);
		Assert.assertTrue(file.exists());
		
		repo.deleteCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
	}

	@Test
	public void shouldDeleteCurrentSessionFileNoEffectWhenFileDoesNotExist(){
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = IdGenerator.newID();
		
		File file = repo.getCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
				
		repo.deleteCurrentSessionFile(sessionId);
		Assert.assertFalse(file.exists());
	}
	
	// SESSION READ
	
	@Test
	public void shouldReadSnapshot(){
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
		String sessionId = "t3";
		String syncID = IdGenerator.newID();
		
		Item item = new Item(new NullContent(syncID), new Sync(syncID, "jmt", new Date(), true));
		MockSyncSession syncSession = new MockSyncSession(new Date(), item, sessionId);
		syncSession.addToSnapshot(item);
		
		repo.snapshot(syncSession);
		
		List<Item> items = repo.readSnapshot(syncSession.getSessionId());
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(item.equals(items.get(0)));		
	}
	
	@Test(expected=MeshException.class)
	public void shouldReadSessionFailsWhenSnapshotAndCurrentSessionDoesNotExist(){
		FileSyncSessionRepository repo = new FileSyncSessionRepository(TestHelper.baseDirectoryForTest());
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		String sessionId = "123";
		repo.readSession(sessionId, sessionFactory);
	}
	
	@Test(expected=MeshException.class)
	public void shouldReadSessionSnapshotFailsWhenSessionIdAndFileNameAreNotEquals(){
		String sessionId = "exampleSnapshotWithErrorInSessionID";
		File file = new File(this.getClass().getResource(sessionId + "_snapshot.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		repo.readSession(sessionId, sessionFactory);
	}
	
	@Test(expected=MeshException.class)
	public void shouldReadSessionCurrentFailsWhenSessionIdAndFileNameAreNotEquals(){
		String sessionId = "exampleCurrentWithErrorInSessionID";
		File file = new File(this.getClass().getResource(sessionId + "_current.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		repo.readSession(sessionId, sessionFactory);
	}
	
	@Test(expected=MeshException.class)
	public void shouldReadSessionSnapshotAndCurrentFailsWhenSessionIdAndFileNameAreNotEquals(){
		String sessionId = "exampleSnapshotAndCurrentWithErrorInSessionID";
		File file = new File(this.getClass().getResource(sessionId + "_snapshot.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		repo.readSession(sessionId, sessionFactory);
	}
	
	@Test
	public void shouldReadCloseSession(){
		String sessionId = "example1";
		File file = new File(this.getClass().getResource(sessionId + "_snapshot.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertEquals(1, syncSessionLoaded.getSnapshot().size());
		Assert.assertEquals(0, syncSessionLoaded.getCurrentSnapshot().size());
	}
	
	@Test
	public void shouldReadOpenSession(){
		String sessionId = "example2";
		File file = new File(this.getClass().getResource(sessionId + "_current.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertEquals(0, syncSessionLoaded.getSnapshot().size());
		Assert.assertEquals(1, syncSessionLoaded.getCurrentSnapshot().size());
	}
	
	@Test
	public void shouldReadOpenSessionWithPreviousSnapshot(){
		String sessionId = "example3";
		File file = new File(this.getClass().getResource(sessionId + "_snapshot.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertEquals(1, syncSessionLoaded.getSnapshot().size());
		Assert.assertEquals(1, syncSessionLoaded.getCurrentSnapshot().size());
	}
	

	@Test
	public void shouldReadFullProtocolSession() throws DocumentException{
		String sessionId = "example4";
		File file = new File(this.getClass().getResource(sessionId + "_snapshot.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertTrue(syncSessionLoaded.isFullProtocol());
	}
	
	@Test
	public void shouldReadLightProtocolSession() throws DocumentException{
		String sessionId = "example5";
		File file = new File(this.getClass().getResource(sessionId + "_snapshot.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertFalse(syncSessionLoaded.isFullProtocol());
	}
	
	@Test
	public void shouldReadLastSyncDateSession() throws DocumentException{
		String sessionId = "example4";
		File file = new File(this.getClass().getResource(sessionId + "_snapshot.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertEquals(DateHelper.parseRFC822("Mon, 30 Jun 2008 18:31:23 GMT"), syncSessionLoaded.getLastSyncDate());
	}
	
	@Test
	public void shouldReadNonLastSyncDateSessionFails() throws DocumentException{
		String sessionId = "example6";
		File file = new File(this.getClass().getResource(sessionId + "_current.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertNull(syncSessionLoaded.getLastSyncDate());
	}
	
	@Test
	public void shouldReadACKSession() throws DocumentException{
		String sessionId = "example7";
		File file = new File(this.getClass().getResource(sessionId + "_current.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));

		Assert.assertFalse(syncSessionLoaded.isCompleteSync());
		Assert.assertEquals(1, syncSessionLoaded.getAllPendingACKs().size());
		Assert.assertEquals("607d3605-f8e2-4a9a-8ba6-806dd15ac3e6", syncSessionLoaded.getAllPendingACKs().get(0));
	}
	
	@Test
	public void shouldReadNonACKSession() throws DocumentException{
		String sessionId = "example8";
		File file = new File(this.getClass().getResource(sessionId + "_current.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertTrue(syncSessionLoaded.isCompleteSync());
		Assert.assertEquals(0, syncSessionLoaded.getAllPendingACKs().size());
	}
	
	@Test
	public void shouldReadConflictSession() throws DocumentException{
		String sessionId = "example9";
		File file = new File(this.getClass().getResource(sessionId + "_current.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertEquals(1, syncSessionLoaded.getConflictsSyncIDs().size());
		Assert.assertTrue(syncSessionLoaded.hasConflict("8c3f341f-51ab-42ac-8a19-e2cffb0530ad"));
	}
	
	@Test
	public void shouldReadNonConflictSession() throws DocumentException{
		String sessionId = "example8";
		File file = new File(this.getClass().getResource(sessionId + "_current.xml").getFile());
		FileSyncSessionRepository repo = new FileSyncSessionRepository(file.getParent()+"\\");
				
		ISyncSessionFactory sessionFactory = new SyncSessionFactory();
		sessionFactory.registerSource(new MockMessageSyncAdapter("123"));
		
		ISyncSession syncSessionLoaded = repo.readSession(sessionId, sessionFactory);
		Assert.assertNotNull(syncSessionLoaded);		
		Assert.assertSame(syncSessionLoaded, sessionFactory.get(sessionId));
		
		Assert.assertEquals(0, syncSessionLoaded.getConflictsSyncIDs().size());
	}

}
