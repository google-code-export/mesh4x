package org.mesh4j.sync.message.core.repository;

import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;

public class OpaqueFeedSyncAdapterFactory extends FeedSyncAdapterFactory implements IOpaqueSyncAdapterFactory{

	public OpaqueFeedSyncAdapterFactory(String baseDirectory) {
		super(baseDirectory);
	}

	@Override
	public String createSourceId(String source){
		StringBuffer sb = new StringBuffer();
			
		if(!source.toUpperCase().startsWith(SOURCE_TYPE)){
			sb.append(SOURCE_TYPE);
			sb.append(":");
		}
		sb.append(source.replace(":", "_"));
		
		if(!source.toUpperCase().endsWith(".XML")){
			sb.append(".xml");	
		}
		return sb.toString();
	}
	
}
