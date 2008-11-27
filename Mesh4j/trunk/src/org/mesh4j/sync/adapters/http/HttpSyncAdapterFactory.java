package org.mesh4j.sync.adapters.http;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.security.IIdentityProvider;

public class HttpSyncAdapterFactory implements ISyncAdapterFactory {

	public static final HttpSyncAdapterFactory INSTANCE = new HttpSyncAdapterFactory();
	
	@Override
	public boolean acceptsSourceId(String sourceId) {
		return sourceId.toUpperCase().startsWith("HTTP://");
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception {
		return new HttpSyncAdapter(sourceId, RssSyndicationFormat.INSTANCE, identityProvider);
	}

}
