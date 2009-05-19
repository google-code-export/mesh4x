package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;

public class LastVersionStatusMessageProcessor implements IMessageProcessor{
	
	public static final String MESSAGE_TYPE = "3";
	
	// MODEL VARIABLES
	private GetForMergeMessageProcessor getForMergeMessage;
	private MergeWithACKMessageProcessor mergeWithACKMessage;
	private EndSyncMessageProcessor endMessage;
	private IMessageSyncProtocol messageSyncProtocol;
	
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
			Guard.throwsArgumentException("ERROR_MESSAGE_SYNC_LAST_STATUS_EMPTY_ITEMS");
		}
		
		IMessageSyncAdapter adapter = this.messageSyncProtocol.getSource(syncSession.getSourceId());
		
		return new Message(
				IProtocolConstants.PROTOCOL,
				this.getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				encode(adapter.getSourceType(), items),
				syncSession.getTarget());
	}

	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		List<IMessage> response = new ArrayList<IMessage>();		
		if(syncSession.isOpen()  && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			
			String targetSourceType = getSourceType(message.getData());
			syncSession.setTargetSorceType(targetSourceType);
			
			ArrayList<String> updatedItems = new ArrayList<String>();

			if(syncSession.shouldReceiveChanges()){
				processRemoteChanges(syncSession, response, updatedItems, message);
			} else {
				
			}
			
			if(syncSession.shouldSendChanges()){
				processLocalChanges(syncSession, response, updatedItems);
			}
			
			if(response.isEmpty()){
				response.add(this.endMessage.createMessage(syncSession));
			}
		}
		return response;
	}

	private ArrayList<String> processRemoteChanges(ISyncSession syncSession, List<IMessage> response, ArrayList<String> updatedItems, IMessage message) {
		ArrayList<Object[]> changes = decodeChanges(message.getData());
		for (Object[] parameters : changes) {
			String syncID = (String)parameters[0];

			Item localItem = syncSession.get(syncID);
			if(localItem != null){
				String itemHashCode = (String)parameters[1];
				boolean delete = (Boolean)parameters[2];
				String deletedBy = (String)parameters[3];
				Date deletedWhen = (Date)parameters[4];
				
				if(hasChanged(localItem, syncSession.getLastSyncDate())){
					if(syncSession.isFullProtocol()){
						response.add(this.getForMergeMessage.createMessage(syncSession, localItem));
					} else {
						syncSession.addConflict(syncID);
					}
				} else{
					if(delete){
						if(!localItem.getSync().isDeleted()){
							if(syncSession.isFullProtocol()){
								response.add(this.getForMergeMessage.createMessage(syncSession, localItem));
							} else {
								syncSession.delete(syncID, deletedBy, deletedWhen);
							}
						}
					} else {
						String localHashCode = this.calculateHasCode(localItem);
						if(!localHashCode.equals(itemHashCode)){
							response.add(this.getForMergeMessage.createMessage(syncSession, localItem));
						}
					}
				}
				updatedItems.add(syncID);
			} else {					
				response.add(this.getForMergeMessage.createMessage(syncSession, syncID)); 
			}
		}
		return updatedItems;
	}

	private boolean hasChanged(Item item, Date sinceDate) {
		if(sinceDate == null){
			return false;
		}
		
		if(item == null || item.getLastUpdate() == null || item.getLastUpdate().getWhen() == null){
			return false;
		} 
		return sinceDate.compareTo(item.getLastUpdate().getWhen()) <= 0;
	}

	private void processLocalChanges(ISyncSession syncSession, List<IMessage> response, ArrayList<String> updatedItems) {
		List<Item> localChanges = syncSession.getAll();
		for (Item item : localChanges) {
			if(!updatedItems.contains(item.getSyncId())){
				response.add(this.mergeWithACKMessage.createMessage(syncSession, item));
			}
		}
	}

	private String encode(String sourceType, List<Item> items) {
		StringBuilder sb = new StringBuilder();
		sb.append(sourceType);
		sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
		
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
	
	public static String getSourceType(String data){
		StringTokenizer st = new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		if(st.hasMoreTokens()){
			return st.nextToken();
		} else {
			return null;
		}		
	}
	
	private ArrayList<Object[]> decodeChanges(String data) {
		ArrayList<Object[]> changes = new ArrayList<Object[]>();
		
		StringTokenizer st = new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		if(st.hasMoreTokens()){
			st.nextToken();  // skip source type
		}
		
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
	
	public void setMessageSyncProtocol(IMessageSyncProtocol messageSyncProtocol) {
		this.messageSyncProtocol = messageSyncProtocol;
	}
	
}