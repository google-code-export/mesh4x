package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.Message;

public class ACKMergeMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private EndSyncMessageProcessor endMessage;
	
	// METHODS
	public ACKMergeMessageProcessor(EndSyncMessageProcessor endMessage) {
		super();
		this.endMessage = endMessage;
	}
	
	@Override
	public String getMessageType() {
		return "7";
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
			String data = message.getData();
			String hasConflictString = data.substring(0, 1);
			String syncID = data.substring(1, data.length());
			
			if("T".equals(hasConflictString)){
				syncSession.addConflict(syncID);
			}
			
			syncSession.notifyAck(syncID);
			if(syncSession.isCompleteSync()){
				ArrayList<IMessage> response = new ArrayList<IMessage>();
				response.add(this.endMessage.createMessage(syncSession));
				return response;
			}
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public IMessage createMessage(ISyncSession syncSession, String syncId, boolean hasConflict) {
		String data = (hasConflict ? "T" : "F") + syncId;
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSourceId(),
				data,
				syncSession.getTarget());
	}

}
