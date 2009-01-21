package org.mesh4j.sync.message.channel.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.InOutStatistics;
import org.mesh4j.sync.message.MockMessageEncoding;
import org.mesh4j.sync.message.MockSmsConnection;
import org.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import org.mesh4j.sync.message.channel.sms.batch.MessageBatchFactory;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.channel.sms.core.MessageFormatter;
import org.mesh4j.sync.message.channel.sms.core.MockSmsReceiver;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.message.core.NonMessageEncoding;
import org.mesh4j.sync.test.utils.TestHelper;


public class SmsChannelTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateChannelFailsWhenSenderIsNull(){
		 new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE),null, new MockSmsReceiver(), new MockMessageEncoding(), 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateChannelFailsWhenReceiverIsNull(){
		 new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE),new MockSmsSender(), null, new MockMessageEncoding(), 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateChannelFailsWhenEncodingIsNull(){
		 new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE),new MockSmsSender(), new MockSmsReceiver(), null, 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldReceiveBathcFailsWhenBathcIsNull(){
		 SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), new MockSmsSender(), new MockSmsReceiver(), new MockMessageEncoding(), 10);
		 channel.receive(null);
	}
	
	@Test
	public void shouldReceiveResendBatchMessagesWhenMessageIsAskForRetry(){
		MockMessageReceiver messageReceiver = new MockMessageReceiver();
		
		SmsMessageBatch batch = createBatch(10, "qqq23672146781&0&46");
		MockSmsSender sender = new MockSmsSender();
		sender.send(batch, true);

		String msg = MessageFormatter.createMessage("R", 0, batch.getId()+"|0");
		SmsMessageBatch batchRetry = createBatch(10, msg);

		
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, new MockSmsReceiver(), new MockMessageEncoding(), 160);
		channel.registerMessageReceiver(messageReceiver);
		channel.receive(batchRetry);
		
		Assert.assertEquals(0, messageReceiver.getMessages().size());
		Assert.assertEquals(1, sender.getMessages().size());
	}
	
	@Test
	public void shouldReceiveNotifyMessageWhenMessageIsNotAskForRetry(){
		String message = MessageFormatter.createMessage("q", 0, "1234567890");
		SmsMessageBatch batch = createBatch(10, message);
		MockMessageReceiver messageReceiver = new MockMessageReceiver();
		
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, new MockSmsReceiver(), new MockMessageEncoding(), 160);
		channel.registerMessageReceiver(messageReceiver);
		channel.receive(batch);
		
		Assert.assertEquals(1, messageReceiver.getMessages().size());
		Assert.assertEquals(0, sender.getMessages().size());
	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldSendMessageFailsWhenMessageIsNull(){		
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), new MockSmsSender(), new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.send(null);
	}	
	
	@Test
	public void shouldSendMessage(){
		Message message = new Message("a", "a", "60131f9c-1e40-47df-b316-ed15a9460515", 0, "123", new SmsEndpoint("123"));
		
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, new MockSmsReceiver(), new MockMessageEncoding(), 100);
		channel.send(message);
		
		Assert.assertEquals(1, sender.getOngoingBatchesCount());
		
		SmsMessageBatch batch = sender.getOngoingBatches().get(0);
		batch.reconstitutePayload();
		Assert.assertEquals("a0&123", batch.getPayload());
	}	
	
	@Test
	public void shouldReceiveAckDiscartedWhenBatchIDIsNull(){
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, new MockSmsReceiver(), new MockMessageEncoding(), 160);
		channel.receiveACK(null);
		
		Assert.assertEquals(0, sender.getACKs().size());
	}
	
	@Test
	public void shouldReceiveAckDiscartedWhenBatchIDIsEmpty(){
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, new MockSmsReceiver(), new MockMessageEncoding(), 160);
		channel.receiveACK("");
		
		Assert.assertEquals(0, sender.getACKs().size());
	}
	
	@Test
	public void shouldReceiveAck(){
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, new MockSmsReceiver(), new MockMessageEncoding(), 160);
		channel.receiveACK("123");
		
		Assert.assertEquals(1, sender.getACKs().size());
		Assert.assertEquals("123", sender.getACKs().get(0));
	}	
	
	@Test
	public void shouldGetOutcommingBatches(){
		SmsMessageBatch batch = createBatch(10, "12345678901234567890");
		
		MockSmsSender sender = new MockSmsSender();
		sender.send(batch, true);
		
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, new MockSmsReceiver(), new MockMessageEncoding(), 160);
		Assert.assertEquals(sender.getOngoingBatches().size(), channel.getOutcommingBatches().size());
		Assert.assertEquals(sender.getOngoingBatches().get(0), channel.getOutcommingBatches().get(0));
		
	}
	
	@Test
	public void shouldGetIncommingBatches(){
		SmsMessageBatch batch = createBatch(10, "12345678901234567890");
		
		MockSmsReceiver receiver = new MockSmsReceiver();
		receiver.addBatch(batch);
		
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), new MockSmsSender(), receiver, new MockMessageEncoding(), 160);
		Assert.assertEquals(receiver.getOngoingBatches().size(), channel.getIncommingBatches().size());
		Assert.assertEquals(receiver.getOngoingBatches().get(0), channel.getIncommingBatches().get(0));
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldSendAskForRetryFailsWhenBatchIsNull(){
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), new MockSmsSender(), new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.sendAskForRetry(null);		
	}
	
	@Test
	public void shouldSendAskForRetry(){
		
		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1);
		SmsMessage smsMessage = new SmsMessage("message 1", date);
		
		SmsMessageBatch batch = new SmsMessageBatch(IdGenerator.INSTANCE.newID(), new SmsEndpoint("1234"), "R", "12345", 3);
		batch.addMessage(1, smsMessage);
		
		MockSmsSender sender = new MockSmsSender();
		
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, new MockSmsReceiver(), new MockMessageEncoding(), 100);
		channel.sendAskForRetry(batch);
		
		Assert.assertTrue(smsMessage.getLastModificationDate().after(date));
		Assert.assertEquals(1, batch.getMessagesCount());
		Assert.assertEquals(1, sender.getOngoingBatchesCount());
		
		SmsMessageBatch batchRetry = sender.getOngoingBatches().get(0);
		batchRetry.reconstitutePayload();
		Assert.assertEquals("R0&12345|0|2|", batchRetry.getPayload());
	}	
	
	
	protected static boolean PURGE_SENDER_WAS_CALLED= false;
	protected static boolean PURGE_RECEIVER_WAS_CALLED= false;
	@Test
	public void shouldPurgeBatches(){
		
		ISmsReceiver receiver = new ISmsReceiver(){
			@Override public void purgeBatches(String sessionId, int sessionVersion) {PURGE_RECEIVER_WAS_CALLED = true;}
			@Override public List<SmsMessageBatch> getCompletedBatches(String sessionId, int version) {Assert.fail();return null;}
			@Override public List<SmsMessageBatch> getOngoingBatches(String sessionId, int version) {Assert.fail(); return null;}
			@Override public List<SmsMessageBatch> getCompletedBatches() {Assert.fail(); return null;}
			@Override public List<DiscardedBatchRecord> getDiscardedBatches() {Assert.fail(); return null;}
			@Override public List<SmsMessageBatch> getOngoingBatches() {Assert.fail(); return null;}
			@Override public int getOngoingBatchesCount() {Assert.fail(); return 0;}
			@Override public void receiveSms(SmsEndpoint endpoint, String message, Date date) {Assert.fail();}
			@Override public void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver) {}
		};

		
		ISmsSender sender = new ISmsSender(){
			@Override public void purgeBatches(String sessionId, int sessionVersion) {PURGE_SENDER_WAS_CALLED = true;}
			@Override public List<SmsMessageBatch> getCompletedBatches(String sessionId, int version) {Assert.fail(); return null;}
			@Override public List<SmsMessageBatch> getOngoingBatches(String sessionId, int version) {Assert.fail(); return null;}
			@Override public SmsMessageBatch getOngoingBatch(String batchID) {Assert.fail();return null;}			
			@Override public List<SmsMessageBatch> getOngoingBatches() {Assert.fail(); return null;}
			@Override public int getOngoingBatchesCount() {Assert.fail(); return 0;}
			@Override public void receiveACK(String batchId) {Assert.fail();}
			@Override public void send(SmsMessageBatch batch, boolean ackRequired) {Assert.fail();}
			@Override public void send(List<SmsMessage> smsMessages, SmsEndpoint endpoint) {Assert.fail();}
			@Override public void send(SmsMessage smsMessage, SmsEndpoint endpoint) {Assert.fail();}
			@Override public void shutdown() {Assert.fail();}
			@Override public void startUp() {Assert.fail();}
			
		};
		
		String sessionId = IdGenerator.INSTANCE.newID();
		int version = 1;
		
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, receiver, new MockMessageEncoding(), 100);
		
		PURGE_RECEIVER_WAS_CALLED = false;
		PURGE_SENDER_WAS_CALLED = false;
		
		channel.purgeMessages(sessionId, version);
		
		Assert.assertTrue(PURGE_RECEIVER_WAS_CALLED);
		Assert.assertTrue(PURGE_SENDER_WAS_CALLED);
	}
	
	@Test
	public void shouldGetInOutStatistics(){
		
		ISmsReceiver receiver = new ISmsReceiver(){

			@Override public List<SmsMessageBatch> getCompletedBatches(String sessionId, int version) {
				ArrayList<SmsMessageBatch> result = new ArrayList<SmsMessageBatch>();
				result.add(createBatch(10, TestHelper.newText(50)));
				return result;
			}
			
			@Override
			public List<SmsMessageBatch> getOngoingBatches(String sessionId, int version) {
				ArrayList<SmsMessageBatch> result = new ArrayList<SmsMessageBatch>();
				SmsMessageBatch batch = new SmsMessageBatch(sessionId, new SmsEndpoint("123"), "a", "1234", 5);
				batch.addMessage(0, new SmsMessage("cccc"));
				batch.addMessage(1, new SmsMessage("cccc"));
				batch.addMessage(2, new SmsMessage("cccc"));
				result.add(batch);
				return result;
			}


			@Override public List<SmsMessageBatch> getCompletedBatches() {Assert.fail(); return null;}
			@Override public List<DiscardedBatchRecord> getDiscardedBatches() {Assert.fail(); return null;}
			@Override public List<SmsMessageBatch> getOngoingBatches() {Assert.fail(); return null;}
			@Override public int getOngoingBatchesCount() {Assert.fail(); return 0;}
			@Override public void purgeBatches(String sessionId, int sessionVersion) {Assert.fail();}
			@Override public void receiveSms(SmsEndpoint endpoint, String message, Date date) {Assert.fail();}
			@Override public void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver) {}
		};

		
		ISmsSender sender = new ISmsSender(){

			@Override public List<SmsMessageBatch> getCompletedBatches(String sessionId, int version) {
				ArrayList<SmsMessageBatch> result = new ArrayList<SmsMessageBatch>();
				result.add(createBatch(10, TestHelper.newText(50)));
				return result;
			}

			@Override public List<SmsMessageBatch> getOngoingBatches(String sessionId, int version) {
				ArrayList<SmsMessageBatch> result = new ArrayList<SmsMessageBatch>();
				result.add(createBatch(10, TestHelper.newText(50)));
				return result;
			}

			@Override public SmsMessageBatch getOngoingBatch(String batchID) {Assert.fail();return null;}			
			@Override public List<SmsMessageBatch> getOngoingBatches() {Assert.fail(); return null;}
			@Override public int getOngoingBatchesCount() {Assert.fail(); return 0;}
			@Override public void purgeBatches(String sessionId, int sessionVersion) {Assert.fail();}
			@Override public void receiveACK(String batchId) {Assert.fail();}
			@Override public void send(SmsMessageBatch batch, boolean ackRequired) {Assert.fail();}
			@Override public void send(List<SmsMessage> smsMessages, SmsEndpoint endpoint) {Assert.fail();}
			@Override public void send(SmsMessage smsMessage, SmsEndpoint endpoint) {Assert.fail();}
			@Override public void shutdown() {Assert.fail();}
			@Override public void startUp() {Assert.fail();}
			
		};
		
		String sessionId = IdGenerator.INSTANCE.newID();
		int version = 1;
		
		SmsChannel channel = new SmsChannel(new MockSmsConnection("1", NonMessageEncoding.INSTANCE), sender, receiver, new MockMessageEncoding(), 100);
		
		InOutStatistics inOut = channel.getInOutStatistics(sessionId, version);
		Assert.assertEquals(8, inOut.getNumberInMessages());
		Assert.assertEquals(2, inOut.getNumberInPendingToArriveMessages());
		Assert.assertEquals(10, inOut.getNumberOutMessages());
		Assert.assertEquals(5, inOut.getNumberOutPendingAckMessages());
		
	}
	
	
	public SmsMessageBatch createBatch(int msgSize, String originalText)
	{
		String id = IdGenerator.INSTANCE.newID();
		MessageBatchFactory factory = new MessageBatchFactory(msgSize);
		SmsMessageBatch batch = factory.createMessageBatch(id, new SmsEndpoint("1234"), "R", id.substring(0,5), originalText);
		return batch;
	}
}
