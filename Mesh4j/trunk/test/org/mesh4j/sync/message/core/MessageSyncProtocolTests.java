package org.mesh4j.sync.message.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.InOutStatistics;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.repository.ISourceIdMapper;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
import org.mesh4j.sync.message.core.repository.OpaqueFeedSyncAdapterFactory;
import org.mesh4j.sync.message.core.repository.SyncSessionFactory;
import org.mesh4j.sync.message.protocol.BeginSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.CancelSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.IProtocolConstants;
import org.mesh4j.sync.message.protocol.MockSyncSession;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.MeshException;

public class MessageSyncProtocolTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenPrefixIsNull(){
		new MessageSyncProtocol(null, new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new MockChannel(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenPrefixIsEmpty(){
		new MessageSyncProtocol("", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new MockChannel(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenInitialMsgIsNull(){
		new MessageSyncProtocol("M", null, new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new MockChannel(), new ArrayList<IMessageProcessor>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenCancelMsgIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), null, new MockSyncSessionRepository(), new MockChannel(), new ArrayList<IMessageProcessor>());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenRepositoryIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), null, new MockChannel(), new ArrayList<IMessageProcessor>());		
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenMsgProcessorsIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new MockChannel(), null);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateProtocolFailsWhenChannelIsNull(){
		new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), null, new ArrayList<IMessageProcessor>());
	}
	
