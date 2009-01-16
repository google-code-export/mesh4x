package org.mesh4j.sync.adapters;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.security.IIdentityProvider;

// examples:
// 	source Id 	= rss20:myfile.xml
// 	source type	= rss20
// 	source name  ~= myfile

public interface ISyncAdapterFactory {

	public boolean acceptsSourceId(String sourceId);
	
	public ISyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception;

	public String getSourceName(String sourceId);
	
	public String getSourceType();

}
 