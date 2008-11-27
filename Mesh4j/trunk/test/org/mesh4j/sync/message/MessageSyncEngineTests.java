package org.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.ISyncSessionRepository;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.message.core.MockSyncSessionRepository;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
import org.mesh4j.sync.message.core.repository.SyncSessionFactory;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.test.utils.TestHelper;


public class MessageSyncEngineTests {

	private final static String SOURCE_ID = "12345";
	private final static String TARGET_A = "A";
	private final static String TARGET_B = "B";
	private MessageSyncEngine syncEngineEndPointA;
	private MessageSyncEngine syncEngineEndPointB;
	private ISyncSessionRepository syncSessionRepoA;
	private ISyncSessionRepository syncSessionRepoB;
	private MockSmsConnection smsConnectionEndpointA;
	private MockSmsConnection smsConnectionEndpointB;
	private List<Item> snapshotA;
	private List<Item> snapshotB;
	private ISmsChannel channelEndpointA;
	private ISmsChannel channelEndpointB;
	private IMessageSyncAdapter endPointA;

	public void setUp(IMessageSyncAdapter endPointA, IMessageSyncAdapter endPointB){
		setUp(endPointA, 0, 140, 50000, 50000, endPointB, 0, 140, 50000, 50000);
	}
		
	public void setUp(IMessageSyncAdapter endPointA, int smsConnectionDelayA, int maxMessageLenghtA, int channelASenderCheckDelay, int channelAReceiveCheckDelay, IMessageSyncAdapter endPointB, int smsConnectionDelayB, int maxMessageLenghtB, int channelBSenderCheckDelay, int channelBReceiveCheckDelay){
		
		this.endPointA = endPointA;
				
		smsConnectionEndpointA = new MockSmsConnection(TARGET_A, MockMessageEncoding.INSTANCE);
		smsConnectionEndpointA.setSleepDelay(smsConnectionDelayA);
		smsConnectionEndpointA.setMaxMessageLenght(maxMessageLenghtA);
		
		smsConnectionEndpointB = new MockSmsConnection(TARGET_B, MockMessageEncoding.INSTANCE);
		smsConnectionEndpointB.setSleepDelay(smsConnectionDelayB);
		smsConnectionEndpointB.setMaxMessageLenght(maxMessageLenghtB);
		
		smsConnectionEndpointA.setEndPoint(smsConnectionEndpointB);
		smsConnectionEndpointA.activateTrace();
		smsConnectionEndpointB.setEndPoint(smsConnectionEndpointA);
		smsConnectionEndpointB.activateTrace();
		
		//FileSmsChannelRepository fileRepoA = new FileSmsChannelRepository("c:\\");
		//channelEndpointA = SmsChannelFactory.createChannel(smsConnectionEndpointA, channelASenderCheckDelay, channelAReceiveCheckDelay, fileRepoA, fileRepoA);
		channelEndpointA = SmsChannelFactory.createChannel(smsConnectionEndpointA, channelASenderCheckDelay, channelAReceiveCheckDelay);
		
		//FileSmsChannelRepository fileRepoB = new FileSmsChannelRepository("d:\\");
		//channelEndpointB = SmsChannelFactory.createChannel(smsConnectionEndpointB, channelBSenderCheckDelay, channelBReceiveCheckDelay, fileRepoB, fileRepoB);
		channelEndpointB = SmsChannelFactory.createChannel(smsConnectionEndpointB, channelBSenderCheckDelay, channelBReceiveCheckDelay);
		
		MessageSyncAdapterFactory syncAdapterFactory = new MessageSyncAdapterFactory(null, true);
		SyncSessionFactory syncSessionFactoryA = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactoryA.registerSource(endPointA);
		
		//this.syncSessionRepoA = new FileSyncSessionRepository("c:\\", syncSessionFactoryA);
		this.syncSessionRepoA = new MockSyncSessionRepository(syncSessionFactoryA);
		
		IMessageSyncProtocol syncProtocolA = MessageSyncProtocolFactory.createSyncProtocol(100, this.syncSessionRepoA);
		syncEngineEndPointA = new MessageSyncEngine(syncProtocolA, channelEndpointA);

		SyncSessionFactory syncSessionFactoryB = new SyncSessionFactory(SmsEndpointFactory.INSTANCE, syncAdapterFactory);
		syncSessionFactoryB.registerSource(endPointB);

		//this.syncSessionRepoB = new FileSyncSessionRepository("d:\\", syncSessionFactoryB);
		this.syncSessionRepoB = new MockSyncSessionRepository(syncSessionFactoryB);
		
		IMessageSyncProtocol syncProtocolB = MessageSyncProtocolFactory.createSyncProtocol(100, this.syncSessionRepoB);
		syncEngineEndPointB = new MessageSyncEngine(syncProtocolB, channelEndpointB);
		Assert.assertNotNull(syncEngineEndPointB);
	}
	
