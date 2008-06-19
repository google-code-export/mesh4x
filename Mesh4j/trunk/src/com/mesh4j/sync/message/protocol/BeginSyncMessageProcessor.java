package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.IBeginSyncMessageProcessor;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.utils.DateHelper;

public class BeginSyncMessageProcessor implements IMessageProcessor, IBeginSyncMessageProcessor {

	// MODEL VARIABLES
	private NoChangesMessageProcessor noChanges;
	private LastVersionStatusMessageProcessor lastVersionStatus;

	// METHODS
	public BeginSyncMessageProcessor(NoChangesMessageProcessor noChanges, LastVersionStatusMessageProcessor lastVersionStatus) {
		super();
		this.noChanges = noChanges;
		this.lastVersionStatus = lastVersionStatus;
	}
	
	@Override
	public String getMessageType() {
		return "1";
	}

	@Override
	public IMessage createMessage(ISyncSession syncSession){
		syncSession.beginSync();
		String data = DateHelper.formatDateTime(syncSession.getLastSyncDate());
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSourceId(),
				data,
				syncSession.getTarget());
	}
	
	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(!syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
					
			Date sinceDate = DateHelper.parseDateTime(message.getData());
			syncSession.beginSync(sinceDate);
			
			List<Item> items = syncSession.getAll();
			
			List<IMessage> response = new ArrayList<IMessage>();
			
			if(items.isEmpty()){
				response.add(this.noChanges.createMessage(syncSession));
				return response;
			} else {
				response.add(this.lastVersionStatus.createMessage(syncSession, items));
				return response;
			}
			
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}	
	}
}
