package com.mesh4j.sync.message.channel.sms.connection.smslib;

import com.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.Guard;

public class SmsLibRefreshTask extends ScheduleTimerTask {

	// MODEL 
	private SmsLibConnection smsLibConnection;
	
	// BUSINESS METHODS
	public SmsLibRefreshTask(SmsLibConnection smsLibConnection) {
		super("SmsLibRefreshTask"+IdGenerator.newID());
		
		Guard.argumentNotNull(smsLibConnection, "smsLibConnection");
		this.smsLibConnection = smsLibConnection;
	}

	@Override
	public void execute() {
		this.smsLibConnection.sendAndReceiveMessages();
	}

}
