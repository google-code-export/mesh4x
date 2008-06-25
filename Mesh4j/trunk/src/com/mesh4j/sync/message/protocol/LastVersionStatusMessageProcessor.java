package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.validations.Guard;

public class LastVersionStatusMessageProcessor implements IMessageProcessor{
	
	// MODEL VARIABLES
	private GetForMergeMessageProcessor getForMergeMessage;
	private MergeWithACKMessageProcessor mergeWithACKMessage;
	private EndSyncMessageProcessor endMessage;
	
	// METHODS
	public LastVersionStatusMessageProcessor(
			GetForMergeMessageProcessor getForMergeMessage,
			MergeWithACKMessageProcessor mergeWithACKMessage,
			EndSyncMessageProcessor endMessage) {
		super();
		this.getForMergeMessage = getForMergeMessage;
		this.mergeWithACKMessage = mergeWithACKMessage;
		this.endMessage = endMessage;
	}
	
	public IMessage createMessage(ISyncSession syncSession, List<Item> items) {
		Guard.argumentNotNull(syncSession, "syncSession");
		Guard.argumentNotNull(items, "items");
		
		if(items.isEmpty()){
			Guard.throwsArgumentException("xxx");  // TODO ooooooooooooooooooooo
		}
		
		return new Message(
				IProtocolConstants.PROTOCOL,
				this.getMessageType(),
				syncSession.getSessionId(),
				encode(items),
				syncSession.getTarget());
	}

	@Override
	public String getMessageType() {
		return "3";
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		List<IMessage> response = new ArrayList<IMessage>();		
		if(syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){
			ArrayList<Object[]> changes = decodeChanges(message.getData());
			ArrayList<String> updatedItems = new ArrayList<String>();
			for (Object[] parameters : changes) {
				String syncID = (String)parameters[0];

				Item localItem = syncSession.get(syncID);
				if(localItem != null){
					String itemHashCode = (String)parameters[1];
					boolean delete = (Boolean)parameters[2];
					String deletedBy = (String)parameters[3];
					Date deletedWhen = (Date)parameters[4];
					
					if(syncSession.hasChanged(syncID)){
						syncSession.addConflict(syncID);
						updatedItems.add(syncID);
					} else{
						if(delete){
							syncSession.delete(syncID, deletedBy, deletedWhen);
							updatedItems.add(syncID);
						} else {
							String localHashCode = this.calculateHasCode(localItem);
							if(!localHashCode.equals(itemHashCode)){
								response.add(this.getForMergeMessage.createMessage(syncSession, localItem));
								updatedItems.add(syncID);
							}
						}
					}
				} else {
					response.add(this.getForMergeMessage.createMessage(syncSession, syncID)); 
				}

			}
			
			response.addAll(processLocalChanges(syncSession, updatedItems));
			if(response.isEmpty()){
				response.add(this.endMessage.createMessage(syncSession));
			}
		}
		return response;
	}

	private List<IMessage> processLocalChanges(ISyncSession syncSession, ArrayList<String> updatedItems) {
		ArrayList<IMessage> response = new ArrayList<IMessage>();
		List<Item> localChanges = syncSession.getAll();
		for (Item item : localChanges) {
			if(!updatedItems.contains(item.getSyncId())){
				response.add(this.mergeWithACKMessage.createMessage(syncSession, item));
			}
		}
		return response;
	}


	private String encode(List<Item> items) {
		StringBuilder sb = new StringBuilder();
	
		Iterator<Item> it = items.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			sb.append(item.getSyncId());
			sb.append(IProtocolConstants.FIELD_SEPARATOR);
			sb.append(this.calculateHasCode(item));
			if(item.isDeleted()){
				sb.append(IProtocolConstants.FIELD_SEPARATOR);
				sb.append("D");
				sb.append(IProtocolConstants.FIELD_SEPARATOR);
				sb.append(item.getLastUpdate().getBy());
				sb.append(IProtocolConstants.FIELD_SEPARATOR);
				sb.append(DateHelper.formatDateTime(item.getLastUpdate().getWhen()));
			}
			if(it.hasNext()){
				sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
			}
		}
		return sb.toString();
	}
	
	private String calculateHasCode(Item item) {
		StringBuffer sb = new StringBuffer();
		sb.append(item.getLastUpdate().getSequence());
		sb.append(item.getLastUpdate().getBy());
		sb.append(item.getLastUpdate().getWhen());
		sb.append(item.isDeleted());
		return String.valueOf(sb.toString().hashCode());		
	}
	
	private ArrayList<Object[]> decodeChanges(String data) {
		ArrayList<Object[]> changes = new ArrayList<Object[]>();
		
		StringTokenizer st = new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		while(st.hasMoreTokens()){
			String itemToSync = st.nextToken();
			StringTokenizer stFields = new StringTokenizer(itemToSync, IProtocolConstants.FIELD_SEPARATOR);
			String syncID = stFields.nextToken();
			String itemHashCode = stFields.nextToken();
			
			String deletedBy = null;
			Date deletedWhen = null;
			boolean deleted = stFields.hasMoreTokens() && "D".equals(stFields.nextToken());
			if(deleted){
				deletedBy = stFields.nextToken();
				deletedWhen = DateHelper.parseDateTime(stFields.nextToken());
			}
			changes.add(new Object[] {syncID, itemHashCode, deleted, deletedBy, deletedWhen});
		}
		return changes;
	}
}