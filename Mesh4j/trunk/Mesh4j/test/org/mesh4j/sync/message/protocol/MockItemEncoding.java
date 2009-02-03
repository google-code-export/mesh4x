package org.mesh4j.sync.message.protocol;

import org.dom4j.Element;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;

public class MockItemEncoding implements IItemEncoding {

	private Item item;
	
	public MockItemEncoding(Item item){
		super();
		this.item = item;
	}
	
	@Override
	public int[] calculateDiffBlockHashCodes(String xml) {
		return new int[]{1,2};
	}
	
	@Override
	public int[] calculateDiffBlockHashCodes(Element element) {
		return new int[]{1,2};
	}

	@Override
	public Item decode(ISyncSession syncSession, String encodingItem) {
		return item;
	}

	@Override
	public String encode(ISyncSession syncSession, Item item,
			int[] diffHashCodes) {
		return diffHashCodes.length == 0 ? "1" : "2";
	}

	@Override
	public String getSyncID(String data) {
		return item.getSyncId();
	}

}
