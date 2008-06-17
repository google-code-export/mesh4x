package com.mesh4j.sync.message.protocol;

import com.mesh4j.sync.model.Item;

public interface IItemEncoding {

	String encode(Item item);
	
	Item decode(String encodingItem)
;}
