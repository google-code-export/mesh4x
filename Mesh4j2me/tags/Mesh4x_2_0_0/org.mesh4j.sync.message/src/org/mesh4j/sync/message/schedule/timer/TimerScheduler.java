package org.mesh4j.sync.message.schedule.timer;

import java.util.Timer;
import java.util.Vector;

import org.mesh4j.sync.message.schedule.IScheduler;


public class TimerScheduler implements IScheduler<ScheduleTimerTask>{

	public final static TimerScheduler INSTANCE = new TimerScheduler();
	
	// MODEL VARIABLES
	private Timer timer;
	private Vector<ScheduleTimerTask> tasks = new Vector<ScheduleTimerTask>();

	// BUSINESS METHODS
	private TimerScheduler(){
		super();
		reset();
	}	

	public void schedule(ScheduleTimerTask task, int timePeriod) {
		this.tasks.addElement(task);
		this.timer.schedule(task, timePeriod, timePeriod);
	}


	public ScheduleTimerTask getTask(String taskId) {
		for (ScheduleTimerTask task : this.tasks) {
			if(task.getScheduleTaskId().equals(taskId)){
				return task;
			}
		}
		return null;
	}


	public void cancelTask(String taskId) {
		ScheduleTimerTask task = this.getTask(taskId);
		if(task != null){
			task.cancel();
			this.tasks.removeElement(task);
		}		
	}

	public void reset() {
		if(this.timer != null){
			this.timer.cancel();
		}
		this.timer = new Timer();
		this.tasks = new Vector<ScheduleTimerTask>();		
	}
		
}
