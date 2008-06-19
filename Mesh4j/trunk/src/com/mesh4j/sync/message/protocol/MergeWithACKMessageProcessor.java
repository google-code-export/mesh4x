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

public class MergeWithACKMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private ACKMergeMessageProcessor ackMessage;
	
	// METHODS
	public MergeWithACKMessageProcessor(ACKMergeMessageProcessor ackMessage) {
		super();
		this.ackMessage = ackMessage;
	}

	@Override
	public String getMessageType() {
		return "6";
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
		if(syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
			Item incomingItem = ItemEncoding.decode(syncSession, message.getData());
			MessageSyncEngine.merge(syncSession, incomingItem);
			
			ArrayList<IMessage> response = new ArrayList<IMessage>();
			response.add(this.ackMessage.createMessage(
					syncSession, 
					incomingItem.getSyncId(),
					syncSession.hasConflict(incomingItem.getSyncId())));
			return response;
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}
}
