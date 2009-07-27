package org.mesh4j.sync.message.schedule;

public interface IScheduleTask {
	
	String getScheduleTaskId();
	
	void run();
}
