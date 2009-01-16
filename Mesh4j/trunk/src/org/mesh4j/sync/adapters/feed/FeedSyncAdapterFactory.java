package org.mesh4j.sync.adapters.feed;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class FeedSyncAdapterFactory implements ISyncAdapterFactory {

	public final static String SOURCE_TYPE = RssSyndicationFormat.INSTANCE.getName();
	
	// MODEL VARIABLES
	private String baseDirectory;
	
	// BUSINESS METHODS
	public FeedSyncAdapterFactory(String baseDirectory){
		Guard.argumentNotNull(baseDirectory, "baseDirectory");
		
		this.baseDirectory = baseDirectory;
	}

	public static String createSourceIdFromFileName(String feedFileName){
		File file = new File(feedFileName);
		String fileName = file.getName();
		return SOURCE_TYPE + ":" + fileName;
	}
	
	@Override
	public boolean acceptsSourceId(String sourceId) {
		return sourceId.startsWith(SOURCE_TYPE) && sourceId.toUpperCase().endsWith(".XML");
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception {
		String fileName = sourceId.substring(SOURCE_TYPE.length()+1, sourceId.length());
		String feedFileName = this.baseDirectory+"/" + fileName.trim();
		File file = new File(feedFileName);
		if(file.exists()){
			return new FeedAdapter(feedFileName, identityProvider, IdGenerator.INSTANCE);
		} else {
			Feed feed = new Feed(fileName, fileName, "");
			return new FeedAdapter(feedFileName, identityProvider, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
		}
	}

	@Override
	public String getSourceName(String sourceId) {
		return sourceId;
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}

}
