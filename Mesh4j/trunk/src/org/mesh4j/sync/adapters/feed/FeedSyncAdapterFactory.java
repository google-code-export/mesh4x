package org.mesh4j.sync.adapters.feed;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class FeedSyncAdapterFactory implements ISyncAdapterFactory {

	// MODEL VARIABLES
	private String baseDirectory;
	
	// BUSINESS METHODS
	public FeedSyncAdapterFactory(String baseDirectory){
		Guard.argumentNotNull(baseDirectory, "baseDirectory");
		
		this.baseDirectory = baseDirectory;
	}
	
	public static String createSourceId(String feedFileName, String mdbTableName){
		File file = new File(feedFileName);
		String sourceID = file.getName();
		return sourceID;
	}
	
	@Override
	public boolean acceptsSourceId(String sourceId) {
		return sourceId.toUpperCase().endsWith(".XML");
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception {
		String feedFileName = this.baseDirectory+"/" + sourceId.trim();
		File file = new File(feedFileName);
		if(file.exists()){
			return new FeedAdapter(feedFileName, identityProvider, IdGenerator.INSTANCE);
		} else {
			Feed feed = new Feed(sourceId, sourceId, "", RssSyndicationFormat.INSTANCE);
			return new FeedAdapter(feedFileName, identityProvider, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
		}
	}

}
