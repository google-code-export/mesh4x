package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.mesh4j.sync.message.IDataSet;
import com.mesh4j.sync.message.IDataSetManager;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.Message;
import com.mesh4j.sync.model.Item;

public class LastVersionStatusMessageProcessor implements IMessageProcessor {

	private static final String ITEM_SEPARATOR = "|";
	private static final String FIELD_SEPARATOR = ":";
	
	// MODEL VARIABLES
	private IDataSetManager dataSetManager;
	private GetForUpdateMessageProcessor getForUpdateMessage;
	private UpdateMessageProcessor updateMessage;
	
	// METHODS
	public LastVersionStatusMessageProcessor(IDataSetManager dataSetManager, GetForUpdateMessageProcessor getForUpdateMessage, UpdateMessageProcessor updateMessage) {
		super();
		this.dataSetManager = dataSetManager;
		this.getForUpdateMessage = getForUpdateMessage;
		this.updateMessage = updateMessage;
	}
	
	@Override
	public String getMessageType() {
		return "3";
	}

	public IMessage createMessage(String dataSetId, List<Item> items) {
		String data = this.encode(items);
		return new Message(
				MessageSyncProtocol.PREFIX,
				getMessageType(),
				dataSetId,
				data);
	}
	
	@Override
	public List<IMessage> process(IMessage message) {
		List<IMessage> response = new ArrayList<IMessage>();
		
		if(this.getMessageType().equals(message.getMessageType())){
			String dataSetId = message.getDataSetId();
			IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);
			List<Item> items = dataSet.getAll(); 
			
			StringTokenizer st = new StringTokenizer(message.getData(), ITEM_SEPARATOR);
			while(st.hasMoreTokens()){
				StringTokenizer stItem = new StringTokenizer(st.nextToken(), FIELD_SEPARATOR);
				String syncID = stItem.nextToken();
				String itemHascode = stItem.nextToken();
										
				Item item = getItem(syncID, items);
				if(item == null){
					response.add(getForUpdateMessage.createMessage(dataSetId, syncID));
				} else {
					items.remove(item);
					String localHasCode = this.calculateHasCode(item);
					if(!localHasCode.equals(itemHascode)){
						response.add(getForUpdateMessage.createMessage(dataSetId, syncID));
					}
				}				
			}
			
			for (Item item : items) {
				response.add(updateMessage.createMessage(dataSetId, item));
			}
		}
		return response;
	}
	
	private Item getItem(String syncID, List<Item> items) {
		for (Item item : items) {
			if (item.getSyncId().equals(syncID)){
				return item;
			}
		}
		return null;
	}

	private String encode(List<Item> items) {
		StringBuffer sb = new StringBuffer();
		for (Item item : items) {
			sb.append(item.getSyncId());
			sb.append(FIELD_SEPARATOR);
			sb.append(this.calculateHasCode(item));
			sb.append(ITEM_SEPARATOR);
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


}
