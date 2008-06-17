package com.mesh4j.sync.message.channel.sms;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.test.utils.TestHelper;

public class SmsMessageReceiverTests {
	
	public SmsMessageBatch createTestBatch(int originalTextlength)
	{
		MessageBatchFactory factory = new MessageBatchFactory();
		return factory.createMessageBatch("M1", TestHelper.newText(originalTextlength));
	}
	
	public SmsMessageBatch createTestBatch(int msgSize, String originalText)
	{
		MessageBatchFactory factory = new MessageBatchFactory(msgSize);
		return factory.createMessageBatch("M1", originalText);
	}

	@Test
	public void ShouldAddReceivedPayloadWhenReceivingSingleMessageBatch()
	{

		SmsMessageBatch batch = createTestBatch(100);

		SmsReceiver receiver = new SmsReceiver();
		receiver.receive(batch.getMessage(0));
		
		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
	}

	@Test
	public void ShouldAcceptMultiMessageBatchesAndReconstitutePayload()
	{
		String txt = "aaabbbcccdd";
		SmsMessageBatch batch = createTestBatch(3, txt);
		
		Assert.assertEquals(txt, batch.getPayload());
		Assert.assertEquals(4, batch.getMessagesCount());
		
		String new0 = batch.getMessage(0).getText();
		String new1 = batch.getMessage(1).getText();
		String new2 = batch.getMessage(2).getText();
		String new3 = batch.getMessage(3).getText();
		
		String newTxt =  new0.substring(MessageFormatter.getBatchHeaderLenght(), new0.length())
			+ new1.substring(MessageFormatter.getBatchHeaderLenght(), new1.length())
			+ new2.substring(MessageFormatter.getBatchHeaderLenght(), new2.length())
			+ new3.substring(MessageFormatter.getBatchHeaderLenght(), new3.length());
		
		Assert.assertEquals(txt, newTxt);
				
		SmsReceiver receiver = new SmsReceiver();

				
		receiver.receive(batch.getMessage(0));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());

