package org.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;

import de.enough.polish.util.StringTokenizer;


public class LastVersionStatusMessageProcessor implements IMessageProcessor{
	
	// CONSTANTS 
	public final static String MESSAGE_TYPE = "3";
		
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
	
	public IMessage createMessage(ISyncSession syncSession, Vector<Item> items) {
		Guard.argumentNotNull(syncSession, "syncSession");
		Guard.argumentNotNull(items, "items");
		
		if(items.isEmpty()){
			Guard.throwsArgumentException("ERROR_MESSAGE_SYNC_LAST_STATUS_EMPTY_ITEMS");
		}
		
		String data = encode(items);
		return new Message(
				IProtocolConstants.PROTOCOL,
				this.getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				data,
				syncSession.getTarget());
	}


	public String getMessageType() {
		return MESSAGE_TYPE;
	}

	public Vector<IMessage> process(ISyncSession syncSession, IMessage message) {
		Vector<IMessage> response = new Vector<IMessage>();		
		if(syncSession.isOpen()  && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			Vector<Object[]> changes = decodeChanges(message.getData());
			Vector<String> updatedItems = new Vector<String>();
			for (Object[] parameters : changes) {
				String syncID = (String)parameters[0];

				Item localItem = syncSession.get(syncID);
				if(localItem != null){
					String itemHashCode = (String)parameters[1];
					boolean delete = (Boolean)parameters[2];
					String deletedBy = (String)parameters[3];
					Date deletedWhen = (Date)parameters[4];
					
					if(syncSession.hasChanged(syncID)){
						if(syncSession.isFullProtocol()){
							response.addElement(this.getForMergeMessage.createMessage(syncSession, localItem));
						} else {
							syncSession.addConflict(syncID);
						}
					} else{
						if(delete){
							if(syncSession.isFullProtocol()){
								response.addElement(this.getForMergeMessage.createMessage(syncSession, localItem));
							} else {
								syncSession.delete(syncID, deletedBy, deletedWhen);
							}
						} else {
							String localHashCode = this.calculateHasCode(localItem);
							if(!localHashCode.equals(itemHashCode)){
								response.addElement(this.getForMergeMessage.createMessage(syncSession, localItem));
							}
						}
					}
					updatedItems.addElement(syncID);
				} else {
					response.addElement(this.getForMergeMessage.createMessage(syncSession, syncID)); 
				}
			}
			
			processLocalChanges(syncSession, updatedItems, response);
			if(response.isEmpty()){
				response.addElement(this.endMessage.createMessage(syncSession));
			}
		}
		return response;
	}

	private Vector<IMessage> processLocalChanges(ISyncSession syncSession, Vector<String> updatedItems, Vector<IMessage> response) {
		NoContainsItemFilter noContainsFilter = new NoContainsItemFilter(updatedItems);
		Vector<Item> localChanges = syncSession.getAll(noContainsFilter);
		for (Item item : localChanges) {
			response.addElement(this.mergeWithACKMessage.createMessage(syncSession, item));
		}
		return response;
	}


	private String encode(Vector<Item> items) {
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
				sb.append(DateHelper.formatW3CDateTime(item.getLastUpdate().getWhen()));
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
	
	private Vector<Object[]> decodeChanges(String data) {
		Vector<Object[]> changes = new Vector<Object[]>();
		
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
				deletedWhen = DateHelper.parseW3CDateTime(stFields.nextToken());
			}
			changes.addElement(new Object[] {syncID, itemHashCode, deleted, deletedBy, deletedWhen});
		}
		return changes;
	}
}