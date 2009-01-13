package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.validations.Guard;

public class EqualStatusMessageProcessor implements IMessageProcessor{

	public static final String MESSAGE_TYPE = "E";
	
	// MODEL VARIABLES
	private EndSyncMessageProcessor endMessage;
	private IMessageSyncProtocol messageSyncProtocol;
	
	// METHODS
	public EqualStatusMessageProcessor(EndSyncMessageProcessor endMessage) {
		super();
		this.endMessage = endMessage;
	}
	
	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}
	
	public IMessage createMessage(ISyncSession syncSession) {
		Guard.argumentNotNull(syncSession, "syncSession");
		
		IMessageSyncAdapter adapter = this.messageSyncProtocol.getSource(syncSession.getSourceId());
				
		return new Message(
				IProtocolConstants.PROTOCOL,
				this.getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				adapter.getSourceType(),
				syncSession.getTarget());
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			ArrayList<IMessage> response = new ArrayList<IMessage>();
			response.add(this.endMessage.createMessage(syncSession));
			return response;
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}
	
	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}

}
