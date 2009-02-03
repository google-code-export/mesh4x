package org.mesh4j.sync.message.protocol;

import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.ICancelSyncMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.validations.Guard;


public class CancelSyncMessageProcessor implements ICancelSyncMessageProcessor {

	// CONSTANTS 
	public final static String MESSAGE_TYPE = "0";
	
	// MODEL VARIABLES
	private IMessageSyncProtocol messageSyncProtocol;
	
	// BUSINESS METHODS
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
		return MESSAGE_TYPE;
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			this.messageSyncProtocol.cancelSync(syncSession);
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}
	
	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}

}
