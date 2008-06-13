package com.mesh4j.sync.message;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.dom.DOMAdapter;
import com.mesh4j.sync.adapters.kml.DOMLoaderFactory;
import com.mesh4j.sync.message.channel.sms.SmsChannel;
import com.mesh4j.sync.message.dataset.DataSetManager;
import com.mesh4j.sync.message.encoding.ZipBase64Encoding;
import com.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.XMLHelper;

public class KmlMessageSyncEngineTests {

	@Test
	public void shouldSyncKml() throws DocumentException, IOException{
		
		String fileNameA = this.getClass().getResource("kmlWithSyncInfo.kml").getFile(); 
		DOMAdapter kmlAdapterA = new DOMAdapter(DOMLoaderFactory.createDOMLoader(fileNameA, NullIdentityProvider.INSTANCE));
		kmlAdapterA.beginSync();

		String fileNameB = this.getClass().getResource("kmlDummyForSync.kml").getFile(); 
		DOMAdapter kmlAdapterB = new DOMAdapter(DOMLoaderFactory.createDOMLoader(fileNameB, NullIdentityProvider.INSTANCE));
		kmlAdapterB.beginSync();
		
		// Sync SMS
		String dataSetId = "12345";
		
		MockSmsConnection smsConnectionEndpointA = new MockSmsConnection();
		MockSmsConnection smsConnectionEndpointB = new MockSmsConnection();

		smsConnectionEndpointA.setEndPoint(smsConnectionEndpointB);
		smsConnectionEndpointB.setEndPoint(smsConnectionEndpointA);
		
		IChannel channelEndpointA = new SmsChannel(smsConnectionEndpointA, ZipBase64Encoding.INSTANCE);		// 164
		IChannel channelEndpointB = new SmsChannel(smsConnectionEndpointB, ZipBase64Encoding.INSTANCE);
//		
//		IChannel channelEndpointA = new SmsChannel(smsConnectionEndpointA, DummyMessageEncoding.INSTANCE);  // 176
//		IChannel channelEndpointB = new SmsChannel(smsConnectionEndpointB, DummyMessageEncoding.INSTANCE);

		DataSetManager dataSetManagerEndPointA = new DataSetManager();
		IDataSet dataSetEndPointA = new MockInMemoryDataSet(dataSetId, kmlAdapterA.getAll());
		dataSetManagerEndPointA.addDataSet(dataSetEndPointA);		
		
		IMessageSyncProtocol syncProtocolEndPointA = MessageSyncProtocolFactory.createSyncProtocol(dataSetManagerEndPointA, NullIdentityProvider.INSTANCE);		
		MessageSyncEngine syncEngineEndPointA = new MessageSyncEngine(dataSetManagerEndPointA, syncProtocolEndPointA, channelEndpointA);

		DataSetManager dataSetManagerEndPointB = new DataSetManager();
		MockInMemoryDataSet dataSetEndPointB = new MockInMemoryDataSet(dataSetId, kmlAdapterB.getAll());
		dataSetManagerEndPointB.addDataSet(dataSetEndPointB);
		
		IMessageSyncProtocol syncProtocolEndPointB = MessageSyncProtocolFactory.createSyncProtocol(dataSetManagerEndPointB, NullIdentityProvider.INSTANCE);		
		MessageSyncEngine syncEngineEndPointB = new MessageSyncEngine(dataSetManagerEndPointB, syncProtocolEndPointB, channelEndpointB);
		Assert.assertNotNull(syncEngineEndPointB);
		
		Assert.assertEquals(611, dataSetEndPointA.getItems().size());
		Assert.assertEquals(0, dataSetEndPointB.getItems().size());
		
		syncEngineEndPointA.synchronize(dataSetId);
	
		Assert.assertEquals(611, dataSetEndPointA.getItems().size());
//		Assert.assertEquals(611, dataSetEndPointB.getItems().size());
	
//		Assert.assertEquals(164, smsConnectionEndpointA.getGeneratedMessageStatistics());
		
		// Sync KML file
		File file = new File(TestHelper.fileName("kmlMessage.kml")); 
		XMLHelper.write(kmlAdapterB.getDOM().toDocument(), file);
		
		DOMAdapter kmlAdapter = new DOMAdapter(DOMLoaderFactory.createDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE));
		
		SyncEngine syncEngine = new SyncEngine(kmlAdapter, dataSetEndPointB);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		kmlAdapter.beginSync();
		int itemsSize = kmlAdapter.getAll().size();
		Assert.assertEquals(611, itemsSize);

		
		Document document = kmlAdapter.getDOM().toDocument();
		
		int xmlCano = XMLHelper.canonicalizeXML(document).length();
		System.out.println("canon: " + xmlCano + "  messages: " + ((xmlCano / 121) + (xmlCano % 121)));
		
		int xmlformat = XMLHelper.formatXML(document, OutputFormat.createCompactFormat()).length();
		System.out.println("format: " + xmlformat + "  messages: " + ((xmlformat / 121) + (xmlformat % 121)));
		
		System.out.println("items: " + itemsSize);
		
		System.out.println("batch A (zip+base64): " 
				+ smsConnectionEndpointA.getGeneratedMessagesSizeStatistics() 
				+ " messages: " + smsConnectionEndpointA.getGeneratedMessagesStatistics());
		
		System.out.println("batch B (zip+base64): " 
				+ smsConnectionEndpointB.getGeneratedMessagesSizeStatistics() 
				+ " messages: " + smsConnectionEndpointB.getGeneratedMessagesStatistics());
	}
	
// Statistics:
//	canon: 352749  messages: 2949
//	format: 339786  messages: 2826
//	items: 611
//	File size: 401 kb
//	batch A (zip+base64): 467913 messages: 3354
//	batch B (zip+base64): 20 messages: 1
//
//	canon: 352708  messages: 3028
//	format: 339745  messages: 2905
//	items: 611
//	File size: 401 kb
//	batch A: 494699 messages: 3656
//	batch B: 19 messages: 1
}
