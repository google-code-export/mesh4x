package org.mesh4j.sync.message.channel.sms.schedule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.MessageBatchFactory;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.channel.sms.core.MockSmsChannel;
import org.mesh4j.sync.message.schedule.timer.TimerScheduler;
import org.mesh4j.sync.test.utils.TestHelper;


public class AskLossMessagesScheduleTaskTests {
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenChannelIsNull(){
		new AskLossMessagesScheduleTask(null, 50);
	}
	
	@Test
	public void shouldDoNotSendRetryWhenDelayIsGreatherThanMessageLastMofificationDate() throws InterruptedException{
		
		Date date = TestHelper.nowAddDays(4);
		SmsMessageBatch batch = this.createTestBatch(10, "012345678901234567890123456789", date);
		MockSmsChannel channel = new MockSmsChannel();
		channel.receive(batch);
		
		AskLossMessagesScheduleTask task = new AskLossMessagesScheduleTask(channel, 50);
		
		TimerScheduler.INSTANCE.reset();
		TimerScheduler.INSTANCE.schedule(task, 50);
		Thread.sleep(300);
		
		Assert.assertEquals(0, channel.getRetries().size());
	}

	@Test
	public void shouldSendRetryWhenDelayIsLessThanMessageLastMofificationDate() throws InterruptedException{
		Date date = TestHelper.now();
		SmsMessageBatch batch = this.createTestBatch(10, "012345678901234567890123456789", date);
		MockSmsChannel channel = new MockSmsChannel();
		channel.receive(batch);
		
		AskLossMessagesScheduleTask task = new AskLossMessagesScheduleTask(channel, 50);
		
		TimerScheduler.INSTANCE.reset();
		TimerScheduler.INSTANCE.schedule(task, 50);
		Thread.sleep(800);
		
		Assert.assertEquals(15, channel.getRetries().size());
	}
	
	public SmsMessageBatch createTestBatch(int msgSize, String originalText, Date date)
	{
		MessageBatchFactory factory = new MessageBatchFactory(msgSize);
		SmsMessageBatch batch = factory.createMessageBatch(IdGenerator.INSTANCE.newID(), new SmsEndpoint("1234"), "M", "12345", originalText);
		for (SmsMessage message : batch.getMessages()) {
			message.setLastModificationDate(date);
		}
		return batch;
	}

}
