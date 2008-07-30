package com.mesh4j.sync.message.channel.sms;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.message.MockMessageEncoding;
import com.mesh4j.sync.message.channel.sms.batch.MessageBatchFactory;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import com.mesh4j.sync.message.channel.sms.core.MessageFormatter;
import com.mesh4j.sync.message.channel.sms.core.MockSmsReceiver;
import com.mesh4j.sync.message.channel.sms.core.SmsChannel;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class SmsChannelTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateChannelFailsWhenSenderIsNull(){
		 new SmsChannel(null, new MockSmsReceiver(), new MockMessageEncoding(), 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateChannelFailsWhenReceiverIsNull(){
		 new SmsChannel(new MockSmsSender(), null, new MockMessageEncoding(), 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateChannelFailsWhenEncodingIsNull(){
		 new SmsChannel(new MockSmsSender(), new MockSmsReceiver(), null, 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldReceiveBathcFailsWhenBathcIsNull(){
		 SmsChannel channel = new SmsChannel(new MockSmsSender(), new MockSmsReceiver(), new MockMessageEncoding(), 10);
		 channel.receive(null);
	}
	
	@Test
	public void shouldReceiveResendBatchMessagesWhenMessageIsAskForRetry(){
		MockMessageReceiver messageReceiver = new MockMessageReceiver();
		
		SmsMessageBatch batch = createTestBatch(10, "qqq23672146781&0&46");
		MockSmsSender sender = new MockSmsSender();
		sender.send(batch, true);

		String msg = MessageFormatter.createMessage("R", 0, batch.getId()+"|0");
		SmsMessageBatch batchRetry = createTestBatch(10, msg);

		
		SmsChannel channel = new SmsChannel(sender, new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.registerMessageReceiver(messageReceiver);
		channel.receive(batchRetry);
		
		Assert.assertEquals(0, messageReceiver.getMessages().size());
		Assert.assertEquals(1, sender.getMessages().size());
	}
	
	@Test
	public void shouldReceiveNotifyMessageWhenMessageIsNotAskForRetry(){
		String message = MessageFormatter.createMessage("q", 0, "1234567890");
		SmsMessageBatch batch = createTestBatch(10, message);
		MockMessageReceiver messageReceiver = new MockMessageReceiver();
		
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(sender, new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.registerMessageReceiver(messageReceiver);
		channel.receive(batch);
		
		Assert.assertEquals(1, messageReceiver.getMessages().size());
		Assert.assertEquals(0, sender.getMessages().size());
	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldSendMessageFailsWhenMessageIsNull(){		
		SmsChannel channel = new SmsChannel(new MockSmsSender(), new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.send(null);
	}	
	
	@Test
	public void shouldSendMessage(){
		Message message = new Message("a", "a", "60131f9c-1e40-47df-b316-ed15a9460515", 0, "123", new SmsEndpoint("123"));
		
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(sender, new MockSmsReceiver(), new MockMessageEncoding(), 100);
		channel.send(message);
		
		Assert.assertEquals(1, sender.getOngoingBatchesCount());
		
		SmsMessageBatch batch = sender.getOngoingBatches().get(0);
		batch.reconstitutePayload();
		Assert.assertEquals("a0&123", batch.getPayload());
	}	
	
	@Test
	public void shouldReceiveAckDiscartedWhenBatchIDIsNull(){
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(sender, new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.receiveACK(null);
		
		Assert.assertEquals(0, sender.getACKs().size());
	}
	
	@Test
	public void shouldReceiveAckDiscartedWhenBatchIDIsEmpty(){
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(sender, new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.receiveACK("");
		
		Assert.assertEquals(0, sender.getACKs().size());
	}
	
	@Test
	public void shouldReceiveAck(){
		MockSmsSender sender = new MockSmsSender();
		SmsChannel channel = new SmsChannel(sender, new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.receiveACK("123");
		
		Assert.assertEquals(1, sender.getACKs().size());
		Assert.assertEquals("123", sender.getACKs().get(0));
	}	
	
	@Test
	public void shouldGetOutcommingBatches(){
		SmsMessageBatch batch = createTestBatch(10, "12345678901234567890");
		
		MockSmsSender sender = new MockSmsSender();
		sender.send(batch, true);
		
		SmsChannel channel = new SmsChannel(sender, new MockSmsReceiver(), new MockMessageEncoding(), 10);
		Assert.assertEquals(sender.getOngoingBatches().size(), channel.getOutcommingBatches().size());
		Assert.assertEquals(sender.getOngoingBatches().get(0), channel.getOutcommingBatches().get(0));
		
	}
	
	@Test
	public void shouldGetIncommingBatches(){
		SmsMessageBatch batch = createTestBatch(10, "12345678901234567890");
		
		MockSmsReceiver receiver = new MockSmsReceiver();
		receiver.addBatch(batch);
		
		SmsChannel channel = new SmsChannel(new MockSmsSender(), receiver, new MockMessageEncoding(), 10);
		Assert.assertEquals(receiver.getOngoingBatches().size(), channel.getIncommingBatches().size());
		Assert.assertEquals(receiver.getOngoingBatches().get(0), channel.getIncommingBatches().get(0));
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldSendAskForRetryFailsWhenBatchIsNull(){
		SmsChannel channel = new SmsChannel(new MockSmsSender(), new MockSmsReceiver(), new MockMessageEncoding(), 10);
		channel.sendAskForRetry(null);		
	}
	
	@Test
	public void shouldSendAskForRetry(){
		
		Date date = TestHelper.makeDate(2008, 1, 1, 1, 1, 1, 1);
		SmsMessage smsMessage = new SmsMessage("message 1", date);
		
		SmsMessageBatch batch = new SmsMessageBatch(IdGenerator.newID(), new SmsEndpoint("1234"), "R", "12345", 3);
		batch.addMessage(1, smsMessage);
		
		MockSmsSender sender = new MockSmsSender();
		
		SmsChannel channel = new SmsChannel(sender, new MockSmsReceiver(), new MockMessageEncoding(), 100);
		channel.sendAskForRetry(batch);
		
		Assert.assertTrue(smsMessage.getLastModificationDate().after(date));
		Assert.assertEquals(1, batch.getMessagesCount());
		Assert.assertEquals(1, sender.getOngoingBatchesCount());
		
		SmsMessageBatch batchRetry = sender.getOngoingBatches().get(0);
		batchRetry.reconstitutePayload();
		Assert.assertEquals("R0&12345|0|2|", batchRetry.getPayload());
	}	
	
	
	public SmsMessageBatch createTestBatch(int msgSize, String originalText)
	{
		MessageBatchFactory factory = new MessageBatchFactory(msgSize);
		SmsMessageBatch batch = factory.createMessageBatch(IdGenerator.newID(), new SmsEndpoint("1234"), "R", "12345", originalText);
		return batch;
	}
}
