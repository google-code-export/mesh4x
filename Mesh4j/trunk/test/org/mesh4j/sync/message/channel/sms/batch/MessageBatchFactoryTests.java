package org.mesh4j.sync.message.channel.sms.batch;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.MessageFormatter;
import org.mesh4j.sync.test.utils.TestHelper;


public class MessageBatchFactoryTests {
	
	@Test
	public void ShouldCreateCompleteMessageBatchFromText()
	{
		MessageBatchFactory factory = new MessageBatchFactory();
		SmsMessageBatch batch = factory.createMessageBatch("123", new SmsEndpoint("1234"), "M", "12345", TestHelper.newText(200));

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
		SmsMessageBatch batch = factory.createMessageBatch(IdGenerator.INSTANCE.newID(), new SmsEndpoint("1234"),"M", "12345", original);

		Assert.assertTrue(batch.isComplete());
		batch.reconstitutePayload();
		String reconstituted = batch.getPayload();

		Assert.assertEquals(original, reconstituted);
	}

	@Test
	public void ShouldGenerateImprobableIdWhenCreatingBatch()
	{
		String sessionId = IdGenerator.INSTANCE.newID();
		SmsMessageBatch batch1 = new SmsMessageBatch(sessionId, new SmsEndpoint("1234"));
		SmsMessageBatch batch2 = new SmsMessageBatch(sessionId, new SmsEndpoint("1234"));

		Assert.assertFalse(batch1.getId().equals(batch2.getId()));
	}

}
