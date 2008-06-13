package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

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
	public List<String> processMessage(String message) {
		ArrayList<String> response = new ArrayList<String>();
		if(this.isValidMessageProtocol(message)){
			for (IMessageProcessor processor : this.messageProcessors) {
				List<String> messageResponse = processor.process(message);
				if(!messageResponse.isEmpty()){
					response.addAll(messageResponse);
				}
			}
		}
		return response;
	}
	
	@Override
	public String createBeginSyncMessage(String dataSetId, List<Item> items) {
		return this.initialMessage.createMessage(dataSetId, items);
	}
	
	private boolean isValidMessageProtocol(String message) {
		return message != null 
			&& message.length() >= MessageFormatter.getHeaderLenght()
			&& MessageFormatter.getProtocol(message).equals(PREFIX)
			&& MessageFormatter.getVersion(message).equals(VERSION);
	}

	public void setInitialMessage(CheckForUpdateMessageProcessor checkForUpdate) {
		this.initialMessage = checkForUpdate;
	}

	protected void setMessageProcessors(ArrayList<IMessageProcessor> processors) {
		this.messageProcessors = processors;
	}

	
	public void notifyEndSync(String dataSetId) {
		// TODO (JMT) MeshSMS: snapshot concept
		// TODO (JMT) MeshSMS: state machine - endSync?
	}

	public void notifyConflict(String dataSetId, Item conflicItem) {
		// TODO (JMT) MeshSMS: state machine - conflicts?
	}

}
