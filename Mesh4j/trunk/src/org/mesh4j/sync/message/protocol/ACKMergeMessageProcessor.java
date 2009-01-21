package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class ACKMergeMessageProcessor implements IMessageProcessor {

	public static final String MESSAGE_TYPE = "7";
	
	// MODEL VARIABLES
	private IItemEncoding itemEncoding;
	private EndSyncMessageProcessor endMessage;
	
	// METHODS
	public ACKMergeMessageProcessor(IItemEncoding itemEncoding, EndSyncMessageProcessor endMessage) {
		super();
		this.itemEncoding = itemEncoding;
		this.endMessage = endMessage;
	}
	
	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			
			int added = getNumberOfAddedItems(message.getData());
			int updated = getNumberOfUpdatedItems(message.getData());
			int deleted = getNumberOfDeletedItems(message.getData());	
			
			if(added > syncSession.getTargetNumberOfAddedItems() || 
					updated > syncSession.getTargetNumberOfUpdatedItems() ||
					deleted > syncSession.getTargetNumberOfDeletedItems()){
				syncSession.setTargetNumberOfAddedItems(added);
				syncSession.setTargetNumberOfUpdatedItems(updated);
				syncSession.setTargetNumberOfDeletedItems(deleted);
			}
				
			
			String data = getData(message.getData());
			
			String hasConflictString = data.substring(0, 1);
					
			String syncID = null;
			if("T".equals(hasConflictString)){
				if(syncSession.isFullProtocol()){
					String encodingItem = data.substring(1, data.length());
					Item conflicItem = this.itemEncoding.decode(syncSession, encodingItem);
					syncSession.addConflict(conflicItem);
					syncID = conflicItem.getSyncId();
				} else {
					syncID = data.substring(1, data.length());
					syncSession.addConflict(syncID);
				}
			} else {
				syncID = data.substring(1, data.length());
			}

			syncSession.notifyAck(syncID);
			
			if(syncSession.isCompleteSync()){
				ArrayList<IMessage> response = new ArrayList<IMessage>();
				response.add(this.endMessage.createMessage(syncSession));
				return response;
			}
		} 
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public IMessage createMessage(ISyncSession syncSession, String syncId, boolean fullProtocolRequired) {
				
		Guard.argumentNotNull(syncSession, "syncSession");
		Guard.argumentNotNull(syncId, "syncId");
		
		StringBuilder sb = new StringBuilder();
		
		int add = syncSession.getNumberOfAddedItems();
		int update = syncSession.getNumberOfUpdatedItems();
		int delete = syncSession.getNumberOfDeletedItems();
		
		sb.append(add);
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
		
		sb.append(update);
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
		
		sb.append(delete);
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
		
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
	
	public static String getData(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip add
		st.nextToken();	// skip update
		st.nextToken();	// skip delete
		return st.nextToken();
	}
	
	public static int getNumberOfAddedItems(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		return Integer.valueOf(st.nextToken());
	}

	public static int getNumberOfUpdatedItems(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();  // skip add
		return Integer.valueOf(st.nextToken());
	}

	public static int getNumberOfDeletedItems(String data) {
		StringTokenizer st =  new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();	// skip add
		st.nextToken();	// skip update
		return Integer.valueOf(st.nextToken());
	}

	public static String getSyncID(String data) {
		String ackData = getData(data);
		String itemData = ackData.substring(1, ackData.length());
		return ItemEncoding.getSyncID(itemData);
	}
	
}
