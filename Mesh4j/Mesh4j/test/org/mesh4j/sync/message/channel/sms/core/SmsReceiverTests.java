package org.mesh4j.sync.message.channel.sms.core;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.DiscardedBatchException;
import org.mesh4j.sync.message.channel.sms.batch.MessageBatchFactory;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.test.utils.TestHelper;


public class SmsReceiverTests {
	
	public SmsMessageBatch creatBatch(int originalTextlength)
	{
		MessageBatchFactory factory = new MessageBatchFactory();
		return factory.createMessageBatch(IdGenerator.INSTANCE.newID(), new SmsEndpoint("1234"), "M", "12345", TestHelper.newText(originalTextlength));
	}
	
	public SmsMessageBatch createBatch(String originalText, int msgSize)
	{
		MessageBatchFactory factory = new MessageBatchFactory(msgSize);
		return factory.createMessageBatch(IdGenerator.INSTANCE.newID(), new SmsEndpoint("1234"), "M", "12345", originalText);
	}
	
	public SmsMessageBatch createBatch(String originalText, int msgSize, String sessionId)
	{
		MessageBatchFactory factory = new MessageBatchFactory(msgSize);
		return factory.createMessageBatch(sessionId, new SmsEndpoint("1234"), "M", sessionId.substring(0,5), originalText);
	}

	@Test
	public void ShouldAddReceivedPayloadWhenReceivingSingleMessageBatch()
	{

		SmsMessageBatch batch = creatBatch(100);

		SmsReceiver receiver = new SmsReceiver();
		receiver.receive("sms:123", batch.getMessage(0));
		
		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
	}

	@Test
	public void ShouldAcceptMultiMessageBatchesAndReconstitutePayload()
	{
		String txt = "aaabbbcccdd";
		SmsMessageBatch batch = createBatch(txt, 3);
		
		Assert.assertEquals(txt, batch.getPayload());
		Assert.assertEquals(4, batch.getMessagesCount());
		
		String new0 = batch.getMessage(0).getText();
		String new1 = batch.getMessage(1).getText();
		String new2 = batch.getMessage(2).getText();
		String new3 = batch.getMessage(3).getText();
		
		String newTxt = new0.substring(MessageFormatter.getBatchHeaderLenght(), new0.length())
			+ new1.substring(MessageFormatter.getBatchHeaderLenght(), new1.length())
			+ new2.substring(MessageFormatter.getBatchHeaderLenght(), new2.length())
			+ new3.substring(MessageFormatter.getBatchHeaderLenght(), new3.length());
		
		Assert.assertEquals(txt, newTxt);
				
		SmsReceiver receiver = new SmsReceiver();
				
		receiver.receive("sms:123", batch.getMessage(0));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());

