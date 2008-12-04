package org.mesh4j.sync.adapters;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.security.IIdentityProvider;

public interface ISyncAdapterFactory {

	public boolean acceptsSourceId(String sourceId);
	
	public ISyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception;

	public String getSourceName(String sourceId);
	
	public String getSourceType(String sourceId);
}
 