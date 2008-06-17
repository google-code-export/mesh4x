package com.mesh4j.sync.message;

import java.util.List;

import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.validations.Guard;

public class MessageSyncEngine implements IMessageReceiver {

	// MODEL VARIABLES
	IDataSetManager dataSetManager;
	IMessageSyncProtocol syncProtocol;
	IChannel channel;
	
	// METHODS
	public MessageSyncEngine(IDataSetManager dataSetManager, IMessageSyncProtocol syncProtocol, IChannel channel){
		Guard.argumentNotNull(dataSetManager, "dataSetManager");
		Guard.argumentNotNull(syncProtocol, "syncProtocol");
		Guard.argumentNotNull(channel, "channel");
		
		this.dataSetManager = dataSetManager;
		this.syncProtocol = syncProtocol;
		this.channel = channel;
		
		this.channel.registerMessageReceiver(this);
	}
	
	public void synchronize(String dataSetId) {
		IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);
		
		List<Item> items = dataSet.getAll();
		IMessage message = this.syncProtocol.createBeginSyncMessage(dataSetId, items);
		this.channel.send(message);
	}
	
	@Override
	public void receiveMessage(IMessage message){
		List<IMessage> response = this.syncProtocol.processMessage(message);
		if(response != IMessageSyncProtocol.NO_RESPONSE){
			for (IMessage msg : response) {
				this.channel.send(msg);	
			}			
		}
	}
}
