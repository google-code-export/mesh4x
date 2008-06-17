package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.mesh4j.sync.message.IDataSet;
import com.mesh4j.sync.message.IDataSetManager;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.Message;
import com.mesh4j.sync.model.Item;

public class CheckForUpdateMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private IDataSetManager dataSetManager;
	private OkLastVersionMessageProcessor okLastVersion;
	private LastVersionStatusMessageProcessor lastVersionStatus;
	
	// METHODS
	public CheckForUpdateMessageProcessor(
			IDataSetManager dataSetManager,
			OkLastVersionMessageProcessor okLastVersion,
			LastVersionStatusMessageProcessor lastVersionStatus) {
		super();
		this.dataSetManager = dataSetManager;
		this.okLastVersion = okLastVersion;
		this.lastVersionStatus = lastVersionStatus;
	}
	
	@Override
	public String getMessageType() {
		return "1";
	}
	
	public IMessage createMessage(String dataSetId, List<Item> items) {
		String data = this.calculateHasCode(items);
		return new Message(
				MessageSyncProtocol.PREFIX,
				MessageSyncProtocol.VERSION,
				getMessageType(),
				dataSetId,
				data);
	}

	@Override
	public List<IMessage> process(IMessage message) {
		if(this.getMessageType().equals(message.getMessageType())){
			String data = message.getData();
			String dataSetId = message.getDataSetId();
			
			IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);		
			List<Item> items = dataSet.getAll();
			
			List<IMessage> response = new ArrayList<IMessage>();
			
			String itemsHasCode = this.calculateHasCode(items);
			if(itemsHasCode.equals(data)){
				response.add(this.okLastVersion.createMessage(dataSetId));
				return response;
			} else {
				response.add(this.lastVersionStatus.createMessage(dataSetId, items));
				return response;
			}
			
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}

	private String calculateHasCode(List<Item> items) {
		TreeMap<String, Item> sortedItems = new TreeMap<String, Item>();
		for (Item item : items) {
			sortedItems.put(item.getSyncId(), item);
		}
		
		StringBuffer sb = new StringBuffer();
		for (Item item : sortedItems.values()) {
			sb.append(item.getSyncId());
			sb.append(item.getLastUpdate().getSequence());
			sb.append(item.getLastUpdate().getBy());
			sb.append(item.getLastUpdate().getWhen());
			sb.append(item.isDeleted());
		}
		return String.valueOf(sb.toString().hashCode());
	}
	
}
