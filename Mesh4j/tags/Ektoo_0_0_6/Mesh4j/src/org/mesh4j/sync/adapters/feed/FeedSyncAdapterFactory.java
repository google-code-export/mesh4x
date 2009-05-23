package org.mesh4j.sync.adapters.feed;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;

public class FeedSyncAdapterFactory implements ISyncAdapterFactory {

	public final static String SOURCE_TYPE = RssSyndicationFormat.INSTANCE.getName();
	
	// BUSINESS METHODS
	public FeedSyncAdapterFactory(){
		super();
	}

	public static String createSourceDefinition(String feedFileName){
		StringBuffer sb = new StringBuffer();
		sb.append(feedFileName);
		
		if(!feedFileName.toUpperCase().endsWith(".XML")){
			sb.append(".xml");	
		}
		
		return SOURCE_TYPE + ":" + sb.toString();
	}
	
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return sourceDefinition != null && sourceDefinition.startsWith(SOURCE_TYPE) && sourceDefinition.toUpperCase().endsWith(".XML");
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception {
		String fileName = sourceDefinition.substring(SOURCE_TYPE.length()+1, sourceDefinition.length());
		String feedFileName = fileName;
		File file = new File(feedFileName);
		if(file.exists()){
			return new FeedAdapter(feedFileName, identityProvider, IdGenerator.INSTANCE);
		} else {
			Feed feed = new Feed(fileName, fileName, "");
			return new FeedAdapter(feedFileName, identityProvider, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
		}
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}

}
