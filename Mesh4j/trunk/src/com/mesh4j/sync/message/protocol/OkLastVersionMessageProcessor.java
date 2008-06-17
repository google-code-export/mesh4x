package com.mesh4j.sync.message.protocol;

import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.Message;

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
	public List<IMessage> process(IMessage message) {
		if(this.getMessageType().equals(message.getMessageType())){
			String dataSetId = message.getDataSetId();
			this.syncProtocol.notifyEndSync(dataSetId);
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public IMessage createMessage(String dataSetId) {
		return new Message(
				MessageSyncProtocol.PREFIX,
				MessageSyncProtocol.VERSION,
				getMessageType(),
				dataSetId,
				"");
	}
}
