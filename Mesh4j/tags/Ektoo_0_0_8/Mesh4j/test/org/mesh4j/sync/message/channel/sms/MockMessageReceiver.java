package org.mesh4j.sync.message.channel.sms;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageReceiver;


public class MockMessageReceiver implements IMessageReceiver {

	private List<IMessage> messages = new ArrayList<IMessage>();
	
	public List<IMessage> getMessages(){
		return messages;
	}
	
	@Override
	public void receiveMessage(IMessage message) {
		this.messages.add(message);
	}

}
