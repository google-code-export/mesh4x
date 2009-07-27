package org.mesh4j.sync.message.protocol;

import java.util.Vector;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class ACKMergeMessageProcessor implements IMessageProcessor {

	// CONSTANTS 
	public final static String MESSAGE_TYPE = "7";
	
	// MODEL VARIABLES
	private IItemEncoding itemEncoding;
	private EndSyncMessageProcessor endMessage;
	
	// METHODS
	public ACKMergeMessageProcessor(IItemEncoding itemEncoding, EndSyncMessageProcessor endMessage) {
		super();
		this.itemEncoding = itemEncoding;
		this.endMessage = endMessage;
	}
	

	public String getMessageType() {
		return MESSAGE_TYPE;
	}


	public Vector<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			String data = message.getData();
			String hasConflictString = data.substring(0, 1);
						
			if("T".equals(hasConflictString)){
				if(syncSession.isFullProtocol()){
					String encodingItem = data.substring(1, data.length());
					Item conflicItem = this.itemEncoding.decode(syncSession, encodingItem);
					syncSession.addConflict(conflicItem);
					syncSession.notifyAck(conflicItem.getSyncId());
				} else {
					String syncID = data.substring(1, data.length());
					syncSession.addConflict(syncID);
					syncSession.notifyAck(syncID);
				}
			} else {
				String syncID = data.substring(1, data.length());
				syncSession.notifyAck(syncID);
			}
			
			if(syncSession.isCompleteSync()){
				Vector<IMessage> response = new Vector<IMessage>();
				response.addElement(this.endMessage.createMessage(syncSession));
				return response;
			}
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public IMessage createMessage(ISyncSession syncSession, String syncId, boolean fullProtocolRequired) {
		Guard.argumentNotNull(syncSession, "syncSession");
		Guard.argumentNotNull(syncId, "syncId");

		StringBuilder sb = new StringBuilder();
		if(syncSession.hasConflict(syncId)){
			sb.append("T");
			if(fullProtocolRequired){
				Item item = syncSession.get(syncId);
				String encodingItem = this.itemEncoding.encode(syncSession, item, new int[0]);
				sb.append(encodingItem);
			} else {
				sb.append(syncId);			
			}
		} else {
			sb.append("F");
			sb.append(syncId);
		}

		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				sb.toString(),
				syncSession.getTarget());
	}

}
