package com.mesh4j.sync.message.channel.sms.connection.smslib;

import com.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.Guard;

public class RefreshSchedulerTimerTask extends ScheduleTimerTask {

	// MODEL 
	private IRefreshTask refreshTask;
	
	// BUSINESS METHODS
	public RefreshSchedulerTimerTask(IRefreshTask refreshTask) {
		super("SmsLibRefreshTask"+IdGenerator.newID());
		
		Guard.argumentNotNull(refreshTask, "refreshTask");
		this.refreshTask = refreshTask;
	}

	@Override
	public void execute() {
		this.refreshTask.refresh();
	}

}
