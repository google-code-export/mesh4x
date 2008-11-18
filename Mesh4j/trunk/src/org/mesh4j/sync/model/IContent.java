package org.mesh4j.sync.model;

import org.dom4j.Element;

public interface IContent extends Cloneable{

	String getId();
	
	Element getPayload();
	
	IContent clone();

	void addToFeedPayload(Sync sync, Element rootPayload);

	int getVersion();

}
