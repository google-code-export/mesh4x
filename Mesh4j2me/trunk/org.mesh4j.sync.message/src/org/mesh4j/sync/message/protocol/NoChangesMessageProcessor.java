package org.mesh4j.sync.message.protocol;

import java.util.Vector;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class NoChangesMessageProcessor implements IMessageProcessor {

	// CONSTANTS 
	public final static String MESSAGE_TYPE = "2";

	// MODEL VARIABLES
	private EndSyncMessageProcessor endMessage;
	private MergeWithACKMessageProcessor mergeWithACKMessage;
	
	// METHODS
	public NoChangesMessageProcessor(EndSyncMessageProcessor endMessage, MergeWithACKMessageProcessor mergeWithACKMessage) {
		super();
		this.endMessage = endMessage;
		this.mergeWithACKMessage = mergeWithACKMessage;
	}
	

	public String getMessageType() {
		return MESSAGE_TYPE;
	}


	public Vector<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			Vector<IMessage> response = this.processLocalChanges(syncSession);
			if(response.isEmpty()){
				response.addElement(this.endMessage.createMessage(syncSession));
			}
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}
	
	private Vector<IMessage> processLocalChanges(ISyncSession syncSession) {
		Vector<IMessage> response = new Vector<IMessage>();
		Vector<Item> localChanges = syncSession.getAll();
		for (Item item : localChanges) {
			response.addElement(this.mergeWithACKMessage.createMessage(syncSession, item));
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
