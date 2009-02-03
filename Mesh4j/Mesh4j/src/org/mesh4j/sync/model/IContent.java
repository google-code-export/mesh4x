package org.mesh4j.sync.model;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;

public interface IContent extends Cloneable{

	String getId();
	
	Element getPayload();
	
	IContent clone();

	void addToFeedPayload(Sync sync, Element itemElement, ISyndicationFormat format);

	int getVersion();

}
