package com.mesh4j.sync.message.channel.sms;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.test.utils.TestHelper;

public class MessageBatchFactoryTests {
	
	//can provide block of text and create set of messages
	//each message does not exceed message length
	//default message length is 140
	
	@Test
	public void ShouldCreateCompleteMessageBatchFromText()
	{
		MessageBatchFactory factory = new MessageBatchFactory();
		SmsMessageBatch batch = factory.createMessageBatch("M", TestHelper.newText(200));

		Assert.assertEquals(2, batch.getMessagesCount());
		Assert.assertTrue(batch.isComplete());

		int expectedLength = 140 + MessageFormatter.getBatchHeaderLenght();
		int i = 0;
		for(SmsMessage msg : batch.getMessages())
		{
			int textLength = msg.getText().length();
			Assert.assertTrue(textLength <= expectedLength);
			Assert.assertEquals(i, MessageFormatter.getBatchMessageSequenceNumber(msg.getText()));
			i++;
		}

	}

	@Test
	public void ShouldReconstituteMessagesFromBatch()
	{
		MessageBatchFactory factory = new MessageBatchFactory();
		String original = TestHelper.newText(200);
		SmsMessageBatch batch = factory.createMessageBatch("M", original);

		Assert.assertTrue(batch.isComplete());
		batch.reconstitutePayload();
		String reconstituted = batch.getPayload();

		Assert.assertEquals(original, reconstituted);
	}

	@Test
	public void ShouldGenerateImprobableIdWhenCreatingBatch()
	{
		SmsMessageBatch batch1 = new SmsMessageBatch();
		SmsMessageBatch batch2 = new SmsMessageBatch();

		Assert.assertFalse(batch1.getId().equals(batch2.getId()));
	}


	//can change message length limit
	//each message has batch ID but does not need length

}
