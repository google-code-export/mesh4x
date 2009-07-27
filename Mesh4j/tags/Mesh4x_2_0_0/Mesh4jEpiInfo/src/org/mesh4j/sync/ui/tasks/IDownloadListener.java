package org.mesh4j.sync.ui.tasks;

public interface IDownloadListener {

	void setInProcess(String message);

	void setError(String error);

	void setOk(String message);

}
