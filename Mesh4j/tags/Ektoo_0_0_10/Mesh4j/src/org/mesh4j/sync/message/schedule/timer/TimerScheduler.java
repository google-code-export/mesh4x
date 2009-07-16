package org.mesh4j.sync.message.schedule.timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import org.mesh4j.sync.message.schedule.IScheduler;


public class TimerScheduler implements IScheduler<ScheduleTimerTask>{

	public final static TimerScheduler INSTANCE = new TimerScheduler();
	
	// MODEL VARIABLES
	private Timer timer;
	private List<ScheduleTimerTask> tasks = Collections.synchronizedList(new ArrayList<ScheduleTimerTask>());

	// BUSINESS METHODS
	private TimerScheduler(){
		super();
		reset();
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
			this.purge();
		}		
	}

	public void purge() {
		this.timer.purge();		
	}

	public void reset() {
		if(this.timer != null){
			this.timer.cancel();
		}
		this.timer = new Timer("TimerScheduler", true);
		this.tasks = new ArrayList<ScheduleTimerTask>();		
	}
		
}
