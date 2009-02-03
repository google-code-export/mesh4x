package org.mesh4j.sync.test.utils.concurrent.command;

public abstract class ConcurrentWorkerCommand extends ConcurrentCommand {

	public ConcurrentWorkerCommand(Object[] params, Long delay) {
		super(params, delay);
	}

	public boolean isWorker() {
		return true;
	}
}
