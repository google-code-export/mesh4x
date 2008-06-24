package com.mesh4j.sync.message.protocol;

import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.ICancelSyncMessageProcessor;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.validations.Guard;

public class CancelSyncMessageProcessor implements ICancelSyncMessageProcessor {

	@Override
	public IMessage createMessage(ISyncSession syncSession) {
		Guard.argumentNotNull(syncSession, "syncSession");
				
		syncSession.cancelSync();
		return new Message(
			IProtocolConstants.PROTOCOL,
			getMessageType(),
			syncSession.getSessionId(),
			"",
			syncSession.getTarget());
	}
	
	@Override
	public String getMessageType() {
		return "0";
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(!syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
			syncSession.cancelSync();
		}
		return IMessageSyncProtocol.NO_RESPONSE;	// TODO (JMT) MeshSms: cancel ack?
	}

}
