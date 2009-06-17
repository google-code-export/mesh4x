package org.mesh4j.sync.adapters;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.security.IIdentityProvider;

// examples:
// 	source definition = rss20:myfile.xml
// 	source type	= rss20

public interface ISyncAdapterFactory {

	public String getSourceType();

	public boolean acceptsSource(String sourceId, String sourceDefinition);
	
	public ISyncAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception;

}
 