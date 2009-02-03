package org.mesh4j.sync.ui;

import org.mesh4j.sync.mappings.EndpointMapping;

public interface ISyncSessionViewOwner {

	void notifyEndSync(boolean error);

	void notifyEndCancelSync();

	void notifyNewSync(boolean isSyncSessioninView);

	void notifyBeginSync();

	void notifyNewEndpointMapping(EndpointMapping endpointMapping);

	void notifyNotAvailableDataSource(String dataSourceAlias, String dataSourceDescription, String endpointId);

	boolean isWorking();
}
