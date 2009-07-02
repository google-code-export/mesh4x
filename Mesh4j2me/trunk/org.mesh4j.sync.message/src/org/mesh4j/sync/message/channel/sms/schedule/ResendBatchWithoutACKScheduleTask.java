package org.mesh4j.sync.message.channel.sms.schedule;

import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import org.mesh4j.sync.validations.Guard;

public class ResendBatchWithoutACKScheduleTask extends ScheduleTimerTask {

	public final static String TASK_ID = "ResendBatchWithoutACKScheduleTask";
	
	// MODEL
	private ISmsChannel channel;
	private int delay;
	
	// BUSINESS METHODS
	public ResendBatchWithoutACKScheduleTask(ISmsChannel channel, int delay) {
		super(TASK_ID);
		
		Guard.argumentNotNull(channel, "channel");
		this.channel = channel;
		this.delay = delay;
	}

	public void execute() {
		try{
			long nowTime = System.currentTimeMillis();
			long min = nowTime - delay;
 
			this.channel.resendPendingACKOutcommingBatches(min);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public ISmsChannel getChannel() {
		return this.channel;
	}

}