		receiver.receive(batch.getMessage(1));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());
		
		receiver.receive(batch.getMessage(2));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());

		receiver.receive(batch.getMessage(3));
		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
		
		Assert.assertEquals(batch.getPayload(), receiver.getCompletedBatch(batch.getId()).getPayload());

	}

	@Test
	public void ShouldAcceptVeyLargeMessageBatchesAndReconstitutePayload()
	{
		SmsMessageBatch batch = createTestBatch(998 * 139);
		SmsReceiver receiver = new SmsReceiver();

		for(SmsMessage msg : batch.getMessages())
		{
			receiver.receive(msg);

		}

		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
		Assert.assertEquals(batch.getPayload(), receiver.getCompletedBatch(batch.getId()).getPayload());

	}

	@Test
	public void ShouldAcceptMultiMessagesOutOfOrder()
	{
		SmsMessageBatch originalbatch = createTestBatch(200);
		SmsReceiver receiver = new SmsReceiver();

		receiver.receive(originalbatch.getMessage(1));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());

		receiver.receive(originalbatch.getMessage(0));
		Assert.assertEquals(1, receiver.getCompletedBatchesCount());
		Assert.assertEquals(originalbatch.getPayload(), receiver.getCompletedBatch(originalbatch.getId()).getPayload());

	}

	@Test
	public void ShouldListOngoingBatches()
	{
		SmsMessageBatch batch = createTestBatch(200);
		SmsReceiver receiver = new SmsReceiver();

		receiver.receive(batch.getMessage(0));
		Assert.assertEquals(0, receiver.getCompletedBatchesCount());


		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(batch.getId(), receiver.getFirstOngoingBatch().getId());

	}



	//should know when message batch started and ended
	@Test
	public void ShouldKnowWhenFirstMessageAndLastMessageOfBatchReceived()
	{
		SmsMessageBatch originalbatch = createTestBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		SmsMessage message1 = originalbatch.getMessage(0);
		SmsMessage message2 = originalbatch.getMessage(2);

		Date first = new Date();
		Date second = TestHelper.nowAddHours(2);

		message1.setReceived(second);
		message2.setReceived(first);


		receiver
			.receive(message2)
			.receive(message1);

		Assert.assertEquals(first, receiver.getOngoingBatch(originalbatch.getId()).getDateTimeFirstMessageReceived());
		Assert.assertEquals(second, receiver.getOngoingBatch(originalbatch.getId()).getDateTimeLastMessageReceived());

	}

	@Test
	public void ShouldAcceptDuplicateMessages()
	{
		SmsMessageBatch originalbatch = createTestBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		SmsMessage msg = originalbatch.getMessage(3);

		receiver.receive(msg);

		SmsMessageBatch ongoing = receiver.getOngoingBatch(originalbatch.getId());

		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(originalbatch.getMessagesCount(), ongoing.getExpectedMessageCount());
		Assert.assertNotNull(ongoing.getMessage(3));

		receiver
			.receive(msg)
			.receive(msg);

		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(originalbatch.getMessagesCount(), ongoing.getExpectedMessageCount());
		Assert.assertNotNull(ongoing.getMessage(3));

	}
	@Test
	public void ShouldDiscardBatchOnDuplicateIdButDifferentPayloadMessages()
	{
		SmsMessageBatch originalbatch = createTestBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		SmsMessage msg = originalbatch.getMessage(3);

		SmsMessageBatch ongoing = receiver
			.receive(msg)
			.getOngoingBatch(originalbatch.getId());

		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(originalbatch.getMessagesCount(), ongoing.getExpectedMessageCount());
		Assert.assertNotNull(ongoing.getMessage(3));

		SmsMessage msg2 = new SmsMessage(msg.getText(), msg.getReceived());

		msg2.setText(msg2.getText().substring(0, msg2.getText().length() - 2) + "**");

		receiver.receive(msg2);

		Assert.assertEquals(0, receiver.getOngoingBatchesCount());
		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));

	}

	@Test
	public void ShouldDiscardMessagesOfDiscardedBatches()
	{
		SmsMessageBatch originalbatch = createTestBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		SmsMessage msg = originalbatch.getMessage(3);

		SmsMessageBatch ongoing = receiver
			.receive(msg)
			.getOngoingBatch(originalbatch.getId());

		Assert.assertEquals(1, receiver.getOngoingBatchesCount());
		Assert.assertEquals(originalbatch.getMessagesCount(), ongoing.getExpectedMessageCount());
		Assert.assertNotNull(ongoing.getMessage(3));

		SmsMessage msg2 = new SmsMessage(msg.getText(), msg.getReceived());

		msg2.setText(msg2.getText().substring(0, msg2.getText().length() - 2) + "**");

		receiver.receive(msg2);

		Assert.assertEquals(0, receiver.getOngoingBatchesCount());
		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));

		receiver.receive(originalbatch.getMessage(4));

		Assert.assertEquals(0, receiver.getOngoingBatchesCount());
		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));

	}

	@Test
	public void ShouldExposeReasonForDiscarded()
	{
		SmsMessageBatch originalbatch = createTestBatch(1000);
		SmsReceiver receiver = new SmsReceiver();
		
		DiscardedBatchException exc = new DiscardedBatchException("Foo");
		
		receiver
			.receive(originalbatch.getMessage(3))
			.discardBatch(originalbatch.getId(), exc);

		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));
		Assert.assertSame(receiver.getDiscardedBatch(originalbatch.getId()).getReason(), exc);
	}


	@Test
	public void ShoulHaveNullForDefaultReasonForDiscarded()
	{
		SmsMessageBatch originalbatch = createTestBatch(1000);
		SmsReceiver receiver = new SmsReceiver();

		receiver
			.receive(originalbatch.getMessage(3))
			.discardBatch(originalbatch.getId());

		Assert.assertNotNull(receiver.getDiscardedBatch(originalbatch.getId()));
		Assert.assertNull(receiver.getDiscardedBatch(originalbatch.getId()).getReason());
	}


	//scneario - you complete a batch and a dupe comes in afterwards
	@Test
	public void ShouldDiscardMessagesOfCompeltedBatches()
	{

		SmsMessageBatch originalbatch = createTestBatch(200);
		SmsReceiver receiver = new SmsReceiver();

		receiver
			.receive(originalbatch.getMessage(0))
			.receive(originalbatch.getMessage(1));

		Assert.assertNotNull(receiver.getCompletedBatch(originalbatch.getId()));
		Assert.assertEquals(0, receiver.getOngoingBatchesCount());

		receiver.receive(originalbatch.getMessage(1));

		Assert.assertNotNull(receiver.getCompletedBatch(originalbatch.getId()));
		Assert.assertEquals(0, receiver.getOngoingBatchesCount());
		Assert.assertEquals(0, receiver.getDiscardedBatchesCount());

	}



	//batchfromdifferentnumbersdontcollide
	//get notification on batch job completion
	//can ask for resends for an incomplete batch jobs
	//can cancel an ongoing batch job and gets logged
	//can get dupe messages
}
