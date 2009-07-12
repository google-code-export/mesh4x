package org.mesh4j.sync.adapters.multimode;

public enum SyncTaskStatus {
	ReadyToSync, Synchronizing, Successfully, Fail, Error;

	public boolean isFailed() {
		return Fail.equals(this);
	}

	public boolean isError() {
		return Error.equals(this);
	}

	public boolean isSuccessfully() {
		return Successfully.equals(this);
	}

	public boolean isReadyToSync() {
		return ReadyToSync.equals(this);
	}

}
