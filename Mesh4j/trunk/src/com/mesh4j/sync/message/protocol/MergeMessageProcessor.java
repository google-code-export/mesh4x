package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.MessageSyncEngine;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.model.Item;

public class MergeMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private EndSyncMessageProcessor endMessage;
	
	// METHODS
	public MergeMessageProcessor(EndSyncMessageProcessor endMessage) {
		super();
		this.endMessage = endMessage;
	}

	@Override
	public String getMessageType() {
		return "5";
	}
	
	public IMessage createMessage(ISyncSession syncSession, String syncId) {
		Item item = syncSession.get(syncId);
		return createMessage(syncSession, item);
	}
	
	public IMessage createMessage(ISyncSession syncSession, Item item) {
		String data = ItemEncoding.encode(syncSession, item);
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSourceId(),
				data,
				syncSession.getTarget());
	}
	
	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(this.getMessageType().equals(message.getMessageType())){
			Item incomingItem = ItemEncoding.decode(syncSession, message.getData());
			MessageSyncEngine.merge(syncSession, incomingItem);
			
			syncSession.notifyAck(incomingItem.getSyncId());
			if(syncSession.isCompleteSync()){
				ArrayList<IMessage> response = new ArrayList<IMessage>();
				response.add(this.endMessage.createMessage(syncSession));
				return response;
			}
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}
}
