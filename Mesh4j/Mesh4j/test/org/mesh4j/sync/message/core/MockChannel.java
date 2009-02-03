package org.mesh4j.sync.message.core;

import org.mesh4j.sync.message.IChannel;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageReceiver;
import org.mesh4j.sync.message.InOutStatistics;

public class MockChannel implements IChannel {
	
	private InOutStatistics inOutStatistics = new InOutStatistics(0, 0, 0, 0);
	public boolean purgeWasCalled = false;
	
	public void setInOutStatistics(InOutStatistics stat){
		this.inOutStatistics = stat;
	}

	@Override
	public InOutStatistics getInOutStatistics(String sessionId, int version) {
		return this.inOutStatistics;
	}

	@Override
	public void purgeMessages(String sessionId, int sessionVersion) {
		this.purgeWasCalled = true;
	}

	@Override
	public void registerMessageReceiver(IMessageReceiver messageReceiver) {
	}

	@Override
	public void send(IMessage message) {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void startUp() {
	}

}
