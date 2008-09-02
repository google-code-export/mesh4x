package org.mesh4j.sync.message.core.repository;

import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.security.IIdentityProvider;

public interface IMessageSyncAdapterFactory {

	IMessageSyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider);

}
