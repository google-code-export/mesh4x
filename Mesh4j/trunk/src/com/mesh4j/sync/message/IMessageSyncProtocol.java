package com.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.model.Item;

public interface IMessageSyncProtocol {

	public static final List<String> NO_RESPONSE = new ArrayList<String>();
	
	String createBeginSyncMessage(String dataSetId, List<Item> items);

	List<String> processMessage(String message);

}
