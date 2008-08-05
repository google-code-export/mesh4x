package org.mesh4j.sync.message.protocol;

import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.ICancelSyncMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.validations.Guard;


public class CancelSyncMessageProcessor implements ICancelSyncMessageProcessor {

	@Override
	public IMessage createMessage(ISyncSession syncSession) {
		Guard.argumentNotNull(syncSession, "syncSession");
				
		syncSession.cancelSync();
		Message message = new Message(
			IProtocolConstants.PROTOCOL,
			getMessageType(),
			syncSession.getSessionId(),
			syncSession.getVersion(),
			"",
			syncSession.getTarget());
		message.setAckIsRequired(false);
		return message;
	}
	
	@Override
	public String getMessageType() {
		return "0";
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(!syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			syncSession.cancelSync();
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

}
