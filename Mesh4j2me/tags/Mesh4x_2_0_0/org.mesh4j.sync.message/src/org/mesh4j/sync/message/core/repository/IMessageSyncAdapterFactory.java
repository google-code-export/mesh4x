package org.mesh4j.sync.message.core.repository;

import org.mesh4j.sync.message.IMessageSyncAdapter;

public interface IMessageSyncAdapterFactory {

	IMessageSyncAdapter createSyncAdapter(String sourceId);

}
