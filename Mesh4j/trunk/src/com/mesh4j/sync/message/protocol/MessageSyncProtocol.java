package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.model.Item;

public class MessageSyncProtocol implements IMessageSyncProtocol {

	// TODO (JMT) MeshSMS: Add messages checkForUpdate/Ok/LastStatus/GetForUpdate/Update/UpdateACK/Conflic
	
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
	
	public String createMessageHeader(String dataSetId, String messageType) {
		StringBuffer sb = new StringBuffer();
		sb.append(PREFIX);
		sb.append(VERSION);
		sb.append(messageType);
		sb.append(StringUtils.leftPad(dataSetId, 5, "0"));
		return sb.toString();
	}
	
	private boolean isValidMessageProtocol(String message) {
		return message != null 
			&& message.length() >= 8 
			&& message.substring(0, 1).equals(PREFIX)
			&& message.substring(1, 2).equals(VERSION);
	}

	public void notifyEndSync(String dataSetId) {
		// TODO (JMT) MeshSMS: state machine?
	}

	public void setInitialMessage(CheckForUpdateMessageProcessor checkForUpdate) {
		this.initialMessage = checkForUpdate;
	}

	protected void setMessageProcessors(ArrayList<IMessageProcessor> processors) {
		this.messageProcessors = processors;
	}

}
