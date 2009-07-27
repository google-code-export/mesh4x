package org.mesh4j.sync.message.schedule.timer;

import java.util.TimerTask;

import org.mesh4j.sync.message.schedule.IScheduleTask;
import org.mesh4j.sync.validations.Guard;


public abstract class ScheduleTimerTask extends TimerTask implements IScheduleTask {

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
			e.printStackTrace();
		}
	}

	protected abstract void execute();
}
