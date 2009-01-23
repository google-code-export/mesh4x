package org.mesh4j.sync.ui;

public interface ISyncSessionViewOwner {

	void notifyEndSync(boolean error);

	void notifyEndCancelSync();

	void notifyNewSync(boolean isSyncSessioninView);

	void notifyBeginSync();

}