	@Test
	public void shouldSyncNewB(){		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(createNewItem(TestHelper.newText(500)));
		
		IMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		IMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, 500, 140, 0, 0, endPointB, 500, 140, 0, 0);
		
		sync(false);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
	}
	
	@Test
	public void shouldSyncNewA(){		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		itemsA.add(createNewItem());
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		
		IMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		IMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(false);			
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotB.get(0).isDeleted());
	}
	
	@Test
	public void shouldSyncDeleteB() throws InterruptedException{
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		itemsA.add(item);
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(false);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		
		Item itemDeleted = new Item(new NullContent(snapshotB.get(0).getSyncId()), snapshotB.get(0).getSync().clone());
		Thread.sleep(1000);
		itemDeleted.getSync().delete("BIA", new Date());
		endPointB.add(itemDeleted);
		
		sync(false);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).isDeleted());
		Assert.assertTrue(snapshotB.get(0).isDeleted());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));

	}
	
	@Test
	public void shouldSyncDeleteA() throws InterruptedException{		
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(item);
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(false);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		
		Item itemDeleted = new Item(new NullContent(snapshotA.get(0).getSyncId()), snapshotA.get(0).getSync().clone());
		Thread.sleep(1000);
		itemDeleted.getSync().delete("BIA", new Date());
		endPointA.add(itemDeleted);
		
		sync(false);				
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).isDeleted());
		Assert.assertTrue(snapshotB.get(0).isDeleted());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
	}
	
	@Test
	public void shouldSyncUpdateB() throws InterruptedException{		
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		itemsA.add(item);
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
				
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(false);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		
		Item itemUpdated = snapshotB.get(0).clone();
		Thread.sleep(1000);
		itemUpdated.getSync().update("BIA", new Date());
		Element element = itemUpdated.getContent().getPayload().element("bar");
		element.add(DocumentHelper.createElement("updateXXXX"));
		((Content)itemUpdated.getContent()).refreshVersion();
		endPointB.add(itemUpdated);
		
		sync(false);				
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		Assert.assertNotNull(snapshotA.get(0).getContent().getPayload().element("bar").element("updateXXXX"));
	}
	
	@Test
	public void shouldSyncUpdateA() throws InterruptedException{		
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(item);
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(false);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		
		Item itemUpdated = snapshotA.get(0).clone();
		Thread.sleep(1000);
		itemUpdated.getSync().update("BIA", new Date());
		Element element = itemUpdated.getContent().getPayload().element("bar");
		element.add(DocumentHelper.createElement("updateXXXX"));
		((Content)itemUpdated.getContent()).refreshVersion();
		endPointA.add(itemUpdated);
		
		sync(false);				
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertFalse(snapshotB.get(0).isDeleted());
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		Assert.assertNotNull(snapshotA.get(0).getContent().getPayload().element("bar").element("updateXXXX"));
		Assert.assertNotNull(snapshotB.get(0).getContent().getPayload().element("bar").element("updateXXXX"));
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
	}
	
	@Test
	public void shouldSyncConflict() throws InterruptedException{		
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(item);
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(false);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());

		
		Item itemUpdated = snapshotA.get(0).clone();
		Thread.sleep(1000);
		itemUpdated.getSync().update("BIA", new Date());
		Element element = itemUpdated.getContent().getPayload().element("bar");
		element.add(DocumentHelper.createElement("updateXXXX"));
		((Content)itemUpdated.getContent()).refreshVersion();
		endPointA.update(itemUpdated);
		
		itemUpdated = snapshotB.get(0).clone();
		Thread.sleep(1000);
		itemUpdated.getSync().update("JUN", new Date());
		element = itemUpdated.getContent().getPayload().element("bar");
		element.add(DocumentHelper.createElement("updateJUN"));
		((Content)itemUpdated.getContent()).refreshVersion();
		endPointB.update(itemUpdated);
		
		Assert.assertFalse(syncSessionRepoA.getSession(SOURCE_ID, TARGET_B).hasConflict(item.getSyncId()));
		Assert.assertFalse(syncSessionRepoB.getSession(SOURCE_ID, TARGET_A).hasConflict(item.getSyncId()));
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertNotNull(endPointA.get(item.getSyncId()).getContent().getPayload().element("bar").element("updateXXXX"));
		Assert.assertNotNull(endPointB.get(item.getSyncId()).getContent().getPayload().element("bar").element("updateJUN"));
		
		sync(false);				
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertFalse(snapshotB.get(0).isDeleted());
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		Assert.assertNotNull(endPointA.get(item.getSyncId()).getContent().getPayload().element("bar").element("updateXXXX"));
		Assert.assertNotNull(endPointB.get(item.getSyncId()).getContent().getPayload().element("bar").element("updateJUN"));
		Assert.assertFalse(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertTrue(syncSessionRepoA.getSession(SOURCE_ID, TARGET_B).hasConflict(item.getSyncId()));
		Assert.assertFalse(syncSessionRepoB.getSession(SOURCE_ID, TARGET_A).hasConflict(item.getSyncId()));
	}
	
	
	@Test
	public void shouldSyncConflictFullProtocol() throws InterruptedException{		
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(item);
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(true);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());

		Item itemUpdated = snapshotB.get(0).clone();
		Thread.sleep(1000);
		itemUpdated.getSync().update("JUN", new Date());
		Element element = itemUpdated.getContent().getPayload().element("bar");
		element.add(DocumentHelper.createElement("updateJUN"));
		((Content)itemUpdated.getContent()).refreshVersion();
		endPointB.update(itemUpdated);
		
		itemUpdated = snapshotA.get(0).clone();
		Thread.sleep(1000);
		itemUpdated.getSync().update("BIA", new Date());
		element = itemUpdated.getContent().getPayload().element("bar");
		element.add(DocumentHelper.createElement("updateXXXX"));
		((Content)itemUpdated.getContent()).refreshVersion();
		endPointA.update(itemUpdated);
		
		Assert.assertFalse(syncSessionRepoA.getSession(SOURCE_ID, TARGET_B).hasConflict(item.getSyncId()));
		Assert.assertFalse(syncSessionRepoB.getSession(SOURCE_ID, TARGET_A).hasConflict(item.getSyncId()));
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertNotNull(endPointA.get(item.getSyncId()).getContent().getPayload().element("bar").element("updateXXXX"));
		Assert.assertNotNull(endPointB.get(item.getSyncId()).getContent().getPayload().element("bar").element("updateJUN"));
		
		sync(true);				
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertFalse(snapshotB.get(0).isDeleted());
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		Assert.assertNotNull(endPointA.get(item.getSyncId()).getContent().getPayload().element("bar").element("updateXXXX"));
		Assert.assertNotNull(endPointB.get(item.getSyncId()).getContent().getPayload().element("bar").element("updateJUN"));
		Assert.assertTrue(syncSessionRepoA.getSession(SOURCE_ID, TARGET_B).hasConflict(item.getSyncId()));
		Assert.assertFalse(syncSessionRepoB.getSession(SOURCE_ID, TARGET_A).hasConflict(item.getSyncId()));
		Assert.assertFalse(snapshotA.get(0).equals(snapshotB.get(0)));
	}
	
	@Test
	public void shouldSyncNewBFullProtocol(){		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(createNewItem());
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(true);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
	}
	
	@Test
	public void shouldSyncNewAFullProtocol(){		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		itemsA.add(createNewItem());
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(true);			
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotB.get(0).isDeleted());
	}
	
	@Test
	public void shouldSyncDeleteBFullProtocol() throws InterruptedException{
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		itemsA.add(item);
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(true);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		
		Item itemDeleted = new Item(new NullContent(snapshotB.get(0).getSyncId()), snapshotB.get(0).getSync().clone());
		Thread.sleep(1000);
		itemDeleted.getSync().delete("BIA", new Date());
		endPointB.add(itemDeleted);
		
		sync(true);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).isDeleted());
		Assert.assertTrue(snapshotB.get(0).isDeleted());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));

	}
	
	@Test
	public void shouldSyncDeleteAFullProtocol() throws InterruptedException{		
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(item);
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(true);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		
		Item itemDeleted = new Item(new NullContent(snapshotA.get(0).getSyncId()), snapshotA.get(0).getSync().clone());
		Thread.sleep(1000);
		itemDeleted.getSync().delete("BIA", new Date());
		endPointA.add(itemDeleted);
		
		sync(true);				
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).isDeleted());
		Assert.assertTrue(snapshotB.get(0).isDeleted());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
	}
	
	@Test
	public void shouldSyncUpdateBFullProtocol() throws InterruptedException{		
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		itemsA.add(item);
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
				
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(true);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		
		Item itemUpdated = snapshotB.get(0).clone();
		Thread.sleep(1000);
		itemUpdated.getSync().update("BIA", new Date());
		Element element = itemUpdated.getContent().getPayload().element("bar");
		element.add(DocumentHelper.createElement("updateXXXX"));
		((Content)itemUpdated.getContent()).refreshVersion();
		endPointB.add(itemUpdated);
		
		sync(true);				
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		Assert.assertNotNull(snapshotA.get(0).getContent().getPayload().element("bar").element("updateXXXX"));
	}
	
	@Test
	public void shouldSyncUpdateAFullProtocol() throws InterruptedException{		
		Item item = createNewItem();
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(item);
		
		InMemoryMessageSyncAdapter endPointA = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsA);
		InMemoryMessageSyncAdapter endPointB = new InMemoryMessageSyncAdapter(SOURCE_ID, itemsB);
		setUp(endPointA, endPointB);
		
		sync(true);		
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		
		Item itemUpdated = snapshotA.get(0).clone();
		Thread.sleep(1000);
		itemUpdated.getSync().update("BIA", new Date());
		Element element = itemUpdated.getContent().getPayload().element("bar");
		element.add(DocumentHelper.createElement("updateXXXX"));
		((Content)itemUpdated.getContent()).refreshVersion();
		endPointA.add(itemUpdated);
		
		sync(true);				
		Assert.assertEquals(1, snapshotA.size());
		Assert.assertEquals(1, snapshotB.size());
		Assert.assertFalse(snapshotB.get(0).isDeleted());
		Assert.assertFalse(snapshotA.get(0).isDeleted());
		Assert.assertNotNull(snapshotA.get(0).getContent().getPayload().element("bar").element("updateXXXX"));
		Assert.assertNotNull(snapshotB.get(0).getContent().getPayload().element("bar").element("updateXXXX"));
		Assert.assertTrue(snapshotA.get(0).equals(snapshotB.get(0)));
	}
	
	private void sync(boolean fullProtocol){
	
		smsConnectionEndpointA.resetStatistics();
		smsConnectionEndpointB.resetStatistics();
		
		SmsEndpoint smsEndpointB = new SmsEndpoint(TARGET_B);
		
		if(fullProtocol){
			syncEngineEndPointA.synchronize(this.endPointA, smsEndpointB, true);
		} else {
			syncEngineEndPointA.synchronize(this.endPointA, smsEndpointB);
		}
		
		Assert.assertFalse(syncSessionRepoA.getSession(SOURCE_ID, TARGET_B).isOpen());
		Assert.assertFalse(syncSessionRepoB.getSession(SOURCE_ID, TARGET_A).isOpen());
		
		Assert.assertEquals(0, channelEndpointA.getIncommingBatches().size());
		Assert.assertEquals(0, channelEndpointA.getOutcommingBatches().size());
		Assert.assertEquals(0, channelEndpointB.getIncommingBatches().size());
		Assert.assertEquals(0, channelEndpointB.getOutcommingBatches().size());
		
		System.out.println("A: " 
				+ smsConnectionEndpointA.getGeneratedMessagesSizeStatistics() 
				+ " messages: " + smsConnectionEndpointA.getGeneratedMessagesStatistics());
		
		System.out.println("B: " 
				+ smsConnectionEndpointB.getGeneratedMessagesSizeStatistics() 
				+ " messages: " + smsConnectionEndpointB.getGeneratedMessagesStatistics());
		
		snapshotA = syncSessionRepoA.getSession(SOURCE_ID, TARGET_B).getSnapshot();
		snapshotB = syncSessionRepoB.getSession(SOURCE_ID, TARGET_A).getSnapshot();
	}
	
	private Item createNewItem() {
		return createNewItem("123");
	}
	
	private Item createNewItem(String text) {
		String syncId = IdGenerator.INSTANCE.newID();
		
		Element payload = DocumentHelper.createElement("foo");
		Element barElement = payload.addElement("bar");
		barElement.setText(text);
		
		IContent content = new XMLContent(syncId, "", "", payload);
		
		Sync sync = new Sync(syncId, "jmt", new Date(), false);
		sync.update("jjj", new Date());
		
		return new Item(content, sync);
	}
	
	@Test
	public void shouldCancelSync(){
		final Message msg = new Message("1", "2", "3", 0, "4", new SmsEndpoint("123"));
		
		IChannel channel = new IChannel(){
			@Override public void registerMessageReceiver(IMessageReceiver messageReceiver) {}
			@Override public void send(IMessage message) {
				Assert.assertSame(msg, message);
			}
			@Override public void shutdown() {}
		};
		
		IMessageSyncProtocol protocol = new IMessageSyncProtocol(){
			@Override public IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol) {return null;}
			@Override public IMessage cancelSync(String sourceId, IEndpoint target) {return msg; }
			@Override public void cancelSync(ISyncSession syncSession) {Assert.fail();}
			@Override public boolean isValidMessageProtocol(IMessage message) {return false;}
			@Override public List<IMessage> processMessage(IMessage message) {return null;}
			@Override public ISyncSession getSyncSession(String sourceId, IEndpoint target) {return null;}
			@Override public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {}
			@Override public void registerSource(IMessageSyncAdapter adapter) {}
			@Override public void endSync(ISyncSession syncSession, Date date) {}
			@Override public void notifyBeginSync(ISyncSession syncSession) {}
			@Override public void registerSyncAware(IMessageSyncAware syncAware) {}
			@Override public IMessageSyncAdapter getSource(String sourceId) { return null; }
		};
		MessageSyncEngine engine = new MessageSyncEngine(protocol, channel);
		engine.cancelSync("123", new SmsEndpoint("123"));
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateEngineFailsWhenProtocolIsNull(){
		IChannel channel = new IChannel(){
			@Override public void registerMessageReceiver(IMessageReceiver messageReceiver) {}
			@Override public void send(IMessage message) {}
			@Override public void shutdown() {}

		};
		new MessageSyncEngine(null, channel);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateEngineFailsWhenChannelIsNull(){
		IMessageSyncProtocol protocol = new IMessageSyncProtocol(){
			@Override public IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol) {return null;}
			@Override public IMessage cancelSync(String sourceId, IEndpoint target) {return null;}
			@Override public boolean isValidMessageProtocol(IMessage message) {return false;}
			@Override public List<IMessage> processMessage(IMessage message) {return null;}
			@Override public ISyncSession getSyncSession(String sourceId, IEndpoint target) {return null;}
			@Override public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {}
			@Override public void registerSource(IMessageSyncAdapter adapter) {}
			@Override public void endSync(ISyncSession syncSession, Date date) {}
			@Override public void notifyBeginSync(ISyncSession syncSession) {}
			@Override public void registerSyncAware(IMessageSyncAware syncAware) {}
			@Override public void cancelSync(ISyncSession syncSession) {Assert.fail();}
			@Override public IMessageSyncAdapter getSource(String sourceId) { return null; }
		};
		new MessageSyncEngine(protocol, null);
	}
}
