package com.mesh4j.sync.message.protocol;

import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.model.Item;

public interface IItemEncoding {
	
	String encode(ISyncSession syncSession, Item item, int[] diffHashCodes);
	
	Item decode(ISyncSession syncSession, String encodingItem);

	int[] calculateDiffBlockHashCodes(String xml);
}
