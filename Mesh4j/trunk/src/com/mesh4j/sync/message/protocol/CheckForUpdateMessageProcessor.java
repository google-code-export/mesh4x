package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.IDataSet;
import com.mesh4j.sync.message.IDataSetManager;
import com.mesh4j.sync.message.IMessageSyncProtocol;
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
	
	public String createMessage(String dataSetId, List<Item> items) {
		String data = this.calculateHasCode(items);
		String msg = MessageFormatter.createMessage(dataSetId, this.getMessageType(), data);
		return msg;
	}

	@Override
	public List<String> process(String message) {
		if(canProcess(message)){
			String data = MessageFormatter.getData(message);
			String dataSetId = MessageFormatter.getDataSetId(message);
			
			IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);		
			List<Item> items = dataSet.getItems();
			
			ArrayList<String> response = new ArrayList<String>();
			
			String itemsHasCode = this.calculateHasCode(items);
			if(itemsHasCode.equals(data)){
				String msgResponse = this.okLastVersion.createMessage(dataSetId);
				response.add(msgResponse);
			} else {
				String msgResponse = this.lastVersionStatus.createMessage(dataSetId, items);
				response.add(msgResponse);
			}
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}

	private boolean canProcess(String message) {
		String messageType = MessageFormatter.getMessageType(message);
		return this.getMessageType().equals(messageType);
	}


	private String calculateHasCode(List<Item> items) {
		StringBuffer sb = new StringBuffer();
		for (Item item : items) {                          // TODO (JMT) MeshSMS: items order by syncID?
			sb.append(item.getSyncId());
			sb.append(item.getLastUpdate().getSequence());
			sb.append(item.getLastUpdate().getBy());
			sb.append(item.getLastUpdate().getWhen());
			sb.append(item.isDeleted());
		}
		return String.valueOf(sb.toString().hashCode());
	}
	
}
