package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.Message;

public class GetForMergeMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private MergeMessageProcessor mergeMessage;
	
	// METHODS	
	
	public GetForMergeMessageProcessor(MergeMessageProcessor mergeMessage) {
		super();
		this.mergeMessage = mergeMessage;
	}

	@Override
	public String getMessageType() {
		return "4";
	}
	
	public IMessage createMessage(ISyncSession syncSession, String syncID) {
		syncSession.waitForAck(syncID);
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				syncID,
				syncSession.getTarget());
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){						
			String syncId = message.getData();
				
			List<IMessage> response = new ArrayList<IMessage>();
			response.add(this.mergeMessage.createMessage(syncSession, syncId));
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}
}
