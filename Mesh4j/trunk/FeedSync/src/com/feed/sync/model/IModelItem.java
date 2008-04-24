package com.feed.sync.model;

public interface IModelItem extends Cloneable{

	String getId();

	String getTitle();
	String getDescription();
	
	Object getPayload();  // TODO (?): XmlElement
		
	IModelItem clone();
}
