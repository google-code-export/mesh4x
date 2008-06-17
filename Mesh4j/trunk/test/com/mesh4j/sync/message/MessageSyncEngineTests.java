package com.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.Date;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.message.channel.sms.SmsChannel;
import com.mesh4j.sync.message.dataset.DataSetManager;
import com.mesh4j.sync.message.encoding.ZipBase64Encoding;
import com.mesh4j.sync.message.protocol.FeedItemEncoding;
import com.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.utils.IdGenerator;

public class MessageSyncEngineTests {

	@Test
	public void shouldSync(){
		
		ArrayList<Item> itemsA = new ArrayList<Item>();
		itemsA.add(createNewItem());
		itemsA.add(createNewItem());
		itemsA.add(createNewItem());
		
		ArrayList<Item> itemsB = new ArrayList<Item>();
		itemsB.add(createNewItem());
		itemsB.add(createNewItem());
		itemsB.add(createNewItem());		
		
		String dataSetId = "12345";

		MockSmsConnection smsConnectionEndpointA = new MockSmsConnection("A");
		MockSmsConnection smsConnectionEndpointB = new MockSmsConnection("B");

		smsConnectionEndpointA.setEndPoint(smsConnectionEndpointB);
		smsConnectionEndpointB.setEndPoint(smsConnectionEndpointA);
		
		IChannel channelEndpointA = new SmsChannel(smsConnectionEndpointA, ZipBase64Encoding.INSTANCE);
		IChannel channelEndpointB = new SmsChannel(smsConnectionEndpointB, ZipBase64Encoding.INSTANCE);
		
		DataSetManager dataSetManagerEndPointA = new DataSetManager();
		IDataSet dataSetEndPointA = new MockInMemoryDataSet(dataSetId, itemsA);
		dataSetManagerEndPointA.addDataSet(dataSetEndPointA);		
		
		FeedItemEncoding feedItemEncoding = new FeedItemEncoding(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);

		IMessageSyncProtocol syncProtocolEndPointA = MessageSyncProtocolFactory.createSyncProtocol(dataSetManagerEndPointA, feedItemEncoding);		
		MessageSyncEngine syncEngineEndPointA = new MessageSyncEngine(dataSetManagerEndPointA, syncProtocolEndPointA, channelEndpointA);

		DataSetManager dataSetManagerEndPointB = new DataSetManager();
		IDataSet dataSetEndPointB = new MockInMemoryDataSet(dataSetId, itemsB);
		dataSetManagerEndPointB.addDataSet(dataSetEndPointB);
		
		IMessageSyncProtocol syncProtocolEndPointB = MessageSyncProtocolFactory.createSyncProtocol(dataSetManagerEndPointB, feedItemEncoding);		
		MessageSyncEngine syncEngineEndPointB = new MessageSyncEngine(dataSetManagerEndPointB, syncProtocolEndPointB, channelEndpointB);
		Assert.assertNotNull(syncEngineEndPointB);
		
		Assert.assertEquals(3, dataSetEndPointA.getAll().size());
		Assert.assertEquals(3, dataSetEndPointB.getAll().size());
		
		syncEngineEndPointA.synchronize(dataSetId);
	
		Assert.assertEquals(6, dataSetEndPointA.getAll().size());
		Assert.assertEquals(6, dataSetEndPointB.getAll().size());
	}
	
	
	private Item createNewItem() {
		String syncId = IdGenerator.newID();
		
		Element payload = DocumentHelper.createElement("foo");
		payload.addElement("bar");
		
		IContent content = new XMLContent(syncId, "title", "desc", payload);
		
		Sync sync = new Sync(syncId, "jmt", new Date(), false);
		sync.update("jjj", new Date());
		
		return new Item(content, sync);
	}
}
