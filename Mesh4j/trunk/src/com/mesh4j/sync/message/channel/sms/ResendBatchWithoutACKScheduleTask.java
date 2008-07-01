package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import com.mesh4j.sync.utils.IdGenerator;

public class ResendBatchWithoutACKScheduleTask extends ScheduleTimerTask {

	// MODEL
	private SmsSender sender;
	private int delay;
	
	// BUSINESS METHODS
	public ResendBatchWithoutACKScheduleTask(SmsSender sender, int delay) {
		super("ResendBatchWithoutACKScheduleTask"+IdGenerator.newID());
		this.sender = sender;
		this.delay = delay;
	}

	@Override
	public void run() {
		long nowTime = System.currentTimeMillis();
		long min = nowTime - delay;
		
		for (SmsMessageBatch ongoingBatch : this.sender.getOngoingBatches()) {
			if(ongoingBatch.getDateTimeLastMessage().getTime() < min){
				this.sender.send(ongoingBatch, false);
			}
		}
	}

}
