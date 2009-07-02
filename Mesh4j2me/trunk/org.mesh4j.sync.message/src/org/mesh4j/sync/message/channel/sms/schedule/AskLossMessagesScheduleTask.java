package org.mesh4j.sync.message.channel.sms.schedule;

import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import org.mesh4j.sync.validations.Guard;

public class AskLossMessagesScheduleTask extends ScheduleTimerTask {
	
	public static final String TASK_ID = "AskLossMessagesScheduleTask";
	
	// MODEL
	private ISmsChannel channel;
	private int delay;
	
	// BUSINESS METHODS
	public AskLossMessagesScheduleTask(ISmsChannel channel, int receiverDelay) {
		super(TASK_ID);
		
		Guard.argumentNotNull(channel, "channel");
		this.delay = receiverDelay;
		this.channel = channel;
	}

	protected void execute() {
		try{
			long nowTime = System.currentTimeMillis();
			long min = nowTime - delay;
			this.channel.sendAskForRetryIncompleteIncommingBatches(min);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public ISmsChannel getChannel() {
		return this.channel;
	}
}
