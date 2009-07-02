package org.mesh4j.sync.message.channel.sms.schedule;

import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import org.mesh4j.sync.validations.Guard;

public class RefreshSchedulerTimerTask extends ScheduleTimerTask {

	// MODEL 
	private IRefreshTask refreshTask;
	
	// BUSINESS METHODS
	public RefreshSchedulerTimerTask(IRefreshTask refreshTask) {
		super("SmsRefreshTask" + IdGenerator.INSTANCE.newID());
		
		Guard.argumentNotNull(refreshTask, "refreshTask");
		this.refreshTask = refreshTask;
	}

	public void execute() {
		this.refreshTask.refresh();
	}

}
