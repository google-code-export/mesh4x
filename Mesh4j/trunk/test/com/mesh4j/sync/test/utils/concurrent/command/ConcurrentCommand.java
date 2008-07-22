package com.mesh4j.sync.test.utils.concurrent.command;

import java.sql.Timestamp;

public abstract class ConcurrentCommand implements IConcurrentCommand {

	// CONSTANTS
	public static final String COMMAND_NAME_KEY = "COMMAND_NAME_KEY"; 

	// MODEL VARIABLES
	private Object[] parameters;
	private Object result;
	private Exception exception;
	private Long delay;
	private ConcurrentCommandExecutor executor;

	// BUSINESS METHODS
	public ConcurrentCommand(Object[] params, Long delay) {
		super();
		setParameters(params);
		setResult(null);
		setException(null);
		setDelay(delay);
	}

	public abstract Object execute() throws Exception;

	public void run() {
		try {
			// wait 'delay' milliseconds
			Thread.sleep(this.getDelay().longValue());
			System.out.println("Concurrent test " + toString() + " begins at " + (new Timestamp(System.currentTimeMillis())).toString());
			setResult(execute());
			System.out.println("Concurrent test " + toString() + " ends at " + (new Timestamp(System.currentTimeMillis())).toString());
		} catch (Exception e) {
			setException(e);
		}
	}
    
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] objects) {
		parameters = objects;
	}

	public Object getResult() {
		return result;
	}
	public void setResult(Object object) {
		result = object;
	}

	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Long getDelay() {
		return delay;
	}
	public void setDelay(Long delay) {
		this.delay = delay;
	}
	
	public ConcurrentCommandExecutor getExecutor() {
		return executor;
	}
	public void setExecutor(ConcurrentCommandExecutor executor) {
		this.executor = executor;
	}

	public boolean isWorker(){
		return false;
	}
	
	public boolean isWeak(){
		return false;
	}
}
