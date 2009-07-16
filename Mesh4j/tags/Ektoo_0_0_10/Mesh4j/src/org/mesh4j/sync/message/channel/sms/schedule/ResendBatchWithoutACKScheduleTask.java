package org.mesh4j.sync.message.channel.sms.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import org.mesh4j.sync.validations.Guard;


public class ResendBatchWithoutACKScheduleTask extends ScheduleTimerTask {

	private final static Log LOGGER = LogFactory.getLog(ResendBatchWithoutACKScheduleTask.class);
	
	// MODEL
	private ISmsChannel channel;
	private int delay;
	
	// BUSINESS METHODS
	public ResendBatchWithoutACKScheduleTask(ISmsChannel channel, int delay) {
		super("ResendBatchWithoutACKScheduleTask" + IdGenerator.INSTANCE.newID());
		
		Guard.argumentNotNull(channel, "channel");
		this.channel = channel;
		this.delay = delay;
	}

	@Override
	public void execute() {
		try{
			long nowTime = System.currentTimeMillis();
			long min = nowTime - delay;
			
			for (SmsMessageBatch batch : this.channel.getOutcommingBatches()) {
				if(batch.getDateTimeLastMessage().getTime() < min){
					try{
						this.channel.resend(batch);
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
