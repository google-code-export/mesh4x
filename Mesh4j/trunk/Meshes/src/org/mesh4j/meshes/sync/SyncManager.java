package org.mesh4j.meshes.sync;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.Feed;
import org.mesh4j.meshes.model.FeedRef;
import org.mesh4j.meshes.model.SyncLog;
import org.mesh4j.meshes.model.SyncMode;
import org.mesh4j.meshes.model.SyncState;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncDirection;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.LoggedInIdentityProvider;

public class SyncManager {
	
	private static SyncManager instance = new SyncManager();
	private Set<String> currentSyncs = new HashSet<String>();
	
	private SyncManager() {
	}
	
	public static SyncManager getInstance() {
		return instance;
	}
	
	public void synchronize(DataSource dataSource) {
		if (isSynchronizing(dataSource))
			return;
		
		currentSyncs.add(getSyncName(dataSource));
		dataSource.setState(SyncState.SYNC);
		try {
			for (FeedRef feedRef : dataSource.getFeeds()) {
				feedRef.setState(SyncState.SYNC);
				try {
					synchronize(feedRef);
					feedRef.addLog(new SyncLog(true, "Synchronization succeeded"));
					feedRef.setState(SyncState.NORMAL);
				} catch (Exception e) {
					feedRef.addLog(new SyncLog(false, e.getMessage()));
					feedRef.setState(SyncState.FAILED);
				}
			}
			dataSource.setState(SyncState.NORMAL);
			
			
			try {
				ConfigurationManager.getInstance().saveMesh(dataSource.getMesh());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} finally {
			currentSyncs.remove(getSyncName(dataSource));
		}
	}
	
	public boolean isSynchronizing(DataSource dataSource) {
		return currentSyncs.contains(getSyncName(dataSource));
	}

	private void synchronize(FeedRef feedRef) {
		HttpSyncAdapter targetAdapter = createTargetAdapter(feedRef.getTargetFeed());
		ISyncAdapter sourceAdapter = feedRef.getDataSource().createSyncAdapter(targetAdapter.getSchema(), feedRef);
		
		Date syncStart = new Date();
		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
		List<Item> conflicts = engine.synchronize(feedRef.getLastSyncDate(), getSyncDirection(feedRef.getDataSource().getSchedule().getSyncMode()));
		if (!conflicts.isEmpty()) {
			feedRef.setHasConflicts(true);
		}

		targetAdapter.setSchema(sourceAdapter.getSchema());
		feedRef.setLastSyncDate(syncStart);
	}
	
	private HttpSyncAdapter createTargetAdapter(Feed feed) {
		return HttpSyncAdapterFactory.createSyncAdapter(feed.getServerFeedUrl(), 
				new LoggedInIdentityProvider(), null, null);
	}
	
	private String getSyncName(DataSource dataSource) {
		return dataSource.getMesh().getName() + "/" + dataSource.getId();
	}

	private SyncDirection getSyncDirection(SyncMode mode) {
		switch (mode) {
		case RECEIVE:
			return SyncDirection.TargetToSource;
		case SEND:
			return SyncDirection.SourceToTarget;
		default:
			return SyncDirection.Both;
		}
	}
}
