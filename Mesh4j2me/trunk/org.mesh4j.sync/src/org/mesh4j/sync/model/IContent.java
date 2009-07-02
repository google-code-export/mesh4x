package org.mesh4j.sync.model;

import java.io.Writer;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;

public interface IContent{

	String getId();
	
	String getPayload();
	
	IContent clone();

	int getVersion();

	void addToFeedPayload(Writer writer, Item item, ISyndicationFormat syndicationFormat) throws Exception;
}
