package com.mesh4j.sync.message.channel.sms.schedule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.batch.MessageBatchFactory;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import com.mesh4j.sync.message.channel.sms.core.MockSmsChannel;
import com.mesh4j.sync.message.schedule.timer.TimerScheduler;
import com.mesh4j.sync.test.utils.TestHelper;

public class ResendBatchWithoutACKScheduleTaskTests {
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateFailsWhenChannelIsNull(){
		new ResendBatchWithoutACKScheduleTask(null, 50);
	}
	
	@Test
	public void shouldDoNotSendRetryWhenDelayIsGreatherThanMessageLastMofificationDate() throws InterruptedException{
		
		Date date = TestHelper.nowAddDays(4);
		SmsMessageBatch batch = this.createTestBatch(10, "012345678901234567890123456789", date);
		MockSmsChannel channel = new MockSmsChannel();
		channel.send(batch, true);
		
		ResendBatchWithoutACKScheduleTask task = new ResendBatchWithoutACKScheduleTask(channel, 50);
		
		TimerScheduler.INSTANCE.reset();
		TimerScheduler.INSTANCE.schedule(task, 50);
		Thread.sleep(300);
		
		Assert.assertEquals(0, channel.getResend().size());
	}

	@Test
	public void shouldSendRetryWhenDelayIsLessThanMessageLastMofificationDate() throws InterruptedException{
		Date date = TestHelper.now();
		SmsMessageBatch batch = this.createTestBatch(10, "012345678901234567890123456789", date);
		MockSmsChannel channel = new MockSmsChannel();
		channel.send(batch, true);
		
		ResendBatchWithoutACKScheduleTask task = new ResendBatchWithoutACKScheduleTask(channel, 70);
		
		TimerScheduler.INSTANCE.reset();
		TimerScheduler.INSTANCE.schedule(task, 50);
		Thread.sleep(300);
		
		Assert.assertEquals(4, channel.getResend().size());
	}
	
	@Test
	public void shouldDoNotSendRetryWhenAckIsNotRequired() throws InterruptedException{
		Date date = TestHelper.now();
		SmsMessageBatch batch = this.createTestBatch(10, "012345678901234567890123456789", date);
		MockSmsChannel channel = new MockSmsChannel();
		channel.send(batch, false);
		
		ResendBatchWithoutACKScheduleTask task = new ResendBatchWithoutACKScheduleTask(channel, 50);
		
		TimerScheduler.INSTANCE.reset();
		TimerScheduler.INSTANCE.schedule(task, 50);
		Thread.sleep(300);
		
		Assert.assertEquals(0, channel.getResend().size());
	}
	
	public SmsMessageBatch createTestBatch(int msgSize, String originalText, Date date)
	{
		MessageBatchFactory factory = new MessageBatchFactory(msgSize);
		SmsMessageBatch batch = factory.createMessageBatch(new SmsEndpoint("1234"), "M", "12345", originalText);
		for (SmsMessage message : batch.getMessages()) {
			message.setLastModificationDate(date);
		}
		return batch;
	}
}
