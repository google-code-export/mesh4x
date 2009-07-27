package org.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;


public class EndSyncMessageProcessor implements IMessageProcessor {

	// CONSTANTS 
	public final static String MESSAGE_TYPE = "8";
	
	// MODEL VARIABLES
	private ACKEndSyncMessageProcessor ackEndMessage; 
	private IMessageSyncProtocol messageSyncProtocol;
	
	// BUSINESS METHODS
	public EndSyncMessageProcessor(ACKEndSyncMessageProcessor ackEndMessage) {
		super();
		this.ackEndMessage = ackEndMessage;
	}


	public String getMessageType() {
		return MESSAGE_TYPE;
	}
	
	public IMessage createMessage(ISyncSession syncSession){
		Guard.argumentNotNull(syncSession, "syncSession");

		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				DateHelper.formatW3CDateTime(syncSession.createSyncDate()),
				syncSession.getTarget());
	}


	public Vector<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			Date sinceDate = DateHelper.parseW3CDateTime(message.getData());
			if(sinceDate != null){
				this.messageSyncProtocol.endSync(syncSession, sinceDate);
				
				Vector<IMessage> response = new Vector<IMessage>();
				response.addElement(this.ackEndMessage.createMessage(syncSession, sinceDate));
				return response;
			}
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}

}
