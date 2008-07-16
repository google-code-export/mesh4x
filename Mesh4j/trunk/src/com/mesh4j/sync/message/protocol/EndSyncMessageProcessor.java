package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.validations.Guard;

public class EndSyncMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private ACKEndSyncMessageProcessor ackEndMessage; 
	private IMessageSyncProtocol messageSyncProtocol;
	
	// BUSINESS METHODS
	public EndSyncMessageProcessor(ACKEndSyncMessageProcessor ackEndMessage) {
		super();
		this.ackEndMessage = ackEndMessage;
	}

	@Override
	public String getMessageType() {
		return "8";
	}
	
	public IMessage createMessage(ISyncSession syncSession){
		Guard.argumentNotNull(syncSession, "syncSession");
				
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				DateHelper.formatDateTime(syncSession.createSyncDate()),
				syncSession.getTarget());
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
			Date sinceDate = DateHelper.parseDateTime(message.getData());
			if(sinceDate != null){
				this.messageSyncProtocol.endSync(syncSession, sinceDate);
				
				ArrayList<IMessage> response = new ArrayList<IMessage>();
				response.add(this.ackEndMessage.createMessage(syncSession, sinceDate));
				return response;
			}
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}

}
