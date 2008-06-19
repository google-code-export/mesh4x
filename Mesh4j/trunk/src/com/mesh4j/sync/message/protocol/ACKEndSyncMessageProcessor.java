package com.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.utils.DateHelper;

public class ACKEndSyncMessageProcessor implements IMessageProcessor {

	@Override
	public String getMessageType() {
		return "9";
	}
	
	public IMessage createMessage(ISyncSession syncSession, Date sinceDate){
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				DateHelper.formatDateTime(sinceDate),
				syncSession.getTarget());
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
			Date sinceDate = DateHelper.parseDateTime(message.getData());
			syncSession.endSync(sinceDate);
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

}
