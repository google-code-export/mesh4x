package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IMessageSyncProtocol;

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
	
	public String createMessage(String dataSetId, String syncID) {
		String msg = MessageFormatter.createMessage(dataSetId, this.getMessageType(), syncID);
		return msg;
	}

	@Override
	public List<String> process(String message) {
		if(this.canProcess(message)){
			String dataSetId = MessageFormatter.getDataSetId(message);
			String syncId = MessageFormatter.getData(message);
			
			ArrayList<String> response = new ArrayList<String>();
			response.add(this.updateMessage.createMessage(dataSetId, syncId));
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}
	
	private boolean canProcess(String message) {
		String messageType = MessageFormatter.getMessgaeType(message);
		return this.getMessageType().equals(messageType);
	}
}
