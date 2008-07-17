package com.mesh4j.sync.message.channel.sms.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mesh4j.sync.message.channel.sms.ISmsChannel;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import com.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.Guard;

public class AskLossMessagesScheduleTask extends ScheduleTimerTask {

	private final static Log LOGGER = LogFactory.getLog(AskLossMessagesScheduleTask.class);
	
	// MODEL
	private ISmsChannel channel;
	private int delay;
	
	// BUSINESS METHODS
	public AskLossMessagesScheduleTask(ISmsChannel channel, int receiverDelay) {
		super("AskLossMessagesScheduleTask"+IdGenerator.newID());
		
		Guard.argumentNotNull(channel, "channel");
		this.delay = receiverDelay;
		this.channel = channel;
	}

	@Override
	protected void execute() {
		try{
			long nowTime = System.currentTimeMillis();
			long min = nowTime - delay;
			
			for (SmsMessageBatch batch : this.channel.getIncommingBatches()) {
				if(batch.getDateTimeLastMessage().getTime() < min){
					try{
						this.channel.sendAskForRetry(batch);
					} catch (RuntimeException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		} catch (RuntimeException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