		receiver.receive("sms:123", batch.getMessage(1));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());
		
		receiver.receive("sms:123", batch.getMessage(2));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());

		receiver.receive("sms:123", batch.getMessage(3));
		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
		
		Assert.assertEquals(batch.getPayload(), receiver.getCompletedBatch(batch.getId()).getPayload());

	}

	@Test
	public void ShouldAcceptVeyLargeMessageBatchesAndReconstitutePayload()
	{
		SmsMessageBatch batch = creatBatch(140 * 9);  // 998 * 139
		SmsReceiver receiver = new SmsReceiver();

		for(SmsMessage msg : batch.getMessages())
		{
			receiver.receive("sms:123", msg);

		}

		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
		Assert.assertEquals(batch.getPayload(), receiver.getCompletedBatch(batch.getId()).getPayload());

	}

	@Test
	public void ShouldAcceptMultiMessagesOutOfOrder()
	{
		SmsMessageBatch originalbatch = creatBatch(200);
		SmsReceiver receiver = new SmsReceiver();

		receiver.receive("sms:123", originalbatch.getMessage(1));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());

		receiver.receive("sms:123", originalbatch.getMessage(0));
		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
		Assert.assertEquals(originalbatch.getPayload(), receiver.getCompletedBatch(originalbatch.getId()).getPayload());

	}

	@Test
	public void ShouldListOngoingBatches()
	{
		SmsMessageBatch batch = creatBatch(200);
		SmsReceiver receiver = new SmsReceiver();

		receiver.receive("sms:123", batch.getMessage(0));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());


		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(batch.getId(), receiver.getFirstOngoingBatch().getId());

	}



	//should know when message batch started and ended
	@Test
	public void ShouldKnowWhenFirstMessageAndLastMessageOfBatchReceived()
	{
		SmsMessageBatch originalbatch = creatBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		SmsMessage message1 = originalbatch.getMessage(0);
		SmsMessage message2 = originalbatch.getMessage(2);

		Date first = new Date();
		Date second = TestHelper.nowAddHours(2);

		message1.setLastModificationDate(second);
		message2.setLastModificationDate(first);


		receiver
			.receive("sms:123", message2)
			.receive("sms:123", message1);

		Assert.assertEquals(first, receiver.getOngoingBatch(originalbatch.getId()).getDateTimeFirstMessage());
		Assert.assertEquals(second, receiver.getOngoingBatch(originalbatch.getId()).getDateTimeLastMessage());

	}

	@Test
	public void ShouldAcceptDuplicateMessages()
	{
		SmsMessageBatch originalbatch = creatBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		SmsMessage msg = originalbatch.getMessage(3);

		receiver.receive("sms:123", msg);

		SmsMessageBatch ongoing = receiver.getOngoingBatch(originalbatch.getId());

		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(originalbatch.getMessagesCount(), ongoing.getExpectedMessageCount());
		Assert.assertNotNull(ongoing.getMessage(3));

		receiver
			.receive("sms:123", msg)
			.receive("sms:123", msg);

		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(originalbatch.getMessagesCount(), ongoing.getExpectedMessageCount());
		Assert.assertNotNull(ongoing.getMessage(3));

	}
	@Test
	public void ShouldDiscardBatchOnDuplicateIdButDifferentPayloadMessages()
	{
		SmsMessageBatch originalbatch = creatBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		SmsMessage msg = originalbatch.getMessage(3);

		SmsMessageBatch ongoing = receiver
			.receive("sms:123", msg)
			.getOngoingBatch(originalbatch.getId());

		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(originalbatch.getMessagesCount(), ongoing.getExpectedMessageCount());
		Assert.assertNotNull(ongoing.getMessage(3));

		SmsMessage msg2 = new SmsMessage(msg.getText(), msg.getLastModificationDate());

		msg2.setText(msg2.getText().substring(0, msg2.getText().length() - 2) + "**");

		receiver.receive("sms:123", msg2);

		Assert.assertEquals(0, receiver.getOngoingBatchesCount());
		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));

	}

	@Test
	public void ShouldDiscardMessagesOfDiscardedBatches()
	{
		SmsMessageBatch originalbatch = creatBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		SmsMessage msg = originalbatch.getMessage(3);

		SmsMessageBatch ongoing = receiver
			.receive("sms:123", msg)
			.getOngoingBatch(originalbatch.getId());

		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(originalbatch.getMessagesCount(), ongoing.getExpectedMessageCount());
		Assert.assertNotNull(ongoing.getMessage(3));

		SmsMessage msg2 = new SmsMessage(msg.getText(), msg.getLastModificationDate());

		msg2.setText(msg2.getText().substring(0, msg2.getText().length() - 2) + "**");

		receiver.receive("sms:123", msg2);

		Assert.assertEquals(0, receiver.getOngoingBatchesCount());
		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));

		receiver.receive("sms:123", originalbatch.getMessage(4));

		Assert.assertEquals(0, receiver.getOngoingBatchesCount());
		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));

	}

	@Test
	public void ShouldExposeReasonForDiscarded()
	{
		SmsMessageBatch originalbatch = creatBatch(1000);
		SmsReceiver receiver = new SmsReceiver();
		
		DiscardedBatchException exc = new DiscardedBatchException("Foo");
		
		receiver
			.receive("sms:123", originalbatch.getMessage(3))
			.discardBatch(originalbatch.getId(), exc);

		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));
		Assert.assertSame(receiver.getDiscardedBatch(originalbatch.getId()).getReason(), exc);
	}


	@Test
	public void ShoulHaveNullForDefaultReasonForDiscarded()
	{
		SmsMessageBatch originalbatch = creatBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		receiver
			.receive("sms:123", originalbatch.getMessage(3))
			.discardBatch(originalbatch.getId());

		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));
		Assert.assertNull(receiver.getDiscardedBatch(originalbatch.getId()).getReason());
	}


	//scneario - you complete a batch and a dupe comes in afterwards
	@Test
	public void ShouldDiscardMessagesOfCompeltedBatches()
	{

		SmsMessageBatch originalbatch = creatBatch(200);
		SmsReceiver receiver = new SmsReceiver();

		receiver
			.receive("sms:123", originalbatch.getMessage(0))
			.receive("sms:123", originalbatch.getMessage(1));

		Assert.assertNotNull(receiver.getCompletedBatch(originalbatch.getId()));
		Assert.assertEquals(0, receiver.getOngoingBatchesCount());

		receiver.receive("sms:123", originalbatch.getMessage(1));

		Assert.assertNotNull(receiver.getCompletedBatch(originalbatch.getId()));
		Assert.assertEquals(0, receiver.getOngoingBatchesCount());
		Assert.assertEquals(0, receiver.getDiscardedBatchesCount());

	}

	@Test
	public void ShouldNotifyToBachReceiverWhenBatchIsCompleted()
	{
		MockSmsChannel batchReceiver = new MockSmsChannel();
		
		SmsMessageBatch batch = creatBatch(100);

		SmsReceiver receiver = new SmsReceiver();
		receiver.setBatchReceiver(batchReceiver);
		
		receiver.receive("sms:123", batch.getMessage(0));
		
		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
		Assert.assertEquals(1, batchReceiver.getIncommingBatches().size());
		Assert.assertEquals(batch.getId(), batchReceiver.getIncommingBatches().get(0).getId());
		Assert.assertEquals(batch.getExpectedMessageCount(), batchReceiver.getIncommingBatches().get(0).getExpectedMessageCount());
		Assert.assertEquals(batch.getPayload(), batchReceiver.getIncommingBatches().get(0).getPayload());
		
	}

	
	@Test
	public void ShouldNotNotifyToBachReceiverWhenBatchIsNotCompleted()
	{
		MockSmsChannel batchReceiver = new MockSmsChannel();
		
		SmsMessageBatch batch = createBatch("12345678901234567890", 10);

		SmsReceiver receiver = new SmsReceiver();
		receiver.setBatchReceiver(batchReceiver);
		
		receiver.receive("sms:123", batch.getMessage(0));
		
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());
		Assert.assertEquals(0, batchReceiver.getIncommingBatches().size());
	}

	
	@Test
	public void ShouldNotifyACKWhenReceiveFirstMessageOfBatch()
	{
		MockSmsChannel batchReceiver = new MockSmsChannel();
		
		SmsMessageBatch batch = createBatch("12345678901234567890", 10);

		SmsReceiver receiver = new SmsReceiver();
		receiver.setBatchReceiver(batchReceiver);

		receiver.receive("sms:123", batch.getMessage(0));
		
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());
		Assert.assertEquals(1, batchReceiver.getBatchACKs().size());
	}
	
	@Test
	public void shouldGetCompletedBatchesForSyncSession(){
		
		String sessionId = IdGenerator.INSTANCE.newID();
		int version = 1;
		
		SmsReceiver smsReceiver = new SmsReceiver();
		
		SmsMessageBatch batch = createBatch("hdxjsadjdksnakdnksandk", 10, IdGenerator.INSTANCE.newID());
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 10, IdGenerator.INSTANCE.newID());
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 10, IdGenerator.INSTANCE.newID());
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 10, IdGenerator.INSTANCE.newID());
		smsReceiver.receive("2345", batch.getMessage(0));
			
		batch = createBatch("hdxjsadjdksnakdnksandk", 100, sessionId);
		smsReceiver.receive("2345", batch.getMessage(0));
		SmsMessageBatch batch2 = createBatch("hdxjsadjdksnakdnksandk", 100, sessionId);
		smsReceiver.receive("2345", batch2.getMessage(0));

		List<SmsMessageBatch> result = smsReceiver.getCompletedBatches(sessionId, version);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertFalse(result.get(1).getId().equals(result.get(0).getId()));
		
		Assert.assertTrue(batch.getId().equals(result.get(1).getId()) || batch.getId().equals(result.get(0).getId()));
		Assert.assertTrue(batch2.getId().equals(result.get(1).getId()) || batch2.getId().equals(result.get(0).getId()));
	}
	
	@Test
	public void shouldGetOngoingBatchesForSyncSession(){
		
		String sessionId = IdGenerator.INSTANCE.newID();
		int version = 1;
		
		SmsReceiver smsReceiver = new SmsReceiver();
		
		SmsMessageBatch batch = createBatch("hdxjsadjdksnakdnksandk", 10, IdGenerator.INSTANCE.newID());
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 10, IdGenerator.INSTANCE.newID());
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 10, IdGenerator.INSTANCE.newID());
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 10, IdGenerator.INSTANCE.newID());
		smsReceiver.receive("2345", batch.getMessage(0));
			
		batch = createBatch("hdxjsadjdksnakdnksandk", 5, sessionId);
		smsReceiver.receive("2345", batch.getMessage(0));
		SmsMessageBatch batch2 = createBatch("hdxjsadjdksnakdnksandk", 5, sessionId);
		smsReceiver.receive("2345", batch2.getMessage(0));
		
		List<SmsMessageBatch> result = smsReceiver.getOngoingBatches(sessionId, version);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		Assert.assertFalse(result.get(1).getId().equals(result.get(0).getId()));
		
		Assert.assertTrue(batch.getId().equals(result.get(1).getId()) || batch.getId().equals(result.get(0).getId()));
		Assert.assertTrue(batch2.getId().equals(result.get(1).getId()) || batch2.getId().equals(result.get(0).getId()));
	}

	@Test
	public void shouldPurgeBatches(){
		String sessionId = IdGenerator.INSTANCE.newID();
		int version = 1;
		
		SmsReceiver smsReceiver = new SmsReceiver();
		
		SmsMessageBatch batch = createBatch("hdxjsadjdksnakdnksandk", 10, sessionId);
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 10, sessionId);
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 100, sessionId);
		smsReceiver.receive("2345", batch.getMessage(0));
		batch = createBatch("hdxjsadjdksnakdnksandk", 100, sessionId);
		smsReceiver.receive("2345", batch.getMessage(0));
			
		List<SmsMessageBatch> result = smsReceiver.getOngoingBatches(sessionId, version);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		result = smsReceiver.getCompletedBatches(sessionId, version);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		
		smsReceiver.purgeBatches(sessionId, version);
		
		result = smsReceiver.getOngoingBatches(sessionId, version);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
		result = smsReceiver.getCompletedBatches(sessionId, version);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}
}
