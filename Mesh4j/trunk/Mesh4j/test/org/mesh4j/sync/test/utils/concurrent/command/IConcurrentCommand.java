package org.mesh4j.sync.test.utils.concurrent.command;

public interface IConcurrentCommand extends Runnable{

	void setExecutor(ConcurrentCommandExecutor concurrentCommandExecutor);

	boolean isWorker();

	Object getResult();

	Exception getException();

}
