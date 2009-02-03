package org.mesh4j.sync.message.protocol;

import org.dom4j.Element;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;

public interface IItemEncoding {
	
	String encode(ISyncSession syncSession, Item item, int[] diffHashCodes);
	
	Item decode(ISyncSession syncSession, String encodingItem);

	int[] calculateDiffBlockHashCodes(String xml);
	
	int[] calculateDiffBlockHashCodes(Element element);

	String getSyncID(String data);
}
