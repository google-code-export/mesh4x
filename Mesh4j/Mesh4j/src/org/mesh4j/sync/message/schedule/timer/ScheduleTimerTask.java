package org.mesh4j.sync.message.schedule.timer;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.message.schedule.IScheduleTask;
import org.mesh4j.sync.validations.Guard;


public abstract class ScheduleTimerTask extends TimerTask implements IScheduleTask {

	private final static Log LOGGER = LogFactory.getLog(ScheduleTimerTask.class);
	
	// MODEL 
	private String scheduleTaskId;
	
	// BUSINESS METHODS
	private ScheduleTimerTask(){
		super();
	}
	
	public ScheduleTimerTask(String scheduleTaskId) {
		this();
		
		Guard.argumentNotNullOrEmptyString(scheduleTaskId, "scheduleTaskId");
		this.scheduleTaskId = scheduleTaskId;
	}
	
	public String getScheduleTaskId() {
		return scheduleTaskId;
	}

	public void run(){
		try{
			this.execute();
		}catch(RuntimeException e){
			LOGGER.error(e.getMessage(), e);
		}
	}

	protected abstract void execute();
}
