package org.mesh4j.sync.merge;

public interface IConflictResolutionListener {

	void notifyConflictResolutionError(String syncId, Throwable e);

	void notifyStartConflictResolution();

	void notifyResolvingConflict(String syncId, int i, int size);

	void notifyUpdatingConflicts(int size);

	void notifyUpdatingConflict(String syncId, int i, int size);

	void notifyEndConflictResolution();

	void notifyConflictResolutionDone();

}
