package org.mesh4j.sync.message.core.repository;

import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.validations.Guard;

public class OpaqueFeedSyncAdapterFactory extends FeedSyncAdapterFactory implements IOpaqueSyncAdapterFactory{

	// MODEL VARIABLES
	private String baseDirectory;
	
	// BUSINESS METHODS
	public OpaqueFeedSyncAdapterFactory(String baseDirectory) {
		super();
		
		Guard.argumentNotNull(baseDirectory, "baseDirectory");
		this.baseDirectory = baseDirectory;		
	}
	
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return true;
	}
	
	@Override
	public String createSourceDefinition(String sourceId, String sourceDefinition){
		StringBuffer sb = new StringBuffer();
		sb.append(this.baseDirectory);
		sb.append("/");
		sb.append(sourceId.replace(":", "_"));
				
		return createSourceDefinition(sb.toString());
	}
	
}
