package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.Message;

public class GetForUpdateMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private UpdateMessageProcessor updateMessage;
	
	// METHODS	
	
	public GetForUpdateMessageProcessor(UpdateMessageProcessor updateMessage) {
		super();
		this.updateMessage = updateMessage;
	}

	@Override
	public String getMessageType() {
		return "4";
	}
	
	public IMessage createMessage(String dataSetId, String syncID) {
		return new Message(
				MessageSyncProtocol.PREFIX,
				MessageSyncProtocol.VERSION,
				getMessageType(),
				dataSetId,
				syncID);
	}

	@Override
	public List<IMessage> process(IMessage message) {
		if(this.getMessageType().equals(message.getMessageType())){
			String dataSetId = message.getDataSetId();
			String syncId = message.getData();
			
			List<IMessage> response = new ArrayList<IMessage>();
			response.add(this.updateMessage.createMessage(dataSetId, syncId));
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}
}
