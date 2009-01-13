package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class NoChangesMessageProcessor implements IMessageProcessor {

	public static final String MESSAGE_TYPE = "2";
	
	// MODEL VARIABLES
	private EndSyncMessageProcessor endMessage;
	private MergeWithACKMessageProcessor mergeWithACKMessage;
	private IMessageSyncProtocol messageSyncProtocol;
	
	// METHODS
	public NoChangesMessageProcessor(EndSyncMessageProcessor endMessage, MergeWithACKMessageProcessor mergeWithACKMessage) {
		super();
		this.endMessage = endMessage;
		this.mergeWithACKMessage = mergeWithACKMessage;
	}
	
	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			ArrayList<IMessage> response = new ArrayList<IMessage>();
			
			if(syncSession.shouldSendChanges()){
				this.processLocalChanges(syncSession, response);
			}
			
			if(response.isEmpty()){
				response.add(this.endMessage.createMessage(syncSession));
			}
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}
	
	private void processLocalChanges(ISyncSession syncSession, ArrayList<IMessage> response) {
		List<Item> localChanges = syncSession.getAll();
		for (Item item : localChanges) {
			response.add(this.mergeWithACKMessage.createMessage(syncSession, item));
		}
	}


	public IMessage createMessage(ISyncSession syncSession) {
		Guard.argumentNotNull(syncSession, "syncSession");

		IMessageSyncAdapter adapter = this.messageSyncProtocol.getSource(syncSession.getSourceId());
		
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				adapter.getSourceType(),
				syncSession.getTarget());
	}
	
	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}
}
