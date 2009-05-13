package org.mesh4j.sync.message;

import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.test.utils.concurrent.command.ConcurrentWorkerCommand;


public class MessageSyncConcurrentCommand extends ConcurrentWorkerCommand{

	// MODEL VARIABLES
	MessageSyncEngine syncEngine;
	IMessageSyncAdapter adapter;
	SmsEndpoint target;
	
	// BUSINESS METHODS
	
	public MessageSyncConcurrentCommand(MessageSyncEngine syncEngine, IMessageSyncAdapter adapter, SmsEndpoint target, long delay) {
		super(new Object[0], delay);
		this.syncEngine = syncEngine;
		this.adapter = adapter;
		this.target = target;
	}
	
	@Override
	public Object execute() throws Exception {
		this.syncEngine.synchronize(adapter, target);
		return "OK";
	}
}
