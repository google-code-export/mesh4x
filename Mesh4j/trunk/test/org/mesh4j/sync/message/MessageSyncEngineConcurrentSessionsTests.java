package org.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.encoding.CompressBase91MessageEncoding;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.test.utils.concurrent.command.ConcurrentCommandExecutor;


public class MessageSyncEngineConcurrentSessionsTests implements IMessageSyncAware {

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
		InMemorySmsConnection inMemorySmsConnection = new InMemorySmsConnection(CompressBase91MessageEncoding.INSTANCE, 160, 50, endpoint);
		return inMemorySmsConnection;
	}
	
	private MessageSyncEngine createSyncEngine(IMessageSyncAware syncAware, String repositoryBaseDirectory, IIdentityProvider identityProvider, ISmsConnection smsConnection, int senderDelay, int receiverDelay){
		IChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, repositoryBaseDirectory);
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(100, repositoryBaseDirectory, identityProvider, syncAware, true);		
		return new MessageSyncEngine(syncProtocol, channel);		
	}
	
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

}
