package com.mesh4j.sync.message.schedule.timer;

import java.util.ArrayList;
import java.util.Timer;

import com.mesh4j.sync.message.schedule.IScheduler;

public class TimerScheduler implements IScheduler<ScheduleTimerTask>{

	public final static TimerScheduler INSTANCE = new TimerScheduler();
	
	// MODEL VARIABLES
	private Timer timer = new Timer("TimerScheduler", true);
	private ArrayList<ScheduleTimerTask> tasks = new ArrayList<ScheduleTimerTask>();

	// BUSINESS METHODS
	private TimerScheduler(){
		super();
	}
	
	@Override
	public void schedule(ScheduleTimerTask task, int timePeriod) {
		this.tasks.add(task);
		this.timer.schedule(task, timePeriod, timePeriod);
	}

	@Override
	public ScheduleTimerTask getTask(String taskId) {
		for (ScheduleTimerTask task : this.tasks) {
			if(task.getScheduleTaskId().equals(taskId)){
				return task;
			}
		}
		return null;
	}

	@Override
	public void cancelTask(String taskId) {
		ScheduleTimerTask task = this.getTask(taskId);
		if(task != null){
			task.cancel();
			this.tasks.remove(task);
			this.timer.purge();
		}		
	}
	
	
	
}
