package com.mesh4j.sync.adapters;

import com.mesh4j.sync.model.IContent;

public interface IIdentifiableContent extends IContent{

	String getType();
	String getId();
	int getVersion();

}
