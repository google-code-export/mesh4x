package com.mesh4j.sync.message.channel.sms.schedule;

import com.mesh4j.sync.message.channel.sms.ISmsChannel;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import com.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.Guard;

public class ResendBatchWithoutACKScheduleTask extends ScheduleTimerTask {

	// MODEL
	private ISmsChannel channel;
	private int delay;
	
	// BUSINESS METHODS
	public ResendBatchWithoutACKScheduleTask(ISmsChannel channel, int delay) {
		super("ResendBatchWithoutACKScheduleTask"+IdGenerator.newID());
		
		Guard.argumentNotNull(channel, "channel");
		this.channel = channel;
		this.delay = delay;
	}

	@Override
	public void execute() {
		long nowTime = System.currentTimeMillis();
		long min = nowTime - delay;
		
		for (SmsMessageBatch batch : this.channel.getOutcommingBatches()) {
			if(batch.getDateTimeLastMessage().getTime() < min){
				this.channel.resend(batch);
			}
		}
	}

}
