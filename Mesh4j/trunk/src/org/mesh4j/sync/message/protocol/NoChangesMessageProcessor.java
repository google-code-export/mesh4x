package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class NoChangesMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private EndSyncMessageProcessor endMessage;
	private MergeWithACKMessageProcessor mergeWithACKMessage;
	
	// METHODS
	public NoChangesMessageProcessor(EndSyncMessageProcessor endMessage, MergeWithACKMessageProcessor mergeWithACKMessage) {
		super();
		this.endMessage = endMessage;
		this.mergeWithACKMessage = mergeWithACKMessage;
	}
	
	@Override
	public String getMessageType() {
		return "2";
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			List<IMessage> response = this.processLocalChanges(syncSession);
			if(response.isEmpty()){
				response.add(this.endMessage.createMessage(syncSession));
			}
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}
	
	private List<IMessage> processLocalChanges(ISyncSession syncSession) {
		ArrayList<IMessage> response = new ArrayList<IMessage>();
		List<Item> localChanges = syncSession.getAll();
		for (Item item : localChanges) {
			response.add(this.mergeWithACKMessage.createMessage(syncSession, item));
		}
		return response;
	}


	public IMessage createMessage(ISyncSession syncSession) {
		Guard.argumentNotNull(syncSession, "syncSession");

		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				"",
				syncSession.getTarget());
	}
}