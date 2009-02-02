package org.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.repository.ISourceIdMapper;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
import org.mesh4j.sync.message.encoding.CompressBase91MessageEncoding;
import org.mesh4j.sync.message.protocol.IItemEncoding;
import org.mesh4j.sync.message.protocol.ItemEncodingFixedBlock;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.test.utils.concurrent.command.ConcurrentCommandExecutor;


public class MessageSyncEngineConcurrentSessionsTests implements IMessageSyncAware, ISmsConnectionInboundOutboundNotification {

	private int syncSessionCount = 0;
	
	@Test
	public void shouldSupportsConcurrentSync() throws InterruptedException{
		
		SmsEndpoint endpointA = new SmsEndpoint(IdGenerator.INSTANCE.newID());
		SmsEndpoint endpointB = new SmsEndpoint(IdGenerator.INSTANCE.newID());
		SmsEndpoint endpointC = new SmsEndpoint(IdGenerator.INSTANCE.newID());
		
		InMemorySmsConnection connectionA = createSmsConnection(endpointA);
		InMemorySmsConnection connectionB = createSmsConnection(endpointB);
		InMemorySmsConnection connectionC = createSmsConnection(endpointC);
		
		connectionA.addEndpointConnection(connectionB);
		connectionA.addEndpointConnection(connectionC);
		connectionB.addEndpointConnection(connectionA);
		connectionB.addEndpointConnection(connectionC);
		connectionC.addEndpointConnection(connectionA);
		connectionC.addEndpointConnection(connectionB);
		
		MessageSyncEngine syncEngineA = createSyncEngine(connectionA);
		MessageSyncEngine syncEngineB = createSyncEngine(connectionB);
		MessageSyncEngine syncEngineC = createSyncEngine(connectionC);
		
		ConcurrentCommandExecutor executor = new ConcurrentCommandExecutor();
		executor.execute(
			new MessageSyncConcurrentCommand(syncEngineA, createAdapter(), endpointB, 0),
			new MessageSyncConcurrentCommand(syncEngineA, createAdapter(), endpointC, 0),
			new MessageSyncConcurrentCommand(syncEngineB, createAdapter(), endpointA, 0),
			new MessageSyncConcurrentCommand(syncEngineB, createAdapter(), endpointC, 0),
			new MessageSyncConcurrentCommand(syncEngineC, createAdapter(), endpointA, 0),
			new MessageSyncConcurrentCommand(syncEngineC, createAdapter(), endpointB, 0)
		);
		
		while(this.syncSessionCount > 0){
			Thread.sleep(500);
		}
	}

	private MessageSyncEngine createSyncEngine(InMemorySmsConnection smsConnection) {
		return createSyncEngine(this, TestHelper.baseDirectoryForTest()+smsConnection.getEndpoint().getEndpointId()+"//", NullIdentityProvider.INSTANCE, smsConnection, 0, 0);
	}

	private IMessageSyncAdapter createAdapter() {
		String sourceId = IdGenerator.INSTANCE.newID();
		List<Item> items = createItems(1);
		return new InMemoryMessageSyncAdapter(sourceId, items);
	}
	
	private InMemorySmsConnection createSmsConnection(SmsEndpoint endpoint){
		InMemorySmsConnection inMemorySmsConnection = new InMemorySmsConnection(
			CompressBase91MessageEncoding.INSTANCE, 
			160, 
			50, 
			endpoint, 
			300, 
			new ISmsConnectionInboundOutboundNotification[]{this});
		return inMemorySmsConnection;
	}
	
	private MessageSyncEngine createSyncEngine(IMessageSyncAware syncAware, String repositoryBaseDirectory, IIdentityProvider identityProvider, ISmsConnection smsConnection, int senderDelay, int receiverDelay){
		
		ISourceIdMapper sourceIdMapper = new ISourceIdMapper(){
			@Override
			public String getSourceDefinition(String sourceId) {
				return sourceId;
			}

			@Override
			public void removeSourceDefinition(String sourceId) {
				// nothing to do			
			}			
		};

		
		IFilter<String> protocolFilter = MessageSyncProtocolFactory.getProtocolMessageFilter();
		
		IChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, repositoryBaseDirectory, protocolFilter);
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, null, true);
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(getItemEncoding(), repositoryBaseDirectory, channel, identityProvider, new IMessageSyncAware[]{syncAware}, SmsEndpointFactory.INSTANCE, syncAdapterFactory);		
		return new MessageSyncEngine(syncProtocol, channel);		
	}
	
	private List<Item> createItems(int max) {
		List<Item> items = new ArrayList<Item>();
		for (int i = 0; i < max; i++) {
			items.add(createItem());
		}
		return items;
	}

	private Item createItem() {
		String syncID = IdGenerator.INSTANCE.newID();
		Element payload = DocumentHelper.createElement("payload");
		payload.addElement("foo").addElement("bar").setText("test sms lib:" + syncID);
		IContent content = new XMLContent(syncID, "title: "+ syncID, "desc: "+ syncID, payload);
		Sync sync = new Sync(syncID, "jmt", TestHelper.now(), false);
		Item item = new Item(content, sync);
		return item;
	}

	
	// IMessageSyncAware protocol
	
	@Override
	public synchronized void beginSync(ISyncSession syncSession) {
		System.out.println("Begin sync: " + syncSession.getSessionId());
		this.syncSessionCount = this.syncSessionCount + 1;
	}

	@Override
	public synchronized void endSync(ISyncSession syncSession, List<Item> conflicts) {
		System.out.println("End sync: " + syncSession.getSessionId());
		this.syncSessionCount = this.syncSessionCount - 1;
	}
	
	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		System.out.println("Error Begin sync: " + syncSession.getSessionId());		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		System.out.println("Cancel sync: " + syncSession.getSessionId());
		this.syncSessionCount = this.syncSessionCount - 1;
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {
		System.out.println("Cancel sync error: " + syncSession.getSourceId() + " endpoint: " + syncSession.getTarget().getEndpointId());
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		System.out.println("Invalid message protocol: " + message.getSessionId());		
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		System.out.println("Invalid protocol message order: " + message.getSessionId());		
	}

	@Override
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		System.out.println("Protocol message processed: " + message.getMessageType());
		for (IMessage message2 : response) {
			System.out.println("	response: " + message2.getMessageType());	
		}
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		System.out.println("Problem with session creation: " + message.getSessionId() + " source: " + sourceId);
	}

	@Override
	public void notifyReceiveMessage(String endpointId, String message,
			Date date) {
		System.out.println("Receive from: " + endpointId + " message: " + message);		
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message,
			Date date) {
		System.out.println("Error - Receive from: " + endpointId + " message: " + message);		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		System.out.println("Send to: " + endpointId + " message: " + message);	
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		System.out.println("Error - Send to: " + endpointId + " message: " + message);		
	}

	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpointId, String message, Date date) {
		System.out.println("Error - msg was not processed - Receive from: " + endpointId + " message: " + message);		
	}

	private IItemEncoding getItemEncoding() {
		//return new ItemEncoding(100);
		return new ItemEncodingFixedBlock(100);
	}


}