	@Test
	public void shouldValidMessageReturnsFalseIfMsgIsNull(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new MockChannel(), new ArrayList<IMessageProcessor>());
		syncProtocol.isValidMessageProtocol(null);
	}

	@Test
	public void shouldValidMessageReturnsFalseIfMsgHasInvalidMsgType(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new MockChannel(), new ArrayList<IMessageProcessor>());
		Assert.assertFalse(syncProtocol.isValidMessageProtocol(new Message("J", "a", "a", 0, "a", new SmsEndpoint("a"))));
	}

	@Test
	public void shouldProcessMsgReturnsNoResponseWhenSessionIsNotNullAndMsgTypeIsNotInitialMsg(){
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), new MockChannel(), new ArrayList<IMessageProcessor>());
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, syncProtocol.processMessage(new Message("J", "a", "a", 0, "a", new SmsEndpoint("a"))));	
	}
	
	
	public void shouldBeginSyncReturnNullWhenSessionIsOpen(){
		
		ISourceIdMapper sourceIdMapper = new ISourceIdMapper(){@Override public String getSourceDefinition(String sourceId) {return sourceId;}};
		
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactory.registerSource(new InMemoryMessageSyncAdapter("123"));
		
		syncSessionFactory.createSession("a", 0, "123", "123", true, true, true, true, false, false, new Date(), new Date(), new Date(), 0, 0, new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>(), 0, 0, 0, null, 0, 0, 0);
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new MockChannel(), new ArrayList<IMessageProcessor>());
		Assert.assertNull(syncProtocol.beginSync("123", new SmsEndpoint("123"), true, true, true));
	}
	
	@Test
	public void shouldBeginSyncUseFeedAdapterWhenSourceIDIsNotRegistered(){
		ISourceIdMapper sourceIdMapper = new ISourceIdMapper(){

			@Override
			public String getSourceDefinition(String sourceId) {
				// ""
				return sourceId;
			}
			
		};
		
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, new OpaqueFeedSyncAdapterFactory(""), true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new MockChannel(), new ArrayList<IMessageProcessor>());
		
		SmsEndpoint endpoint = new SmsEndpoint("123");
		Message message = (Message)syncProtocol.beginSync("MySourceType:123", endpoint, true, true, true);
		String sourceId = syncProtocol.getInitialMessage().getSourceId(message.getData());
		SyncSession syncSession = (SyncSession)syncProtocol.getSyncSession(sourceId, endpoint);

		Assert.assertEquals(MessageSyncAdapter.class.getName(), syncSession.getSyncAdapter().getClass().getName());
		Assert.assertEquals(FeedAdapter.class.getName(), ((MessageSyncAdapter)syncSession.getSyncAdapter()).getSyncAdapter().getClass().getName());

	}
	
	@Test
	public void shouldProcessMessageReturnNoResponseWhenSourceIDIsNotRegistered(){
		ISourceIdMapper sourceIdMapper = new ISourceIdMapper(){

			@Override
			public String getSourceDefinition(String sourceId) {
				// ""
				return sourceId;
			}
			
		};
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new MockChannel(), new ArrayList<IMessageProcessor>());
		Assert.assertEquals(IMessageSyncProtocol.NO_RESPONSE, syncProtocol.processMessage(new Message("M", "1", "1", 0, "a|T|T|T|0", new SmsEndpoint("sms:1"))));
	}
	
	@Test(expected=MeshException.class)
	public void shouldCancelSyncFailsWhenSessionIsNull(){
		ISourceIdMapper sourceIdMapper = new ISourceIdMapper(){

			@Override
			public String getSourceDefinition(String sourceId) {
				// ""
				return sourceId;
			}
			
		};
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactory.registerSource(new InMemoryMessageSyncAdapter("123"));

		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new MockChannel(), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
	}
	
	@Test(expected=MeshException.class)
	public void shouldCancelSyncFailsWhenSessionIsClosed(){
		ISourceIdMapper sourceIdMapper = new ISourceIdMapper(){

			@Override
			public String getSourceDefinition(String sourceId) {
				// ""
				return sourceId;
			}
			
		};
		
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactory.registerSource(new InMemoryMessageSyncAdapter("123"));
		
		ISyncSession syncSession = syncSessionFactory.createSession("a", 0, "123", "123", true, true, true, true, false, true, new Date(), new Date(), new Date(), 0, 0, new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>(), 0, 0, 0, null, 0, 0, 0);
		syncSession.endSync(new Date(), 0, 0);
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new MockChannel(), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
	}
	
	@Test
	public void shouldCancelSync(){
		
		ISourceIdMapper sourceIdMapper = new ISourceIdMapper(){

			@Override
			public String getSourceDefinition(String sourceId) {
				// ""
				return sourceId;
			}
			
		};
		
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, null, true);
		SyncSessionFactory syncSessionFactory = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactory.registerSource(new InMemoryMessageSyncAdapter("123"));
		
		ISyncSession syncSession = syncSessionFactory.createSession("a", 0, "123", "123", true, true, true, true, false, true, new Date(), new Date(), new Date(), 0, 0, new ArrayList<Item>(), new ArrayList<Item>(), new ArrayList<String>(), new ArrayList<String>(), 0, 0, 0, null, 0, 0, 0);
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(syncSessionFactory), new MockChannel(), new ArrayList<IMessageProcessor>());
		syncProtocol.cancelSync("123", new SmsEndpoint("123"));
		
		Assert.assertFalse(syncSession.isOpen());
	}
	
	@Test
	public void shouldEndSyncChangeInOutStatistics(){
		
		InOutStatistics stat = new InOutStatistics(5, 0, 8, 0);
		MockChannel channel = new MockChannel();
		channel.setInOutStatistics(stat);
		
		MockSyncSession syncSession = new MockSyncSession(null);
		
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol("M", new BeginSyncMessageProcessor(null, null, null), new CancelSyncMessageProcessor(), new MockSyncSessionRepository(), channel, new ArrayList<IMessageProcessor>());
		
		Assert.assertEquals(0, syncSession.getLastNumberInMessages());
		Assert.assertEquals(0, syncSession.getLastNumberOutMessages());
		Assert.assertFalse(channel.purgeWasCalled);
		
		syncProtocol.endSync(syncSession, new Date());
		
		Assert.assertEquals(stat.getNumberInMessages(), syncSession.getLastNumberInMessages());
		Assert.assertEquals(stat.getNumberOutMessages(), syncSession.getLastNumberOutMessages());
		Assert.assertTrue(channel.purgeWasCalled);
	}
	
	@Test
	public void shouldBeginSyncNotifyErrorIfSyncSessionIsBroken(){
		
		final ISyncSession syncSession = new ISyncSession(){
			@Override public void add(Item item) {}
			@Override public void addConflict(String syncID) {}
			@Override public void addConflict(Item conflicItem) {}
			@Override public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {}
			@Override public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges, Date sinceDate, int version, String targetSourceType) {}
			@Override public void cancelSync() {}
			@Override public Date createSyncDate() {return null;}
			@Override public void delete(String syncID, String by, Date when) {}
			@Override public void endSync(Date sinceDate, int in, int out) {}
			@Override public Item get(String syncId) {return null;}
			@Override public List<Item> getAll() {return null;}
			@Override public List<String> getAllPendingACKs() {return null;}
			@Override public List<String> getConflictsSyncIDs() {return null;}
			@Override public List<Item> getCurrentSnapshot() {return null;}
			@Override public Date getLastSyncDate() {return null;}
			@Override public int getNumberOfAddedItems() {return 0;}
			@Override public int getNumberOfDeletedItems() {return 0;}
			@Override public int getNumberOfUpdatedItems() {return 0;}
			@Override public String getSessionId() {return null;}
			@Override public List<Item> getSnapshot() {return null;}
			@Override public String getSourceId() {return null;}
			@Override public String getSourceType() {return null;}
			@Override public IEndpoint getTarget() {return null;}
			@Override public int getTargetNumberOfAddedItems() {return 0;}
			@Override public int getTargetNumberOfDeletedItems() {return 0;}
			@Override public int getTargetNumberOfUpdatedItems() {return 0;}
			@Override public String getTargetSourceType() {return null;}
			@Override public int getVersion() {return 0;}
			@Override public boolean hasConflict(String syncId) {return false;}
			@Override public boolean isBroken() {return true;}                        /// broken true
 			@Override public boolean isCancelled() {return false;}
			@Override public boolean isCompleteSync() {return false;}
			@Override public boolean isFullProtocol() {return false;}
			@Override public boolean isOpen() {return false;}						// open false
			@Override public void notifyAck(String syncId) {}
			@Override public void setBroken() {}
			@Override public void setTargetNumberOfAddedItems(int added) {}
			@Override public void setTargetNumberOfDeletedItems(int deleted) {}
			@Override public void setTargetNumberOfUpdatedItems(int updated) {}
			@Override public void setTargetSorceType(String targetSourceType) {}
			@Override public boolean shouldReceiveChanges() {return false;}
			@Override public boolean shouldSendChanges() {return false;}
			@Override public void update(Item item) {}
			@Override public void waitForAck(String syncId) {}
			@Override public int getLastNumberInMessages() {return 0;}
			@Override public int getLastNumberOutMessages() {return 0;}
			@Override public Date getEndDate() {return null;}
			@Override public Date getStartDate() {return null;}
		};
		
		ISyncSessionRepository repo = new ISyncSessionRepository(){
			@Override public void cancel(ISyncSession syncSession) {}
			@Override public ISyncSession createSession(String sessionID, int version, String sourceId, IEndpoint endpoint, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {return null;}
			@Override public void flush(ISyncSession syncSession) {}
			@Override public List<ISyncSession> getAllSyncSessions() {return null;}
			@Override public ISyncSession getSession(String sessionId) { return syncSession;}
			@Override public ISyncSession getSession(String sourceId, String endpointId) {return syncSession;}
			@Override public IMessageSyncAdapter getSource(String sourceId) { return null;}
			@Override public IMessageSyncAdapter getSourceOrCreateIfAbsent(String sourceId) {return null;}
			@Override public void registerSource(IMessageSyncAdapter adapter) {}
			@Override public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {}
			@Override public void snapshot(ISyncSession syncSession) {}
		};
		
		BeginSyncMessageProcessor begin  = new BeginSyncMessageProcessor(null, null, null);
		CancelSyncMessageProcessor cancel = new CancelSyncMessageProcessor();
		MyMessageSyncAware syncAware = new MyMessageSyncAware();
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol(IProtocolConstants.PROTOCOL, begin, cancel, repo, new MockChannel(), new ArrayList<IMessageProcessor>());
		syncProtocol.registerSyncAware(syncAware);
		syncProtocol.beginSync("rss20:myFeed", new SmsEndpoint("111"), true, true, true);
		
		Assert.assertTrue(syncAware.beginSyncWithErrorWasCalled);
	}
	
	@Test
	public void shouldProcessMessageNotifyInvalidProtocolMessageOrderIfSyncSessionIsBroken(){
		
		final ISyncSession syncSession = new ISyncSession(){
			@Override public void add(Item item) {}
			@Override public void addConflict(String syncID) {}
			@Override public void addConflict(Item conflicItem) {}
			@Override public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {}
			@Override public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges, Date sinceDate, int version, String targetSourceType) {}
			@Override public void cancelSync() {}
			@Override public Date createSyncDate() {return null;}
			@Override public void delete(String syncID, String by, Date when) {}
			@Override public void endSync(Date sinceDate, int in, int out) {}
			@Override public Item get(String syncId) {return null;}
			@Override public List<Item> getAll() {return null;}
			@Override public List<String> getAllPendingACKs() {return null;}
			@Override public List<String> getConflictsSyncIDs() {return null;}
			@Override public List<Item> getCurrentSnapshot() {return null;}
			@Override public Date getLastSyncDate() {return null;}
			@Override public int getNumberOfAddedItems() {return 0;}
			@Override public int getNumberOfDeletedItems() {return 0;}
			@Override public int getNumberOfUpdatedItems() {return 0;}
			@Override public String getSessionId() {return null;}
			@Override public List<Item> getSnapshot() {return null;}
			@Override public String getSourceId() {return null;}
			@Override public String getSourceType() {return null;}
			@Override public IEndpoint getTarget() {return null;}
			@Override public int getTargetNumberOfAddedItems() {return 0;}
			@Override public int getTargetNumberOfDeletedItems() {return 0;}
			@Override public int getTargetNumberOfUpdatedItems() {return 0;}
			@Override public String getTargetSourceType() {return null;}
			@Override public int getVersion() {return 0;}
			@Override public boolean hasConflict(String syncId) {return false;}
			@Override public boolean isBroken() {return true;}                        /// broken true
 			@Override public boolean isCancelled() {return false;}					// canceled false
			@Override public boolean isCompleteSync() {return false;}
			@Override public boolean isFullProtocol() {return false;}
			@Override public boolean isOpen() {return false;}						// open false
			@Override public void notifyAck(String syncId) {}
			@Override public void setBroken() {}
			@Override public void setTargetNumberOfAddedItems(int added) {}
			@Override public void setTargetNumberOfDeletedItems(int deleted) {}
			@Override public void setTargetNumberOfUpdatedItems(int updated) {}
			@Override public void setTargetSorceType(String targetSourceType) {}
			@Override public boolean shouldReceiveChanges() {return false;}
			@Override public boolean shouldSendChanges() {return false;}
			@Override public void update(Item item) {}
			@Override public void waitForAck(String syncId) {}
			@Override public int getLastNumberInMessages() {return 0;}
			@Override public int getLastNumberOutMessages() {return 0;}
			@Override public Date getEndDate() {return null;}
			@Override public Date getStartDate() {return null;}
		};
		
		ISyncSessionRepository repo = new ISyncSessionRepository(){
			@Override public void cancel(ISyncSession syncSession) {}
			@Override public ISyncSession createSession(String sessionID, int version, String sourceId, IEndpoint endpoint, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {return null;}
			@Override public void flush(ISyncSession syncSession) {}
			@Override public List<ISyncSession> getAllSyncSessions() {return null;}
			@Override public ISyncSession getSession(String sessionId) { return syncSession;}
			@Override public ISyncSession getSession(String sourceId, String endpointId) {return syncSession;}
			@Override public IMessageSyncAdapter getSource(String sourceId) { return null;}
			@Override public IMessageSyncAdapter getSourceOrCreateIfAbsent(String sourceId) {return null;}
			@Override public void registerSource(IMessageSyncAdapter adapter) {}
			@Override public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {}
			@Override public void snapshot(ISyncSession syncSession) {}
		};
		
		BeginSyncMessageProcessor begin  = new BeginSyncMessageProcessor(null, null, null);
		CancelSyncMessageProcessor cancel = new CancelSyncMessageProcessor();
		MyMessageSyncAware2 syncAware = new MyMessageSyncAware2();
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol(IProtocolConstants.PROTOCOL, begin, cancel, repo, new MockChannel(), new ArrayList<IMessageProcessor>());
		syncProtocol.registerSyncAware(syncAware);
		syncProtocol.processMessage(cancel.createMessage(syncSession));
		
		Assert.assertTrue(syncAware.invalidProtocolMessageOrder);
	}
	
	
	@Test
	public void shouldProcessMessageSetSyncSessionBrokenIfMessageProcessorThrowsException(){
		
		final ISyncSession syncSession = new ISyncSession(){
			
			private boolean broken = false;
			
			@Override public void add(Item item) {}
			@Override public void addConflict(String syncID) {}
			@Override public void addConflict(Item conflicItem) {}
			@Override public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {}
			@Override public void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges, Date sinceDate, int version, String targetSourceType) {}
			@Override public void cancelSync() {}
			@Override public Date createSyncDate() {return null;}
			@Override public void delete(String syncID, String by, Date when) {}
			@Override public void endSync(Date sinceDate, int in, int out) {}
			@Override public Item get(String syncId) {return null;}
			@Override public List<Item> getAll() {return null;}
			@Override public List<String> getAllPendingACKs() {return null;}
			@Override public List<String> getConflictsSyncIDs() {return null;}
			@Override public List<Item> getCurrentSnapshot() {return null;}
			@Override public Date getLastSyncDate() {return null;}
			@Override public int getNumberOfAddedItems() {return 0;}
			@Override public int getNumberOfDeletedItems() {return 0;}
			@Override public int getNumberOfUpdatedItems() {return 0;}
			@Override public String getSessionId() {return null;}
			@Override public List<Item> getSnapshot() {return null;}
			@Override public String getSourceId() {return null;}
			@Override public String getSourceType() {return null;}
			@Override public IEndpoint getTarget() {return null;}
			@Override public int getTargetNumberOfAddedItems() {return 0;}
			@Override public int getTargetNumberOfDeletedItems() {return 0;}
			@Override public int getTargetNumberOfUpdatedItems() {return 0;}
			@Override public String getTargetSourceType() {return null;}
			@Override public int getVersion() {return 0;}
			@Override public boolean hasConflict(String syncId) {return false;}
			@Override public boolean isBroken() {return broken;}                        
 			@Override public boolean isCancelled() {return false;}					
			@Override public boolean isCompleteSync() {return false;}
			@Override public boolean isFullProtocol() {return false;}
			@Override public boolean isOpen() {return true;}						// open true
			@Override public void notifyAck(String syncId) {}
			@Override public void setBroken() {this.broken = true; }
			@Override public void setTargetNumberOfAddedItems(int added) {}
			@Override public void setTargetNumberOfDeletedItems(int deleted) {}
			@Override public void setTargetNumberOfUpdatedItems(int updated) {}
			@Override public void setTargetSorceType(String targetSourceType) {}
			@Override public boolean shouldReceiveChanges() {return false;}
			@Override public boolean shouldSendChanges() {return false;}
			@Override public void update(Item item) {}
			@Override public void waitForAck(String syncId) {}
			@Override public int getLastNumberInMessages() {return 0;}
			@Override public int getLastNumberOutMessages() {return 0;}
			@Override public Date getEndDate() {return null;}
			@Override public Date getStartDate() {return null;}
		};
		
	
		IMessageSyncAware syncAware = new IMessageSyncAware() {
			@Override public void beginSync(ISyncSession syncSession) {Assert.fail();}
			@Override public void beginSyncWithError(ISyncSession syncSession) {Assert.fail();}
			@Override public void endSync(ISyncSession syncSession, List<Item> conflicts) {Assert.fail();}
			@Override public void notifyCancelSync(ISyncSession syncSession) {Assert.fail();}
			@Override public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {Assert.fail();}
			@Override public void notifyInvalidMessageProtocol(IMessage message) {Assert.fail();}
			@Override public void notifyInvalidProtocolMessageOrder(IMessage message) {Assert.fail();}
			@Override public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {Assert.fail();}
			@Override public void notifySessionCreationError(IMessage message, String sourceId){Assert.fail();}
		};
		
		
		ICancelSyncMessageProcessor cancel = new ICancelSyncMessageProcessor(){
			@Override public IMessage createMessage(ISyncSession syncSession) {
				return new Message(IProtocolConstants.PROTOCOL, "CANCEL", "1", 1, "data", new SmsEndpoint("endpoint"));
			}
			@Override public String getMessageType() {return "CANCEL";}
			@Override public List<IMessage> process(ISyncSession syncSession, IMessage message) {
				throw new NullPointerException();
			}			
		};
		
		BeginSyncMessageProcessor begin  = new BeginSyncMessageProcessor(null, null, null);
		
		ArrayList<IMessageProcessor> messageProcessors = new ArrayList<IMessageProcessor>();
		messageProcessors.add(cancel);
		
		MySyncSessionRepository repo = new MySyncSessionRepository(syncSession);
		MessageSyncProtocol syncProtocol = new MessageSyncProtocol(IProtocolConstants.PROTOCOL, begin, cancel, repo, new MockChannel(), messageProcessors);
		syncProtocol.registerSyncAware(syncAware);
		
		try{
			Assert.assertFalse(repo.flushWasCalled);
			Assert.assertFalse(syncSession.isBroken());
			syncProtocol.processMessage(cancel.createMessage(syncSession));
			Assert.fail();
		}catch (NullPointerException e) {
			// nothing to do
			Assert.assertTrue(repo.flushWasCalled);
			Assert.assertTrue(syncSession.isBroken());
		}
	}
	
	private class MySyncSessionRepository implements ISyncSessionRepository{
		private ISyncSession syncSession;
		public boolean flushWasCalled = false;
		
		public MySyncSessionRepository(ISyncSession syncSession){
			this.syncSession = syncSession;
		}
		
		@Override public void cancel(ISyncSession syncSession) {Assert.fail();}
		@Override public ISyncSession createSession(String sessionID, int version, String sourceId, IEndpoint endpoint, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {Assert.fail(); return null;}
		@Override public void flush(ISyncSession syncSession) {flushWasCalled=true;}
		@Override public List<ISyncSession> getAllSyncSessions() {Assert.fail(); return null;}
		@Override public ISyncSession getSession(String sessionId) { return syncSession;}
		@Override public ISyncSession getSession(String sourceId, String endpointId) {Assert.fail(); return null;}
		@Override public IMessageSyncAdapter getSource(String sourceId) {Assert.fail(); return null;}
		@Override public IMessageSyncAdapter getSourceOrCreateIfAbsent(String sourceId) {Assert.fail(); return null;}
		@Override public void registerSource(IMessageSyncAdapter adapter) {Assert.fail();}
		@Override public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {Assert.fail();}
		@Override public void snapshot(ISyncSession syncSession) {Assert.fail();}
	};
	
	private class MyMessageSyncAware implements IMessageSyncAware {
		public boolean beginSyncWithErrorWasCalled = false;
		
		@Override public void beginSync(ISyncSession syncSession) {Assert.fail();}
		@Override public void beginSyncWithError(ISyncSession syncSession) { beginSyncWithErrorWasCalled = true; }
		@Override public void endSync(ISyncSession syncSession, List<Item> conflicts) {Assert.fail();}
		@Override public void notifyCancelSync(ISyncSession syncSession) {Assert.fail();}
		@Override public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {Assert.fail();}
		@Override public void notifyInvalidMessageProtocol(IMessage message) {Assert.fail();}
		@Override public void notifyInvalidProtocolMessageOrder(IMessage message) {Assert.fail();}
		@Override public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {Assert.fail();}
		@Override public void notifySessionCreationError(IMessage message, String sourceId){Assert.fail();}
	};
	
	private class MyMessageSyncAware2 implements IMessageSyncAware {
		public boolean invalidProtocolMessageOrder = false;
		
		@Override public void beginSync(ISyncSession syncSession) {Assert.fail();}
		@Override public void beginSyncWithError(ISyncSession syncSession) { Assert.fail();}
		@Override public void endSync(ISyncSession syncSession, List<Item> conflicts) {Assert.fail();}
		@Override public void notifyCancelSync(ISyncSession syncSession) {Assert.fail();}
		@Override public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {Assert.fail();}
		@Override public void notifyInvalidMessageProtocol(IMessage message) {Assert.fail();}
		@Override public void notifyInvalidProtocolMessageOrder(IMessage message) {invalidProtocolMessageOrder = true;}
		@Override public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {Assert.fail();}
		@Override public void notifySessionCreationError(IMessage message, String sourceId){Assert.fail();}
	};
}
