package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import com.mesh4j.sync.utils.IdGenerator;

public class AskLossMessagesScheduleTask extends ScheduleTimerTask {

	// MODEL
	private SmsReceiver receiver;
	private SmsChannel channel;
	private int delay;
	
	// BUSINESS METHODS
	public AskLossMessagesScheduleTask(SmsChannel channel, SmsReceiver receiver, int receiverDelay) {
		super("AskLossMessagesScheduleTask"+IdGenerator.newID());
		this.receiver = receiver;
		this.delay = receiverDelay;
		this.channel = channel;
	}

	@Override
	public void run() {
		long nowTime = System.currentTimeMillis();
		long min = nowTime - delay;
		
		for (SmsMessageBatch ongoingBatch : this.receiver.getOngoingBatches()) {
			if(ongoingBatch.getDateTimeLastMessage().getTime() < min){				
				this.channel.sendAskRetry(ongoingBatch);
			}
		}
	}
}
