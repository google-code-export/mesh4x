package com.mesh4j.sync.message.schedule.timer;

import java.util.TimerTask;

import com.mesh4j.sync.message.schedule.IScheduleTask;
import com.mesh4j.sync.validations.Guard;

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

	public abstract void run();
}
