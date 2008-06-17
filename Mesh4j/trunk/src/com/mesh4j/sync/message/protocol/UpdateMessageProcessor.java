package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.merge.MergeBehavior;
import com.mesh4j.sync.merge.MergeResult;
import com.mesh4j.sync.message.IDataSet;
import com.mesh4j.sync.message.IDataSetManager;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.Message;
import com.mesh4j.sync.model.Item;

public class UpdateMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private MessageSyncProtocol syncProtocol;
	private IDataSetManager dataSetManager;
	private IItemEncoding itemEncoding;
	
	// METHODS	
	
	public UpdateMessageProcessor(MessageSyncProtocol syncProtocol, 
			IDataSetManager dataSetManager, IItemEncoding itemEncoding) {
		super();
		this.dataSetManager = dataSetManager;
		this.syncProtocol = syncProtocol;
		this.itemEncoding = itemEncoding;
	}

	@Override
	public String getMessageType() {
		return "5";
	}
	
	public IMessage createMessage(String dataSetId, String syncId) {
		IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);
		Item item = dataSet.get(syncId);
		return createMessage(dataSetId, item);
	}
	
	public IMessage createMessage(String dataSetId, Item item) {
		String data = this.itemEncoding.encode(item);
		return new Message(
				MessageSyncProtocol.PREFIX,
				getMessageType(),
				dataSetId,
				data);
	}
	
	@Override
	public List<IMessage> process(IMessage message) {
		if(this.getMessageType().equals(message.getMessageType())){
			String dataSetId = message.getDataSetId();
			String data = message.getData();
			
			Item incomingItem = this.itemEncoding.decode(data);
			return this.merge(dataSetId, incomingItem);
		}else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}
	
	private List<IMessage> merge(String dataSetId, Item incomingItem) {
		IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);
		Item originalItem = dataSet.get(incomingItem.getSyncId());
		
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);
		if (!result.isMergeNone()) {
			Item conflicItem = this.importItem(result, dataSet);
			if(conflicItem != null){
				this.syncProtocol.notifyConflict(dataSetId, conflicItem);
			}
			return IMessageSyncProtocol.NO_RESPONSE;
		} else {
			List<IMessage> response = new ArrayList<IMessage>();
			response.add(createMessage(dataSetId, result.getOriginal()));
			return response;
		}
	}
	
	private Item importItem(MergeResult result, IDataSet dataSet) {
		if (result.getOperation() == null
				|| result.getOperation().isRemoved()) {
			throw new UnsupportedOperationException();
		} else if (result.getOperation().isAdded()) {
			dataSet.add(result.getProposed());
		} else if (result.getOperation().isUpdated()
				|| result.getOperation().isConflict()) {
			dataSet.update(result.getProposed());
		}
		if (!result.isMergeNone() && result.getProposed() != null
				&& result.getProposed().hasSyncConflicts()) {
			return result.getProposed();
		}
		return null;
	}
}
