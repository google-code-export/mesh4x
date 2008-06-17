package com.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.model.Item;

public interface IMessageSyncProtocol {

	public static final List<IMessage> NO_RESPONSE = new ArrayList<IMessage>();
	
	IMessage createBeginSyncMessage(String dataSetId, List<Item> items);

	List<IMessage> processMessage(IMessage message);

}
