package org.mesh4j.sync.epiinfo.ui;

public interface ISyncSessionViewOwner {

	void notifyEndSync(boolean error);

	void notifyEndCancelSync();

	void notifyNewSync();

	void notifyBeginSync();

}
