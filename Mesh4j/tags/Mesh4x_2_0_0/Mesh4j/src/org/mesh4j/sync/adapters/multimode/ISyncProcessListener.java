package org.mesh4j.sync.adapters.multimode;

import java.util.Set;

public interface ISyncProcessListener {

	void syncProcessChangeStatusNotification(SyncTask syncTask);

	void syncProcessDataSourcesNotification(Set<String> dataSources);

	void notifyCreatingMsAccessSyncAdapter(String dataSource);

	void notifyCreatingCloudSyncAdapter(String dataSource, String url);

	void notifyErrorReadingMsAccessTables();

	void notifyErrorCreatingHttpAdapter(String dataSource, String url);

	void notifyErrorCreatingMsAccessAdapter(String dataSource);

	void notifyErrorCreatingMySqlAdapter(String tableName);

	void notifyCreatingMySqlSyncAdapter(String tableName);

}
