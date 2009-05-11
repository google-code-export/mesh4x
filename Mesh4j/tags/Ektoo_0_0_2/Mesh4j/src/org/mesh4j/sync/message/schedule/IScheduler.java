package org.mesh4j.sync.message.schedule;

public interface IScheduler<T extends IScheduleTask> {
	
	public void schedule(T task, int timePeriod);
	
	public T getTask(String taskId);
	
	public void cancelTask(String taskId);
}
