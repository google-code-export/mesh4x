package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.model.Item;

public class MessageSyncProtocol implements IMessageSyncProtocol {

	// COSTANTS
	public final static String PREFIX = "M";
	public final static String VERSION = "1";
	
	// MODEL
	private ArrayList<IMessageProcessor> messageProcessors = new ArrayList<IMessageProcessor>();
	private CheckForUpdateMessageProcessor initialMessage;
	
	// METHODS
	protected MessageSyncProtocol(){
		super();
	}
	
	@Override
	public List<IMessage> processMessage(IMessage message) {
		List<IMessage> response = new ArrayList<IMessage>();
		if(this.isValidMessageProtocol(message)){
			for (IMessageProcessor processor : this.messageProcessors) {
				List<IMessage> msgResponse = processor.process(message);
				response.addAll(msgResponse);
			}
		}
		return response;
	}
	
	@Override
	public IMessage createBeginSyncMessage(String dataSetId, List<Item> items) {
		return this.initialMessage.createMessage(dataSetId, items);
	}
	
	private boolean isValidMessageProtocol(IMessage message) {
		return message != null 
			&& message.getProtocol().equals(PREFIX)
			&& message.getProtocolVersion().equals(VERSION);
	}

	public void setInitialMessage(CheckForUpdateMessageProcessor checkForUpdate) {
		this.initialMessage = checkForUpdate;
	}

	protected void setMessageProcessors(ArrayList<IMessageProcessor> processors) {
		this.messageProcessors = processors;
	}

	
	public void notifyEndSync(String dataSetId) {
		// TODO (JMT) MeshSMS: snapshot concept
		// TODO (JMT) MeshSMS: getAll(since date)
		// TODO (JMT) MeshSMS: state machine - endSync?
	}

	public void notifyConflict(String dataSetId, Item conflicItem) {
		// TODO (JMT) MeshSMS: state machine - conflicts?
	}

}
