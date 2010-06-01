package org.mesh4j.meshes.scheduling;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;


public abstract class AbstractSyncTask extends Task {
	
	public final static String SYNCHRONIZATION_FAILED = "failed";
	public final static String SYNCHRONIZATION_SUCCEED = "succeed";
	public final static String SYNCHRONIZATION_CONFLICTED = "conflicted";
	public final static String SYNCHRONIZATION_ERROR_CREATING_ADAPTER = "error_creating_adapter";
	
	private String status;

	@Override
	public boolean canBePaused() {
		return false;
	}
	
	@Override
	public boolean canBeStopped() {
		return false;
	}
	
	@Override
	public boolean supportsCompletenessTracking() {
		return false;
	}
	
	@Override
	public boolean supportsStatusTracking() {
		return true;
	}
	
	@Override
	public void execute(TaskExecutionContext context) throws RuntimeException {
		System.out.println("Starting sync task...");
		try {
			SyncEngine engine = new SyncEngine(getSourceAdapter(), getTargetAdapter());
			List<Item> items = engine.synchronize(getSince());
			if (items != null && items.size() > 0) {
				status = SYNCHRONIZATION_CONFLICTED;
			} else {
				status = SYNCHRONIZATION_SUCCEED;
			}
		} catch (Exception e) {
			status = SYNCHRONIZATION_FAILED;
		}
		context.setStatusMessage(status);
		System.out.println("Sync task ended");
	}
	
	public abstract ISyncAdapter getSourceAdapter();
	
	public abstract ISyncAdapter getTargetAdapter();
	
	public abstract Date getSince();
	
}
