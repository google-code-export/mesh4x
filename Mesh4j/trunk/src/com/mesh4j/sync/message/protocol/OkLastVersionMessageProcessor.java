package com.mesh4j.sync.message.protocol;

import java.util.List;

import com.mesh4j.sync.message.IMessageSyncProtocol;

public class OkLastVersionMessageProcessor implements IMessageProcessor {
	
	// MODEL VARIABLES
	private MessageSyncProtocol syncProtocol;
	
	// METHODS
	public OkLastVersionMessageProcessor(MessageSyncProtocol syncProtocol) {
		super();
		this.syncProtocol = syncProtocol;
	}
	
	@Override
	public String getMessageType() {
		return "2";
	}

	@Override
	public List<String> process(String message) {
		if(canProcess(message)){
			String dataSetId = this.getDataSetId(message);
			this.syncProtocol.notifyEndSync(dataSetId);
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public String createMessage(String dataSetId) {
		String header = MessageFormatter.createMessageHeader(dataSetId, this.getMessageType());
		return header;
	}
	
	protected String getDataSetId(String message) {
		return message.substring(3, 8);
	}
	
	private boolean canProcess(String message) {
		String messageType = message.substring(2, 3);
		return this.getMessageType().equals(messageType);
	}

}
