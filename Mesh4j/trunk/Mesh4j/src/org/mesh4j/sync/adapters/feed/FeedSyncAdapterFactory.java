package org.mesh4j.sync.adapters.feed;

import java.io.File;
import java.util.Map;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IIdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
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
		return createSyncAdapter(fileName, identityProvider);
	}

	public static ISyncAdapter createSyncAdapter(String fileName, IIdentityProvider identityProvider) {
		File file = new File(fileName);
		if(file.exists()){
			return new FeedAdapter(fileName, identityProvider, IdGenerator.INSTANCE);
		} else {
			Feed feed = new Feed(fileName, fileName, "");
			return new FeedAdapter(fileName, identityProvider, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
		}
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}

	public static CompositeSyncAdapter createSyncAdapterForMultiFiles(Map<String, String> feedFileNames, IIdentityProvider identityProvider, ISyncAdapter opaqueAdapter) {
		IIdentifiableSyncAdapter[] adapters = new IIdentifiableSyncAdapter[feedFileNames.size()];
		
		int i = 0;
		for (String type : feedFileNames.keySet()) {
			String fileName = feedFileNames.get(type);
			IdentifiableSyncAdapter adapter = new IdentifiableSyncAdapter(type, createSyncAdapter(fileName, identityProvider));
			adapters[i] = adapter;
			i = i +1;
		}
		
		return new CompositeSyncAdapter("Feed file composite", opaqueAdapter, identityProvider, adapters);
	}

}
